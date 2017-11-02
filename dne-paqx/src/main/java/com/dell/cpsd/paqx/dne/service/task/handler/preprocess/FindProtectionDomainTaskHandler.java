/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.task.handler.preprocess;

import com.dell.cpsd.paqx.dne.domain.IWorkflowTaskHandler;
import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.domain.node.NodeInventory;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOData;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOProtectionDomain;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOSDS;
import com.dell.cpsd.paqx.dne.domain.vcenter.Host;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.paqx.dne.service.model.FindProtectionDomainTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.NodeExpansionRequest;
import com.dell.cpsd.paqx.dne.service.model.Status;
import com.dell.cpsd.paqx.dne.service.model.TaskResponse;
import com.dell.cpsd.paqx.dne.service.model.ValidateProtectionDomainResponse;
import com.dell.cpsd.paqx.dne.service.task.handler.BaseTaskHandler;
import com.dell.cpsd.service.common.client.exception.ServiceExecutionException;
import com.dell.cpsd.service.common.client.exception.ServiceTimeoutException;
import com.dell.cpsd.service.engineering.standards.EssValidateProtectionDomainsRequestMessage;
import com.dell.cpsd.service.engineering.standards.EssValidateProtectionDomainsResponseMessage;
import com.dell.cpsd.service.engineering.standards.NodeData;
import com.dell.cpsd.service.engineering.standards.ProtectionDomain;
import com.dell.cpsd.service.engineering.standards.ScaleIODataServer;
import com.dell.cpsd.service.engineering.standards.ValidProtectionDomain;
import com.dell.cpsd.service.engineering.standards.Warning;
import com.dell.cpsd.storage.capabilities.api.CreateProtectionDomainRequestMessage;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.naming.InvalidNameException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@Component
public class FindProtectionDomainTaskHandler extends BaseTaskHandler implements IWorkflowTaskHandler
{
    /**
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FindProtectionDomainTaskHandler.class);

    protected static final String R6_PRODUCT_NAME       = "R6";
    protected static final String R7_PRODUCT_NAME       = "R7";
    protected static final String NODE_TYPE_1U1N        = "1U1N";
    protected static final String NODE_TYPE_2U1N        = "2U1N";
    protected static final int    RANDOM_PD_NAME_LENGTH = 10;
    private static final   String COMPONENT_TYPE        = "SCALEIO-CLUSTER";

    /**
     * The <code>NodeService</code> instance
     */
    private NodeService nodeService;

    private final DataServiceRepository repository;

    public FindProtectionDomainTaskHandler(final NodeService nodeService, final DataServiceRepository repository)
    {
        this.nodeService = nodeService;
        this.repository = repository;
    }

    @Override
    public boolean executeTask(Job job)
    {
        LOGGER.info("Execute Find Protection Domain task");

        final FindProtectionDomainTaskResponse response = initializeResponse(job);

        try
        {
            final Validate validate = new Validate(job).invoke();
            final String uuid = validate.getUuid();
            final List<ScaleIOData> scaleIODataList = validate.getScaleIODataList();
            final ScaleIOProtectionDomain scaleIOProtectionDomain = validate.getScaleIOProtectionDomain();
            final String esxiManagementHostname = validate.getEsxiManagementHostname();
            final String esxiManagementIpAddress = validate.getEsxiManagementIpAddress();
            final List<Host> hosts = validate.getHosts();

            final NodeData nodeData = new NodeData();
            nodeData.setSymphonyUuid(uuid);
            nodeData.setType(getNodeType(uuid));

            final ScaleIOData scaleIo = scaleIODataList.get(0);

            final List<ScaleIOProtectionDomain> protectionDomains = scaleIo.getProtectionDomains();
            final List<ProtectionDomain> protectionDomainList = new ArrayList<>();

            if (!CollectionUtils.isEmpty(protectionDomains))
            {
                protectionDomains.stream().filter(Objects::nonNull).forEach(pd -> {
                    final ProtectionDomain protectionDomainRequest = buildProtectionDomainRequestObject(scaleIOProtectionDomain,
                            esxiManagementHostname, esxiManagementIpAddress, hosts, nodeData, pd);

                    protectionDomainList.add(protectionDomainRequest);
                });
            }

            final EssValidateProtectionDomainsRequestMessage requestMessage = new EssValidateProtectionDomainsRequestMessage();

            requestMessage.setNodeData(nodeData);
            requestMessage.setProtectionDomains(protectionDomainList);

            final EssValidateProtectionDomainsResponseMessage protectionDomainResponse = nodeService
                    .validateProtectionDomains(requestMessage);

            if (!CollectionUtils.isEmpty(protectionDomainResponse.getValidProtectionDomains()))
            {
                setResultsInFindProtectionDomainTaskResponse(response, protectionDomainList, protectionDomainResponse);

                addWarningsToFindProtectionDomainResponse(response, protectionDomainResponse);
            }
            else
            {
                //createProtectionDomain(job, response, scaleIo); //Disabled creation of new protection domain ESTS-136904
                LOGGER.error("No protection domain found. Creation of protection domain is not required.");
                response.addError("No valid protection domain found.");
                response.setWorkFlowTaskStatus(Status.FAILED);
                return false;
            }

            response.setWorkFlowTaskStatus(Status.SUCCEEDED);
            return true;

        }
        catch (Exception ex)
        {
            LOGGER.error("Exception occurred", ex);
            response.addError(ex.toString());
        }

        response.setWorkFlowTaskStatus(Status.FAILED);
        return false;
    }

    private void createProtectionDomain(Job job, FindProtectionDomainTaskResponse response, ScaleIOData scaleIo) throws ServiceTimeoutException, ServiceExecutionException {
        //Create a New Protection Domain
        final ComponentEndpointIds componentEndpointIds = repository.getComponentEndpointIds(COMPONENT_TYPE);

        if (componentEndpointIds == null)
        {
            LOGGER.error("No Component Endpoint Ids Found for ScaleIO Cluster");
            throw new IllegalStateException("No Component Endpoint Ids Found for ScaleIO Cluster");
        }

        final CreateProtectionDomainRequestMessage createProtectionDomainRequestMessage = new CreateProtectionDomainRequestMessage();
        final String protectionDomainName = buildCreateProtectionDomainRequestMessage(componentEndpointIds,
                createProtectionDomainRequestMessage);

        final String protectionDomainId = nodeService.createProtectionDomain(createProtectionDomainRequestMessage);

        if (StringUtils.isEmpty(protectionDomainId))
        {
            throw new IllegalStateException("Unable to Create Protection Domain with name: " + protectionDomainName);
        }

        //save the protection domain in H2 database so that it is available in the next task in the workflow
        ScaleIOProtectionDomain newScaleIOProtectionDomain = repository.createProtectionDomain(job.getId().toString(),protectionDomainId, protectionDomainName);
        scaleIo.addProtectionDomain(newScaleIOProtectionDomain);

        response.setResults(buildResponseResult(protectionDomainId,protectionDomainName));
        response.setProtectionDomainName(protectionDomainName);
        response.setProtectionDomainId(protectionDomainId);
    }

    private String buildCreateProtectionDomainRequestMessage(final ComponentEndpointIds componentEndpointIds,
            final CreateProtectionDomainRequestMessage createProtectionDomainRequestMessage)
    {
        final String protectionDomainName = RandomStringUtils.randomAlphanumeric(RANDOM_PD_NAME_LENGTH);
        createProtectionDomainRequestMessage.setComponentEndpointIds(
                new com.dell.cpsd.storage.capabilities.api.ComponentEndpointIds(componentEndpointIds.getComponentUuid(),
                        componentEndpointIds.getEndpointUuid(), componentEndpointIds.getCredentialUuid()));
        createProtectionDomainRequestMessage.setEndpointUrl("https://" + componentEndpointIds.getEndpointUrl());
        createProtectionDomainRequestMessage.setName(protectionDomainName);
        return protectionDomainName;
    }

    private ProtectionDomain buildProtectionDomainRequestObject(final ScaleIOProtectionDomain scaleIOProtectionDomain,
            final String esxiManagementHostname, final String esxiManagementIpAddress, final List<Host> hosts, final NodeData nodeData,
            final ScaleIOProtectionDomain pd)
    {
        final ProtectionDomain protectionDomainRequest = new ProtectionDomain();
        protectionDomainRequest.setId(pd.getId());
        protectionDomainRequest.setName(pd.getName());
        protectionDomainRequest.setState(pd.getProtectionDomainState());

        final List<ScaleIOSDS> sdsList = pd.getSdsList();

        if (!CollectionUtils.isEmpty(sdsList))
        {
            final List<ScaleIODataServer> scaleIODataServers = extractScaleIoDataServersForProtectionDomain(scaleIOProtectionDomain,
                    esxiManagementHostname, esxiManagementIpAddress, hosts, nodeData, sdsList);

            protectionDomainRequest.setScaleIODataServers(scaleIODataServers);
        }

        return protectionDomainRequest;
    }

    private List<ScaleIODataServer> extractScaleIoDataServersForProtectionDomain(final ScaleIOProtectionDomain scaleIOProtectionDomain,
            final String esxiManagementHostname, final String esxiManagementIpAddress, final List<Host> hosts, final NodeData nodeData,
            final List<ScaleIOSDS> sdsList)
    {
        final List<ScaleIODataServer> scaleIODataServers = new ArrayList<>();

        sdsList.stream().filter(Objects::nonNull).forEach(sds -> {
            createSdsInstance(hosts, scaleIODataServers, sds);

            setProtectionDomainIdInNodeData(scaleIOProtectionDomain, esxiManagementHostname, esxiManagementIpAddress, nodeData, sds);
        });

        return scaleIODataServers;
    }

    private void setResultsInFindProtectionDomainTaskResponse(final FindProtectionDomainTaskResponse response,
            final List<ProtectionDomain> protectionDomainList, final EssValidateProtectionDomainsResponseMessage protectionDomainResponse)
    {
        final ValidateProtectionDomainResponse validateProtectionDomainResponse = new ValidateProtectionDomainResponse();
        final String protectionDomainId = protectionDomainResponse.getValidProtectionDomains().get(0).getProtectionDomainID();

        final List<String> protectionDomainNameList = protectionDomainList.stream().filter(Objects::nonNull)
                .filter(p -> p.getId().equalsIgnoreCase(protectionDomainId)).map(ProtectionDomain::getName).collect(Collectors.toList());

        validateProtectionDomainResponse
                .setProtectionDomainName(protectionDomainNameList.size() == 1 ? protectionDomainList.get(0).getName() : protectionDomainId);
        validateProtectionDomainResponse.setProtectionDomainId(protectionDomainId);
        response.setResults(buildResponseResult(validateProtectionDomainResponse.getProtectionDomainId(), validateProtectionDomainResponse.getProtectionDomainName()));
        response.setProtectionDomainId(validateProtectionDomainResponse.getProtectionDomainId());
        response.setProtectionDomainName(validateProtectionDomainResponse.getProtectionDomainName());
    }

    private void addWarningsToFindProtectionDomainResponse(final TaskResponse response,
            final EssValidateProtectionDomainsResponseMessage protectionDomainResponse)
    {
        final List<ValidProtectionDomain> validProtectionDomains = protectionDomainResponse.getValidProtectionDomains();

        if (!CollectionUtils.isEmpty(validProtectionDomains))
        {
            final List<Warning> warningMessages = validProtectionDomains.get(0).getWarningMessages();

            if (!CollectionUtils.isEmpty(warningMessages))
            {
                warningMessages.stream().filter(Objects::nonNull).forEach(warning -> response.addWarning(warning.getMessage()));
            }
        }
    }

    private void setProtectionDomainIdInNodeData(final ScaleIOProtectionDomain scaleIOProtectionDomain, final String esxiManagementHostname,
            final String esxiManagementIpAddress, final NodeData nodeData, final ScaleIOSDS sds)
    {
        if (sds.getName().contains(esxiManagementHostname) || sds.getName().contains(esxiManagementIpAddress))
        {
            nodeData.setProtectionDomainId(scaleIOProtectionDomain.getId());
        }
        else
        {
            nodeData.setProtectionDomainId("");
        }
    }

    private void createSdsInstance(final List<Host> hosts, final List<ScaleIODataServer> scaleIODataServers, final ScaleIOSDS sds)
    {
        final ScaleIODataServer scaleIODataServer = new ScaleIODataServer();
        scaleIODataServer.setType(getHostType(sds, hosts));
        scaleIODataServer.setId(sds.getId());
        scaleIODataServer.setName(sds.getName());
        scaleIODataServers.add(scaleIODataServer);
    }

    private Map<String, String> buildResponseResult(String protectionDomainId, String protectionDomainName)
    {
        final Map<String, String> result = new HashMap<>();
        result.put("protectionDomainName", protectionDomainName);
        result.put("protectionDomainId", protectionDomainId);

        return result;
    }

    private String getHostType(ScaleIOSDS scaleIOSDS, List<Host> hosts)
    {
        String type = null;
        String hostDomain = repository.getDomainName(); //getting the host domain

        String scaleIoName = scaleIOSDS.getName();

        String scaleIONameSuffix="-ESX";
        if (scaleIoName.endsWith(scaleIONameSuffix))
        {
            scaleIoName = scaleIoName.substring(0, scaleIoName.length() - scaleIONameSuffix.length());
        }

        for (Host host : hosts)
        {
            String hostName = host.getName();
            if (scaleIoName.equals(hostName))
            {
                type = host.getType();
                break;
            }
            else
            {
                //Try to find appending the domain
                String scaleIoNameWithDomain = scaleIoName + "." + hostDomain;
                if (scaleIoNameWithDomain.equals(hostName))
                {
                    type = host.getType();
                    break;
                }
                else
                {
                    LOGGER.warn("ScaleIO Sds name does not match with Host name");
                }
            }
        }

        return type;
    }

    private String getNodeType(final String symphonyUuid)
    {
        final NodeInventory nodeInventory = repository.getNodeInventory(symphonyUuid);

        try
        {
            if (nodeInventory != null && nodeInventory.getNodeInventory() != null)
            {
                final String productName = extractProductNameFromProductNamesForNode(symphonyUuid, nodeInventory);

                return matchProductNameAndReturnNodeType(productName);
            }
        }
        catch (Exception e)
        {
            LOGGER.error("Error while getting the node inventory data", e);
        }

        return null;
    }

    private String extractProductNameFromProductNamesForNode(final String symphonyUuid, final NodeInventory nodeInventory)
            throws InvalidNameException
    {
        final JSONArray productNames = JsonPath.read(nodeInventory.getNodeInventory(), "$..data..['System Information']['Product Name']");

        if (CollectionUtils.isEmpty(productNames))
        {
            throw new InvalidNameException("No product names found for node " + symphonyUuid);
        }

        return (String) productNames.get(0);
    }

    private String matchProductNameAndReturnNodeType(final String productName)
    {
        if (productName.contains(R6_PRODUCT_NAME))
        {
            return NODE_TYPE_1U1N;
        }
        else if (productName.contains(R7_PRODUCT_NAME))
        {
            return NODE_TYPE_2U1N;
        }

        return null;
    }

    private class Validate
    {
        private final Job                     job;
        private       ScaleIOProtectionDomain scaleIOProtectionDomain;
        private       String                  uuid;
        private       String                  esxiManagementHostname;
        private       String                  esxiManagementIpAddress;
        private       List<ScaleIOData>       scaleIODataList;
        private       List<Host>              hosts;

        Validate(final Job job)
        {
            this.job = job;
        }

        ScaleIOProtectionDomain getScaleIOProtectionDomain()
        {
            return scaleIOProtectionDomain;
        }

        String getUuid()
        {
            return uuid;
        }

        String getEsxiManagementHostname()
        {
            return esxiManagementHostname;
        }

        String getEsxiManagementIpAddress()
        {
            return esxiManagementIpAddress;
        }

        List<ScaleIOData> getScaleIODataList()
        {
            return scaleIODataList;
        }

        List<Host> getHosts()
        {
            return hosts;
        }

        Validate invoke() throws Exception
        {
            scaleIOProtectionDomain = repository.getScaleIoProtectionDomains().stream().findFirst().orElseGet(null);

            if (scaleIOProtectionDomain == null)
            {
                throw new IllegalStateException("No ScaleIO protection domains found.");
            }

            final NodeExpansionRequest inputParams = job.getInputParams();

            if (inputParams == null)
            {
                throw new Exception("Input Parameters are null");
            }

            uuid = inputParams.getSymphonyUuid();

            LOGGER.info("Symphony UUID is [{}]", uuid);

            esxiManagementHostname = inputParams.getEsxiManagementHostname();

            if (StringUtils.isEmpty(esxiManagementHostname))
            {
                throw new IllegalStateException("ESXi Management Host name is either null or empty");
            }

            esxiManagementIpAddress = inputParams.getEsxiManagementIpAddress();

            if (StringUtils.isEmpty(esxiManagementIpAddress))
            {
                throw new IllegalStateException("ESXi Management IP Address is either null or empty");
            }

            scaleIODataList = nodeService.listScaleIOData();

            if (CollectionUtils.isEmpty(scaleIODataList))
            {
                LOGGER.error("No ScaleIO Data Found");
                throw new IllegalStateException("No ScaleIO Data Found");
            }

            hosts = repository.getVCenterHosts();

            if (CollectionUtils.isEmpty(hosts))
            {
                LOGGER.error("No Hosts found");
                throw new IllegalStateException("No Hosts found");
            }
            return this;
        }
    }

    @Override
    public FindProtectionDomainTaskResponse initializeResponse(final Job job)
    {
        final FindProtectionDomainTaskResponse response = new FindProtectionDomainTaskResponse();
        setupResponse(job, response);
        return response;
    }
}

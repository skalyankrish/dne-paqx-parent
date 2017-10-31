/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.task.handler.addnode;

import com.dell.cpsd.paqx.dne.domain.IWorkflowTaskHandler;
import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.model.AddHostToProtectionDomainResponse;
import com.dell.cpsd.paqx.dne.service.model.NodeExpansionRequest;
import com.dell.cpsd.paqx.dne.service.model.Status;
import com.dell.cpsd.paqx.dne.service.task.handler.BaseTaskHandler;
import com.dell.cpsd.service.engineering.standards.DeviceAssignment;
import com.dell.cpsd.storage.capabilities.api.AddHostToProtectionDomainRequestMessage;
import com.dell.cpsd.storage.capabilities.api.DeviceInfo;
import com.dell.cpsd.storage.capabilities.api.HostToProtectionDomain;
import com.dell.cpsd.storage.capabilities.api.SdsIp;
import com.dell.cpsd.storage.capabilities.api.SdsIpDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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
public class AddHostToProtectionDomainTaskHandler extends BaseTaskHandler implements IWorkflowTaskHandler
{
    /**
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AddHostToProtectionDomainTaskHandler.class);

    /**
     * The <code>NodeService</code> instance
     */
    private final NodeService nodeService;

    private final DataServiceRepository repository;

    public AddHostToProtectionDomainTaskHandler(final NodeService nodeService, final DataServiceRepository repository)
    {
        this.nodeService = nodeService;
        this.repository = repository;
    }

    @Override
    public boolean executeTask(final Job job)
    {
        LOGGER.info("Execute AddHostToProtectionDomain task");
        AddHostToProtectionDomainResponse response = initializeResponse(job);

        try{
            AddHostToProtectionDomainRequestMessage requestMessage = new AddHostToProtectionDomainRequestMessage();

            /**
             * Getting the protection domain id from FindProtectionDomainTaskHandler
             */
            final NodeExpansionRequest inputParams = job.getInputParams();
            final String protectionDomainId = inputParams.getProtectionDomainId();
            final Map<String, DeviceAssignment> deviceToDeviceStoragePoolAssignment = inputParams.getDeviceToDeviceStoragePool();

            if (protectionDomainId == null)
            {
                throw new IllegalStateException("No Protection domain provided");
            }

            /**
             * Getting the SdsName
             */
            String name = getSdsName(job);

            /**
             * Getting the sdsList
             */
            List<SdsIp> sdsIpList = createSdsIps(job);

            List<DeviceInfo> deviceInfoList = createDeviceInfoList(deviceToDeviceStoragePoolAssignment);

            final com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds componentEndpointIds = repository.getComponentEndpointIds("SCALEIO-CLUSTER");

            /**
             * Adding component endpoint ids
             */
            com.dell.cpsd.storage.capabilities.api.ComponentEndpointIds componentEndpoints = new com.dell.cpsd.storage.capabilities.api.ComponentEndpointIds();
            componentEndpoints.setEndpointUuid(componentEndpointIds.getEndpointUuid());
            componentEndpoints.setComponentUuid(componentEndpointIds.getComponentUuid());
            componentEndpoints.setCredentialUuid(componentEndpointIds.getCredentialUuid());

            HostToProtectionDomain hostToProtectionDomain = createHostToProtectionDomain(protectionDomainId, name, sdsIpList, deviceInfoList);

            /**
             * Adding the endpoint url
             */
            String endpointUrl = ("https://" + componentEndpointIds.getEndpointUrl() +":443");
            requestMessage.setHostToProtectionDomain(hostToProtectionDomain);
            requestMessage.setComponentEndpointIds(componentEndpoints);
            requestMessage.setEndpointUrl(endpointUrl);

            /**
             * Creating the response message
             */
            final boolean success = this.nodeService.requestAddHostToProtectionDomain(requestMessage);
            if (!success)
            {
                throw new IllegalStateException("Request add host to protection domain failed");
            }
            response.setWorkFlowTaskStatus(Status.SUCCEEDED);
            response.setProtectionDomainId(protectionDomainId);
            response.setProtectionDomainName(inputParams.getProtectionDomainName());
            response.setStoragePoolDetails(deviceToDeviceStoragePoolAssignment);
            return true;
        }
        catch(Exception e)
        {
            LOGGER.error("Exception occurred", e);
            response.addError(e.toString());
        }
        response.setWorkFlowTaskStatus(Status.FAILED);
        return false;
    }

    /**
     * set Host to protection domain
     *
     * @param protectionDomain
     * @param name
     * @param sdsIpList
     * @param deviceInfoList
     * @return
     */
    private HostToProtectionDomain createHostToProtectionDomain(String protectionDomain, String name, List<SdsIp> sdsIpList, final List<DeviceInfo> deviceInfoList)
    {
        /**
         * Creating the request message
         */
        HostToProtectionDomain hostToProtectionDomain = new HostToProtectionDomain();
        hostToProtectionDomain.setProtectionDomainId(protectionDomain);
        hostToProtectionDomain.setName(name);
        hostToProtectionDomain.setSdsIpList(sdsIpList);
        hostToProtectionDomain.setDeviceInfoList(deviceInfoList);
        return hostToProtectionDomain;
    }

    /**
     * get Sds names
     *
     * @param job
     * @return
     */
    private String getSdsName(Job job) {
        String name = null;
        if (job.getInputParams().getEsxiManagementHostname() != null){
            name = (job.getInputParams().getEsxiManagementHostname() + "-ESX");
        }
        else if (job.getInputParams().getEsxiManagementIpAddress() != null){
            name = (job.getInputParams().getEsxiManagementIpAddress() + "-ESX");
        }
        return name;
    }

    /**
     * Create Sds Ip list from input
     * @param job
     * @return
     */
    private List<SdsIp> createSdsIps(Job job) {
        String ScaleIoData1IP = job.getInputParams().getScaleIoData1SvmIpAddress();
        String ScaleIoData2IP = job.getInputParams().getScaleIoData2SvmIpAddress();

        SdsIp sdsIp1 = new SdsIp();
        SdsIpDetails sdsIpDetails1 = new SdsIpDetails();
        sdsIpDetails1.setIp(ScaleIoData1IP);
        sdsIpDetails1.setRole(SdsIpDetails.Role.ALL);
        sdsIp1.setSdsIpDetails(sdsIpDetails1);

        SdsIp sdsIp2 = new SdsIp();
        SdsIpDetails sdsIpDetails2 = new SdsIpDetails();
        sdsIpDetails2.setIp(ScaleIoData2IP);
        sdsIpDetails2.setRole(SdsIpDetails.Role.ALL);
        sdsIp2.setSdsIpDetails(sdsIpDetails2);

        List<SdsIp> sdsIpList = new ArrayList<>();
        sdsIpList.add(0,sdsIp1);
        sdsIpList.add(1,sdsIp2);
        return sdsIpList;
    }

    private List<DeviceInfo> createDeviceInfoList(Map<String, DeviceAssignment> deviceToDeviceStoragePoolAssignment) {

        List<DeviceInfo> returnVal=null;

        if (deviceToDeviceStoragePoolAssignment != null)
        {
            //spaces are NOT allowed in the deviceName, so we will replace any spaces with '/'
            // '/' is a valid character for deviceName
            //the deviceName tends to look like "/dev/sda scsi" so it will change to "/dev/sda/scsi"
            returnVal = deviceToDeviceStoragePoolAssignment.values().stream().filter(Objects::nonNull)
                    .map(ddspa -> new DeviceInfo(ddspa.getLogicalName(), ddspa.getStoragePoolId(), null)).collect(Collectors.toList());
        }
        return returnVal;
    }

    @Override
    public AddHostToProtectionDomainResponse initializeResponse(Job job)
    {
        final AddHostToProtectionDomainResponse response = new AddHostToProtectionDomainResponse();
        setupResponse(job, response);
        return response;
    }
}

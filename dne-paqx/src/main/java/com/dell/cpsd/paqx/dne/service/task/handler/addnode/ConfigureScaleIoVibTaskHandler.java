/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.task.handler.addnode;

import com.dell.cpsd.paqx.dne.domain.IWorkflowTaskHandler;
import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOData;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOMdmCluster;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.paqx.dne.service.model.ConfigureScaleIoVibTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.InstallEsxiTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.ListESXiCredentialDetailsTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.Status;
import com.dell.cpsd.paqx.dne.service.task.handler.BaseTaskHandler;
import com.dell.cpsd.virtualization.capabilities.api.Credentials;
import com.dell.cpsd.virtualization.capabilities.api.SoftwareVIBConfigureRequest;
import com.dell.cpsd.virtualization.capabilities.api.SoftwareVIBConfigureRequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * TODO: Document Usage
 * <p>
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public class ConfigureScaleIoVibTaskHandler extends BaseTaskHandler implements IWorkflowTaskHandler
{
    /**
     * The logger instance
     */
    private static final Logger LOGGER                = LoggerFactory.getLogger(ConfigureScaleIoVibTaskHandler.class);
    private static final String IOCTL_INI_GUID_STRING = "IoctlIniGuidStr";
    private static final String IOCTL_MDM_IP_STRING   = "IoctlMdmIPStr";
    private static final String EQUALS_STRING         = "=";
    private static final String SPACE_STRING          = " ";
    private static final String COMMA_DELIMITER       = ",";
    private static final String SOFTWARE_VIB_MODULE   = "scini";

    /**
     * The <code>NodeService</code> instance
     */
    private final NodeService           nodeService;
    private final DataServiceRepository repository;

    public ConfigureScaleIoVibTaskHandler(final NodeService nodeService, final DataServiceRepository repository)
    {
        this.nodeService = nodeService;
        this.repository = repository;
    }

    @Override
    public boolean executeTask(final Job job)
    {
        LOGGER.info("Execute Configure ScaleIO VIB task");

        final ConfigureScaleIoVibTaskResponse response = initializeResponse(job);

        try
        {
            final ComponentEndpointIds componentEndpointIds = repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER");

            if (componentEndpointIds == null)
            {
                throw new IllegalStateException("No VCenter components found.");
            }

            final InstallEsxiTaskResponse installEsxiTaskResponse = (InstallEsxiTaskResponse) job.getTaskResponseMap().get("installEsxi");

            if (installEsxiTaskResponse == null)
            {
                throw new IllegalStateException("No Install ESXi task response found");
            }

            final String hostname = installEsxiTaskResponse.getHostname();

            if (hostname == null)
            {
                throw new IllegalStateException("Host name is null");
            }

            final ListESXiCredentialDetailsTaskResponse listEsxiDefaultCredentialDetailsTaskResponse = (ListESXiCredentialDetailsTaskResponse) job
                    .getTaskResponseMap().get("retrieveEsxiDefaultCredentialDetails");

            if (listEsxiDefaultCredentialDetailsTaskResponse == null)
            {
                throw new IllegalStateException("Default ESXi Host Credential Details are null.");
            }

            final String ioctlIniGuidStr = UUID.randomUUID().toString();

            final SoftwareVIBConfigureRequestMessage requestMessage = getSoftwareVIBConfigureRequestMessage(componentEndpointIds, hostname,
                    listEsxiDefaultCredentialDetailsTaskResponse, ioctlIniGuidStr);

            final boolean success = this.nodeService.requestConfigureScaleIoVib(requestMessage);

            if (!success)
            {
                throw new IllegalStateException("Unable to Configure Software VIB");
            }

            response.setWorkFlowTaskStatus(Status.SUCCEEDED);
            response.setIoctlIniGuidStr(ioctlIniGuidStr);

            return true;
        }
        catch (Exception e)
        {
            LOGGER.error("Exception occurred", e);
            response.addError(e.getMessage());
        }

        response.setWorkFlowTaskStatus(Status.FAILED);
        return false;
    }

    private SoftwareVIBConfigureRequestMessage getSoftwareVIBConfigureRequestMessage(final ComponentEndpointIds vCenterComponentEndpointIds,
            final String hostname, final ListESXiCredentialDetailsTaskResponse esxiHostComponentEndpointIds, final String ioctlIniGuidStr)
            throws Exception
    {
        final SoftwareVIBConfigureRequestMessage requestMessage = new SoftwareVIBConfigureRequestMessage();
        requestMessage.setCredentials(new Credentials(vCenterComponentEndpointIds.getEndpointUrl(), null, null));
        requestMessage.setComponentEndpointIds(
                new com.dell.cpsd.virtualization.capabilities.api.ComponentEndpointIds(vCenterComponentEndpointIds.getComponentUuid(),
                        vCenterComponentEndpointIds.getEndpointUuid(), vCenterComponentEndpointIds.getCredentialUuid()));

        final SoftwareVIBConfigureRequest softwareVIBConfigureRequest = new SoftwareVIBConfigureRequest();
        softwareVIBConfigureRequest.setHostName(hostname);
        softwareVIBConfigureRequest.setModuleName(SOFTWARE_VIB_MODULE);
        softwareVIBConfigureRequest.setModuleOptions(buildModuleOptions(ioctlIniGuidStr));
        softwareVIBConfigureRequest.setComponentEndpointIds(
                new com.dell.cpsd.virtualization.capabilities.api.ComponentEndpointIds(esxiHostComponentEndpointIds.getComponentUuid(),
                        esxiHostComponentEndpointIds.getEndpointUuid(), esxiHostComponentEndpointIds.getCredentialUuid()));
        requestMessage.setSoftwareVIBConfigureRequest(softwareVIBConfigureRequest);
        return requestMessage;
    }

    @Override
    public ConfigureScaleIoVibTaskResponse initializeResponse(Job job)
    {
        final ConfigureScaleIoVibTaskResponse response = new ConfigureScaleIoVibTaskResponse();
        setupResponse(job,response);
        return response;
    }

    private String buildModuleOptions(final String ioctlIniGuidStr) throws Exception
    {
        try
        {
            final ScaleIOData scaleIOData = repository.getScaleIoData();

            if (scaleIOData == null)
            {
                throw new Exception("ScaleIO Data is null");
            }

            final Set<String> mdmIpList = new HashSet<>();

            final ScaleIOMdmCluster scaleIOMdmCluster = scaleIOData.getMdmCluster();
            if (scaleIOMdmCluster != null)
            {
                // Scan the master element info the ips
                scaleIOMdmCluster.getMasterElementInfo().stream().filter(Objects::nonNull)
                        .forEach(scaleIOSDSElementInfo -> scaleIOSDSElementInfo.getIps().forEach(scaleIOIP -> {
                            if (!scaleIOIP.getSdsElementInfo().getRole().equalsIgnoreCase("TieBreaker") && "master"
                                    .equalsIgnoreCase(scaleIOIP.getType()))
                            {
                                mdmIpList.add(scaleIOIP.getIp());
                            }
                        }));

                // Scan the slave element info for ips
                scaleIOMdmCluster.getSlaveElementInfo().stream().filter(Objects::nonNull)
                        .forEach(scaleIOSDSElementInfo -> scaleIOSDSElementInfo.getIps().forEach(scaleIOIP -> {
                            if (!scaleIOIP.getSdsElementInfo().getRole().equalsIgnoreCase("TieBreaker") && "slave"
                                    .equalsIgnoreCase(scaleIOIP.getType()))
                            {
                                mdmIpList.add(scaleIOIP.getIp());
                            }
                        }));
            }

            final StringBuilder builder = new StringBuilder();
            builder.append(IOCTL_INI_GUID_STRING);
            builder.append(EQUALS_STRING);

            LOGGER.info("IoctlIniGuidStr is [{}]", ioctlIniGuidStr);

            builder.append(ioctlIniGuidStr);
            builder.append(SPACE_STRING);
            builder.append(IOCTL_MDM_IP_STRING);
            builder.append(EQUALS_STRING);
            builder.append(String.join(COMMA_DELIMITER, mdmIpList));

            return builder.toString();

        }
        catch (Exception exception)
        {
            throw new Exception("Exception occurred", exception);
        }
    }
}

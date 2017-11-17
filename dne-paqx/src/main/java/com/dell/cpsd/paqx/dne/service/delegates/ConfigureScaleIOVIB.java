/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOData;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOMdmCluster;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.delegates.model.ESXiCredentialDetails;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.virtualization.capabilities.api.Credentials;
import com.dell.cpsd.virtualization.capabilities.api.SoftwareVIBConfigureRequest;
import com.dell.cpsd.virtualization.capabilities.api.SoftwareVIBConfigureRequestMessage;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.CONFIGURE_SCALEIO_VIB_FAILED;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.ESXI_CREDENTIAL_DETAILS;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.HOSTNAME;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.IOCTL_INI_GUI_STR;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;

/**
 * Configure ScaleIo Data Client (SDC)
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@Component
@Scope("prototype")
@Qualifier("configureScaleIOVIB")
public class ConfigureScaleIOVIB extends BaseWorkflowDelegate
{
    /**
     * The logger instance
     */
    private static final Logger LOGGER                = LoggerFactory.getLogger(ConfigureScaleIOVIB.class);
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

    @Autowired
    public ConfigureScaleIOVIB(final NodeService nodeService, final DataServiceRepository repository)
    {
        this.nodeService = nodeService;
        this.repository = repository;
    }

    private SoftwareVIBConfigureRequestMessage getSoftwareVIBConfigureRequestMessage(final ComponentEndpointIds vCenterComponentEndpointIds,
            final String hostname, final ESXiCredentialDetails esxiHostComponentEndpointIds, final String ioctlIniGuidStr) throws Exception
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

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Execute Configure ScaleIO VIB task");
        final String taskMessage = "Configure ScaleIO Vib";

        final NodeDetail nodeDetail = (NodeDetail) delegateExecution.getVariable(NODE_DETAIL);
        final String hostname = (String) delegateExecution.getVariable(HOSTNAME);
        final ESXiCredentialDetails esxiCredentialDetails = (ESXiCredentialDetails) delegateExecution.getVariable(ESXI_CREDENTIAL_DETAILS);

        ComponentEndpointIds componentEndpointIds;
        try
        {
            componentEndpointIds = repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER");
        }
        catch (Exception e)
        {
            String errorMessage = "An Unexpected Exception occurred attempting to retrieve VCenter Component Endpoints.  Reason: ";
            LOGGER.error(errorMessage, e);
            updateDelegateStatus(errorMessage + e.getMessage());
            throw new BpmnError(CONFIGURE_SCALEIO_VIB_FAILED, errorMessage + e.getMessage());
        }

        final String ioctlIniGuidStr = UUID.randomUUID().toString();

        boolean success;
        try
        {
            SoftwareVIBConfigureRequestMessage requestMessage = getSoftwareVIBConfigureRequestMessage(componentEndpointIds, hostname,
                    esxiCredentialDetails, ioctlIniGuidStr);

            success = this.nodeService.requestConfigureScaleIoVib(requestMessage);
        }
        catch (Exception e)
        {
            String errorMessage = "An Unexpected Exception occurred attempting to request " + taskMessage + ".  Reason: ";
            LOGGER.error(errorMessage, e);
            updateDelegateStatus(errorMessage + e.getMessage());
            throw new BpmnError(CONFIGURE_SCALEIO_VIB_FAILED, errorMessage + e.getMessage());
        }

        if (!success)
        {
            String errorMessage = taskMessage + " on Node " + nodeDetail.getServiceTag() + " failed!";
            LOGGER.error(errorMessage);
            updateDelegateStatus(errorMessage);
            throw new BpmnError(CONFIGURE_SCALEIO_VIB_FAILED, errorMessage);
        }

        delegateExecution.setVariable(IOCTL_INI_GUI_STR, ioctlIniGuidStr);
        String returnMessage = taskMessage + " on Node " + nodeDetail.getServiceTag() + " was successful.";
        LOGGER.info(returnMessage);
        updateDelegateStatus(returnMessage);

    }
}

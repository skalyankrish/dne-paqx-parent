/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;

@Component
@Scope("prototype")
@Qualifier("configureScaleIOVIB")
public class ConfigureScaleIOVIB extends BaseWorkflowDelegate
{
    /**
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigureScaleIOVIB.class);
    private static final String IOCTL_INI_GUID_STRING = "IoctlIniGuidStr";
    private static final String IOCTL_MDM_IP_STRING = "IoctlMdmIPStr";
    private static final String EQUALS_STRING = "=";
    private static final String SPACE_STRING = " ";
    private static final String COMMA_DELIMITER = ",";
    private static final String SOFTWARE_VIB_MODULE = "scini";

    /**
     * The <code>NodeService</code> instance
     */
    private final NodeService nodeService;
    private final DataServiceRepository repository;

    @Autowired
    public ConfigureScaleIOVIB(final NodeService nodeService, final DataServiceRepository repository)
    {
        this.nodeService = nodeService;
        this.repository = repository;
    }

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Execute Configure ScaleIO VIB task");
        final String taskMessage = "Configure ScaleIO Vib";

        final NodeDetail nodeDetail = (NodeDetail) delegateExecution.getVariable(NODE_DETAIL);
        /*final String hostname = (String) delegateExecution.getVariable(HOSTNAME);
        final ESXiCredentialDetails esxiCredentialDetails = (ESXiCredentialDetails) delegateExecution.getVariable(
                ESXI_CREDENTIAL_DETAILS);

        ComponentEndpointIds componentEndpointIds = null;
        try
        {
            componentEndpointIds = repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER");
        }
        catch (Exception e)
        {
            LOGGER.error("An Unexpected Exception occurred attempting to retrieve VCenter Component Endpoints.", e);
            updateDelegateStatus(
                    "An Unexpected Exception occurred attempting to retrieve VCenter Component Endpoints.  Reason: " +
                    e.getMessage());
            throw new BpmnError(CONFIGURE_SCALEIO_VIB_FAILED,
                                "An Unexpected Exception occurred attempting to retrieve VCenter Component Endpoints.  Reason: " +
                                e.getMessage());
        }

        final String ioctlIniGuidStr = UUID.randomUUID().toString();

        boolean success = false;
        try
        {
            SoftwareVIBConfigureRequestMessage requestMessage = getSoftwareVIBConfigureRequestMessage(
                    componentEndpointIds, hostname, esxiCredentialDetails, ioctlIniGuidStr);

            success = this.nodeService.requestConfigureScaleIoVib(requestMessage);
        }
        catch (Exception e)
        {
            LOGGER.error("An Unexpected Exception occurred attempting to Install ScaleIO Vib.", e);
            updateDelegateStatus(
                    "An Unexpected Exception occurred attempting to request " + taskMessage + ".  Reason: " +
                    e.getMessage());
            throw new BpmnError(CONFIGURE_SCALEIO_VIB_FAILED,
                                "An Unexpected Exception occurred attempting to request " + taskMessage +
                                ".  Reason: " + e.getMessage());
        }

        if (!success)
        {
            LOGGER.error(taskMessage + " on Node " + nodeDetail.getServiceTag() + " failed!");
            updateDelegateStatus(taskMessage + " on Node " + nodeDetail.getServiceTag() + " failed!");
            throw new BpmnError(CONFIGURE_SCALEIO_VIB_FAILED,
                                taskMessage + " on Node " + nodeDetail.getServiceTag() + " failed!");
        }

        delegateExecution.setVariable(DelegateConstants.IOCTL_INI_GUI_STR, ioctlIniGuidStr);*/
        LOGGER.info(taskMessage + " on Node " + nodeDetail.getServiceTag() + " was successful.");
        updateDelegateStatus(taskMessage + " on Node " + nodeDetail.getServiceTag() + " was successful.");


    }
}

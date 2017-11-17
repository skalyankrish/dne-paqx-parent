/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.virtualization.capabilities.api.RemoteCommandExecutionRequestMessage;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.INSTALL_SCALEIO_VM_PACKAGES;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;

/**
 * Install ScaleIo VM packages.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@Component
@Scope("prototype")
@Qualifier("installScaleIOVMPackages")
public class InstallScaleIOVMPackages extends BaseWorkflowDelegate
{
    /*
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(InstallScaleIOVMPackages.class);

    /*
     * The <code>NodeService</code> instance
     */
    private final NodeService nodeService;

    /*
     * The <code>DataServiceRepository</code> instance
     */
    private final DataServiceRepository repository;

    private static final String COMPONENT_TYPE     = "COMMON-SERVER";
    private static final String ENDPOINT_TYPE      = "COMMON-SVM";
    private static final String COMMON_CREDENTIALS = "SVM-COMMON";

    /**
     * InstallSvmPackagesTaskHandler constructor.
     *
     * @param nodeService - The <code>NodeService</code> instance
     * @param repository  - The <code>DataServiceRepository</code> instance
     */
    @Autowired
    public InstallScaleIOVMPackages(final NodeService nodeService, final DataServiceRepository repository)
    {
        this.nodeService = nodeService;
        this.repository = repository;
    }

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Execute InstallSvmPackagesTaskHandler task");
        final String taskMessage = "Install Scale IO VM Packages";
        final NodeDetail nodeDetail = (NodeDetail) delegateExecution.getVariable(NODE_DETAIL);
        final String scaleIoSvmManagementIpAddress = nodeDetail.getScaleIoSvmManagementIpAddress();

        ComponentEndpointIds componentEndpointIds;
        try {
            componentEndpointIds = repository
                    .getComponentEndpointIds(COMPONENT_TYPE, ENDPOINT_TYPE, COMMON_CREDENTIALS);
        }
        catch (Exception e)
        {
            String errorMessage = "An Unexpected Exception occurred attempting to retrieve VCenter Component Endpoints. Reason: ";
            LOGGER.error(errorMessage, e);
            updateDelegateStatus(errorMessage + e.getMessage());
            throw new BpmnError(INSTALL_SCALEIO_VM_PACKAGES, errorMessage + e.getMessage());
        }

        RemoteCommandExecutionRequestMessage requestMessage = new RemoteCommandExecutionRequestMessage();
        requestMessage.setRemoteHost(scaleIoSvmManagementIpAddress);
        requestMessage.setRemoteCommand(RemoteCommandExecutionRequestMessage.RemoteCommand.INSTALL_PACKAGE_SDS_LIA);
        requestMessage.setOsType(RemoteCommandExecutionRequestMessage.OsType.LINUX);
        requestMessage.setComponentEndpointIds(
                new com.dell.cpsd.virtualization.capabilities.api.ComponentEndpointIds(componentEndpointIds.getComponentUuid(),
                        componentEndpointIds.getEndpointUuid(), componentEndpointIds.getCredentialUuid()));

        boolean succeeded;
        try
        {
            succeeded = this.nodeService.requestRemoteCommandExecution(requestMessage);
        }
        catch (Exception ex)
        {
            String errorMessage = "An Unexpected Exception occurred attempting to request " + taskMessage + ".  Reason: ";
            LOGGER.error(errorMessage, ex);
            updateDelegateStatus(errorMessage + ex.getMessage());
            throw new BpmnError(INSTALL_SCALEIO_VM_PACKAGES, errorMessage + ex.getMessage());
        }

        if (!succeeded)
        {
            String errorMessage = taskMessage + ": install ScaleIO packages request failed";
            LOGGER.error(errorMessage);
            updateDelegateStatus(errorMessage);
            throw new BpmnError(INSTALL_SCALEIO_VM_PACKAGES, errorMessage);
        }

        String returnMessage = taskMessage + " on Node " + nodeDetail.getServiceTag() + " was successful.";
        LOGGER.info(returnMessage);
        updateDelegateStatus(returnMessage);
    }
}

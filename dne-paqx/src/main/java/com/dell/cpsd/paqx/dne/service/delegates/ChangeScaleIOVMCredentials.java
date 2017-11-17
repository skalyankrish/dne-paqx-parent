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
import com.dell.cpsd.virtualization.capabilities.api.VmPasswordUpdateRequest;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.CHANGE_SCALEIO_VM_CREDENTIALS;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;

/**
 * Change ScaleIo VM credentials.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@Component
@Scope("prototype")
@Qualifier("changeScaleIOVMCredentials")
public class ChangeScaleIOVMCredentials extends BaseWorkflowDelegate
{
    /*
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeScaleIOVMCredentials.class);

    /*
     * The <code>NodeService</code> instance
     */
    private final NodeService nodeService;

    /*
     * The <code>DataServiceRepository</code> instance
     */
    private final DataServiceRepository repository;

    /**
     * ScaleIO SVM credential components
     */
    private static final String COMPONENT_TYPE      = "COMMON-SERVER";
    private static final String ENDPOINT_TYPE       = "COMMON-SVM";
    private static final String FACTORY_CREDENTIALS = "SVM-FACTORY";
    private static final String COMMON_CREDENTIALS  = "SVM-COMMON";

    /**
     * ChangeSvmCredentialsTaskHandler constructor.
     *
     * @param nodeService - The <code>NodeService</code> instance
     * @param repository  - The <code>DataServiceRepository</code> instance
     */
    @Autowired
    public ChangeScaleIOVMCredentials(final NodeService nodeService, final DataServiceRepository repository)
    {
        this.nodeService = nodeService;
        this.repository = repository;
    }

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Execute ChangeSvmCredentialsTaskHandler task");

        final String taskMessage = "Configure VM Network Settings";
        final NodeDetail nodeDetail = (NodeDetail) delegateExecution.getVariable(NODE_DETAIL);
        final String scaleIoSvmManagementIpAddress = nodeDetail.getScaleIoSvmManagementIpAddress();

        ComponentEndpointIds factoryComponentEndpointIds;
        try
        {
            factoryComponentEndpointIds = repository.getComponentEndpointIds(COMPONENT_TYPE, ENDPOINT_TYPE, FACTORY_CREDENTIALS);
        }
        catch (Exception e)
        {
            String errorMessage = "No factory component ids found. Reason: ";
            LOGGER.error(errorMessage, e);
            updateDelegateStatus(errorMessage + e.getMessage());
            throw new BpmnError(CHANGE_SCALEIO_VM_CREDENTIALS, errorMessage + e.getMessage());
        }

        ComponentEndpointIds commonComponentEndpointIds;
        try
        {
            commonComponentEndpointIds = repository.getComponentEndpointIds(COMPONENT_TYPE, ENDPOINT_TYPE, COMMON_CREDENTIALS);
        }
        catch (Exception e)
        {
            String errorMessage = "No common component ids found. Reason: ";
            LOGGER.error(errorMessage, e);
            updateDelegateStatus(errorMessage + e.getMessage());
            throw new BpmnError(CHANGE_SCALEIO_VM_CREDENTIALS, errorMessage + e.getMessage());
        }

        VmPasswordUpdateRequest vmPasswordUpdateRequest = new VmPasswordUpdateRequest();
        vmPasswordUpdateRequest.setCredentialName(VmPasswordUpdateRequest.CredentialName.SVM_FACTORY);
        vmPasswordUpdateRequest.setComponentEndpointIds(
                new com.dell.cpsd.virtualization.capabilities.api.ComponentEndpointIds(factoryComponentEndpointIds.getComponentUuid(),
                        factoryComponentEndpointIds.getEndpointUuid(), factoryComponentEndpointIds.getCredentialUuid()));

        RemoteCommandExecutionRequestMessage requestMessage = new RemoteCommandExecutionRequestMessage();
        requestMessage.setVmPasswordUpdateRequest(vmPasswordUpdateRequest);
        requestMessage.setRemoteCommand(RemoteCommandExecutionRequestMessage.RemoteCommand.CHANGE_PASSWORD);
        requestMessage.setRemoteHost(scaleIoSvmManagementIpAddress);
        requestMessage.setOsType(RemoteCommandExecutionRequestMessage.OsType.LINUX);
        requestMessage.setComponentEndpointIds(
                new com.dell.cpsd.virtualization.capabilities.api.ComponentEndpointIds(commonComponentEndpointIds.getComponentUuid(),
                        commonComponentEndpointIds.getEndpointUuid(), commonComponentEndpointIds.getCredentialUuid()));

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
            throw new BpmnError(CHANGE_SCALEIO_VM_CREDENTIALS, errorMessage + ex.getMessage());
        }

        if (!succeeded)
        {
            String errorMessage = taskMessage + ": Change ScaleIO vm credentials request failed";
            LOGGER.error(errorMessage);
            updateDelegateStatus(errorMessage);
            throw new BpmnError(CHANGE_SCALEIO_VM_CREDENTIALS, errorMessage);
        }

        String returnMessage = taskMessage + " on Node " + nodeDetail.getServiceTag() + " was successful.";
        LOGGER.info(returnMessage);
        updateDelegateStatus(returnMessage);
    }
}

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

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.PERFORMANCE_TUNE_SCALEIO_VM;

/**
 * Performance tune on ScaleIo VM.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@Component
@Scope("prototype")
@Qualifier("performanceTuneScaleIOVM")
public class PerformanceTuneScaleIOVM extends BaseWorkflowDelegate
{
    /*
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PerformanceTuneScaleIOVM.class);

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
     * PerformanceTuneSvmTaskHandler constructor.
     *
     * @param nodeService - The <code>NodeService</code> instance
     * @param repository  - The <code>DataServiceRepository</code> instance
     */
    @Autowired
    public PerformanceTuneScaleIOVM(final NodeService nodeService, final DataServiceRepository repository)
    {
        this.nodeService = nodeService;
        this.repository = repository;
    }

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Execute PerformanceTuneSvmTaskHandler task");
        final String taskMessage = "Performance Tune Scale IO VM";
        final NodeDetail nodeDetail = (NodeDetail) delegateExecution.getVariable(NODE_DETAIL);

        ComponentEndpointIds componentEndpointIds;
        try
        {
            componentEndpointIds = repository.getComponentEndpointIds(COMPONENT_TYPE, ENDPOINT_TYPE, COMMON_CREDENTIALS);
        }
        catch (Exception e)
        {
            String errorMessage = "An Unexpected Exception occurred attempting to retrieve Common Credentials Component Endpoints. Reason: ";
            LOGGER.error(errorMessage, e);
            updateDelegateStatus(errorMessage + e.getMessage());
            throw new BpmnError(PERFORMANCE_TUNE_SCALEIO_VM, errorMessage + e.getMessage());
        }

        final String scaleIoSvmManagementIpAddress = nodeDetail.getScaleIoSvmManagementIpAddress();

        RemoteCommandExecutionRequestMessage requestMessage = new RemoteCommandExecutionRequestMessage();
        requestMessage.setRemoteCommand(RemoteCommandExecutionRequestMessage.RemoteCommand.PERFORMANCE_TUNING_SVM);
        requestMessage.setRemoteHost(scaleIoSvmManagementIpAddress);
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
            throw new BpmnError(PERFORMANCE_TUNE_SCALEIO_VM, errorMessage + ex.getMessage());
        }

        if (!succeeded)
        {
            String errorMessage = taskMessage + ": performance tune ScaleIO vm request failed";
            LOGGER.error(errorMessage);
            updateDelegateStatus(errorMessage);
            throw new BpmnError(PERFORMANCE_TUNE_SCALEIO_VM, errorMessage);
        }

        String returnMessage = taskMessage + " on Node " + nodeDetail.getServiceTag() + " was successful.";
        LOGGER.info(returnMessage);
        updateDelegateStatus(returnMessage);
    }
}

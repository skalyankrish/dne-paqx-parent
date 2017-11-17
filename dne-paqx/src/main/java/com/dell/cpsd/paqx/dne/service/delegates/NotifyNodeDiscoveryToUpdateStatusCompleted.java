/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import com.dell.cpsd.service.common.client.exception.ServiceExecutionException;
import com.dell.cpsd.service.common.client.exception.ServiceTimeoutException;
import org.apache.commons.lang.StringUtils;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NOTIFY_NODE_STATUS_COMPLETED_FAILED;

@Component
@Scope("prototype")
@Qualifier("notifyNodeDiscoveryToUpdateStatusCompleted")
public class NotifyNodeDiscoveryToUpdateStatusCompleted extends BaseWorkflowDelegate
{
    /*
     * The logger instance
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(NotifyNodeDiscoveryToUpdateStatusCompleted.class);

    /*
     * The <code>NodeService</code> instance
     */
    private final NodeService nodeService;


    /**
     * NotifyNodeDiscoveryToUpdateStatusTaskHandler constructor.
     *
     * @param nodeService
     *            - The <code>NodeService</code> instance.
     *
     * @since 1.0
     */
    @Autowired
    public NotifyNodeDiscoveryToUpdateStatusCompleted(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Execute NotifyNodeDiscoveryToUpdateStatusCompleted task");
        final String taskMessage = "Update Node Status";
        final NodeDetail nd = (NodeDetail) delegateExecution.getVariable(NODE_DETAIL);
        try
        {
            boolean succeeded = this.nodeService.notifyNodeAllocationStatus(nd.getId(), "Completed");
            if(!succeeded)
            {
                LOGGER.error("Node Status was not updated to added.");
                updateDelegateStatus("Node Status was not updated to added.");
                throw new BpmnError(NOTIFY_NODE_STATUS_COMPLETED_FAILED, "Node Status was not updated to added.");
            }
            LOGGER.info(taskMessage + " on Node " + nd.getServiceTag() + " was successful.");
            updateDelegateStatus(taskMessage + " on Node " + nd.getServiceTag() + " was successful.");
        }
        catch (Exception e)
        {
            throw new BpmnError(NOTIFY_NODE_STATUS_COMPLETED_FAILED, "Node Status was not updated to added.");
        }

    }
}

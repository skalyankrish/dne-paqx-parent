/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
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
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NOTIFY_NODE_STATUS_STARTED_FAILED;

@Component
@Scope("prototype")
@Qualifier("notifyNodeDiscoveryToUpdateStatusStarted")
public class NotifyNodeDiscoveryToUpdateStatusStarted extends BaseWorkflowDelegate
{
    /*
     * The logger instance
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(NotifyNodeDiscoveryToUpdateStatusStarted.class);

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
    public NotifyNodeDiscoveryToUpdateStatusStarted(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Execute NotifyNodeDiscoveryToUpdateStatusStarted task");
        final String taskMessage = "Update Node Status";
        final NodeDetail nd = (NodeDetail) delegateExecution.getVariable(NODE_DETAIL);

        try
        {
            boolean succeeded = this.nodeService.notifyNodeAllocationStatus(nd.getId(), "Started");

            if(!succeeded)
            {
                LOGGER.error("Node Status was not updated to provisioning in-progress.");
                updateDelegateStatus("Node Status was not updated to provisioning in-progress.");
                throw new BpmnError(NOTIFY_NODE_STATUS_STARTED_FAILED, "Node Status was not updated to provisioning in-progress.");
            }
            LOGGER.info(taskMessage + " on Node " + nd.getServiceTag() + " was successful.");
            updateDelegateStatus(taskMessage + " on Node " + nd.getServiceTag() + " was successful.");
        } catch (Exception e)
        {
            throw new BpmnError(NOTIFY_NODE_STATUS_STARTED_FAILED, "Node Status was not updated to provisioning inprogress.");
        }
    }
}

/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.task.handler.addnode;


import com.dell.cpsd.paqx.dne.service.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dell.cpsd.paqx.dne.domain.IWorkflowTaskHandler;
import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.task.handler.BaseTaskHandler;
import org.springframework.util.StringUtils;

/**
 * Task responsible for notifying the node discovery service that node
 * allocation has completed and to update its status.
 * 
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 * 
 * @since 1.0
 */
public class NotifyNodeDiscoveryToUpdateStatusTaskHandler extends BaseTaskHandler implements IWorkflowTaskHandler
{
    /*
     * The logger instance
     */
    private static final Logger LOGGER = 
            LoggerFactory.getLogger(NotifyNodeDiscoveryToUpdateStatusTaskHandler.class);

    /*
     * The <code>NodeService</code> instance
     */
    private final NodeService nodeService;

    private final String action;

    /**
     * NotifyNodeDiscoveryToUpdateStatusTaskHandler constructor.
     *
     * @param nodeService
     *            - The <code>NodeService</code> instance.
     * 
     * @since 1.0
     */
    public NotifyNodeDiscoveryToUpdateStatusTaskHandler(NodeService nodeService, String action)
    {
        this.nodeService = nodeService;
        this.action = action;
    }

    /**
     * Perform the task of notifying the node discovery service that node
     * allocation has completed and to update its status.
     * 
     * @param job - The <code>Job</code> this task is part of.
     * 
     * @since   1.0
     */
    @Override
    public boolean executeTask(Job job)
    {
        LOGGER.info("Execute NotifyNodeDiscoveryToUpdateStatusTaskHandler task");

        TaskResponse response = initializeResponse(job);

        try
        {
            final NodeExpansionRequest inputParams = job.getInputParams();

            if (inputParams == null)
            {
                throw new IllegalStateException("Job input parameters are null");
            }

            final String symphonyUuid = inputParams.getSymphonyUuid();

            if (StringUtils.isEmpty(symphonyUuid))
            {
                throw new IllegalStateException("Symphony uuid is null");
            }
            
            boolean succeeded = this.nodeService.notifyNodeAllocationStatus(symphonyUuid, action);
            
            if (!succeeded)
            {
                throw new IllegalStateException("Node allocation status change failed");
            }

            response.setWorkFlowTaskStatus(Status.SUCCEEDED);
            return true;
        }
        catch (Exception e)
        {
            LOGGER.error("Error notifying node discovery service", e);
            response.addError(e.toString());
        }

        response.setWorkFlowTaskStatus(Status.FAILED);
        return false;
    }
}

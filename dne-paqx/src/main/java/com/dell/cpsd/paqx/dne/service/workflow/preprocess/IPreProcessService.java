/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.workflow.preprocess;

import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.service.IBaseService;
import com.dell.cpsd.paqx.dne.service.WorkflowService;
import com.dell.cpsd.paqx.dne.service.model.NodeExpansionResponse;

import java.util.UUID;

/**
 * Preprocess service interface.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public interface IPreProcessService extends IBaseService
{
    /**
     * Create the workflow.
     *
     * @param workflowType - The workflow type
     * @param startingStep - The wirkflow starting step
     * @param currentStatus - The workflow current step
     *
     * @return The created <code>Job</code> instance
     */
    Job createWorkflow(String workflowType, String startingStep, String currentStatus);

    /**
     * Find a job with the given id.
     *
     * @param jobId - The id of the job to find
     *
     * @return The <code>Job</code> instance
     */
    Job findJob(UUID jobId);

    /**
     * Make a <code>NodeExpansionResponse</code> instance.
     *
     * @param job - The job for which to create the <code>NodeExpansionResponse</code> instance
     *
     * @return The <code>NodeExpansionResponse</code> instance
     */
    NodeExpansionResponse makeNodeExpansionResponse(Job job);

    /**
     * Get the workflow service
     *
     * @return The <code>WorkflowService</code> instance
     */
    WorkflowService getWorkflowService();
}

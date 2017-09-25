/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * VCE Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.service;

import com.dell.cpsd.paqx.dne.domain.Job;

import com.dell.cpsd.paqx.dne.domain.WorkflowTask;
import com.dell.cpsd.paqx.dne.service.model.Step;

import java.util.Map;
import java.util.UUID;

/**
 * Workflow service interface.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public interface WorkflowService
{
    /**
     * This creates a job for the specified workflow type.
     * 
     * @param   workflowType    The type of workflow.
     * @param   startingStep    The starting step in the workflow.
     * @param   startingStep    The current step in the workflow.
     * 
     * @return  The job for the specified workflow.
     * 
     * @since   1.0
     */
    Job createWorkflow(String workflowType, String startingStep, String currentStatus, Map<String, WorkflowTask> taskMap);

    /**
     * This returns the next step in the workflow, or null if there is none.
     * 
     * @param   workflowType    The type of workflow.
     * @param   currentStep     The current step in the workflow.
     * 
     * @return  The next step in the workflow, or null.
     * 
     * @since   1.0
     */
    Step findNextStep(String workflowType, String currentStep);
    
    /**
     * This returns the active workflow jobs.
     * 
     * @return  The active workflow jobs.
     * 
     * @since   1.0
     */
    Job[] findActiveJobs();

    /**
     * This returns the job with the specified job identifier.
     * 
     * @param   jobId   The job identifier.
     * 
     * @return  The job with the identifier, or null.
     * 
     * @since   1.0
     */
    Job findJob(UUID jobId);

    /**
     * This saves the given job to the database
     *
     * @param job   The job to save
     *
     * @since   1.0
     */
    void saveJob(Job job);

    /**
     * This advances the job to the next step and assigns the specified status.
     * 
     * @param   job             The job to advance.
     * @param   currentStep     The current step.
     * @param   currentStatus   The status to assign to the job.
     * 
     * @return  The job advanced to the next step.
     * 
     * @since   1.0
     */
    Job advanceToNextStep(Job job, String currentStep, String currentStatus);

    /**
     * This gets the workflow steps
     *
     * @return The map of steps keyed by step name
     *
     * @since   1.0
     */
    Map<String, Step> getWorkflowSteps();
}

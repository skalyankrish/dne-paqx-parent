/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * VCE Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.domain;

import com.dell.cpsd.paqx.dne.service.model.NodeExpansionRequest;
import com.dell.cpsd.paqx.dne.service.model.Status;
import com.dell.cpsd.paqx.dne.service.model.TaskResponse;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.*;

/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */

public class Job
{
    /*
     * The job identifier
     */
    private UUID   id;
    
    /*
     * The name of the workflow the job is associated with.
     */
    private String workflow;
    
    /*
     * The name of the current step.
     */
    private String step;
    
    /*
     * The current statusDetails of the job.
     */
    private String stateDetails;

    private Status status;

    private Map<String, WorkflowTask> taskMap;

    private Map<String, TaskResponse> taskResponseMap;
    private List<TaskResponse> taskResponseList;

    private String initialStep;

    private NodeExpansionRequest inputParams;

    /**
     * Job constructor.
     * 
     * @param   id              The job identifier.
     * @param   workflow         The name of the workflow.
     * @param   currentStep     The current step in the workflow.
     * @param   currentStatus   The current statusDetails of the job
     */
    public Job(final UUID id,
               final String workflow,
               final String currentStep,
               final String currentStatus,
               Map<String, WorkflowTask> taskMap)
    {
        if (id == null)
        {
            throw new IllegalArgumentException("The job identifier is null.");
        }
        
        if (workflow == null)
        {
            throw new IllegalArgumentException("The workflow name is null.");
        }
        
        this.id = id;
        this.workflow = workflow;
        this.step = currentStep;
        this.initialStep = currentStep;

        // NOTE : The statusDetails is expected to change to a Status object.
        this.stateDetails = currentStatus;

        this.status =Status.SUBMITTED;

        this.taskMap = taskMap;
        taskResponseMap = new HashMap<>();
        taskResponseList = new ArrayList<>();
    }

    public String getInitialStep() {
        return initialStep;
    }
    

    /**
     * This returns the job identifier.
     * 
     * @return  The job identifier.
     * 
     * @since   1.0
     */
    public UUID getId()
    {
        return id;
    }

    
    /**
     * This returns the name of the current step.
     * 
     * @return  The name of the current step.
     * 
     * @since   1.0
     */
    public String getStep()
    {
        return step;
    }

    
    /**
     * This returns the name of the workflow.
     * 
     * @return  The name of the workflow.
     * 
     * @since   1.0
     */
    public String getWorkflow()
    {
        return workflow;
    }

    
    /**
     * This moves the job forward to the next step.
     * 
     * @param   nextStep    The name of the next step.
     * 
     * @since   1.0
     */
    public void changeToNextStep(final String nextStep)
    {
        step = nextStep;
    }
    
    
    /**
     * This returns the current statusDetails of the job.
     * 
     * @return  The current statusDetails of the job.
     * 
     * @since   1.0
     */
    public String getStateDetails()
    {
        // NOTE : This is expected to change to a Status object.
        return this.stateDetails;
    }
    
    
    /**
     * This sets the current statusDetails of the job.
     * 
     * @param   currentStatus  The current statusDetails of the job.
     * 
     * @since   1.0
     */
    public void setStateDetails(final String currentStatus)
    {
        // NOTE : This is expected to change to a Status object.
        this.stateDetails = currentStatus;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setTaskMap(Map<String, WorkflowTask> taskMap){ this.taskMap = taskMap; }

    public WorkflowTask getCurrentTask() {
        return taskMap.get(step);
    }

    public void addTaskResponse(String stepName, TaskResponse response){
        taskResponseMap.put(stepName, response);
        taskResponseList.add(response);
    }
    public Map<String, TaskResponse> getTaskResponseMap() {
        return taskResponseMap;
    }
    public List<TaskResponse> getTaskResponseList() {
        return taskResponseList;
    }

    public NodeExpansionRequest getInputParams() {
        return inputParams;
    }

    public void setInputParams(NodeExpansionRequest inputParams) {
        this.inputParams = inputParams;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() 
    {
        final StringBuilder builder = new StringBuilder();
        
        builder.append("Job {");
        
        builder.append("id=").append(this.id);
        builder.append(", Step=").append(this.step);
        builder.append(", workflow=").append(this.workflow);
        builder.append(", Status=").append(this.stateDetails);
        builder.append(", State=").append(this.status);
        
        builder.append("}");
        
        return builder.toString();
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() 
    {
        final HashCodeBuilder builder = new HashCodeBuilder();
        
        builder.append(this.id);
        builder.append(this.step);
        builder.append(this.workflow);
        builder.append(this.status);
        
        return builder.toHashCode();
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object other) 
    {
        if (other == this) 
        {
            return true;
        }
        
        if (!(other instanceof Job))
        {
            return false;
        }
        
        final Job rhs = ((Job) other);
        
        final EqualsBuilder builder = new EqualsBuilder();
        
        builder.append(this.id, rhs.id);
        builder.append(this.step, rhs.step);
        builder.append(this.workflow, rhs.workflow);
        builder.append(this.status, rhs.status);
        
        return builder.isEquals();
    }


}

/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.service.model;

import com.dell.cpsd.paqx.dne.domain.Job;

import java.util.*;

public class NodeExpansionResponse {
    private String details;
    
    private UUID correlationId;
    
    private UUID workflowId;
    
    private String workflow;

    private Status status;
    private List<String> errors;
    private List<String> warnings;
    private List<TaskResponse> workflowTasksResponseList;
    private Map<String, TaskResponse> workflowTasksResponseMap;

    private final Set<LinkRepresentation> links = new HashSet<>();


    public NodeExpansionResponse() {
        errors = new ArrayList<>();
        warnings = new ArrayList<>();
    }

    public NodeExpansionResponse(Job job)
    {
        correlationId = job.getId();
        details = job.getStateDetails();
        workflow = job.getWorkflow();
        workflowId = job.getId();
        status = job.getStatus();
        errors = new ArrayList<>();
        warnings = new ArrayList<>();
        workflowTasksResponseMap = job.getTaskResponseMap();
        workflowTasksResponseList = job.getTaskResponseList();
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }


    public UUID getCorrelationId() {
        return correlationId;
    }


    public void setCorrelationId(UUID correlationId) {
        this.correlationId = correlationId;
    }

    
    public UUID getWorkflowId() 
    {
        return this.workflowId;
    }

    public void setWorkflowId(final UUID workflowId) 
    {
        this.workflowId = workflowId;
    }
    
    public String getWorkflow()
    {
        return workflow;
    }
    
    public void setWorkflow(String workflow)
    {
        this.workflow = workflow;
    }

    public Set<LinkRepresentation> getLinks()
    {
        return links;
    }

    public void addLink(LinkRepresentation linkRepresentation)
    {
        links.add(linkRepresentation);
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

//    public Map<String, TaskResponse> getWorkflowTasksMap() {
//        return workflowTasksResponseMap;
//    }

    public List<TaskResponse> getWorkflowTasksResponseList() {
        return workflowTasksResponseList;
    }
    public void addWorkflowTask(String step, TaskResponse taskResponse) { this.workflowTasksResponseMap.put(step, taskResponse); }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}



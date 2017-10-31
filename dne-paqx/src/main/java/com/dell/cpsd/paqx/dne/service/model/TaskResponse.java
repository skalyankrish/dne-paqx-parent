/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */

public class TaskResponse
{

    private       String              workFlowTaskName;
    private       Status              workFlowTaskStatus;
    private final List<String>        errors;
    private final List<String>        warnings;
    private       Map<String, String> results;

    public TaskResponse()
    {
        errors = new ArrayList<>();
        warnings = new ArrayList<>();
    }

    public void addError(String errorMsg)
    {
        errors.add(errorMsg);
    }

    public List<String> getErrors()
    {
        return errors;
    }

    public void addWarning(String warningMsg)
    {
        warnings.add(warningMsg);
    }

    public List<String> getWarnings()
    {
        return warnings;
    }

    public String getWorkFlowTaskName()
    {
        return workFlowTaskName;
    }

    public void setWorkFlowTaskName(String workFlowTaskName)
    {
        this.workFlowTaskName = workFlowTaskName;
    }

    public Status getWorkFlowTaskStatus()
    {
        return workFlowTaskStatus;
    }

    public void setWorkFlowTaskStatus(Status workFlowTaskStatus)
    {
        this.workFlowTaskStatus = workFlowTaskStatus;
    }

    public Map<String, String> getResults()
    {
        return results;
    }

    public void setResults(Map<String, String> results)
    {
        this.results = results;
    }
}

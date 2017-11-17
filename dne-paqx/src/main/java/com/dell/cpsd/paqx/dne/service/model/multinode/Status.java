/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 */
package com.dell.cpsd.paqx.dne.service.model.multinode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Status
{
    public enum StatusState {
        ACTIVE,
        SUSPENDED,
        COMPLETED,
        EXTERNALLY_TERMINATED,
        INTERNALLY_TERMINATED,
        FAILED;

        public static StatusState fromValue(String value) {
            for (StatusState s: StatusState.values()) {
                if (s.name().equals(value)) {
                    return s;
                }
            }
            throw new IllegalArgumentException("Invalid value for STATE enum: " + value);
        }

    };

    private String id;
    private String jobId;
    private StatusState state;
    private List<Activity> currentActivities;
    private List<Activity> completedSteps = new ArrayList<>();
    private List<Error> errors = new ArrayList<>();
    private List<Status> subProcesses = new ArrayList<>();
    private Map<String, Object> additionalProperties;


    public String getId()
    {
        return id;
    }

    public void setId(final String id)
    {
        this.id = id;
    }

    public String getJobId()
    {
        return jobId;
    }

    public void setJobId(final String jobId)
    {
        this.jobId = jobId;
    }

    public StatusState getState()
    {
        return state;
    }

    public void setState(final StatusState state)
    {
        this.state = state;
    }

    public List<Activity> getCurrentActivities()
    {
        return currentActivities;
    }

    public void setCurrentActivities(final List<Activity> currentActivities)
    {
        this.currentActivities = currentActivities;
    }

    public List<Activity> getCompletedSteps()
    {
        return completedSteps;
    }

    public void setCompletedSteps(final List<Activity> completedSteps)
    {
        this.completedSteps = completedSteps;
    }

    public List<Error> getErrors()
    {
        return errors;
    }

    public void setErrors(final List<Error> errors)
    {
        this.errors = errors;
    }

    public Map<String, Object> getAdditionalProperties()
    {
        return additionalProperties;
    }

    public void setAdditionalProperties(final Map<String, Object> additionalProperties)
    {
        this.additionalProperties = additionalProperties;
    }

    public List<Status> getSubProcesses()
    {
        return subProcesses;
    }

    public void setSubProcesses(final List<Status> subProcesses)
    {
        this.subProcesses = subProcesses;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof Status))
        {
            return false;
        }

        final Status status = (Status) o;

        if (getId() != null ? !getId().equals(status.getId()) : status.getId() != null)
        {
            return false;
        }
        return getSubProcesses() != null ?
                getSubProcesses().equals(status.getSubProcesses()) :
                status.getSubProcesses() == null;
    }

    @Override
    public int hashCode()
    {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getSubProcesses() != null ? getSubProcesses().hashCode() : 0);
        return result;
    }
}

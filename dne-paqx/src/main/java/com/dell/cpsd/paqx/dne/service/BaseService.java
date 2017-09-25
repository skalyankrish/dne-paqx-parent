/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service;

import com.dell.cpsd.paqx.dne.domain.IWorkflowTaskHandler;
import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.domain.WorkflowTask;
import com.dell.cpsd.paqx.dne.service.model.*;
import org.springframework.http.HttpMethod;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract base service implementation.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public abstract class BaseService
{
    public NodeExpansionResponse makeNodeExpansionResponse(final Job job, final WorkflowService workflowService)
    {
        final NodeExpansionResponse response = new NodeExpansionResponse(job);

        response.addLink(createSelfLink(job, job.getStep()));

        Map<String, TaskResponse> taskResponseMap = job.getTaskResponseMap();
        Map<String, Step> stepMap = workflowService.getWorkflowSteps();
        String stepName = job.getInitialStep();

        Step step = stepMap.get(stepName);
        while(!step.isFinalStep()) {
            TaskResponse taskResponse = taskResponseMap.get(stepName);
            if (taskResponse != null && taskResponse.getWorkFlowTaskStatus() == Status.SUCCEEDED) {
                final Step nextStep = workflowService.findNextStep(
                        job.getWorkflow(), job.getStep());

                if (nextStep != null) {
                    response.addLink(createNextStepLink(job, workflowService));
                }
            }
            stepName = step.getNextStep();
            step = stepMap.get(stepName);
        }
        return response;
    }

    public WorkflowTask createTask(String taskName, IWorkflowTaskHandler serviceBeanName){
        WorkflowTask task = new WorkflowTask();
        task.setTaskName(taskName);
        task.setTaskHandler(serviceBeanName);
        return task;
    }

    public LinkRepresentation createNextStepLink(final Job job, WorkflowService workflowService)
    {

        final Step nextStep =
                workflowService.findNextStep(job.getWorkflow(), job.getStep());
        final String path = nextStep.getNextStep(); //findPathFromStep(nextStep.getNextStep());
        final String type = findTypeFromStep(nextStep.getNextStep());
        final String method = findMethodFromStep(nextStep.getNextStep());

        final String uriInfo = this.formatUri(job, path);

        return new LinkRepresentation("step-next", uriInfo, type, method);
    }

    //public abstract String findPathFromStep(String nextStep);

    private String findTypeFromStep(final String step)
    {
        final Map<String, String> stepToType = new HashMap<>();

        return stepToType.getOrDefault(step, "application/json");
    }

    private String findMethodFromStep(final String step)
    {
        final Map<String, String> stepToMethod = new HashMap<>();
        stepToMethod.put("completed", "GET");
        return stepToMethod.getOrDefault(step, HttpMethod.POST.toString());
    }

    private String formatUri(final Job job, final String path)
    {
        UriComponents uriComponents = null;

        if ((path == null) || (path.isEmpty()))
        {
            uriComponents =
                    UriComponentsBuilder.fromUriString("/nodes/{jobId}").
                            buildAndExpand(job.getId().toString());
        }
        else
        {
            uriComponents =
                    UriComponentsBuilder.fromUriString("/nodes/{jobId}/{path}").
                            buildAndExpand(job.getId().toString(), path);
        }

        return uriComponents.toUriString();
    }

    private LinkRepresentation createSelfLink(final Job job, final String step)
    {
        final String path = job.getStep();
        final String type = findTypeFromStep(step);
        final String method = findMethodFromStep(step);

        final String uriInfo = this.formatUri(job, path);

        return new LinkRepresentation("self", uriInfo, type, method);
    }
}

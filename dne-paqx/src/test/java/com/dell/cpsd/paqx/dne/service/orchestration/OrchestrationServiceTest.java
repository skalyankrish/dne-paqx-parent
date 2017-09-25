/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.orchestration;

import com.dell.cpsd.paqx.dne.domain.IWorkflowTaskHandler;
import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.domain.WorkflowTask;
import com.dell.cpsd.paqx.dne.service.IBaseService;
import com.dell.cpsd.paqx.dne.service.WorkflowService;
import com.dell.cpsd.paqx.dne.service.model.Status;
import com.dell.cpsd.paqx.dne.service.model.Step;
import com.dell.cpsd.paqx.dne.service.model.TaskResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The tests for OrchestrationService.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class OrchestrationServiceTest
{
    @Mock
    private Job job;

    @Mock
    private IBaseService jobService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private Step step;

    @Mock
    private WorkflowTask workflowTask;

    @Mock
    private IWorkflowTaskHandler workflowTaskHandler;

    @Mock
    private TaskResponse taskResponse;

    private OrchestrationService service;
    private Map<String, TaskResponse> taskResponseMap;
    private String workFlowName = "MyWorkflow";
    private String stepName = "Step1";

    @Before
    public void setUp() throws Exception
    {
        this.service = new OrchestrationService();
        this.taskResponseMap = new HashMap<>();
        this.taskResponseMap.put(this.stepName, this.taskResponse);
    }

    @Test
    public void orchestrateWorkflow_should_successfully_orchestrate_the_given_job() throws Exception
    {
        when(this.jobService.getWorkflowService()).thenReturn(this.workflowService);
        when(this.job.getWorkflow()).thenReturn(this.workFlowName);
        when(this.job.getStep()).thenReturn(this.stepName);
        when(this.workflowService.findNextStep(anyString(), anyString())).thenReturn(this.step, null);
        when(this.job.getCurrentTask()).thenReturn(this.workflowTask);
        when(this.workflowTask.getTaskHandler()).thenReturn(this.workflowTaskHandler);
        when(this.workflowTaskHandler.preExecute(this.job)).thenReturn(true);
        when(this.workflowTaskHandler.executeTask(this.job)).thenReturn(true);
        when(this.workflowTaskHandler.postExecute(this.job)).thenReturn(true);
        when(this.job.getTaskResponseMap()).thenReturn(this.taskResponseMap);
        when(this.taskResponse.getWorkFlowTaskStatus()).thenReturn(Status.IN_PROGRESS, Status.SUCCEEDED);
        when(this.workflowService.advanceToNextStep(any(), anyString(), anyString())).thenReturn(this.job);

        this.service.orchestrateWorkflow(this.job, this.jobService);

        verify(this.job).setStatus(Status.IN_PROGRESS);
        verify(this.job).setStatus(Status.SUCCEEDED);
    }

    @Test
    public void orchestrateWorkflow_should_fail_when_the_preexecute_step_fails() throws Exception
    {
        when(this.jobService.getWorkflowService()).thenReturn(this.workflowService);
        when(this.job.getWorkflow()).thenReturn(this.workFlowName);
        when(this.job.getStep()).thenReturn(this.stepName);
        when(this.workflowService.findNextStep(anyString(), anyString())).thenReturn(this.step, null);
        when(this.job.getCurrentTask()).thenReturn(this.workflowTask);
        when(this.workflowTask.getTaskHandler()).thenReturn(this.workflowTaskHandler);
        when(this.workflowTaskHandler.preExecute(this.job)).thenReturn(false);

        this.service.orchestrateWorkflow(this.job, this.jobService);

        verify(this.job).setStatus(Status.IN_PROGRESS);
        verify(this.job).setStatus(Status.FAILED);
    }

    @Test
    public void orchestrateWorkflow_should_fail_when_the_executetask_step_fails() throws Exception
    {
        when(this.jobService.getWorkflowService()).thenReturn(this.workflowService);
        when(this.job.getWorkflow()).thenReturn(this.workFlowName);
        when(this.job.getStep()).thenReturn(this.stepName);
        when(this.workflowService.findNextStep(anyString(), anyString())).thenReturn(this.step, null);
        when(this.job.getCurrentTask()).thenReturn(this.workflowTask);
        when(this.workflowTask.getTaskHandler()).thenReturn(this.workflowTaskHandler);
        when(this.workflowTaskHandler.preExecute(this.job)).thenReturn(true);
        when(this.workflowTaskHandler.executeTask(this.job)).thenReturn(false);

        this.service.orchestrateWorkflow(this.job, this.jobService);

        verify(this.job).setStatus(Status.IN_PROGRESS);
        verify(this.job).setStatus(Status.FAILED);
    }

    @Test
    public void orchestrateWorkflow_should_fail_when_the_postexecute_step_fails() throws Exception
    {
        when(this.jobService.getWorkflowService()).thenReturn(this.workflowService);
        when(this.job.getWorkflow()).thenReturn(this.workFlowName);
        when(this.job.getStep()).thenReturn(this.stepName);
        when(this.workflowService.findNextStep(anyString(), anyString())).thenReturn(this.step, null);
        when(this.job.getCurrentTask()).thenReturn(this.workflowTask);
        when(this.workflowTask.getTaskHandler()).thenReturn(this.workflowTaskHandler);
        when(this.workflowTaskHandler.preExecute(this.job)).thenReturn(true);
        when(this.workflowTaskHandler.executeTask(this.job)).thenReturn(true);
        when(this.workflowTaskHandler.postExecute(this.job)).thenReturn(false);

        this.service.orchestrateWorkflow(this.job, this.jobService);

        verify(this.job).setStatus(Status.IN_PROGRESS);
        verify(this.job).setStatus(Status.FAILED);
    }
}
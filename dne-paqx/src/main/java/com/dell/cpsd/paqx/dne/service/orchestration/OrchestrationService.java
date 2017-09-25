/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.orchestration;

import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.domain.WorkflowTask;
import com.dell.cpsd.paqx.dne.service.IBaseService;
import com.dell.cpsd.paqx.dne.service.WorkflowService;
import com.dell.cpsd.paqx.dne.service.model.Status;
import com.dell.cpsd.paqx.dne.service.model.TaskResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import java.util.Map;

/**
 * Orchestration service.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public class OrchestrationService implements IOrchestrationService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(OrchestrationService.class);
    private static final int WAIT_TIME = 100; // 100 milli-seconds

    @Async
    public void orchestrateWorkflow(Job job, IBaseService jobService)
    {
        job.setStatus(Status.IN_PROGRESS);
        WorkflowService workflowService = jobService.getWorkflowService();
        while (workflowService.findNextStep(job.getWorkflow(), job.getStep()) != null)
        {
            LOGGER.info("starting Orchestration on job:" + job);
            WorkflowTask task = job.getCurrentTask();
            if (task != null)
            {
                if (task.getTaskHandler().preExecute(job))
                {
                    if (task.getTaskHandler().executeTask(job))
                    {
                        //wait fot the execute task to finish.
                        while (getTaskResponseStatus(job) == Status.IN_PROGRESS)
                        {
                            try
                            {
                                Thread.sleep(WAIT_TIME);
                            }
                            catch (Exception e)
                            {
                            }
                        }
                        if (task.getTaskHandler().postExecute(job))
                        {

                        }
                        else
                        {
                            //post execute failed
                            job.setStatus(Status.FAILED);
                            LOGGER.info("Finished Orchestration on job:" + job);
                            return;
                        }
                    }
                    else
                    {
                        //execute failed
                        job.setStatus(Status.FAILED);
                        LOGGER.info("Finished Orchestration on job:" + job);
                        return;
                    }
                }
                else
                {
                    //pre execute failed
                    job.setStatus(Status.FAILED);
                    LOGGER.info("Finished Orchestration on job:" + job);
                    return;
                }
            }
            job = workflowService.advanceToNextStep(job, job.getStep(), "Inprogress");
        }
        job.setStatus(Status.SUCCEEDED);
        LOGGER.info("Finished Orchestration on job:" + job);
        return;
    }

    private Status getTaskResponseStatus(Job job)
    {
        Status status = Status.UNKNOWN;
        Map<String, TaskResponse> taskResponseMap = job.getTaskResponseMap();
        TaskResponse taskResponse = taskResponseMap.get(job.getStep());
        if (taskResponse != null)
        {
            status = taskResponse.getWorkFlowTaskStatus();
        }

        return status;
    }

}

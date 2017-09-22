/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.task.handler.preprocess;

import com.dell.cpsd.paqx.dne.domain.IWorkflowTaskHandler;
import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.model.*;
import com.dell.cpsd.paqx.dne.service.task.handler.BaseTaskHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Task responsible for handling Boot Order Sequence
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
@Component
public class ConfigureBootDeviceIdracTaskHandler extends BaseTaskHandler implements IWorkflowTaskHandler {
    /**
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigureBootDeviceIdracTaskHandler.class);

    /**
     * The <code>NodeService</code> instance
     */
    private NodeService nodeService;

    /**
     *  SetBootOrderAndDisablePXETaskHandler constructor.
     *
     * @param nodeService
     *            - The <code>NodeService</code> instance.
     *
     * @since 1.0
     */
    public ConfigureBootDeviceIdracTaskHandler(NodeService nodeService){
        this.nodeService = nodeService;
    }

    /**
     * Perform the task of setting up boot order and disabling PXE
     *
     * @param job
     *            - The <code>Job</code> this task is part of.
     *
     * @since 1.0
     */
    @Override
    public boolean executeTask(Job job)
    {   LOGGER.info("Execute BootOrderSequence Task");
        TaskResponse response = initializeResponse(job);

        try
        {
            Map<String, TaskResponse> responseMap = job.getTaskResponseMap();
            TaskResponse findNodeTask = responseMap.get("findAvailableNodes");
            if (findNodeTask == null)
            {
                throw new IllegalStateException("No discovered node task found.");
            }
            if (findNodeTask.getResults() == null)
            {
                throw new IllegalStateException("No results found.");
            }

            if (findNodeTask.getResults().get("symphonyUuid") == null)
            {
                throw new IllegalStateException("No discovered node info found.");
            }

            String uuid = findNodeTask.getResults().get("symphonyUuid");
            String ipAddress = job.getInputParams().getIdracIpAddress();

            LOGGER.info("uuid:" + uuid);
            LOGGER.info("ipAddress:" + ipAddress);

            ConfigureBootDeviceIdracRequest configureBootDeviceIdracRequest = new ConfigureBootDeviceIdracRequest();
            configureBootDeviceIdracRequest.setUuid(uuid);
            configureBootDeviceIdracRequest.setIdracIpAddress(ipAddress);

            BootDeviceIdracStatus bootDeviceIdracStatus = nodeService.bootDeviceIdracStatus(configureBootDeviceIdracRequest);
            if ("SUCCESS".equalsIgnoreCase(bootDeviceIdracStatus.getStatus()))
            {
                response.setResults(buildResponseResult(bootDeviceIdracStatus));
                response.setWorkFlowTaskStatus(Status.SUCCEEDED);
                return true;
            }
            else{
                if ( bootDeviceIdracStatus.getErrors()!= null )
                    response.addError(bootDeviceIdracStatus.getErrors().toString());
            }
        }
        catch(Exception e){
            LOGGER.error("Error showing boot order status", e);
            response.addError(e.toString());
        }
        response.setWorkFlowTaskStatus(Status.FAILED);
        return false;
    }

    /*
     * This method add all the node information to the response object
     */
    private Map<String, String> buildResponseResult(BootDeviceIdracStatus bootDeviceIdracStatus)
    {
        Map<String, String> result = new HashMap<>();

        if (bootDeviceIdracStatus == null)
        {
            return result;
        }

        if (bootDeviceIdracStatus.getStatus() != null)
        {
            result.put("bootDeviceIdracStatus", bootDeviceIdracStatus.getStatus());
        }

        if (bootDeviceIdracStatus.getErrors() != null)
        {
            result.put("bootDeviceIdracErrorsList", bootDeviceIdracStatus.getErrors().toString());
        }

        return result;
    }

    /**
     * Create the <code>BootOrderSequenceResponse</code> instance and initialize it.
     *
     * @param job
     *            - The <code>Job</code> this task is part of.
     */
    @Override
    public ConfigureBootDeviceIdracResponse initializeResponse(Job job)
    {
        ConfigureBootDeviceIdracResponse response = new ConfigureBootDeviceIdracResponse();
        response.setWorkFlowTaskName(job.getCurrentTask().getTaskName());
        response.setWorkFlowTaskStatus(Status.IN_PROGRESS);
        job.addTaskResponse(job.getStep(), response);
        return response;
    }
}

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

import java.util.HashMap;
import java.util.Map;

/**
 * Task responsible for configuring the iDRAC network settings.
 * 
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public class ConfigIdracTaskHandler extends BaseTaskHandler implements IWorkflowTaskHandler
{
    /*
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigIdracTaskHandler.class);

    /*
     * The <code>NodeService</code> instance
     */
    private NodeService         nodeService;

    /**
     * ConfigIdracTaskHandler constructor.
     * 
     * @param nodeService
     *            - The <code>NodeService</code> instance.
     * 
     * @since 1.0
     */
    public ConfigIdracTaskHandler(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    /**
     * Perform the task of configuring the iDRAC network settings.
     * 
     * @param job
     *            - The <code>Job</code> this task is part of.
     * 
     * @since 1.0
     */
    @Override
    public boolean executeTask(Job job)
    {
        LOGGER.info("Execute ConfigIdracTaskHandler task");

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
            String gatewayIpAddress = job.getInputParams().getIdracGatewayIpAddress();
            String subnetMask = job.getInputParams().getIdracSubnetMask();

            LOGGER.info("uuid:" + uuid);
            LOGGER.info("Idrac input request parameters: " + job.getInputParams().toString());

            IdracNetworkSettingsRequest idracNetworkSettingsRequest = new IdracNetworkSettingsRequest();
            idracNetworkSettingsRequest.setUuid(uuid);
            idracNetworkSettingsRequest.setIdracIpAddress(ipAddress);
            idracNetworkSettingsRequest.setIdracGatewayIpAddress(gatewayIpAddress);
            idracNetworkSettingsRequest.setIdracSubnetMask(subnetMask);

            IdracInfo idracInfo = nodeService.idracNetworkSettings(idracNetworkSettingsRequest);
            if ("SUCCESS".equalsIgnoreCase(idracInfo.getMessage()))
            {
                response.setResults(buildResponseResult(idracInfo));
                response.setWorkFlowTaskStatus(Status.SUCCEEDED);
                return true;
            }
            else {
                response.addError(idracInfo.getMessage());
            }
        }
        catch (Exception e)
        {
            LOGGER.error("Error configuring idrac network settings", e);
            response.addError(e.toString());
        }

        response.setWorkFlowTaskStatus(Status.FAILED);
        return false;
    }

    /*
     * This method add all the node information to the response object
     */
    private Map<String, String> buildResponseResult(IdracInfo idracInfo)
    {
        Map<String, String> result = new HashMap<>();

        if (idracInfo == null)
        {
            return result;
        }

        if (idracInfo.getIdracIpAddress() != null)
        {
            result.put("idracIpAddress", idracInfo.getIdracIpAddress());
        }

        if (idracInfo.getIdracGatewayIpAddress() != null)
        {
            result.put("idracGatewayIpAddress", idracInfo.getIdracGatewayIpAddress());
        }

        if (idracInfo.getIdracSubnetMask() != null)
        {
            result.put("idracSubnetMask", idracInfo.getIdracSubnetMask());
        }

        return result;
    }

    /**
     * Create the <code>IdracNetworkSettingsResponseInfo</code> instance and initialize it.
     * 
     * @param job
     *            - The <code>Job</code> this task is part of.
     */
    @Override
    public IdracNetworkSettingsResponseInfo initializeResponse(Job job)
    {
        IdracNetworkSettingsResponseInfo response = new IdracNetworkSettingsResponseInfo();
        response.setWorkFlowTaskName(job.getCurrentTask().getTaskName());
        response.setWorkFlowTaskStatus(Status.IN_PROGRESS);
        job.addTaskResponse(job.getStep(), response);
        return response;
    }
}

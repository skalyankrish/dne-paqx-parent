/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.task.handler.preprocess;

import com.dell.cpsd.paqx.dne.domain.IWorkflowTaskHandler;
import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.model.Status;
import com.dell.cpsd.paqx.dne.service.model.TaskResponse;
import com.dell.cpsd.paqx.dne.service.task.handler.BaseTaskHandler;
import com.dell.cpsd.virtualization.capabilities.api.ClusterInfo;
import com.dell.cpsd.virtualization.capabilities.api.ValidateVcenterClusterResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public class FindVClusterTaskHandler extends BaseTaskHandler implements IWorkflowTaskHandler
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FindVClusterTaskHandler.class);

    private NodeService nodeService;

    public FindVClusterTaskHandler(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    @Override
    public boolean executeTask(Job job)
    {
        LOGGER.info("Execute FindVCenterCluster task");

        TaskResponse response = initializeResponse(job);

        try
        {
            List<ClusterInfo> clusterInfo = nodeService.listClusters();
            ValidateVcenterClusterResponseMessage responseMsg = nodeService.validateClusters(clusterInfo);
            if (responseMsg.getClusters().size() > 0)
            {
                Map<String, String> result = new HashMap<>();
                result.put("clusterName", responseMsg.getClusters().get(0));
                response.setResults(result);
                response.setWorkFlowTaskStatus(Status.SUCCEEDED);
                return true;
            }

            response.setWorkFlowTaskStatus(Status.FAILED);
            responseMsg.getFailedCluster().forEach(response::addError);
        }
        catch (Exception ex)
        {
            LOGGER.info("Error while finding vcluster", ex);
            response.setWorkFlowTaskStatus(Status.FAILED);
            response.addError(ex.toString());
        }

        return false;
    }
}

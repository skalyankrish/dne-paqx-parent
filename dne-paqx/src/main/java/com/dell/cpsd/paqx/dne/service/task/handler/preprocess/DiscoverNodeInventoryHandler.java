/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.task.handler.preprocess;

import com.dell.cpsd.paqx.dne.domain.IWorkflowTaskHandler;
import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.domain.inventory.NodeInventory;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.model.FirstAvailableDiscoveredNodeResponse;
import com.dell.cpsd.paqx.dne.service.model.NodeInfo;
import com.dell.cpsd.paqx.dne.service.model.Status;
import com.dell.cpsd.paqx.dne.service.model.TaskResponse;
import com.dell.cpsd.paqx.dne.service.task.handler.BaseTaskHandler;
import com.dell.cpsd.service.common.client.exception.ServiceExecutionException;
import com.dell.cpsd.service.common.client.exception.ServiceTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * This class implements the logic to discover RackHD Node Inventory.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public class DiscoverNodeInventoryHandler extends BaseTaskHandler implements IWorkflowTaskHandler
{
    /*
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscoverNodeInventoryHandler.class);

    /*
     * Node Service instance used to find and validate Storage pool entries
     */
    private NodeService nodeService;

    /*
     * Reference to H2 repository
     */
    private final DataServiceRepository repository;

    /**
     * Construct an instance based on nodeservice reference
     *
     * @param nodeService
     */
    public DiscoverNodeInventoryHandler(final NodeService nodeService, final DataServiceRepository repository)
    {
        this.nodeService = nodeService;
        this.repository = repository;
    }

    @Override
    public boolean executeTask(final Job job)
    {
        LOGGER.info("Execute DiscoverNodeInventoryHandler task.");

        TaskResponse response = initializeResponse(job);

        NodeInfo nodeInfo = findNodeInfo(job.getTaskResponseList());

        if (nodeInfo != null)
        {
            try
            {
                String symphonyUuid = nodeInfo.getSymphonyUuid();

                Object nodeInventoryResponse = nodeService.listNodeInventory(symphonyUuid);
                if (nodeInventoryResponse != null)
                {

                    NodeInventory nodeInventory = new NodeInventory(symphonyUuid, nodeInventoryResponse.toString());
                    boolean isNodeInventorySaved = repository.saveNodeInventory(nodeInventory);
                    if(isNodeInventorySaved) {
                        response.setWorkFlowTaskStatus(Status.SUCCEEDED);

                        return isNodeInventorySaved;
                    }
                } else {
                    LOGGER.info("There is no node inventory for UUID ", symphonyUuid);
                    response.addError("There is no node inventory for UUID " + symphonyUuid);
                }
            }
            catch (ServiceTimeoutException | ServiceExecutionException ex)
            {
                LOGGER.error("Node Inventory discover failed : ", ex);
                response.addError("Unable to discover node inventory.");
            }
        } else {
            response.addError("There is no discovered node available.");
        }

        response.setWorkFlowTaskStatus(Status.FAILED);
        return false;
    }

    /*
     * Find Node previously configured on the workflow execution.
     */
    private NodeInfo findNodeInfo(List<TaskResponse> taskResponses)
    {
        NodeInfo result = null;

        if (taskResponses != null)
        {
            for (TaskResponse taskResponse : taskResponses)
            {
                //Search for Discovered node response
                if (taskResponse instanceof FirstAvailableDiscoveredNodeResponse)
                {
                    result = ((FirstAvailableDiscoveredNodeResponse) taskResponse).getNodeInfo();
                    break;
                }
            }
        }

        return result;
    }

}

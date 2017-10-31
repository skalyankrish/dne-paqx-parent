/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.domain.node.NodeInventory;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.model.FirstAvailableDiscoveredNodeResponse;
import com.dell.cpsd.paqx.dne.service.model.NodeInfo;
import com.dell.cpsd.paqx.dne.service.model.TaskResponse;
import com.dell.cpsd.service.common.client.exception.ServiceExecutionException;
import com.dell.cpsd.service.common.client.exception.ServiceTimeoutException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.collections.CollectionUtils;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.DISCOVERED_NODES;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NO_DISCOVERED_NODES;

@Component
@Scope("prototype")
@Qualifier("inventoryNodes")
public class InventoryNodes extends BaseWorkflowDelegate
{
    /*
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryNodes.class);

    /*
     * Node Service instance used to find and validate Storage pool entries
     */
    private final NodeService nodeService;

    /*
     * Reference to H2 repository
     */
    private final DataServiceRepository repository;

    @Autowired
    public InventoryNodes(final NodeService nodeService, final DataServiceRepository repository)
    {
        this.nodeService = nodeService;
        this.repository = repository;
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

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Execute InventoryNodes");

        List<NodeInfo> discoveredNodes = (List<NodeInfo>) delegateExecution.getVariable(DISCOVERED_NODES);
        List<NodeInfo> removeNodes = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(discoveredNodes))
        {
            discoveredNodes.forEach(nodeInfo -> {
                    try
                    {
                        String symphonyUUID = nodeInfo.getSymphonyUuid();
                        Object nodeInventoryResponse = nodeService.listNodeInventory(symphonyUUID);
                        if (nodeInventoryResponse != null)
                        {
                            NodeInventory nodeInventory = new NodeInventory(symphonyUUID, nodeInventoryResponse.toString());
                            boolean isNodeInventorySaved = repository.saveNodeInventory(nodeInventory);
                            if (!isNodeInventorySaved)
                            {
                                LOGGER.error("Node Inventory save failed for node " + nodeInfo.getSymphonyUuid());
                                removeNodes.add(nodeInfo);
                            }
                        }
                        else
                        {
                            LOGGER.error("Node Inventory retrieval failed for node " + nodeInfo.getSymphonyUuid());
                            removeNodes.add(nodeInfo);
                        }
                    }
                    catch (JsonProcessingException | ServiceTimeoutException | ServiceExecutionException ex)
                    {
                        LOGGER.error("Node Inventory request failed for node " + nodeInfo.getSymphonyUuid() , ex);
                        removeNodes.add(nodeInfo);
                    }
            });
            if (CollectionUtils.isNotEmpty(removeNodes)) {
                discoveredNodes.removeAll(removeNodes);
                delegateExecution.setVariable(DISCOVERED_NODES, discoveredNodes);
            }
        }
        if (CollectionUtils.isEmpty(discoveredNodes)) {
            LOGGER.error(NO_DISCOVERED_NODES, "There are no nodes currently discovered in Rack HD");
            updateDelegateStatus("There are no nodes currently discovered in Rack HD");
            throw new BpmnError(NO_DISCOVERED_NODES, "There are no nodes currently discovered in Rack HD");
        }
        LOGGER.info("Execute InventoryNodes completed");
    }
}

/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.domain.node.NodeInventory;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
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
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.INVENTORY_NODE_FAILED;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NO_DISCOVERED_NODES;

@Component
@Scope("prototype")
@Qualifier("inventoryNode")
public class InventoryNode extends BaseWorkflowDelegate
{
    /*
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryNode.class);

    /*
     * Node Service instance used to find and validate Storage pool entries
     */
    private NodeService nodeService;

    /*
     * Reference to H2 repository
     */
    private final DataServiceRepository repository;

    @Autowired
    public InventoryNode(final NodeService nodeService, final DataServiceRepository repository)
    {
        this.nodeService = nodeService;
        this.repository = repository;
    }


    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.debug("Execute InventoryNode");

        NodeDetail nodeDetail = (NodeDetail) delegateExecution.getVariable(NODE_DETAIL);
        Object nodeInventoryResponse = null;
        if (nodeDetail != null && nodeDetail.getId() != null)
        {
            try
            {
                updateDelegateStatus("Requesting Update Node Inventory for Node " + nodeDetail.getServiceTag());
                nodeInventoryResponse = nodeService.listNodeInventory(nodeDetail.getId());
            }
            catch (ServiceTimeoutException | ServiceExecutionException ex)
            {
                final String message = "Update Node Inventory request failed for Node " + nodeDetail.getServiceTag();
                LOGGER.error(message, ex);
                updateDelegateStatus(message);
                throw new BpmnError(INVENTORY_NODE_FAILED, message);
            }
        }
        NodeInventory nodeInventory = null;
        if (nodeInventoryResponse != null)
        {
            try
            {
                nodeInventory = new NodeInventory(nodeDetail.getId(), nodeInventoryResponse.toString());
            }
            catch (JsonProcessingException jpe) {
                final String message = "Update Node Inventory failed due to unrecognized response for Node " + (nodeDetail==null?null:nodeDetail.getServiceTag());
                LOGGER.error(message, jpe);
                updateDelegateStatus(message);
                throw new BpmnError(INVENTORY_NODE_FAILED, message);
            }
        }
        boolean isNodeInventorySaved = false;
        if (nodeInventory != null)
        {
            isNodeInventorySaved = repository.saveNodeInventory(nodeInventory);
        }
        if (!isNodeInventorySaved)
        {
            final String message = "Update Node Inventory failed for Node " + (nodeDetail==null?null:nodeDetail.getServiceTag());
            LOGGER.error(message);
            updateDelegateStatus(message);
            throw new BpmnError(INVENTORY_NODE_FAILED, message);
        }
        final String message = "Update Node Inventory was successful for Node " + nodeDetail.getServiceTag();
        LOGGER.debug(message);
        updateDelegateStatus(message);

    }
}

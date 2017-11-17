
/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import com.dell.cpsd.service.common.client.exception.ServiceExecutionException;
import com.dell.cpsd.service.common.client.exception.ServiceTimeoutException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.codehaus.jackson.map.JsonMappingException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.INVENTORY_NODE_FAILED;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@RunWith(MockitoJUnitRunner.class)
public class InventoryNodeTest
{

    private InventoryNode inventoryNode;

    @Mock
    private NodeService nodeService;

    @Mock
    private DataServiceRepository dataServiceRepository;

    @Mock
    private DelegateExecution delegateExecution;

    private String nodeInventoryResponse = "TestResponse";

    @Before
    public void setUp() throws Exception
    {
        inventoryNode = new InventoryNode(nodeService, dataServiceRepository);

        NodeDetail nodeDetail = new NodeDetail("1", "abc");

        doReturn(nodeDetail).when(delegateExecution).getVariable(NODE_DETAIL);

        doReturn(nodeInventoryResponse).when(nodeService).listNodeInventory("1");

        doReturn(true).when(dataServiceRepository).saveNodeInventory(any());

    }

    @Test
    public void testSuccessful() throws Exception {
        inventoryNode.delegateExecute(delegateExecution);
    }

    @Test
    public void testServiceExecutionException() throws Exception
    {
        try
        {
            doThrow(new ServiceExecutionException("ServiceExecutionException")).when(nodeService).listNodeInventory("1");
            inventoryNode.delegateExecute(delegateExecution);
            fail("Should not be getting here");
        } catch(BpmnError bpmnError) {
            assertEquals(bpmnError.getErrorCode(), INVENTORY_NODE_FAILED);
            assertEquals(bpmnError.getMessage(), "Update Node Inventory request failed for Node abc");
        }
    }

    @Test
    public void testServiceTimeoutException() throws Exception
    {
        try
        {
            doThrow(new ServiceTimeoutException("ServiceTimeoutException")).when(nodeService).listNodeInventory("1");
            inventoryNode.delegateExecute(delegateExecution);
            fail("Should not be getting here");
        } catch(BpmnError bpmnError) {
            assertEquals(bpmnError.getErrorCode(), INVENTORY_NODE_FAILED);
            assertEquals(bpmnError.getMessage(), "Update Node Inventory request failed for Node abc");
        }
    }

    @Test
    public void testNodeInventorySaveFailed() throws Exception
    {
        try
        {
            doReturn(false).when(dataServiceRepository).saveNodeInventory(any());
            inventoryNode.delegateExecute(delegateExecution);
            fail("Should not be getting here");
        } catch(BpmnError bpmnError) {
            assertEquals(bpmnError.getErrorCode(), INVENTORY_NODE_FAILED);
            assertEquals(bpmnError.getMessage(), "Update Node Inventory failed for Node abc");
        }
    }

}

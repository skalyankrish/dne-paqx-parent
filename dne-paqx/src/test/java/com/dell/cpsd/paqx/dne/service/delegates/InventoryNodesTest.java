
/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.domain.node.NodeInventory;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants;
import com.dell.cpsd.paqx.dne.service.model.NodeInfo;
import com.dell.cpsd.paqx.dne.service.model.NodeStatus;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.DISCOVERED_NODES;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InventoryNodesTest {

    private InventoryNodes inventoryNodes;
    private NodeService nodeService;
    private DataServiceRepository dataServiceRepository;
    private DelegateExecution delegateExecution;
    private List<NodeInfo> discoveredNodes;
    private NodeInfo discoveredNode;
    private Object nodeInventoryResponse;
    private NodeInventory nodeInventory;
    private DataServiceRepository repository;

    @Before
    public void setUp() throws Exception
    {
        nodeService = mock(NodeService.class);
        dataServiceRepository = mock(DataServiceRepository.class);
        delegateExecution = mock(DelegateExecution.class);
        inventoryNodes = new InventoryNodes(nodeService, dataServiceRepository);
        nodeInventoryResponse = mock(Object.class);
        nodeInventory = mock(NodeInventory.class);
        repository = mock(DataServiceRepository.class);
        discoveredNodes = new ArrayList<NodeInfo>();
        discoveredNode = mock(NodeInfo.class);
        discoveredNode = new NodeInfo("1", NodeStatus.DISCOVERED.DISCOVERED);
        discoveredNodes.add(discoveredNode);
    }

    @Ignore @Test
    public void nodeInventoryFailed() throws Exception
    {
        try {
            when(delegateExecution.getVariable(DISCOVERED_NODES)).thenReturn(discoveredNodes);
            inventoryNodes.delegateExecute(delegateExecution);
        } catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.NO_DISCOVERED_NODES));
        }
    }

    @Ignore @Test
    public void nodeInventoryNotSaved() throws Exception
    {
        try {
            when(delegateExecution.getVariable(DISCOVERED_NODES)).thenReturn(discoveredNodes);
            when(nodeService.listNodeInventory("1")).thenReturn(nodeInventoryResponse);
            when(repository.saveNodeInventory(nodeInventory)).thenReturn(false);

            inventoryNodes.delegateExecute(delegateExecution);
        } catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.NO_DISCOVERED_NODES));
        }
    }

    @Ignore @Test
    public void noDiscoveredNodes() throws Exception
    {
        try {
            when(repository.saveNodeInventory(nodeInventory)).thenReturn(true);
            inventoryNodes.delegateExecute(delegateExecution);
        } catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.NO_DISCOVERED_NODES));
        }
    }

    @Ignore @Test
    public void nodeDiscoveryException() throws Exception
    {
        try {
            when(delegateExecution.getVariable(DISCOVERED_NODES)).thenReturn(discoveredNodes);
            when(nodeService.listNodeInventory("1")).thenReturn(nodeInventoryResponse);
            given(repository.saveNodeInventory(nodeInventory)).willThrow(new NullPointerException());
            inventoryNodes.delegateExecute(delegateExecution);
        } catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.NO_DISCOVERED_NODES));
        }
    }
}

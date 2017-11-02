
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
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.paqx.dne.service.model.NodeInfo;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class InventoryScaleIOTest {

    private InventoryScaleIO inventoryScaleIO;
    private NodeService nodeService;
    private DelegateExecution delegateExecution;
    private List<NodeInfo> discoveredNodes;
    private NodeInfo discoveredNode;
    private Object nodeInventoryResponse;
    private NodeInventory nodeInventory;
    private DataServiceRepository repository;
    private ComponentEndpointIds componentEndpointIds;

    @Before
    public void setUp() throws Exception
    {
        nodeService = mock(NodeService.class);
        delegateExecution = mock(DelegateExecution.class);
        nodeInventoryResponse = mock(Object.class);
        nodeInventory = mock(NodeInventory.class);
        repository = mock(DataServiceRepository.class);
        inventoryScaleIO = new InventoryScaleIO(nodeService, repository);
        discoveredNodes = new ArrayList<NodeInfo>();
        discoveredNode = mock(NodeInfo.class);
        componentEndpointIds = new ComponentEndpointIds("abc","abc","abc", "abc");

    }

    @Ignore @Test
    public void scaleIoInventoryFailed() throws Exception {
        try {
            inventoryScaleIO = new InventoryScaleIO(nodeService, null);
            inventoryScaleIO.delegateExecute(delegateExecution);
        } catch (BpmnError error) {
            assertTrue(error.getErrorCode().equals(DelegateConstants.INVENTORY_SCALE_IO_FAILED));
            assertTrue(error.getMessage().contains("An Unexpected Exception occurred attempting to inventory Scale IO."));
        }
    }


    @Ignore @Test
    public void scaleIoInventoryNotSaved() throws Exception {
        try {
            when(repository.getComponentEndpointIds("SCALEIO-CLUSTER")).thenReturn(null);
            inventoryScaleIO.delegateExecute(delegateExecution);
        } catch (BpmnError error) {
            assertTrue(error.getErrorCode().equals(DelegateConstants.SCALE_IO_INFORMATION_NOT_FOUND));
            assertTrue(error.getMessage().contains("Scale IO Endpoints not found."));
        }
    }

    @Ignore @Test
    public void scaleIoDiscoveryException() throws Exception {
        try {
            when(repository.getComponentEndpointIds("SCALEIO-CLUSTER")).thenReturn(componentEndpointIds);
            given(delegateExecution.getProcessInstanceId()).willThrow(new NullPointerException());
            inventoryScaleIO.delegateExecute(delegateExecution);
        } catch (BpmnError error) {
            assertTrue(error.getErrorCode().equals(DelegateConstants.INVENTORY_SCALE_IO_FAILED));
            assertTrue(error.getMessage().contains("An Unexpected Exception occurred attempting to inventory Scale IO."));
        }
    }

    @Ignore @Test
    public void scaleIoDiscoveryFailed() throws Exception {
        try {
            when(repository.getComponentEndpointIds("SCALEIO-CLUSTER")).thenReturn(componentEndpointIds);
            when(delegateExecution.getProcessInstanceId()).thenReturn("1");
            when(nodeService.requestDiscoverScaleIo(any(), any())).thenReturn(false);
            inventoryScaleIO.delegateExecute(delegateExecution);
        } catch (BpmnError error) {
            assertTrue(error.getErrorCode().equals(DelegateConstants.INVENTORY_SCALE_IO_FAILED));
            assertTrue(error.getMessage().contains("Inventory request for Scale IO Failed."));
        }
    }

    @Ignore @Test
    public void scaleIoDiscoverySuccess() throws Exception {
        when(repository.getComponentEndpointIds("SCALEIO-CLUSTER")).thenReturn(componentEndpointIds);
        when(delegateExecution.getProcessInstanceId()).thenReturn("1");
        when(nodeService.requestDiscoverScaleIo(any(), any())).thenReturn(true);
        final InventoryScaleIO c = spy(new InventoryScaleIO(nodeService, repository));
        c.delegateExecute(delegateExecution);
        verify(c).updateDelegateStatus("Inventory request for Scale IO completed successfully.");
    }
}


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

public class InventoryVCenterTest {

    private InventoryVCenter inventoryVCenter;
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
        inventoryVCenter = new InventoryVCenter(nodeService, repository);
        discoveredNodes = new ArrayList<NodeInfo>();
        discoveredNode = mock(NodeInfo.class);
        componentEndpointIds = new ComponentEndpointIds("abc","abc","abc", "abc");

    }

    @Ignore @Test
    public void vcInventoryFailed() throws Exception {
        try {
            inventoryVCenter = new InventoryVCenter(nodeService, null);
            inventoryVCenter.delegateExecute(delegateExecution);
        } catch (BpmnError error) {
            assertTrue(error.getErrorCode().equals(DelegateConstants.INVENTORY_VCENTER_FAILED));
            assertTrue(error.getMessage().contains("An Unexpected Exception occurred attempting to inventory VCenter."));
        }
    }

    @Ignore @Test
    public void scaleIoInventoryNotSaved() throws Exception {
        try {
            when(repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER")).thenReturn(null);
            inventoryVCenter.delegateExecute(delegateExecution);
        } catch (BpmnError error) {
            assertTrue(error.getErrorCode().equals(DelegateConstants.VCENTER_INFORMATION_NOT_FOUND));
            assertTrue(error.getMessage().contains("VCenter Endpoints not found."));
        }
    }

    @Ignore @Test
    public void scaleIoDiscoveryException() throws Exception {
        try {
            when(repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER")).thenReturn(componentEndpointIds);
            given(delegateExecution.getProcessInstanceId()).willThrow(new NullPointerException());
            inventoryVCenter.delegateExecute(delegateExecution);
        } catch (BpmnError error) {
            assertTrue(error.getErrorCode().equals(DelegateConstants.INVENTORY_VCENTER_FAILED));
            assertTrue(error.getMessage().contains("An Unexpected Exception occurred attempting to inventory VCenter."));
        }
    }

    @Ignore @Test
    public void scaleIoDiscoveryFailed() throws Exception {
        try {
            when(repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER")).thenReturn(componentEndpointIds);
            when(delegateExecution.getProcessInstanceId()).thenReturn("1");
            when(nodeService.requestDiscoverVCenter(any(), any())).thenReturn(false);
            inventoryVCenter.delegateExecute(delegateExecution);
        } catch (BpmnError error) {
            assertTrue(error.getErrorCode().equals(DelegateConstants.INVENTORY_VCENTER_FAILED));
            assertTrue(error.getMessage().contains("Inventory request for VCenter Failed."));
        }
    }

    @Ignore @Test
    public void scaleIoDiscoverySuccess() throws Exception {
        when(repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER")).thenReturn(componentEndpointIds);
        when(delegateExecution.getProcessInstanceId()).thenReturn("1");
        when(nodeService.requestDiscoverVCenter(any(), any())).thenReturn(true);
        final InventoryVCenter c = spy(new InventoryVCenter(nodeService, repository));
        c.delegateExecute(delegateExecution);
        verify(c).updateDelegateStatus("Inventory request for VCenter completed successfully.");
    }
}

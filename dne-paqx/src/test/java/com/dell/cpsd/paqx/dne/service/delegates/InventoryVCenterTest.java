
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        componentEndpointIds = mock(ComponentEndpointIds.class);

    }

    @Ignore @Test
    public void dataRepoNull() throws Exception {
        try {
            inventoryVCenter = new InventoryVCenter(nodeService, null);
            inventoryVCenter.delegateExecute(delegateExecution);
        } catch (BpmnError error) {
            assertTrue(error.getErrorCode().equals(DelegateConstants.INVENTORY_VCENTER_FAILED));
        }
    }


    @Ignore @Test
    public void vCenterInvetoryFailed() throws Exception {
        try {
            when(repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER")).thenReturn(componentEndpointIds);
            inventoryVCenter.delegateExecute(delegateExecution);
        } catch (BpmnError error) {
            assertTrue(error.getErrorCode().equals(DelegateConstants.INVENTORY_VCENTER_FAILED));
        }
    }

    @Ignore @Test
    public void vCenterInfoNotFound() throws Exception {
        try {
            componentEndpointIds = null;
            when(repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER")).thenReturn(componentEndpointIds);
            inventoryVCenter.delegateExecute(delegateExecution);
        } catch (BpmnError error) {
            assertTrue(error.getErrorCode().equals(DelegateConstants.VCENTER_INFORMATION_NOT_FOUND));
        }
    }

    @Ignore @Test
    public void vCenterInvetoryException() throws Exception {
        try {
            when(repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER")).thenReturn(componentEndpointIds);
            given(delegateExecution.getProcessInstanceId()).willThrow(new NullPointerException());
            inventoryVCenter.delegateExecute(delegateExecution);
        } catch (BpmnError error) {
            assertTrue(error.getErrorCode().equals(DelegateConstants.INVENTORY_VCENTER_FAILED));
        }
    }
}

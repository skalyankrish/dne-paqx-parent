
/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.domain.node.DiscoveredNodeInfo;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class VerfiyNodesSelectedTest {

    private VerifyNodesSelected verifyNodesSelected;
    private NodeService nodeService;
    private DataServiceRepository repository;
    private DelegateExecution delegateExecution;
    private NodeDetail nodeDetail;
    private DiscoveredNodeInfo discoveredNodeInfo;
    private List<NodeDetail> nodeDetails = new ArrayList<>();
    private List<DiscoveredNodeInfo> discoveredNodes = new ArrayList<>();

    @Before
    public void setUp() throws Exception
    {
        nodeService = mock(NodeService.class);
        repository = mock(DataServiceRepository.class);
        verifyNodesSelected = new VerifyNodesSelected(nodeService);
        delegateExecution = mock(DelegateExecution.class);
        nodeDetail = new NodeDetail();
        nodeDetail.setServiceTag("abc");
        nodeDetail.setvMotionManagementIpAddress("abc");
        nodeDetail.setvMotionManagementSubnetMask("abc");
        nodeDetail.setScaleIoData1SvmIpAddress("abc");
        nodeDetail.setScaleIoData2SvmIpAddress("abc");
        nodeDetails.add(nodeDetail);
        discoveredNodeInfo = new DiscoveredNodeInfo("abc", "abc","abc","abc","abc","abc");
        discoveredNodes.add(discoveredNodeInfo);
    }

    @Ignore @Test
    public void testVerifyNodesFailed() throws Exception
    {
        try {
            nodeDetails.clear();
            when(delegateExecution.getVariable(DelegateConstants.NODE_DETAILS)).thenReturn(nodeDetails);
            when(nodeService.listDiscoveredNodeInfo()).thenReturn(discoveredNodes);
            verifyNodesSelected.delegateExecute(delegateExecution);
        } catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.VERIFY_NODES_SELECTED_FAILED));
            assertTrue(error.getMessage().contains("The List of Node Detail was not found!  Please add at least one Node Detail and try again."));
        }
    }

    @Ignore @Test
    public void testException() throws Exception
    {
        try {
            when(delegateExecution.getVariable(DelegateConstants.NODE_DETAILS)).thenReturn(nodeDetails);
            when(nodeService.listDiscoveredNodeInfo()).thenReturn(discoveredNodes);
            given(nodeService.listDiscoveredNodeInfo()).willThrow(new NullPointerException());
            verifyNodesSelected.delegateExecute(delegateExecution);
        } catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.VERIFY_NODES_SELECTED_FAILED));
            assertTrue(error.getMessage().contains("An Unexpected Exception occurred attempting to verify selected Nodes."));
        }
    }

    @Ignore @Test
    public void testNodeDetailsEmpty() throws Exception
    {
        try {
            when(delegateExecution.getVariable(DelegateConstants.NODE_DETAILS)).thenReturn(nodeDetails);
            when(nodeService.listDiscoveredNodeInfo()).thenReturn(discoveredNodes);
            nodeDetail.setServiceTag("xyz");
            verifyNodesSelected.delegateExecute(delegateExecution);
        } catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.VERIFY_NODES_SELECTED_FAILED));
            assertTrue(error.getMessage().contains("An Unexpected Exception occurred attempting to verify selected Nodes."));
        }
    }

    @Ignore @Test
    public void testVerifiedSuccess() throws Exception
    {
        when(delegateExecution.getVariable(DelegateConstants.NODE_DETAILS)).thenReturn(nodeDetails);
        when(nodeService.listDiscoveredNodeInfo()).thenReturn(discoveredNodes);
        final VerifyNodesSelected c = spy(new VerifyNodesSelected(nodeService));
        c.delegateExecute(delegateExecution);
        verify(c).updateDelegateStatus("Attempting to verify selected Nodes are still available.");
    }
}


/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants;
import com.dell.cpsd.paqx.dne.service.model.DiscoveredNode;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RetrieveVCenterComponetnsTest {

    private RetrieveVCenterComponents retrieveVCenterComponents;
    private NodeService nodeService;
    private DelegateExecution delegateExecution;
    private List<DiscoveredNode> discoveredNodesResponse;
    private DiscoveredNode discoveredNode;
    private BaseWorkflowDelegate baseWorkflowDelegate;

    @Before
    public void setUp() throws Exception
    {
        nodeService = mock(NodeService.class);
        retrieveVCenterComponents = new RetrieveVCenterComponents(nodeService);
        delegateExecution = mock(DelegateExecution.class);
        discoveredNodesResponse = nodeService.listDiscoveredNodes();
        baseWorkflowDelegate = mock(BaseWorkflowDelegate.class);
    }

    @Ignore @Test
    public void testException() throws Exception
    {
        try {
            given(nodeService.requestVCenterComponents()).willThrow(new NullPointerException());
            retrieveVCenterComponents.delegateExecute(delegateExecution);
        } catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.RETRIEVE_VCENTER_COMPONENTS_FAILED));
            assertTrue(error.getMessage().contains("An Unexpected Exception occurred while retrieving VCenter Components."));
        }
    }

    @Ignore @Test
    public void testFailed() throws Exception
    {
        try {
            nodeService = null;
            retrieveVCenterComponents.delegateExecute(delegateExecution);
        } catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.RETRIEVE_VCENTER_COMPONENTS_FAILED));
            assertTrue(error.getMessage().contains("VCenter Components were not retrieved."));
        }
    }

    @Ignore @Test
    public void testSuccess() throws Exception
    {
        when(nodeService.requestVCenterComponents()).thenReturn(true);
        final RetrieveVCenterComponents c = spy(new RetrieveVCenterComponents(nodeService));
        c.delegateExecute(delegateExecution);
        verify(c).updateDelegateStatus("VCenter Components were retrieved successfully.");
    }
}

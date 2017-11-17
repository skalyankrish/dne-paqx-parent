
/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class NotifyNodeDiscoveryToUpdateStatusCompletedTest {

    private NotifyNodeDiscoveryToUpdateStatusCompleted notifyNodeDiscoveryToUpdateStatusCompleted;
    private NodeService nodeService;
    private DelegateExecution delegateExecution;
    private NodeDetail nodeDetail;

    @Before
    public void setUp() throws Exception
    {
        nodeService = mock(NodeService.class);
        delegateExecution = mock(DelegateExecution.class);
        notifyNodeDiscoveryToUpdateStatusCompleted = new NotifyNodeDiscoveryToUpdateStatusCompleted(nodeService);
        nodeDetail = new NodeDetail();
        nodeDetail.setId("abc");
        nodeDetail.setServiceTag("test.ServiceTag");
    }

    @Test
    public void testNodeDetailsNull() throws Exception
    {
        try {
            nodeDetail = null;
            when(delegateExecution.getVariable(DelegateConstants.NODE_DETAIL)).thenReturn(nodeDetail);
            notifyNodeDiscoveryToUpdateStatusCompleted.delegateExecute(delegateExecution);
            fail("Should be an exception.");
        } catch (BpmnError error) {
            assertTrue(error.getErrorCode().equals(DelegateConstants.NOTIFY_NODE_STATUS_COMPLETED_FAILED));
            assertTrue(error.getMessage().contains("Node Status was not updated to added."));
        }
    }

    @Test
    public void testSucessNodeStatusChange() throws Exception
    {
        final NotifyNodeDiscoveryToUpdateStatusCompleted nus = spy(new NotifyNodeDiscoveryToUpdateStatusCompleted(nodeService));
        when(delegateExecution.getVariable(DelegateConstants.NODE_DETAIL)).thenReturn(nodeDetail);
        when(nodeService.notifyNodeAllocationStatus(anyString(),anyString())).thenReturn(true);
        nus.delegateExecute(delegateExecution);
        verify(nus).updateDelegateStatus("Update Node Status on Node test.ServiceTag was successful.");
    }

    @Test
    public void testFailedNodeStatusChange() throws Exception
    {
        final NotifyNodeDiscoveryToUpdateStatusCompleted nuc = spy(new NotifyNodeDiscoveryToUpdateStatusCompleted(nodeService));
        when(delegateExecution.getVariable(DelegateConstants.NODE_DETAIL)).thenReturn(nodeDetail);
        when(nodeService.notifyNodeAllocationStatus(anyString(),anyString())).thenReturn(false);
        try {
            nuc.delegateExecute(delegateExecution);
        } catch (BpmnError ex) {
            assertTrue(ex.getErrorCode().equals(DelegateConstants.NOTIFY_NODE_STATUS_COMPLETED_FAILED));
            assertTrue(ex.getMessage().contains("Node Status was not updated to added."));
        }

    }
}

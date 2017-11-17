
/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
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

public class NotifyNodeDiscoveryToUpdateStatusFailedTest {

    private NotifyNodeDiscoveryToUpdateStatusFailed notifyNodeDiscoveryToUpdateStatusFailed;
    private NodeService nodeService;
    private DelegateExecution delegateExecution;
    private NodeDetail nodeDetail;

    @Before
    public void setUp() throws Exception
    {
        nodeService = mock(NodeService.class);
        delegateExecution = mock(DelegateExecution.class);
        notifyNodeDiscoveryToUpdateStatusFailed = new NotifyNodeDiscoveryToUpdateStatusFailed(nodeService);
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
            notifyNodeDiscoveryToUpdateStatusFailed.delegateExecute(delegateExecution);
        } catch (BpmnError error) {
            fail("Fail should not be an exception.");
        }
    }

    @Test
    public void testSucessNodeStatusChange() throws Exception
    {
        final NotifyNodeDiscoveryToUpdateStatusFailed nuf = spy(new NotifyNodeDiscoveryToUpdateStatusFailed(nodeService));
        when(delegateExecution.getVariable(DelegateConstants.NODE_DETAIL)).thenReturn(nodeDetail);
        when(nodeService.notifyNodeAllocationStatus(anyString(),anyString())).thenReturn(true);
        nuf.delegateExecute(delegateExecution);
        verify(nuf).updateDelegateStatus("Update Node Status on Node test.ServiceTag was successful.");
    }

    @Test
    public void testFailedNodeStatusChange() throws Exception
    {
        final NotifyNodeDiscoveryToUpdateStatusFailed nuf = spy(new NotifyNodeDiscoveryToUpdateStatusFailed(nodeService));
        when(delegateExecution.getVariable(DelegateConstants.NODE_DETAIL)).thenReturn(nodeDetail);
        when(nodeService.notifyNodeAllocationStatus(anyString(),anyString())).thenReturn(false);
        nuf.delegateExecute(delegateExecution);
        verify(nuf).updateDelegateStatus("Node Status was not updated to provisioning failed.");
    }
}


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

public class RetrieveScaleIoComponetnsTest {

    private RetrieveScaleIoComponents retrieveScaleIoComponents;
    private NodeService nodeService;
    private DelegateExecution delegateExecution;
    private List<DiscoveredNode> discoveredNodesResponse;
    private DiscoveredNode discoveredNode;

    @Before
    public void setUp() throws Exception
    {
        nodeService = mock(NodeService.class);
        retrieveScaleIoComponents = new RetrieveScaleIoComponents(nodeService);
        delegateExecution = mock(DelegateExecution.class);
        discoveredNodesResponse = nodeService.listDiscoveredNodes();
    }

    @Ignore @Test
    public void testFailed() throws Exception
    {
        try {
            nodeService = null;
            retrieveScaleIoComponents.delegateExecute(delegateExecution);
        } catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.RETRIEVE_SCALE_IO_COMPONENTS_FAILED));
            assertTrue(error.getMessage().contains("Scale IO Components were not retrieved."));
        }
    }

    @Ignore @Test
    public void testException() throws Exception
    {
        try {
            given(nodeService.requestScaleIoComponents()).willThrow(new NullPointerException());
            retrieveScaleIoComponents.delegateExecute(delegateExecution);
        } catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.RETRIEVE_SCALE_IO_COMPONENTS_FAILED));
            assertTrue(error.getMessage().contains("An Unexpected Exception occurred while retrieving Scale IO Components."));
        }
    }

    @Ignore @Test
    public void testSuccess() throws Exception
    {
        when(nodeService.requestScaleIoComponents()).thenReturn(true);
        final RetrieveScaleIoComponents c = spy(new RetrieveScaleIoComponents(nodeService));
        c.delegateExecute(delegateExecution);
        verify(c).updateDelegateStatus("Scale IO Components were retrieved successfully.");
    }
}

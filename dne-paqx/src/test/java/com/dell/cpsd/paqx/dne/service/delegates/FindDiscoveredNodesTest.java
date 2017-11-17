
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FindDiscoveredNodesTest {

    private FindDiscoveredNodes findDiscoveredNodes;
    private NodeService nodeService;
    private DelegateExecution delegateExecution;
    private List<DiscoveredNode> discoveredNodesResponse;
    private DiscoveredNode discoveredNode;

    @Before
    public void setUp() throws Exception
    {
        nodeService = mock(NodeService.class);
        findDiscoveredNodes = new FindDiscoveredNodes(nodeService);
        delegateExecution = mock(DelegateExecution.class);
        discoveredNodesResponse = nodeService.listDiscoveredNodes();
    }

    @Ignore @Test
    public void nodeDiscoveryException() throws Exception
    {
        try {
            findDiscoveredNodes.delegateExecute(delegateExecution);
        } catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.NO_DISCOVERED_NODES));
            assertTrue(error.getMessage().contains("An Unexpected exception occurred trying to retrieve the list of Discovered Nodes."));
        }
    }

    @Ignore @Test
    public void noNodesDiscovered() throws Exception {
        discoveredNodesResponse.add(discoveredNode);
        when(nodeService.listDiscoveredNodes()).thenReturn(discoveredNodesResponse);
        try {
            findDiscoveredNodes.delegateExecute(delegateExecution);
        } catch (BpmnError error) {
            assertTrue(error.getErrorCode().equals(DelegateConstants.NO_DISCOVERED_NODES));
            assertTrue(error.getMessage().contains("There are no nodes currently discovered in Rack HD"));
        }
    }

    @Ignore @Test
    public void nodeDiscovered() throws Exception
    {
        discoveredNode = new DiscoveredNode("1", com.dell.cpsd.DiscoveredNode.AllocationStatus.DISCOVERED, "testserial", "testproduct",
                "test_vendor");
        discoveredNodesResponse.add(discoveredNode);
        when(nodeService.listDiscoveredNodes()).thenReturn(discoveredNodesResponse);
        findDiscoveredNodes.delegateExecute(delegateExecution);
        verify(delegateExecution, times(1)).setVariable(any(), any());
    }
}

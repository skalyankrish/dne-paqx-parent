
/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants;
import com.dell.cpsd.paqx.dne.service.model.DiscoveredNode;
import com.dell.cpsd.paqx.dne.service.model.IdracInfo;
import com.dell.cpsd.paqx.dne.service.model.IdracNetworkSettingsRequest;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ConfigIdracIpAddressTest {

    private ConfigIdracIpAddress configIdracIpAddress;
    private NodeService nodeService;
    private DelegateExecution delegateExecution;
    private List<DiscoveredNode> discoveredNodesResponse;
    private DiscoveredNode discoveredNode;
    private NodeDetail nodeDetail;
    private IdracNetworkSettingsRequest idracNetworkSettingsRequest;
    private IdracInfo idracInfo;
    private BaseWorkflowDelegate baseWorkflowDelegate;

    @Before
    public void setUp() throws Exception
    {
        nodeService = mock(NodeService.class);
        configIdracIpAddress = new ConfigIdracIpAddress(nodeService);
        delegateExecution = mock(DelegateExecution.class);
        nodeDetail = new NodeDetail();
        idracNetworkSettingsRequest = mock(IdracNetworkSettingsRequest.class);
        idracInfo = new IdracInfo();
        baseWorkflowDelegate = mock(BaseWorkflowDelegate.class);
        nodeDetail.setId("1");
        nodeDetail.setIdracIpAddress("1");
        nodeDetail.setIdracGatewayIpAddress("1");
        nodeDetail.setIdracSubnetMask("1");
        nodeDetail.setServiceTag("abc");
    }

    @Ignore @Test
    public void testFailConfig() throws Exception {
        try {
            idracInfo.setMessage("FAIL");
            when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
            when(nodeService.idracNetworkSettings(any())).thenReturn(idracInfo);
            configIdracIpAddress.delegateExecute(delegateExecution);
        } catch (BpmnError error) {
            assertTrue(error.getErrorCode().equals(DelegateConstants.CONFIGURE_IP_ADDRESS_FAILED));
            assertTrue(error.getMessage().contains("Configure IP Address on Node abc failed!"));
        }
    }

    @Ignore @Test
    public void testSuccess() throws Exception {
        idracInfo.setMessage("SUCCESS");
        when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
        when(nodeService.idracNetworkSettings(any())).thenReturn(idracInfo);
        final ConfigIdracIpAddress c = spy(new ConfigIdracIpAddress(nodeService));
        c.delegateExecute(delegateExecution);
        verify(c).updateDelegateStatus("Configure IP Address on Node abc was successful.");
    }

    @Ignore @Test
    public void testException() throws Exception {
        try {
            idracInfo.setMessage("FAIL");
            when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
            given(nodeService.idracNetworkSettings(any())).willThrow(new NullPointerException());
            configIdracIpAddress.delegateExecute(delegateExecution);
        } catch (BpmnError error) {
            assertTrue(error.getErrorCode().equals(DelegateConstants.CONFIGURE_IP_ADDRESS_FAILED));
            assertTrue(error.getMessage().contains("Configure IP Address on Node abc failed!"));
        }
    }
}

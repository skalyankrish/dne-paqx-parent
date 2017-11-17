
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
import com.dell.cpsd.paqx.dne.service.model.IdracNetworkSettingsRequest;
import com.dell.cpsd.paqx.dne.service.model.ObmSettingsResponse;
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

public class ConfigureObmSettingsTest {

    private ConfigureObmSettings configureObmSettings;
    private NodeService nodeService;
    private String[] obmServices = new String[2];
    private DelegateExecution delegateExecution;
    private List<DiscoveredNode> discoveredNodesResponse;
    private DiscoveredNode discoveredNode;
    private NodeDetail nodeDetail;
    private IdracNetworkSettingsRequest idracNetworkSettingsRequest;
    private ObmSettingsResponse obmSettingsResponse;

    @Before
    public void setUp() throws Exception
    {
        nodeService = mock(NodeService.class);
        obmServices[0] = "abc";
        obmServices[1] = "xyz";
        configureObmSettings = new ConfigureObmSettings(nodeService);
        delegateExecution = mock(DelegateExecution.class);
        nodeDetail = new NodeDetail();
        idracNetworkSettingsRequest = mock(IdracNetworkSettingsRequest.class);
        obmSettingsResponse = new ObmSettingsResponse();
        nodeDetail.setId("1");
        nodeDetail.setIdracIpAddress("1");
        nodeDetail.setIdracGatewayIpAddress("1");
        nodeDetail.setIdracSubnetMask("1");
        nodeDetail.setServiceTag("abc");
    }

    @Ignore @Test
    public void testException() throws Exception {
        try {
            when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
            given(nodeService.obmSettingsResponse(any())).willThrow(new NullPointerException());
            configureObmSettings.delegateExecute(delegateExecution);
        } catch (BpmnError error) {
            assertTrue(error.getErrorCode().equals(DelegateConstants.CONFIGURE_OBM_SETTINGS_FAILED));
            assertTrue(error.getMessage().contains("An Unexpected Exception occurred while attempting to Configure the Obm Settings on Node abc"));
        }
    }

    @Ignore @Test
    public void testSuccess() throws Exception {
        obmSettingsResponse.setStatus("SUCCESS");
        when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
        when(nodeService.obmSettingsResponse(any())).thenReturn(obmSettingsResponse);
        final ConfigureObmSettings c = spy(new ConfigureObmSettings(nodeService));
        c.delegateExecute(delegateExecution);
        verify(c).updateDelegateStatus("Obm Settings on Node abc were configured successfully.");
    }

    @Ignore @Test
    public void testFailure() throws Exception {
        try {
            obmSettingsResponse.setStatus("FAIL");
            when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
            when(nodeService.obmSettingsResponse(any())).thenReturn(obmSettingsResponse);
            configureObmSettings.delegateExecute(delegateExecution);
        } catch (BpmnError error) {
            assertTrue(error.getErrorCode().equals(DelegateConstants.CONFIGURE_OBM_SETTINGS_FAILED));
            assertTrue(error.getMessage().equalsIgnoreCase("Obm Settings on Node abc were not configured."));
        }
    }
}

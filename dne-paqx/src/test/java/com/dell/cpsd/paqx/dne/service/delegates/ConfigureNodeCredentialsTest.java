
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
import com.dell.cpsd.paqx.dne.service.model.ChangeIdracCredentialsResponse;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.HOSTNAME;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class ConfigureNodeCredentialsTest {

    private ConfigureNodeCredentials configureNodeCredentials;
    private NodeService nodeService;
    private DelegateExecution delegateExecution;
    private NodeDetail nodeDetail;
    private ChangeIdracCredentialsResponse responseMessage;

    @Before
    public void setUp() throws Exception
    {
        nodeService = mock(NodeService.class);
        configureNodeCredentials = new ConfigureNodeCredentials(nodeService);
        delegateExecution = mock(DelegateExecution.class);
        nodeDetail = new NodeDetail();
        nodeDetail.setServiceTag("abc");
        nodeDetail.setEsxiManagementIpAddress("abc");
        responseMessage = new ChangeIdracCredentialsResponse();
    }

    @Ignore @Test
    public void testExceptionThrown() throws Exception
    {
        try {
            when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
            given(nodeService.changeIdracCredentials(any())).willThrow(new NullPointerException());
            configureNodeCredentials.delegateExecute(delegateExecution);
        } catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.INSTALL_ESXI_FAILED));
            assertTrue(error.getMessage().contains("Configure Node Credentials on Node abc failed!"));
        }
    }

    @Ignore @Test
    public void testFailed() throws Exception
    {
        try {
            when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
            when(nodeService.changeIdracCredentials(any())).thenReturn(responseMessage);
            responseMessage.setMessage("FAILED");
            configureNodeCredentials.delegateExecute(delegateExecution);
        } catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.INSTALL_ESXI_FAILED));
            assertTrue(error.getMessage().contains("Configure Node Credentials on Node abc failed!"));
        }
    }

    @Ignore @Test
    public void testSuccess() throws Exception
    {
        when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
        when(nodeService.changeIdracCredentials(any())).thenReturn(responseMessage);
        responseMessage.setMessage("SUCCESS");
        final ConfigureNodeCredentials c = spy(new ConfigureNodeCredentials(nodeService));
        c.delegateExecute(delegateExecution);
        verify(c).updateDelegateStatus("Configure Node Credentials on Node abc was successful.");
    }
}

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
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ChangeScaleIOVMCredentialsTest
{
    private static final String COMPONENT_TYPE      = "COMMON-SERVER";
    private static final String ENDPOINT_TYPE       = "COMMON-SVM";
    private static final String FACTORY_CREDENTIALS = "SVM-FACTORY";
    private static final String COMMON_CREDENTIALS  = "SVM-COMMON";

    private ChangeScaleIOVMCredentials changeScaleIOVMCredentials;
    private NodeService                nodeService;
    private DelegateExecution          delegateExecution;
    private DataServiceRepository      repository;
    private ComponentEndpointIds       componentEndpointIds1;
    private ComponentEndpointIds       componentEndpointIds2;
    private NodeDetail                 nodeDetail;

    @Before
    public void setUp() throws Exception
    {
        nodeService = mock(NodeService.class);
        repository = mock(DataServiceRepository.class);
        changeScaleIOVMCredentials = new ChangeScaleIOVMCredentials(nodeService, repository);
        delegateExecution = mock(DelegateExecution.class);
        componentEndpointIds1 = new ComponentEndpointIds("abc", "abc", "abc", "FACTORY");
        componentEndpointIds2 = new ComponentEndpointIds("abc", "abc", "abc", "COMMON");
        nodeDetail = new NodeDetail();
        nodeDetail.setServiceTag("abc");
        nodeDetail.setScaleIoSvmManagementIpAddress("abc");
    }

    @Test
    public void testExceptionThrown1() throws Exception
    {
        try
        {
            when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
            given(repository.getComponentEndpointIds(COMPONENT_TYPE, ENDPOINT_TYPE, FACTORY_CREDENTIALS))
                    .willThrow(new NullPointerException());
            changeScaleIOVMCredentials.delegateExecute(delegateExecution);
        }
        catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.CHANGE_SCALEIO_VM_CREDENTIALS));
            assertTrue(error.getMessage().contains("No factory component ids found. Reason:"));
        }
    }

    @Test
    public void testExceptionThrown2() throws Exception
    {
        try
        {
            when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
            when(repository.getComponentEndpointIds(COMPONENT_TYPE, ENDPOINT_TYPE, FACTORY_CREDENTIALS)).thenReturn(componentEndpointIds1);
            given(repository.getComponentEndpointIds(COMPONENT_TYPE, ENDPOINT_TYPE, COMMON_CREDENTIALS))
                    .willThrow(new NullPointerException());
            changeScaleIOVMCredentials.delegateExecute(delegateExecution);
        }
        catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.CHANGE_SCALEIO_VM_CREDENTIALS));
            assertTrue(error.getMessage().contains("No common component ids found. Reason:"));
        }
    }

    @Test
    public void testExecutionFailed() throws Exception
    {
        try
        {
            when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
            when(repository.getComponentEndpointIds(COMPONENT_TYPE, ENDPOINT_TYPE, FACTORY_CREDENTIALS)).thenReturn(componentEndpointIds1);
            when(repository.getComponentEndpointIds(COMPONENT_TYPE, ENDPOINT_TYPE, COMMON_CREDENTIALS)).thenReturn(componentEndpointIds2);
            when(nodeService.requestRemoteCommandExecution(any())).thenReturn(false);
            changeScaleIOVMCredentials.delegateExecute(delegateExecution);
        }
        catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.CHANGE_SCALEIO_VM_CREDENTIALS));
            assertTrue(error.getMessage().contains("Change ScaleIO vm credentials request failed"));
        }
    }

    @Test
    public void testSuccess() throws Exception
    {
        when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
        when(repository.getComponentEndpointIds(COMPONENT_TYPE, ENDPOINT_TYPE, FACTORY_CREDENTIALS)).thenReturn(componentEndpointIds1);
        when(repository.getComponentEndpointIds(COMPONENT_TYPE, ENDPOINT_TYPE, COMMON_CREDENTIALS)).thenReturn(componentEndpointIds2);
        when(nodeService.requestRemoteCommandExecution(any())).thenReturn(true);
        changeScaleIOVMCredentials.delegateExecute(delegateExecution);
        final ChangeScaleIOVMCredentials changeScaleIOVMCredentialsSpy = spy(changeScaleIOVMCredentials);
        changeScaleIOVMCredentialsSpy.delegateExecute(delegateExecution);
        verify(changeScaleIOVMCredentialsSpy).updateDelegateStatus("Configure VM Network Settings on Node abc was successful.");
    }
}

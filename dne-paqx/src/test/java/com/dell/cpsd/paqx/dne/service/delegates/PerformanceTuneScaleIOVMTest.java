/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
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
public class PerformanceTuneScaleIOVMTest
{
    private PerformanceTuneScaleIOVM performanceTuneScaleIOVM;
    private NodeService              nodeService;
    private DelegateExecution        delegateExecution;
    private DataServiceRepository    repository;
    private ComponentEndpointIds     componentEndpointIds;
    private NodeDetail               nodeDetail;

    @Before
    public void setUp() throws Exception
    {
        nodeService = mock(NodeService.class);
        repository = mock(DataServiceRepository.class);
        performanceTuneScaleIOVM = new PerformanceTuneScaleIOVM(nodeService, repository);
        delegateExecution = mock(DelegateExecution.class);
        componentEndpointIds = new ComponentEndpointIds("abc", "abc", "abc", "abc");
        nodeDetail = new NodeDetail();
        nodeDetail.setServiceTag("abc");
        nodeDetail.setEsxiManagementIpAddress("abc");
    }

    @Test
    public void testExceptionThrown1() throws Exception
    {
        try
        {
            when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
            given(repository.getComponentEndpointIds(any(), any(), any())).willThrow(new NullPointerException());
            performanceTuneScaleIOVM.delegateExecute(delegateExecution);
        }
        catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.PERFORMANCE_TUNE_SCALEIO_VM));
            assertTrue(error.getMessage()
                    .contains("An Unexpected Exception occurred attempting to retrieve Common Credentials Component Endpoints. Reason:"));
        }
    }

    @Test
    public void testExceptionThrown2() throws Exception
    {
        try
        {
            when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
            when(repository.getComponentEndpointIds(any(), any(), any())).thenReturn(componentEndpointIds);
            given(nodeService.requestRemoteCommandExecution(any())).willThrow(new NullPointerException());
            performanceTuneScaleIOVM.delegateExecute(delegateExecution);
        }
        catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.PERFORMANCE_TUNE_SCALEIO_VM));
            assertTrue(error.getMessage().contains("An Unexpected Exception occurred attempting to request"));
        }
    }

    @Test
    public void testExecutionFailed()
    {
        try
        {
            when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
            when(repository.getComponentEndpointIds(any(), any(), any())).thenReturn(componentEndpointIds);
            when(nodeService.requestRemoteCommandExecution(any())).thenReturn(false);
            performanceTuneScaleIOVM.delegateExecute(delegateExecution);
        }
        catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.PERFORMANCE_TUNE_SCALEIO_VM));
            assertTrue(error.getMessage().contains("performance tune ScaleIO vm request failed"));
        }
    }

    @Test
    public void testSuccess()
    {
        when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
        when(repository.getComponentEndpointIds(any(), any(), any())).thenReturn(componentEndpointIds);
        when(nodeService.requestRemoteCommandExecution(any())).thenReturn(true);
        final PerformanceTuneScaleIOVM performanceTuneScaleIOVMSpy = spy(performanceTuneScaleIOVM);
        performanceTuneScaleIOVMSpy.delegateExecute(delegateExecution);
        verify(performanceTuneScaleIOVMSpy).updateDelegateStatus("Performance Tune Scale IO VM on Node abc was successful.");
    }
}

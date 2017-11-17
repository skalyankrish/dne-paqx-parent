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

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.HOSTNAME;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InstallScaleIoVibTest
{
    private InstallScaleIoVib     installScaleIoVib;
    private NodeService           nodeService;
    private DelegateExecution     delegateExecution;
    private DataServiceRepository repository;
    private ComponentEndpointIds  componentEndpointIds;
    private NodeDetail            nodeDetail;

    @Before
    public void setUp() throws Exception
    {
        nodeService = mock(NodeService.class);
        repository = mock(DataServiceRepository.class);
        installScaleIoVib = new InstallScaleIoVib(nodeService, repository);
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
            when(delegateExecution.getVariable(HOSTNAME)).thenReturn("abc");
            when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
            given(repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER")).willThrow(new NullPointerException());
            installScaleIoVib.delegateExecute(delegateExecution);
        }
        catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.INSTALL_SCALEIO_VIB_FAILED));
            assertTrue(error.getMessage().contains("An Unexpected Exception occurred attempting to retrieve VCenter Component Endpoints."));
        }
    }

    @Test
    public void testExceptionThrown2() throws Exception
    {
        try
        {
            when(delegateExecution.getVariable(HOSTNAME)).thenReturn("abc");
            when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
            when(repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER")).thenReturn(componentEndpointIds);
            given(nodeService.requestInstallSoftwareVib(any())).willThrow(new NullPointerException());
            installScaleIoVib.delegateExecute(delegateExecution);
        }
        catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.INSTALL_SCALEIO_VIB_FAILED));
            assertTrue(error.getMessage().contains("An Unexpected Exception occurred attempting to request Install ScaleIO Vib.  Reason:"));
        }
    }

    @Test
    public void testExecutionFailed()
    {
        try
        {
            when(delegateExecution.getVariable(HOSTNAME)).thenReturn("abc");
            when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
            when(repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER")).thenReturn(componentEndpointIds);
            when(nodeService.requestInstallSoftwareVib(any())).thenReturn(false);
            installScaleIoVib.delegateExecute(delegateExecution);
        }
        catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.INSTALL_SCALEIO_VIB_FAILED));
            assertTrue(error.getMessage().contains("on Node " + nodeDetail.getServiceTag() + " failed"));
        }
    }

    @Test
    public void testSuccess()
    {
        when(delegateExecution.getVariable(HOSTNAME)).thenReturn("abc");
        when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
        when(repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER")).thenReturn(componentEndpointIds);
        when(nodeService.requestInstallSoftwareVib(any())).thenReturn(true);
        final InstallScaleIoVib installScaleIoVibSpy = spy(installScaleIoVib);
        installScaleIoVibSpy.delegateExecute(delegateExecution);
        verify(installScaleIoVibSpy).updateDelegateStatus("Install ScaleIO Vib on Node abc was successful.");
    }
}

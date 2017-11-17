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
public class InstallScaleIOVMPackagesTest
{
    private InstallScaleIOVMPackages installScaleIOVMPackages;
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
        installScaleIOVMPackages = new InstallScaleIOVMPackages(nodeService, repository);
        delegateExecution = mock(DelegateExecution.class);
        componentEndpointIds = new ComponentEndpointIds("abc", "abc", "abc", "abc");
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
            given(repository.getComponentEndpointIds(any(), any(), any())).willThrow(new NullPointerException());
            installScaleIOVMPackages.delegateExecute(delegateExecution);
        }
        catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.INSTALL_SCALEIO_VM_PACKAGES));
            assertTrue(error.getMessage()
                    .contains("An Unexpected Exception occurred attempting to retrieve VCenter Component Endpoints. Reason:"));
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
            installScaleIOVMPackages.delegateExecute(delegateExecution);
        }
        catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.INSTALL_SCALEIO_VM_PACKAGES));
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
            installScaleIOVMPackages.delegateExecute(delegateExecution);
        }
        catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.INSTALL_SCALEIO_VM_PACKAGES));
            assertTrue(error.getMessage().contains("install ScaleIO packages request failed"));
        }
    }

    @Test
    public void testSuccess()
    {
        when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
        when(repository.getComponentEndpointIds(any(), any(), any())).thenReturn(componentEndpointIds);
        when(nodeService.requestRemoteCommandExecution(any())).thenReturn(true);
        installScaleIOVMPackages.delegateExecute(delegateExecution);
        final InstallScaleIOVMPackages installScaleIOVMPackagesSpy = spy(installScaleIOVMPackages);
        installScaleIOVMPackagesSpy.delegateExecute(delegateExecution);
        verify(installScaleIOVMPackagesSpy).updateDelegateStatus("Install Scale IO VM Packages on Node abc was successful.");
    }
}

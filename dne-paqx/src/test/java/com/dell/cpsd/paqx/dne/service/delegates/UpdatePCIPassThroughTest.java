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

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.DEPLOY_SCALEIO_NEW_VM_NAME;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.HOSTNAME;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.HOST_PCI_DEVICE_ID;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UpdatePCIPassThroughTest
{
    private UpdatePCIPassThrough  updatePCIPassThrough;
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
        updatePCIPassThrough = new UpdatePCIPassThrough(nodeService, repository);
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
            when(repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER")).thenReturn(componentEndpointIds);
            when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
            when(delegateExecution.getVariable(HOST_PCI_DEVICE_ID)).thenReturn("pci-id");
            when(delegateExecution.getVariable(DEPLOY_SCALEIO_NEW_VM_NAME)).thenReturn("new-vm-name");
            when(delegateExecution.getVariable(HOSTNAME)).thenReturn("hostabc");
            given(nodeService.requestSetPciPassThrough(any())).willThrow(new NullPointerException());
            updatePCIPassThrough.delegateExecute(delegateExecution);
        }
        catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.UPDATE_PCI_PASSTHROUGH));
            assertTrue(error.getMessage().contains("An Unexpected Exception occurred attempting to request"));
        }
    }

    @Test
    public void testExecutionFailed()
    {
        try
        {
            when(repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER")).thenReturn(componentEndpointIds);
            when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
            when(delegateExecution.getVariable(HOST_PCI_DEVICE_ID)).thenReturn("pci-id");
            when(delegateExecution.getVariable(DEPLOY_SCALEIO_NEW_VM_NAME)).thenReturn("new-vm-name");
            when(delegateExecution.getVariable(HOSTNAME)).thenReturn("hostabc");
            when(nodeService.requestSetPciPassThrough(any())).thenReturn(false);
            updatePCIPassThrough.delegateExecute(delegateExecution);
        }
        catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.UPDATE_PCI_PASSTHROUGH));
            assertTrue(error.getMessage().contains("Configure PCI PassThrough Failed!"));
        }
    }

    @Test
    public void testSuccess()
    {
        when(repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER")).thenReturn(componentEndpointIds);
        when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
        when(delegateExecution.getVariable(HOST_PCI_DEVICE_ID)).thenReturn("pci-id");
        when(delegateExecution.getVariable(DEPLOY_SCALEIO_NEW_VM_NAME)).thenReturn("new-vm-name");
        when(delegateExecution.getVariable(HOSTNAME)).thenReturn("hostabc");
        when(nodeService.requestSetPciPassThrough(any())).thenReturn(true);
        final UpdatePCIPassThrough updatePCIPassThroughSpy = spy(updatePCIPassThrough);
        updatePCIPassThroughSpy.delegateExecute(delegateExecution);
        verify(updatePCIPassThroughSpy).updateDelegateStatus("Set PCI Pass Through on Node abc was successful.");
    }
}

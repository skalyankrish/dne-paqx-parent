
/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.domain.vcenter.Host;
import com.dell.cpsd.paqx.dne.domain.vcenter.PciDevice;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.HOSTNAME;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class EnablePCIPassThroughTest {

    private EnablePCIPassThrough enablePCIPassThrough;
    private NodeService nodeService;
    private DataServiceRepository repository;
    private DelegateExecution delegateExecution;
    private NodeDetail nodeDetail;
    private ComponentEndpointIds componentEndpointIds;
    List<PciDevice> pciDeviceList;
    Host host = new Host("abc", "abc", "abc", "abc", "abc", true);
    PciDevice pciDevice = new PciDevice("abc", "abc", "abc", "abc", "abc", "abc", host);

    @Before
    public void setUp() throws Exception {
        nodeService = mock(NodeService.class);
        repository = mock(DataServiceRepository.class);
        enablePCIPassThrough = new EnablePCIPassThrough(nodeService, repository);
        delegateExecution = mock(DelegateExecution.class);
        nodeDetail = new NodeDetail();
        nodeDetail.setServiceTag("abc");
        nodeDetail.setEsxiManagementIpAddress("abc");
        componentEndpointIds = new ComponentEndpointIds("abc", "abc", "abc", "abc");
        pciDeviceList = new ArrayList<>();
        pciDeviceList.add(pciDevice);
    }

    @Ignore @Test
    public void testExceptionThrown1() throws Exception {
        try {
            when(delegateExecution.getVariable(HOSTNAME)).thenReturn("abc");
            when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
            given(repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER")).willThrow(new NullPointerException());
            enablePCIPassThrough.delegateExecute(delegateExecution);
        } catch (BpmnError error) {
            assertTrue(error.getErrorCode().equals(DelegateConstants.ENABLE_PCI_PASSTHROUGH_FAILED));
            assertTrue(error.getMessage().contains("An Unexpected Exception occurred attempting to retrieve VCenter Component Endpoints."));
        }
    }

    @Ignore @Test
    public void testExceptionThrown2() throws Exception {
        try {
            when(delegateExecution.getVariable(HOSTNAME)).thenReturn("abc");
            when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
            when(repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER")).thenReturn(componentEndpointIds);
            given(repository.getPciDeviceList()).willThrow(new NullPointerException());
            enablePCIPassThrough.delegateExecute(delegateExecution);
        } catch(BpmnError error) {
            assertTrue(error.getErrorCode().equals(DelegateConstants.ENABLE_PCI_PASSTHROUGH_FAILED));
            assertTrue(error.getMessage().contains("An Unexpected Exception occurred attempting to retrieve PCI Device List."));
        }
    }

    @Ignore @Test
    public void testExceptionThrown3() throws Exception {
        try {
            when(delegateExecution.getVariable(HOSTNAME)).thenReturn("abc");
            when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
            when(repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER")).thenReturn(componentEndpointIds);
            pciDeviceList.clear();
            when(repository.getPciDeviceList()).thenReturn(pciDeviceList);
            enablePCIPassThrough.delegateExecute(delegateExecution);
        } catch(BpmnError error) {
            assertTrue(error.getErrorCode().equals(DelegateConstants.ENABLE_PCI_PASSTHROUGH_FAILED));
            assertTrue(error.getMessage().contains("An Unexpected Exception occurred attempting to inventory VCenter."));
        }
    }

    @Ignore @Test
    public void testExceptionThrown4() throws Exception
    {
        try {
            when(delegateExecution.getVariable(HOSTNAME)).thenReturn("abc");
            when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
            when(repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER")).thenReturn(componentEndpointIds);
            when(repository.getPciDeviceList()).thenReturn(pciDeviceList);
            given(nodeService.requestEnablePciPassThrough(any())).willThrow(new NullPointerException());
            enablePCIPassThrough.delegateExecute(delegateExecution);
        } catch(BpmnError error) {
            assertTrue(error.getErrorCode().equals(DelegateConstants.ENABLE_PCI_PASSTHROUGH_FAILED));
            assertTrue(error.getMessage().contains("An Unexpected Exception occurred attempting to request Enable PCI Pass Through."));
        }
    }

    @Ignore @Test
    public void testFailed() throws Exception
    {
        try {
            when(delegateExecution.getVariable(HOSTNAME)).thenReturn("abc");
            when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
            when(repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER")).thenReturn(componentEndpointIds);
            when(repository.getPciDeviceList()).thenReturn(pciDeviceList);
            when(nodeService.requestEnablePciPassThrough(any())).thenReturn(false);
            enablePCIPassThrough.delegateExecute(delegateExecution);
        } catch(BpmnError error) {
            assertTrue(error.getErrorCode().equals(DelegateConstants.INSTALL_ESXI_FAILED));
            assertTrue(error.getMessage().contains("Install Esxi on Node abc failed!"));
        }
    }

    @Ignore @Test
    public void testSuccess() throws Exception
    {
        when(delegateExecution.getVariable(HOSTNAME)).thenReturn("abc");
        when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
        when(repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER")).thenReturn(componentEndpointIds);
        when(repository.getPciDeviceList()).thenReturn(pciDeviceList);
        when(nodeService.requestEnablePciPassThrough(any())).thenReturn(true);
        final EnablePCIPassThrough c = spy(new EnablePCIPassThrough(nodeService, repository));
        c.delegateExecute(delegateExecution);
        verify(c).updateDelegateStatus("Request PCI Pass Through on Node abc was successful.");
    }
}

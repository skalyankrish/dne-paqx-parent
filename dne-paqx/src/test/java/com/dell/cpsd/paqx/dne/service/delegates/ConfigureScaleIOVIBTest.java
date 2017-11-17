
/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOData;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.delegates.model.ESXiCredentialDetails;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.Test;


import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.ESXI_CREDENTIAL_DETAILS;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.HOSTNAME;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class ConfigureScaleIOVIBTest {

    private ConfigureScaleIOVIB configureScaleIOVIB;
    private NodeService nodeService;
    private DataServiceRepository repository;
    private DelegateExecution delegateExecution;
    private NodeDetail nodeDetail;
    private ComponentEndpointIds componentEndpointIds;
    private ESXiCredentialDetails esxiCredentialDetails;
    private ScaleIOData scaleIOData;

    @Before
    public void setUp() throws Exception {
        nodeService = mock(NodeService.class);
        repository = mock(DataServiceRepository.class);
        configureScaleIOVIB = new ConfigureScaleIOVIB(nodeService, repository);
        delegateExecution = mock(DelegateExecution.class);
        nodeDetail = new NodeDetail();
        nodeDetail.setServiceTag("abc");
        nodeDetail.setEsxiManagementIpAddress("abc");
        componentEndpointIds = new ComponentEndpointIds("abc", "abc", "abc", "abc");
        esxiCredentialDetails = new ESXiCredentialDetails();
        esxiCredentialDetails.setComponentUuid("abc");
        esxiCredentialDetails.setCredentialUuid("abc");
        esxiCredentialDetails.setEndpointUuid("abc");
        scaleIOData = new ScaleIOData("abc", "abc", "abc", "abc", "abc", "abc", "abc");
    }

    @Test
    public void testExceptionThrown1() throws Exception {
        try {
            when(delegateExecution.getVariable(HOSTNAME)).thenReturn("abc");
            when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
            when(delegateExecution.getVariable(ESXI_CREDENTIAL_DETAILS)).thenReturn(esxiCredentialDetails);
            given(repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER")).willThrow(new NullPointerException());
            configureScaleIOVIB.delegateExecute(delegateExecution);
        } catch (BpmnError error) {
            assertTrue(error.getErrorCode().equals(DelegateConstants.CONFIGURE_SCALEIO_VIB_FAILED));
            assertTrue(error.getMessage().contains("An Unexpected Exception occurred attempting to retrieve VCenter Component Endpoints."));
        }
    }

    @Test
    public void testExceptionThrown2() throws Exception {
        try {
            when(delegateExecution.getVariable(HOSTNAME)).thenReturn("abc");
            when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
            when(repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER")).thenReturn(componentEndpointIds);
            given(nodeService.requestConfigureScaleIoVib(any())).willThrow(new NullPointerException());
            when(repository.getScaleIoData()).thenReturn(scaleIOData);
            configureScaleIOVIB.delegateExecute(delegateExecution);
        } catch(BpmnError error) {
            assertTrue(error.getErrorCode().equals(DelegateConstants.CONFIGURE_SCALEIO_VIB_FAILED));
            assertTrue(error.getMessage().contains("An Unexpected Exception occurred attempting to request Configure ScaleIO Vib"));
        }
    }

    @Test
    public void testFailed() throws Exception
    {
        try {
            when(delegateExecution.getVariable(HOSTNAME)).thenReturn("abc");
            when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
            when(repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER")).thenReturn(componentEndpointIds);
            when(nodeService.requestConfigureScaleIoVib(any())).thenReturn(false);
            when(delegateExecution.getVariable(ESXI_CREDENTIAL_DETAILS)).thenReturn(esxiCredentialDetails);
            when(repository.getScaleIoData()).thenReturn(scaleIOData);
            configureScaleIOVIB.delegateExecute(delegateExecution);
        } catch(BpmnError error) {
            assertTrue(error.getErrorCode().equals(DelegateConstants.CONFIGURE_SCALEIO_VIB_FAILED));
            assertTrue(error.getMessage().contains("Configure ScaleIO Vib on Node abc failed!"));
        }
    }

    @Test
    public void testSuccess() throws Exception
    {
        when(delegateExecution.getVariable(HOSTNAME)).thenReturn("abc");
        when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
        when(repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER")).thenReturn(componentEndpointIds);
        when(nodeService.requestConfigureScaleIoVib(any())).thenReturn(true);
        when(delegateExecution.getVariable(ESXI_CREDENTIAL_DETAILS)).thenReturn(esxiCredentialDetails);
        when(repository.getScaleIoData()).thenReturn(scaleIOData);
        final ConfigureScaleIOVIB c = spy(new ConfigureScaleIOVIB(nodeService, repository));
        c.delegateExecute(delegateExecution);
        verify(c).updateDelegateStatus("Configure ScaleIO Vib on Node abc was successful.");
    }
}

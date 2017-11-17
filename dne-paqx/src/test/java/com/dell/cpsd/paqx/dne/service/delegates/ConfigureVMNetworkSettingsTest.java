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
import org.apache.commons.collections.map.HashedMap;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.DEPLOY_SCALEIO_NEW_VM_NAME;
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
public class ConfigureVMNetworkSettingsTest
{
    private ConfigureVMNetworkSettings configureVMNetworkSettings;
    private NodeService                nodeService;
    private DelegateExecution          delegateExecution;
    private DataServiceRepository      repository;
    private ComponentEndpointIds       componentEndpointIds;
    private NodeDetail                 nodeDetail;
    private Map<String, String>        dvSwitchNames;
    private Map<String, String>        scaleIoNetworkNamesMap;

    @Before
    public void setUp() throws Exception
    {
        nodeService = mock(NodeService.class);
        repository = mock(DataServiceRepository.class);
        configureVMNetworkSettings = new ConfigureVMNetworkSettings(nodeService, repository);
        delegateExecution = mock(DelegateExecution.class);
        componentEndpointIds = new ComponentEndpointIds("abc", "abc", "abc", "abc");
        nodeDetail = new NodeDetail();
        nodeDetail.setServiceTag("abc");
        nodeDetail.setEsxiManagementIpAddress("abc");
        dvSwitchNames = new HashedMap();
        dvSwitchNames.put("switch1", "value1");
        scaleIoNetworkNamesMap = new HashedMap();
        scaleIoNetworkNamesMap.put("net1", "network1");
    }

    @Test
    public void testExceptionThrown1() throws Exception
    {
        try
        {
            when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
            when(delegateExecution.getVariable(HOSTNAME)).thenReturn("host1");
            when(delegateExecution.getVariable(DEPLOY_SCALEIO_NEW_VM_NAME)).thenReturn("vmName");
            given(repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER")).willThrow(new NullPointerException());
            configureVMNetworkSettings.delegateExecute(delegateExecution);
        }
        catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.CONFIGURE_VM_NETWORK_SETTINGS));
            assertTrue(error.getMessage()
                    .contains("An Unexpected Exception occurred attempting to retrieve VCenter Component Endpoints.  Reason:"));
        }
    }

    @Test
    public void testExceptionThrown2() throws Exception
    {
        try
        {
            when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
            when(delegateExecution.getVariable(HOSTNAME)).thenReturn("host1");
            when(delegateExecution.getVariable(DEPLOY_SCALEIO_NEW_VM_NAME)).thenReturn("vmName");
            when(repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER")).thenReturn(componentEndpointIds);
            given(repository.getDvSwitchNames()).willThrow(new NullPointerException());
            configureVMNetworkSettings.delegateExecute(delegateExecution);
        }
        catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.CONFIGURE_VM_NETWORK_SETTINGS));
            assertTrue(error.getMessage().contains("An Unexpected Exception occurred attempting to retrieve DV Switch names.  Reason:"));
        }
    }

    @Test
    public void testExceptionThrown3() throws Exception
    {
        try
        {
            when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
            when(delegateExecution.getVariable(HOSTNAME)).thenReturn("host1");
            when(delegateExecution.getVariable(DEPLOY_SCALEIO_NEW_VM_NAME)).thenReturn("vmName");
            when(repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER")).thenReturn(componentEndpointIds);
            when(repository.getDvSwitchNames()).thenReturn(dvSwitchNames);
            given(repository.getScaleIoNetworkNames(any())).willThrow(new NullPointerException());
            configureVMNetworkSettings.delegateExecute(delegateExecution);
        }
        catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.CONFIGURE_VM_NETWORK_SETTINGS));
            assertTrue(error.getMessage()
                    .contains("An Unexpected Exception occurred attempting to retrieve ScaleIO DVSwitch-Network Names Map.  Reason:"));
        }
    }

    @Test
    public void testExecutionFailed() throws Exception
    {
        try
        {
            when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
            when(delegateExecution.getVariable(HOSTNAME)).thenReturn("host1");
            when(delegateExecution.getVariable(DEPLOY_SCALEIO_NEW_VM_NAME)).thenReturn("vmName");
            when(repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER")).thenReturn(componentEndpointIds);
            when(repository.getDvSwitchNames()).thenReturn(dvSwitchNames);
            when(repository.getScaleIoNetworkNames(any())).thenReturn(scaleIoNetworkNamesMap);
            when(nodeService.requestConfigureVmNetworkSettings(any())).thenReturn(false);
            configureVMNetworkSettings.delegateExecute(delegateExecution);
        }
        catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.CONFIGURE_VM_NETWORK_SETTINGS));
            assertTrue(error.getMessage()
                    .contains("Configure VM network settings request failed"));
        }
    }

    @Test
    public void testSuccess()
    {
        when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
        when(delegateExecution.getVariable(HOSTNAME)).thenReturn("host1");
        when(delegateExecution.getVariable(DEPLOY_SCALEIO_NEW_VM_NAME)).thenReturn("vmName");
        when(repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER")).thenReturn(componentEndpointIds);
        when(repository.getDvSwitchNames()).thenReturn(dvSwitchNames);
        when(repository.getScaleIoNetworkNames(any())).thenReturn(scaleIoNetworkNamesMap);
        when(nodeService.requestConfigureVmNetworkSettings(any())).thenReturn(true);
        configureVMNetworkSettings.delegateExecute(delegateExecution);
        final ConfigureVMNetworkSettings configureVMNetworkSettingsSpy = spy(configureVMNetworkSettings);
        configureVMNetworkSettingsSpy.delegateExecute(delegateExecution);
        verify(configureVMNetworkSettingsSpy).updateDelegateStatus("Configure VM Network Settings on Node abc was successful.");
    }
}

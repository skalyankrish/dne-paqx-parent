
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
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.HOSTNAME;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class AddHostToDvSwitchTest {

    private AddHostToDvSwitch addHostToDvSwitch;
    private NodeService nodeService;
    private DataServiceRepository repository;
    private DelegateExecution delegateExecution;
    private NodeDetail nodeDetail;
    private ComponentEndpointIds componentEndpointIds;
    private Map<String, String> dvSwitchNames;
    private Map<String, String> dvPortGroupNames;

    @Before
    public void setUp() throws Exception
    {
        nodeService = mock(NodeService.class);
        repository = mock(DataServiceRepository.class);
        addHostToDvSwitch = new AddHostToDvSwitch(nodeService, repository);
        delegateExecution = mock(DelegateExecution.class);
        nodeDetail = new NodeDetail();
        nodeDetail.setServiceTag("abc");
        nodeDetail.setvMotionManagementIpAddress("abc");
        nodeDetail.setvMotionManagementSubnetMask("abc");
        nodeDetail.setScaleIoData1SvmIpAddress("abc");
        nodeDetail.setScaleIoData2SvmIpAddress("abc");
        componentEndpointIds = new ComponentEndpointIds("abc","abc","abc", "abc");
        dvSwitchNames = new HashMap<String, String>();
        dvSwitchNames.put("dvswitch1","dvswitch1");
        dvSwitchNames.put("dvswitch2","dvswitch2");
        dvPortGroupNames = new HashMap<String, String>();
        dvPortGroupNames.put("vmotion", "vmotion");
        dvPortGroupNames.put("sio-data1", "sio-data1");
        dvPortGroupNames.put("sio-data2", "sio-data2");
    }

    @Ignore @Test
    public void testFailedException1() throws Exception
    {
        try {
            when(delegateExecution.getVariable(HOSTNAME)).thenReturn("abc");
            when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
            when(repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER")).thenReturn(componentEndpointIds);
            when(repository.getDvSwitchNames()).thenReturn(null);
            addHostToDvSwitch.delegateExecute(delegateExecution);
        } catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.ADD_HOST_TO_DV_SWITCH_FAILED));
            assertTrue(error.getMessage().equalsIgnoreCase("DV Switches were not found or are missing while attempting to Add Host To DV Switch"));
        }
    }

    @Ignore @Test
    public void testFailedException2() throws Exception
    {
        try {
            when(delegateExecution.getVariable(HOSTNAME)).thenReturn("abc");
            when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
            when(repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER")).thenReturn(componentEndpointIds);
            when(repository.getDvPortGroupNames(any())).thenReturn(null);
            addHostToDvSwitch.delegateExecute(delegateExecution);
        } catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.ADD_HOST_TO_DV_SWITCH_FAILED));
            assertTrue(error.getMessage().equalsIgnoreCase("DV Switches were not found or are missing while attempting to Add Host To DV Switch"));
        }
    }

    @Ignore @Test
    public void testFailedException3() throws Exception
    {
        try {
            when(delegateExecution.getVariable(HOSTNAME)).thenReturn("abc");
            when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
            when(repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER")).thenReturn(componentEndpointIds);
            when(repository.getDvSwitchNames()).thenReturn(dvSwitchNames);
            when(repository.getDvPortGroupNames(any())).thenReturn(dvPortGroupNames);
            given(nodeService.requestAddHostToDvSwitch(any())).willThrow(new NullPointerException());
            addHostToDvSwitch.delegateExecute(delegateExecution);
        } catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.ADD_HOST_TO_DV_SWITCH_FAILED));
            assertTrue(error.getMessage().contains("An Unexpected Exception occurred attempting to request Add Host To DV Switch"));
        }
    }

    @Ignore @Test
    public void testFailureToAdd() throws Exception
    {
        try {
            when(delegateExecution.getVariable(HOSTNAME)).thenReturn("abc");
            when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
            when(repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER")).thenReturn(componentEndpointIds);
            when(repository.getDvSwitchNames()).thenReturn(dvSwitchNames);
            when(repository.getDvPortGroupNames(any())).thenReturn(dvPortGroupNames);
            when(nodeService.requestAddHostToDvSwitch(any())).thenReturn(false);
            addHostToDvSwitch.delegateExecute(delegateExecution);
        } catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.ADD_HOST_TO_DV_SWITCH_FAILED));
            assertTrue(error.getMessage().equalsIgnoreCase("Add Host To DV Switch on Node abc failed!"));
        }
    }

    @Ignore @Test
    public void testSuccess() throws Exception
    {
        when(delegateExecution.getVariable(HOSTNAME)).thenReturn("abc");
        when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
        when(repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER")).thenReturn(componentEndpointIds);
        when(repository.getDvSwitchNames()).thenReturn(dvSwitchNames);
        when(repository.getDvPortGroupNames(any())).thenReturn(dvPortGroupNames);
        when(nodeService.requestAddHostToDvSwitch(any())).thenReturn(true);
        final AddHostToDvSwitch c = spy(new AddHostToDvSwitch(nodeService, repository));
        c.delegateExecute(delegateExecution);
        verify(c).updateDelegateStatus("Add Host To DV Switch on Node abc was successful.");
    }
}

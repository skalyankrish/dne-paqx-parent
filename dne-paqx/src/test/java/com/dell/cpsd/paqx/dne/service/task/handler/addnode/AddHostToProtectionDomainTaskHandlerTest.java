package com.dell.cpsd.paqx.dne.service.task.handler.addnode;

import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.domain.WorkflowTask;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.model.*;
import com.dell.cpsd.service.engineering.standards.DeviceAssignment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * The tests for the AddHostToProtectionDomainTaskHandler class.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class AddHostToProtectionDomainTaskHandlerTest
{
    @Mock
    private WorkflowTask task;

    @Mock
    private NodeService service;

    @Mock
    private DataServiceRepository repository;

    @Mock
    private Job job;

    @Mock
    private AddHostToProtectionDomainResponse response;

    private AddHostToProtectionDomainTaskHandler handler;

    @Mock
    private ComponentEndpointIds componentEndpointIds;

    @Mock
    private Map<String, TaskResponse> taskResponseMap;

    @Mock
    private NodeExpansionRequest inputParams;

    private static final String esxiManagementHostname = "fpr1-h17.lab.vce.com";

    private static final String protectionDomainId = "756edc5b00000000";

    private static final String protectionDomainName = "protectionDomainName1";

    private static final String scaleIoData1SvmIpAddress = "192.168.152.24";

    private static final String scaleIoData2SvmIpAddress = "192.168.160.24";

    private Map<String, DeviceAssignment> deviceAssignmentpool =  new HashMap<>();


    private static final String endpointUrl = "10.10.20.30";

    private static final String esxiManagementIpAddress = "11.12.12.14";

    @Before
    public void setUp() throws Exception
    {
        this.handler = spy(new AddHostToProtectionDomainTaskHandler(this.service, this.repository));
        deviceAssignmentpool.put("Device1",new DeviceAssignment("Device1","serialNumber1","logicalName1",
                "deviceName1","storagePoolId1","storagePoolName1"));
        deviceAssignmentpool.put("Device2",new DeviceAssignment("Device2","serialNumber2","logicalName2",
                "deviceName2","storagePoolId1","storagePoolName1"));
    }

    @Test
    public void executeTask_successful_case() throws Exception
    {
        doReturn(this.response).when(this.handler).initializeResponse(this.job);
        doReturn(this.inputParams).when(this.job).getInputParams();
        when(this.inputParams.getProtectionDomainId()).thenReturn(this.protectionDomainId);
        when(this.inputParams.getProtectionDomainName()).thenReturn(this.protectionDomainName);
        when(this.inputParams.getEsxiManagementHostname()).thenReturn(this.esxiManagementHostname);
        when(this.inputParams.getScaleIoData1SvmIpAddress()).thenReturn(this.scaleIoData1SvmIpAddress);
        when(this.inputParams.getScaleIoData2SvmIpAddress()).thenReturn(this.scaleIoData2SvmIpAddress);
        when(this.inputParams.getDeviceToDeviceStoragePool()).thenReturn(this.deviceAssignmentpool);
        doReturn(this.componentEndpointIds).when(this.repository).getComponentEndpointIds(anyString());
        when(this.componentEndpointIds.getEndpointUrl()).thenReturn(this.endpointUrl);
        when(this.service.requestAddHostToProtectionDomain(any())).thenReturn(true);

        boolean result = this.handler.executeTask(this.job);

        assertThat(result, is(true));
        verify(this.response).setWorkFlowTaskStatus(Status.SUCCEEDED);
        verify(this.response).setProtectionDomainId("756edc5b00000000");
        verify(this.response).setProtectionDomainName("protectionDomainName1");
        verify(this.response).setStoragePoolDetails(deviceAssignmentpool);
        verify(this.response, never()).addError(anyString());
    }

    @Test
    public void executeTask_successful_case_noHostname() throws Exception
    {
        doReturn(this.response).when(this.handler).initializeResponse(this.job);
        doReturn(this.inputParams).when(this.job).getInputParams();
        when(this.inputParams.getProtectionDomainId()).thenReturn(this.protectionDomainId);
        when(this.inputParams.getProtectionDomainName()).thenReturn(this.protectionDomainName);
        when(this.inputParams.getEsxiManagementIpAddress()).thenReturn(this.esxiManagementIpAddress);
        when(this.inputParams.getScaleIoData1SvmIpAddress()).thenReturn(this.scaleIoData1SvmIpAddress);
        when(this.inputParams.getScaleIoData2SvmIpAddress()).thenReturn(this.scaleIoData2SvmIpAddress);
        when(this.inputParams.getDeviceToDeviceStoragePool()).thenReturn(this.deviceAssignmentpool);
        doReturn(this.componentEndpointIds).when(this.repository).getComponentEndpointIds(anyString());
        when(this.componentEndpointIds.getEndpointUrl()).thenReturn(this.endpointUrl);
        when(this.service.requestAddHostToProtectionDomain(any())).thenReturn(true);

        boolean result = this.handler.executeTask(this.job);

        assertThat(result, is(true));
        verify(this.response).setWorkFlowTaskStatus(Status.SUCCEEDED);
        verify(this.response).setProtectionDomainId("756edc5b00000000");
        verify(this.response).setProtectionDomainName("protectionDomainName1");
        verify(this.response).setStoragePoolDetails(deviceAssignmentpool);
        verify(this.response, never()).addError(anyString());
    }

    @Test
    public void executeTask_exception_nodeservice_case() throws Exception
    {
        doReturn(this.response).when(this.handler).initializeResponse(this.job);
        doReturn(this.inputParams).when(this.job).getInputParams();
        when(this.inputParams.getProtectionDomainId()).thenReturn(this.protectionDomainId);
        when(this.inputParams.getEsxiManagementIpAddress()).thenReturn(this.esxiManagementIpAddress);
        when(this.inputParams.getScaleIoData1SvmIpAddress()).thenReturn(this.scaleIoData1SvmIpAddress);
        when(this.inputParams.getScaleIoData2SvmIpAddress()).thenReturn(this.scaleIoData2SvmIpAddress);
        doReturn(this.componentEndpointIds).when(this.repository).getComponentEndpointIds(anyString());
        when(this.componentEndpointIds.getEndpointUrl()).thenReturn(this.endpointUrl);
        when(this.service.requestAddHostToProtectionDomain(any())).thenReturn(false);

        boolean result = this.handler.executeTask(this.job);

        assertThat(result, is(false));
        verify(this.response).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.response).addError(anyString());
    }

    @Test
    public void executeTask_no_scaleIo_components() throws Exception
    {
        final ComponentEndpointIds nullComponentEndpointIds = null;

        doReturn(this.response).when(this.handler).initializeResponse(this.job);
        doReturn(this.inputParams).when(this.job).getInputParams();
        when(this.inputParams.getProtectionDomainId()).thenReturn(this.protectionDomainId);
        when(this.inputParams.getEsxiManagementHostname()).thenReturn(this.esxiManagementHostname);
        when(this.inputParams.getScaleIoData1SvmIpAddress()).thenReturn(this.scaleIoData1SvmIpAddress);
        when(this.inputParams.getScaleIoData2SvmIpAddress()).thenReturn(this.scaleIoData2SvmIpAddress);
        doReturn(nullComponentEndpointIds).when(this.repository).getComponentEndpointIds(anyString());

        boolean result = this.handler.executeTask(this.job);

        assertThat(result, is(false));
        verify(this.response).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.response).addError(anyString());
    }

    @Test
    public void initializeResponse() throws Exception
    {
        doReturn(this.task).when(this.job).getCurrentTask();
        String taskName = "addHostToProtectionDomainTask";
        doReturn(taskName).when(this.task).getTaskName();
        String stepName = "addHostToProtectionDomainStep";
        doReturn(stepName).when(this.job).getStep();

        final TaskResponse response = this.handler.initializeResponse(this.job);

        assertNotNull(response);
        assertEquals(taskName, response.getWorkFlowTaskName());
        assertEquals(Status.IN_PROGRESS, response.getWorkFlowTaskStatus());
    }

}

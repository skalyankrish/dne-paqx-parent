/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
package com.dell.cpsd.paqx.dne.service.task.handler.addnode;

import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.domain.WorkflowTask;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.model.AddHostToDvSwitchTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.paqx.dne.service.model.InstallEsxiTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.NodeExpansionRequest;
import com.dell.cpsd.paqx.dne.service.model.Status;
import com.dell.cpsd.paqx.dne.service.model.TaskResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * The tests for the AddHostToDvSwitchTaskHandler class.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class AddHostToDvSwitchTaskHandlerTest
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
    private AddHostToDvSwitchTaskResponse response;

    @Mock
    private InstallEsxiTaskResponse installEsxiTaskResponse;

    @Mock
    private ComponentEndpointIds componentEndpointIds;

    @Mock
    private Map<String, TaskResponse> taskResponseMap;

    @Mock
    private NodeExpansionRequest inputParams;

    @Mock
    private Map<String, String> dvSwitchNames;

    @Mock
    private Map<String, String> dvPortGroupNames;

    private AddHostToDvSwitchTaskHandler handler;
    private static final String hostname = "hostname_1.2.3.4";

    /**
     * The test setup.
     *
     * @since 1.0
     */
    @Before
    public void setUp() throws Exception
    {
        this.handler = spy(new AddHostToDvSwitchTaskHandler(this.service, this.repository));
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.AddHostToDvSwitchTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     *
     * @throws Exception
     */
    @Test
    public void executeTask_successful_case() throws Exception
    {
        doReturn(this.response).when(this.handler).initializeResponse(this.job);
        doReturn(this.componentEndpointIds).when(this.repository).getVCenterComponentEndpointIdsByEndpointType(anyString());
        doReturn(this.taskResponseMap).when(this.job).getTaskResponseMap();
        doReturn(this.installEsxiTaskResponse).when(this.taskResponseMap).get(anyString());
        doReturn(this.hostname).when(this.installEsxiTaskResponse).getHostname();
        doReturn(this.inputParams).when(this.job).getInputParams();
        doReturn("1.1.1.1").when(this.inputParams).getvMotionManagementIpAddress();
        doReturn("2.2.2.2").when(this.inputParams).getvMotionManagementSubnetMask();
        doReturn("3.3.3.3").when(this.inputParams).getScaleIoData1SvmIpAddress();
        doReturn("4.4.4.4").when(this.inputParams).getScaleIoSvmData1SubnetMask();
        doReturn("5.5.5.5").when(this.inputParams).getScaleIoData2SvmIpAddress();
        doReturn("6.6.6.6").when(this.inputParams).getScaleIoSvmData2SubnetMask();
        doReturn(this.dvSwitchNames).when(this.repository).getDvSwitchNames();
        doReturn("dvswitch0").when(this.dvSwitchNames).get("dvswitch0");
        doReturn("dvswitch1").when(this.dvSwitchNames).get("dvswitch1");
        doReturn("dvswitch2").when(this.dvSwitchNames).get("dvswitch2");
        doReturn(this.dvPortGroupNames).when(this.repository).getDvPortGroupNames(dvSwitchNames);
        doReturn("esx-mgmt-dvport-group").when(this.dvPortGroupNames).get("esx-mgmt");
        doReturn("vmotion-dvport-group").when(this.dvPortGroupNames).get("vmotion");
        doReturn("sio-data1-dvport-group").when(this.dvPortGroupNames).get("sio-data1");
        doReturn("sio-data2-dvport-group").when(this.dvPortGroupNames).get("sio-data2");

        doReturn(true).when(this.service).requestAddHostToDvSwitch(any());

        boolean result = this.handler.executeTask(this.job);

        assertThat(result, is(true));
        verify(this.response).setWorkFlowTaskStatus(Status.SUCCEEDED);
        verify(this.response, never()).addError(anyString());
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.AddHostToDvSwitchTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     *
     * @throws Exception
     */
    @Test
    public void executeTask_no_vcenter_components() throws Exception
    {
        ComponentEndpointIds nullComponentEndpointIds = null;

        doReturn(this.response).when(this.handler).initializeResponse(this.job);
        doReturn(nullComponentEndpointIds).when(this.repository).getVCenterComponentEndpointIdsByEndpointType(anyString());

        boolean result = this.handler.executeTask(this.job);

        assertThat(result, is(false));
        verify(this.response).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.response).addError(anyString());
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.AddHostToDvSwitchTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     *
     * @throws Exception
     */
    @Test
    public void executeTask_no_task_response() throws Exception
    {
        InstallEsxiTaskResponse nullInstallEsxiTaskResponse = null;

        doReturn(this.response).when(this.handler).initializeResponse(this.job);
        doReturn(this.componentEndpointIds).when(this.repository).getVCenterComponentEndpointIdsByEndpointType(anyString());
        doReturn(this.taskResponseMap).when(this.job).getTaskResponseMap();
        doReturn(nullInstallEsxiTaskResponse).when(this.taskResponseMap).get(anyString());

        boolean result = this.handler.executeTask(this.job);

        assertThat(result, is(false));
        verify(this.response).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.response).addError(anyString());
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.AddHostToDvSwitchTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     *
     * @throws Exception
     */
    @Test
    public void executeTask_no_hostname() throws Exception
    {
        String nullHostname = null;

        doReturn(this.response).when(this.handler).initializeResponse(this.job);
        doReturn(this.componentEndpointIds).when(this.repository).getVCenterComponentEndpointIdsByEndpointType(anyString());
        doReturn(this.taskResponseMap).when(this.job).getTaskResponseMap();
        doReturn(this.installEsxiTaskResponse).when(this.taskResponseMap).get(anyString());
        doReturn(nullHostname).when(this.installEsxiTaskResponse).getHostname();

        boolean result = this.handler.executeTask(this.job);

        assertThat(result, is(false));
        verify(this.response).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.response).addError(anyString());
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.AddHostToDvSwitchTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     *
     * @throws Exception
     */
    @Test
    public void executeTask_failed_add_host_to_dv_switch_request() throws Exception
    {
        doReturn(this.response).when(this.handler).initializeResponse(this.job);
        doReturn(this.componentEndpointIds).when(this.repository).getVCenterComponentEndpointIdsByEndpointType(anyString());
        doReturn(this.taskResponseMap).when(this.job).getTaskResponseMap();
        doReturn(this.installEsxiTaskResponse).when(this.taskResponseMap).get(anyString());
        doReturn(this.hostname).when(this.installEsxiTaskResponse).getHostname();
        doReturn(this.inputParams).when(this.job).getInputParams();
        doReturn("1.1.1.1").when(this.inputParams).getvMotionManagementIpAddress();
        doReturn("2.2.2.2").when(this.inputParams).getvMotionManagementSubnetMask();
        doReturn("3.3.3.3").when(this.inputParams).getScaleIoData1SvmIpAddress();
        doReturn("4.4.4.4").when(this.inputParams).getScaleIoSvmData1SubnetMask();
        doReturn("5.5.5.5").when(this.inputParams).getScaleIoData2SvmIpAddress();
        doReturn("6.6.6.6").when(this.inputParams).getScaleIoSvmData2SubnetMask();
        doReturn(this.dvSwitchNames).when(this.repository).getDvSwitchNames();
        doReturn("dvswitch0").when(this.dvSwitchNames).get("dvswitch0");
        doReturn("dvswitch1").when(this.dvSwitchNames).get("dvswitch1");
        doReturn("dvswitch2").when(this.dvSwitchNames).get("dvswitch2");
        doReturn(this.dvPortGroupNames).when(this.repository).getDvPortGroupNames(dvSwitchNames);
        doReturn("esx-mgmt-dvport-group").when(this.dvPortGroupNames).get("esx-mgmt");
        doReturn("vmotion-dvport-group").when(this.dvPortGroupNames).get("vmotion");
        doReturn("sio-data1-dvport-group").when(this.dvPortGroupNames).get("sio-data1");
        doReturn("sio-data2-dvport-group").when(this.dvPortGroupNames).get("sio-data2");
        doReturn(false).when(this.service).requestAddHostToDvSwitch(any());

        boolean result = this.handler.executeTask(this.job);

        assertThat(result, is(false));
        verify(this.response).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.response).addError(anyString());
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.AddHostToDvSwitchTaskHandler#initializeResponse(com.dell.cpsd.paqx.dne.domain.Job)}.
     *
     * @throws Exception
     */
    @Test
    public void initializeResponse() throws Exception
    {
        doReturn(this.task).when(this.job).getCurrentTask();
        String taskName = "addHostToDvSwitchTask";
        doReturn(taskName).when(this.task).getTaskName();
        String stepName = "addHostToDvSwitchStep";
        doReturn(stepName).when(this.job).getStep();

        AddHostToDvSwitchTaskResponse response = this.handler.initializeResponse(this.job);
        
        assertNotNull(response);
        assertEquals(taskName, response.getWorkFlowTaskName());
        assertEquals(Status.IN_PROGRESS, response.getWorkFlowTaskStatus());
    }
}
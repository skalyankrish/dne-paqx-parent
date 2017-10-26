/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.task.handler.addnode;

import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.domain.WorkflowTask;
import com.dell.cpsd.paqx.dne.domain.vcenter.Host;
import com.dell.cpsd.paqx.dne.domain.vcenter.HostDnsConfig;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.paqx.dne.service.model.DeployScaleIoVmTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.InstallEsxiTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.NodeExpansionRequest;
import com.dell.cpsd.paqx.dne.service.model.Status;
import com.dell.cpsd.paqx.dne.service.model.TaskResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * The tests for the DeployScaleIoVmTaskHandler class.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class DeployScaleIoVmTaskHandlerTest
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
    private DeployScaleIoVmTaskResponse response;

    @Mock
    private NodeExpansionRequest request;

    @Mock
    private InstallEsxiTaskResponse installEsxiTaskResponse;

    @Mock
    private ComponentEndpointIds componentEndpointIds;

    @Mock
    private Map<String, TaskResponse> taskResponseMap;

    @Mock
    private Host host;

    @Mock
    private HostDnsConfig hostDnsConfig;

    private DeployScaleIoVmTaskHandler handler;

    private String hostname                       = "hostname_1.2.3.4";
    private String domainName                     = "example.com";
    private String clusterName                    = "cluster_01";
    private String dataCenterName                 = "datacenter_01";
    private String scaleIOSVMManagementIpAddress  = "1.2.3.4";
    private String scaleIOSVMManagementGatewayAddress  = "1.2.3.5";
    private String esxiManagementGatewayIpAddress = "4.3.2.1";
    private String sioMgmtSubnetMask              = "255.255.255.0";
    private String sioData1IpAddress              = "4.4.4.4";
    private String sioData1SubnetMask             = "255.255.255.0";
    private String sioData2IpAddress              = "5.5.5.5";
    private String sioData2SubnetMask             = "255.255.255.0";

    private List<String> dnsConfigIps;

    @Before
    public void setUp() throws Exception
    {
        this.handler = spy(new DeployScaleIoVmTaskHandler(this.service, this.repository, 0));

        this.dnsConfigIps = new ArrayList<>();
        this.dnsConfigIps.add("1.1.1.1");
        this.dnsConfigIps.add("2.2.2.2");
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.DeployScaleIoVmTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     *
     * @throws Exception
     */
    @Test
    public void executeTask_should_successfully_deploy_the_scaleio_vm() throws Exception
    {
        doReturn(this.response).when(this.handler).initializeResponse(this.job);
        doReturn(this.taskResponseMap).when(this.job).getTaskResponseMap();
        doReturn(this.installEsxiTaskResponse).when(this.taskResponseMap).get(anyString());
        doReturn(this.request).when(this.job).getInputParams();
        doReturn(this.componentEndpointIds).when(this.repository).getVCenterComponentEndpointIdsByEndpointType(anyString());
        doReturn(this.hostname).when(this.installEsxiTaskResponse).getHostname();
        doReturn(this.clusterName).when(this.request).getClusterName();
        doReturn(this.dataCenterName).when(this.repository).getDataCenterName(anyString());
        doReturn(this.scaleIOSVMManagementIpAddress).when(this.request).getScaleIoSvmManagementIpAddress();
        doReturn(this.domainName).when(this.repository).getDomainName();
        doReturn(this.host).when(this.repository).getExistingVCenterHost();
        doReturn(this.hostDnsConfig).when(this.host).getHostDnsConfig();
        doReturn(this.dnsConfigIps).when(this.hostDnsConfig).getDnsConfigIPs();
        doReturn(this.esxiManagementGatewayIpAddress).when(this.request).getEsxiManagementGatewayIpAddress();
        doReturn(this.sioMgmtSubnetMask).when(this.request).getScaleIoSvmManagementSubnetMask();
        doReturn(this.scaleIOSVMManagementGatewayAddress).when(this.request).getScaleIoSvmManagementGatewayAddress();
        doReturn(this.sioData1IpAddress).when(this.request).getScaleIoData1SvmIpAddress();
        doReturn(this.sioData1SubnetMask).when(this.request).getScaleIoSvmData1SubnetMask();
        doReturn(this.sioData2IpAddress).when(this.request).getScaleIoData2SvmIpAddress();
        doReturn(this.sioData2SubnetMask).when(this.request).getScaleIoSvmData2SubnetMask();

        doReturn(true).when(this.service).requestDeployScaleIoVm(any());

        boolean result = this.handler.executeTask(this.job);

        assertThat(result, is(true));
        verify(this.response).setWorkFlowTaskStatus(Status.SUCCEEDED);
        verify(this.response).setNewVMName(argThat(value -> value.contains(this.scaleIOSVMManagementIpAddress)));
        verify(this.response, never()).addError(anyString());
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.DeployScaleIoVmTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     *
     * @throws Exception
     */
    @Test
    public void executeTask_should_fail_the_workflow_when_there_is_no_esxi_task_response() throws Exception
    {
        doReturn(this.response).when(this.handler).initializeResponse(this.job);
        doReturn(this.taskResponseMap).when(this.job).getTaskResponseMap();
        doReturn(null).when(this.taskResponseMap).get(anyString());

        boolean result = this.handler.executeTask(this.job);

        assertThat(result, is(false));
        verify(this.response).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.response, never()).setNewVMName(anyString());
        verify(this.response).addError(anyString());
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.DeployScaleIoVmTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     *
     * @throws Exception
     */
    @Test
    public void executeTask_should_fail_the_workflow_when_there_is_no_NodeExpansionRequest_object() throws Exception
    {
        doReturn(this.response).when(this.handler).initializeResponse(this.job);
        doReturn(this.taskResponseMap).when(this.job).getTaskResponseMap();
        doReturn(this.installEsxiTaskResponse).when(this.taskResponseMap).get(anyString());
        doReturn(null).when(this.job).getInputParams();

        boolean result = this.handler.executeTask(this.job);

        assertThat(result, is(false));
        verify(this.response).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.response, never()).setNewVMName(anyString());
        verify(this.response).addError(anyString());
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.DeployScaleIoVmTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     *
     * @throws Exception
     */
    @Test
    public void executeTask_should_fail_the_workflow_when_there_are_no_vcenter_components() throws Exception
    {
        doReturn(this.response).when(this.handler).initializeResponse(this.job);
        doReturn(this.taskResponseMap).when(this.job).getTaskResponseMap();
        doReturn(this.installEsxiTaskResponse).when(this.taskResponseMap).get(anyString());
        doReturn(this.request).when(this.job).getInputParams();
        doReturn(null).when(this.repository).getVCenterComponentEndpointIdsByEndpointType(anyString());

        boolean result = this.handler.executeTask(this.job);

        assertThat(result, is(false));
        verify(this.response).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.response, never()).setNewVMName(anyString());
        verify(this.response).addError(anyString());
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.DeployScaleIoVmTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     *
     * @throws Exception
     */
    @Test
    public void executeTask_should_fail_the_workflow_when_there_is_no_hostname() throws Exception
    {
        doReturn(this.response).when(this.handler).initializeResponse(this.job);
        doReturn(this.taskResponseMap).when(this.job).getTaskResponseMap();
        doReturn(this.installEsxiTaskResponse).when(this.taskResponseMap).get(anyString());
        doReturn(this.request).when(this.job).getInputParams();
        doReturn(this.componentEndpointIds).when(this.repository).getVCenterComponentEndpointIdsByEndpointType(anyString());
        doReturn(null).when(this.installEsxiTaskResponse).getHostname();

        boolean result = this.handler.executeTask(this.job);

        assertThat(result, is(false));
        verify(this.response).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.response, never()).setNewVMName(anyString());
        verify(this.response).addError(anyString());
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.DeployScaleIoVmTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     *
     * @throws Exception
     */
    @Test
    public void executeTask_should_fail_the_workflow_when_there_is_no_clustername() throws Exception
    {
        doReturn(this.response).when(this.handler).initializeResponse(this.job);
        doReturn(this.taskResponseMap).when(this.job).getTaskResponseMap();
        doReturn(this.installEsxiTaskResponse).when(this.taskResponseMap).get(anyString());
        doReturn(this.request).when(this.job).getInputParams();
        doReturn(this.componentEndpointIds).when(this.repository).getVCenterComponentEndpointIdsByEndpointType(anyString());
        doReturn(this.hostname).when(this.installEsxiTaskResponse).getHostname();
        doReturn(null).when(this.request).getClusterName();

        boolean result = this.handler.executeTask(this.job);

        assertThat(result, is(false));
        verify(this.response).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.response, never()).setNewVMName(anyString());
        verify(this.response).addError(anyString());
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.DeployScaleIoVmTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     *
     * @throws Exception
     */
    @Test
    public void executeTask_should_fail_the_workflow_when_there_is_no_datacenter_name() throws Exception
    {
        doReturn(this.response).when(this.handler).initializeResponse(this.job);
        doReturn(this.taskResponseMap).when(this.job).getTaskResponseMap();
        doReturn(this.installEsxiTaskResponse).when(this.taskResponseMap).get(anyString());
        doReturn(this.request).when(this.job).getInputParams();
        doReturn(this.componentEndpointIds).when(this.repository).getVCenterComponentEndpointIdsByEndpointType(anyString());
        doReturn(this.hostname).when(this.installEsxiTaskResponse).getHostname();
        doReturn(this.clusterName).when(this.request).getClusterName();
        doReturn(null).when(this.repository).getDataCenterName(anyString());

        boolean result = this.handler.executeTask(this.job);

        assertThat(result, is(false));
        verify(this.response).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.response, never()).setNewVMName(anyString());
        verify(this.response).addError(anyString());
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.DeployScaleIoVmTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     *
     * @throws Exception
     */
    @Test
    public void executeTask_should_fail_the_workflow_when_there_is_no_scaleIOSVMManagementIpAddress() throws Exception
    {
        doReturn(this.response).when(this.handler).initializeResponse(this.job);
        doReturn(this.taskResponseMap).when(this.job).getTaskResponseMap();
        doReturn(this.installEsxiTaskResponse).when(this.taskResponseMap).get(anyString());
        doReturn(this.request).when(this.job).getInputParams();
        doReturn(this.componentEndpointIds).when(this.repository).getVCenterComponentEndpointIdsByEndpointType(anyString());
        doReturn(this.hostname).when(this.installEsxiTaskResponse).getHostname();
        doReturn(this.clusterName).when(this.request).getClusterName();
        doReturn(this.dataCenterName).when(this.repository).getDataCenterName(anyString());
        doReturn(null).when(this.request).getScaleIoSvmManagementIpAddress();

        boolean result = this.handler.executeTask(this.job);

        assertThat(result, is(false));
        verify(this.response).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.response, never()).setNewVMName(anyString());
        verify(this.response).addError(anyString());
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.DeployScaleIoVmTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     *
     * @throws Exception
     */
    @Test
    public void executeTask_should_fail_the_workflow_when_there_is_no_scaleIOSVMManagementGatewayAddress() throws Exception
    {
        doReturn(this.response).when(this.handler).initializeResponse(this.job);
        doReturn(this.taskResponseMap).when(this.job).getTaskResponseMap();
        doReturn(this.installEsxiTaskResponse).when(this.taskResponseMap).get(anyString());
        doReturn(this.request).when(this.job).getInputParams();
        doReturn(this.componentEndpointIds).when(this.repository).getVCenterComponentEndpointIdsByEndpointType(anyString());
        doReturn(this.hostname).when(this.installEsxiTaskResponse).getHostname();
        doReturn(this.clusterName).when(this.request).getClusterName();
        doReturn(this.dataCenterName).when(this.repository).getDataCenterName(anyString());
        doReturn(this.scaleIOSVMManagementIpAddress).when(this.request).getScaleIoSvmManagementIpAddress();

        boolean result = this.handler.executeTask(this.job);

        assertThat(result, is(false));
        verify(this.response).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.response, never()).setNewVMName(anyString());
        verify(this.response).addError(anyString());
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.DeployScaleIoVmTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     *
     * @throws Exception
     */
    @Test
    public void executeTask_should_fail_the_workflow_when_there_is_no_domainname() throws Exception
    {
        doReturn(this.response).when(this.handler).initializeResponse(this.job);
        doReturn(this.taskResponseMap).when(this.job).getTaskResponseMap();
        doReturn(this.installEsxiTaskResponse).when(this.taskResponseMap).get(anyString());
        doReturn(this.request).when(this.job).getInputParams();
        doReturn(this.componentEndpointIds).when(this.repository).getVCenterComponentEndpointIdsByEndpointType(anyString());
        doReturn(this.hostname).when(this.installEsxiTaskResponse).getHostname();
        doReturn(this.clusterName).when(this.request).getClusterName();
        doReturn(this.dataCenterName).when(this.repository).getDataCenterName(anyString());
        doReturn(this.scaleIOSVMManagementIpAddress).when(this.request).getScaleIoSvmManagementIpAddress();
        doReturn(this.scaleIOSVMManagementGatewayAddress).when(this.request).getScaleIoSvmManagementGatewayAddress();

        boolean result = this.handler.executeTask(this.job);

        assertThat(result, is(false));
        verify(this.response).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.response, never()).setNewVMName(anyString());
        verify(this.response).addError(anyString());
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.DeployScaleIoVmTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     *
     * @throws Exception
     */
    @Test
    public void executeTask_should_fail_the_workflow_when_there_is_no_dns_ips() throws Exception
    {
        doReturn(this.response).when(this.handler).initializeResponse(this.job);
        doReturn(this.taskResponseMap).when(this.job).getTaskResponseMap();
        doReturn(this.installEsxiTaskResponse).when(this.taskResponseMap).get(anyString());
        doReturn(this.request).when(this.job).getInputParams();
        doReturn(this.componentEndpointIds).when(this.repository).getVCenterComponentEndpointIdsByEndpointType(anyString());
        doReturn(this.hostname).when(this.installEsxiTaskResponse).getHostname();
        doReturn(this.clusterName).when(this.request).getClusterName();
        doReturn(this.dataCenterName).when(this.repository).getDataCenterName(anyString());
        doReturn(this.scaleIOSVMManagementIpAddress).when(this.request).getScaleIoSvmManagementIpAddress();
        doReturn(this.scaleIOSVMManagementGatewayAddress).when(this.request).getScaleIoSvmManagementGatewayAddress();

        boolean result = this.handler.executeTask(this.job);

        assertThat(result, is(false));
        verify(this.response).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.response, never()).setNewVMName(anyString());
        verify(this.response).addError(anyString());
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.DeployScaleIoVmTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     *
     * @throws Exception
     */
    @Test
    public void executeTask_should_fail_the_workflow_when_there_is_no_esxi_management_gateway_ip_address() throws Exception
    {
        doReturn(this.response).when(this.handler).initializeResponse(this.job);
        doReturn(this.taskResponseMap).when(this.job).getTaskResponseMap();
        doReturn(this.installEsxiTaskResponse).when(this.taskResponseMap).get(anyString());
        doReturn(this.request).when(this.job).getInputParams();
        doReturn(this.componentEndpointIds).when(this.repository).getVCenterComponentEndpointIdsByEndpointType(anyString());
        doReturn(this.hostname).when(this.installEsxiTaskResponse).getHostname();
        doReturn(this.clusterName).when(this.request).getClusterName();
        doReturn(this.dataCenterName).when(this.repository).getDataCenterName(anyString());
        doReturn(this.scaleIOSVMManagementIpAddress).when(this.request).getScaleIoSvmManagementIpAddress();
        doReturn(this.scaleIOSVMManagementGatewayAddress).when(this.request).getScaleIoSvmManagementGatewayAddress();

        boolean result = this.handler.executeTask(this.job);

        assertThat(result, is(false));
        verify(this.response).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.response, never()).setNewVMName(anyString());
        verify(this.response).addError(anyString());
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.DeployScaleIoVmTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     *
     * @throws Exception
     */
    @Test
    public void executeTask_should_fail_the_workflow_when_there_is_no_esxi_management_subnet_mask() throws Exception
    {
        doReturn(this.response).when(this.handler).initializeResponse(this.job);
        doReturn(this.taskResponseMap).when(this.job).getTaskResponseMap();
        doReturn(this.installEsxiTaskResponse).when(this.taskResponseMap).get(anyString());
        doReturn(this.request).when(this.job).getInputParams();
        doReturn(this.componentEndpointIds).when(this.repository).getVCenterComponentEndpointIdsByEndpointType(anyString());
        doReturn(this.hostname).when(this.installEsxiTaskResponse).getHostname();
        doReturn(this.clusterName).when(this.request).getClusterName();
        doReturn(this.dataCenterName).when(this.repository).getDataCenterName(anyString());
        doReturn(this.scaleIOSVMManagementIpAddress).when(this.request).getScaleIoSvmManagementIpAddress();
        doReturn(this.scaleIOSVMManagementGatewayAddress).when(this.request).getScaleIoSvmManagementGatewayAddress();

        boolean result = this.handler.executeTask(this.job);

        assertThat(result, is(false));
        verify(this.response).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.response, never()).setNewVMName(anyString());
        verify(this.response).addError(anyString());
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.DeployScaleIoVmTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     *
     * @throws Exception
     */
    @Test
    public void executeTask_should_fail_the_workflow_when_the_deploy_scaleio_vm_request_fails() throws Exception
    {
        doReturn(this.response).when(this.handler).initializeResponse(this.job);
        doReturn(this.taskResponseMap).when(this.job).getTaskResponseMap();
        doReturn(this.installEsxiTaskResponse).when(this.taskResponseMap).get(anyString());
        doReturn(this.request).when(this.job).getInputParams();
        doReturn(this.componentEndpointIds).when(this.repository).getVCenterComponentEndpointIdsByEndpointType(anyString());
        doReturn(this.hostname).when(this.installEsxiTaskResponse).getHostname();
        doReturn(this.clusterName).when(this.request).getClusterName();
        doReturn(this.dataCenterName).when(this.repository).getDataCenterName(anyString());
        doReturn(this.scaleIOSVMManagementIpAddress).when(this.request).getScaleIoSvmManagementIpAddress();
        doReturn(this.scaleIOSVMManagementGatewayAddress).when(this.request).getScaleIoSvmManagementGatewayAddress();
        doReturn(this.domainName).when(this.repository).getDomainName();
        doReturn(this.host).when(this.repository).getExistingVCenterHost();
        doReturn(this.hostDnsConfig).when(this.host).getHostDnsConfig();
        doReturn(this.dnsConfigIps).when(this.hostDnsConfig).getDnsConfigIPs();
        doReturn(this.esxiManagementGatewayIpAddress).when(this.request).getEsxiManagementGatewayIpAddress();
        doReturn(this.sioMgmtSubnetMask).when(this.request).getScaleIoSvmManagementSubnetMask();
        doReturn(this.sioData1IpAddress).when(this.request).getScaleIoData1SvmIpAddress();
        doReturn(this.sioData1SubnetMask).when(this.request).getScaleIoSvmData1SubnetMask();
        doReturn(this.sioData2IpAddress).when(this.request).getScaleIoData2SvmIpAddress();
        doReturn(this.sioData2SubnetMask).when(this.request).getScaleIoSvmData2SubnetMask();
        doReturn(false).when(this.service).requestDeployScaleIoVm(any());

        boolean result = this.handler.executeTask(this.job);

        assertThat(result, is(false));
        verify(this.response).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.response, never()).setNewVMName(anyString());
        verify(this.response).addError(anyString());
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.DeployScaleIoVmTaskHandler#initializeResponse(com.dell.cpsd.paqx.dne.domain.Job)}.
     *
     * @throws Exception
     */
    @Test
    public void initializeResponse() throws Exception
    {
        doReturn(this.task).when(this.job).getCurrentTask();
        String taskName = "deployScaleIoVmTask";
        doReturn(taskName).when(this.task).getTaskName();
        String stepName = "deployScaleIoVmStep";
        doReturn(stepName).when(this.job).getStep();

        DeployScaleIoVmTaskResponse response = this.handler.initializeResponse(this.job);

        assertNotNull(response);
        assertEquals(taskName, response.getWorkFlowTaskName());
        assertEquals(Status.IN_PROGRESS, response.getWorkFlowTaskStatus());
    }

}
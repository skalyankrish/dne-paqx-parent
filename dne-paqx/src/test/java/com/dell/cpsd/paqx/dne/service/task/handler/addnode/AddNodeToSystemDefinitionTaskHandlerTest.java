/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.task.handler.addnode;

import com.dell.cpsd.paqx.dne.TestUtil;
import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.domain.WorkflowTask;
import com.dell.cpsd.paqx.dne.domain.node.DiscoveredNodeInfo;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.model.NodeExpansionRequest;
import com.dell.cpsd.paqx.dne.service.model.Status;
import com.dell.cpsd.sdk.AMQPClient;
import com.dell.cpsd.service.system.definition.api.Component;
import com.dell.cpsd.service.system.definition.api.ConvergedSystem;
import com.dell.cpsd.service.system.definition.api.Definition;
import com.dell.cpsd.service.system.definition.api.Identity;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The tests for the AddNodeToSystemDefinitionTaskHandler class.
 * <p>
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class AddNodeToSystemDefinitionTaskHandlerTest
{
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private AMQPClient client;

    @Mock
    private DataServiceRepository repository;

    private AddNodeToSystemDefinitionTaskHandler handler;

    private Job jobIn;

    private List<ConvergedSystem> convergedSystems;

    private List<ConvergedSystem> updatedConvergedSystems;

    private DiscoveredNodeInfo discoveredNodeInfo;

    /**
     * The test setup.
     *
     * @since 1.0
     */
    @Before
    public void setUp()
    {
        handler = new AddNodeToSystemDefinitionTaskHandler(this.client, repository);

        jobIn = new Job(UUID.randomUUID(), "testWorkflow", "updateSystemDefinition", "in-progress", null);
        NodeExpansionRequest nodeExpansionRequest = new NodeExpansionRequest("idracIpAddress", "idracGatewayIpAddress", "idracSubnetMask",
                "managementIpAddress", "esxiKernelIpAddress1", "esxiKernelIpAddress2", "esxiManagementHostname", "scaleIoData1SvmIpAddress",
                "scaleIoSvmData1SubnetMask", "scaleIoData2SvmIpAddress", "scaleIoSvmData2SubnetMask",
                "scaleIoSvmManagementIpAddress", "scaleIoSvmManagementGatewayAddress", "scaleIoSvmManagementSubnetMask", "symphonyUuid",
                "clusterName", "vMotionManagementIpAddress", "vMotionManagementSubnetMask", TestUtil.createDeviceAssignmentMap());
        jobIn.setInputParams(nodeExpansionRequest);
        Map<String, WorkflowTask> taskMap = new HashMap<>();
        WorkflowTask task = new WorkflowTask();
        taskMap.put("updateSystemDefinition", task);
        jobIn.setTaskMap(taskMap);

        convergedSystems = new ArrayList<>();
        ConvergedSystem system = new ConvergedSystem();
        Definition definition = new Definition();
        definition.setModelFamily("TEST");
        definition.setModel("test");
        definition.setProduct("FLEX");
        definition.setProductFamily("FLEX FAMILY");
        system.setDefinition(definition);
        system.setUuid("testSystem");
        convergedSystems.add(system);

        updatedConvergedSystems = new ArrayList<>();
        ConvergedSystem updatedSystem = new ConvergedSystem();
        updatedSystem.setDefinition(definition);
        updatedSystem.setUuid("testSystem");

        Component com = new Component();
        com.setUuid("testuuid");
        Definition comDef = new Definition();
        comDef.setProductFamily("Dell");
        comDef.setProduct("Dell");
        comDef.setModel("R730");
        comDef.setModelFamily("R730");
        com.setDefinition(comDef);

        Identity identity = new Identity();
        identity.setIdentifier("testuuid");
        identity.setElementType("computeServer");
        identity.setSerialNumber("SerialNumber");
        com.setIdentity(identity);
        updatedSystem.setComponents(Collections.singletonList(com));

        updatedConvergedSystems.add(updatedSystem);
        discoveredNodeInfo = new DiscoveredNodeInfo("R730", "R730", "Dell", "Dell", "SerialNumber", "testuuid");
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.AddNodeToSystemDefinitionTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     */
    @Test
    public void testExecuteTask_successful_case()
    {
        when(client.getConvergedSystems()).thenReturn(convergedSystems);
        when(client.getComponents(any())).thenReturn(convergedSystems, updatedConvergedSystems);
        when(repository.getDiscoveredNodeInfo(anyString())).thenReturn(discoveredNodeInfo);

        boolean result = this.handler.executeTask(jobIn);

        assertThat(result, is(true));
        verify(this.client).addComponent(any(), any(), any(), any());
        assertThat(jobIn.getTaskResponseMap().get(jobIn.getStep()).getWorkFlowTaskStatus(), is(Status.SUCCEEDED));
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.AddNodeToSystemDefinitionTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     *
     * @since 1.0
     */
    @Test
    public void testExecuteTask_should_fail_when_the_jobParams_is_null()
    {
        jobIn.setInputParams(null);

        boolean result = this.handler.executeTask(jobIn);

        assertThat(result, is(false));
        verify(this.client, never()).addComponent(any(), any(), any(), any());
        assertThat(jobIn.getTaskResponseMap().get(jobIn.getStep()).getWorkFlowTaskStatus(), is(Status.FAILED));
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.AddNodeToSystemDefinitionTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     *
     * @since 1.0
     */
    @Test
    public void testExecuteTask_should_fail_when_the_symphonyuuid_is_null()
    {
        jobIn.getInputParams().setSymphonyUuid(null);

        boolean result = this.handler.executeTask(jobIn);

        assertThat(result, is(false));
        verify(this.client, never()).addComponent(any(), any(), any(), any());
        assertThat(jobIn.getTaskResponseMap().get(jobIn.getStep()).getWorkFlowTaskStatus(), is(Status.FAILED));
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.AddNodeToSystemDefinitionTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     *
     * @since 1.0
     */
    @Test
    public void testExecuteTask_should_fail_when_there_is_no_converged_systems()
    {
        when(client.getConvergedSystems()).thenReturn(null);

        boolean result = this.handler.executeTask(jobIn);

        assertThat(result, is(false));
        verify(this.client, never()).addComponent(any(), any(), any(), any());
        assertThat(jobIn.getTaskResponseMap().get(jobIn.getStep()).getWorkFlowTaskStatus(), is(Status.FAILED));
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.AddNodeToSystemDefinitionTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     *
     * @since 1.0
     */
    @Test
    public void testExecuteTask_should_fail_when_there_is_no_system()
    {
        when(client.getConvergedSystems()).thenReturn(convergedSystems);
        when(client.getComponents(any())).thenReturn(null);

        boolean result = this.handler.executeTask(jobIn);

        assertThat(result, is(false));
        verify(this.client, never()).addComponent(any(), any(), any(), any());
        assertThat(jobIn.getTaskResponseMap().get(jobIn.getStep()).getWorkFlowTaskStatus(), is(Status.FAILED));
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.AddNodeToSystemDefinitionTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     *
     * @since 1.0
     */
    @Test
    public void testExecuteTask_should_fail_when_there_is_no_ndoeInfo()
    {
        when(client.getConvergedSystems()).thenReturn(convergedSystems);
        when(client.getComponents(any())).thenReturn(convergedSystems, updatedConvergedSystems);

        boolean result = this.handler.executeTask(jobIn);

        assertThat(result, is(false));
        verify(this.client, never()).addComponent(any(), any(), any(), any());
        assertThat(jobIn.getTaskResponseMap().get(jobIn.getStep()).getWorkFlowTaskStatus(), is(Status.FAILED));
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.AddNodeToSystemDefinitionTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     *
     * @since 1.0
     */
    @Test
    public void testExecuteTask_should_fail_when_the_node_is_not_added_to_system_definition()
    {
        when(client.getConvergedSystems()).thenReturn(convergedSystems);
        when(client.getComponents(any())).thenReturn(convergedSystems, convergedSystems);
        when(repository.getDiscoveredNodeInfo(anyString())).thenReturn(discoveredNodeInfo);

        boolean result = this.handler.executeTask(jobIn);

        assertThat(result, is(false));
        verify(this.client).addComponent(any(), any(), any(), any());
        assertThat(jobIn.getTaskResponseMap().get(jobIn.getStep()).getWorkFlowTaskStatus(), is(Status.FAILED));
    }
}

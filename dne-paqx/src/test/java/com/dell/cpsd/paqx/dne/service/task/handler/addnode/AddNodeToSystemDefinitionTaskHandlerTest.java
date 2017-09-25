/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.task.handler.addnode;

import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.domain.WorkflowTask;
import com.dell.cpsd.paqx.dne.service.model.NodeExpansionRequest;
import com.dell.cpsd.paqx.dne.service.model.Status;
import com.dell.cpsd.paqx.dne.service.model.TaskResponse;
import com.dell.cpsd.sdk.AMQPClient;
import com.dell.cpsd.service.common.client.exception.ServiceExecutionException;
import com.dell.cpsd.service.common.client.exception.ServiceTimeoutException;
import com.dell.cpsd.service.system.definition.api.Component;
import com.dell.cpsd.service.system.definition.api.ComponentsFilter;
import com.dell.cpsd.service.system.definition.api.ConvergedSystem;
import com.dell.cpsd.service.system.definition.api.Group;
import com.dell.cpsd.service.system.definition.api.Identity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * The tests for the AddNodeToSystemDefinitionTaskHandler class.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class AddNodeToSystemDefinitionTaskHandlerTest
{
    @Mock
    private AMQPClient client;

    @Mock
    private WorkflowTask task;

    @Mock
    private Job job;

    @Mock
    private TaskResponse response;

    @Mock
    private NodeExpansionRequest request;

    @Mock
    private ConvergedSystem system;

    @Mock
    private Component component;

    @Mock
    private Group group;

    @Mock
    private Identity identity;

    private String taskName = "addNodeTosystemDefinitionTask";
    private String stepName = "addNodeTosystemDefinitionStep";
    private String symphonyUuid = "symphonyUuid";

    private AddNodeToSystemDefinitionTaskHandler handler;

    private List<ConvergedSystem> systems;
    private List<Component> components;
    private List<Group> groups;

    /**
     * The test setup.
     *
     * @since 1.0
     */
    @Before
    public void setUp()
    {
        this.handler = spy(new AddNodeToSystemDefinitionTaskHandler(this.client));

        this.systems = new ArrayList<>();
        this.systems.add(this.system);

        this.components = new ArrayList<>();
        this.component.setIdentity(new Identity());
        this.components.add(this.component);

        this.groups = new ArrayList<>();
        this.groups.add(this.group);
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.AddNodeToSystemDefinitionTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     */
    @Test
    public void testExecuteTask_successful_case()
    {
        doReturn(this.response).when(this.handler).initializeResponse(this.job);
        doReturn(this.request).when(this.job).getInputParams();
        doReturn(this.symphonyUuid).when(this.request).getSymphonyUuid();
        doReturn(this.systems).when(this.client).getConvergedSystems();
        doReturn(this.systems).when(this.client).getComponents(any(ComponentsFilter.class));
        doReturn(this.groups).when(this.system).getGroups();
        doNothing().when(this.client).addComponent(any(ConvergedSystem.class), any(Component.class), anyList(), anyString());
        doReturn(this.components).when(this.system).getComponents();
        doReturn(this.identity).when(this.component).getIdentity();
        doReturn(this.symphonyUuid).when(this.identity).getIdentifier();

        boolean result = this.handler.executeTask(this.job);

        assertThat(result, is(true));
        verify(this.client).addComponent(any(), any(), any(), any());
        verify(this.response).setWorkFlowTaskStatus(Status.SUCCEEDED);
        verify(this.response, never()).addError(anyString());
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.AddNodeToSystemDefinitionTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     *
     * @throws ServiceExecutionException
     * @throws ServiceTimeoutException
     * @since 1.0
     */
    @Test
    public void testExecuteTask_should_fail_when_the_node_expansion_request_is_null() throws ServiceTimeoutException, ServiceExecutionException
    {
        NodeExpansionRequest nullRequest = null;

        doReturn(this.response).when(this.handler).initializeResponse(this.job);
        doReturn(nullRequest).when(this.job).getInputParams();

        boolean result = this.handler.executeTask(this.job);

        assertThat(result, is(false));
        verify(this.client, never()).addComponent(any(), any(), any(), any());
        verify(this.response).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.response).addError(anyString());
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.AddNodeToSystemDefinitionTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     *
     * @since 1.0
     */
    @Test
    public void testExecuteTask_should_fail_when_the_symphonyuuid_is_empty()
    {
        String emptySymphonyUuid = null;

        doReturn(this.response).when(this.handler).initializeResponse(this.job);
        doReturn(this.request).when(this.job).getInputParams();
        doReturn(emptySymphonyUuid).when(this.request).getSymphonyUuid();

        boolean result = this.handler.executeTask(this.job);

        assertThat(result, is(false));
        verify(this.client, never()).addComponent(any(), any(), any(), any());
        verify(this.response).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.response).addError(anyString());
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.AddNodeToSystemDefinitionTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     *
     * @since 1.0
     */
    @Test
    public void testExecuteTask_should_fail_when_there_is_no_converged_systems()
    {
        doReturn(this.response).when(this.handler).initializeResponse(this.job);
        doReturn(this.request).when(this.job).getInputParams();
        doReturn(this.symphonyUuid).when(this.request).getSymphonyUuid();
        doReturn(Collections.emptyList()).when(this.client).getConvergedSystems();

        boolean result = this.handler.executeTask(this.job);

        assertThat(result, is(false));
        verify(this.client, never()).addComponent(any(), any(), any(), any());
        verify(this.response).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.response).addError(anyString());
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.AddNodeToSystemDefinitionTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     *
     * @since 1.0
     */
    @Test
    public void testExecuteTask_should_fail_when_there_is_no_converged_system()
    {
        doReturn(this.response).when(this.handler).initializeResponse(this.job);
        doReturn(this.request).when(this.job).getInputParams();
        doReturn(this.symphonyUuid).when(this.request).getSymphonyUuid();
        doReturn(this.systems).when(this.client).getConvergedSystems();
        doReturn(Collections.emptyList()).when(this.client).getComponents(any(ComponentsFilter.class));

        boolean result = this.handler.executeTask(this.job);

        assertThat(result, is(false));
        verify(this.client, never()).addComponent(any(), any(), any(), any());
        verify(this.response).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.response).addError(anyString());
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.AddNodeToSystemDefinitionTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     *
     * @since 1.0
     */
    @Test
    public void testExecuteTask_should_fail_when_the_node_is_not_added_to_system_definition()
    {
        String identifier = "identifier-1";

        doReturn(this.response).when(this.handler).initializeResponse(this.job);
        doReturn(this.request).when(this.job).getInputParams();
        doReturn(this.symphonyUuid).when(this.request).getSymphonyUuid();
        doReturn(this.systems).when(this.client).getConvergedSystems();
        doReturn(this.systems).when(this.client).getComponents(any(ComponentsFilter.class));
        doReturn(this.groups).when(this.system).getGroups();
        doNothing().when(this.client).addComponent(any(ConvergedSystem.class), any(Component.class), anyList(), anyString());
        doReturn(this.components).when(this.system).getComponents();
        doReturn(this.identity).when(this.component).getIdentity();
        doReturn(identifier).when(this.identity).getIdentifier();

        boolean result = this.handler.executeTask(this.job);

        assertThat(result, is(false));
        verify(this.client).addComponent(any(), any(), any(), any());
        verify(this.response).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.response).addError(anyString());
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.AddNodeToSystemDefinitionTaskHandler#initializeResponse(com.dell.cpsd.paqx.dne.domain.Job)}.
     */
    @Test
    public void testInitializeResponse_should_successfully_create_the_response()
    {
        doReturn(this.task).when(this.job).getCurrentTask();
        doReturn(this.taskName).when(this.task).getTaskName();
        doReturn(this.stepName).when(this.job).getStep();

        TaskResponse response = this.handler.initializeResponse(this.job);

        assertNotNull(response);
        assertEquals(this.taskName, response.getWorkFlowTaskName());
        assertEquals(Status.IN_PROGRESS, response.getWorkFlowTaskStatus());
    }
}

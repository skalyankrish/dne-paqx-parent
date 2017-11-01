/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.task.handler.preprocess;

import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.domain.WorkflowTask;
import com.dell.cpsd.paqx.dne.domain.node.NodeInventory;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOData;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOProtectionDomain;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOSDS;
import com.dell.cpsd.paqx.dne.domain.vcenter.Host;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.paqx.dne.service.model.FindProtectionDomainTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.NodeExpansionRequest;
import com.dell.cpsd.paqx.dne.service.model.Status;
import com.dell.cpsd.service.engineering.standards.EssValidateProtectionDomainsResponseMessage;
import com.dell.cpsd.service.engineering.standards.ValidProtectionDomain;
import com.dell.cpsd.service.engineering.standards.Warning;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Unit test for the find protection domain task handler.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class FindProtectionDomainTaskHandlerTest
{
    @Mock
    private WorkflowTask task;

    @Mock
    private NodeService nodeService;

    @Mock
    private DataServiceRepository repository;

    @Mock
    private Job job;

    @Mock
    private NodeExpansionRequest nodeExpansionRequest;

    @Mock
    private FindProtectionDomainTaskResponse findProtectionDomainTaskResponse;

    @Mock
    private NodeInventory nodeInventory;

    @Mock
    private ScaleIOProtectionDomain scaleIOProtectionDomain;

    @Mock
    private ScaleIOData scaleIOData;

    @Mock
    private Host host;

    @Mock
    private ScaleIOSDS scaleIOSDS;

    @Mock
    private EssValidateProtectionDomainsResponseMessage essValidateProtectionDomainsResponseMessage;

    @Mock
    private ValidProtectionDomain validProtectionDomain;

    @Mock
    private Warning warning;

    @Mock
    private ComponentEndpointIds componentEndpointIds;

    private FindProtectionDomainTaskHandler handler;
    private String                          nodeInventoryJSON;

    private final String symphonyUuid            = "symphonyuuid-1";
    private final String esxiManagementHostname  = "esxi-host-1";
    private final String esxiManagementIpAddress = "1.2.3.4";
    private final String sdsName                 = esxiManagementHostname + "-ESX";
    private final String protectionDomainId      = "protection-domain-id-1";
    private final String protectionDomainName    = "protection-domain-1";
    private final String taskName                = "findProtectionDomainTask";
    private final String stepName                = "findProtectionDomainStep";

    @Before
    public void setUp() throws Exception
    {
        this.handler = spy(new FindProtectionDomainTaskHandler(this.nodeService, this.repository));

        StringBuilder stringBuilder = new StringBuilder();
        String line;

        try (BufferedReader reader = new BufferedReader(new FileReader("src/test/resources/node_inventory.json")))
        {
            while ((line = reader.readLine()) != null)
            {
                stringBuilder.append(line);
            }
        }

        nodeInventoryJSON = stringBuilder.toString();

        doReturn(Arrays.asList(this.scaleIOProtectionDomain)).when(this.repository).getScaleIoProtectionDomains();
        doReturn(this.nodeExpansionRequest).when(this.job).getInputParams();
        doReturn(this.symphonyUuid).when(this.nodeExpansionRequest).getSymphonyUuid();
        doReturn(this.esxiManagementHostname).when(this.nodeExpansionRequest).getEsxiManagementHostname();
        doReturn(this.esxiManagementIpAddress).when(this.nodeExpansionRequest).getEsxiManagementIpAddress();
        doReturn(this.nodeInventory).when(this.repository).getNodeInventory(anyString());
        doReturn(this.nodeInventoryJSON).when(this.nodeInventory).getNodeInventory();
        doReturn(Arrays.asList(this.scaleIOData)).when(this.nodeService).listScaleIOData();
        doReturn(Arrays.asList(this.host)).when(this.repository).getVCenterHosts();
        doReturn(Arrays.asList(this.scaleIOProtectionDomain)).when(this.scaleIOData).getProtectionDomains();
        doReturn(this.protectionDomainId).when(this.scaleIOProtectionDomain).getId();
        doReturn(this.protectionDomainName).when(this.scaleIOProtectionDomain).getName();
        doReturn("protection-domain-state").when(this.scaleIOProtectionDomain).getProtectionDomainState();
        doReturn(Arrays.asList(this.scaleIOSDS)).when(this.scaleIOProtectionDomain).getSdsList();
        doReturn("sds-id-1").when(this.scaleIOSDS).getId();
        doReturn(this.sdsName).when(this.scaleIOSDS).getName();
        doReturn(this.esxiManagementHostname).when(this.host).getName();
        doReturn(this.essValidateProtectionDomainsResponseMessage).when(this.nodeService).validateProtectionDomains(any());
    }

    @Test
    public void executeTask_should_successfully_find_an_existing_protection_domain() throws Exception
    {
        doReturn(this.findProtectionDomainTaskResponse).when(this.handler).initializeResponse(any());
        doReturn(Arrays.asList(this.validProtectionDomain)).when(this.essValidateProtectionDomainsResponseMessage)
                .getValidProtectionDomains();
        doReturn(this.protectionDomainId).when(this.validProtectionDomain).getProtectionDomainID();
        doReturn(Arrays.asList(this.warning)).when(this.validProtectionDomain).getWarningMessages();
        doReturn("this is a warning").when(this.warning).getMessage();

        final boolean result = this.handler.executeTask(this.job);

        assertTrue(result);
        verify(this.findProtectionDomainTaskResponse).setWorkFlowTaskStatus(Status.SUCCEEDED);
        verify(this.findProtectionDomainTaskResponse, never()).addError(anyString());
        ArgumentCaptor<String> setProtectionDomainIdCaptor = ArgumentCaptor.forClass(String.class);
        verify(this.findProtectionDomainTaskResponse).setProtectionDomainId(setProtectionDomainIdCaptor.capture());
        ArgumentCaptor<String> setProtectionDomainNameCaptor = ArgumentCaptor.forClass(String.class);
        verify(this.findProtectionDomainTaskResponse).setProtectionDomainName(setProtectionDomainNameCaptor.capture());
    }

    @Test
    public void executeTask_should_successfully_create_a_new_protection_domain_if_a_valid_one_cannot_be_found() throws Exception
    {
        doReturn(this.findProtectionDomainTaskResponse).when(this.handler).initializeResponse(any());
        doReturn(Collections.emptyList()).when(this.essValidateProtectionDomainsResponseMessage).getValidProtectionDomains();

        final boolean result = this.handler.executeTask(this.job);

        assertFalse(result);
        verify(this.findProtectionDomainTaskResponse).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.findProtectionDomainTaskResponse).addError(anyString());
    }

    @Test
    public void executeTask_should_fail_the_workflow_if_the_component_endpoint_ids_are_null() throws Exception
    {
        doReturn(this.findProtectionDomainTaskResponse).when(this.handler).initializeResponse(any());
        doReturn(Collections.emptyList()).when(this.essValidateProtectionDomainsResponseMessage).getValidProtectionDomains();

        final boolean result = this.handler.executeTask(this.job);

        assertFalse(result);
        verify(this.findProtectionDomainTaskResponse).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.findProtectionDomainTaskResponse).addError(anyString());
        verify(this.findProtectionDomainTaskResponse, never()).setProtectionDomainId(anyString());
        verify(this.findProtectionDomainTaskResponse, never()).setProtectionDomainName(anyString());
    }

    @Test
    public void executeTask_should_fail_the_workflow_when_creating_the_protection_domain_returns_an_empty_protection_domain_id() throws Exception
    {
        doReturn(this.findProtectionDomainTaskResponse).when(this.handler).initializeResponse(any());
        doReturn(Collections.emptyList()).when(this.essValidateProtectionDomainsResponseMessage).getValidProtectionDomains();

        final boolean result = this.handler.executeTask(this.job);

        assertFalse(result);
        verify(this.findProtectionDomainTaskResponse).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.findProtectionDomainTaskResponse).addError(anyString());
        verify(this.findProtectionDomainTaskResponse, never()).setProtectionDomainId(anyString());
        verify(this.findProtectionDomainTaskResponse, never()).setProtectionDomainName(anyString());
    }

    @Test
    public void testInitializeResponse_should_successfully_initialize_the_task_response()
    {
        doReturn(this.task).when(this.job).getCurrentTask();
        doReturn(this.taskName).when(this.task).getTaskName();
        doReturn(this.stepName).when(this.job).getStep();

        FindProtectionDomainTaskResponse response = this.handler.initializeResponse(this.job);

        assertNotNull(response);
        assertEquals(this.taskName, response.getWorkFlowTaskName());
        assertEquals(Status.IN_PROGRESS, response.getWorkFlowTaskStatus());
    }
}
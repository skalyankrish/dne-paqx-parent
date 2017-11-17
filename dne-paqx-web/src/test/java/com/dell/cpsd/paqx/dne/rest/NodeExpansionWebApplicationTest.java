/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */
package com.dell.cpsd.paqx.dne.rest;

import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.rest.controller.NodeExpansionController;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.model.DiscoveredNode;
import com.dell.cpsd.paqx.dne.service.model.NodeExpansionRequest;
import com.dell.cpsd.paqx.dne.service.orchestration.IOrchestrationService;
import com.dell.cpsd.paqx.dne.service.workflow.addnode.IAddNodeService;
import com.dell.cpsd.paqx.dne.service.workflow.preprocess.IPreProcessService;
import com.dell.cpsd.virtualization.capabilities.api.ClusterInfo;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class NodeExpansionWebApplicationTest
{

    @Mock
    IAddNodeService addNodeServiceUnderTest;

    @Mock
    IPreProcessService preProcessService;

    @Mock
    IOrchestrationService orchestrationService;

    @Mock
    NodeService nodeService;

    @InjectMocks
    private NodeExpansionController nodeExpansionController;

    private MockMvc mockMvc;

    private String json;

    /**
     * This sets up the test.
     *
     * @since   1.0
     */
    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);

         mockMvc = MockMvcBuilders
                .standaloneSetup(nodeExpansionController)
                .build();

        NodeExpansionRequest request = new NodeExpansionRequest();
        request.setEsxiManagementIpAddress("1.1.1.1");
        request.setEsxiManagementGatewayIpAddress("2.2.2.2");
        request.setEsxiManagementHostname("vCenter_1_2_3_4");
        request.setIdracGatewayIpAddress("3.3.3.3");
        request.setIdracIpAddress("4.4.4.4");
        request.setIdracSubnetMask("5.55.5.5");
        request.setEsxiManagementSubnetMask("6.66.6.6");
        request.setScaleIoData1SvmSubnetMask("7.7.7.7");
        request.setScaleIoData1SvmIpAddress("9.99.9.9");
        request.setSymphonyUuid(UUID.randomUUID().toString());

        Gson gson = new Gson();
        json = gson.toJson(request);
    }

    @Test
    public void verifyRequestParams(){
        NodeExpansionRequest params = new NodeExpansionRequest();

        params.setEsxiManagementIpAddress("1.1.1.1");
        assertNotNull(params.getEsxiManagementIpAddress());

        params.setEsxiManagementGatewayIpAddress("1.1.1.1");
        assertNotNull(params.getEsxiManagementGatewayIpAddress());

        params.setEsxiManagementHostname("vCenter_1_2_3_4");
        assertNotNull(params.getEsxiManagementHostname());

        params.setIdracIpAddress("1.1.1.1");
        assertNotNull(params.getIdracIpAddress());

        params.setEsxiManagementSubnetMask("1.1.1.1");
        assertNotNull(params.getEsxiManagementSubnetMask());

        params.setScaleIoData1SvmSubnetMask("1.1.1.1");
        assertNotNull(params.getScaleIoData1SvmSubnetMask());

        params.setScaleIoData1SvmIpAddress("1.1.1.1");
        assertNotNull(params.getScaleIoData1SvmIpAddress());
    }

    @Test
    public void testAbout() throws Exception{
        this.mockMvc.perform(get("/dne/about")).andExpect(status().isOk()).andExpect(content().string("{\"message\":\"Node Expansion API v0.1\"}"));
    }

    // Some hard coded properteis were added to new NodeInfo() function.
    // need check if they are temp code or not before updating unit test.
    @Ignore
    @Test
    public void testGetNodes() throws  Exception{
        String uuidStr = UUID.randomUUID().toString();
        DiscoveredNode node = new DiscoveredNode(uuidStr, com.dell.cpsd.DiscoveredNode.AllocationStatus.ADDED, "testserial" , "testproduct", "testvendor");
        Mockito.when(nodeService.listDiscoveredNodes()).thenReturn(Collections.singletonList(node));
        MvcResult result = this.mockMvc.perform(get("/dne/nodes"))
                .andExpect(status().isOk()).andReturn();
        assertEquals("[{\"symphonyUuid\":\"" + uuidStr+ "\",\"nodeId\":\"TestNode\",\"nodeStatus\":\"ADDED\"}]", result.getResponse().getContentAsString());
        verify(nodeService, times(1)).listDiscoveredNodes();
        verifyNoMoreInteractions(nodeService);
    }

    @Test
    public void testGetClusters() throws  Exception{
        String uuidStr = UUID.randomUUID().toString();
        ClusterInfo cluster = new ClusterInfo("TestCluster", 10);
        Mockito.when(nodeService.listClusters()).thenReturn(Collections.singletonList(cluster));
        MvcResult result = this.mockMvc.perform(get("/dne/clusters"))
                .andExpect(status().isAccepted()).andReturn();
        assertEquals("[{\"name\":\"TestCluster\",\"numberOfHosts\":10}]", result.getResponse().getContentAsString());
        verify(nodeService, times(1)).listClusters();
        verifyNoMoreInteractions(nodeService);
    }

    @Test
    public void testPostPreProcess() throws  Exception{
        Job mockJob = new Job(UUID.randomUUID(), "testPreProess", "startAddNodeWorkflow", "status1", new HashMap<>());
        Mockito.when(preProcessService.createWorkflow(anyString(), anyString(), anyString())).thenReturn(mockJob);

        this.mockMvc.perform(post("/dne/preprocess")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());

    }
    @Test
    public void getInvalidNodeJobIdForPreprocess() throws Exception {

        Mockito.when(preProcessService.findJob(Mockito.any(UUID.class))).thenReturn(null);
        UUID jobId = UUID.randomUUID();

        this.mockMvc.perform(get("/dne/preprocess/{jobId}", jobId)
                .param("jobId", jobId.toString())
                .param("servletRequest", ""))
                .andExpect(status().isNotFound());
        verify(preProcessService, times(1)).findJob(jobId);
        verifyNoMoreInteractions(preProcessService);
    }

    @Test
    public void getInvalidNodeJobIdForPreprocess_StringJobId() throws Exception {

        Mockito.when(preProcessService.findJob(Mockito.any(UUID.class))).thenReturn(null);
        final String jobId = "anyString";

        this.mockMvc.perform(get("/dne/preprocess/{jobId}", jobId)
                .param("jobId", jobId)
                .param("servletRequest", ""))
                .andExpect(status().isNotFound());
        verify(preProcessService, times(0)).findJob(null);
        verifyNoMoreInteractions(preProcessService);
    }


    @Test
    public void getGetNodeJobIdForPreprocess() throws Exception {
        Job mockJob = new Job(UUID.randomUUID(), "test", "startAddNodeWorkflow", "status1", new HashMap<>());
        Mockito.when(preProcessService.findJob(Mockito.any(UUID.class))).thenReturn(mockJob);
        UUID jobId = mockJob.getId();

        this.mockMvc.perform(get("/dne/preprocess/{jobId}", jobId)
                .param("jobId", jobId.toString())
                .param("servletRequest", ""))
                .andExpect(status().isOk());
        verify(preProcessService, times(1)).findJob(jobId);
        verify(preProcessService, times(1)).makeNodeExpansionResponse(mockJob);
        verifyNoMoreInteractions(preProcessService);
    }

    @Test
    public void postStepForPreprocess() throws Exception {
        String stepName = "testStepWorkflow";
        Job mockJob = new Job(UUID.randomUUID(), "test", stepName, "status1", new HashMap<>());
        Mockito.when(preProcessService.createWorkflow(anyString(),anyString(),anyString())).thenReturn(mockJob);

        this.mockMvc.perform(post("/dne/preprocess/step/{stepName}", stepName)
                .param("stepName", stepName)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }

    @Test
    public void postInvalidStepForPreprocess() throws Exception {
        Mockito.when(preProcessService.createWorkflow(anyString(),anyString(),anyString())).thenReturn(null);


        this.mockMvc.perform(post("/dne/preprocess/step/{stepName}", "invalidStepName")
                .param("stepName", "invalidStepName")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isNotFound());

    }


    @Test
    public void testPostNodes() throws Exception {
        Job mockJob = new Job(UUID.randomUUID(), "test", "startAddNodeWorkflow", "status1", new HashMap<>());
        Mockito.when(addNodeServiceUnderTest.createWorkflow(anyString(), anyString(),anyString())).thenReturn(mockJob);


        this.mockMvc.perform(post("/dne/nodes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }


    @Test
    public void getInvalidNodeJobIdForNodes() throws Exception {

        Mockito.when(addNodeServiceUnderTest.findJob(Mockito.any(UUID.class))).thenReturn(null);
        UUID jobId = UUID.randomUUID();

        this.mockMvc.perform(get("/dne/nodes/{jobId}", jobId)
                .param("jobId", jobId.toString())
                .param("servletRequest", ""))
                .andExpect(status().isNotFound());
        verify(addNodeServiceUnderTest, times(1)).findJob(jobId);
        verifyNoMoreInteractions(addNodeServiceUnderTest);
    }

    @Test
    public void getInvalidNodeJobIdForNodes_StringJobId() throws Exception {

        Mockito.when(addNodeServiceUnderTest.findJob(Mockito.any(UUID.class))).thenReturn(null);
        final String jobId = "anyString";

        this.mockMvc.perform(get("/dne/nodes/{jobId}", jobId)
                .param("jobId", jobId)
                .param("servletRequest", ""))
                .andExpect(status().isNotFound());
        verify(addNodeServiceUnderTest, times(0)).findJob(null);
        verifyNoMoreInteractions(addNodeServiceUnderTest);
    }

    @Test
    public void getGetNodeJobIdForNodes() throws Exception {
        Job mockJob = new Job(UUID.randomUUID(), "test", "startAddNodeWorkflow", "status1", new HashMap<>());
        Mockito.when(addNodeServiceUnderTest.findJob(Mockito.any(UUID.class))).thenReturn(mockJob);
        UUID jobId = mockJob.getId();

        this.mockMvc.perform(get("/dne/nodes/{jobId}", jobId)
                .param("jobId", jobId.toString())
                .param("servletRequest", ""))
                .andExpect(status().isOk());
        verify(addNodeServiceUnderTest, times(1)).findJob(jobId);
        verify(addNodeServiceUnderTest, times(1)).makeNodeExpansionResponse(mockJob);
        verifyNoMoreInteractions(addNodeServiceUnderTest);
    }

    @Test
    public void postStepForNodes() throws Exception {
        String stepName = "testStepWorkflow";
        Job mockJob = new Job(UUID.randomUUID(), "test", stepName, "status1", new HashMap<>());
        Mockito.when(addNodeServiceUnderTest.createWorkflow(anyString(),anyString(),anyString())).thenReturn(mockJob);

        this.mockMvc.perform(post("/dne/nodes/step/{stepName}", stepName)
                .param("stepName", stepName)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());

    }

    @Test
    public void postInvalidStepForNodes() throws Exception {
        Mockito.when(addNodeServiceUnderTest.createWorkflow(anyString(),anyString(),anyString())).thenReturn(null);

        this.mockMvc.perform(post("/dne/nodes/step/{stepName}", "invalidStepName")
                .param("stepName", "invalidStepName")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isNotFound());

    }
}
/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.workflow.preprocess;

import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.domain.WorkflowTask;
import com.dell.cpsd.paqx.dne.repository.InMemoryJobRepository;
import com.dell.cpsd.paqx.dne.service.WorkflowServiceImpl;
import com.dell.cpsd.paqx.dne.service.model.Step;


import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class PreProcessServiceImplTest {

    private PreProcessService   preProcessServiceUnderTest;
    private Map<String, WorkflowTask> workFlowTasks;
    @Before
    public void setUp() throws Exception
    {
        final InMemoryJobRepository inMemoryJobRepository = new InMemoryJobRepository();

        final Map<String, Step> workflowSteps = new HashMap<>();

        workflowSteps.put("startPreProcessWorkflow", new Step("completed", true));
        workflowSteps.put("completed", null);

        WorkflowServiceImpl workflowServiceUnderTest = new WorkflowServiceImpl(inMemoryJobRepository, workflowSteps);

        this.preProcessServiceUnderTest = new PreProcessService();
        preProcessServiceUnderTest.setWorkflowService(workflowServiceUnderTest);

        workFlowTasks = preProcessServiceUnderTest.preProcessWorkflowTasks();
    }

    @Test
    public void testCreateWorkflow()
    {

        Job initialJob = preProcessServiceUnderTest.createWorkflow(
                "PreProcess", "startPreProcessWorkflow", "status1");

        assertNotNull(initialJob);
        assertNotNull(initialJob.getId());
    }

    @Test
    public void testFindJob()
    {
        Job initialJob = preProcessServiceUnderTest.createWorkflow(
                "PreProcess", "startPreProcessWorkflow", "status1");

        Job foundJob = preProcessServiceUnderTest.findJob(initialJob.getId());
        assertNotNull(initialJob);
        assertNotNull(foundJob);
        assertEquals(initialJob, foundJob);
    }

    @Test
    public void testPreProcessWorkFlowTask_setup()
    {
        assertNotNull(workFlowTasks);
        assertThat(workFlowTasks.size(), greaterThan(0));
    }

    @Test
    public void testTaskName_configIdrac()
    {
        assertEquals("Configure server out of band IP address", workFlowTasks.get("configIdrac").getTaskName());
    }

    @Test
    public void testTaskName_listScaleIoComponents()
    {
        assertEquals("List ScaleIO components", workFlowTasks.get("listScaleIoComponents").getTaskName());
    }

    @Test
    public void testTaskName_listVCenterComponentsTask()
    {
        assertEquals("List vCenter components", workFlowTasks.get("listVCenterComponents").getTaskName());
    }

    @Test
    public void testTaskName_discoverScaleIo()
    {
        assertEquals("Discover ScaleIO", workFlowTasks.get("discoverScaleIo").getTaskName());
    }

    @Test
    public void testTaskName_discoverVCenter()
    {
        assertEquals("Discover vCenter", workFlowTasks.get("discoverVCenter").getTaskName());
    }

    @Test
    public void testTaskName_configureObmSettings()
    {
        assertEquals("Configure out of band management settings", workFlowTasks.get("configureObmSettings").getTaskName());
    }

    @Test
    public void testTaskName_pingIdrac()
    {
        assertEquals("Ping server out of band IP address", workFlowTasks.get("pingIdrac").getTaskName());
    }

    @Test
    public void testTaskName_findVCluster() {
        assertEquals("Find vCenter cluster", workFlowTasks.get("findVCluster").getTaskName());
    }

    @Test
    public void testTaskName_findScaleIO() {
        assertEquals("Find or create valid storage pool", workFlowTasks.get("findOrCreateValidStoragePool").getTaskName());
    }

    @Test
    public void testTaskName_findProtectionDomain() {
        assertEquals("Find or create protection domain", workFlowTasks.get("findProtectionDomain").getTaskName());
    }

    @Test
    public void testTaskName_changeIdracCredentials() {
        assertEquals("Change out of band management credentials", workFlowTasks.get("changeIdracCredentials").getTaskName());
    }
}

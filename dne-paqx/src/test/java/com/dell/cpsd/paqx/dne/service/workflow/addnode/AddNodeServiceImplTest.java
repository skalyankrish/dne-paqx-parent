/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.workflow.addnode;

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

public class AddNodeServiceImplTest
{
    private AddNodeService            addNodeServiceUnderTest;
    private Map<String, WorkflowTask> workFlowTasks;

    @Before
    public void setUp()
    {
        final InMemoryJobRepository inMemoryJobRepository = new InMemoryJobRepository();

        final Map<String, Step> workflowSteps = new HashMap<>();

        workflowSteps.put("startAddNodeWorkflow", new Step("completed", true));
        workflowSteps.put("completed", null);

        WorkflowServiceImpl workflowServiceUnderTest = new WorkflowServiceImpl(inMemoryJobRepository, workflowSteps);

        this.addNodeServiceUnderTest = new AddNodeService();
        addNodeServiceUnderTest.setWorkflowService(workflowServiceUnderTest);

        workFlowTasks = addNodeServiceUnderTest.addNodeWorkflowTasks();
    }

    @Test
    public void testCreateWorkflow()
    {
        Job initialJob = addNodeServiceUnderTest.createWorkflow("addNode", "startAddNodeWorkflow", "status1");

        assertNotNull(initialJob);
        assertNotNull(initialJob.getId());
    }

    @Test
    public void testFindJob()
    {
        Job initialJob = addNodeServiceUnderTest.createWorkflow("addNode", "startAddNodeWorkflow", "status1");

        Job foundJob = addNodeServiceUnderTest.findJob(initialJob.getId());
        assertNotNull(initialJob);
        assertNotNull(foundJob);
        assertEquals(initialJob, foundJob);
    }

    @Test
    public void testAddNodeWorkFlowTasks_setup()
    {
        assertNotNull(workFlowTasks);
        assertThat(workFlowTasks.size(), greaterThan(0));
    }

    @Test
    public void testTaskName_addHostToVcenter()
    {
        assertEquals("Add host to vCenter cluster", workFlowTasks.get("addHostToVcenter").getTaskName());
    }

    @Test
    public void testTaskName_applyEsxiLicense()
    {
        assertEquals("Apply ESXi license", workFlowTasks.get("applyEsxiLicense").getTaskName());
    }

    @Test
    public void testTaskName_updateSoftwareAcceptance()
    {
        assertEquals("Update software acceptance", workFlowTasks.get("updateSoftwareAcceptance").getTaskName());
    }

    @Test
    public void testTaskName_installScaleIoVib()
    {
        assertEquals("Install SDC vSphere Installation Bundle (VIB)", workFlowTasks.get("installScaleIoVib").getTaskName());
    }

    @Test
    public void testTaskName_configureScaleIoVib()
    {
        assertEquals("Configure SDC vSphere Installation Bundle (VIB)", workFlowTasks.get("configureScaleIoVib").getTaskName());
    }

    @Test
    public void testTaskName_enterHostMaintenanceMode()
    {
        assertEquals("Enter host maintenance mode", workFlowTasks.get("enterHostMaintenanceMode").getTaskName());
    }

    @Test
    public void testTaskName_addHostToDvSwitch()
    {
        assertEquals("Add ESXi host to cluster DVSwitch", workFlowTasks.get("addHostToDvSwitch").getTaskName());
    }

    @Test
    public void testTaskName_deploySVM()
    {
        assertEquals("Clone and deploy ScaleIO VM", workFlowTasks.get("deploySVM").getTaskName());
    }

    @Test
    public void testTaskName_powerOnSVM()
    {
        assertEquals("Power on the ScaleIO VM", workFlowTasks.get("powerOnSVM").getTaskName());
    }

    @Test
    public void testTaskName_enablePciPassthroughHost()
    {
        assertEquals("Enable PCI passthrough ESXi host", workFlowTasks.get("enablePciPassthroughHost").getTaskName());
    }

    @Test
    public void testTaskName_rebootHost()
    {
        assertEquals("Reboot Host", workFlowTasks.get("rebootHost").getTaskName());
    }

    @Test
    public void testTaskName_setPciPassthroughSioVmTask()
    {
        assertEquals("Configure PCI passthrough ScaleIO VM", workFlowTasks.get("setPciPassthroughSioVm").getTaskName());
    }

    @Test
    public void testTaskName_datastoreRename()
    {
        assertEquals("Rename datastore", workFlowTasks.get("datastoreRename").getTaskName());
    }

    @Test
    public void testTaskName_configureVmNetworkSettings()
    {
        assertEquals("Configure ScaleIO VM network settings", workFlowTasks.get("configureVmNetworkSettings").getTaskName());
    }

    @Test
    public void testTaskName_changeSvmCredentials()
    {
        assertEquals("Change ScaleIO VM credentials", workFlowTasks.get("changeSvmCredentials").getTaskName());
    }

    @Test
    public void testTaskName_installSvmPackages()
    {
        assertEquals("Install SDS and Light Installation Agent (LIA) packages", workFlowTasks.get("installSvmPackages").getTaskName());
    }

    @Test
    public void testTaskName_performanceTuneSvm()
    {
        assertEquals("Performance tune the ScaleIO VM", workFlowTasks.get("performanceTuneSvm").getTaskName());
    }

    @Test
    public void testTaskName_addHostToProtectionDomain()
    {
        assertEquals("Add host to protection domain", workFlowTasks.get("addHostToProtectionDomain").getTaskName());
    }

    @Test
    public void testTaskName_updateSdcPerformanceProfile()
    {
        assertEquals("Configure SDC profile for high performance", workFlowTasks.get("updateSdcPerformanceProfile").getTaskName());
    }

    @Test
    public void testTaskName_findConfigurePxeBoot()
    {
        assertEquals("Configure PXE boot", workFlowTasks.get("configurePxeBoot").getTaskName());
    }

    @Test
    public void testTaskName_updateSystemDefinitionTask()
    {
        assertEquals("Update System Definition", workFlowTasks.get("updateSystemDefinition").getTaskName());
    }

    @Test
    public void testTaskName_notifyNodeDiscoveryToUpdateStatus()
    {
        assertEquals("Notify node discovery to update status", workFlowTasks.get("notifyNodeDiscoveryToUpdateStatus").getTaskName());
    }
}

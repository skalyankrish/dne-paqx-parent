/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.workflow.preprocess;

import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.domain.WorkflowTask;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.BaseService;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.WorkflowService;
import com.dell.cpsd.paqx.dne.service.model.NodeExpansionResponse;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.FindDiscoveredNodesTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.preprocess.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Preprocess service.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
@Service
public class PreProcessService extends BaseService implements IPreProcessService
{
    @Autowired
    @Qualifier("preProcessWorkflowService")
    private WorkflowService workflowService;

    @Autowired
    private NodeService nodeService;

    @Autowired
    private DataServiceRepository repository;

    private static final int PING_TIMEOUT = 120000; // 120 seconds

    @Bean("findDiscoveredNodesTask")
    private WorkflowTask findDiscoveredNodesTask()
    {
        return createTask("Finding discovered Nodes", new FindDiscoveredNodesTaskHandler(nodeService));
    }

    @Bean("configureObmSettingsTask")
    private WorkflowTask configureObmSettingsTask(){
        return createTask("Configuring Obm Settings", new ConfigureObmSettingsTaskHandler(nodeService));
    }

    @Bean("configIdracTask")
    private WorkflowTask configIdracTask()
    {
        return createTask("Configuring Out of Band Management", new ConfigIdracTaskHandler(nodeService));
    }

    @Bean("pingIdracTask")
    private WorkflowTask pingIdracTask()
    {
        return createTask("Ping iDRAC IP Address", new PingIdracTaskHandler(PING_TIMEOUT));
    }

    @Bean("configureBootDeviceIdrac")
    private WorkflowTask configureBootDeviceIdrac()
    {
        return createTask("Configure Boot Device Idrac", new ConfigureBootDeviceIdracTaskHandler(nodeService));
    }

    @Bean("findVClusterTask")
    public WorkflowTask createVClusterTask()
    {
        return createTask("Find VCluster", new FindVClusterTaskHandler(nodeService));
    }

    @Bean("discoverNodeInventory")
    public WorkflowTask discoverNodeInventory()
    {
        return createTask("Discover Rackhd Node Inventory", new DiscoverNodeInventoryHandler(nodeService, repository));
    }

    @Bean("findScaleIO")
    public WorkflowTask createFindScaleIOTask()
    {
        return createTask("Find ScaleIO", new FindScaleIOTaskHandler(nodeService));
    }

    @Bean("findProtectionDomainTask")
    public WorkflowTask findProtectionDomainTask()
    {
        return createTask("Find ProtectionDomain", new FindProtectionDomainTaskHandler(workflowService));
    }

    @Bean("listScaleIoComponentsTask")
    private WorkflowTask listScaleIoComponentsTask()
    {
        return createTask("List ScaleIO Components", new ListScaleIoComponentsTaskHandler(this.nodeService));
    }

    @Bean("listVCenterComponentsTask")
    private WorkflowTask listVCenterComponentsTask()
    {
        return createTask("List VCenter Components", new ListVCenterComponentsTaskHandler(this.nodeService));
    }

    @Bean("discoverScaleIoTask")
    private WorkflowTask discoverScaleIoTask()
    {
        return createTask("Discover ScaleIO", new DiscoverScaleIoTaskHandler(this.nodeService, repository));
    }

    @Bean("discoverVCenterTask")
    private WorkflowTask discoverVCenterTask()
    {
        return createTask("Discover VCenter", new DiscoverVCenterTaskHandler(this.nodeService, repository));
    }

    @Bean("preProcessWorkflowTasks")
    public Map<String, WorkflowTask> preProcessWorkflowTasks()
    {
        final Map<String, WorkflowTask> workflowTasks = new HashMap<>();

        workflowTasks.put("findAvailableNodes", findDiscoveredNodesTask());
        workflowTasks.put("listScaleIoComponents", listScaleIoComponentsTask());
        workflowTasks.put("listVCenterComponents", listVCenterComponentsTask());
        workflowTasks.put("discoverScaleIo", discoverScaleIoTask());
        workflowTasks.put("discoverVCenter", discoverVCenterTask());
        workflowTasks.put("configureObmSettings", configureObmSettingsTask());
        workflowTasks.put("configIdrac", configIdracTask());
        workflowTasks.put("pingIdrac", pingIdracTask());
        workflowTasks.put("configureBootDeviceIdrac", configureBootDeviceIdrac());
        workflowTasks.put("findScaleIO", createFindScaleIOTask());
        workflowTasks.put("findVCluster", createVClusterTask());
        workflowTasks.put("discoverNodeInventory", discoverNodeInventory());
        //workflowTasks.put("findProtectionDomain", findProtectionDomainTask());
        return workflowTasks;
    }

    @Override
    public Job createWorkflow(final String workflowType, final String startingStep, final String currentStatus)
    {

        Job job = workflowService.createWorkflow(workflowType, startingStep, currentStatus, preProcessWorkflowTasks());
        return job;
    }

    public Job findJob(UUID jobId)
    {
        final Job job = workflowService.findJob(jobId);

        return job;
    }

    public NodeExpansionResponse makeNodeExpansionResponse(final Job job)
    {
        return makeNodeExpansionResponse(job, workflowService);
    }

    public WorkflowService getWorkflowService()
    {
        return workflowService;
    }

    public void setWorkflowService(WorkflowService workflowService)
    {
        this.workflowService = workflowService;
    }
}

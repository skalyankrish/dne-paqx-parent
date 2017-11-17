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
import com.dell.cpsd.paqx.dne.service.task.handler.preprocess.ChangeIdracCredentialsTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.preprocess.CleanInMemoryDatabaseTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.preprocess.ConfigIdracTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.preprocess.ConfigureObmSettingsTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.preprocess.DiscoverScaleIoTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.preprocess.DiscoverVCenterTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.preprocess.FindOrCreateValidStoragePoolTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.preprocess.FindProtectionDomainTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.preprocess.FindVClusterTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.preprocess.ListScaleIoComponentsTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.preprocess.ListVCenterComponentsTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.preprocess.PingIdracTaskHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${obm.services}")
    private String[] obmServices;

    private static final int PING_IDRAC_TIMEOUT = 120000; // 120 seconds

    @Bean("cleanInMemoryDatabaseTask")
    private WorkflowTask cleanInMemoryDatabase()
    {
        return createTask("Clean in memory database", new CleanInMemoryDatabaseTaskHandler(this.nodeService, this.repository));
    }

    @Bean("changeIdracCredentialsTask")
    private WorkflowTask changeIdracCredentialsTask()
    {
        return createTask("Change out of band management credentials", new ChangeIdracCredentialsTaskHandler(this.nodeService));
    }

    @Bean("configureObmSettingsTask")
    private WorkflowTask configureObmSettingsTask(){
        return createTask("Configure out of band management settings", new ConfigureObmSettingsTaskHandler(nodeService, obmServices));
    }

    @Bean("configIdracTask")
    private WorkflowTask configIdracTask()
    {
        return createTask("Configure server out of band IP address", new ConfigIdracTaskHandler(nodeService));
    }

    @Bean("pingIdracTask")
    private WorkflowTask pingIdracTask()
    {
        return createTask("Ping server out of band IP address", new PingIdracTaskHandler(PING_IDRAC_TIMEOUT));
    }

    @Bean("findVClusterTask")
    public WorkflowTask createVClusterTask()
    {
        return createTask("Find vCenter cluster", new FindVClusterTaskHandler(nodeService));
    }

    @Bean("findOrCreateValidStoragePoolTask")
    public WorkflowTask findOrCreateValidStoragePoolTask()
    {
        return createTask("Find or create valid storage pool", new FindOrCreateValidStoragePoolTaskHandler(nodeService));
    }

    @Bean("findProtectionDomainTask")
    public WorkflowTask findProtectionDomainTask()
    {
        return createTask("Find or create protection domain", new FindProtectionDomainTaskHandler(nodeService, repository));
    }

    @Bean("listScaleIoComponentsTask")
    private WorkflowTask listScaleIoComponentsTask()
    {
        return createTask("List ScaleIO components", new ListScaleIoComponentsTaskHandler(this.nodeService));
    }

    @Bean("listVCenterComponentsTask")
    private WorkflowTask listVCenterComponentsTask()
    {
        return createTask("List vCenter components", new ListVCenterComponentsTaskHandler(this.nodeService));
    }

    @Bean("discoverScaleIoTask")
    private WorkflowTask discoverScaleIoTask()
    {
        return createTask("Discover ScaleIO", new DiscoverScaleIoTaskHandler(this.nodeService, repository));
    }

    @Bean("discoverVCenterTask")
    private WorkflowTask discoverVCenterTask()
    {
        return createTask("Discover vCenter", new DiscoverVCenterTaskHandler(this.nodeService, repository));
    }

    @Bean("preProcessWorkflowTasks")
    public Map<String, WorkflowTask> preProcessWorkflowTasks()
    {
        final Map<String, WorkflowTask> workflowTasks = new HashMap<>();

        workflowTasks.put("cleanInMemoryDatabase", cleanInMemoryDatabase());
        workflowTasks.put("changeIdracCredentials", changeIdracCredentialsTask());
        workflowTasks.put("listScaleIoComponents", listScaleIoComponentsTask());
        workflowTasks.put("listVCenterComponents", listVCenterComponentsTask());
        workflowTasks.put("discoverScaleIo", discoverScaleIoTask());
        workflowTasks.put("discoverVCenter", discoverVCenterTask());
        workflowTasks.put("configureObmSettings", configureObmSettingsTask());
        workflowTasks.put("configIdrac", configIdracTask());
        workflowTasks.put("pingIdrac", pingIdracTask());
        workflowTasks.put("findOrCreateValidStoragePool", findOrCreateValidStoragePoolTask());
        workflowTasks.put("findVCluster", createVClusterTask());
        workflowTasks.put("findProtectionDomain", findProtectionDomainTask());
        return workflowTasks;
    }

    @Override
    public Job createWorkflow(final String workflowType, final String startingStep, final String currentStatus)
    {

        return workflowService.createWorkflow(workflowType, startingStep, currentStatus, preProcessWorkflowTasks());
    }

    public Job findJob(UUID jobId)
    {
        return workflowService.findJob(jobId);
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

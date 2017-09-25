/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.workflow.addnode;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.AddHostToDvSwitchTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.AddHostToVCenterTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.ApplyEsxiLicenseTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.ConfigureScaleIoVibTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.DatastoreRenameTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.DeployScaleIoVmTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.EnablePciPassthroughTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.EnterHostMaintenanceModeTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.ExitHostMaintenanceModeTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.InstallEsxiTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.InstallScaleIoVibTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.ListESXiCredentialDetailsTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.RebootHostTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.UpdatePciPassThroughTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.UpdateSoftwareAcceptanceTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.ConfigurePxeBootTaskHandler;
import com.dell.cpsd.paqx.dne.transformers.HostToInstallEsxiRequestTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.domain.WorkflowTask;
import com.dell.cpsd.paqx.dne.service.BaseService;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.WorkflowService;
import com.dell.cpsd.paqx.dne.service.model.NodeExpansionResponse;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.AddNodeToSystemDefinitionTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.ChangeIdracCredentialsTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.NotifyNodeDiscoveryToUpdateStatusTaskHandler;
import com.dell.cpsd.sdk.AMQPClient;

/**
 * Add node workflow service.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@Service
public class AddNodeService extends BaseService implements IAddNodeService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AddNodeService.class);

    @Autowired
    private NodeService nodeService;

    @Autowired
    private AMQPClient sdkAMQPClient;

    @Autowired
    @Qualifier("addNodeWorkflowService")
    private WorkflowService workflowService;

    @Autowired
    private HostToInstallEsxiRequestTransformer hostToInstallEsxiRequestTransformer;

    @Autowired
    private DataServiceRepository repository;

    @Override
    public Job createWorkflow(final String workflowType, final String startingStep, final String currentStatus)
    {
        Job job = workflowService.createWorkflow(workflowType, startingStep, currentStatus, addNodeWorkflowTasks());
        return job;
    }

    @Bean("addNodesWorkflowTasks")
    public Map<String, WorkflowTask> addNodeWorkflowTasks()
    {
        final Map<String, WorkflowTask> workflowTasks = new HashMap<>();

		workflowTasks.put("changeIdracCredentials", changeIdracCredentialsTask());
        workflowTasks.put("installEsxi", installEsxiTask());
        workflowTasks.put("retrieveEsxiDefaultCredentialDetails", esxiCredentialDetailsTask());
        workflowTasks.put("addHostToVcenter", addHostToVcenterTask());
        workflowTasks.put("applyEsxiLicense", applyEsxiLicenseTask());
        workflowTasks.put("updateSoftwareAcceptance", updateSoftwareAcceptanceTask());
        workflowTasks.put("installScaleIoVib", installScaleIoVibTask());
        workflowTasks.put("configureScaleIoVib", configureScaleIoVibTask());
        workflowTasks.put("enterHostMaintenanceMode", enterHostMaintenanceModeTask());
        workflowTasks.put("addHostToDvSwitch", addHostToDvSwitchTask());
        workflowTasks.put("deploySVM", deploySVMTask());
        workflowTasks.put("enablePciPassthroughHost", enablePciPassthroughHostTask());
        workflowTasks.put("rebootHost", rebootHostTask());
        workflowTasks.put("exitHostMaintenanceMode", exitHostMaintenanceModeTask());
        workflowTasks.put("setPciPassthroughSioVm", setPciPassthroughSioVmTask());
        /*workflowTasks.put("installScaleIOSDC", null);
        workflowTasks.put("addNewHostToScaleIO", null);*/
        workflowTasks.put("datastoreRename", datastoreRenameTask());
        workflowTasks.put("configurePxeBoot", configurePxeBoot());
        workflowTasks.put("updateSystemDefinition", updateSystemDefinitionTask());
        workflowTasks.put("notifyNodeDiscoveryToUpdateStatus", notifyNodeDiscoveryToUpdateStatusTask());

        return workflowTasks;
    }

    @Bean("exitHostMaintenanceModeTask")
    private WorkflowTask exitHostMaintenanceModeTask()
    {
        return createTask("Exit Host Maintenance Mode", new ExitHostMaintenanceModeTaskHandler(nodeService, repository));
    }

    @Bean("esxiCredentialDetailsTask")
    private WorkflowTask esxiCredentialDetailsTask()
    {
        return createTask("Retrieve default ESXi host credential details", new ListESXiCredentialDetailsTaskHandler(this.nodeService));
    }

    @Bean("updateSystemDefinitionTask")
    private WorkflowTask updateSystemDefinitionTask()
    {
        return createTask("Update System Definition", new AddNodeToSystemDefinitionTaskHandler(this.sdkAMQPClient));
    }
    
    @Bean("changeIdracCredentialsTask")
    private WorkflowTask changeIdracCredentialsTask()
    {
        return createTask("Change Out of Band Management Credentials", new ChangeIdracCredentialsTaskHandler(this.nodeService));
    }

    @Bean("configurePxeBoot")
    private WorkflowTask configurePxeBoot(){
        return createTask("Configure Pxe boot", new ConfigurePxeBootTaskHandler(nodeService));
    }

    @Bean("installEsxiTask")
    private WorkflowTask installEsxiTask()
    {
        return createTask("Install ESXi", new InstallEsxiTaskHandler(this.nodeService, hostToInstallEsxiRequestTransformer, this.repository));
    }

    @Bean("addHostToVcenterTask")
    private WorkflowTask addHostToVcenterTask()
    {
        return createTask("Add Host to VCenter", new AddHostToVCenterTaskHandler(this.nodeService, repository));
    }

    @Bean("installScaleIoVibTask")
    private WorkflowTask installScaleIoVibTask()
    {
        return createTask("Install ScaleIO VIB", new InstallScaleIoVibTaskHandler(this.nodeService, repository));
    }

    @Bean("configureScaleIoVibTask")
    private WorkflowTask configureScaleIoVibTask()
    {
        return createTask("Configure ScaleIO VIB", new ConfigureScaleIoVibTaskHandler(this.nodeService, repository));
    }

    @Bean("addHostToDvSwitchTask")
    private WorkflowTask addHostToDvSwitchTask()
    {
        return createTask("Add Host to DV Switch", new AddHostToDvSwitchTaskHandler(this.nodeService, repository));
    }

    @Bean("deploySVMTask")
    private WorkflowTask deploySVMTask()
    {
        return createTask("Deploy ScaleIO VM", new DeployScaleIoVmTaskHandler(this.nodeService, repository));
    }

    @Bean("enablePciPassthroughHostTask")
    private WorkflowTask enablePciPassthroughHostTask()
    {
        return createTask("Enable PCI pass through", new EnablePciPassthroughTaskHandler(this.nodeService, repository));
    }

    @Bean("rebootHostTask")
    private WorkflowTask rebootHostTask()
    {
        return createTask("Reboot Host", new RebootHostTaskHandler(this.nodeService, repository));
    }

    @Bean("setPciPassthroughSioVmTask")
    private WorkflowTask setPciPassthroughSioVmTask()
    {
        return createTask("Set PCI Pass through ScaleIO VM", new UpdatePciPassThroughTaskHandler(this.nodeService, repository));
    }

    @Bean("applyEsxiLicenseTask")
    private WorkflowTask applyEsxiLicenseTask()
    {
        return createTask("Apply ESXi License", new ApplyEsxiLicenseTaskHandler(this.nodeService, repository));
    }

    @Bean("notifyNodeDiscoveryToUpdateStatusTask")
    private WorkflowTask notifyNodeDiscoveryToUpdateStatusTask()
    {
        return createTask("Notify Node Discovery To Update Status",
                new NotifyNodeDiscoveryToUpdateStatusTaskHandler(this.nodeService));
    }

    @Bean("datastoreRenameTask")
    private WorkflowTask datastoreRenameTask()
    {
        return createTask("Datastore rename",
                new DatastoreRenameTaskHandler(this.nodeService, this.repository));
    }

    @Bean("updateSoftwareAcceptanceTask")
    private WorkflowTask updateSoftwareAcceptanceTask()
    {
        return createTask("Update software acceptance",
                new UpdateSoftwareAcceptanceTaskHandler(this.nodeService, this.repository));
    }

    @Bean("enterHostMaintenanceModeTask")
    private WorkflowTask enterHostMaintenanceModeTask()
    {
        return createTask("Enter Host Maintenance Mode", new EnterHostMaintenanceModeTaskHandler(nodeService, repository));
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

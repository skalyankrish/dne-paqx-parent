/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.workflow.addnode;

import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.domain.WorkflowTask;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.BaseService;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.WorkflowService;
import com.dell.cpsd.paqx.dne.service.model.NodeExpansionResponse;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.AddHostToDvSwitchTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.AddHostToProtectionDomainTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.AddHostToVCenterTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.AddNodeToSystemDefinitionTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.ApplyEsxiLicenseTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.ChangeSvmCredentialsTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.ConfigurePxeBootTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.ConfigureScaleIoVibTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.ConfigureVmNetworkSettingsTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.DatastoreRenameTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.DeployScaleIoVmTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.EnablePciPassthroughTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.EnterHostMaintenanceModeTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.ExitHostMaintenanceModeTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.InstallScaleIoVibTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.InstallSvmPackagesTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.ListESXiCredentialDetailsTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.NotifyNodeDiscoveryToUpdateStatusTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.PerformanceTuneSvmTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.PowerOnScaleIoVmTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.RebootHostTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.UpdatePciPassThroughTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.UpdateSdcPerformanceProfileTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.UpdateSoftwareAcceptanceTaskHandler;
import com.dell.cpsd.paqx.dne.transformers.HostToInstallEsxiRequestTransformer;
import com.dell.cpsd.sdk.AMQPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Add node workflow service.
 * <p>
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

    @Value("${rackhd.sdc.vib.install.repo.url}")
    private String sdcVibUrl;

    private static final long DEPLOY_SVM_WAIT_TIME              = TimeUnit.SECONDS.toMillis(30L);
    private static final long CHANGE_SVM_CREDENTIALS_WAIT_TIME  = TimeUnit.SECONDS.toMillis(75L);
    private static final long SDC_PERF_PROFILE_UPDATE_WAIT_TIME = TimeUnit.SECONDS.toMillis(15L);
    private static final long ESXI_HOST_PING_TIMEOUT            = TimeUnit.SECONDS.toMillis(240L);

    @Override
    public Job createWorkflow(final String workflowType, final String startingStep, final String currentStatus)
    {
        return workflowService.createWorkflow(workflowType, startingStep, currentStatus, addNodeWorkflowTasks());
    }

    @Bean("addNodesWorkflowTasks")
    public Map<String, WorkflowTask> addNodeWorkflowTasks()
    {
        final Map<String, WorkflowTask> workflowTasks = new HashMap<>();

        workflowTasks.put("retrieveEsxiDefaultCredentialDetails", esxiCredentialDetailsTask());
        workflowTasks.put("addHostToVcenter", addHostToVcenterTask());
        workflowTasks.put("applyEsxiLicense", applyEsxiLicenseTask());
        workflowTasks.put("updateSoftwareAcceptance", updateSoftwareAcceptanceTask());
        workflowTasks.put("installScaleIoVib", installScaleIoVibTask());
        workflowTasks.put("configureScaleIoVib", configureScaleIoVibTask());
        workflowTasks.put("enterHostMaintenanceMode", enterHostMaintenanceModeTask());
        workflowTasks.put("addHostToDvSwitch", addHostToDvSwitchTask());
        workflowTasks.put("deploySVM", deploySVMTask());
        workflowTasks.put("powerOnSVM", powerOnSVMTask());
        workflowTasks.put("enablePciPassthroughHost", enablePciPassthroughHostTask());
        workflowTasks.put("rebootHost", rebootHostTask());
        workflowTasks.put("exitHostMaintenanceMode1", exitHostMaintenanceModeTask());
        workflowTasks.put("exitHostMaintenanceMode2", exitHostMaintenanceModeTask());
        workflowTasks.put("setPciPassthroughSioVm", setPciPassthroughSioVmTask());
        /*workflowTasks.put("installScaleIOSDC", null);
        workflowTasks.put("addNewHostToScaleIO", null);*/
        workflowTasks.put("datastoreRename", datastoreRenameTask());
        workflowTasks.put("configureVmNetworkSettings", configureVmNetworkSettingsTask());
        workflowTasks.put("changeSvmCredentials", changeSvmCredentialsTask());
        workflowTasks.put("installSvmPackages", installSvmPackagesTask());
        workflowTasks.put("performanceTuneSvm", performanceTuneSvmTask());
        workflowTasks.put("addHostToProtectionDomain", addHostToProtectionDomainTask());
        workflowTasks.put("updateSdcPerformanceProfile", updateSdcPerformanceProfileTask());
        workflowTasks.put("configurePxeBoot", configurePxeBootTask());
        workflowTasks.put("updateSystemDefinition", updateSystemDefinitionTask());
        workflowTasks.put("notifyNodeDiscoveryToUpdateStatus", notifyNodeDiscoveryToUpdateStatusTask());

        return workflowTasks;
    }

    @Bean("updateSdcPerformanceProfileTask")
    private WorkflowTask updateSdcPerformanceProfileTask()
    {
        return createTask("Configure SDC profile for high performance",
                new UpdateSdcPerformanceProfileTaskHandler(nodeService, repository, SDC_PERF_PROFILE_UPDATE_WAIT_TIME));
    }

    @Bean("performanceTuneSvmTask")
    private WorkflowTask performanceTuneSvmTask()
    {
        return createTask("Performance tune the ScaleIO VM", new PerformanceTuneSvmTaskHandler(nodeService, repository));
    }

    @Bean("addHostToProtectionDomain")
    private WorkflowTask addHostToProtectionDomainTask()
    {
        return createTask("Add host to protection domain", new AddHostToProtectionDomainTaskHandler(nodeService, repository));
    }

    @Bean("installSvmPackagesTask")
    private WorkflowTask installSvmPackagesTask()
    {
        return createTask("Install SDS and Light Installation Agent (LIA) packages", new InstallSvmPackagesTaskHandler(nodeService, repository));
    }

    @Bean("changeSvmCredentialsTask")
    private WorkflowTask changeSvmCredentialsTask()
    {
        return createTask("Change ScaleIO VM credentials",
                new ChangeSvmCredentialsTaskHandler(nodeService, repository, CHANGE_SVM_CREDENTIALS_WAIT_TIME, ESXI_HOST_PING_TIMEOUT));
    }

    @Bean("configureVmNetworkSettingsTask")
    private WorkflowTask configureVmNetworkSettingsTask()
    {
        return createTask("Configure ScaleIO VM network settings", new ConfigureVmNetworkSettingsTaskHandler(nodeService, repository));
    }

    @Bean("powerOnSVMTask")
    private WorkflowTask powerOnSVMTask()
    {
        return createTask("Power on the ScaleIO VM", new PowerOnScaleIoVmTaskHandler(nodeService, repository, ESXI_HOST_PING_TIMEOUT));
    }

    @Bean("exitHostMaintenanceModeTask")
    private WorkflowTask exitHostMaintenanceModeTask()
    {
        return createTask("Exit host maintenance mode", new ExitHostMaintenanceModeTaskHandler(nodeService, repository));
    }

    @Bean("esxiCredentialDetailsTask")
    private WorkflowTask esxiCredentialDetailsTask()
    {
        return createTask("Retrieve default ESXi host credential details", new ListESXiCredentialDetailsTaskHandler(this.nodeService));
    }

    @Bean("updateSystemDefinitionTask")
    private WorkflowTask updateSystemDefinitionTask()
    {
        return createTask("Update System Definition", new AddNodeToSystemDefinitionTaskHandler(this.sdkAMQPClient, repository));
    }

    @Bean("configurePxeBootTask")
    private WorkflowTask configurePxeBootTask()
    {
        return createTask("Configure PXE boot", new ConfigurePxeBootTaskHandler(nodeService));
    }

    @Bean("addHostToVcenterTask")
    private WorkflowTask addHostToVcenterTask()
    {
        return createTask("Add host to vCenter cluster", new AddHostToVCenterTaskHandler(this.nodeService, repository));
    }

    @Bean("installScaleIoVibTask")
    private WorkflowTask installScaleIoVibTask()
    {
        return createTask("Install SDC vSphere Installation Bundle (VIB)", new InstallScaleIoVibTaskHandler(this.nodeService, repository, sdcVibUrl));
    }

    @Bean("configureScaleIoVibTask")
    private WorkflowTask configureScaleIoVibTask()
    {
        return createTask("Configure SDC vSphere Installation Bundle (VIB)", new ConfigureScaleIoVibTaskHandler(this.nodeService, repository));
    }

    @Bean("addHostToDvSwitchTask")
    private WorkflowTask addHostToDvSwitchTask()
    {
        return createTask("Add ESXi host to cluster DVSwitch", new AddHostToDvSwitchTaskHandler(this.nodeService, repository));
    }

    @Bean("deploySVMTask")
    private WorkflowTask deploySVMTask()
    {
        return createTask("Clone and deploy ScaleIO VM",
                new DeployScaleIoVmTaskHandler(this.nodeService, repository, DEPLOY_SVM_WAIT_TIME));
    }

    @Bean("enablePciPassthroughHostTask")
    private WorkflowTask enablePciPassthroughHostTask()
    {
        return createTask("Enable PCI passthrough ESXi host", new EnablePciPassthroughTaskHandler(this.nodeService, repository));
    }

    @Bean("rebootHostTask")
    private WorkflowTask rebootHostTask()
    {
        return createTask("Reboot Host", new RebootHostTaskHandler(this.nodeService, repository));
    }

    @Bean("setPciPassthroughSioVmTask")
    private WorkflowTask setPciPassthroughSioVmTask()
    {
        return createTask("Configure PCI passthrough ScaleIO VM", new UpdatePciPassThroughTaskHandler(this.nodeService, repository));
    }

    @Bean("applyEsxiLicenseTask")
    private WorkflowTask applyEsxiLicenseTask()
    {
        return createTask("Apply ESXi license", new ApplyEsxiLicenseTaskHandler(this.nodeService, repository));
    }

    @Bean("notifyNodeDiscoveryToUpdateStatusTask")
    private WorkflowTask notifyNodeDiscoveryToUpdateStatusTask()
    {
        return createTask("Notify node discovery to update status", new NotifyNodeDiscoveryToUpdateStatusTaskHandler(this.nodeService,"Completed"));
    }

    @Bean("datastoreRenameTask")
    private WorkflowTask datastoreRenameTask()
    {
        return createTask("Rename datastore", new DatastoreRenameTaskHandler(this.nodeService, this.repository));
    }

    @Bean("updateSoftwareAcceptanceTask")
    private WorkflowTask updateSoftwareAcceptanceTask()
    {
        return createTask("Update software acceptance", new UpdateSoftwareAcceptanceTaskHandler(this.nodeService, this.repository));
    }

    @Bean("enterHostMaintenanceModeTask")
    private WorkflowTask enterHostMaintenanceModeTask()
    {
        return createTask("Enter host maintenance mode", new EnterHostMaintenanceModeTaskHandler(nodeService, repository));
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

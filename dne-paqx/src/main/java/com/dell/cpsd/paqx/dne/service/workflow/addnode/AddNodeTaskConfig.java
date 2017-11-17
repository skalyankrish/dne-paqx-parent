/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.workflow.addnode;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dell.cpsd.paqx.dne.service.model.Step;

@Configuration
public class AddNodeTaskConfig
{
    @Bean("addNodeWorkflowSteps")
    public Map<String, Step> addNodeWorkflowSteps()
    {
        final Map<String, Step> workflowSteps = new HashMap<>();
        addWorkflowSteps(workflowSteps,
                "notifyNodeDiscoveryToUpdateStatus",
                "retrieveEsxiDefaultCredentialDetails",
                "installEsxi",
                "addHostToVcenter",
                "applyEsxiLicense",
                "datastoreRename",
                "exitHostMaintenanceMode1",
                "enablePciPassthroughHost",
                "installScaleIoVib",
                "enterHostMaintenanceMode",
                "rebootHost",
                "exitHostMaintenanceMode2",
                "configureScaleIoVib",
                "updateSdcPerformanceProfile",
                "addHostToDvSwitch",
                "deploySVM",
                "setPciPassthroughSioVm",
                "configureVmNetworkSettings",
                "configurePxeBoot",
                //"powerOnSVM", //VM Auto start enabled now upon host reboot
                "changeSvmCredentials",
                "installSvmPackages",
                "performanceTuneSvm",
                "addHostToProtectionDomain",
                "updateSystemDefinition",
                "notifyNodeDiscoveryToUpdateStatus");

        return workflowSteps;
    }

    private void addWorkflowSteps(Map<String,Step> workflowSteps, String... steps)
    {
        String currentStep="startAddNodeWorkflow";
        for (String step : steps)
        {
            workflowSteps.put(currentStep, new Step(step));
            currentStep=step;
        }
        workflowSteps.put(currentStep, new Step("completed", true));
        workflowSteps.put("completed", null);
    }
}
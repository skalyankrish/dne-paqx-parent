/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.workflow.preprocess;

import com.dell.cpsd.paqx.dne.service.model.Step;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */

@Configuration
public class PreProcessTaskConfig {
    @Bean("preProcessWorkflowSteps")
    public Map<String, Step> preProcessWorkflowSteps(){
        final Map<String, Step> workflowSteps = new HashMap<>();

        addWorkflowSteps(workflowSteps,
                "cleanInMemoryDatabase",
                "listScaleIoComponents",
                "listVCenterComponents",
                "discoverScaleIo",
                "discoverVCenter",
                //"discoverNodeInventory",
                "configIdrac",
                "pingIdrac",
                //"changeIdracCredentials", currently has a bug so leave out for now...
                "configureObmSettings",
                "configureBootDeviceIdrac",
                "findVCluster",
                "findProtectionDomain",
                "findOrCreateValidStoragePool");

        return workflowSteps;
    }

    private void addWorkflowSteps(Map<String,Step> workflowSteps, String... steps)
    {
        String currentStep="startPreProcessWorkflow";
        for (String step : steps)
        {
            workflowSteps.put(currentStep, new Step(step));
            currentStep=step;
        }
        workflowSteps.put(currentStep, new Step("completed", true));
        workflowSteps.put("completed", null);
    }
}
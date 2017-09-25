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
                "findAvailableNodes",
                //TODO: Re-enable listScaleIoComponets when defect is fixed:
                //https://jira.cec.lab.emc.com:8443/browse/ESTS-133801
                //"listScaleIoComponents",
                "listVCenterComponents",
                //TODO: Re-enable the discover scaleio when mdm is up and running
                /*"discoverScaleIo",*/
                "discoverVCenter",
                "discoverNodeInventory",
                "configIdrac",
                "pingIdrac",
                "configureObmSettings",
                "configureBootDeviceIdrac",
                //TODO: Re-enable the find scaleio when mdm is up and running
                /*"findScaleIO"*/
                "findVCluster",
                "findProtectionDomain");

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
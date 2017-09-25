/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.orchestration;

import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.service.IBaseService;

/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public interface IOrchestrationService
{
    /**
     * Orchestrate the workflow for the given <code>Job</code> instance.
     *
     * @param job - The <code>Job</code> instance whose worflow will be orchestrated
     * @param jobService - The workflow <code>IBaseService</code> service
     */
    void orchestrateWorkflow(Job job, IBaseService jobService);
}

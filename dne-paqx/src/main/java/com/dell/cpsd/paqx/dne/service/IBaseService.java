/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service;

/**
 * Base service interface.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public interface IBaseService
{
    /**
     * Get the workflow service
     *
     * @return The <code>WorkflowService</code> instance
     */
    WorkflowService getWorkflowService();

    /**
     * Sets the workflow serivice
     *
     * @param workflowService
     */
    void setWorkflowService (WorkflowService workflowService);
}

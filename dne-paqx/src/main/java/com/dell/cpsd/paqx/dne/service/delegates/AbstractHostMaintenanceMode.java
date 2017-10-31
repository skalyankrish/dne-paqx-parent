/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractHostMaintenanceMode extends BaseWorkflowDelegate
{
    /*
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractHostMaintenanceMode.class);

    /*
     * The <code>NodeService</code> instance
     */
    private final NodeService nodeService;

    /*
     * The <code>DataServiceRepository</code> instance
     */
    private final DataServiceRepository repository;

    /*
    * The task name
    */
    private final String taskName;

    /**
     * AbstractHostMaintenanceModeTaskHandler constructor
     *
     * @param nodeService - The <code>NodeService</code> instance
     * @param repository  - The <code>DataServiceRepository</code> instance
     * @param taskName    - The task name
     * @since 1.0
     */
    public AbstractHostMaintenanceMode(final NodeService nodeService, final DataServiceRepository repository,
                                       final String taskName)
    {
        this.nodeService = nodeService;
        this.repository = repository;
        this.taskName = taskName;
    }

    /**
     * Subclasses hould override to set the desired maintenance mode state.
     *
     * @return True to enter maintenance mode, false to exit maintenance mode
     * @since 1.0
     */
    protected abstract boolean getMaintenanceModeEnable();

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Execute {}", this.taskName);

       /* final ComponentEndpointIds componentEndpointIds = repository.getVCenterComponentEndpointIdsByEndpointType(
                "VCENTER-CUSTOMER");
        final String hostname = (String) delegateExecution.getVariable(HOSTNAME);

        final boolean maintenanceModeEnable = this.getMaintenanceModeEnable();
        final HostMaintenanceModeRequestMessage requestMessage = getHostMaintenanceModeRequestMessage(
                componentEndpointIds, hostname, maintenanceModeEnable);
        final boolean success = this.nodeService.requestHostMaintenanceMode(requestMessage);
        if (!success)
        {
            LOGGER.error(taskName + " failed!");
            updateDelegateStatus(taskName + " failed!");
            throw new BpmnError(INSTALL_ESXI_FAILED, taskName + " failed!");
        }
*/
        LOGGER.info(taskName + " was successful.");
        updateDelegateStatus(taskName + " was successful.");
    }
}

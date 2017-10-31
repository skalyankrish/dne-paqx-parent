/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.INVENTORY_SCALE_IO_FAILED;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.SCALE_IO_INFORMATION_NOT_FOUND;

@Component
@Scope("prototype")
@Qualifier("inventoryScaleIO")
public class InventoryScaleIO extends BaseWorkflowDelegate
{
    /**
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryScaleIO.class);

    /**
     * The <code>NodeService</code> instance
     */
    private final NodeService nodeService;
    private final DataServiceRepository repository;

    @Autowired
    public InventoryScaleIO(final NodeService nodeService, final DataServiceRepository repository)
    {
        this.nodeService = nodeService;
        this.repository = repository;
    }

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Execute Inventory ScaleIO");

        ComponentEndpointIds componentEndpointIds;
        try
        {
            componentEndpointIds = repository.getComponentEndpointIds("SCALEIO-CLUSTER");
        }
        catch (Exception e)
        {
            LOGGER.error("An Unexpected Exception occurred attempting to retrieve Scale IO Component Endpoints.", e);
            updateDelegateStatus(
                    "An Unexpected Exception occurred attempting to inventory Scale IO.  Reason: " + e.getMessage());
            throw new BpmnError(INVENTORY_SCALE_IO_FAILED,
                                "An Unexpected Exception occurred attempting to inventory Scale IO.  Reason: " +
                                e.getMessage());
        }
        if (componentEndpointIds == null)
        {
            LOGGER.error("Scale IO Endpoints not found.");
            updateDelegateStatus("Scale IO Endpoints not found.");
            throw new BpmnError(SCALE_IO_INFORMATION_NOT_FOUND, "Scale IO Endpoints not found.");
        }

        boolean success;
        try
        {
            success = this.nodeService.requestDiscoverScaleIo(componentEndpointIds,
                                                              delegateExecution.getProcessInstanceId());
        }
        catch (Exception e)
        {
            LOGGER.error("An Unexpected Exception occurred attempting to Inventory Scale IO.", e);
            updateDelegateStatus(
                    "An Unexpected Exception occurred attempting to inventory Scale IO.  Reason: " + e.getMessage());
            throw new BpmnError(INVENTORY_SCALE_IO_FAILED,
                                "An Unexpected Exception occurred attempting to inventory Scale IO.  Reason: " +
                                e.getMessage());
        }

        if (!success)
        {
            LOGGER.error("Request for Inventory Scale IO failed.");
            updateDelegateStatus("Inventory request for Scale IO Failed.");
            throw new BpmnError(INVENTORY_SCALE_IO_FAILED, "Inventory request for Scale IO Failed.");
        }
        LOGGER.info("Request for Inventory Scale IO completed successfully.");
        updateDelegateStatus("Inventory request for Scale IO completed successfully.");

    }

}

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

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.INVENTORY_VCENTER_FAILED;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.VCENTER_INFORMATION_NOT_FOUND;

@Component
@Scope("prototype")
@Qualifier("inventoryVCenter")
public class InventoryVCenter extends BaseWorkflowDelegate
{
    /**
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryVCenter.class);

    /**
     * The <code>NodeService</code> instance
     */
    private final NodeService nodeService;
    private final DataServiceRepository repository;

    @Autowired
    public InventoryVCenter(final NodeService nodeService, final DataServiceRepository repository)
    {
        this.nodeService = nodeService;
        this.repository = repository;
    }

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Execute Inventory VCenter");

        ComponentEndpointIds componentEndpointIds;
        try
        {
            componentEndpointIds = repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER");
        }
        catch (Exception e)
        {
            LOGGER.error("An Unexpected Exception occurred attempting to retrieve VCenter Component Endpoints.", e);
            updateDelegateStatus(
                    "An Unexpected Exception occurred attempting to inventory VCenter.  Reason: " + e.getMessage());
            throw new BpmnError(INVENTORY_VCENTER_FAILED,
                                "An Unexpected Exception occurred attempting to inventory VCenter.  Reason: " +
                                e.getMessage());
        }
        if (componentEndpointIds == null)
        {
            LOGGER.error("VCenter Endpoints not found.");
            updateDelegateStatus("VCenter Endpoints not found.");
            throw new BpmnError(VCENTER_INFORMATION_NOT_FOUND, "VCenter Endpoints not found.");
        }

        boolean success;
        try
        {
            success = this.nodeService.requestDiscoverVCenter(componentEndpointIds,
                                                              delegateExecution.getProcessInstanceId());
        }
        catch (Exception e)
        {
            LOGGER.error("An Unexpected Exception occurred attempting to Inventory VCenter.", e);
            updateDelegateStatus(
                    "An Unexpected Exception occurred attempting to inventory VCenter.  Reason: " + e.getMessage());
            throw new BpmnError(INVENTORY_VCENTER_FAILED,
                                "An Unexpected Exception occurred attempting to inventory VCenter.  Reason: " +
                                e.getMessage());
        }
        if (!success)
        {
            LOGGER.error("Request for Inventory VCenter failed");
            updateDelegateStatus("Inventory request for VCenter Failed.");
            throw new BpmnError(INVENTORY_VCENTER_FAILED, "Inventory request for VCenter Failed.");
        }
        LOGGER.info("Request for Inventory VCenter completed successfully.");
        updateDelegateStatus("Inventory request for VCenter completed successfully.");
    }
}

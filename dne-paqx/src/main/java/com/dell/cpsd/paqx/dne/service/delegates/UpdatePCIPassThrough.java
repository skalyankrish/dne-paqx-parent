/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.virtualization.capabilities.api.Credentials;
import com.dell.cpsd.virtualization.capabilities.api.UpdatePCIPassthruSVMRequestMessage;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.DEPLOY_SCALEIO_NEW_VM_NAME;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.HOSTNAME;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.HOST_PCI_DEVICE_ID;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.UPDATE_PCI_PASSTHROUGH;

/**
 * Update PCI Passthrough Controller for ScaleIo Vm.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@Component
@Scope("prototype")
@Qualifier("updatePCIPassThrough")
public class UpdatePCIPassThrough extends BaseWorkflowDelegate
{
    /**
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdatePCIPassThrough.class);

    /**
     * The <code>NodeService</code> instance
     */
    private final NodeService           nodeService;
    private final DataServiceRepository repository;

    @Autowired
    public UpdatePCIPassThrough(final NodeService nodeService, final DataServiceRepository repository)
    {
        this.nodeService = nodeService;
        this.repository = repository;
    }

    private UpdatePCIPassthruSVMRequestMessage getUpdatePCIPassthruSVMRequestMessage(final String hostname, final String hostPciDeviceId,
            final String newVMName, final ComponentEndpointIds componentEndpointIds)
    {
        final UpdatePCIPassthruSVMRequestMessage requestMessage = new UpdatePCIPassthruSVMRequestMessage();
        requestMessage.setHostname(hostname);
        requestMessage.setHostPciDeviceId(hostPciDeviceId);
        requestMessage.setVmName(newVMName);
        requestMessage.setCredentials(new Credentials(componentEndpointIds.getEndpointUrl(), null, null));
        requestMessage.setComponentEndpointIds(
                new com.dell.cpsd.virtualization.capabilities.api.ComponentEndpointIds(componentEndpointIds.getComponentUuid(),
                        componentEndpointIds.getEndpointUuid(), componentEndpointIds.getCredentialUuid()));
        return requestMessage;
    }

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Execute Update/Set PCI pass through task");
        final String taskMessage = "Set PCI Pass Through";

        final NodeDetail nodeDetail = (NodeDetail) delegateExecution.getVariable(NODE_DETAIL);
        final String hostname =(String) delegateExecution.getVariable(HOSTNAME);
        final String hostPciDeviceId = (String) delegateExecution.getVariable(HOST_PCI_DEVICE_ID);
        final String newVMName = (String) delegateExecution.getVariable(DEPLOY_SCALEIO_NEW_VM_NAME);

        ComponentEndpointIds componentEndpointIds;
        try
        {
            componentEndpointIds = repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER");
        }
        catch (Exception e)
        {
            String errorMessage = "An Unexpected Exception occurred attempting to retrieve VCenter Component Endpoints. Reason: ";
            LOGGER.error(errorMessage, e);
            updateDelegateStatus(errorMessage + e.getMessage());
            throw new BpmnError(UPDATE_PCI_PASSTHROUGH, errorMessage + e.getMessage());
        }

        final UpdatePCIPassthruSVMRequestMessage requestMessage = getUpdatePCIPassthruSVMRequestMessage(hostname, hostPciDeviceId,
                newVMName, componentEndpointIds);

        boolean success;
        try
        {
           success = this.nodeService.requestSetPciPassThrough(requestMessage);
        }
        catch (Exception ex)
        {
            String errorMessage = "An Unexpected Exception occurred attempting to request " + taskMessage + ".  Reason: ";
            LOGGER.error(errorMessage, ex);
            updateDelegateStatus(errorMessage + ex.getMessage());
            throw new BpmnError(UPDATE_PCI_PASSTHROUGH, errorMessage + ex.getMessage());
        }

        if (!success)
        {
            String errorMessage = "Configure PCI PassThrough Failed!";
            LOGGER.error(errorMessage);
            updateDelegateStatus(errorMessage);
            throw new BpmnError(UPDATE_PCI_PASSTHROUGH, errorMessage);
        }

        String returnMessage = taskMessage + " on Node " + nodeDetail.getServiceTag() + " was successful.";
        LOGGER.info(returnMessage);
        updateDelegateStatus(returnMessage);
    }
}

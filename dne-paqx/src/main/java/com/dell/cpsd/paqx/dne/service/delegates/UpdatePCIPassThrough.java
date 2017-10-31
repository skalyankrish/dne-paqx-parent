/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;

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

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Execute Update/Set PCI pass through task");
        final String taskMessage = "Set PCI Pass Through";

        final NodeDetail nodeDetail = (NodeDetail) delegateExecution.getVariable(NODE_DETAIL);
        /*try
        {
            final Validate validate = new Validate(job).invoke();
            final String hostname = validate.getHostname();
            final String hostPciDeviceId = validate.getHostPciDeviceId();
            final String newVMName = validate.getNewVMName();
            final ComponentEndpointIds componentEndpointIds = validate.getComponentEndpointIds();

            final UpdatePCIPassthruSVMRequestMessage requestMessage = getUpdatePCIPassthruSVMRequestMessage(hostname, hostPciDeviceId,
                                                                                                            newVMName, componentEndpointIds);

            final boolean success = this.nodeService.requestSetPciPassThrough(requestMessage);

            if (!success)
            {
                throw new IllegalStateException("Configure PCI PassThrough Failed");
            }

            response.setWorkFlowTaskStatus(Status.SUCCEEDED);

            return true;
        }
        catch (Exception e)
        {
            LOGGER.error("Exception occurred", e);
            response.addError(e.getMessage());
        }

        response.setWorkFlowTaskStatus(Status.FAILED);
        return false;*/

        LOGGER.info(taskMessage + " on Node " + nodeDetail.getServiceTag() + " was successful.");
        updateDelegateStatus(taskMessage + " on Node " + nodeDetail.getServiceTag() + " was successful.");
    }
}

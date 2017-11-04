/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import com.dell.cpsd.paqx.dne.service.model.ChangeIdracCredentialsResponse;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.INSTALL_ESXI_FAILED;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;

@Component
@Scope("prototype")
@Qualifier("configureNodeCredentials")
public class ConfigureNodeCredentials extends BaseWorkflowDelegate
{
    /*
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigureNodeCredentials.class);

    /*
     * The <code>NodeService</code> instance
     */
    private NodeService nodeService;

    /**
     * ChangeIdracCredentialsTaskHandler constructor.
     *
     * @param nodeService - The <code>NodeService</code> instance.
     * @since 1.0
     */
    @Autowired
    public ConfigureNodeCredentials(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Execute ConfigureNodeCredentials");
        final String taskMessage = "Configure Node Credentials";
        NodeDetail nodeDetail = (NodeDetail) delegateExecution.getVariable(NODE_DETAIL);

        final String symphonuUuid = nodeDetail.getId();
        ChangeIdracCredentialsResponse responseMessage = null;
        try
        {
            responseMessage = this.nodeService.changeIdracCredentials(symphonuUuid);
        }
        catch (Exception e)
        {
            LOGGER.error("An Unexpected Exception Occurred attempting to Change iDrac Credentials on Node " +
                         nodeDetail.getServiceTag(), e);
            updateDelegateStatus("An Unexpected Exception Occurred attempting to Change iDrac Credentials on Node " +
                                 nodeDetail.getServiceTag());
            throw new BpmnError(INSTALL_ESXI_FAILED, taskMessage + " on Node " + nodeDetail.getServiceTag() +
                                                     " failed!  Reason: " + e.getMessage());
        }
        if (responseMessage != null && !"SUCCESS".equalsIgnoreCase(responseMessage.getMessage()))
        {
            LOGGER.error(taskMessage + " on Node " + nodeDetail.getServiceTag() + " failed!");
            updateDelegateStatus(taskMessage + " on Node " + nodeDetail.getServiceTag() + " failed!");
            throw new BpmnError(INSTALL_ESXI_FAILED,
                                taskMessage + " on Node " + nodeDetail.getServiceTag() + " failed!");
        }
        LOGGER.info(taskMessage + " on Node " + nodeDetail.getServiceTag() + " was successful.");
        updateDelegateStatus(taskMessage + " on Node " + nodeDetail.getServiceTag() + " was successful.");

    }
}

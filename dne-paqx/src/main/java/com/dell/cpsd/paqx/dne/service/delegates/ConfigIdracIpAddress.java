/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import com.dell.cpsd.paqx.dne.service.model.IdracInfo;
import com.dell.cpsd.paqx.dne.service.model.IdracNetworkSettingsRequest;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.CONFIGURE_IP_ADDRESS_FAILED;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;

@Component
@Scope("prototype")
@Qualifier("configIdracIpAddress")
public class ConfigIdracIpAddress extends BaseWorkflowDelegate
{
    /*
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigIdracIpAddress.class);

    /*
     * The <code>NodeService</code> instance
     */
    private NodeService nodeService;

    @Autowired
    public ConfigIdracIpAddress(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Executing Config IP Address");
        NodeDetail nodeDetail = (NodeDetail) delegateExecution.getVariable(NODE_DETAIL);
        final String taskMessage = "Configure IP Address";
        
        IdracInfo idracInfo;
        try
        {
            IdracNetworkSettingsRequest idracNetworkSettingsRequest = new IdracNetworkSettingsRequest();
            idracNetworkSettingsRequest.setUuid(nodeDetail.getId());
            idracNetworkSettingsRequest.setIdracIpAddress(nodeDetail.getIdracIpAddress());
            idracNetworkSettingsRequest.setIdracGatewayIpAddress(nodeDetail.getIdracGatewayIpAddress());
            idracNetworkSettingsRequest.setIdracSubnetMask(nodeDetail.getIdracSubnetMask());

            idracInfo = nodeService.idracNetworkSettings(idracNetworkSettingsRequest);
        }
        catch (Exception e)
        {
            LOGGER.error("An Unexpected Exception Occurred attempting to Configure IP Address on Node " +
                         nodeDetail.getServiceTag(), e);
            updateDelegateStatus("An Unexpected Exception Occurred attempting to Configure IP Address on Node " +
                                 nodeDetail.getServiceTag());
            throw new BpmnError(CONFIGURE_IP_ADDRESS_FAILED,
                                taskMessage + " on Node " + nodeDetail.getServiceTag() + " failed!  Reason: " +
                                e.getMessage());
        }
        if (idracInfo == null || !"SUCCESS".equalsIgnoreCase(idracInfo.getMessage()))
        {
            String errorMessage=taskMessage + " on Node " + nodeDetail.getServiceTag() + " failed!  Reason: " +
                    (idracInfo==null?null:idracInfo.getMessage());
            LOGGER.error(errorMessage);
            updateDelegateStatus(errorMessage);
            throw new BpmnError(CONFIGURE_IP_ADDRESS_FAILED,
                                errorMessage);
        }
        LOGGER.info(taskMessage + " on Node " + nodeDetail.getServiceTag() + " was successful.");
        updateDelegateStatus(taskMessage + " on Node " + nodeDetail.getServiceTag() + " was successful.");
    }
}

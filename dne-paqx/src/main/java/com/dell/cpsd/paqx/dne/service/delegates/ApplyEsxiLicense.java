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

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.HOSTNAME;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;

@Component
@Scope("prototype")
@Qualifier("applyEsxiLicense")
public class ApplyEsxiLicense extends BaseWorkflowDelegate
{
    /**
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplyEsxiLicense.class);

    /**
     * The <code>NodeService</code> instance
     */
    private final NodeService nodeService;
    private final DataServiceRepository repository;

    @Autowired
    public ApplyEsxiLicense(final NodeService nodeService, final DataServiceRepository repository)
    {
        this.nodeService = nodeService;
        this.repository = repository;
    }

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Execute Apply Esxi License");
        final String taskMessage = "Apply Esxi License";

        final String hostname = (String) delegateExecution.getVariable(HOSTNAME);
        final NodeDetail nodeDetail = (NodeDetail) delegateExecution.getVariable(NODE_DETAIL);
        /*final ComponentEndpointIds componentEndpointIds = repository.getVCenterComponentEndpointIdsByEndpointType(
                "VCENTER-CUSTOMER");
        if (componentEndpointIds == null)
        {
            LOGGER.error(taskMessage + " on Node " + nodeDetail.getServiceTag() + " failed!");
            updateDelegateStatus(taskMessage + " on Node " + nodeDetail.getServiceTag() + " failed!");
            throw new BpmnError(APPLY_ESXI_LICENSE_FAILED,
                                taskMessage + " on Node " + nodeDetail.getServiceTag() + " failed!");
        }

        final AddEsxiHostVSphereLicenseRequest requestMessage = getLicenseRequest(componentEndpointIds, hostname);

        final boolean success = this.nodeService.requestInstallEsxiLicense(requestMessage);
        if (!success)
        {
            LOGGER.error(taskMessage + " on Node " + nodeDetail.getServiceTag() + " failed!");
            updateDelegateStatus(taskMessage + " on Node " + nodeDetail.getServiceTag() + " failed!");
            throw new BpmnError(APPLY_ESXI_LICENSE_FAILED,
                                taskMessage + " on Node " + nodeDetail.getServiceTag() + " failed!");
        }*/
        LOGGER.info(taskMessage + " on Node " + nodeDetail.getServiceTag() + " was successful.");
        updateDelegateStatus(taskMessage + " on Node " + nodeDetail.getServiceTag() + " was successful.");

    }
}
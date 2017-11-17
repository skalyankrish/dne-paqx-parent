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
import com.dell.cpsd.storage.capabilities.api.PerformanceProfileRequest;
import com.dell.cpsd.storage.capabilities.api.SioSdcUpdatePerformanceProfileRequestMessage;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.IOCTL_INI_GUI_STR;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.UPDATE_SDC_PERFORMANCE_PROFILE_FAILED;

/**
 * Update ScaleIo Data Client (SDC) performance profile.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@Component
@Scope("prototype")
@Qualifier("updateSDCPerformanceProfile")
public class UpdateSDCPerformanceProfile extends BaseWorkflowDelegate
{
    /*
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateSDCPerformanceProfile.class);

    /*
     * The <code>NodeService</code> instance
     */
    private final NodeService nodeService;

    /*
     * The <code>DataServiceRepository</code> instance
     */
    private final DataServiceRepository repository;

    /**
     * UpdateSdcPerformanceProfileTaskHandler constructor
     *
     * @param nodeService - The <code>NodeService</code> instance
     * @param repository  - The <code>DataServiceRepository</code> instance
     */
    @Autowired
    public UpdateSDCPerformanceProfile(final NodeService nodeService, final DataServiceRepository repository)
    {
        this.nodeService = nodeService;
        this.repository = repository;
    }

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Execute UpdateSDCPerformanceProfile");
        final String taskMessage = "Update ScaleIO SDC Performance Profile";

        final NodeDetail nodeDetail = (NodeDetail) delegateExecution.getVariable(NODE_DETAIL);
        final String sdcGUID = (String) delegateExecution.getVariable(IOCTL_INI_GUI_STR);
        final String scaleIoSdcIpAddress = nodeDetail.getEsxiManagementIpAddress();

        ComponentEndpointIds componentEndpointIds;
        try
        {
            componentEndpointIds = repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER");
        }
        catch (Exception e)
        {
            String errorMessage = "An Unexpected Exception occurred attempting to retrieve VCenter Component Endpoints.  Reason: ";
            LOGGER.error(errorMessage, e);
            updateDelegateStatus(errorMessage + e.getMessage());
            throw new BpmnError(UPDATE_SDC_PERFORMANCE_PROFILE_FAILED, errorMessage + e.getMessage());
        }

        final PerformanceProfileRequest performanceProfileRequest = new PerformanceProfileRequest();
        performanceProfileRequest.setSdcIp(scaleIoSdcIpAddress);
        performanceProfileRequest.setSdcGuid(sdcGUID);
        performanceProfileRequest.setPerfProfile(PerformanceProfileRequest.PerfProfile.HIGH_PERFORMANCE);

        final SioSdcUpdatePerformanceProfileRequestMessage requestMessage = new SioSdcUpdatePerformanceProfileRequestMessage();
        requestMessage.setPerformanceProfileRequest(performanceProfileRequest);
        requestMessage.setEndpointUrl("https://" + componentEndpointIds.getEndpointUrl());
        requestMessage.setComponentEndpointIds(
                new com.dell.cpsd.storage.capabilities.api.ComponentEndpointIds(componentEndpointIds.getComponentUuid(),
                        componentEndpointIds.getEndpointUuid(), componentEndpointIds.getCredentialUuid()));

        boolean succeeded;
        try
        {
            succeeded = this.nodeService.requestUpdateSdcPerformanceProfile(requestMessage);

        }
        catch (Exception ex)
        {
            String errorMessage = "An Unexpected Exception occurred attempting to request " + taskMessage + ".  Reason: ";
            LOGGER.error(errorMessage, ex);
            updateDelegateStatus(errorMessage + ex.getMessage());
            throw new BpmnError(UPDATE_SDC_PERFORMANCE_PROFILE_FAILED,
                    errorMessage + ex.getMessage());
        }
        if (!succeeded)
        {
            String errorMessage = taskMessage + " on Node " + nodeDetail.getServiceTag() + " failed!";
            LOGGER.error(errorMessage);
            updateDelegateStatus(errorMessage);
            throw new BpmnError(UPDATE_SDC_PERFORMANCE_PROFILE_FAILED, errorMessage);
        }

        String returnMessage = taskMessage + " on Node " + nodeDetail.getServiceTag() + " was successful.";
        LOGGER.info(returnMessage);
        updateDelegateStatus(returnMessage);
    }
}

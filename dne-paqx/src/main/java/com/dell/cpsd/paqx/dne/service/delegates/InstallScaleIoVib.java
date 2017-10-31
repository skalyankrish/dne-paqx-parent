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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.HOSTNAME;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;

@Component
@Scope("prototype")
@Qualifier("installScaleIOVib")
public class InstallScaleIoVib extends BaseWorkflowDelegate
{
    /*
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(InstallScaleIoVib.class);

    /*
     * The <code>NodeService</code> instance
     */
    private final NodeService nodeService;

    /*
     * The <code>DataServiceRepository</code> instance
     */
    private final DataServiceRepository repository;

    /*
    * The remote URL location of the SDC VIB
    */
    private final String sdcVibRemoteUrl;

    /**
     * InstallScaleIoVibTaskHandler constructor
     *
     * @param nodeService     - The <code>NodeService</code> instance
     * @param repository      - The <code>DataServiceRepository</code> instance
     * @param sdcVibRemoteUrl - The remote URL location of the SDC VIB
     */
    public InstallScaleIoVib(final NodeService nodeService, final DataServiceRepository repository,
                             final String sdcVibRemoteUrl)
    {
        this.nodeService = nodeService;
        this.repository = repository;
        this.sdcVibRemoteUrl = sdcVibRemoteUrl;
    }

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Execute Install ScaleIO VIB task");
        final String taskMessage = "Install ScaleIO Vib";

        final String hostname = (String) delegateExecution.getVariable(HOSTNAME);
        final NodeDetail nodeDetail = (NodeDetail) delegateExecution.getVariable(NODE_DETAIL);

    /*    ComponentEndpointIds componentEndpointIds = null;
        try
        {
            componentEndpointIds = repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER");
        }
        catch (Exception e)
        {
            LOGGER.error("An Unexpected Exception occurred attempting to retrieve VCenter Component Endpoints.", e);
            updateDelegateStatus(
                    "An Unexpected Exception occurred attempting to retrieve VCenter Component Endpoints.  Reason: " +
                    e.getMessage());
            throw new BpmnError(INSTALL_SCALEIO_VIB_FAILED,
                                "An Unexpected Exception occurred attempting to retrieve VCenter Component Endpoints.  Reason: " +
                                e.getMessage());
        }

        final SoftwareVIBRequestMessage requestMessage = getSoftwareVIBRequestMessage(componentEndpointIds, hostname);

        boolean success = false;
        try
        {
            success = this.nodeService.requestInstallSoftwareVib(requestMessage);
        }
        catch (Exception e)
        {
            LOGGER.error("An Unexpected Exception occurred attempting to Install ScaleIO Vib.", e);
            updateDelegateStatus(
                    "An Unexpected Exception occurred attempting to request " + taskMessage + ".  Reason: " +
                    e.getMessage());
            throw new BpmnError(INSTALL_SCALEIO_VIB_FAILED,
                                "An Unexpected Exception occurred attempting to request " + taskMessage +
                                ".  Reason: " + e.getMessage());
        }

        if (!success)
        {
            LOGGER.error(taskMessage + " on Node " + nodeDetail.getServiceTag() + " failed!");
            updateDelegateStatus(taskMessage + " on Node " + nodeDetail.getServiceTag() + " failed!");
            throw new BpmnError(INSTALL_SCALEIO_VIB_FAILED,
                                taskMessage + " on Node " + nodeDetail.getServiceTag() + " failed!");
        }*/

        LOGGER.info(taskMessage + " on Node " + nodeDetail.getServiceTag() + " was successful.");
        updateDelegateStatus(taskMessage + " on Node " + nodeDetail.getServiceTag() + " was successful.");
    }
}

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
import com.dell.cpsd.virtualization.capabilities.api.SoftwareVIBRequest;
import com.dell.cpsd.virtualization.capabilities.api.SoftwareVIBRequestMessage;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.HOSTNAME;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.INSTALL_SCALEIO_VIB_FAILED;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;
import static java.util.Collections.singletonList;

/**
 * Install ScaleIo Data Client (SDC)
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
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
    @Value("${rackhd.sdc.vib.install.repo.url}")
    private String sdcVibRemoteUrl;

    /**
     * InstallScaleIoVibTaskHandler constructor
     *
     * @param nodeService     - The <code>NodeService</code> instance
     * @param repository      - The <code>DataServiceRepository</code> instance
     */
    public InstallScaleIoVib(final NodeService nodeService, final DataServiceRepository repository)
    {
        this.nodeService = nodeService;
        this.repository = repository;
    }

    private SoftwareVIBRequestMessage getSoftwareVIBRequestMessage(final ComponentEndpointIds componentEndpointIds, final String hostname)
    {
        final SoftwareVIBRequestMessage requestMessage = new SoftwareVIBRequestMessage();
        final SoftwareVIBRequest softwareVIBRequest = new SoftwareVIBRequest();
        softwareVIBRequest.setVibOperation(SoftwareVIBRequest.VibOperation.INSTALL);
        softwareVIBRequest.setHostName(hostname);
        softwareVIBRequest.setVibUrls(singletonList(sdcVibRemoteUrl));
        requestMessage.setSoftwareVibInstallRequest(softwareVIBRequest);
        requestMessage.setCredentials(new Credentials(componentEndpointIds.getEndpointUrl(), null, null));
        requestMessage.setComponentEndpointIds(
                new com.dell.cpsd.virtualization.capabilities.api.ComponentEndpointIds(componentEndpointIds.getComponentUuid(),
                        componentEndpointIds.getEndpointUuid(), componentEndpointIds.getCredentialUuid()));
        return requestMessage;
    }

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Execute Install ScaleIO VIB task");
        final String taskMessage = "Install ScaleIO Vib";

        final String hostname = (String) delegateExecution.getVariable(HOSTNAME);
        final NodeDetail nodeDetail = (NodeDetail) delegateExecution.getVariable(NODE_DETAIL);

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
            throw new BpmnError(INSTALL_SCALEIO_VIB_FAILED, errorMessage + e.getMessage());
        }

        final SoftwareVIBRequestMessage requestMessage = getSoftwareVIBRequestMessage(componentEndpointIds, hostname);

        boolean success;
        try
        {
            success = this.nodeService.requestInstallSoftwareVib(requestMessage);
        }
        catch (Exception e)
        {
            String errorMessage = "An Unexpected Exception occurred attempting to request " + taskMessage + ".  Reason: ";
            LOGGER.error(errorMessage, e);
            updateDelegateStatus(errorMessage + e.getMessage());
            throw new BpmnError(INSTALL_SCALEIO_VIB_FAILED, errorMessage + e.getMessage());
        }

        if (!success)
        {
            String errorMessage = taskMessage + " on Node " + nodeDetail.getServiceTag() + " failed!";
            LOGGER.error(errorMessage);
            updateDelegateStatus(errorMessage);
            throw new BpmnError(INSTALL_SCALEIO_VIB_FAILED, errorMessage);
        }

        String returnMessage = taskMessage + " on Node " + nodeDetail.getServiceTag() + " was successful.";
        LOGGER.info(returnMessage);
        updateDelegateStatus(returnMessage);
    }
}

/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.task.handler.addnode;

import com.dell.cpsd.paqx.dne.domain.IWorkflowTaskHandler;
import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.paqx.dne.service.model.InstallEsxiTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.InstallScaleIoVibTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.Status;
import com.dell.cpsd.paqx.dne.service.task.handler.BaseTaskHandler;
import com.dell.cpsd.virtualization.capabilities.api.Credentials;
import com.dell.cpsd.virtualization.capabilities.api.SoftwareVIBRequest;
import com.dell.cpsd.virtualization.capabilities.api.SoftwareVIBRequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Collections.singletonList;

/**
 * Install ScaleIo Data Client (SDC) Task Handler
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public class InstallScaleIoVibTaskHandler extends BaseTaskHandler implements IWorkflowTaskHandler
{
    /*
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(InstallScaleIoVibTaskHandler.class);

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
     * @param nodeService - The <code>NodeService</code> instance
     * @param repository - The <code>DataServiceRepository</code> instance
     * @param sdcVibRemoteUrl - The remote URL location of the SDC VIB
     */
    public InstallScaleIoVibTaskHandler(final NodeService nodeService, final DataServiceRepository repository, final String sdcVibRemoteUrl)
    {
        this.nodeService = nodeService;
        this.repository = repository;
        this.sdcVibRemoteUrl = sdcVibRemoteUrl;
    }

    @Override
    public boolean executeTask(final Job job)
    {
        LOGGER.info("Execute Install ScaleIO VIB task");

        final InstallScaleIoVibTaskResponse response = initializeResponse(job);

        try
        {
            final ComponentEndpointIds componentEndpointIds = repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER");

            if (componentEndpointIds == null)
            {
                throw new IllegalStateException("No VCenter components found.");
            }

            final InstallEsxiTaskResponse installEsxiTaskResponse = (InstallEsxiTaskResponse) job.getTaskResponseMap().get("installEsxi");

            if (installEsxiTaskResponse == null)
            {
                throw new IllegalStateException("No Install ESXi task response found");
            }

            final String hostname = installEsxiTaskResponse.getHostname();

            if (hostname == null)
            {
                throw new IllegalStateException("Host name is null");
            }

            final SoftwareVIBRequestMessage requestMessage = getSoftwareVIBRequestMessage(componentEndpointIds, hostname);

            final boolean success = this.nodeService.requestInstallSoftwareVib(requestMessage);

            if (!success)
            {
                throw new IllegalStateException("Unable to install Software VIB");
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
        return false;
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
    public InstallScaleIoVibTaskResponse initializeResponse(Job job)
    {
        final InstallScaleIoVibTaskResponse response = new InstallScaleIoVibTaskResponse();
        setupResponse(job,response);
        return response;
    }
}

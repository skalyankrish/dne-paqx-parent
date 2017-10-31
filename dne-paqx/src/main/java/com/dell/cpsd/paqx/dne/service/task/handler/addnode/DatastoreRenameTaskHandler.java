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
import com.dell.cpsd.paqx.dne.service.model.DatastoreRenameTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.InstallEsxiTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.Status;
import com.dell.cpsd.paqx.dne.service.task.handler.BaseTaskHandler;
import com.dell.cpsd.virtualization.capabilities.api.Credentials;
import com.dell.cpsd.virtualization.capabilities.api.DatastoreRenameRequestMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Task responsible for renaming the host datastore after it has been added to vcenter.
 * <p>
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public class DatastoreRenameTaskHandler extends BaseTaskHandler implements IWorkflowTaskHandler
{
    /**
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DatastoreRenameTaskHandler.class);

    /*
     * The <code>NodeService</code> instance
     */
    private final NodeService nodeService;

    /*
    * The <code>DataServiceRepository</code> instance
    */
    private final DataServiceRepository dataServiceRepository;

    /**
     * DatastoreRenameTaskHandler constructor.
     *
     * @param nodeService           - The <code>NodeService</code> instance.
     * @param dataServiceRepository
     * @since 1.0
     */
    public DatastoreRenameTaskHandler(final NodeService nodeService, final DataServiceRepository dataServiceRepository)
    {
        this.nodeService = nodeService;
        this.dataServiceRepository = dataServiceRepository;
    }

    @Override
    public boolean executeTask(final Job job)
    {
        LOGGER.info("Execute datastore rename task");

        final DatastoreRenameTaskResponse response = initializeResponse(job);

        try
        {
            final InstallEsxiTaskResponse installEsxiTaskResponse = (InstallEsxiTaskResponse)job.getTaskResponseMap().get("installEsxi");

            if (installEsxiTaskResponse == null)
            {
                throw new IllegalStateException("No Install ESXi task response found");
            }

            final ComponentEndpointIds vCenterComponentEndpointIdsByEndpointType = dataServiceRepository
                    .getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER");

            if (vCenterComponentEndpointIdsByEndpointType == null)
            {
                throw new IllegalStateException("VCenter Component Endpoint Ids are null");
            }

            final String esxiManagementHostname = installEsxiTaskResponse.getHostname();

            if (esxiManagementHostname == null)
            {
                throw new IllegalStateException("Hostname is null");
            }

            final DatastoreRenameRequestMessage requestMessage = new DatastoreRenameRequestMessage();
            requestMessage.setHostname(esxiManagementHostname);
            requestMessage.setCredentials(new Credentials(vCenterComponentEndpointIdsByEndpointType.getEndpointUrl(), null, null));
            requestMessage.setComponentEndpointIds(new com.dell.cpsd.virtualization.capabilities.api.ComponentEndpointIds(
                    vCenterComponentEndpointIdsByEndpointType.getComponentUuid(),
                    vCenterComponentEndpointIdsByEndpointType.getEndpointUuid(),
                    vCenterComponentEndpointIdsByEndpointType.getCredentialUuid()));

            final String newDatastoreName = this.nodeService.requestDatastoreRename(requestMessage);

            if (StringUtils.isEmpty(newDatastoreName))
            {
                throw new IllegalStateException("Datastore rename failed");
            }

            response.setDatastoreName(newDatastoreName);
            response.setWorkFlowTaskStatus(Status.SUCCEEDED);
            return true;
        }
        catch (Exception ex)
        {
            LOGGER.error("Error during datastore rename", ex);
            response.addError(ex.toString());
        }

        response.setWorkFlowTaskStatus(Status.FAILED);
        return false;
    }

    @Override
    public DatastoreRenameTaskResponse initializeResponse(Job job)
    {
        final DatastoreRenameTaskResponse response = new DatastoreRenameTaskResponse();
        setupResponse(job, response);
        return response;
    }
}

/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.task.handler.preprocess;

import com.dell.cpsd.paqx.dne.domain.IWorkflowTaskHandler;
import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.paqx.dne.service.model.DiscoverVCenterTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.Status;
import com.dell.cpsd.paqx.dne.service.task.handler.BaseTaskHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO: Document Usage
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public class DiscoverVCenterTaskHandler extends BaseTaskHandler implements IWorkflowTaskHandler
{
    /**
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscoverVCenterTaskHandler.class);

    /**
     * The <code>NodeService</code> instance
     */
    private final NodeService           nodeService;
    private final DataServiceRepository repository;

    public DiscoverVCenterTaskHandler(final NodeService nodeService, final DataServiceRepository repository)
    {
        this.nodeService = nodeService;
        this.repository = repository;
    }

    @Override
    public boolean executeTask(final Job job)
    {
        LOGGER.info("Execute Discover VCenter task");

        final DiscoverVCenterTaskResponse response = initializeResponse(job);

        try
        {
            final ComponentEndpointIds componentEndpointIds = repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER");

            if (componentEndpointIds == null)
            {
                throw new IllegalStateException("No VCenter components found.");
            }

            final boolean success = this.nodeService.requestDiscoverVCenter(componentEndpointIds, job.getId().toString());

            if (!success)
            {
                throw new IllegalStateException("Request for Discover VCenter failed");
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

    @Override
    public DiscoverVCenterTaskResponse initializeResponse(Job job)
    {
        final DiscoverVCenterTaskResponse response = new DiscoverVCenterTaskResponse();
        setupResponse(job,response);
        return response;
    }
}

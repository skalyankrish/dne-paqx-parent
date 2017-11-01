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
import com.dell.cpsd.paqx.dne.service.model.Status;
import com.dell.cpsd.paqx.dne.service.model.TaskResponse;
import com.dell.cpsd.paqx.dne.service.task.handler.BaseTaskHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Task responsible for cleaning in memory database.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public class CleanInMemoryDatabaseTaskHandler extends BaseTaskHandler implements IWorkflowTaskHandler
{
    /*
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CleanInMemoryDatabaseTaskHandler.class);

    /*
     * The <code>NodeService</code> instance
     */
    private final NodeService           nodeService;
    private final DataServiceRepository repository;

    /**
     * CleanInMemoryDatabaseTaskHandler constructor.
     *
     * @param nodeService - The <code>NodeService</code> instance.
     * @since 1.0
     */
    public CleanInMemoryDatabaseTaskHandler(NodeService nodeService, DataServiceRepository repository)
    {
        this.nodeService = nodeService;
        this.repository = repository;
    }

    @Override
    public boolean executeTask(Job job)
    {
        /*
         * This step is an workaround for single node.
         */
        LOGGER.info("Execute CleanInMemoryDatabaseTaskHandler task");
        boolean result = false;

        TaskResponse response = initializeResponse(job);
        response.setWorkFlowTaskStatus(Status.FAILED);

        try
        {
            result = repository.cleanInMemoryDatabase();
            if (result)
            {
                response.setWorkFlowTaskStatus(Status.SUCCEEDED);
            }
        }
        catch (Exception ex)
        {
            LOGGER.error("Error cleaning in memory database: " + ex);
        }

        return result;
    }
}

/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.task.handler.addnode;

import java.util.ArrayList;
import java.util.List;

import com.dell.cpsd.paqx.dne.domain.node.DiscoveredNodeInfo;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.model.*;
import com.dell.cpsd.service.system.definition.api.*;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dell.cpsd.paqx.dne.domain.IWorkflowTaskHandler;
import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.service.task.handler.BaseTaskHandler;
import com.dell.cpsd.sdk.AMQPClient;
import org.springframework.util.StringUtils;

/**
 * Task responsible for adding a discovered node to the system definition.
 * 
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 * 
 * @since 1.0
 */
public class AddNodeToSystemDefinitionTaskHandler extends BaseTaskHandler implements IWorkflowTaskHandler
{
    /*
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AddNodeToSystemDefinitionTaskHandler.class);

    /*
     * The <code>AMQPClient</code> instance
     */
    private final AMQPClient    sdkAMQPClient;
    /*
     * Reference to H2 repository
     */
    private final DataServiceRepository repository;

    /*
    * The component on which to base new components.
    */
    private static final String COMPONENT_SERVER_TEMPLATE = "COMMON-DELL-POWEREDGE";

    /**
     * AddNodeToSystemDefinitionTaskHandler constructor.
     * 
     * @param sdkAMQPClient
     *            - The <code>AMQPClient</code> instance.
     *
     * @param repository
     * @since 1.0
     */
    public AddNodeToSystemDefinitionTaskHandler(AMQPClient sdkAMQPClient, DataServiceRepository repository)
    {
        this.sdkAMQPClient = sdkAMQPClient;
        this.repository = repository;
    }

    /**
     * Perform the task of adding a discovered node to the system definition.
     * 
     * @param job
     *            - The <code>Job</code> this task is part of.
     * 
     * @since 1.0
     */
    @Override
    public boolean executeTask(Job job)
    {
        LOGGER.info("Execute AddNodeToSystemDefinitionTaskHandler task");

        TaskResponse response = initializeResponse(job);

        try
        {
            final NodeExpansionRequest inputParams = job.getInputParams();

            if (inputParams == null)
            {
                throw new IllegalStateException("Job input parameters are null");
            }

            final String symphonyUuid = inputParams.getSymphonyUuid();

            if (StringUtils.isEmpty(symphonyUuid))
            {
                throw new IllegalStateException("Symphony Id is null");
            }

            List<ConvergedSystem> allConvergedSystems = this.sdkAMQPClient.getConvergedSystems();
            if (CollectionUtils.isEmpty(allConvergedSystems))
            {
                throw new IllegalStateException("No converged systems found.");
            }

            ConvergedSystem system = allConvergedSystems.get(0);
            ComponentsFilter componentsFilter = new ComponentsFilter();
            componentsFilter.setSystemUuid(system.getUuid());

            List<ConvergedSystem> systemDetails = this.sdkAMQPClient.getComponents(componentsFilter);
            if (CollectionUtils.isEmpty(systemDetails))
            {
                throw new IllegalStateException("No converged system found.");
            }

            ConvergedSystem systemToBeUpdated = systemDetails.get(0);

            DiscoveredNodeInfo nodeInfo = repository.getDiscoveredNodeInfo(symphonyUuid);
            if( nodeInfo == null)
            {
                {
                    throw new IllegalStateException("No discovered node info.");
                }
            }
            Component newNode = new Component();
            List<String> parentGroups = new ArrayList<>();
            List<String> endpoints = new ArrayList<>();
            parentGroups.add("SystemCompute");
            endpoints.add("RACKHD-EP");
            newNode.setUuid(nodeInfo.getSymphonyUuid());
            newNode.setIdentity(new Identity("SERVER", nodeInfo.getSymphonyUuid(), nodeInfo.getSymphonyUuid(), nodeInfo.getSerialNumber(),null));
            newNode.setDefinition(new Definition(nodeInfo.getProductFamily(), nodeInfo.getProduct(), nodeInfo.getModelFamily(), nodeInfo.getModel()));
            newNode.setParentGroupUuids(this.mapGroupNamesToUUIDs(parentGroups, systemToBeUpdated.getGroups()));
            newNode.setEndpoints(new ArrayList<>());

            this.sdkAMQPClient.addComponent(systemToBeUpdated, newNode, endpoints, COMPONENT_SERVER_TEMPLATE);

            // Need to make sure the node was really added.
            // The SDK has a habit of swallowing exceptions making it difficult to know
            // if there was an error...
            List<ConvergedSystem>  updateSystemDetails = this.sdkAMQPClient.getComponents(componentsFilter);
            ConvergedSystem systemThatWasUpdated = updateSystemDetails.get(0);

            Component newComponent = systemThatWasUpdated.getComponents().stream()
                .filter(c -> c.getIdentity().getIdentifier().equals(newNode.getIdentity().getIdentifier()))
                    .findFirst()
                    .orElse(null);

            if (newComponent == null)
            {
                throw new IllegalStateException("Discovered node was not added to the system definition.");
            }

            response.setWorkFlowTaskStatus(Status.SUCCEEDED);
            return true;
        }
        catch (Exception e)
        {
            LOGGER.error("Error adding node to the system definition", e);
            response.addError(e.toString());
        }

        response.setWorkFlowTaskStatus(Status.FAILED);
        return false;
    }

    /*
     * Given a list of group names map each one to its corresponding UUID.
     */
    private List<String> mapGroupNamesToUUIDs(List<String> groupNames, List<Group> groups)
    {
        List<String> groupUuids = new ArrayList<>();
        groups.forEach(group -> {
            if (groupNames.contains(group.getName()))
            {
                groupUuids.add(group.getUuid());
            }
        });
        return groupUuids;
    }
}

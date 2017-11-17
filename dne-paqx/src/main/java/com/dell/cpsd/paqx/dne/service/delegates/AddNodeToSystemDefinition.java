/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import com.dell.cpsd.sdk.AMQPClient;
import com.dell.cpsd.service.system.definition.api.Group;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.List;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;

@org.springframework.stereotype.Component
@Scope("prototype")
@Qualifier("addNodeToSystemDefinition")
public class AddNodeToSystemDefinition extends BaseWorkflowDelegate
{
    /*
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AddNodeToSystemDefinition.class);

    /*
     * The <code>AMQPClient</code> instance
     */
    private final AMQPClient sdkAMQPClient;
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
     * @param sdkAMQPClient - The <code>AMQPClient</code> instance.
     * @param repository
     * @since 1.0
     */
    @Autowired
    public AddNodeToSystemDefinition(AMQPClient sdkAMQPClient, DataServiceRepository repository)
    {
        this.sdkAMQPClient = sdkAMQPClient;
        this.repository = repository;
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

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Execute AddNodeToSystemDefinitionTaskHandler task");
        final String taskMessage = "Add Node To System Definition";
        final NodeDetail nodeDetail = (NodeDetail) delegateExecution.getVariable(NODE_DETAIL);
       /* try
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
            if (nodeInfo == null)
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
            newNode.setIdentity(new Identity("SERVER", nodeInfo.getSymphonyUuid(), nodeInfo.getSymphonyUuid(),
                                             nodeInfo.getSerialNumber(), null));
            newNode.setDefinition(
                    new Definition(nodeInfo.getProductFamily(), nodeInfo.getProduct(), nodeInfo.getModelFamily(),
                                   nodeInfo.getModel()));
            newNode.setParentGroupUuids(this.mapGroupNamesToUUIDs(parentGroups, systemToBeUpdated.getGroups()));
            newNode.setEndpoints(new ArrayList<>());

            this.sdkAMQPClient.addComponent(systemToBeUpdated, newNode, endpoints, COMPONENT_SERVER_TEMPLATE);

            // Need to make sure the node was really added.
            // The SDK has a habit of swallowing exceptions making it difficult to know
            // if there was an error...
            List<ConvergedSystem> updateSystemDetails = this.sdkAMQPClient.getComponents(componentsFilter);
            ConvergedSystem systemThatWasUpdated = updateSystemDetails.get(0);

            Component newComponent = systemThatWasUpdated.getComponents().stream().filter(
                    c -> c.getIdentity().getIdentifier().equals(newNode.getIdentity().getIdentifier())).findFirst()
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
        return false;*/
        LOGGER.info(taskMessage + " on Node " + nodeDetail.getServiceTag() + " was successful.");
        updateDelegateStatus(taskMessage + " on Node " + nodeDetail.getServiceTag() + " was successful.");

    }

}

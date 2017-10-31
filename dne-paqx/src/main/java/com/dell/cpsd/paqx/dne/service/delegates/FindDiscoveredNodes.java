/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.service.NodeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Qualifier("findDiscoveredNodes")
public class FindDiscoveredNodes extends BaseWorkflowDelegate
{
    /*
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FindDiscoveredNodes.class);

    /*
     * The <code>NodeService</code> instance
     */
    private final NodeService nodeService;

    @Autowired
    public FindDiscoveredNodes(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Execute Find Discovered Nodes");

        /*List<NodeInfo> nodeInfoList = null;
        try
        {
            List<DiscoveredNode> discoveredNodesResponse = nodeService.listDiscoveredNodes();

            if (CollectionUtils.isNotEmpty(discoveredNodesResponse))
            {
                nodeInfoList = discoveredNodesResponse.stream().filter(node -> com.dell.cpsd.DiscoveredNode.AllocationStatus.DISCOVERED.equals(node.getNodeStatus()))
                        .map(n -> new NodeInfo(n.getConvergedUuid(),  NodeStatus.valueOf(n.getNodeStatus().toString())))
                        .collect(Collectors.toList());
            }
        }
        catch (Exception e)
        {
            LOGGER.error("An unexpected Exception occurred while attempting to retrieve the list of discovered nodes", e);
            updateDelegateStatus("An Unexpected exception occurred trying to retrieve the list of Discovered Nodes.  Reason: " + e.getMessage());
            throw new BpmnError(NO_DISCOVERED_NODES, "An Unexpected exception occurred trying to retrieve the list of Discovered Nodes.  Reason: " + e.getMessage());
        }
        if (CollectionUtils.isNotEmpty(nodeInfoList)) {
            delegateExecution.setVariable(DISCOVERED_NODES, nodeInfoList);
        } else {
            LOGGER.error(NO_DISCOVERED_NODES, "There are no nodes currently discovered in Rack HD");
            updateDelegateStatus("There are no nodes currently discovered in Rack HD");
            throw new BpmnError(NO_DISCOVERED_NODES, "There are no nodes currently discovered in Rack HD");
        }*/
    }
}

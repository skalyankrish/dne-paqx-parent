/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.domain.node.DiscoveredNodeInfo;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.VERIFY_NODES_SELECTED_FAILED;

@Component
@Scope("prototype")
@Qualifier("verifyNodesSelected")
public class VerifyNodesSelected extends BaseWorkflowDelegate
{
    private static final Logger LOGGER = LoggerFactory.getLogger(VerifyNodesSelected.class);

    private final NodeService nodeService;

    @Autowired
    public VerifyNodesSelected(final NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Execute Verify Selected Nodes");
        updateDelegateStatus("Attempting to verify selected Nodes are still available.");

        List<NodeDetail> nodeDetails = (List<NodeDetail>) delegateExecution.getVariable(DelegateConstants.NODE_DETAILS);
        if (CollectionUtils.isEmpty(nodeDetails)) {
            final String message = "The List of Node Detail was not found!  Please add at least one Node Detail and try again.";
            LOGGER.error(message);
            updateDelegateStatus(message);
            throw new BpmnError(VERIFY_NODES_SELECTED_FAILED,
                                message);
        }

        try
        {
            List<DiscoveredNodeInfo> discoveredNodes = nodeService.listDiscoveredNodeInfo();
            Set<String> serviceTags = new HashSet<>();
            if (CollectionUtils.isNotEmpty(discoveredNodes)) {
                discoveredNodes.forEach(dn -> serviceTags.add(dn.getSerialNumber()));
            }
            List<NodeDetail> missing = nodeDetails.stream().filter(nodeDetail -> !serviceTags.contains(nodeDetail.getServiceTag())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(missing)) {
                final String message = "The following Nodes have already been added.  Please remove the Nodes from the request and try again.  Nodes currently in use: " +
                                       StringUtils.join(missing.stream().map(NodeDetail::getServiceTag).collect(Collectors.toList()), ", ");
                LOGGER.error(message);
                updateDelegateStatus(message);
                throw new BpmnError(VERIFY_NODES_SELECTED_FAILED,
                                    message);
            }
        }
        catch(Exception e) {
            final String message = "An Unexpected Exception occurred attempting to verify selected Nodes.";
            LOGGER.error(message, e);
            updateDelegateStatus(message);
            throw new BpmnError(VERIFY_NODES_SELECTED_FAILED,
                                message);
        }


        LOGGER.info("All selected Nodes are available.");
        updateDelegateStatus("All selected Nodes are available.");

    }
}

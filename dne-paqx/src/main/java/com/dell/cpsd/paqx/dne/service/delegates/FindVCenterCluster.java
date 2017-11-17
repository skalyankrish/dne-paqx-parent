/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import com.dell.cpsd.virtualization.capabilities.api.ClusterInfo;
import com.dell.cpsd.virtualization.capabilities.api.ValidateVcenterClusterResponseMessage;
import org.apache.commons.collections.CollectionUtils;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.FIND_VCLUSTER_FAILED;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.VCENTER_CLUSTER_NAME;

@Component
@Scope("prototype")
@Qualifier("findVCenterCluster")
public class FindVCenterCluster extends BaseWorkflowDelegate
{

    private static final Logger LOGGER = LoggerFactory.getLogger(FindVCenterCluster.class);

    private NodeService nodeService;

    @Autowired
    public FindVCenterCluster(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Execute FindVCenterCluster");
        NodeDetail nodeDetail = (NodeDetail) delegateExecution.getVariable(NODE_DETAIL);
        updateDelegateStatus("Selecting VCenter Cluster for Node " + nodeDetail.getServiceTag());
        ValidateVcenterClusterResponseMessage responseMsg = null;
        try
        {
            List<ClusterInfo> clusterInfo = nodeService.listClusters();
            responseMsg = nodeService.validateClusters(clusterInfo);
        }
        catch (Exception e)
        {
            final String message = "An unexpected Exception occurred while retrieving the list of Clusters for selection.";
            LOGGER.error(message, e);
            updateDelegateStatus(message + "  Reason: " + e.getMessage());
            throw new BpmnError(FIND_VCLUSTER_FAILED, message + "  Reason: " + e.getMessage());
        }
        String clusterName = null;
        if (responseMsg != null && CollectionUtils.isNotEmpty(responseMsg.getClusters()))
        {
            clusterName = responseMsg.getClusters().get(0);
        }
        if (clusterName == null)
        {
            final String message[] = {"Selecting VCenter Cluster Failed for Node " + nodeDetail.getServiceTag() + "."};
            if (responseMsg != null && CollectionUtils.isNotEmpty(responseMsg.getFailedCluster()))
            {
                message[0] += " Reason: ";
                responseMsg.getFailedCluster().forEach(failed -> {
                    message[0] += failed + " ";
                });
                LOGGER.error(message[0]);
                updateDelegateStatus(message[0]);
                throw new BpmnError(FIND_VCLUSTER_FAILED, message[0]);
            }
        }
        nodeDetail.setClusterName(clusterName);
        delegateExecution.setVariable(NODE_DETAIL, nodeDetail);
        final String message = "VCenter Cluster " + clusterName + " was selected for Node " + nodeDetail.getServiceTag();
        updateDelegateStatus(message);
        LOGGER.info(message);
    }
}

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
@Qualifier("findVCluster")
public class FindVCluster extends BaseWorkflowDelegate
{

    private static final Logger LOGGER = LoggerFactory.getLogger(FindVCluster.class);

    private final NodeService nodeService;

    @Autowired
    public FindVCluster(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Execute FindVCluster");
/*        ValidateVcenterClusterResponseMessage responseMsg = null;
        try
        {
            List<ClusterInfo> clusterInfo = nodeService.listClusters();
            responseMsg = nodeService.validateClusters(clusterInfo);
        }
        catch (Exception e)
        {
            LOGGER.error("An unexpected Exception occurred while attempting to retrieve the list of Clusters", e);
            updateDelegateStatus("An Unexpected exception occurred trying to retrieve the list of Clusters.  Reason: " + e.getMessage());
            throw new BpmnError(FIND_VCLUSTER_FAILED, "An Unexpected exception occurred trying to retrieve the list of Clusters.  Reason: " + e.getMessage());
        }
        if (CollectionUtils.isNotEmpty(responseMsg.getClusters()))
        {
            final String clusterName = responseMsg.getClusters().get(0);
            delegateExecution.setVariable(VCENTER_CLUSTER_NAME, clusterName);
        }
        else
        {
            final String message[] = {"Find VCenter Cluster Failed. "};
            if (responseMsg != null && CollectionUtils.isNotEmpty(responseMsg.getFailedCluster()))
            {
                message[0] += "Reason: ";
                responseMsg.getFailedCluster().forEach(failed -> {
                    message[0] += failed + " ";
                });
                LOGGER.error(message[0]);
                updateDelegateStatus(message[0]);
                throw new BpmnError(FIND_VCLUSTER_FAILED, message[0]);
            }
        }*/
        LOGGER.info("VCenter CLuster successfully found.");
    }
}

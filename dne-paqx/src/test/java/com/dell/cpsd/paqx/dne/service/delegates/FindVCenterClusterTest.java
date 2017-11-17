/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import com.dell.cpsd.service.common.client.exception.ServiceTimeoutException;
import com.dell.cpsd.virtualization.capabilities.api.ClusterInfo;
import com.dell.cpsd.virtualization.capabilities.api.MessageProperties;
import com.dell.cpsd.virtualization.capabilities.api.ValidateVcenterClusterResponseMessage;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.FIND_VCLUSTER_FAILED;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@RunWith(MockitoJUnitRunner.class)
public class FindVCenterClusterTest
{

    private FindVCenterCluster findVCenterCluster;
    private NodeDetail nodeDetail;
    private ValidateVcenterClusterResponseMessage responseMessage;
    private List<String> clusterList;

    @Mock
    private NodeService nodeService;

    @Mock
    private DelegateExecution delegateExecution;

    @Mock
    private MessageProperties messageProperties;

    @Before
    public void setUp() throws Exception {
        findVCenterCluster = new FindVCenterCluster(nodeService);

        nodeDetail = new NodeDetail("1", "abc");
        doReturn(nodeDetail). when(delegateExecution).getVariable(NODE_DETAIL);

        List<ClusterInfo> clusterInfos = new ArrayList<>();
        ClusterInfo clusterInfo = new ClusterInfo("TestCluster", 1);
        clusterInfos.add(clusterInfo);
        doReturn(clusterInfos).when(nodeService).listClusters();

        clusterList = new ArrayList<>();
        clusterList.add("TestCluster");
        responseMessage = new ValidateVcenterClusterResponseMessage(messageProperties, clusterList, null, "Description" );
        doReturn(responseMessage).when(nodeService).validateClusters(any());
    }

    @Test
    public void testSuccessful() {
        findVCenterCluster.delegateExecute(delegateExecution);
        assertEquals(nodeDetail.getClusterName(), "TestCluster");
    }

    @Test
    public void testRequestClusterException() throws Exception {
        try
        {
            doThrow(new ServiceTimeoutException("Timeout")).when(nodeService).listClusters();
            findVCenterCluster.delegateExecute(delegateExecution);
            fail("Should not get here!");
        } catch( BpmnError bpmnError) {
            assertEquals(bpmnError.getErrorCode(), FIND_VCLUSTER_FAILED);
            assertEquals(bpmnError.getMessage(), "An unexpected Exception occurred while retrieving the list of Clusters for selection.  Reason: Timeout");
        }
    }

    @Test
    public void testClusterFailedException() throws Exception {
        responseMessage = new ValidateVcenterClusterResponseMessage(messageProperties, null, clusterList, "Description" );
        doReturn(responseMessage).when(nodeService).validateClusters(any());
        try
        {
            findVCenterCluster.delegateExecute(delegateExecution);
            fail("Should not get here!");
        } catch( BpmnError bpmnError) {
            assertEquals(bpmnError.getErrorCode(), FIND_VCLUSTER_FAILED);
            assertEquals(bpmnError.getMessage(), "Selecting VCenter Cluster Failed for Node abc. Reason: TestCluster ");
        }
    }
}

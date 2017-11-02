/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOData;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOProtectionDomain;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOStoragePool;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants;
import com.dell.cpsd.paqx.dne.service.model.IdracNetworkSettingsRequest;
import com.dell.cpsd.service.common.client.exception.ServiceTimeoutException;
import com.dell.cpsd.virtualization.capabilities.api.ClusterInfo;
import com.dell.cpsd.virtualization.capabilities.api.ValidateVcenterClusterResponseMessage;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.VCENTER_CLUSTER_NAME;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FindVClusterTest {

    private FindVCluster findVCluster;
    private NodeService nodeService;
    private DelegateExecution delegateExecution;
    private ScaleIOData scaleIOData;
    private List<ScaleIOProtectionDomain> protectionDomains;
    private ScaleIOProtectionDomain scaleIOProtectionDomain;
    private NodeDetail nodeDetail;
    private IdracNetworkSettingsRequest idracNetworkSettingsRequest;
    private List<ScaleIOData> scaleIODataList;
    private ScaleIOStoragePool scaleIOStoragePool;
    private ValidateVcenterClusterResponseMessage storageResponseMessage;
    private ClusterInfo clusterInfo;
    private List<ClusterInfo> clusterInfoList;
    List<String> message = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        nodeService = mock(NodeService.class);
        findVCluster = new FindVCluster(nodeService);
        delegateExecution = mock(DelegateExecution.class);
        nodeDetail = new NodeDetail();
        idracNetworkSettingsRequest = mock(IdracNetworkSettingsRequest.class);
        clusterInfoList = new ArrayList<ClusterInfo>();
        clusterInfo = new ClusterInfo("abc", 1);
        message.add("A");
        message.add("B");
    }

    @Ignore
    @Test
    public void setFindScaleIOException() throws Exception {
        try {
            clusterInfoList.add(clusterInfo);
            when(nodeService.listClusters()).thenReturn(clusterInfoList);
            given(nodeService.validateClusters(any())).willThrow(new ServiceTimeoutException());
            findVCluster.delegateExecute(delegateExecution);
        } catch (BpmnError error) {
            assertTrue(error.getErrorCode().equals(DelegateConstants.FIND_VCLUSTER_FAILED));
            assertTrue(error.getMessage().contains("An Unexpected exception occurred trying to retrieve the list of Clusters."));
        }
    }

    @Ignore
    @Test
    public void validClusters() throws Exception {
        storageResponseMessage = new ValidateVcenterClusterResponseMessage();
        storageResponseMessage.setClusters(message);
        when(nodeService.validateClusters(any())).thenReturn(storageResponseMessage);
        findVCluster.delegateExecute(delegateExecution);
        verify(delegateExecution, times(1)).setVariable(VCENTER_CLUSTER_NAME, "A");
    }

    @Ignore
    @Test
    public void invalidClusters() throws Exception {
        try {
            storageResponseMessage = new ValidateVcenterClusterResponseMessage();
            storageResponseMessage.setFailedCluster(message);
            when(nodeService.validateClusters(any())).thenReturn(storageResponseMessage);
            findVCluster.delegateExecute(delegateExecution);
        } catch (BpmnError error) {
            assertTrue(error.getErrorCode().equals(DelegateConstants.FIND_VCLUSTER_FAILED));
            assertTrue(error.getMessage().contains("Find VCenter Cluster Failed. Reason: A B"));
        }
    }
}

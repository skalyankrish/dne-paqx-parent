/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.task.handler.preprocess;

import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.repository.InMemoryJobRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.WorkflowService;
import com.dell.cpsd.paqx.dne.service.WorkflowServiceImpl;
import com.dell.cpsd.paqx.dne.service.model.NodeExpansionRequest;
import com.dell.cpsd.paqx.dne.service.model.NodeInfo;
import com.dell.cpsd.paqx.dne.service.model.NodeStatus;
import com.dell.cpsd.paqx.dne.service.model.TaskResponse;
import com.dell.cpsd.paqx.dne.service.workflow.preprocess.PreProcessService;
import com.dell.cpsd.paqx.dne.service.workflow.preprocess.PreProcessTaskConfig;
import com.dell.cpsd.service.common.client.exception.ServiceExecutionException;
import com.dell.cpsd.service.common.client.exception.ServiceTimeoutException;
import com.dell.cpsd.virtualization.capabilities.api.ClusterInfo;
import com.dell.cpsd.virtualization.capabilities.api.ValidateVcenterClusterResponseMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FindVClusterTaskHandlerTest
{
    @Mock
    private NodeService nodeService = null;

    /*
     * The job running the add node to system definition task handler.
     */
    private Job         job         = null;

    /**
     * The test setup.
     * 
     * @since 1.0
     */
    @Before
    public void setUp()
    {
        PreProcessTaskConfig preprocessConfig = new PreProcessTaskConfig();
        WorkflowService workflowService = new WorkflowServiceImpl(new InMemoryJobRepository(), preprocessConfig.preProcessWorkflowSteps());

        PreProcessService preprocessService = new PreProcessService();
        preprocessService.setWorkflowService(workflowService);

        this.job = preprocessService.createWorkflow("preProcessWorkflow", "startPreProcessWorkflow", "submitted");

        NodeExpansionRequest nodeExpansionRequest = new NodeExpansionRequest("idracIpAddress", "idracGatewayIpAddress", "idracSubnetMask",
                "managementIpAddress", "esxiKernelIpAddress1", "esxiKernelIpAddress2", "esxiManagementHostname", "scaleIOSVMDataIpAddress1",
                "scaleIOSVMDataIpAddress2", "scaleIOSVMManagementIpAddress", "nodeId", "symphonyUuid", "clausterName");
        this.job.setInputParams(nodeExpansionRequest);

        TaskResponse response = new TaskResponse();
        NodeInfo nodeInfo = new NodeInfo("symphonyUuid", NodeStatus.DISCOVERED);
        Map<String, String> results = new HashMap<>();

        results.put("symphonyUuid", nodeInfo.getSymphonyUuid());
        results.put("nodeStatus", nodeInfo.getNodeStatus().toString());

        response.setResults(results);

        this.job.addTaskResponse("findAvailableNodes", response);

        this.job.changeToNextStep("findVCluster");
    }

    /**
     * Test successful execution of FindVClusterTaskHandler.executeTask() method
     * 
     * @throws ServiceExecutionException
     * @throws ServiceTimeoutException
     * 
     * @since 1.0
     */
    @Test
    public void testExecuteTask_successful_case() throws ServiceTimeoutException, ServiceExecutionException {
        ClusterInfo vCluster = new ClusterInfo("clusterTest1", 2);
        List<ClusterInfo> vClusters = new ArrayList<>();
        vClusters.add(vCluster);

        List<String> clusterNames = new ArrayList<>();
        clusterNames.add("clusterTest1");
        ValidateVcenterClusterResponseMessage resMsg = new ValidateVcenterClusterResponseMessage();
        resMsg.setClusters(clusterNames);

        when(this.nodeService.listClusters()).thenReturn(vClusters);
        when(this.nodeService.validateClusters(vClusters)).thenReturn(resMsg);

        FindVClusterTaskHandler instance = new FindVClusterTaskHandler(this.nodeService);
        boolean expectedResult = true;
        boolean actualResult = instance.executeTask(this.job);

        assertEquals(expectedResult, actualResult);
    }

    /**
     * Test unsuccessful execution of FindVClusterTaskHandler.executeTask() method
     * 
     * @throws ServiceExecutionException
     * @throws ServiceTimeoutException
     * 
     * @since 1.0
     */
    @Test
    public void testExecuteTask_unsuccessful_case() throws ServiceTimeoutException, ServiceExecutionException {
        ClusterInfo vCluster = new ClusterInfo("clusterTest1", 2);
        List<ClusterInfo> vClusters = new ArrayList<>();
        vClusters.add(vCluster);

        List<String> failedClusterNames = new ArrayList<>();
        failedClusterNames.add("REQUIRED:  No more than 1000 nodes per cluster -- Cluster test1 with 1000 nodes failed rule checking.\n");
        failedClusterNames.add("REQUIRED:  No more than 1000 nodes per cluster -- Cluster test4 with 2000 nodes failed rule checking.\n");
        ValidateVcenterClusterResponseMessage resMsg = new ValidateVcenterClusterResponseMessage();
        resMsg.setClusters(Collections.emptyList());
        resMsg.setFailedCluster(failedClusterNames);

        when(this.nodeService.listClusters()).thenReturn(vClusters);
        when(this.nodeService.validateClusters(vClusters)).thenReturn(resMsg);


        FindVClusterTaskHandler instance = new FindVClusterTaskHandler(this.nodeService);
        boolean expectedResult = false;
        boolean actualResult = instance.executeTask(this.job);

        assertEquals(expectedResult, actualResult);
    }
}

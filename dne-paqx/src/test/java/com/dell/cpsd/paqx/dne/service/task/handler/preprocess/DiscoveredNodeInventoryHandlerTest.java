/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.task.handler.preprocess;

import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.domain.inventory.NodeInventory;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.repository.InMemoryJobRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.WorkflowService;
import com.dell.cpsd.paqx.dne.service.WorkflowServiceImpl;
import com.dell.cpsd.paqx.dne.service.model.FirstAvailableDiscoveredNodeResponse;
import com.dell.cpsd.paqx.dne.service.model.NodeExpansionRequest;
import com.dell.cpsd.paqx.dne.service.model.NodeInfo;
import com.dell.cpsd.paqx.dne.service.model.NodeStatus;
import com.dell.cpsd.paqx.dne.service.model.TaskResponse;
import com.dell.cpsd.paqx.dne.service.workflow.preprocess.PreProcessService;
import com.dell.cpsd.paqx.dne.service.workflow.preprocess.PreProcessTaskConfig;
import com.dell.cpsd.service.common.client.exception.ServiceExecutionException;
import com.dell.cpsd.service.common.client.exception.ServiceTimeoutException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Uni test for DiscoveredNodeInventoryHandler task.
 * <p>
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */

@RunWith(MockitoJUnitRunner.class)
public class DiscoveredNodeInventoryHandlerTest
{
    @Mock
    private NodeService nodeService;

    @Mock
    private DataServiceRepository repository;

    /*
     * The job running the add node to system definition task handler.
     */
    private Job job = null;

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

        FirstAvailableDiscoveredNodeResponse response = new FirstAvailableDiscoveredNodeResponse();
        response.setWorkFlowTaskName("findAvailableNodes");
        NodeInfo nodeInfo = new NodeInfo("symphonyUuid", NodeStatus.DISCOVERED);
        response.setNodeInfo(nodeInfo);
        Map<String, String> results = new HashMap<>();

        results.put("symphonyUuid", nodeInfo.getSymphonyUuid());
        results.put("nodeStatus", nodeInfo.getNodeStatus().toString());

        response.setResults(results);

        this.job.addTaskResponse("discoverNodeInventory", response);

        this.job.changeToNextStep("discoverNodeInventory");
    }

    @Test
    public void testExecuteTask_successful_case() throws ServiceTimeoutException, ServiceExecutionException
    {
        DiscoverNodeInventoryHandler handler = new DiscoverNodeInventoryHandler(nodeService, repository);
        String nodeInventoryResponse = "FAKE_NODE_INVENTORY";

        when(this.nodeService.listNodeInventory(anyString())).thenReturn(nodeInventoryResponse);
        doReturn(true).when(this.repository).saveNodeInventory(any());

        boolean expectedResult = true;
        boolean actualResult = handler.executeTask(job);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testExecuteTask_unsuccessful_case() throws ServiceTimeoutException, ServiceExecutionException
    {
        DiscoverNodeInventoryHandler handler = new DiscoverNodeInventoryHandler(nodeService, repository);
        String nodeInventoryResponse = null;

        when(this.nodeService.listNodeInventory(anyString())).thenReturn(nodeInventoryResponse);

        boolean expectedResult = false;
        boolean actualResult = handler.executeTask(job);

        assertEquals(expectedResult, actualResult);
        verify(this.repository, times(0)).saveNodeInventory(any(NodeInventory.class));
    }
}

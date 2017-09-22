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
import com.dell.cpsd.paqx.dne.service.model.*;
import com.dell.cpsd.paqx.dne.service.workflow.preprocess.PreProcessService;
import com.dell.cpsd.paqx.dne.service.workflow.preprocess.PreProcessTaskConfig;
import com.dell.cpsd.service.common.client.exception.ServiceExecutionException;
import com.dell.cpsd.service.common.client.exception.ServiceTimeoutException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * The tests for ConfigureBootDeviceIdracTaskHandler.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */

@RunWith(MockitoJUnitRunner.class)
public class ConfigureBootDeviceIdracTaskHandlerTest {

    /*
     * The <code>NodeService</code> instance.
     * @since 1.0
     */
    @Mock
    private NodeService nodeService = null;

    /*
     * The job running the add node to system definition task handler.
     */
    private Job job         = null;
    /**
     * The test setup.
     *
     * @since 1.0
     */
    @Before
    public void setUp(){
        PreProcessTaskConfig preProcessTaskConfig = new PreProcessTaskConfig();
        WorkflowService workflowService = new WorkflowServiceImpl(new InMemoryJobRepository(), preProcessTaskConfig.preProcessWorkflowSteps());

        PreProcessService preProcessService = new PreProcessService();
        preProcessService.setWorkflowService(workflowService);

        this.job = preProcessService.createWorkflow("preProcessWorkflow", "startPreProcessWorkflow", "submitted");
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
        this.job.changeToNextStep("configureBootDeviceIdrac");
    }
    /**
     * Test successful execution of ConfigureBootDeviceIdracTaskHandler.executeTask() method
     *
     * @throws ServiceExecutionException
     * @throws ServiceTimeoutException
     *
     * @since 1.0
     */
    @Test
    public void testExecuteTask_successful_case() throws ServiceTimeoutException, ServiceExecutionException
    {
        BootDeviceIdracStatus bootDeviceIdracStatus = new BootDeviceIdracStatus("SUCCESS",null);
        ArgumentCaptor<ConfigureBootDeviceIdracRequest> requestCaptor = ArgumentCaptor.forClass(ConfigureBootDeviceIdracRequest.class);
        when(this.nodeService.bootDeviceIdracStatus(requestCaptor.capture())).thenReturn(bootDeviceIdracStatus);

        ConfigureBootDeviceIdracTaskHandler instance = new ConfigureBootDeviceIdracTaskHandler(this.nodeService);
        boolean expectedResult = true;
        boolean actualResult = instance.executeTask(this.job);

        assertEquals(expectedResult, actualResult);
        verify(this.nodeService, times(1)).bootDeviceIdracStatus(requestCaptor.capture());
    }

    /**
     * Test unsuccessful execution of ConfigureBootDeviceIdracTaskHandler.executeTask() method
     *
     * @throws ServiceExecutionException
     * @throws ServiceTimeoutException
     *
     * @since 1.0
     */
    @Test
    public void testExecuteTask_unsuccessful_case() throws ServiceTimeoutException, ServiceExecutionException
    {
        BootDeviceIdracStatus bootDeviceIdracStatus = new BootDeviceIdracStatus("FAILED",null);
        ArgumentCaptor<ConfigureBootDeviceIdracRequest> requestCaptor = ArgumentCaptor.forClass(ConfigureBootDeviceIdracRequest.class);
        when(this.nodeService.bootDeviceIdracStatus(requestCaptor.capture())).thenReturn(bootDeviceIdracStatus);

        ConfigureBootDeviceIdracTaskHandler instance = new ConfigureBootDeviceIdracTaskHandler(this.nodeService);
        boolean expectedResult = false;
        boolean actualResult = instance.executeTask(this.job);

        assertEquals(expectedResult, actualResult);
        verify(this.nodeService, times(1)).bootDeviceIdracStatus(requestCaptor.capture());
    }
}

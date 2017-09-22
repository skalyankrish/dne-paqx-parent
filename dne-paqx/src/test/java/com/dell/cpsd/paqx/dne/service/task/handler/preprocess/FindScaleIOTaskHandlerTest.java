/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.task.handler.preprocess;

import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOData;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIODevice;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOProtectionDomain;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOStoragePool;
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
import com.dell.cpsd.service.engineering.standards.EssValidateStoragePoolResponseMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Uni test for find scaleio task.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */

@RunWith(MockitoJUnitRunner.class)
public class FindScaleIOTaskHandlerTest {

    @Mock
    private NodeService nodeService = null;

    /*
     * The job running the add node to system definition task handler.
     */
    private Job job         = null;

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

        this.job.addTaskResponse("findScaleIO", response);

        this.job.changeToNextStep("findScaleIO");
    }

    @Test
    public void testExecuteTask_successful_case() throws ServiceTimeoutException, ServiceExecutionException
    {
        FindScaleIOTaskHandler handler = new FindScaleIOTaskHandler(this.nodeService);
        EssValidateStoragePoolResponseMessage storageResponseMessage = new EssValidateStoragePoolResponseMessage();
        storageResponseMessage.setValidStorage(Arrays.asList("Sp-1"));
        ScaleIOData scaleIOData =  new ScaleIOData();
        ScaleIOStoragePool scaleIOStoragePool = new ScaleIOStoragePool();
        ScaleIOProtectionDomain scaleIOProtectionDomain = new ScaleIOProtectionDomain();
        scaleIOStoragePool.setId("1");
        scaleIOStoragePool.setName("Sp-1");
        scaleIOStoragePool.addDevice(new ScaleIODevice("1", "scaleIOName1", "deviceCurrentPathName", "Normal"));
        scaleIOStoragePool.addDevice(new ScaleIODevice("2", "scaleIOName2", "deviceCurrentPathName", "Normal"));
        scaleIOProtectionDomain.addStoragePool(scaleIOStoragePool);
        scaleIOData.setProtectionDomains(Arrays.asList(scaleIOProtectionDomain));

        when(this.nodeService.listScaleIOData()).thenReturn(Arrays.asList(scaleIOData));
        when(this.nodeService.validateStoragePools(Arrays.asList(scaleIOStoragePool))).thenReturn(storageResponseMessage);


        boolean expectedResult = true;
        boolean actualResult = handler.executeTask(job);

        assertEquals(expectedResult, actualResult);

    }
}

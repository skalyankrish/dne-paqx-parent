
/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class VerfiyNodeDetailTest {

    private VerifyNodeDetail verifyNodeDetail;
    private NodeService nodeService;
    private DataServiceRepository repository;
    private DelegateExecution delegateExecution;
    private NodeDetail nodeDetail;

    @Before
    public void setUp() throws Exception
    {
        nodeService = mock(NodeService.class);
        repository = mock(DataServiceRepository.class);
        verifyNodeDetail = new VerifyNodeDetail();
        delegateExecution = mock(DelegateExecution.class);
        nodeDetail = new NodeDetail();
        nodeDetail.setServiceTag("abc");
        nodeDetail.setvMotionManagementIpAddress("abc");
        nodeDetail.setvMotionManagementSubnetMask("abc");
        nodeDetail.setId("abc");
        nodeDetail.setIdracIpAddress("abc");
        nodeDetail.setIdracGatewayIpAddress("abc");
        nodeDetail.setIdracSubnetMask("abc");
        nodeDetail.setEsxiManagementIpAddress("abc");
        nodeDetail.setEsxiManagementGatewayIpAddress("abc");
        nodeDetail.setEsxiManagementSubnetMask("abc");
        nodeDetail.setEsxiManagementHostname("abc");
        nodeDetail.setScaleIoData1SvmIpAddress("abc");

//        nodeDetail.setScaleIoData1KernelIpAddress("abc");
        nodeDetail.setScaleIoData2SvmIpAddress("abc");
//        nodeDetail.setScaleIoData2KernelIpAddress("abc");

//        nodeDetail.setScaleIoSvmManagementIpAddress("abc");
//        nodeDetail.setScaleIoSvmManagementGatewayAddress("abc");
//        nodeDetail.setScaleIoSvmManagementSubnetMask("abc");
//        nodeDetail.setHostname("abc");
        nodeDetail.setClusterName("abc");
        nodeDetail.setProtectionDomainId("abc");
        nodeDetail.setProtectionDomainName("pd-abc");
    }

    @Ignore @Test
    public void testNodeDetailsNull() throws Exception
    {
        try {
            nodeDetail = null;
            when(delegateExecution.getVariable(DelegateConstants.NODE_DETAIL)).thenReturn(nodeDetail);
            verifyNodeDetail.delegateExecute(delegateExecution);
        } catch (BpmnError error) {
            assertTrue(error.getErrorCode().equals(DelegateConstants.VERIFY_NODE_DETAIL_FAILED));
            assertTrue(error.getMessage().contains("Node details were not found!  Please add Node details and try again."));
        }
    }

    @Ignore @Test
    public void testNodeDetailsMissing() throws Exception
    {
        try {
            nodeDetail.setClusterName("");
            when(delegateExecution.getVariable(DelegateConstants.NODE_DETAIL)).thenReturn(nodeDetail);
            verifyNodeDetail.delegateExecute(delegateExecution);
        } catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.VERIFY_NODE_DETAIL_FAILED));
            assertTrue(error.getMessage().contains("Node details are incomplete!  Please update Node details with the following information and try again."));
        }
    }

    @Ignore @Test
    public void testVerifiedSuccess() throws Exception
    {
        when(delegateExecution.getVariable(DelegateConstants.NODE_DETAIL)).thenReturn(nodeDetail);
        final VerifyNodeDetail c = spy(new VerifyNodeDetail());
        c.delegateExecute(delegateExecution);
        verify(c).updateDelegateStatus("Verification of Details on Node abc was successful.");
    }
}

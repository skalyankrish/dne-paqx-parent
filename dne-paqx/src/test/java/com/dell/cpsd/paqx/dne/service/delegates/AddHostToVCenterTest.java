
/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.delegates.model.ESXiCredentialDetails;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.*;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class AddHostToVCenterTest {

    private AddHostToVCenter addHostToVCenter;
    private NodeService nodeService;
    private DataServiceRepository repository;
    private DelegateExecution delegateExecution;
    private NodeDetail nodeDetail;
    private ComponentEndpointIds componentEndpointIds;
    private ESXiCredentialDetails esxiCredentialDetails;

    @Before
    public void setUp() throws Exception
    {
        nodeService = mock(NodeService.class);
        repository = mock(DataServiceRepository.class);
        addHostToVCenter = new AddHostToVCenter(nodeService, repository);
        delegateExecution = mock(DelegateExecution.class);
        nodeDetail = new NodeDetail();
        nodeDetail.setServiceTag("abc");
        nodeDetail.setvMotionManagementIpAddress("abc");
        nodeDetail.setvMotionManagementSubnetMask("abc");
        nodeDetail.setScaleIoData1SvmIpAddress("abc");
        nodeDetail.setScaleIoData2SvmIpAddress("abc");
        componentEndpointIds = new ComponentEndpointIds("abc","abc","abc", "abc");
        esxiCredentialDetails = new ESXiCredentialDetails();
        esxiCredentialDetails.setComponentUuid("abc");
        esxiCredentialDetails.setCredentialUuid("abc");
        esxiCredentialDetails.setEndpointUuid("abc");
    }

    @Ignore @Test
    public void testAddHostFailed() throws Exception
    {
        try {
            when(delegateExecution.getVariable(VCENTER_CLUSTER_NAME)).thenReturn("abc");
            when(delegateExecution.getVariable(HOSTNAME)).thenReturn("abc");
            when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
            when(delegateExecution.getVariable(ESXI_CREDENTIAL_DETAILS)).thenReturn(esxiCredentialDetails);
            when(repository.getClusterId("abc")).thenReturn("abc");
            when(repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER")).thenReturn(componentEndpointIds);
            when(nodeService.requestAddHostToVCenter(any())).thenReturn(false);

            addHostToVCenter.delegateExecute(delegateExecution);
        } catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.ADD_HOST_TO_CLUSTER_FAILED));
            assertTrue(error.getMessage().equalsIgnoreCase("Add Host to VCenter on Node abc failed!"));
        }
    }

    @Ignore @Test
    public void testAddHostSuccess() throws Exception
    {
        when(delegateExecution.getVariable(VCENTER_CLUSTER_NAME)).thenReturn("abc");
        when(delegateExecution.getVariable(HOSTNAME)).thenReturn("abc");
        when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
        when(delegateExecution.getVariable(ESXI_CREDENTIAL_DETAILS)).thenReturn(esxiCredentialDetails);
        when(repository.getClusterId("abc")).thenReturn("abc");
        when(repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER")).thenReturn(componentEndpointIds);
        when(nodeService.requestAddHostToVCenter(any())).thenReturn(true);
        final AddHostToVCenter c = spy(new AddHostToVCenter(nodeService, repository));
        c.delegateExecute(delegateExecution);
        verify(c).updateDelegateStatus("Add Host to VCenter on Node abc was successful.");
    }
}

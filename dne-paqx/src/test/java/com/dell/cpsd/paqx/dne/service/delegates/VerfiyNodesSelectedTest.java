/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.domain.node.DiscoveredNodeInfo;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOData;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOSDC;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOSDS;
import com.dell.cpsd.paqx.dne.domain.vcenter.Host;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class VerfiyNodesSelectedTest
{
    private VerifyNodesSelected   verifyNodesSelected;
    private NodeService           nodeService;
    private DataServiceRepository repository;
    private DelegateExecution     delegateExecution;
    private NodeDetail            nodeDetail;
    private DiscoveredNodeInfo    discoveredNodeInfo;
    private Host                  vCenterHost;
    private ScaleIOData           scaleIOData;
    private ScaleIOSDC            scaleIOSDC;
    private ScaleIOSDS            scaleIOSDS;
    private List<NodeDetail>         nodeDetails     = new ArrayList<>();
    private List<DiscoveredNodeInfo> discoveredNodes = new ArrayList<>();
    private List<Host>               vCenterHosts    = new ArrayList<>();
    private List<ScaleIOSDC>         scaleIOSDCs     = new ArrayList<>();
    private List<ScaleIOSDS>         scaleIOSDSs     = new ArrayList<>();

    @Before
    public void setUp() throws Exception
    {
        nodeService = mock(NodeService.class);
        repository = mock(DataServiceRepository.class);
        delegateExecution = mock(DelegateExecution.class);
        scaleIOData = mock(ScaleIOData.class);

        verifyNodesSelected = new VerifyNodesSelected(nodeService, repository);

        nodeDetail = new NodeDetail();
        nodeDetail.setServiceTag("abc");
        nodeDetail.setEsxiManagementHostname("abc");
        nodeDetail.setEsxiManagementIpAddress("abc");
        nodeDetail.setvMotionManagementIpAddress("abc");
        nodeDetail.setvMotionManagementSubnetMask("abc");
        nodeDetail.setScaleIoData1SvmIpAddress("abc");
        nodeDetail.setScaleIoData2SvmIpAddress("abc");
        nodeDetails.add(nodeDetail);

        discoveredNodeInfo = new DiscoveredNodeInfo("abc", "abc", "abc", "abc", "abc", "abc");
        discoveredNodes.add(discoveredNodeInfo);

        vCenterHost = new Host();
        vCenterHost.setName("cba");
        vCenterHosts.add(vCenterHost);

        scaleIOSDC = new ScaleIOSDC();
        scaleIOSDC.setName("cba");
        scaleIOSDCs.add(scaleIOSDC);

        scaleIOSDS = new ScaleIOSDS();
        scaleIOSDS.setName("cba");
        scaleIOSDSs.add(scaleIOSDS);
    }

    @Test
    public void testVerifyNodesFailed() throws Exception
    {
        try
        {
            nodeDetails.clear();
            when(delegateExecution.getVariable(DelegateConstants.NODE_DETAILS)).thenReturn(nodeDetails);
            when(nodeService.listDiscoveredNodeInfo()).thenReturn(discoveredNodes);

            verifyNodesSelected.delegateExecute(delegateExecution);

            fail("Exception expected but did not occur");
        }
        catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.VERIFY_NODES_SELECTED_FAILED));
            assertTrue(error.getMessage()
                    .contains("The List of Node Detail was not found!  Please add at least one Node Detail and try again"));
        }
    }

    @Test
    public void testException() throws Exception
    {
        try
        {
            when(delegateExecution.getVariable(DelegateConstants.NODE_DETAILS)).thenReturn(nodeDetails);
            when(nodeService.listDiscoveredNodeInfo()).thenReturn(discoveredNodes);
            given(nodeService.listDiscoveredNodeInfo()).willThrow(new NullPointerException());

            verifyNodesSelected.delegateExecute(delegateExecution);

            fail("Exception expected but did not occur");
        }
        catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.VERIFY_NODES_SELECTED_FAILED));
            assertTrue(error.getMessage().contains("An unexpected exception occurred attempting to verify selected Nodes"));
        }
    }

    @Ignore("Need to revisit this test")
    @Test
    public void testNodeDetailsEmpty() throws Exception
    {
        try
        {
            when(delegateExecution.getVariable(DelegateConstants.NODE_DETAILS)).thenReturn(nodeDetails);
            when(nodeService.listDiscoveredNodeInfo()).thenReturn(discoveredNodes);
            nodeDetail.setServiceTag("xyz");

            verifyNodesSelected.delegateExecute(delegateExecution);

            fail("Exception expected but did not occur");
        }
        catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.VERIFY_NODES_SELECTED_FAILED));
            assertTrue(error.getMessage().contains("An unexpected exception occurred attempting to verify selected Nodes"));
        }
    }

    @Test
    public void testNodeAlreadyProvisionedByFindingHostByNameException() throws Exception
    {
        vCenterHost.setName("abc");
        nodeDetail.setEsxiManagementHostname("abc");
        verifyNodeAlreadyProvisioned();
    }

    @Test
    public void testNodeAlreadyProvisionedByFindingHostByIpAddressException() throws Exception
    {
        vCenterHost.setName("1.2.3.4");
        nodeDetail.setEsxiManagementIpAddress("1.2.3.4");
        verifyNodeAlreadyProvisioned();
    }

    @Test
    public void testNodeAlreadyProvisionedByFindingScaleIoSdcByNameException() throws Exception
    {
        scaleIOSDC.setName("abc");
        nodeDetail.setEsxiManagementHostname("abc");
        verifyNodeAlreadyProvisioned();
    }

    @Test
    public void testNodeAlreadyProvisionedByFindingScaleIoSdcByIpAddressException() throws Exception
    {
        scaleIOSDC.setName("1.2.3.4");
        nodeDetail.setEsxiManagementIpAddress("1.2.3.4");
        verifyNodeAlreadyProvisioned();
    }

    @Test
    public void testNodeAlreadyProvisionedByFindingScaleIoSdsByNameException() throws Exception
    {
        scaleIOSDS.setName("abc");
        nodeDetail.setEsxiManagementHostname("abc");
        verifyNodeAlreadyProvisioned();
    }

    @Test
    public void testNodeAlreadyProvisionedByFindingScaleIoSdsByIpAddressException() throws Exception
    {
        scaleIOSDS.setName("1.2.3.4");
        nodeDetail.setEsxiManagementIpAddress("1.2.3.4");
        verifyNodeAlreadyProvisioned();
    }

    @Test
    public void testVerifiedSuccess() throws Exception
    {
        when(delegateExecution.getVariable(DelegateConstants.NODE_DETAILS)).thenReturn(nodeDetails);
        when(nodeService.listDiscoveredNodeInfo()).thenReturn(discoveredNodes);
        when(repository.getVCenterHosts()).thenReturn(vCenterHosts);
        when(repository.getScaleIoData()).thenReturn(scaleIOData);
        when(scaleIOData.getSdcList()).thenReturn(scaleIOSDCs);
        when(scaleIOData.getSdsList()).thenReturn(scaleIOSDSs);
        final VerifyNodesSelected verifyNodesSelectedSpy = spy(new VerifyNodesSelected(nodeService, repository));

        verifyNodesSelectedSpy.delegateExecute(delegateExecution);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(verifyNodesSelectedSpy, times(2)).updateDelegateStatus(captor.capture());
        List<String> updateDelegateStatusParams = captor.getAllValues();
        assertThat(updateDelegateStatusParams, hasSize(2));
        assertTrue(updateDelegateStatusParams.get(0).contains("Attempting to verify selected Nodes are still available"));
        assertTrue(updateDelegateStatusParams.get(1).contains("All selected Nodes are available"));
    }

    private void verifyNodeAlreadyProvisioned() throws Exception
    {
        try
        {
            when(delegateExecution.getVariable(DelegateConstants.NODE_DETAILS)).thenReturn(nodeDetails);
            when(nodeService.listDiscoveredNodeInfo()).thenReturn(discoveredNodes);
            when(repository.getVCenterHosts()).thenReturn(vCenterHosts);
            when(repository.getScaleIoData()).thenReturn(scaleIOData);
            when(scaleIOData.getSdcList()).thenReturn(scaleIOSDCs);
            when(scaleIOData.getSdsList()).thenReturn(scaleIOSDSs);

            verifyNodesSelected.delegateExecute(delegateExecution);

            fail("Exception expected but did not occur");
        }
        catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.VERIFY_NODES_SELECTED_FAILED));
            assertTrue(error.getMessage().contains("An unexpected exception occurred attempting to verify selected Nodes"));
        }
    }
}

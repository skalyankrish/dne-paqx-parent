/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.domain.vcenter.Host;
import com.dell.cpsd.paqx.dne.domain.vcenter.HostDnsConfig;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.HOSTNAME;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DeployScaleIOVmTest
{
    private DeployScaleIOVm       deployScaleIOVm;
    private NodeService           nodeService;
    private DelegateExecution     delegateExecution;
    private DataServiceRepository repository;
    private ComponentEndpointIds  componentEndpointIds;
    private NodeDetail            nodeDetail;
    private Host                  host;

    @Before
    public void setUp() throws Exception
    {
        nodeService = mock(NodeService.class);
        repository = mock(DataServiceRepository.class);
        deployScaleIOVm = new DeployScaleIOVm(nodeService, repository);
        delegateExecution = mock(DelegateExecution.class);
        componentEndpointIds = new ComponentEndpointIds("abc", "abc", "abc", "abc");
        nodeDetail = new NodeDetail();
        nodeDetail.setServiceTag("abc");
        nodeDetail.setEsxiManagementIpAddress("abc");
        nodeDetail.setScaleIoSvmManagementIpAddress("ip1");
        nodeDetail.setClusterName("cluster");
        nodeDetail.setEsxiManagementGatewayIpAddress("ip2");
        nodeDetail.setScaleIoSvmManagementGatewayAddress("ip3");
        nodeDetail.setScaleIoSvmManagementSubnetMask("ip4");
        nodeDetail.setScaleIoData1SvmIpAddress("ip5");
        nodeDetail.setScaleIoData1SvmSubnetMask("ip6");
        nodeDetail.setScaleIoData2SvmIpAddress("ip7");
        nodeDetail.setProtectionDomainName("pd-abc");
        nodeDetail.setScaleIoData1EsxSubnetMask("ip8");
        host = new Host();
        HostDnsConfig hostDnsConfig = new HostDnsConfig();
        List<String> ips = new ArrayList<>();
        ips.add("ip10");
        hostDnsConfig.setDnsConfigIPs(ips);
        host.setHostDnsConfig(hostDnsConfig);
    }

    @Test
    public void testExceptionThrown1() throws Exception
    {
        try
        {
            when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
            when(delegateExecution.getVariable(HOSTNAME)).thenReturn("hostname");
            when(repository.getDomainName()).thenReturn("domain");
            when(repository.getExistingVCenterHost()).thenReturn(host);
            when(repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER")).thenReturn(componentEndpointIds);
            given(nodeService.requestDeployScaleIoVm(any())).willThrow(new NullPointerException());
            deployScaleIOVm.delegateExecute(delegateExecution);
        }
        catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.DEPLOY_SCALEIO_VM_FAILED));
            assertTrue(error.getMessage().contains("on Node " + nodeDetail.getServiceTag() + " failed"));
        }
    }

    @Test
    public void testExecutionFailed()
    {
        try
        {
            when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
            when(delegateExecution.getVariable(HOSTNAME)).thenReturn("hostname");
            when(repository.getDomainName()).thenReturn("domain");
            when(repository.getExistingVCenterHost()).thenReturn(host);
            when(repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER")).thenReturn(componentEndpointIds);
            when(nodeService.requestDeployScaleIoVm(any())).thenReturn(false);
            deployScaleIOVm.delegateExecute(delegateExecution);
        }
        catch (BpmnError error)
        {
            assertTrue(error.getErrorCode().equals(DelegateConstants.DEPLOY_SCALEIO_VM_FAILED));
            assertTrue(error.getMessage().contains("request deploy ScaleIO VM failed"));
        }
    }

    @Test
    public void testSuccess()
    {
        when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
        when(delegateExecution.getVariable(HOSTNAME)).thenReturn("hostname");
        when(repository.getDomainName()).thenReturn("domain");
        when(repository.getExistingVCenterHost()).thenReturn(host);
        when(repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER")).thenReturn(componentEndpointIds);
        when(nodeService.requestDeployScaleIoVm(any())).thenReturn(true);
        final DeployScaleIOVm deployScaleIOVmSpy = spy(deployScaleIOVm);
        deployScaleIOVmSpy.delegateExecute(delegateExecution);
        verify(deployScaleIOVmSpy).updateDelegateStatus("Deploy ScaleIo Vm on Node abc was successful.");
    }
}

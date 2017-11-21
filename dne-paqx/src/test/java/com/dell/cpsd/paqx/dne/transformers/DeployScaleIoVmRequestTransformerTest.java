/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 **/

package com.dell.cpsd.paqx.dne.transformers;

import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.virtualization.capabilities.api.AutoStartDefaults;
import com.dell.cpsd.virtualization.capabilities.api.AutoStartPowerInfo;
import com.dell.cpsd.virtualization.capabilities.api.DeployVMFromTemplateRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.NicSetting;
import com.dell.cpsd.virtualization.capabilities.api.VirtualMachineCloneSpec;
import com.dell.cpsd.virtualization.capabilities.api.VirtualMachineConfigSpec;
import com.dell.cpsd.virtualization.capabilities.api.VmAutoStartConfig;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.HOSTNAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Unit tests for deploy ScaleIO VM Request Transformer
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class DeployScaleIoVmRequestTransformerTest
{
    @Mock
    private ComponentIdsTransformer componentIdsTransformer;

    @Mock
    private DelegateExecution delegateExecution;

    @Mock
    private ComponentEndpointIds componentEndpointIds;

    @Mock
    private DataServiceRepository repository;

    @Mock
    private NodeDetail nodeDetail;

    private DeployScaleIoVmRequestTransformer deployScaleIoVmRequestTransformer;

    private static final String VCENTER_CUSTOMER_TYPE        = "VCENTER-CUSTOMER";
    private final        String hostname                     = "hostname";
    private final        String clusterName                  = "cluster-name";
    private final        String scaleIoVmManagementIpAddress = "1.1.1.1";
    private final        String datacenterName               = "datacenter-name";
    private final        String domainName                   = "domain-name";

    @Before
    public void setup() throws Exception
    {
        deployScaleIoVmRequestTransformer = new DeployScaleIoVmRequestTransformer(repository, componentIdsTransformer);
    }

    @Test(expected = IllegalStateException.class)
    public void testDatacenterNameNotFoundThrowsIllegalStateException() throws Exception
    {
        when(delegateExecution.getVariable(HOSTNAME)).thenReturn(hostname);
        when(componentIdsTransformer.getVCenterComponentEndpointIdsByEndpointType(VCENTER_CUSTOMER_TYPE)).thenReturn(componentEndpointIds);
        when(delegateExecution.getVariable(DelegateConstants.NODE_DETAIL)).thenReturn(nodeDetail);
        when(nodeDetail.getClusterName()).thenReturn(clusterName);
        when(nodeDetail.getScaleIoSvmManagementIpAddress()).thenReturn(scaleIoVmManagementIpAddress);

        when(repository.getDataCenterName(clusterName)).thenReturn(null);

        deployScaleIoVmRequestTransformer.buildDeployVmRequest(delegateExecution);
    }

    @Test(expected = IllegalStateException.class)
    public void testDomainNameNotFoundThrowsIllegalStateException() throws Exception
    {
        when(delegateExecution.getVariable(HOSTNAME)).thenReturn(hostname);
        when(componentIdsTransformer.getVCenterComponentEndpointIdsByEndpointType(VCENTER_CUSTOMER_TYPE)).thenReturn(componentEndpointIds);
        when(delegateExecution.getVariable(DelegateConstants.NODE_DETAIL)).thenReturn(nodeDetail);
        when(nodeDetail.getClusterName()).thenReturn(clusterName);
        when(nodeDetail.getScaleIoSvmManagementIpAddress()).thenReturn(scaleIoVmManagementIpAddress);

        when(repository.getDataCenterName(clusterName)).thenReturn(datacenterName);
        when(repository.getDomainName()).thenReturn(null);

        deployScaleIoVmRequestTransformer.buildDeployVmRequest(delegateExecution);
    }

    @Test
    public void testBuildDeployVmRequestMessageIsValid() throws Exception
    {
        when(delegateExecution.getVariable(HOSTNAME)).thenReturn(hostname);
        when(componentIdsTransformer.getVCenterComponentEndpointIdsByEndpointType(VCENTER_CUSTOMER_TYPE)).thenReturn(componentEndpointIds);
        when(delegateExecution.getVariable(DelegateConstants.NODE_DETAIL)).thenReturn(nodeDetail);
        when(nodeDetail.getClusterName()).thenReturn(clusterName);
        when(nodeDetail.getScaleIoSvmManagementIpAddress()).thenReturn(scaleIoVmManagementIpAddress);

        when(repository.getDataCenterName(clusterName)).thenReturn(datacenterName);
        when(repository.getDomainName()).thenReturn(domainName);
        when(repository.getDnsServers()).thenReturn(new ArrayList<>());

        final DeployVMFromTemplateRequestMessage requestMessage = deployScaleIoVmRequestTransformer.buildDeployVmRequest(delegateExecution);
        assertNotNull(requestMessage);

        final VirtualMachineCloneSpec virtualMachineCloneSpec = requestMessage.getVirtualMachineCloneSpec();
        assertNotNull(virtualMachineCloneSpec);

        final VmAutoStartConfig vmAutoStartConfig = requestMessage.getVmAutoStartConfig();
        assertNotNull(vmAutoStartConfig);

        assertNotNull(requestMessage.getComponentEndpointIds());
        assertNotNull(requestMessage.getCredentials());
        assertTrue(requestMessage.getConfigureAutoStart());
        assertEquals(datacenterName, requestMessage.getDatacenterName());
        assertEquals(hostname, requestMessage.getHostName());
        assertEquals("ScaleIO-" + scaleIoVmManagementIpAddress, requestMessage.getNewVMName());
        assertEquals("EMC ScaleIO SVM Template.*", requestMessage.getTemplateName());

        final AutoStartDefaults autoStartDefaults = vmAutoStartConfig.getAutoStartDefaults();
        assertNotNull(autoStartDefaults);

        assertEquals(Integer.valueOf(120), autoStartDefaults.getStartDelay());
        assertEquals(Integer.valueOf(120), autoStartDefaults.getStopDelay());
        assertTrue(autoStartDefaults.getEnabled());
        assertEquals(AutoStartDefaults.StopAction.POWER_OFF, autoStartDefaults.getStopAction());

        final AutoStartPowerInfo autoStartPowerInfo = vmAutoStartConfig.getAutoStartPowerInfo();
        assertNotNull(autoStartPowerInfo);

        assertEquals(AutoStartPowerInfo.StartAction.POWER_ON, autoStartPowerInfo.getStartAction());
        assertEquals(AutoStartPowerInfo.StopAction.SYSTEM_DEFAULT, autoStartPowerInfo.getStopAction());
        assertEquals(Integer.valueOf(20), autoStartPowerInfo.getStartDelay());
        assertEquals(Integer.valueOf(-1), autoStartPowerInfo.getStopDelay());
        assertEquals(Integer.valueOf(1), autoStartPowerInfo.getStartOrder());
        assertEquals(AutoStartPowerInfo.WaitForHeartBeat.SYSTEM_DEFAULT, autoStartPowerInfo.getWaitForHeartBeat());

        final VirtualMachineConfigSpec virtualMachineConfigSpec = virtualMachineCloneSpec.getVirtualMachineConfigSpec();
        assertNotNull(virtualMachineConfigSpec);

        assertFalse(virtualMachineCloneSpec.getPoweredOn());
        assertFalse(virtualMachineCloneSpec.getTemplate());
        assertEquals(domainName, virtualMachineCloneSpec.getDomain());

        assertEquals("ScaleIO-1-1-1-1", virtualMachineConfigSpec.getHostName());
        assertEquals(Integer.valueOf(8192), virtualMachineConfigSpec.getMemoryMB());
        assertEquals(Integer.valueOf(8), virtualMachineConfigSpec.getNumCPUs());
        assertTrue(virtualMachineConfigSpec.getDnsServerList().size() == 0);

        final List<NicSetting> nicSettings = virtualMachineConfigSpec.getNicSettings();

        assertNotNull(nicSettings);
        assertEquals(3, nicSettings.size());
    }
}

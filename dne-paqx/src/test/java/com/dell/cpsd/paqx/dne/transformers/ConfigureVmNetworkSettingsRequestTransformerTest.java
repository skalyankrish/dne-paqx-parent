/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 **/

package com.dell.cpsd.paqx.dne.transformers;

import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.virtualization.capabilities.api.ConfigureVmNetworkSettingsRequestMessage;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.HOSTNAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * Unit tests for Configure VM Network Settings Request Transformer
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class ConfigureVmNetworkSettingsRequestTransformerTest
{
    @Mock
    private ComponentIdsTransformer componentIdsTransformer;

    @Mock
    private DelegateExecution delegateExecution;

    @Mock
    private ComponentEndpointIds componentEndpointIds;

    @Mock
    private DataServiceRepository repository;

    private ConfigureVmNetworkSettingsRequestTransformer configureVmNetworkSettingsRequestTransformer;

    private static final String              VCENTER_CUSTOMER_TYPE = "VCENTER-CUSTOMER";
    private final        String              hostname              = "hostname";
    private final        Map<String, String> dvSwitchNamesMap      = new HashMap<>();
    private final        Map<String, String> networkNamesMap       = new HashMap<>();
    private final        String              virtualMachineName    = "vm-name";
    private final        String              endpointUrl           = "https://fake";

    @Before
    public void setup() throws Exception
    {
        configureVmNetworkSettingsRequestTransformer = new ConfigureVmNetworkSettingsRequestTransformer(repository,
                componentIdsTransformer);
    }

    @Test(expected = IllegalStateException.class)
    public void dvSwitchNamesNullThrowsIllegalStateException() throws Exception
    {
        when(delegateExecution.getVariable(HOSTNAME)).thenReturn(hostname);
        when(componentIdsTransformer.getVCenterComponentEndpointIdsByEndpointType(VCENTER_CUSTOMER_TYPE)).thenReturn(componentEndpointIds);
        when(delegateExecution.getVariable(DelegateConstants.VIRTUAL_MACHINE_NAME)).thenReturn(virtualMachineName);

        when(repository.getDvSwitchNames()).thenReturn(null);

        configureVmNetworkSettingsRequestTransformer.buildConfigureVmNetworkSettingsRequest(delegateExecution);
    }

    @Test(expected = IllegalStateException.class)
    public void networkNamesNullThrowsIllegalStateException() throws Exception
    {
        when(delegateExecution.getVariable(HOSTNAME)).thenReturn(hostname);
        when(componentIdsTransformer.getVCenterComponentEndpointIdsByEndpointType(VCENTER_CUSTOMER_TYPE)).thenReturn(componentEndpointIds);
        when(delegateExecution.getVariable(DelegateConstants.VIRTUAL_MACHINE_NAME)).thenReturn(virtualMachineName);

        dvSwitchNamesMap.put("test", "test");
        when(repository.getDvSwitchNames()).thenReturn(dvSwitchNamesMap);
        when(repository.getScaleIoNetworkNames(dvSwitchNamesMap)).thenReturn(null);

        configureVmNetworkSettingsRequestTransformer.buildConfigureVmNetworkSettingsRequest(delegateExecution);
    }

    @Test
    public void testBuildRequestMessageIsValid() throws Exception
    {
        when(delegateExecution.getVariable(HOSTNAME)).thenReturn(hostname);
        when(componentIdsTransformer.getVCenterComponentEndpointIdsByEndpointType(VCENTER_CUSTOMER_TYPE)).thenReturn(componentEndpointIds);
        when(delegateExecution.getVariable(DelegateConstants.VIRTUAL_MACHINE_NAME)).thenReturn(virtualMachineName);
        when(componentEndpointIds.getEndpointUrl()).thenReturn(endpointUrl);

        dvSwitchNamesMap.put("test", "test");
        when(repository.getDvSwitchNames()).thenReturn(dvSwitchNamesMap);
        networkNamesMap.put("test", "test");
        when(repository.getScaleIoNetworkNames(dvSwitchNamesMap)).thenReturn(networkNamesMap);

        final ConfigureVmNetworkSettingsRequestMessage requestMessage = configureVmNetworkSettingsRequestTransformer
                .buildConfigureVmNetworkSettingsRequest(delegateExecution);

        assertNotNull(requestMessage);

        assertNotNull(requestMessage.getComponentEndpointIds());
        assertEquals(hostname, requestMessage.getHostname());
        assertEquals(virtualMachineName, requestMessage.getVmName());
        assertEquals(endpointUrl, requestMessage.getEndpointUrl());
        assertNotNull(requestMessage.getNetworkSettingsMap());
    }
}

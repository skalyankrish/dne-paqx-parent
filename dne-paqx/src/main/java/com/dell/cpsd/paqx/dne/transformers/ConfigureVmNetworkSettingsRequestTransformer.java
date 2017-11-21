/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 **/

package com.dell.cpsd.paqx.dne.transformers;

import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.virtualization.capabilities.api.ConfigureVmNetworkSettingsRequestMessage;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.HOSTNAME;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.VIRTUAL_MACHINE_NAME;

/**
 * Configure VM Network settings request transformer that builds the
 * {@link com.dell.cpsd.virtualization.capabilities.api.ConfigureVmNetworkSettingsRequestMessage}
 * request message.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@Component
public class ConfigureVmNetworkSettingsRequestTransformer
{
    private static final String VCENTER_CUSTOMER_TYPE = "VCENTER-CUSTOMER";

    private final DataServiceRepository   repository;
    private final ComponentIdsTransformer componentIdsTransformer;

    public ConfigureVmNetworkSettingsRequestTransformer(final DataServiceRepository repository,
            final ComponentIdsTransformer componentIdsTransformer)
    {
        this.repository = repository;
        this.componentIdsTransformer = componentIdsTransformer;
    }

    public ConfigureVmNetworkSettingsRequestMessage buildConfigureVmNetworkSettingsRequest(final DelegateExecution delegateExecution)
    {
        final ComponentEndpointIds componentEndpointIds = componentIdsTransformer
                .getVCenterComponentEndpointIdsByEndpointType(VCENTER_CUSTOMER_TYPE);
        final String hostname = (String) delegateExecution.getVariable(HOSTNAME);
        final String virtualMachineName = (String) delegateExecution.getVariable(VIRTUAL_MACHINE_NAME);
        final Map<String, String> dvSwitchNames = getDvSwitchNamesMap();
        final Map<String, String> scaleIoNetworkNamesMap = getNetworkNamesMap(dvSwitchNames);

        return getConfigureVmNetworkSettingsRequestMessage(componentEndpointIds, hostname, virtualMachineName, scaleIoNetworkNamesMap);
    }

    private ConfigureVmNetworkSettingsRequestMessage getConfigureVmNetworkSettingsRequestMessage(
            final ComponentEndpointIds componentEndpointIds, final String hostname, final String virtualMachineName,
            final Map<String, String> scaleIoNetworkNamesMap)
    {
        final ConfigureVmNetworkSettingsRequestMessage requestMessage = new ConfigureVmNetworkSettingsRequestMessage();
        requestMessage.setHostname(hostname);
        requestMessage.setVmName(virtualMachineName);
        requestMessage.setNetworkSettingsMap(scaleIoNetworkNamesMap);
        requestMessage.setEndpointUrl(componentEndpointIds.getEndpointUrl());
        requestMessage.setComponentEndpointIds(
                new com.dell.cpsd.virtualization.capabilities.api.ComponentEndpointIds(componentEndpointIds.getComponentUuid(),
                        componentEndpointIds.getEndpointUuid(), componentEndpointIds.getCredentialUuid()));
        return requestMessage;
    }

    private Map<String, String> getNetworkNamesMap(final Map<String, String> dvSwitchNames)
    {
        final Map<String, String> scaleIoNetworkNamesMap = repository.getScaleIoNetworkNames(dvSwitchNames);

        if (scaleIoNetworkNamesMap == null)
        {
            throw new IllegalStateException("ScaleIO DVSwitch-Network Names Map is null");
        }
        return scaleIoNetworkNamesMap;
    }

    private Map<String, String> getDvSwitchNamesMap()
    {
        final Map<String, String> dvSwitchNames = repository.getDvSwitchNames();

        if (dvSwitchNames == null)
        {
            throw new IllegalStateException("DVSwitch Names Map is null");
        }
        return dvSwitchNames;
    }
}

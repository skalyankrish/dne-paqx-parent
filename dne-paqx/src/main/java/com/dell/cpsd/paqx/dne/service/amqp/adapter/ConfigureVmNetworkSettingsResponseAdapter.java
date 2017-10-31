/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.service.common.client.callback.IServiceCallback;
import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackRegistry;
import com.dell.cpsd.virtualization.capabilities.api.ConfigureVmNetworkSettingsResponseMessage;

/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public class ConfigureVmNetworkSettingsResponseAdapter implements
        ServiceCallbackAdapter<ConfigureVmNetworkSettingsResponseMessage, ServiceResponse<ConfigureVmNetworkSettingsResponseMessage>>
{
    private final ServiceCallbackRegistry serviceCallbackRegistry;

    public ConfigureVmNetworkSettingsResponseAdapter(ServiceCallbackRegistry serviceCallbackRegistry)
    {
        this.serviceCallbackRegistry = serviceCallbackRegistry;
    }

    @Override
    public ServiceResponse<ConfigureVmNetworkSettingsResponseMessage> transform(ConfigureVmNetworkSettingsResponseMessage responseMessage)
    {
        return new ServiceResponse<>(responseMessage.getMessageProperties().getCorrelationId(), responseMessage, null);
    }

    @Override
    public void consume(IServiceCallback callback, ServiceResponse<ConfigureVmNetworkSettingsResponseMessage> responseMessage)
    {
        callback.handleServiceResponse(responseMessage);
    }

    @Override
    public IServiceCallback take(ConfigureVmNetworkSettingsResponseMessage responseMessage)
    {
        return serviceCallbackRegistry.removeServiceCallback(responseMessage.getMessageProperties().getCorrelationId());
    }

    @Override
    public Class<ConfigureVmNetworkSettingsResponseMessage> getSourceClass()
    {
        return ConfigureVmNetworkSettingsResponseMessage.class;
    }
}

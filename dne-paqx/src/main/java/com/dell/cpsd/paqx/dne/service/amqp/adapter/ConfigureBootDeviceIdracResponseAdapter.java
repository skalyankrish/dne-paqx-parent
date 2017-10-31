/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.ConfigureBootDeviceIdracResponseMessage;
import com.dell.cpsd.service.common.client.callback.IServiceCallback;
import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackRegistry;

/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public class ConfigureBootDeviceIdracResponseAdapter
        implements ServiceCallbackAdapter<ConfigureBootDeviceIdracResponseMessage, ServiceResponse<ConfigureBootDeviceIdracResponseMessage>>
{
    private final ServiceCallbackRegistry serviceCallbackRegistry;

    public ConfigureBootDeviceIdracResponseAdapter(ServiceCallbackRegistry serviceCallbackRegistry)
    {
        this.serviceCallbackRegistry = serviceCallbackRegistry;
    }

    @Override
    public ServiceResponse<ConfigureBootDeviceIdracResponseMessage> transform(
            ConfigureBootDeviceIdracResponseMessage configureBootDeviceIdracResponseMessage)
    {
        return new ServiceResponse<>(configureBootDeviceIdracResponseMessage.getMessageProperties().getCorrelationId(),
                configureBootDeviceIdracResponseMessage, null);
    }

    @Override
    public void consume(IServiceCallback callback,
            ServiceResponse<ConfigureBootDeviceIdracResponseMessage> configureBootDeviceIdracResponseMessage)
    {
        callback.handleServiceResponse(configureBootDeviceIdracResponseMessage);
    }

    @Override
    public IServiceCallback take(ConfigureBootDeviceIdracResponseMessage configureBootDeviceIdracResponseMessage)
    {
        return serviceCallbackRegistry
                .removeServiceCallback(configureBootDeviceIdracResponseMessage.getMessageProperties().getCorrelationId());
    }

    @Override
    public Class<ConfigureBootDeviceIdracResponseMessage> getSourceClass()
    {
        return ConfigureBootDeviceIdracResponseMessage.class;
    }
}

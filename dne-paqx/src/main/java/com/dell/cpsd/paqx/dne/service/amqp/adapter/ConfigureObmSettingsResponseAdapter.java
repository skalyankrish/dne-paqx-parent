/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.SetObmSettingsResponseMessage;
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
public class ConfigureObmSettingsResponseAdapter
        implements ServiceCallbackAdapter<SetObmSettingsResponseMessage, ServiceResponse<SetObmSettingsResponseMessage>>
{
    private final ServiceCallbackRegistry serviceCallbackRegistry;

    public ConfigureObmSettingsResponseAdapter(ServiceCallbackRegistry serviceCallbackRegistry)
    {
        this.serviceCallbackRegistry = serviceCallbackRegistry;
    }

    @Override
    public ServiceResponse<SetObmSettingsResponseMessage> transform(SetObmSettingsResponseMessage setObmSettingsResponseMessage)
    {
        return new ServiceResponse<>(setObmSettingsResponseMessage.getMessageProperties().getCorrelationId(), setObmSettingsResponseMessage,
                null);
    }

    @Override
    public void consume(IServiceCallback callback, ServiceResponse<SetObmSettingsResponseMessage> configureObmSettingsResponseMessage)
    {
        callback.handleServiceResponse(configureObmSettingsResponseMessage);
    }

    @Override
    public IServiceCallback take(SetObmSettingsResponseMessage configureObmSettingsResponseMessage)
    {
        return serviceCallbackRegistry.removeServiceCallback(configureObmSettingsResponseMessage.getMessageProperties().getCorrelationId());
    }

    @Override
    public Class<SetObmSettingsResponseMessage> getSourceClass()
    {
        return SetObmSettingsResponseMessage.class;
    }
}

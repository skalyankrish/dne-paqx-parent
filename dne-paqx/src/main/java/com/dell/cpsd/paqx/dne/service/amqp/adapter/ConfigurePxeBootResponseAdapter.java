/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.ConfigurePxeBootResponseMessage;
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
public class ConfigurePxeBootResponseAdapter
        implements ServiceCallbackAdapter<ConfigurePxeBootResponseMessage, ServiceResponse<ConfigurePxeBootResponseMessage>>
{
    private final ServiceCallbackRegistry serviceCallbackRegistry;

    public ConfigurePxeBootResponseAdapter(ServiceCallbackRegistry serviceCallbackRegistry)
    {
        this.serviceCallbackRegistry = serviceCallbackRegistry;
    }

    @Override
    public ServiceResponse<ConfigurePxeBootResponseMessage> transform(ConfigurePxeBootResponseMessage configurePxeBootResponseMessage)
    {
        return new ServiceResponse<>(configurePxeBootResponseMessage.getMessageProperties().getCorrelationId(),
                configurePxeBootResponseMessage, null);
    }

    @Override
    public void consume(IServiceCallback callback, ServiceResponse<ConfigurePxeBootResponseMessage> configurePxeBootResponseMessage)
    {
        callback.handleServiceResponse(configurePxeBootResponseMessage);
    }

    @Override
    public IServiceCallback take(ConfigurePxeBootResponseMessage configurePxeBootResponseMessage)
    {
        return serviceCallbackRegistry.removeServiceCallback(configurePxeBootResponseMessage.getMessageProperties().getCorrelationId());
    }

    @Override
    public Class<ConfigurePxeBootResponseMessage> getSourceClass()
    {
        return ConfigurePxeBootResponseMessage.class;
    }
}

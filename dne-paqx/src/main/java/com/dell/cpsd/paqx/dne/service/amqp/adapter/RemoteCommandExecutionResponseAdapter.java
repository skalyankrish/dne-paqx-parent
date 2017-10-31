/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.service.common.client.callback.IServiceCallback;
import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackRegistry;
import com.dell.cpsd.virtualization.capabilities.api.RemoteCommandExecutionResponseMessage;

/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public class RemoteCommandExecutionResponseAdapter
        implements ServiceCallbackAdapter<RemoteCommandExecutionResponseMessage, ServiceResponse<RemoteCommandExecutionResponseMessage>>
{
    private final ServiceCallbackRegistry serviceCallbackRegistry;

    public RemoteCommandExecutionResponseAdapter(ServiceCallbackRegistry serviceCallbackRegistry)
    {
        this.serviceCallbackRegistry = serviceCallbackRegistry;
    }

    @Override
    public ServiceResponse<RemoteCommandExecutionResponseMessage> transform(RemoteCommandExecutionResponseMessage responseMessage)
    {
        return new ServiceResponse<>(responseMessage.getMessageProperties().getCorrelationId(), responseMessage, null);
    }

    @Override
    public void consume(IServiceCallback callback, ServiceResponse<RemoteCommandExecutionResponseMessage> responseMessage)
    {
        callback.handleServiceResponse(responseMessage);
    }

    @Override
    public IServiceCallback take(RemoteCommandExecutionResponseMessage responseMessage)
    {
        return serviceCallbackRegistry.removeServiceCallback(responseMessage.getMessageProperties().getCorrelationId());
    }

    @Override
    public Class<RemoteCommandExecutionResponseMessage> getSourceClass()
    {
        return RemoteCommandExecutionResponseMessage.class;
    }
}

/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.service.common.client.callback.IServiceCallback;
import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackRegistry;
import com.dell.cpsd.storage.capabilities.api.SioSdcUpdatePerformanceProfileResponseMessage;

/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public class SioSdcUpdatePerformanceProfileResponseAdapter implements
        ServiceCallbackAdapter<SioSdcUpdatePerformanceProfileResponseMessage, ServiceResponse<SioSdcUpdatePerformanceProfileResponseMessage>>
{
    private final ServiceCallbackRegistry serviceCallbackRegistry;

    public SioSdcUpdatePerformanceProfileResponseAdapter(ServiceCallbackRegistry serviceCallbackRegistry)
    {
        this.serviceCallbackRegistry = serviceCallbackRegistry;
    }

    @Override
    public ServiceResponse<SioSdcUpdatePerformanceProfileResponseMessage> transform(
            SioSdcUpdatePerformanceProfileResponseMessage responseMessage)
    {
        return new ServiceResponse<>(responseMessage.getMessageProperties().getCorrelationId(), responseMessage, null);
    }

    @Override
    public void consume(IServiceCallback callback, ServiceResponse<SioSdcUpdatePerformanceProfileResponseMessage> responseMessage)
    {
        callback.handleServiceResponse(responseMessage);
    }

    @Override
    public IServiceCallback take(SioSdcUpdatePerformanceProfileResponseMessage responseMessage)
    {
        return serviceCallbackRegistry.removeServiceCallback(responseMessage.getMessageProperties().getCorrelationId());
    }

    @Override
    public Class<SioSdcUpdatePerformanceProfileResponseMessage> getSourceClass()
    {
        return SioSdcUpdatePerformanceProfileResponseMessage.class;
    }
}

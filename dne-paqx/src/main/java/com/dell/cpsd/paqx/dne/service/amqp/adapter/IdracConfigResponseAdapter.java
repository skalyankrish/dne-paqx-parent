/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * <p>
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.rackhd.adapter.model.idrac.IdracNetworkSettingsResponseMessage;
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
public class IdracConfigResponseAdapter
        implements ServiceCallbackAdapter<IdracNetworkSettingsResponseMessage, ServiceResponse<IdracNetworkSettingsResponseMessage>>
{
    private final ServiceCallbackRegistry serviceCallbackRegistry;

    public IdracConfigResponseAdapter(ServiceCallbackRegistry serviceCallbackRegistry)
    {
        this.serviceCallbackRegistry = serviceCallbackRegistry;
    }

    @Override
    public ServiceResponse<IdracNetworkSettingsResponseMessage> transform(IdracNetworkSettingsResponseMessage idracConfigResponse)
    {
        return new ServiceResponse<>(idracConfigResponse.getMessageProperties().getCorrelationId(), idracConfigResponse, null);
    }

    @Override
    public void consume(IServiceCallback callback, ServiceResponse<IdracNetworkSettingsResponseMessage> idracConfigResponse)
    {
        callback.handleServiceResponse(idracConfigResponse);
    }

    @Override
    public IServiceCallback take(IdracNetworkSettingsResponseMessage idracConfigResponse)
    {
        return serviceCallbackRegistry.removeServiceCallback(idracConfigResponse.getMessageProperties().getCorrelationId());
    }

    @Override
    public Class<IdracNetworkSettingsResponseMessage> getSourceClass()
    {
        return IdracNetworkSettingsResponseMessage.class;
    }
}

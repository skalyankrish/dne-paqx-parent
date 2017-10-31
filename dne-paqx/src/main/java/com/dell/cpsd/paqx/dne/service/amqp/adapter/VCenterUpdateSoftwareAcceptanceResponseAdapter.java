/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.service.common.client.callback.IServiceCallback;
import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackRegistry;
import com.dell.cpsd.virtualization.capabilities.api.VCenterUpdateSoftwareAcceptanceResponseMessage;

/**
 * Callback adapter class used to process the response message from update software acceptance task.
 * <p>
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public class VCenterUpdateSoftwareAcceptanceResponseAdapter implements
        ServiceCallbackAdapter<VCenterUpdateSoftwareAcceptanceResponseMessage, ServiceResponse<VCenterUpdateSoftwareAcceptanceResponseMessage>>
{
    private final ServiceCallbackRegistry serviceCallbackRegistry;

    public VCenterUpdateSoftwareAcceptanceResponseAdapter(ServiceCallbackRegistry serviceCallbackRegistry)
    {
        this.serviceCallbackRegistry = serviceCallbackRegistry;
    }

    @Override
    public ServiceResponse<VCenterUpdateSoftwareAcceptanceResponseMessage> transform(
            VCenterUpdateSoftwareAcceptanceResponseMessage vCenterUpdateSoftwareAcceptanceResponseMessage)
    {
        return new ServiceResponse<>(vCenterUpdateSoftwareAcceptanceResponseMessage.getMessageProperties().getCorrelationId(),
                vCenterUpdateSoftwareAcceptanceResponseMessage, null);

    }

    @Override
    public void consume(IServiceCallback callback,
            ServiceResponse<VCenterUpdateSoftwareAcceptanceResponseMessage> storageResponseMessageServiceResponse)
    {
        callback.handleServiceResponse(storageResponseMessageServiceResponse);
    }

    @Override
    public IServiceCallback take(VCenterUpdateSoftwareAcceptanceResponseMessage vCenterUpdateSoftwareAcceptanceResponseMessage)
    {
        return serviceCallbackRegistry
                .removeServiceCallback(vCenterUpdateSoftwareAcceptanceResponseMessage.getMessageProperties().getCorrelationId());

    }

    @Override
    public Class<VCenterUpdateSoftwareAcceptanceResponseMessage> getSourceClass()
    {
        return VCenterUpdateSoftwareAcceptanceResponseMessage.class;
    }

}

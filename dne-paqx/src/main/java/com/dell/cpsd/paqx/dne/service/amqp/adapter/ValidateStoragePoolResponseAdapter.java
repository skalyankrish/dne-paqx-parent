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
import com.dell.cpsd.service.engineering.standards.EssValidateStoragePoolResponseMessage;

/**
 * Callback adapter class used to process the response message from find scaleIO task.
 * <p>
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public class ValidateStoragePoolResponseAdapter
        implements ServiceCallbackAdapter<EssValidateStoragePoolResponseMessage, ServiceResponse<EssValidateStoragePoolResponseMessage>>
{
    private final ServiceCallbackRegistry serviceCallbackRegistry;

    public ValidateStoragePoolResponseAdapter(ServiceCallbackRegistry serviceCallbackRegistry)
    {
        this.serviceCallbackRegistry = serviceCallbackRegistry;
    }

    @Override
    public ServiceResponse<EssValidateStoragePoolResponseMessage> transform(
            EssValidateStoragePoolResponseMessage essValidateStorageResponseMessage)
    {
        return new ServiceResponse<>(essValidateStorageResponseMessage.getMessageProperties().getCorrelationId(),
                essValidateStorageResponseMessage, null);

    }

    @Override
    public void consume(IServiceCallback callback,
            ServiceResponse<EssValidateStoragePoolResponseMessage> storageResponseMessageServiceResponse)
    {
        callback.handleServiceResponse(storageResponseMessageServiceResponse);
    }

    @Override
    public IServiceCallback take(EssValidateStoragePoolResponseMessage essValidateStorageResponseMessage)
    {
        return serviceCallbackRegistry.removeServiceCallback(essValidateStorageResponseMessage.getMessageProperties().getCorrelationId());

    }

    @Override
    public Class<EssValidateStoragePoolResponseMessage> getSourceClass()
    {
        return EssValidateStoragePoolResponseMessage.class;
    }

}

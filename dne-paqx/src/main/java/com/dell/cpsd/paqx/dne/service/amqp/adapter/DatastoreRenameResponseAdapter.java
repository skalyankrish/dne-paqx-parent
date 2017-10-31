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
import com.dell.cpsd.virtualization.capabilities.api.DatastoreRenameResponseMessage;

/**
 * Callback adapter class used to process the response message from datastore rename task.
 * <p>
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public class DatastoreRenameResponseAdapter
        implements ServiceCallbackAdapter<DatastoreRenameResponseMessage, ServiceResponse<DatastoreRenameResponseMessage>>
{
    private final ServiceCallbackRegistry serviceCallbackRegistry;

    public DatastoreRenameResponseAdapter(ServiceCallbackRegistry serviceCallbackRegistry)
    {
        this.serviceCallbackRegistry = serviceCallbackRegistry;
    }

    @Override
    public ServiceResponse<DatastoreRenameResponseMessage> transform(DatastoreRenameResponseMessage datastoreRenameResponseMessage)
    {
        return new ServiceResponse<>(datastoreRenameResponseMessage.getMessageProperties().getCorrelationId(),
                datastoreRenameResponseMessage, null);
    }

    @Override
    public void consume(IServiceCallback callback, ServiceResponse<DatastoreRenameResponseMessage> storageResponseMessageServiceResponse)
    {
        callback.handleServiceResponse(storageResponseMessageServiceResponse);
    }

    @Override
    public IServiceCallback take(DatastoreRenameResponseMessage datastoreRenameResponseMessage)
    {
        return serviceCallbackRegistry.removeServiceCallback(datastoreRenameResponseMessage.getMessageProperties().getCorrelationId());
    }

    @Override
    public Class<DatastoreRenameResponseMessage> getSourceClass()
    {
        return DatastoreRenameResponseMessage.class;
    }

}

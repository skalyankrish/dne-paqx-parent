/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.NodeInventoryResponseMessage;
import com.dell.cpsd.service.common.client.callback.IServiceCallback;
import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackRegistry;

/**
 * Callback adapter class used to process the response message from discover node inventory task.
 * <p>
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public class NodeInventoryResponseMessageAdapter
        implements ServiceCallbackAdapter<NodeInventoryResponseMessage, ServiceResponse<NodeInventoryResponseMessage>>
{
    private final ServiceCallbackRegistry serviceCallbackRegistry;

    public NodeInventoryResponseMessageAdapter(ServiceCallbackRegistry serviceCallbackRegistry)
    {
        this.serviceCallbackRegistry = serviceCallbackRegistry;
    }

    @Override
    public ServiceResponse<NodeInventoryResponseMessage> transform(NodeInventoryResponseMessage nodeInventoryResponseMessage)
    {
        return new ServiceResponse<>(nodeInventoryResponseMessage.getMessageProperties().getCorrelationId(), nodeInventoryResponseMessage,
                null);

    }

    @Override
    public void consume(IServiceCallback callback,
            ServiceResponse<NodeInventoryResponseMessage> nodeInventoryResponseMessageServiceResponse)
    {
        callback.handleServiceResponse(nodeInventoryResponseMessageServiceResponse);
    }

    @Override
    public IServiceCallback take(NodeInventoryResponseMessage nodeInventoryResponseMessage)
    {
        return serviceCallbackRegistry.removeServiceCallback(nodeInventoryResponseMessage.getMessageProperties().getCorrelationId());

    }

    @Override
    public Class<NodeInventoryResponseMessage> getSourceClass()
    {
        return NodeInventoryResponseMessage.class;
    }

}

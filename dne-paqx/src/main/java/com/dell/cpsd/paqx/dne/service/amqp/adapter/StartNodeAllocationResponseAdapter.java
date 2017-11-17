package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.StartNodeAllocationResponseMessage;
import com.dell.cpsd.service.common.client.callback.IServiceCallback;
import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackRegistry;

public class StartNodeAllocationResponseAdapter implements ServiceCallbackAdapter<StartNodeAllocationResponseMessage, ServiceResponse<StartNodeAllocationResponseMessage>>
{
    /*
 * The ServiceCallbackRegistry instance.
 */
    private ServiceCallbackRegistry serviceCallbackRegistry;

    /**
     * StartNodeAllocationResponseAdapter constructor.
     *
     * @param serviceCallbackRegistry - The <code>ServiceCallbackRegistry</code> instance.
     *
     * @since 1.0
     */
    public StartNodeAllocationResponseAdapter(ServiceCallbackRegistry serviceCallbackRegistry)
    {
        this.serviceCallbackRegistry = serviceCallbackRegistry;
    }

    /**
     *
     * @param responseMessage
     * @return
     */
    @Override
    public ServiceResponse<StartNodeAllocationResponseMessage> transform(StartNodeAllocationResponseMessage responseMessage)
    {
        return new ServiceResponse<>(responseMessage.getMessageProperties().getCorrelationId(), responseMessage, null);
    }

    /**
     *
     * @param callback
     * @param responseMessage
     */
    @Override
    public void consume(IServiceCallback callback, ServiceResponse<StartNodeAllocationResponseMessage> responseMessage)
    {
        callback.handleServiceResponse(responseMessage);
    }

    /**
     *
     * @param startNodeAllocationResponseMessage
     * @return
     */
    @Override
    public IServiceCallback take(StartNodeAllocationResponseMessage startNodeAllocationResponseMessage) {
        return serviceCallbackRegistry.removeServiceCallback(startNodeAllocationResponseMessage.getMessageProperties().getCorrelationId());
    }

    /**
     *
     * @return Class<StartNodeAllocationResponseMessage>
     */
    @Override
    public Class<StartNodeAllocationResponseMessage> getSourceClass() {
        return StartNodeAllocationResponseMessage.class;
    }
}

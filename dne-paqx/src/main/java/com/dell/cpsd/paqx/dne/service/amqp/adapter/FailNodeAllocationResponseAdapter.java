package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.FailNodeAllocationResponseMessage;
import com.dell.cpsd.service.common.client.callback.IServiceCallback;
import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackRegistry;

public class FailNodeAllocationResponseAdapter implements ServiceCallbackAdapter<FailNodeAllocationResponseMessage, ServiceResponse<FailNodeAllocationResponseMessage>>
{
    /*
 * The ServiceCallbackRegistry instance.
 */
    private ServiceCallbackRegistry serviceCallbackRegistry;

    /**
     * FailNodeAllocationResponseAdapter constructor.
     *
     * @param serviceCallbackRegistry - The <code>ServiceCallbackRegistry</code> instance.
     *
     * @since 1.0
     */
    public FailNodeAllocationResponseAdapter(ServiceCallbackRegistry serviceCallbackRegistry)
    {
        this.serviceCallbackRegistry = serviceCallbackRegistry;
    }

    /**
     *
     * @param responseMessage
     * @return
     */
    @Override
    public ServiceResponse<FailNodeAllocationResponseMessage> transform(FailNodeAllocationResponseMessage responseMessage)
    {
        return new ServiceResponse<>(responseMessage.getMessageProperties().getCorrelationId(), responseMessage, null);
    }

    /**
     *
     * @param callback
     * @param responseMessage
     */
    @Override
    public void consume(IServiceCallback callback, ServiceResponse<FailNodeAllocationResponseMessage> responseMessage)
    {
        callback.handleServiceResponse(responseMessage);
    }

    /**
     *
     * @param failNodeAllocationResponseMessage
     * @return
     */
    @Override
    public IServiceCallback take(FailNodeAllocationResponseMessage failNodeAllocationResponseMessage) {
        return serviceCallbackRegistry.removeServiceCallback(failNodeAllocationResponseMessage.getMessageProperties().getCorrelationId());
    }

    /**
     *
     * @return Class<FailNodeAllocationResponseMessage>
     */
    @Override
    public Class<FailNodeAllocationResponseMessage> getSourceClass() {
        return FailNodeAllocationResponseMessage.class;
    }
}

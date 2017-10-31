/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.CompleteNodeAllocationResponseMessage;
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
public class CompleteNodeAllocationResponseAdapter implements 
    ServiceCallbackAdapter<CompleteNodeAllocationResponseMessage, ServiceResponse<CompleteNodeAllocationResponseMessage>>
{
    /*
     * The ServiceCallbackRegistry instance.
     */
    private final ServiceCallbackRegistry serviceCallbackRegistry;

    /**
     * CompleteNodeAllocationResponseAdapter constructor.
     * 
     * @param serviceCallbackRegistry - The <code>ServiceCallbackRegistry</code> instance.
     * 
     * @since 1.0
     */
    public CompleteNodeAllocationResponseAdapter(ServiceCallbackRegistry serviceCallbackRegistry)
    {
        this.serviceCallbackRegistry = serviceCallbackRegistry;
    }

    /**
     * Transform a <code>CompleteNodeAllocationResponseMessage</code> to a 
     * <code>ServiceResponse<CompleteNodeAllocationResponseMessage></code> instance.
     * 
     * @param responseMessage - The <code>CompleteNodeAllocationResponseMessage</code> to transform.
     * 
     * @since 1.0
     */
    @Override
    public ServiceResponse<CompleteNodeAllocationResponseMessage> transform(CompleteNodeAllocationResponseMessage responseMessage)
    {
        return new ServiceResponse<>(responseMessage.getMessageProperties().getCorrelationId(), responseMessage, null);
    }

    /**
     * Consume a <code>ServiceResponse<CompleteNodeAllocationResponseMessage></code> instance.
     * 
     * @param callback - The <code>IServiceCallback</code> used to handle the message.
     * @param responseMessage - The <code>ServiceResponse<CompleteNodeAllocationResponseMessage></code> to be consumed.
     * 
     * @since 1.0
     */
    @Override
    public void consume(IServiceCallback callback, ServiceResponse<CompleteNodeAllocationResponseMessage> responseMessage)
    {
        callback.handleServiceResponse(responseMessage);
    }

    /**
     * Remove the service callback for a <code>CompleteNodeAllocationResponseMessage</code> instance.
     * 
     * @param clusterResponseInfo - The <code>CompleteNodeAllocationResponseMessage</code> instance.
     * 
     * @return The removed <code>IServiceCallback</code> instance.
     * 
     * @since 1.0
     */
    @Override
    public IServiceCallback take(CompleteNodeAllocationResponseMessage clusterResponseInfo)
    {
        return serviceCallbackRegistry.removeServiceCallback(clusterResponseInfo.getMessageProperties().getCorrelationId());
    }

    /**
     * Get the <code>CompleteNodeAllocationResponseMessage</code> class instance.
     * 
     * @return The <code>Class<CompleteNodeAllocationResponseMessage></code> instance.
     * 
     * @since 1.0
     */
    @Override
    public Class<CompleteNodeAllocationResponseMessage> getSourceClass()
    {
        return CompleteNodeAllocationResponseMessage.class;
    }
}

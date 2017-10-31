/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.ChangeIdracCredentialsResponseMessage;
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
public class ChangeIdracCredentialsResponseAdapter implements 
    ServiceCallbackAdapter<ChangeIdracCredentialsResponseMessage, ServiceResponse<ChangeIdracCredentialsResponseMessage>>
{
    /*
     * The ServiceCallbackRegistry instance.
     */
    private final ServiceCallbackRegistry serviceCallbackRegistry;

    /**
     * ChangeIdracCredentialsResponseAdapter constructor.
     * 
     * @param serviceCallbackRegistry - The <code>ServiceCallbackRegistry</code> instance.
     * 
     * @since 1.0
     */
    public ChangeIdracCredentialsResponseAdapter(ServiceCallbackRegistry serviceCallbackRegistry)
    {
        this.serviceCallbackRegistry = serviceCallbackRegistry;
    }

    /**
     * Transform a <code>ChangeIdracCredentialsResponseMessage</code> to a 
     * <code>ServiceResponse<ChangeIdracCredentialsResponseMessage></code> instance.
     * 
     * @param responseMessage - The <code>ChangeIdracCredentialsResponseMessage</code> to transform.
     * 
     * @since 1.0
     */
    @Override
    public ServiceResponse<ChangeIdracCredentialsResponseMessage> transform(ChangeIdracCredentialsResponseMessage responseMessage)
    {
        return new ServiceResponse<>(responseMessage.getMessageProperties().getCorrelationId(), responseMessage, null);
    }

    /**
     * Consume a <code>ServiceResponse<ChangeIdracCredentialsResponseMessage></code> instance.
     * 
     * @param callback - The <code>IServiceCallback</code> used to handle the message.
     * @param responseMessage - The <code>ServiceResponse<ChangeIdracCredentialsResponseMessage></code> to be consumed.
     * 
     * @since 1.0
     */
    @Override
    public void consume(IServiceCallback callback, ServiceResponse<ChangeIdracCredentialsResponseMessage> responseMessage)
    {
        callback.handleServiceResponse(responseMessage);
    }

    /**
     * Remove the service callback for a <code>ChangeIdracCredentialsResponseMessage</code> instance.
     * 
     * @param clusterResponseInfo - The <code>ChangeIdracCredentialsResponseMessage</code> instance.
     * 
     * @return The removed <code>IServiceCallback</code> instance.
     * 
     * @since 1.0
     */
    @Override
    public IServiceCallback take(ChangeIdracCredentialsResponseMessage clusterResponseInfo)
    {
        return serviceCallbackRegistry.removeServiceCallback(clusterResponseInfo.getMessageProperties().getCorrelationId());
    }

    /**
     * Get the <code>ChangeIdracCredentialsResponseMessage</code> class instance.
     * 
     * @return The <code>Class<ChangeIdracCredentialsResponseMessage></code> instance.
     * 
     * @since 1.0
     */
    @Override
    public Class<ChangeIdracCredentialsResponseMessage> getSourceClass()
    {
        return ChangeIdracCredentialsResponseMessage.class;
    }
}

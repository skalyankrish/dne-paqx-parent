/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.service.common.client.callback.IServiceCallback;
import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackRegistry;
import com.dell.cpsd.virtualization.capabilities.api.DiscoverClusterResponseInfoMessage;

/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public class ClustersListedResponseAdapter implements ServiceCallbackAdapter<DiscoverClusterResponseInfoMessage, ServiceResponse<DiscoverClusterResponseInfoMessage>>
{
    private final ServiceCallbackRegistry serviceCallbackRegistry;

    public ClustersListedResponseAdapter(ServiceCallbackRegistry serviceCallbackRegistry)
    {
        this.serviceCallbackRegistry = serviceCallbackRegistry;
    }

    @Override
    public ServiceResponse<DiscoverClusterResponseInfoMessage> transform(DiscoverClusterResponseInfoMessage clusterResponseInfo)
    {
        return new ServiceResponse<>(clusterResponseInfo.getMessageProperties().getCorrelationId(), clusterResponseInfo, null);
    }

    @Override
    public void consume(IServiceCallback callback, ServiceResponse<DiscoverClusterResponseInfoMessage> clusterResponseInfo)
    {
        callback.handleServiceResponse(clusterResponseInfo);
    }

    @Override
    public IServiceCallback take(DiscoverClusterResponseInfoMessage clusterResponseInfo)
    {
        return serviceCallbackRegistry.removeServiceCallback(clusterResponseInfo.getMessageProperties().getCorrelationId());
    }

    @Override
    public Class<DiscoverClusterResponseInfoMessage> getSourceClass()
    {
        return DiscoverClusterResponseInfoMessage.class;
    }
}

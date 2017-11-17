/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved. Dell EMC Confidential/Proprietary Information
 *
 */

package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.ConfigureBootDeviceIdracResponseMessage;
import com.dell.cpsd.MessageProperties;
import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * The tests for ConfigureBootDeviceIdracResponseAdapter class.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public class ConfigureBootDeviceIdracResponseAdapterTest extends BaseAsynchronousResponseAdapterTest<ConfigureBootDeviceIdracResponseMessage>
{
    @Override
    protected ServiceCallbackAdapter<ConfigureBootDeviceIdracResponseMessage, ServiceResponse<ConfigureBootDeviceIdracResponseMessage>> createTestable()
    {
        return new ConfigureBootDeviceIdracResponseAdapter(this.registry, this.runtimeService);
    }

    @Override
    protected ConfigureBootDeviceIdracResponseMessage createResponseMessageSpy()
    {
        ConfigureBootDeviceIdracResponseMessage theSpy = spy(ConfigureBootDeviceIdracResponseMessage.class);
        theSpy.setMessageProperties(mock(MessageProperties.class));
        return theSpy;
    }
}
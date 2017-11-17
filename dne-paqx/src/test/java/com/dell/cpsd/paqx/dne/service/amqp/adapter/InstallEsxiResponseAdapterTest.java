/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved. Dell EMC Confidential/Proprietary Information
 *
 */

package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.InstallESXiResponseMessage;
import com.dell.cpsd.MessageProperties;
import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * The tests for InstallEsxiResponseAdapter class.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public class InstallEsxiResponseAdapterTest extends BaseAsynchronousResponseAdapterTest<InstallESXiResponseMessage>
{
    @Override
    protected ServiceCallbackAdapter<InstallESXiResponseMessage, ServiceResponse<InstallESXiResponseMessage>> createTestable()
    {
        return new InstallEsxiResponseAdapter(this.registry, this.runtimeService);
    }

    @Override
    protected InstallESXiResponseMessage createResponseMessageSpy()
    {
        InstallESXiResponseMessage theSpy = spy(InstallESXiResponseMessage.class);
        theSpy.setMessageProperties(mock(MessageProperties.class));
        return theSpy;
    }
}
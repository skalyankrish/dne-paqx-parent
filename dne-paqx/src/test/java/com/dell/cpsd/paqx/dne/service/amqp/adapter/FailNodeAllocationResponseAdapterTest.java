/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved. Dell EMC Confidential/Proprietary Information
 *
 */

package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.CompleteNodeAllocationResponseMessage;
import com.dell.cpsd.FailNodeAllocationResponseMessage;
import com.dell.cpsd.MessageProperties;
import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * The tests for CompleteNodeAllocationResponseAdapter class.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public class FailNodeAllocationResponseAdapterTest extends BaseResponseAdapterTest<FailNodeAllocationResponseMessage>
{
    @Override
    protected ServiceCallbackAdapter<FailNodeAllocationResponseMessage, ServiceResponse<FailNodeAllocationResponseMessage>> createTestable()
    {
        return new FailNodeAllocationResponseAdapter(this.registry);
    }

    @Override
    protected FailNodeAllocationResponseMessage createResponseMessageSpy()
    {
        FailNodeAllocationResponseMessage theSpy = spy(FailNodeAllocationResponseMessage.class);
        theSpy.setMessageProperties(mock(MessageProperties.class));
        return theSpy;
    }
}
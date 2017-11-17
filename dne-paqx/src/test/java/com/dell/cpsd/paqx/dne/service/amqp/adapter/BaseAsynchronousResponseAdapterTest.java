/*
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved. Dell EMC Confidential/Proprietary Information
 *
 */

package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.common.rabbitmq.message.HasMessageProperties;
import com.dell.cpsd.common.rabbitmq.message.MessagePropertiesContainer;
import com.dell.cpsd.paqx.dne.amqp.callback.AsynchronousNodeServiceCallback;
import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackRegistry;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.ExecutionQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public abstract class BaseAsynchronousResponseAdapterTest<R extends HasMessageProperties<?>>
{
    @Mock
    protected ServiceCallbackRegistry registry;

    @Mock
    protected AsynchronousNodeServiceCallback<ServiceResponse<R>> serviceCallback;

    @Mock
    protected ServiceResponse<R> serviceResponse;

    @Mock
    protected RuntimeService runtimeService;

    @Mock
    protected ExecutionQuery executionQuery;

    @Mock
    protected Execution execution;

    protected ServiceCallbackAdapter<R, ServiceResponse<R>> adapter;
    protected MessagePropertiesContainer properties;
    protected String correlationId = "correlationId";
    protected R responseMessage;

    protected abstract ServiceCallbackAdapter<R, ServiceResponse<R>> createTestable();
    protected abstract <R> R createResponseMessageSpy();

    /**
     * The test setup.
     *
     * @since 1.0
     */
    @Before
    public void setUp() throws Exception
    {
        this.adapter = this.createTestable();
        this.responseMessage = this.createResponseMessageSpy();
        this.properties = this.responseMessage.getMessageProperties();

        doReturn("1").when(this.serviceCallback).getProcessInstanceId();
        doReturn("2").when(this.serviceCallback).getActivityId();
        doReturn("3").when(this.serviceCallback).getMessageId();
    }

    @Test
    public void transform() throws Exception
    {
        doReturn(this.properties).when(this.responseMessage).getMessageProperties();
        doReturn(this.correlationId).when(this.properties).getCorrelationId();

        assertNotNull(this.adapter.transform(this.responseMessage));
    }

    @Test
    public void consume() throws Exception
    {
        doReturn(this.executionQuery).when(this.runtimeService).createExecutionQuery();
        doReturn(this.executionQuery).when(this.executionQuery).processInstanceId(any());
        doReturn(this.executionQuery).when(this.executionQuery).activityId(any());
        doReturn(this.execution).when(this.executionQuery).singleResult();
        doReturn("4").when(this.execution).getId();

        this.adapter.consume(this.serviceCallback, this.serviceResponse);

        verify(this.serviceCallback).handleServiceResponse(this.serviceResponse);
        verify(this.runtimeService).messageEventReceived("3", "4");
    }

    @Test
    public void take() throws Exception
    {
        doReturn(this.properties).when(this.responseMessage).getMessageProperties();
        doReturn(this.correlationId).when(this.properties).getCorrelationId();

        this.adapter.take(this.responseMessage);

        verify(this.registry).removeServiceCallback(this.correlationId);
    }

    @Test
    public void getSourceClass() throws Exception
    {
        assertEquals(this.responseMessage.getClass().getSuperclass(), this.adapter.getSourceClass());
    }
}

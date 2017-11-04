/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */
package com.dell.cpsd.paqx.dne.service.amqp;

import com.dell.cpsd.ConfigureBootDeviceIdracResponseMessage;
import com.dell.cpsd.MessageProperties;
import com.dell.cpsd.paqx.dne.amqp.callback.AsynchronousNodeServiceCallback;
import com.dell.cpsd.paqx.dne.amqp.producer.DneProducer;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.model.BootDeviceIdracStatus;
import com.dell.cpsd.paqx.dne.service.model.ConfigureBootDeviceIdracRequest;
import com.dell.cpsd.service.common.client.callback.ServiceError;
import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.exception.ServiceExecutionException;
import com.dell.cpsd.service.common.client.rpc.DefaultMessageConsumer;
import com.dell.cpsd.service.common.client.rpc.DelegatingMessageConsumer;
import org.camunda.bpm.engine.RuntimeService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AmqpAsynchronousNodeServiceTest
{
    private DelegatingMessageConsumer consumer;
    private DneProducer dneProducer;
    private DataServiceRepository repository;
    private RuntimeService runtimeService;
    private String replyTo;

    private static final String processInstanceId = "PI1";
    private static final String activityId = "Activity1";
    private static final String messageId = "Message1";

    private AmqpAsynchronousNodeService amqpAsynchronousNodeService;

    @Before
    public void setup() {
        consumer = mock(DefaultMessageConsumer.class);
        dneProducer = mock(DneProducer.class);
        repository = mock(DataServiceRepository.class);
        runtimeService = mock(RuntimeService.class);
        replyTo = "AmqpAsynchronousNodeServiceTest";

        amqpAsynchronousNodeService = new AmqpAsynchronousNodeService(consumer,
                                                                      dneProducer,
                                                                      replyTo,
                                                                      repository,
                                                                      runtimeService);

    }

    @Ignore
    public void verifyInitialization() {
        verify(consumer, times(1)).addAdapter(any());
    }

    @Test
    public void bootDeviceIdracStatusRequestSuccessful() throws Exception
    {
        ConfigureBootDeviceIdracRequest request =  new ConfigureBootDeviceIdracRequest();
        request.setUuid("UUID1");
        request.setIdracIpAddress("192.168.1.1");

        AsynchronousNodeServiceCallback<?> callback = amqpAsynchronousNodeService.bootDeviceIdracStatusRequest(processInstanceId,
                                                                                                               activityId,
                                                                                                               messageId,
                                                                                                               request);
        assertNotNull(callback);
        assertEquals(callback.getProcessInstanceId(), processInstanceId);
        assertEquals(callback.getActivityId(), activityId);
        assertEquals(callback.getMessageId(), messageId);
        verify(dneProducer, times(1)).publishConfigureBootDeviceIdrac(any());
    }

    @Test
    public void bootDeviceIdracStatusRequestException() throws Exception
    {
        ConfigureBootDeviceIdracRequest request =  new ConfigureBootDeviceIdracRequest();
        request.setUuid("UUID1");
        request.setIdracIpAddress("192.168.1.1");

        amqpAsynchronousNodeService.release();

        AsynchronousNodeServiceCallback<?> callback = amqpAsynchronousNodeService.bootDeviceIdracStatusRequest(processInstanceId,
                                                                                                               activityId,
                                                                                                               messageId,
                                                                                                               request);
        assertNull(callback);
    }

    @Test
    public void bootDeviceIdracStatusResponseSuccessful() throws Exception
    {
        AsynchronousNodeServiceCallback<ServiceResponse<ConfigureBootDeviceIdracResponseMessage>> callback = mock(AsynchronousNodeServiceCallback.class);
        when(callback.isDone()).thenReturn(true);
        when(callback.getProcessInstanceId()).thenReturn(processInstanceId);
        when(callback.getActivityId()).thenReturn(activityId);
        when(callback.getMessageId()).thenReturn(messageId);
        when(callback.getServiceError()).thenReturn(null);

        ConfigureBootDeviceIdracResponseMessage responseMessage = new ConfigureBootDeviceIdracResponseMessage();
        MessageProperties messageProperties = mock(MessageProperties.class);
        responseMessage.setMessageProperties(messageProperties);
        responseMessage.setStatus(ConfigureBootDeviceIdracResponseMessage.Status.SUCCESS);
        ServiceResponse<ConfigureBootDeviceIdracResponseMessage> response = new ServiceResponse<ConfigureBootDeviceIdracResponseMessage>("UUID1", responseMessage, "Configure");
        when(callback.getServiceResponse()).thenReturn(response);

        BootDeviceIdracStatus status = amqpAsynchronousNodeService.bootDeviceIdracStatusResponse(callback);

        assertNotNull(status);
        assertEquals(status.getStatus(), "SUCCESS");
    }

    @Test(expected = ServiceExecutionException.class)
    public void bootDeviceIdracStatusResponseException() throws Exception
    {
        AsynchronousNodeServiceCallback<ServiceResponse<ConfigureBootDeviceIdracResponseMessage>> callback = mock(
                AsynchronousNodeServiceCallback.class);
        when(callback.isDone()).thenReturn(true);
        when(callback.getProcessInstanceId()).thenReturn(processInstanceId);
        when(callback.getActivityId()).thenReturn(activityId);
        when(callback.getMessageId()).thenReturn(messageId);
        ServiceError error = new ServiceError("UUID1", "error1", "ErrorMessage1");
        when(callback.getServiceError()).thenReturn(error);

        amqpAsynchronousNodeService.bootDeviceIdracStatusResponse(callback);
    }
}

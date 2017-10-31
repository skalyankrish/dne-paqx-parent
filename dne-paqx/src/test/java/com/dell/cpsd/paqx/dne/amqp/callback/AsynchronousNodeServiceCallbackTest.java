/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */
package com.dell.cpsd.paqx.dne.amqp.callback;

import com.dell.cpsd.service.common.client.callback.ServiceError;
import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AsynchronousNodeServiceCallbackTest
{
    private AsynchronousNodeServiceCallback<ServiceResponse<String>> asynchronousNodeServiceCallback;

    @Before
    public void setUp() {
            asynchronousNodeServiceCallback = new AsynchronousNodeServiceCallback<ServiceResponse<String>>("1",
                                                                                                           "2",
                                                                                                           "3");
    }

    @Test
    public void handleServiceError() throws Exception
    {
        String requestId = "request id 1";
        String errorCode = "error code 1";
        String errorMessage = "error message 1";
        ServiceError serviceError = new ServiceError(requestId, errorCode, errorMessage);

        asynchronousNodeServiceCallback.handleServiceError(serviceError);

        assertEquals(serviceError, asynchronousNodeServiceCallback.getServiceError());
        assertTrue(asynchronousNodeServiceCallback.isDone());
    }

    @Test
    public void handleServiceResponse() throws Exception
    {
        String requestId = "request id 1";
        String response = "response 1";
        String message = "message 1";
        ServiceResponse serviceresponse = new ServiceResponse<>(requestId, response, message);

        asynchronousNodeServiceCallback.handleServiceResponse(serviceresponse);

        assertEquals(serviceresponse, asynchronousNodeServiceCallback.getServiceResponse());
        assertTrue(asynchronousNodeServiceCallback.isDone());
    }
}

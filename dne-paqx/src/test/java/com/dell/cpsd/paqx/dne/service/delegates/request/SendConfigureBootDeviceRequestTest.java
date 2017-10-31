/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */
package com.dell.cpsd.paqx.dne.service.delegates.request;

import com.dell.cpsd.paqx.dne.amqp.callback.AsynchronousNodeServiceCallback;
import com.dell.cpsd.paqx.dne.service.AsynchronousNodeService;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants;
import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.Test;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SendConfigureBootDeviceRequestTest
{
    private SendConfigureBootDeviceRequest sendConfigureBootDeviceRequest;
    private AsynchronousNodeService asynchronousNodeService;
    private DelegateExecution delegateExecution;
    private NodeDetail nodeDetail;
    private AsynchronousNodeServiceCallback<ServiceResponse<?>> asynchronousNodeServiceCallback;

    @Before
    public void setUp() throws Exception
    {
        asynchronousNodeService = mock(AsynchronousNodeService.class);

        sendConfigureBootDeviceRequest = new SendConfigureBootDeviceRequest(asynchronousNodeService);

        asynchronousNodeServiceCallback = mock(AsynchronousNodeServiceCallback.class);
        doReturn(asynchronousNodeServiceCallback).when(asynchronousNodeService).bootDeviceIdracStatusRequest(any(), any(), any(), any());

        delegateExecution = mock(DelegateExecution.class);

        nodeDetail = new NodeDetail();
        nodeDetail.setId("1");
        nodeDetail.setIdracIpAddress("1");
        nodeDetail.setIdracGatewayIpAddress("1");
        nodeDetail.setIdracSubnetMask("1");
        nodeDetail.setServiceTag("abc");

        doReturn(nodeDetail).when(delegateExecution).getVariable(NODE_DETAIL);
        doReturn("1").when(delegateExecution).getProcessInstanceId();
    }

    @Test
    public void delegateExecuteSuccess() throws Exception
    {
        sendConfigureBootDeviceRequest.delegateExecute(delegateExecution);
        verify(delegateExecution,times(1)).setVariable(DelegateConstants.CONFIGURE_BOOT_DEVICE_MESSAGE_ID, asynchronousNodeServiceCallback);
    }

    @Test
    public void delegateExecuteRequestUnsuccessful() throws Exception
    {

        doReturn(null).when(asynchronousNodeService).bootDeviceIdracStatusRequest(any(), any(), any(), any());
        try{
            sendConfigureBootDeviceRequest.delegateExecute(delegateExecution);
            fail("An exception was expected.");
        } catch (BpmnError error) {
            assertTrue(error.getErrorCode().equals(DelegateConstants.CONFIGURE_BOOT_DEVICE_FAILED));
            assertTrue(error.getMessage().equals("Failed to send the request for to Configure Boot Device on Node abc"));
        }

    }
}

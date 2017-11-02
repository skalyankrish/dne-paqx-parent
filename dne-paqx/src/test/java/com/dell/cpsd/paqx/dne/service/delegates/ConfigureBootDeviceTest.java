/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */
package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.amqp.callback.AsynchronousNodeServiceCallback;
import com.dell.cpsd.paqx.dne.service.AsynchronousNodeService;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants;
import com.dell.cpsd.paqx.dne.service.model.BootDeviceIdracStatus;
import com.dell.cpsd.service.common.client.exception.ServiceExecutionException;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.Test;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.CONFIGURE_BOOT_DEVICE_MESSAGE_ID;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ConfigureBootDeviceTest {

    private ConfigureBootDevice configureBootDevice;
    private AsynchronousNodeService asynchronousNodeService;
    private DelegateExecution delegateExecution;
    private NodeDetail nodeDetail;
    private AsynchronousNodeServiceCallback<?> asynchronousNodeServiceCallback;
    private BootDeviceIdracStatus bootDeviceIdracStatus;


    @Before
    public void setUp() throws Exception
    {
        asynchronousNodeService = mock(AsynchronousNodeService.class);
        configureBootDevice = new ConfigureBootDevice(asynchronousNodeService);
        delegateExecution = mock(DelegateExecution.class);
        bootDeviceIdracStatus = mock(BootDeviceIdracStatus.class);
        when(bootDeviceIdracStatus.getStatus()). thenReturn("SUCCESS");
        asynchronousNodeServiceCallback = mock(AsynchronousNodeServiceCallback.class);
        when(asynchronousNodeServiceCallback.isDone()).thenReturn(true);
        nodeDetail = new NodeDetail();
        nodeDetail.setId("1");
        nodeDetail.setIdracIpAddress("1");
        nodeDetail.setIdracGatewayIpAddress("1");
        nodeDetail.setIdracSubnetMask("1");
        nodeDetail.setServiceTag("abc");
        when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
        when(delegateExecution.getVariable(CONFIGURE_BOOT_DEVICE_MESSAGE_ID)).thenReturn(asynchronousNodeServiceCallback);
    }

    @Test
    public void testFailedBootDeviceConfig() throws Exception {
        try {
            when(asynchronousNodeService.bootDeviceIdracStatusResponse(asynchronousNodeServiceCallback)).thenThrow(new ServiceExecutionException("An Error happened!"));
            configureBootDevice.delegateExecute(delegateExecution);
            fail("An exception was expected.");
        } catch (BpmnError error) {
            assertTrue(error.getErrorCode().equals(DelegateConstants.CONFIGURE_BOOT_DEVICE_FAILED));
            assertTrue(error.getMessage().equals("Configure Boot Device on Node abc failed!  Reason: An Error happened!"));
        }
    }

    @Test
    public void testSuccess() throws Exception {
        when(asynchronousNodeService.bootDeviceIdracStatusResponse(asynchronousNodeServiceCallback)).thenReturn(bootDeviceIdracStatus);
        final ConfigureBootDevice c = spy(new ConfigureBootDevice(asynchronousNodeService));
        c.delegateExecute(delegateExecution);
        verify(c).updateDelegateStatus("Boot Device Configuration was successful on Node abc");
        }

    @Test
    public void testException() throws Exception {
        try {
            when(bootDeviceIdracStatus.getStatus()). thenReturn("FAIL");
            configureBootDevice.delegateExecute(delegateExecution);
        } catch (BpmnError error) {
            assertTrue(error.getErrorCode().equals(DelegateConstants.CONFIGURE_BOOT_DEVICE_FAILED));
            assertTrue((error.getMessage().equals("Boot Device Configuration was unsuccessful on Node abc. Please correct the following errors and try again.\n")));
        }
    }
}

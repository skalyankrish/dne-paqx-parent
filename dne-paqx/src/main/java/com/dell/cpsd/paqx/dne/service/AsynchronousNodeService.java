/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.service;

import com.dell.cpsd.paqx.dne.amqp.callback.AsynchronousNodeServiceCallback;
import com.dell.cpsd.paqx.dne.service.model.BootDeviceIdracStatus;
import com.dell.cpsd.paqx.dne.service.model.ConfigureBootDeviceIdracRequest;
import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.exception.ServiceExecutionException;

/**
 * Node service interface.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public interface AsynchronousNodeService
{
    public <T extends ServiceResponse<?>> AsynchronousNodeServiceCallback<?> bootDeviceIdracStatusRequest(final String processId,
                                                                                                          final String activityId,
                                                                                                          final String messageId,
                                                                                                          final ConfigureBootDeviceIdracRequest configureBootDeviceIdracRequest);

    public BootDeviceIdracStatus bootDeviceIdracStatusResponse(final AsynchronousNodeServiceCallback<?> serviceCallback)
            throws ServiceExecutionException;


}

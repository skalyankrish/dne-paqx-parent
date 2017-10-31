/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
package com.dell.cpsd.paqx.dne.amqp.callback;

import com.dell.cpsd.service.common.client.callback.IServiceCallback;
import com.dell.cpsd.service.common.client.callback.ServiceError;
import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.callback.ServiceTimeout;

import java.io.Serializable;

public class AsynchronousNodeServiceCallback<T extends ServiceResponse<?>> implements IServiceCallback<T>, Serializable
{
    private static final long serialVersionUID = 75445335622776147L;

    private String processInstanceId;
    private String activityId;
    private String messageId;
    private volatile boolean done = false;
    private ServiceError error = null;
    private T serviceResponse = null;

    public AsynchronousNodeServiceCallback(final String processInstanceId, final String activityId, final String messageId)
    {
        this.processInstanceId = processInstanceId;
        this.activityId = activityId;
        this.messageId = messageId;
    }

    public ServiceError getServiceError() {
        return this.error;
    }

    public T getServiceResponse() {
        return this.serviceResponse;
    }

    public boolean isDone() {
        return this.done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public void handleServiceError(ServiceError error) {
        this.error = error;
        this.setDone(true);
    }

    public void handleServiceTimeout(ServiceTimeout timeout) {
    }

    public void handleServiceResponse(T serviceResponse) {
        this.serviceResponse = serviceResponse;
        this.setDone(true);
    }

    public String getProcessInstanceId()
    {
        return processInstanceId;
    }

    public void setProcessInstanceId(final String processInstanceId)
    {
        this.processInstanceId = processInstanceId;
    }

    public String getActivityId()
    {
        return activityId;
    }

    public void setActivityId(final String activityId)
    {
        this.activityId = activityId;
    }

    public String getMessageId()
    {
        return messageId;
    }

    public void setMessageId(final String messageId)
    {
        this.messageId = messageId;
    }
}

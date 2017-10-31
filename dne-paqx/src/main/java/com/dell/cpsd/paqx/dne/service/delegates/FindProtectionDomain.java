/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
package com.dell.cpsd.paqx.dne.service.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Qualifier("findProtectionDomain")
public class FindProtectionDomain extends BaseWorkflowDelegate
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FindProtectionDomain.class);


    private static final int WAIT_TIME = 1000; // 50 seconds

    @Autowired
    public FindProtectionDomain(){
    }

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Execute ProtectionDomain ");
        try
        {
            Thread.sleep(WAIT_TIME);
        }
        catch(InterruptedException e){}
        LOGGER.info("Successfully completed retrieving Protection Domain");
    }
}

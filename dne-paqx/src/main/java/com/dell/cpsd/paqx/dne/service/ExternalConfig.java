/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * <p>
 * Returns default environment(production) valuesfor the settings below
 */
@Configuration
public class ExternalConfig implements IExternalConfig
{
    @Autowired
    private Environment environment;

    @Override
    public String getRabbitHostname()
    {
        return this.environment.getRequiredProperty("remote.dell.amqp.rabbitHostname");
    }

    @Override
    public Boolean isSslEnabled()
    {
        return Boolean.valueOf(this.environment.getProperty("remote.dell.amqp.rabbitIsSslEnabled", Boolean.TRUE.toString()));
    }

    @Override
    public Integer getRabbitPort()
    {
        return (Integer)this.environment.getProperty("remote.dell.amqp.rabbitPort", Integer.class, Integer.valueOf(RABBIT_INSECURE_PORT));
    }

    @Override
    public String getTlsVersion()
    {
        return this.environment.getProperty("remote.dell.amqp.rabbitTlsVersion", "TLSv1.2");
    }

    @Override
    public String getRabbitUsername()
    {
        return this.environment.getProperty("remote.dell.amqp.rabbitUsername", "");
    }

    @Override
    public String getRabbitPassword()
    {
        return this.environment.getProperty("remote.dell.amqp.rabbitPassword", "");
    }
}

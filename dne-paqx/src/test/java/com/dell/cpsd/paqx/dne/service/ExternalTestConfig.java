/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.service;

import com.dell.cpsd.common.integration.docker.compose.DockerComposeLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * The test configuration for the values below.
 * Configuration marked as @Primary so it will take precedence when testing.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@Configuration
@Primary
public class ExternalTestConfig implements IExternalConfig
{
    @Override
    public String getRabbitHostname()
    {
        return DockerComposeLauncher.getIPForContainer("amqp");
    }

    @Override
    public Integer getRabbitPort()
    {
        return 5672;
    }

    @Override
    public String getTlsVersion()
    {
        return null;
    }

    @Override
    public String getRabbitUsername()
    {
        return System.getProperty("rabbitusername");
    }

    @Override
    public String getRabbitPassword()
    {
        return System.getProperty("rabbitpassword");
    }

    @Override
    public Boolean isSslEnabled()
    {
        return false;
    }

}

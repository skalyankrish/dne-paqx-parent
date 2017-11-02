/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */
package com.dell.cpsd.paqx.dne.amqp.config;

import com.dell.cpsd.common.rabbitmq.config.RabbitMQPropertiesConfig;
import com.dell.cpsd.paqx.dne.service.IExternalConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */

@Configuration
@PropertySources({@PropertySource(value = "classpath:META-INF/spring/dne-paqx/rabbitmq.properties"),
        @PropertySource(value = "classpath:META-INF/spring/dne-paqx/service.properties"),
        @PropertySource(value = "file:/etc/rabbitmq/client/rabbitmq-config.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "file:${CREDENTIALS}", ignoreResourceNotFound = true),
        @PropertySource(value = "file:${PASSPHRASES}", ignoreResourceNotFound = true)})
@Qualifier("rabbitPropertiesConfig")
public class PropertiesConfig extends RabbitMQPropertiesConfig {

    @Autowired
    private IExternalConfig externalConfig;

    @Bean
    @Override
    public String rabbitHostname()
    {
        return externalConfig.getRabbitHostname();
    }

    @Bean
    @Override
    public Integer rabbitPort()
    {
        return externalConfig.getRabbitPort();
    }

    @Bean
    @Override
    public Boolean isSslEnabled() {
        return externalConfig.isSslEnabled();
    }

    @Bean
    @Override
    public String tlsVersion() {
        return externalConfig.getTlsVersion();
    }

    @Bean
    @Override
    public String rabbitUsername()
    {
        return externalConfig.getRabbitUsername();
    }

    @Bean
    @Override
    public String rabbitPassword()
    {
        return externalConfig.getRabbitPassword();
    }

}


/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */
package com.dell.cpsd.paqx.dne.amqp.config;

import com.dell.cpsd.common.rabbitmq.config.RabbitMQPropertiesConfig;
import org.springframework.beans.factory.annotation.Qualifier;
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
}


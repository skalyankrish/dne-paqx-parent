/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.amqp.config;

import com.dell.cpsd.common.rabbitmq.connectors.RabbitMQCachingConnectionFactory;
import com.dell.cpsd.common.rabbitmq.connectors.RabbitMQTLSFactoryBean;
import com.rabbitmq.client.DefaultSaslConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */

@Configuration
@Import({PropertiesConfig.class})
public class ProductionConfig
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductionConfig.class);

    @Autowired
    private PropertiesConfig propertiesConfig;

    @Bean
    @Qualifier("rabbitConnectionFactory")
    public ConnectionFactory productionCachingConnectionFactory()
    {

        RabbitMQCachingConnectionFactory cachingCF = null;
        com.rabbitmq.client.ConnectionFactory connectionFactory;

        LOGGER.info("Rabbit connection properties : sslEnabled: [{}], host: [{}], port: [{}], tlsVersion:[{}]",
                propertiesConfig.isSslEnabled(), propertiesConfig.rabbitHostname(), propertiesConfig.rabbitPort(),
                propertiesConfig.tlsVersion());
        try
        {
            if (propertiesConfig.isSslEnabled())
            {
                RabbitMQTLSFactoryBean rabbitMQTLSFactoryBean = new RabbitMQTLSFactoryBean(propertiesConfig);

                connectionFactory = rabbitMQTLSFactoryBean.getObject();

                cachingCF = new RabbitMQCachingConnectionFactory(connectionFactory, propertiesConfig);
                cachingCF.getRabbitConnectionFactory().setSaslConfig(DefaultSaslConfig.EXTERNAL);
            }
            else
            {
                connectionFactory = new com.rabbitmq.client.ConnectionFactory();
                cachingCF = new RabbitMQCachingConnectionFactory(connectionFactory, propertiesConfig);
            }
        }
        catch (Exception e)
        {
            LOGGER.error("Exception retrieving connection factory", e);
            e.printStackTrace();
        }

        return cachingCF;
    }
}

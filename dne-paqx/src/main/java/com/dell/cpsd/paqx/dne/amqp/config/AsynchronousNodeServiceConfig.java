/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.amqp.config;

import com.dell.cpsd.paqx.dne.amqp.producer.DneProducer;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.AsynchronousNodeService;
import com.dell.cpsd.paqx.dne.service.amqp.AmqpAsynchronousNodeService;
import com.dell.cpsd.sdk.AMQPClient;
import com.dell.cpsd.sdk.SystemDefinitionMessenger;
import com.dell.cpsd.sdk.config.SDKConfiguration;
import com.dell.cpsd.service.common.client.rpc.DelegatingMessageConsumer;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
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
@ComponentScan({"com.dell.cpsd.paqx.dne.service", "com.dell.cpsd.paqx.dne.transformers", "com.dell.cpsd.paqx.dne.repository"})
@Import({
    RabbitConfig.class,
    ConsumerConfig.class,
    ProducerConfig.class,
    SDKConfiguration.class,
    SystemDefinitionMessenger.class,
    AMQPClient.class,
    PersistenceConfig.class
})
public class AsynchronousNodeServiceConfig
{

    private DataServiceRepository repository;

    private RuntimeService runtimeService;

    @Autowired
    public AsynchronousNodeServiceConfig(final DataServiceRepository repository,
                                         final RuntimeService runtimeService)
    {
        this.repository = repository;
        this.runtimeService = runtimeService;
    }

    @Bean
    public AsynchronousNodeService asynchronousNodeServiceClient(@Autowired DelegatingMessageConsumer delegatingMessageConsumer,
                                                                 @Autowired DneProducer dneProducer,
                                                                 @Autowired String replyTo)
    {
        return new AmqpAsynchronousNodeService(delegatingMessageConsumer,
                                               dneProducer,
                                               replyTo,
                                               repository, runtimeService);
    }
}

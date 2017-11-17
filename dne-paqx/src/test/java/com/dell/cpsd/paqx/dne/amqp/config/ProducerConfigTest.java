/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
package com.dell.cpsd.paqx.dne.amqp.config;

import com.dell.cpsd.hdp.capability.registry.client.binder.CapabilityBinder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.Assert.assertNotNull;

/**
 * Test for {@link ProducerConfig}
 */
@RunWith(MockitoJUnitRunner.class)
public class ProducerConfigTest {

    @InjectMocks
    private ProducerConfig producerConfig;

    @Mock
    RabbitTemplate rabbitTemplate;

    @Mock
    CapabilityBinder capabilityBinder;

    @Test
    public void testDneProducer()
    {
        assertNotNull( producerConfig.dneProducer(rabbitTemplate, capabilityBinder));
    }
}

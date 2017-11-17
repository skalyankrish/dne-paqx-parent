/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
package com.dell.cpsd.paqx.dne.amqp.config;

import com.dell.cpsd.common.rabbitmq.connectors.RabbitMQCachingConnectionFactory;
import com.dell.cpsd.common.rabbitmq.connectors.TLSConnectionFactory;
import com.rabbitmq.client.ConnectionFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * test for {@link ProductionConfig}
 */
@RunWith(MockitoJUnitRunner.class)
public class ProductionConfigTest {
    @InjectMocks
    private ProductionConfig productionConfig;

    @Mock
    private PropertiesConfig propertiesConfig;

    @Test
    public void testProductionCachingConnectionFactoryWithoutSSL() throws Exception {
        assertNotNull( productionConfig.productionCachingConnectionFactory());
        assertEquals ( ((RabbitMQCachingConnectionFactory)productionConfig.productionCachingConnectionFactory()).getRabbitConnectionFactory().getClass().getName(),
                ConnectionFactory.class.getName());

    }

    @Test
    @Ignore
    public void testProductionCachingConnectionFactoryWithSSL() throws Exception {
        when( propertiesConfig.isSslEnabled()).thenReturn(true);
        when(propertiesConfig.rabbitPort()).thenReturn(5671);
        when(propertiesConfig.rabbitHostname()).thenReturn("amqp");
        when(propertiesConfig.tlsVersion()).thenReturn("1.2");

        assertNotNull( productionConfig.productionCachingConnectionFactory());
        assertEquals ( ((RabbitMQCachingConnectionFactory)productionConfig.productionCachingConnectionFactory()).getRabbitConnectionFactory().getClass().getName(),
                TLSConnectionFactory.class.getName());

    }

}

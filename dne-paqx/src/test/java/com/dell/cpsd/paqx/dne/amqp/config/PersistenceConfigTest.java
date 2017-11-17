/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
package com.dell.cpsd.paqx.dne.amqp.config;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertNotNull;

/**
 * Test for {@link PersistenceConfig}
 */
@SpringBootTest
@RunWith(MockitoJUnitRunner.class)

public class PersistenceConfigTest {
    private PersistenceConfig persistenceConfig;

    @Before
    public void setup(){
        persistenceConfig = new PersistenceConfig();
        ReflectionTestUtils.setField(persistenceConfig, "databasePassword", "");
        ReflectionTestUtils.setField(persistenceConfig, "databaseUrl", "jdbc:h2:~/tmp/dnepaqxdb");
        ReflectionTestUtils.setField(persistenceConfig, "databaseDriverClassName", "org.h2.Driver");
        ReflectionTestUtils.setField(persistenceConfig, "databaseUsername", "sa");
        ReflectionTestUtils.setField(persistenceConfig, "hibernateDialect", "org.hibernate.dialect.H2Dialect");
        ReflectionTestUtils.setField(persistenceConfig, "hibernateShowSql", "false");
        ReflectionTestUtils.setField(persistenceConfig, "hibernateHBM2DdlAuto", "create-drop");
        ReflectionTestUtils.setField(persistenceConfig, "hibernateNamingStrategy", "org.hibernate.cfg.EJB3NamingStrategy");
        ReflectionTestUtils.setField(persistenceConfig, "hibernateDefaultSchema", "DNE");
    }

    @Test
    public void testDataSource() throws Exception {
        assertNotNull( persistenceConfig.dataSource());
    }

    @Test
    public void testTransactionManager() throws Exception {
        assertNotNull( persistenceConfig.transactionManager());
    }

    @Test
    public void testEntityManagerFactory() throws Exception {
        assertNotNull( persistenceConfig.entityManagerFactory());
    }

    @Test
    public void testJpaVendorAdapter() throws Exception {
        assertNotNull( persistenceConfig.jpaVendorAdapter());
    }
}

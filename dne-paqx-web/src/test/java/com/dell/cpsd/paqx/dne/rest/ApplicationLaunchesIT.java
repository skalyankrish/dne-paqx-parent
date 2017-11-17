/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */
package com.dell.cpsd.paqx.dne.rest;

import com.dell.cpsd.common.integration.docker.compose.DockerComposeLauncher;
import org.apache.commons.lang3.StringUtils;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestExecutionListeners(listeners = {
         org.springframework.boot.test.autoconfigure.restdocs.RestDocsTestExecutionListener.class,
         org.springframework.test.context.web.ServletTestExecutionListener.class,
         org.springframework.test.context.support.DirtiesContextBeforeModesTestExecutionListener.class,
         org.springframework.boot.test.autoconfigure.SpringBootDependencyInjectionTestExecutionListener.class,
         org.springframework.test.context.support.DirtiesContextTestExecutionListener.class,
         org.springframework.test.context.transaction.TransactionalTestExecutionListener.class,
         org.springframework.test.context.jdbc.SqlScriptsTestExecutionListener.class})
/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */
public class ApplicationLaunchesIT
{
    @BeforeClass
    public static void setup()
    {
        System.setProperty("spring.cloud.consul.host", DockerComposeLauncher.getIPForContainer("consul"));
    }
    @Test
    @Ignore
    public void testApplicationLaunchesSuccessfully()
    {
        assertTrue(StringUtils.isNotEmpty(DockerComposeLauncher.getIPForContainer("amqp")));
        assertTrue(StringUtils.isNotEmpty(DockerComposeLauncher.getIPForContainer("consul")));

        //By default IT's are skipped to make for a better developer experience.  To unskip them, you run
        //mvn clean install -DskipITs=false

        //To run the IT's in the IDE, just run the IntegrationTestSuite class as a unit test.
        //Make sure you set up your credentials as VM args in your IDE's run configuration
        //-Drabbitusername=${RABBITMQ_USER} -Drabbitpassword=${RABBITMQ_USER}
        //replace ${RABBITMQ_USER} with the actual rabbit username and password

        //You can use the internal ports of vault and amqp when referencing via the ip's like above.

        //Make the following changes locally to be able to run the Integration Tests with DockerCompose.
        //For every integration test (*IT) that you write, you add the test to the list in IntegrationTestSuite
        /**
         * Put the following in etc/docker/daemon.json and then restart the docker service:
         *
         * {
         "insecure-registries": ["registry.hub.docker.com", "docker-dev-local.art.local", "docker-prod-local.art.local", "docker-remote.art.local", "docker-virtual.art.local"]
         }

         */

        /**
         * Ensure that you have the following in your /etc/hosts file:
         * 10.239.131.63 repo.vmo.lab docker-dev-local.art.local docker-prod-local.art.local docker-remote.art.local docker-virtual.art.local
         */
    }
}

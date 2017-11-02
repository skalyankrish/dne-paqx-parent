/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 **/

package com.dell.cpsd.paqx.dne.rest;

import com.dell.cpsd.common.integration.docker.compose.DockerComposeLauncher;
import com.dell.cpsd.common.integration.docker.compose.ServiceInfo;
import org.joda.time.Duration;
import org.junit.ClassRule;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.io.File;

@RunWith(Suite.class)
@Suite.SuiteClasses({ApplicationLaunchesIT.class})
/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 *
 * This test class is the integration test suite
 * It spins up the appropriate docker containers, runs the integration tests and spins down the containers at the end.
 **/
public class IntegrationTestSuite
{
    private static final String     dockerComposeRulesLoggingDirectory = "containersStartupLogs";
    private static final String     envFilePath                        = "../ci/docker/.env";
    private static final String[][] variables                          = {{"TAG", "devel"},
            {"WORKSPACE", new File(System.getProperty("user.dir")).getParent()}, {"COMPOSE_PROJECT_NAME", "devel"}};
    private static final String[]   DOCKER_COMPOSE_FILE                = {"../ci/docker/docker-compose-it.yml"};
    private static final Duration   TIMEOUT_VALUE                      = new Duration(Long.MAX_VALUE);

    /**
     * TYhe services required for the fru integration test
     */
    private static ServiceInfo[] services = {

            new ServiceInfo.AmqpServiceInfo(), new ServiceInfo.ConsulServiceInfo()};

    //Creates the docker compose
    @ClassRule
    public static TestRule spinupDockerCompose()
    {
        return DockerComposeLauncher
                .launchDockerCompose(services, envFilePath, variables, TIMEOUT_VALUE, dockerComposeRulesLoggingDirectory,
                        DOCKER_COMPOSE_FILE, true, false);
    }
}

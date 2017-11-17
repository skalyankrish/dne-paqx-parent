/**
 * Startup application.
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */
package com.dell.cpsd.paqx.dne.rest;

import com.dell.cpsd.hdp.capability.registry.client.lookup.config.CapabilityRegistryLookupManagerConfig;
import com.dell.cpsd.paqx.dne.amqp.config.AsynchronousNodeServiceConfig;
import com.dell.cpsd.paqx.dne.amqp.config.ServiceConfig;
import org.h2.tools.Server;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.sql.SQLException;

@SpringBootApplication
@EnableDiscoveryClient
@EnableSwagger2
@Import({ServiceConfig.class , AsynchronousNodeServiceConfig.class, CapabilityRegistryLookupManagerConfig.class })
public class NodeExpansionWebApplication {

    public static void main(String[] args) {

        SpringApplication.run(NodeExpansionWebApplication.class, args);

        //TODO: Remove this its only for testing
        try
        {
            Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8555").start();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }


    }
}

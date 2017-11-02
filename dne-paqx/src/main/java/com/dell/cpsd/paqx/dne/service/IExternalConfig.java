/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */
package com.dell.cpsd.paqx.dne.service;

/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 *
 * Interface to capture external changeables
 */
public interface IExternalConfig
{
    Integer RABBIT_INSECURE_PORT=5672;

    String getRabbitHostname();

    Boolean isSslEnabled();

    Integer getRabbitPort();

    String getTlsVersion();

    String getRabbitUsername();

    String getRabbitPassword();
}


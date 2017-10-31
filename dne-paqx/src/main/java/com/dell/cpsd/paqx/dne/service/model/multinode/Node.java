/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 */
package com.dell.cpsd.paqx.dne.service.model.multinode;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Node implements Serializable {
    @JsonProperty
    public String getName() {
        return name;
    }

    public void setName(String n) {
        this.name = n;
    }

    private String name;
    public Node(String n) {
        name = n;
    }

    @Override
    public String toString() {
        return name;
    }
}


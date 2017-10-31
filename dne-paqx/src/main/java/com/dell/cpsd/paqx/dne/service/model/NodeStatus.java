/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.service.model;

public enum NodeStatus {
    DISCOVERED("DISCOVERED"),
    ADDED("ADDED"),
    FAILED("FAILED"),
    RESERVED("RESERVED");

    private final String stateMessage;
    NodeStatus(String stateMsg) {
        stateMessage = stateMsg;
    }

    @Override
    public String toString() {
        return stateMessage;
    }
}

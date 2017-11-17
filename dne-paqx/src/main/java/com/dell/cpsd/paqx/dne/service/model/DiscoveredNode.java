/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.service.model;

public class DiscoveredNode
{
    private final String convergedUuid;
    private final com.dell.cpsd.DiscoveredNode.AllocationStatus nodeStatus;
    private final String serial;
    private final String product;
    private final String vendor;

    public DiscoveredNode(String convergedUuid, com.dell.cpsd.DiscoveredNode.AllocationStatus nodeStatus, String serial, String product,
            String vendor)
    {
        this.convergedUuid = convergedUuid;
        this.nodeStatus = nodeStatus;
        this.serial = serial;
        this.product = product;
        this.vendor = vendor;
    }

    public String getConvergedUuid()
    {
        return convergedUuid;
    }

    public com.dell.cpsd.DiscoveredNode.AllocationStatus getNodeStatus() {
        return nodeStatus;
    }

    public String getSerial()
    {
        return serial;
    }

    public String getProduct()
    {
        return product;
    }

    public String getVendor()
    {
        return vendor;
    }
}

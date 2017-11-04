/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
package com.dell.cpsd.paqx.dne.service.delegates.model;

import java.io.Serializable;

public class NodeDetail implements Serializable
{
    private String id;
    private String serviceTag;

    private String idracIpAddress;
    private String idracGatewayIpAddress;
    private String idracSubnetMask;

    private String esxiManagementIpAddress;
    private String esxiManagementGatewayIpAddress;
    private String esxiManagementSubnetMask;
    private String esxiManagementHostname;

    private String scaleIoData1SvmIpAddress;
    private String scaleIoData1KernelIpAddress;
    private String scaleIoSvmData1SubnetMask;
    private String scaleIoData2SvmIpAddress;
    private String scaleIoData2KernelIpAddress;
    private String scaleIoSvmData2SubnetMask;
    private String scaleIoSvmManagementIpAddress;
    private String scaleIoSvmManagementGatewayAddress;
    private String scaleIoSvmManagementSubnetMask;
    private String hostname;
    private String clusterName;
    private String vMotionManagementIpAddress;
    private String vMotionManagementSubnetMask;
    private String protectionDomain;

    public String getId()
    {
        return id;
    }

    public void setId(final String id)
    {
        this.id = id;
    }

    public String getServiceTag()
    {
        return serviceTag;
    }

    public void setServiceTag(final String serviceTag)
    {
        this.serviceTag = serviceTag;
    }

    public String getIdracIpAddress()
    {
        return idracIpAddress;
    }

    public void setIdracIpAddress(final String idracIpAddress)
    {
        this.idracIpAddress = idracIpAddress;
    }

    public String getIdracGatewayIpAddress()
    {
        return idracGatewayIpAddress;
    }

    public void setIdracGatewayIpAddress(final String idracGatewayIpAddress)
    {
        this.idracGatewayIpAddress = idracGatewayIpAddress;
    }

    public String getIdracSubnetMask()
    {
        return idracSubnetMask;
    }

    public void setIdracSubnetMask(final String idracSubnetMask)
    {
        this.idracSubnetMask = idracSubnetMask;
    }

    public String getEsxiManagementIpAddress()
    {
        return esxiManagementIpAddress;
    }

    public void setEsxiManagementIpAddress(final String esxiManagementIpAddress)
    {
        this.esxiManagementIpAddress = esxiManagementIpAddress;
    }

    public String getEsxiManagementGatewayIpAddress()
    {
        return esxiManagementGatewayIpAddress;
    }

    public void setEsxiManagementGatewayIpAddress(final String esxiManagementGatewayIpAddress)
    {
        this.esxiManagementGatewayIpAddress = esxiManagementGatewayIpAddress;
    }

    public String getEsxiManagementSubnetMask()
    {
        return esxiManagementSubnetMask;
    }

    public void setEsxiManagementSubnetMask(final String esxiManagementSubnetMask)
    {
        this.esxiManagementSubnetMask = esxiManagementSubnetMask;
    }

    public String getEsxiManagementHostname()
    {
        return esxiManagementHostname;
    }

    public void setEsxiManagementHostname(final String esxiManagementHostname)
    {
        this.esxiManagementHostname = esxiManagementHostname;
    }

    public String getScaleIoData1SvmIpAddress()
    {
        return scaleIoData1SvmIpAddress;
    }

    public void setScaleIoData1SvmIpAddress(final String scaleIoData1SvmIpAddress)
    {
        this.scaleIoData1SvmIpAddress = scaleIoData1SvmIpAddress;
    }

    public String getScaleIoData1KernelIpAddress()
    {
        return scaleIoData1KernelIpAddress;
    }

    public void setScaleIoData1KernelIpAddress(final String scaleIoData1KernelIpAddress)
    {
        this.scaleIoData1KernelIpAddress = scaleIoData1KernelIpAddress;
    }

    public String getScaleIoSvmData1SubnetMask()
    {
        return scaleIoSvmData1SubnetMask;
    }

    public void setScaleIoSvmData1SubnetMask(final String scaleIoSvmData1SubnetMask)
    {
        this.scaleIoSvmData1SubnetMask = scaleIoSvmData1SubnetMask;
    }

    public String getScaleIoData2SvmIpAddress()
    {
        return scaleIoData2SvmIpAddress;
    }

    public void setScaleIoData2SvmIpAddress(final String scaleIoData2SvmIpAddress)
    {
        this.scaleIoData2SvmIpAddress = scaleIoData2SvmIpAddress;
    }

    public String getScaleIoData2KernelIpAddress()
    {
        return scaleIoData2KernelIpAddress;
    }

    public void setScaleIoData2KernelIpAddress(final String scaleIoData2KernelIpAddress)
    {
        this.scaleIoData2KernelIpAddress = scaleIoData2KernelIpAddress;
    }

    public String getScaleIoSvmData2SubnetMask()
    {
        return scaleIoSvmData2SubnetMask;
    }

    public void setScaleIoSvmData2SubnetMask(final String scaleIoSvmData2SubnetMask)
    {
        this.scaleIoSvmData2SubnetMask = scaleIoSvmData2SubnetMask;
    }

    public String getScaleIoSvmManagementIpAddress()
    {
        return scaleIoSvmManagementIpAddress;
    }

    public void setScaleIoSvmManagementIpAddress(final String scaleIoSvmManagementIpAddress)
    {
        this.scaleIoSvmManagementIpAddress = scaleIoSvmManagementIpAddress;
    }

    public String getScaleIoSvmManagementGatewayAddress()
    {
        return scaleIoSvmManagementGatewayAddress;
    }

    public void setScaleIoSvmManagementGatewayAddress(final String scaleIoSvmManagementGatewayAddress)
    {
        this.scaleIoSvmManagementGatewayAddress = scaleIoSvmManagementGatewayAddress;
    }

    public String getScaleIoSvmManagementSubnetMask()
    {
        return scaleIoSvmManagementSubnetMask;
    }

    public void setScaleIoSvmManagementSubnetMask(final String scaleIoSvmManagementSubnetMask)
    {
        this.scaleIoSvmManagementSubnetMask = scaleIoSvmManagementSubnetMask;
    }

    public String getHostname()
    {
        return hostname;
    }

    public void setHostname(final String hostname)
    {
        this.hostname = hostname;
    }

    public String getClusterName()
    {
        return clusterName;
    }

    public void setClusterName(final String clusterName)
    {
        this.clusterName = clusterName;
    }

    public String getvMotionManagementIpAddress()
    {
        return vMotionManagementIpAddress;
    }

    public void setvMotionManagementIpAddress(final String vMotionManagementIpAddress)
    {
        this.vMotionManagementIpAddress = vMotionManagementIpAddress;
    }

    public String getvMotionManagementSubnetMask()
    {
        return vMotionManagementSubnetMask;
    }

    public void setvMotionManagementSubnetMask(final String vMotionManagementSubnetMask)
    {
        this.vMotionManagementSubnetMask = vMotionManagementSubnetMask;
    }

    public String getProtectionDomain()
    {
        return protectionDomain;
    }

    public void setProtectionDomain(final String protectionDomain)
    {
        this.protectionDomain = protectionDomain;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof NodeDetail))
        {
            return false;
        }

        final NodeDetail that = (NodeDetail) o;

        if (!getId().equals(that.getId()))
        {
            return false;
        }
        return getServiceTag().equals(that.getServiceTag());
    }

    @Override
    public int hashCode()
    {
        int result = getId().hashCode();
        result = 31 * result + getServiceTag().hashCode();
        return result;
    }
}

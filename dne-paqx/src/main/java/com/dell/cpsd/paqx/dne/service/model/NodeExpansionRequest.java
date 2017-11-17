/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.service.model;

import com.dell.cpsd.service.engineering.standards.DeviceAssignment;

import java.util.Map;

public class NodeExpansionRequest
{
    private String idracIpAddress;
    private String idracGatewayIpAddress;
    private String idracSubnetMask;

    private String esxiManagementIpAddress;
    private String esxiManagementGatewayIpAddress;
    private String esxiManagementSubnetMask;
    private String esxiManagementHostname;

    private String scaleIoData1SvmIpAddress;
    private String scaleIoData1SvmSubnetMask;
    private String scaleIoData2SvmIpAddress;
    private String scaleIoData2SvmSubnetMask;
    private String scaleIoData1EsxIpAddress;
    private String scaleIoData1EsxSubnetMask;
    private String scaleIoData2EsxIpAddress;
    private String scaleIoData2EsxSubnetMask;
    private String scaleIoSvmManagementIpAddress;
    private String scaleIoSvmManagementGatewayAddress;
    private String scaleIoSvmManagementSubnetMask;
    private String clusterName;
    private String symphonyUuid;
    private String vMotionManagementIpAddress;
    private String vMotionManagementSubnetMask;
    private String protectionDomainName;
    private String protectionDomainId;

    private Map<String, DeviceAssignment> deviceToDeviceStoragePool;

    public NodeExpansionRequest()
    {
    }

    public NodeExpansionRequest(final String idracIpAddress, final String idracGatewayIpAddress, final String idracSubnetMask,
            final String esxiManagementIpAddress, final String esxiManagementGatewayIpAddress, final String esxiManagementSubnetMask,
            final String esxiManagementHostname, final String scaleIoData1SvmIpAddress, final String scaleIoData1SvmSubnetMask,
            final String scaleIoData2SvmIpAddress, final String scaleIoData2SvmSubnetMask, final String scaleIoData1EsxIpAddress,
            final String scaleIoData1EsxSubnetMask, final String scaleIoData2EsxIpAddress, final String scaleIoData2EsxSubnetMask,
            final String scaleIoSvmManagementIpAddress, final String scaleIoSvmManagementGatewayAddress,
            final String scaleIoSvmManagementSubnetMask, final String clusterName, final String symphonyUuid,
            final String vMotionManagementIpAddress, final String vMotionManagementSubnetMask, final String protectionDomainName,
            final String protectionDomainId, final Map<String, DeviceAssignment> deviceToDeviceStoragePool)
    {
        this.idracIpAddress = idracIpAddress;
        this.idracGatewayIpAddress = idracGatewayIpAddress;
        this.idracSubnetMask = idracSubnetMask;
        this.esxiManagementIpAddress = esxiManagementIpAddress;
        this.esxiManagementGatewayIpAddress = esxiManagementGatewayIpAddress;
        this.esxiManagementSubnetMask = esxiManagementSubnetMask;
        this.esxiManagementHostname = esxiManagementHostname;
        this.scaleIoData1SvmIpAddress = scaleIoData1SvmIpAddress;
        this.scaleIoData1SvmSubnetMask = scaleIoData1SvmSubnetMask;
        this.scaleIoData2SvmIpAddress = scaleIoData2SvmIpAddress;
        this.scaleIoData2SvmSubnetMask = scaleIoData2SvmSubnetMask;
        this.scaleIoData1EsxIpAddress = scaleIoData1EsxIpAddress;
        this.scaleIoData1EsxSubnetMask = scaleIoData1EsxSubnetMask;
        this.scaleIoData2EsxIpAddress = scaleIoData2EsxIpAddress;
        this.scaleIoData2EsxSubnetMask = scaleIoData2EsxSubnetMask;
        this.scaleIoSvmManagementIpAddress = scaleIoSvmManagementIpAddress;
        this.scaleIoSvmManagementGatewayAddress = scaleIoSvmManagementGatewayAddress;
        this.scaleIoSvmManagementSubnetMask = scaleIoSvmManagementSubnetMask;
        this.clusterName = clusterName;
        this.symphonyUuid = symphonyUuid;
        this.vMotionManagementIpAddress = vMotionManagementIpAddress;
        this.vMotionManagementSubnetMask = vMotionManagementSubnetMask;
        this.protectionDomainName = protectionDomainName;
        this.protectionDomainId = protectionDomainId;
        this.deviceToDeviceStoragePool = deviceToDeviceStoragePool;
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

    public String getScaleIoData1SvmSubnetMask()
    {
        return scaleIoData1SvmSubnetMask;
    }

    public void setScaleIoData1SvmSubnetMask(final String scaleIoData1SvmSubnetMask)
    {
        this.scaleIoData1SvmSubnetMask = scaleIoData1SvmSubnetMask;
    }

    public String getScaleIoData2SvmIpAddress()
    {
        return scaleIoData2SvmIpAddress;
    }

    public void setScaleIoData2SvmIpAddress(final String scaleIoData2SvmIpAddress)
    {
        this.scaleIoData2SvmIpAddress = scaleIoData2SvmIpAddress;
    }

    public String getScaleIoData2SvmSubnetMask()
    {
        return scaleIoData2SvmSubnetMask;
    }

    public void setScaleIoData2SvmSubnetMask(final String scaleIoData2SvmSubnetMask)
    {
        this.scaleIoData2SvmSubnetMask = scaleIoData2SvmSubnetMask;
    }

    public String getScaleIoData1EsxIpAddress()
    {
        return scaleIoData1EsxIpAddress;
    }

    public void setScaleIoData1EsxIpAddress(final String scaleIoData1EsxIpAddress)
    {
        this.scaleIoData1EsxIpAddress = scaleIoData1EsxIpAddress;
    }

    public String getScaleIoData1EsxSubnetMask()
    {
        return scaleIoData1EsxSubnetMask;
    }

    public void setScaleIoData1EsxSubnetMask(final String scaleIoData1EsxSubnetMask)
    {
        this.scaleIoData1EsxSubnetMask = scaleIoData1EsxSubnetMask;
    }

    public String getScaleIoData2EsxIpAddress()
    {
        return scaleIoData2EsxIpAddress;
    }

    public void setScaleIoData2EsxIpAddress(final String scaleIoData2EsxIpAddress)
    {
        this.scaleIoData2EsxIpAddress = scaleIoData2EsxIpAddress;
    }

    public String getScaleIoData2EsxSubnetMask()
    {
        return scaleIoData2EsxSubnetMask;
    }

    public void setScaleIoData2EsxSubnetMask(final String scaleIoData2EsxSubnetMask)
    {
        this.scaleIoData2EsxSubnetMask = scaleIoData2EsxSubnetMask;
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

    public String getClusterName()
    {
        return clusterName;
    }

    public void setClusterName(final String clusterName)
    {
        this.clusterName = clusterName;
    }

    public String getSymphonyUuid()
    {
        return symphonyUuid;
    }

    public void setSymphonyUuid(final String symphonyUuid)
    {
        this.symphonyUuid = symphonyUuid;
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

    public String getProtectionDomainName()
    {
        return protectionDomainName;
    }

    public void setProtectionDomainName(final String protectionDomainName)
    {
        this.protectionDomainName = protectionDomainName;
    }

    public String getProtectionDomainId()
    {
        return protectionDomainId;
    }

    public void setProtectionDomainId(final String protectionDomainId)
    {
        this.protectionDomainId = protectionDomainId;
    }

    public Map<String, DeviceAssignment> getDeviceToDeviceStoragePool()
    {
        return deviceToDeviceStoragePool;
    }

    public void setDeviceToDeviceStoragePool(final Map<String, DeviceAssignment> deviceToDeviceStoragePool)
    {
        this.deviceToDeviceStoragePool = deviceToDeviceStoragePool;
    }

    @Override
    public String toString()
    {
        return "NodeExpansionRequest{" + "idracIpAddress='" + idracIpAddress + '\'' + ", idracGatewayIpAddress='" + idracGatewayIpAddress
                + '\'' + ", idracSubnetMask='" + idracSubnetMask + '\'' + ", esxiManagementIpAddress='" + esxiManagementIpAddress + '\''
                + ", esxiManagementGatewayIpAddress='" + esxiManagementGatewayIpAddress + '\'' + ", esxiManagementSubnetMask='"
                + esxiManagementSubnetMask + '\'' + ", esxiManagementHostname='" + esxiManagementHostname + '\''
                + ", scaleIoData1SvmIpAddress='" + scaleIoData1SvmIpAddress + '\'' + ", scaleIoData1SvmSubnetMask='"
                + scaleIoData1SvmSubnetMask + '\'' + ", scaleIoData2SvmIpAddress='" + scaleIoData2SvmIpAddress + '\''
                + ", scaleIoData2SvmSubnetMask='" + scaleIoData2SvmSubnetMask + '\'' + ", scaleIoData1EsxIpAddress='"
                + scaleIoData1EsxIpAddress + '\'' + ", scaleIoData1EsxSubnetMask='" + scaleIoData1EsxSubnetMask + '\''
                + ", scaleIoData2EsxIpAddress='" + scaleIoData2EsxIpAddress + '\'' + ", scaleIoData2EsxSubnetMask='"
                + scaleIoData2EsxSubnetMask + '\'' + ", scaleIoSvmManagementIpAddress='" + scaleIoSvmManagementIpAddress + '\''
                + ", scaleIoSvmManagementGatewayAddress='" + scaleIoSvmManagementGatewayAddress + '\''
                + ", scaleIoSvmManagementSubnetMask='" + scaleIoSvmManagementSubnetMask + '\'' + ", clusterName='" + clusterName + '\''
                + ", symphonyUuid='" + symphonyUuid + '\'' + ", vMotionManagementIpAddress='" + vMotionManagementIpAddress + '\''
                + ", vMotionManagementSubnetMask='" + vMotionManagementSubnetMask + '\'' + ", protectionDomainName='" + protectionDomainName
                + '\'' + ", protectionDomainId='" + protectionDomainId + '\'' + ", deviceToDeviceStoragePool=" + deviceToDeviceStoragePool
                + '}';
    }
}

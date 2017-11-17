/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
package com.dell.cpsd.paqx.dne.service.delegates.model;

import com.dell.cpsd.service.engineering.standards.DeviceAssignment;

import java.io.Serializable;
import java.util.Map;

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
    private String vMotionManagementIpAddress;
    private String vMotionManagementSubnetMask;
    private String protectionDomainName;
    private String protectionDomainId;

    public NodeDetail()
    {
    }

    public NodeDetail(final String id, final String serviceTag)
    {
        this.id = id;
        this.serviceTag = serviceTag;
    }

    private Map<String, DeviceAssignment> deviceToDeviceStoragePool;

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

    public String getScaleIoData2SvmSubnetMask()
    {
        return scaleIoData2SvmSubnetMask;
    }

    public String getScaleIoData1EsxIpAddress()
    {
        return scaleIoData1EsxIpAddress;
    }

    public String getScaleIoData1EsxSubnetMask()
    {
        return scaleIoData1EsxSubnetMask;
    }

    public String getScaleIoData2EsxIpAddress()
    {
        return scaleIoData2EsxIpAddress;
    }

    public String getScaleIoData2EsxSubnetMask()
    {
        return scaleIoData2EsxSubnetMask;
    }

    public String getProtectionDomainName()
    {
        return protectionDomainName;
    }

    public String getProtectionDomainId()
    {
        return protectionDomainId;
    }

    public Map<String, DeviceAssignment> getDeviceToDeviceStoragePool()
    {
        return deviceToDeviceStoragePool;
    }

    public void setScaleIoData2SvmSubnetMask(final String scaleIoData2SvmSubnetMask)
    {
        this.scaleIoData2SvmSubnetMask = scaleIoData2SvmSubnetMask;
    }

    public void setScaleIoData1EsxIpAddress(final String scaleIoData1EsxIpAddress)
    {
        this.scaleIoData1EsxIpAddress = scaleIoData1EsxIpAddress;
    }

    public void setScaleIoData1EsxSubnetMask(final String scaleIoData1EsxSubnetMask)
    {
        this.scaleIoData1EsxSubnetMask = scaleIoData1EsxSubnetMask;
    }

    public void setScaleIoData2EsxIpAddress(final String scaleIoData2EsxIpAddress)
    {
        this.scaleIoData2EsxIpAddress = scaleIoData2EsxIpAddress;
    }

    public void setScaleIoData2EsxSubnetMask(final String scaleIoData2EsxSubnetMask)
    {
        this.scaleIoData2EsxSubnetMask = scaleIoData2EsxSubnetMask;
    }

    public void setProtectionDomainName(final String protectionDomainName)
    {
        this.protectionDomainName = protectionDomainName;
    }

    public void setProtectionDomainId(final String protectionDomainId)
    {
        this.protectionDomainId = protectionDomainId;
    }

    public void setDeviceToDeviceStoragePool(final Map<String, DeviceAssignment> deviceToDeviceStoragePool)
    {
        this.deviceToDeviceStoragePool = deviceToDeviceStoragePool;
    }

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

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        final NodeDetail that = (NodeDetail) o;

        if (idracIpAddress != null ? !idracIpAddress.equals(that.idracIpAddress) : that.idracIpAddress != null)
        {
            return false;
        }
        if (idracGatewayIpAddress != null ? !idracGatewayIpAddress.equals(that.idracGatewayIpAddress) : that.idracGatewayIpAddress != null)
        {
            return false;
        }
        if (idracSubnetMask != null ? !idracSubnetMask.equals(that.idracSubnetMask) : that.idracSubnetMask != null)
        {
            return false;
        }
        if (esxiManagementIpAddress != null ?
                !esxiManagementIpAddress.equals(that.esxiManagementIpAddress) :
                that.esxiManagementIpAddress != null)
        {
            return false;
        }
        if (esxiManagementGatewayIpAddress != null ?
                !esxiManagementGatewayIpAddress.equals(that.esxiManagementGatewayIpAddress) :
                that.esxiManagementGatewayIpAddress != null)
        {
            return false;
        }
        if (esxiManagementSubnetMask != null ?
                !esxiManagementSubnetMask.equals(that.esxiManagementSubnetMask) :
                that.esxiManagementSubnetMask != null)
        {
            return false;
        }
        if (esxiManagementHostname != null ?
                !esxiManagementHostname.equals(that.esxiManagementHostname) :
                that.esxiManagementHostname != null)
        {
            return false;
        }
        if (scaleIoData1SvmIpAddress != null ?
                !scaleIoData1SvmIpAddress.equals(that.scaleIoData1SvmIpAddress) :
                that.scaleIoData1SvmIpAddress != null)
        {
            return false;
        }
        if (scaleIoData1SvmSubnetMask != null ?
                !scaleIoData1SvmSubnetMask.equals(that.scaleIoData1SvmSubnetMask) :
                that.scaleIoData1SvmSubnetMask != null)
        {
            return false;
        }
        if (scaleIoData2SvmIpAddress != null ?
                !scaleIoData2SvmIpAddress.equals(that.scaleIoData2SvmIpAddress) :
                that.scaleIoData2SvmIpAddress != null)
        {
            return false;
        }
        if (scaleIoData2SvmSubnetMask != null ?
                !scaleIoData2SvmSubnetMask.equals(that.scaleIoData2SvmSubnetMask) :
                that.scaleIoData2SvmSubnetMask != null)
        {
            return false;
        }
        if (scaleIoData1EsxIpAddress != null ?
                !scaleIoData1EsxIpAddress.equals(that.scaleIoData1EsxIpAddress) :
                that.scaleIoData1EsxIpAddress != null)
        {
            return false;
        }
        if (scaleIoData1EsxSubnetMask != null ?
                !scaleIoData1EsxSubnetMask.equals(that.scaleIoData1EsxSubnetMask) :
                that.scaleIoData1EsxSubnetMask != null)
        {
            return false;
        }
        if (scaleIoData2EsxIpAddress != null ?
                !scaleIoData2EsxIpAddress.equals(that.scaleIoData2EsxIpAddress) :
                that.scaleIoData2EsxIpAddress != null)
        {
            return false;
        }
        if (scaleIoData2EsxSubnetMask != null ?
                !scaleIoData2EsxSubnetMask.equals(that.scaleIoData2EsxSubnetMask) :
                that.scaleIoData2EsxSubnetMask != null)
        {
            return false;
        }
        if (scaleIoSvmManagementIpAddress != null ?
                !scaleIoSvmManagementIpAddress.equals(that.scaleIoSvmManagementIpAddress) :
                that.scaleIoSvmManagementIpAddress != null)
        {
            return false;
        }
        if (scaleIoSvmManagementGatewayAddress != null ?
                !scaleIoSvmManagementGatewayAddress.equals(that.scaleIoSvmManagementGatewayAddress) :
                that.scaleIoSvmManagementGatewayAddress != null)
        {
            return false;
        }
        if (scaleIoSvmManagementSubnetMask != null ?
                !scaleIoSvmManagementSubnetMask.equals(that.scaleIoSvmManagementSubnetMask) :
                that.scaleIoSvmManagementSubnetMask != null)
        {
            return false;
        }
        if (clusterName != null ? !clusterName.equals(that.clusterName) : that.clusterName != null)
        {
            return false;
        }
        if (vMotionManagementIpAddress != null ?
                !vMotionManagementIpAddress.equals(that.vMotionManagementIpAddress) :
                that.vMotionManagementIpAddress != null)
        {
            return false;
        }
        if (vMotionManagementSubnetMask != null ?
                !vMotionManagementSubnetMask.equals(that.vMotionManagementSubnetMask) :
                that.vMotionManagementSubnetMask != null)
        {
            return false;
        }
        if (protectionDomainName != null ? !protectionDomainName.equals(that.protectionDomainName) : that.protectionDomainName != null)
        {
            return false;
        }
        if (protectionDomainId != null ? !protectionDomainId.equals(that.protectionDomainId) : that.protectionDomainId != null)
        {
            return false;
        }
        return deviceToDeviceStoragePool != null ?
                deviceToDeviceStoragePool.equals(that.deviceToDeviceStoragePool) :
                that.deviceToDeviceStoragePool == null;
    }

    @Override
    public int hashCode()
    {
        int result = idracIpAddress != null ? idracIpAddress.hashCode() : 0;
        result = 31 * result + (idracGatewayIpAddress != null ? idracGatewayIpAddress.hashCode() : 0);
        result = 31 * result + (idracSubnetMask != null ? idracSubnetMask.hashCode() : 0);
        result = 31 * result + (esxiManagementIpAddress != null ? esxiManagementIpAddress.hashCode() : 0);
        result = 31 * result + (esxiManagementGatewayIpAddress != null ? esxiManagementGatewayIpAddress.hashCode() : 0);
        result = 31 * result + (esxiManagementSubnetMask != null ? esxiManagementSubnetMask.hashCode() : 0);
        result = 31 * result + (esxiManagementHostname != null ? esxiManagementHostname.hashCode() : 0);
        result = 31 * result + (scaleIoData1SvmIpAddress != null ? scaleIoData1SvmIpAddress.hashCode() : 0);
        result = 31 * result + (scaleIoData1SvmSubnetMask != null ? scaleIoData1SvmSubnetMask.hashCode() : 0);
        result = 31 * result + (scaleIoData2SvmIpAddress != null ? scaleIoData2SvmIpAddress.hashCode() : 0);
        result = 31 * result + (scaleIoData2SvmSubnetMask != null ? scaleIoData2SvmSubnetMask.hashCode() : 0);
        result = 31 * result + (scaleIoData1EsxIpAddress != null ? scaleIoData1EsxIpAddress.hashCode() : 0);
        result = 31 * result + (scaleIoData1EsxSubnetMask != null ? scaleIoData1EsxSubnetMask.hashCode() : 0);
        result = 31 * result + (scaleIoData2EsxIpAddress != null ? scaleIoData2EsxIpAddress.hashCode() : 0);
        result = 31 * result + (scaleIoData2EsxSubnetMask != null ? scaleIoData2EsxSubnetMask.hashCode() : 0);
        result = 31 * result + (scaleIoSvmManagementIpAddress != null ? scaleIoSvmManagementIpAddress.hashCode() : 0);
        result = 31 * result + (scaleIoSvmManagementGatewayAddress != null ? scaleIoSvmManagementGatewayAddress.hashCode() : 0);
        result = 31 * result + (scaleIoSvmManagementSubnetMask != null ? scaleIoSvmManagementSubnetMask.hashCode() : 0);
        result = 31 * result + (clusterName != null ? clusterName.hashCode() : 0);
        result = 31 * result + (vMotionManagementIpAddress != null ? vMotionManagementIpAddress.hashCode() : 0);
        result = 31 * result + (vMotionManagementSubnetMask != null ? vMotionManagementSubnetMask.hashCode() : 0);
        result = 31 * result + (protectionDomainName != null ? protectionDomainName.hashCode() : 0);
        result = 31 * result + (protectionDomainId != null ? protectionDomainId.hashCode() : 0);
        result = 31 * result + (deviceToDeviceStoragePool != null ? deviceToDeviceStoragePool.hashCode() : 0);
        return result;
    }

}

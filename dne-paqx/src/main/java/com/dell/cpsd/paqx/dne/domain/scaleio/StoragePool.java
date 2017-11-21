/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 */

package com.dell.cpsd.paqx.dne.domain.scaleio;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Storage Pool
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * </p>
 *
 * @version 1.0
 * @since 1.1
 */
@Entity
@Table(name = "STORAGE_POOL")
public class StoragePool
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STORAGE_POOL_UUID")
    private Long uuid;

    @Column
    private String id;

    @Column
    private String name;

    @Column(name = "PROTECTION_DOMIN_ID")
    private String protectionDomainId;

    @OneToOne(cascade = CascadeType.ALL)
    private ScaleIOSDCVolume scaleIoSdcVolume;

    public StoragePool()
    {
        // Default Constructor
    }

    public StoragePool(final String id, final String name, final String protectionDomainId)
    {
        this.id = id;
        this.name = name;
        this.protectionDomainId = protectionDomainId;
    }

    public Long getUuid()
    {
        return uuid;
    }

    public void setUuid(final Long uuid)
    {
        this.uuid = uuid;
    }

    public String getId()
    {
        return id;
    }

    public void setId(final String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public String getProtectionDomainId()
    {
        return protectionDomainId;
    }

    public void setProtectionDomainId(final String protectionDomainId)
    {
        this.protectionDomainId = protectionDomainId;
    }

    public ScaleIOSDCVolume getScaleIoSdcVolume()
    {
        return scaleIoSdcVolume;
    }

    public void setScaleIoSdcVolume(final ScaleIOSDCVolume scaleIoSdcVolume)
    {
        this.scaleIoSdcVolume = scaleIoSdcVolume;
    }

    @Override
    public String toString()
    {
        return "StoragePool{" + "uuid=" + uuid + ", id='" + id + '\'' + ", name='" + name + '\'' + ", protectionDomainId='"
                + protectionDomainId + '\'' + '}';
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

        final StoragePool that = (StoragePool) o;

        return new EqualsBuilder().append(uuid, that.uuid).append(id, that.id).append(name, that.name)
                .append(protectionDomainId, that.protectionDomainId).isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37).append(uuid).append(id).append(name).append(protectionDomainId).toHashCode();
    }
}
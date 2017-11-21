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
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

/**
 * TODO: Document usage.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * </p>
 *
 * @version 1.0
 * @since 1.1
 */
@Entity
@Table(name = "SCALEIO_SDC_VOLUME")
public class ScaleIOSDCVolume
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SDC_VOLUME_UUID", unique = true, nullable = false)
    private Long uuid;

    @Column(name = "VOLUME_ID")
    private String id;

    @Column
    private String name;

    @Column(name = "SIZE_IN_KB")
    private Long sizeInKb;

    @OneToOne(optional = false, mappedBy = "scaleIoSdcVolume", cascade = CascadeType.ALL)
    private StoragePool storagePool;

    @Column(name = "STORAGE_POOL_ID")
    private String storagePoolId;

    @Column(name = "VOLUME_TYPE")
    private String volumeType;

    @Column(name = "VTREE_ID")
    private String vtreeId;

    @ManyToMany(mappedBy = "scaleIOSDCVolumes")
    private Set<ScaleIOSDC> scaleIOSDCList = new HashSet<>();

    public ScaleIOSDCVolume()
    {
        // Default Constructor
    }

    public ScaleIOSDCVolume(final String id, final String name, final Long sizeInKb, final String storagePoolId, final String volumeType,
            final String vtreeId)
    {
        this.id = id;
        this.name = name;
        this.sizeInKb = sizeInKb;
        this.storagePoolId = storagePoolId;
        this.volumeType = volumeType;
        this.vtreeId = vtreeId;
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

    public Long getSizeInKb()
    {
        return sizeInKb;
    }

    public void setSizeInKb(final Long sizeInKb)
    {
        this.sizeInKb = sizeInKb;
    }

    public StoragePool getStoragePool()
    {
        return storagePool;
    }

    public void setStoragePool(final StoragePool storagePool)
    {
        this.storagePool = storagePool;
    }

    public String getStoragePoolId()
    {
        return storagePoolId;
    }

    public void setStoragePoolId(final String storagePoolId)
    {
        this.storagePoolId = storagePoolId;
    }

    public String getVolumeType()
    {
        return volumeType;
    }

    public void setVolumeType(final String volumeType)
    {
        this.volumeType = volumeType;
    }

    public String getVtreeId()
    {
        return vtreeId;
    }

    public void setVtreeId(final String vtreeId)
    {
        this.vtreeId = vtreeId;
    }

    public Set<ScaleIOSDC> getScaleIOSDCList()
    {
        return scaleIOSDCList;
    }

    public void setScaleIOSDCList(final HashSet<ScaleIOSDC> scaleIOSDCList)
    {
        this.scaleIOSDCList = scaleIOSDCList;
    }

    @Override
    public String toString()
    {
        return "ScaleIOSDCVolume{" + "uuid=" + uuid + ", id='" + id + '\'' + ", name='" + name + '\'' + ", sizeInKb=" + sizeInKb
                + ", storagePool=" + storagePool + ", storagePoolId='" + storagePoolId + '\'' + ", volumeType='" + volumeType + '\''
                + ", vtreeId='" + vtreeId + '\'' + '}';
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

        final ScaleIOSDCVolume that = (ScaleIOSDCVolume) o;

        return new EqualsBuilder().append(uuid, that.uuid).append(id, that.id).append(name, that.name).append(sizeInKb, that.sizeInKb)
                .append(storagePool, that.storagePool).append(storagePoolId, that.storagePoolId).append(volumeType, that.volumeType)
                .append(vtreeId, that.vtreeId).isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37).append(uuid).append(id).append(name).append(sizeInKb).append(storagePool).append(storagePoolId)
                .append(volumeType).append(vtreeId).toHashCode();
    }

    public void addScaleIOSDC(final ScaleIOSDC scaleIOSDC)
    {
        scaleIOSDCList.add(scaleIOSDC);
    }
}
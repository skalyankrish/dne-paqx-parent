/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.domain.scaleio;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */
@Entity
@Table(name = "SCALEIO_STORAGE_POOL")
public class ScaleIOStoragePool
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "STORAGE_POOL_UUID", unique = true, nullable = false)
    private Long uuid;

    @Column(name = "STORAGE_POOL_ID")
    private String id;

    @Column(name = "STORAGE_POOL_NAME")
    private String name;

    @Column(name = "STORAGE_POOL_CAPACITY_AVILABLE")
    private Integer capacityAvailableForVolumeAllocationInKb;

    @Column(name = "STORAGE_POOL_MAX_CAPACITY_AVILABLE")
    private Integer maxCapacityInKb;

    @Column(name = "STORAGE_POOL_NUM_VOLUMES")
    private Integer numOfVolumes;

    @Column(name = "RF_CACHE")
    private boolean useRfcache;

    @Column(name = "RM_CACHE")
    private boolean useRmcache;

    @Column(name = "ZERO_PADDING_ENABLED")
    private boolean zeroPaddingEnabled;

    @ManyToOne(cascade = CascadeType.ALL)
    private ScaleIOProtectionDomain protectionDomain;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "storagePool", orphanRemoval = true)
    private List<ScaleIODevice> devices = new ArrayList<>();

    public ScaleIOStoragePool()
    {
    }

    public ScaleIOStoragePool(final String id, final String storagePoolName, final int capacityAvailableForVolumeAllocationInKb,
            final int maxCapacityInKb, final int numOfVolumes, boolean useRfcache, boolean useRmcache, boolean zeroPaddingEnabled)
    {
        this.id = id;
        this.name = storagePoolName;
        this.capacityAvailableForVolumeAllocationInKb = capacityAvailableForVolumeAllocationInKb;
        this.maxCapacityInKb = maxCapacityInKb;
        this.numOfVolumes = numOfVolumes;
        this.useRfcache = useRfcache;
        this.useRmcache = useRmcache;
        this.zeroPaddingEnabled = zeroPaddingEnabled;
    }

    public void setProtectionDomain(final ScaleIOProtectionDomain protectionDomain)
    {
        this.protectionDomain = protectionDomain;
    }

    public void addDevice(final ScaleIODevice device)
    {
        this.devices.add(device);
    }

    public String getId()
    {
        return id;
    }

    public Long getUuid()
    {
        return uuid;
    }

    public void setUuid(final Long uuid)
    {
        this.uuid = uuid;
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

    public Integer getCapacityAvailableForVolumeAllocationInKb()
    {
        return capacityAvailableForVolumeAllocationInKb;
    }

    public void setCapacityAvailableForVolumeAllocationInKb(final Integer capacityAvailableForVolumeAllocationInKb)
    {
        this.capacityAvailableForVolumeAllocationInKb = capacityAvailableForVolumeAllocationInKb;
    }

    public Integer getMaxCapacityInKb()
    {
        return maxCapacityInKb;
    }

    public void setMaxCapacityInKb(final Integer maxCapacityInKb)
    {
        this.maxCapacityInKb = maxCapacityInKb;
    }

    public Integer getNumOfVolumes()
    {
        return numOfVolumes;
    }

    public void setNumOfVolumes(final Integer numOfVolumes)
    {
        this.numOfVolumes = numOfVolumes;
    }

    public ScaleIOProtectionDomain getProtectionDomain()
    {
        return protectionDomain;
    }

    public List<ScaleIODevice> getDevices()
    {
        return devices;
    }

    public void setDevices(final List<ScaleIODevice> devices)
    {
        this.devices = devices;
    }

    public boolean isUseRfcache()
    {
        return useRfcache;
    }

    public void setUseRfcache(final boolean useRfcache)
    {
        this.useRfcache = useRfcache;
    }

    public boolean isUseRmcache()
    {
        return useRmcache;
    }

    public void setUseRmcache(final boolean useRmcache)
    {
        this.useRmcache = useRmcache;
    }

    public boolean isZeroPaddingEnabled()
    {
        return zeroPaddingEnabled;
    }

    public void setZeroPaddingEnabled(final boolean zeroPaddingEnabled)
    {
        this.zeroPaddingEnabled = zeroPaddingEnabled;
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder().append(uuid).append(id).append(name).append(capacityAvailableForVolumeAllocationInKb)
                .append(maxCapacityInKb).append(numOfVolumes).append(devices).append(useRfcache).append(useRmcache)
                .append(zeroPaddingEnabled).toHashCode();
    }

    /**
     * For the sake of non-circular checks "equals" checks for relationship attributes must be checked
     * on only one side of the relationship. In the case of OneToMany relationships it will be done on
     * the "One" side (the one holding the List)
     * <p>
     * On the "Many" Side we'll ignore the attribute when doing the equals comparison as a way to avoid
     * a circular reference starting and endless cycle.
     *
     * @param other the object to compare to
     * @return true if their attributes are equal
     */
    @Override
    public boolean equals(Object other)
    {
        if (other == this)
        {
            return true;
        }
        if (!(other instanceof ScaleIOStoragePool))
        {
            return false;
        }
        //Toot stands for "That Object Over There"
        ScaleIOStoragePool toot = ((ScaleIOStoragePool) other);
        return new EqualsBuilder().append(uuid, toot.uuid).append(id, toot.id).append(name, toot.name)
                .append(capacityAvailableForVolumeAllocationInKb, toot.capacityAvailableForVolumeAllocationInKb)
                .append(maxCapacityInKb, toot.maxCapacityInKb).append(numOfVolumes, toot.numOfVolumes).append(devices, toot.devices)
                .append(useRfcache, toot.useRfcache).append(useRmcache, toot.useRmcache).append(zeroPaddingEnabled, toot.zeroPaddingEnabled)
                .isEquals();
    }
}

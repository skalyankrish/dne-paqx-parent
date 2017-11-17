/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.domain.scaleio;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */
@Entity
@Table(name = "SCALE_IO_SDC")
public class ScaleIOSDC
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SDC_UUID", unique = true, nullable = false)
    private Long uuid;

    @Column(name = "SDC_ID", unique = true, nullable = false)
    private String id;

    @Column(name = "SDC_NAME")
    private String name;

    @Column(name = "SDC_IP")
    private String sdcIp;

    @Column(name = "SDC_GUID")
    private String sdcGuid;

    @Column(name = "SDC_MDM_CONNECTION_STATE")
    private String mdmConnectionState;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "SCALEIO_SDC_VOLUMES", joinColumns = {@JoinColumn(name = "SDC_UUID")}, inverseJoinColumns = {
            @JoinColumn(name = "SDC_VOLUME_UUID")})
    private Set<ScaleIOSDCVolume> scaleIOSDCVolumes = new HashSet<>();

    @ManyToOne(cascade = CascadeType.ALL)
    private ScaleIOData scaleIOData;

    public ScaleIOSDC()
    {
    }

    public ScaleIOSDC(final String id, final String name, final String sdcIp, final String sdcGuid, final String mdmConnectorState)
    {
        this.id = id;
        this.name = name;
        this.sdcIp = sdcIp;
        this.sdcGuid = sdcGuid;
        this.mdmConnectionState = mdmConnectorState;
    }

    public String getName() { return name; }

    public ScaleIOData getScaleIOData()
    {
        return scaleIOData;
    }

    public void setScaleIOData(final ScaleIOData scaleIOData)
    {
        this.scaleIOData = scaleIOData;
    }

    public void addSDCVolume(final ScaleIOSDCVolume scaleIOSDCVolume)
    {
        scaleIOSDCVolumes.add(scaleIOSDCVolume);
    }

    public Set<ScaleIOSDCVolume> getScaleIOSDCVolumes()
    {
        return scaleIOSDCVolumes;
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder().append(uuid).append(id).append(name).append(sdcIp).append(sdcGuid).append(mdmConnectionState)
                .toHashCode();
    }

    /**
     * For the sake of non-circular checks "equals" checks for relationship attributes must be checked
     * on only one side of the relationship. In the case of OneToMany relationships it will be done on
     * the "One" side (the one holding the List)
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
        if (!(other instanceof ScaleIOSDC))
        {
            return false;
        }
        //Toot stands for "That Object Over There"
        ScaleIOSDC toot = ((ScaleIOSDC) other);
        return new EqualsBuilder().append(uuid, toot.uuid).append(id, toot.id).append(name, toot.name).append(sdcIp, toot.sdcIp)
                .append(sdcGuid, toot.sdcGuid).append(mdmConnectionState, toot.mdmConnectionState).isEquals();
    }

    @Override
    public String toString()
    {
        return "ScaleIOSDC{" + "uuid=" + uuid + ", id='" + id + '\'' + ", name='" + name + '\'' + ", sdcIp='" + sdcIp + '\'' + ", sdcGuid='"
                + sdcGuid + '\'' + ", mdmConnectionState='" + mdmConnectionState + '\'' + '}';
    }
}

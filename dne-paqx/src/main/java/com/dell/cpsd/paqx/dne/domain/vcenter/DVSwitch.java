/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.domain.vcenter;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "DVSWITCH")
public class DVSwitch
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UUID", unique = true, nullable = false)
    private Long uuid;

    @Column(name = "DVSWITCH_ID", unique = true, nullable = false)
    private String id;

    @Column(name = "DVSWITCH_NAME")
    private String name;

    @Column(name = "ALLOW_PROMISCUOUS")
    private boolean allowPromiscuous;

    @ManyToOne(cascade = CascadeType.ALL)
    private DataCenter dataCenter;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dvSwitch", orphanRemoval = true)
    private List<PhysicalNicDVSConnection> physicalNicDVSConnectionList = new ArrayList<>();

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dvSwitch", orphanRemoval = true)
    private List<PortGroup> portGroupList = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "HOST_MEMBER_ID", joinColumns = @JoinColumn(name = "UUID"))
    private List<String> hostMemberIds = new ArrayList<>();

    public DVSwitch()
    {
    }

    public DVSwitch(String id, String name, boolean allowPromiscuous)
    {
        this.id = id;
        this.name = name;
        this.allowPromiscuous = allowPromiscuous;
    }

    public Long getUuid()
    {
        return uuid;
    }

    public void setUuid(Long uuid)
    {
        this.uuid = uuid;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean isAllowPromiscuous()
    {
        return allowPromiscuous;
    }

    public void setAllowPromiscuous(boolean allowPromiscuous)
    {
        this.allowPromiscuous = allowPromiscuous;
    }

    public DataCenter getDataCenter()
    {
        return dataCenter;
    }

    public void setDataCenter(DataCenter dataCenter)
    {
        this.dataCenter = dataCenter;
    }

    public List<PhysicalNicDVSConnection> getPhysicalNicDVSConnectionList()
    {
        return physicalNicDVSConnectionList;
    }

    public void setPhysicalNicDVSConnectionList(List<PhysicalNicDVSConnection> physicalNicDVSConnectionList)
    {
        this.physicalNicDVSConnectionList = physicalNicDVSConnectionList;
    }

    public List<PortGroup> getPortGroupList()
    {
        return portGroupList;
    }

    public void setPortGroupList(final List<PortGroup> portGroupList)
    {
        this.portGroupList = portGroupList;
    }

    public List<String> getHostMemberIds()
    {
        return hostMemberIds;
    }

    public void setHostMemberIds(final List<String> hostMemberIds)
    {
        this.hostMemberIds = hostMemberIds;
    }

    public void addHostMember(final String hostMember)
    {
        hostMemberIds.add(hostMember);
    }
}
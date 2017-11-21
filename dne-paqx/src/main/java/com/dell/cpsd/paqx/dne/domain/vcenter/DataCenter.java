/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.domain.vcenter;

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

@Entity
@Table(name = "DATACENTER")
public class DataCenter
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UUID", unique = true, nullable = false)
    private Long uuid;

    @Column(name = "DATACENTER_ID", unique = true, nullable = false)
    private String id;

    @Column(name = "DATACENTER_NAME")
    private String name;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dataCenter", orphanRemoval = true)
    private List<DVSwitch> dvSwitchList = new ArrayList<>();

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dataCenter", orphanRemoval = true)
    private List<Datastore> datastoreList = new ArrayList<>();

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dataCenter", orphanRemoval = true)
    private List<Cluster> clusterList = new ArrayList<>();

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dataCenter", orphanRemoval = true)
    private List<Network> networkList = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.ALL)
    private VCenter vCenter;

    public DataCenter()
    {
    }

    public DataCenter(String id, String name)
    {
        this.id = id;
        this.name = name;
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

    public List<DVSwitch> getDvSwitchList()
    {
        return dvSwitchList;
    }

    public void addDvSwitch(DVSwitch dvSwitch)
    {
        this.dvSwitchList.add(dvSwitch);
    }

    public void setDvSwitchList(List<DVSwitch> dvSwitchList)
    {
        this.dvSwitchList = dvSwitchList;
    }

    public List<Datastore> getDatastoreList()
    {
        return datastoreList;
    }

    public void addDatastore(Datastore datastore)
    {
        this.datastoreList.add(datastore);
    }

    public void setDatastoreList(List<Datastore> datastoreList)
    {
        this.datastoreList = datastoreList;
    }

    public List<Cluster> getClusterList()
    {
        return clusterList;
    }

    public void addCluster(Cluster cluster)
    {
        this.clusterList.add(cluster);
    }

    public void setClusterList(List<Cluster> clusterList)
    {
        this.clusterList = clusterList;
    }

    public List<Network> getNetworkList()
    {
        return networkList;
    }

    public void setNetworkList(final List<Network> networkList)
    {
        this.networkList = networkList;
    }

    public VCenter getvCenter()
    {
        return vCenter;
    }

    public void setvCenter(VCenter vCenter)
    {
        this.vCenter = vCenter;
    }
}
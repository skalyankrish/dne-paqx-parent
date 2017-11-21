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
@Table(name = "DATASTORE")
public class Datastore
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UUID", unique = true, nullable = false)
    private Long uuid;

    @Column(name = "DATASTORE_ID", unique = true, nullable = false)
    private String id;

    @Column(name = "DATASTORE_NAME")
    private String name;

    @Column(name = "DATASTORE_TYPE")
    private String type;

    @Column(name = "DATASTORE_URL")
    private String url;

    @ManyToOne(cascade = CascadeType.ALL)
    private DataCenter dataCenter;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "datastore", orphanRemoval = true)
    private List<VirtualMachine> virtualMachineList = new ArrayList<>();

    public Datastore()
    {
    }

    public Datastore(String id, String name, String type, String url)
    {
        this.id = id;
        this.name = name;
        this.type = type;
        this.url = url;
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

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public DataCenter getDataCenter()
    {
        return dataCenter;
    }

    public void setDataCenter(DataCenter dataCenter)
    {
        this.dataCenter = dataCenter;
    }

    public List<VirtualMachine> getVirtualMachineList()
    {
        return virtualMachineList;
    }

    public void setVirtualMachineList(List<VirtualMachine> virtualMachineList)
    {
        this.virtualMachineList = virtualMachineList;
    }
}

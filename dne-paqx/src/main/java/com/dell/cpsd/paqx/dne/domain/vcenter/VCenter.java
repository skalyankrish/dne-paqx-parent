/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.domain.vcenter;

import com.dell.cpsd.paqx.dne.domain.DneJob;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "VCENTER")
public class VCenter
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UUID", unique = true, nullable = false)
    private Long uuid;

    @Column(name = "VCENTER_ID", unique = true, nullable = false)
    private String id;

    @Column(name = "VCENTER_NAME")
    private String name;

    @OneToOne(optional = false, mappedBy = "vcenter", cascade = CascadeType.ALL)
    private DneJob job;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vCenter", orphanRemoval = true)
    private List<DataCenter> dataCenterList = new ArrayList<>();

    public VCenter()
    {

    }

    public VCenter(String id, String name)
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

    public List<DataCenter> getDataCenterList()
    {
        return dataCenterList;
    }

    public void setDataCenterList(List<DataCenter> dataCenterList)
    {
        this.dataCenterList = dataCenterList;
    }

    public void addDatacenter(DataCenter dataCenter)
    {
        this.dataCenterList.add(dataCenter);
    }

    public DneJob getJob()
    {
        return job;
    }

    public void setJob(final DneJob job)
    {
        this.job = job;
    }
}

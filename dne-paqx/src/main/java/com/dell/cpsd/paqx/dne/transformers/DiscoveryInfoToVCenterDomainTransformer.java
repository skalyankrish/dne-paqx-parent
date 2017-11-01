/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 **/

package com.dell.cpsd.paqx.dne.transformers;

import com.dell.cpsd.paqx.dne.domain.vcenter.*;
import com.dell.cpsd.virtualization.capabilities.api.DiscoveryResponseInfoMessage;
import com.dell.cpsd.virtualization.capabilities.api.DistributedVirtualSwicthPortConnection;
import com.dell.cpsd.virtualization.capabilities.api.DvSwitch;
import com.dell.cpsd.virtualization.capabilities.api.GuestNicInfo;
import com.dell.cpsd.virtualization.capabilities.api.HostPciDevice;
import com.dell.cpsd.virtualization.capabilities.api.HostScsiDisk;
import com.dell.cpsd.virtualization.capabilities.api.HostSystem;
import com.dell.cpsd.virtualization.capabilities.api.HostVirtualNic;
import com.dell.cpsd.virtualization.capabilities.api.HostVirtualSwitch;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class DiscoveryInfoToVCenterDomainTransformer
{

    private static final String ONE_RACK_UNIT = "1U1N";
    private static final String TWO_RACK_UNIT = "2U1N";

    public VCenter transform(final DiscoveryResponseInfoMessage discoveryResponseInfoMessage)
    {
        if (discoveryResponseInfoMessage == null)
        {
            return null;
        }

        // Create the VCenter object
        // TODO: Add vcenter properties
        final VCenter returnVal = new VCenter("change-me", "change-me");

        // Transform and link datacenters
        final List<DataCenter> datacenters = discoveryResponseInfoMessage.getDatacenters().stream().filter(Objects::nonNull)
                .map(datacenter -> transformDatacenter(datacenter, returnVal)).collect(Collectors.toList());
        returnVal.setDataCenterList(datacenters);

        return returnVal;
    }

    private DataCenter transformDatacenter(com.dell.cpsd.virtualization.capabilities.api.Datacenter datacenter, VCenter vCenter)
    {
        final DataCenter returnVal = new DataCenter();
        returnVal.setId(datacenter.getId());
        returnVal.setName(datacenter.getName());

        final List<VirtualMachine> virtualMachines = new ArrayList<>();

        Optional.ofNullable(datacenter.getVms()).ifPresent(virtualMachineMap -> {
            if (!virtualMachineMap.values().isEmpty())
            {
                // Transform vms
                virtualMachines.addAll(virtualMachineMap.values().stream().filter(Objects::nonNull).map(this::transformVirtualMachine)
                        .collect(Collectors.toList()));
            }
        });

        Optional.ofNullable(datacenter.getDatastores()).ifPresent(datastoreMap -> {
            if (!datastoreMap.values().isEmpty())
            {
                // Transform and link datastores
                final List<Datastore> datastores = datastoreMap.values().stream().filter(Objects::nonNull)
                        .map(datastore -> transformDatastore(datastore, returnVal, virtualMachines)).collect(Collectors.toList());

                returnVal.setDatastoreList(datastores);
            }
        });

        Optional.ofNullable(datacenter.getDvSwitches()).ifPresent(dvSwitchMap -> {
            if (!dvSwitchMap.values().isEmpty())
            {
                // Transform and link dvswitches
                final List<com.dell.cpsd.virtualization.capabilities.api.PortGroup> portGroups = datacenter.getPortgroups().values()
                        .stream().filter(Objects::nonNull).collect(Collectors.toList());

                final List<DVSwitch> dvSwitches = dvSwitchMap.values().stream().filter(Objects::nonNull)
                        .map(dvSwitch -> transformDVSwitch(dvSwitch, returnVal, portGroups)).collect(Collectors.toList());

                returnVal.setDvSwitchList(dvSwitches);
            }
        });

        Optional.ofNullable(datacenter.getClusters()).ifPresent(clusterMap -> {
            if (!clusterMap.values().isEmpty())
            {
                // Transform and link clusters
                final List<Cluster> clusters = clusterMap.values().stream().filter(Objects::nonNull)
                        .map(cluster -> transformCluster(cluster, returnVal, virtualMachines)).collect(Collectors.toList());

                returnVal.setClusterList(clusters);
            }
        });

        Optional.ofNullable(datacenter.getNetworks()).ifPresent(networkMap -> {
            if (!networkMap.isEmpty()) // why not `networkMap.values().isEmpty()` here as with all the others???
            {
                // Transform and link networks
                final List<Network> networks = networkMap.values().stream().filter(Objects::nonNull)
                        .map(network -> transformNetwork(network, returnVal)).collect(Collectors.toList());

                returnVal.setNetworkList(networks);
            }
        });

        // TODO: Link Host Pnics to Dvswitches
        // TODO: Link Datastores to Hosts
        // TODO: Link Virtual Nics to DVPortgroups
        // TODO: Link VMGuestNetworks to DVPortgroups

        // FK link
        returnVal.setvCenter(vCenter);

        return returnVal;
    }

    private Datastore transformDatastore(com.dell.cpsd.virtualization.capabilities.api.Datastore datastore, DataCenter datacenter,
            List<VirtualMachine> virtualMachines)
    {
        final Datastore returnVal = new Datastore();
        returnVal.setId(datastore.getId());
        returnVal.setName(datastore.getName());
        returnVal.setType(datastore.getDatastoreSummary().getType());
        returnVal.setUrl(datastore.getDatastoreSummary().getUrl());

        // One to Many Link to VMs
        final List<VirtualMachine> vmsOnDatastore = virtualMachines.stream()
                .filter(virtualMachine -> datastore.getVmIds().contains(virtualMachine.getId())).collect(Collectors.toList());
        vmsOnDatastore.forEach(virtualMachine -> virtualMachine.setDatastore(returnVal));
        returnVal.setVirtualMachineList(vmsOnDatastore);

        // FK link
        returnVal.setDataCenter(datacenter);

        return returnVal;
    }

    private DVSwitch transformDVSwitch(DvSwitch dvSwitch, DataCenter datacenter,
            List<com.dell.cpsd.virtualization.capabilities.api.PortGroup> portGroups)
    {
        final DVSwitch returnVal = new DVSwitch();
        returnVal.setId(dvSwitch.getId());
        returnVal.setName(dvSwitch.getName());
        returnVal.setAllowPromiscuous(dvSwitch.getVMwareDVSConfigInfo().getDVPortSetting().getDVSSecurityPolicy().getAllowPromicuous());

        dvSwitch.getVMwareDVSConfigInfo().getHostMembers().stream().filter(Objects::nonNull).forEach(
                distributedVirtualSwitchHostMember -> returnVal
                        .addHostMember(distributedVirtualSwitchHostMember.getDistributedVirtualSwitchHostMemberConfigInfo().getHostId()));

        final List<com.dell.cpsd.virtualization.capabilities.api.PortGroup> dvsPortGroups = portGroups.stream()
                .filter(portGroup -> dvSwitch.getPortGroupids().contains(portGroup.getId())).collect(Collectors.toList());

        // Transform and link portgroups to dvs
        final List<PortGroup> domainPortGroups = dvsPortGroups.stream().filter(Objects::nonNull)
                .map(portGroup -> transformPortgroup(portGroup, returnVal)).collect(Collectors.toList());

        // Link dvs to portgroups
        returnVal.setPortGroupList(domainPortGroups);

        // FK link
        returnVal.setDataCenter(datacenter);

        return returnVal;
    }

    private PortGroup transformPortgroup(com.dell.cpsd.virtualization.capabilities.api.PortGroup portGroup, DVSwitch dvSwitch)
    {
        final PortGroup returnVal = new PortGroup();
        returnVal.setId(portGroup.getId());
        returnVal.setName(portGroup.getName());
        returnVal.setAllowPromiscuous(portGroup.getDVPortgroupConfigInfo().getDVPortSetting().getDVSSecurityPolicy().getAllowPromicuous());
        returnVal.setVlanId(portGroup.getDVPortgroupConfigInfo().getDVPortSetting().getVlanId());
        returnVal.setDvSwitch(dvSwitch);

        return returnVal;
    }

    private Network transformNetwork(com.dell.cpsd.virtualization.capabilities.api.Network network, DataCenter datacenter)
    {
        final Network returnVal = new Network();
        returnVal.setId(network.getId());
        returnVal.setName(network.getName());
        returnVal.setDataCenter(datacenter);

        return returnVal;
    }

    private Cluster transformCluster(com.dell.cpsd.virtualization.capabilities.api.Cluster cluster, DataCenter datacenter,
            List<VirtualMachine> virtualMachines)
    {
        final Cluster returnVal = new Cluster();
        returnVal.setId(cluster.getId());
        returnVal.setName(cluster.getName());

        // Transform and link hosts
        if (cluster.getHosts() != null)
        {
            if (!cluster.getHosts().values().isEmpty())
            {
                final List<Host> hosts = cluster.getHosts().values().stream().filter(Objects::nonNull)
                        .map(hostSystem -> transformHost(hostSystem, returnVal, virtualMachines)).collect(Collectors.toList());
                returnVal.setHostList(hosts);
            }
        }

        // FK link
        returnVal.setDataCenter(datacenter);

        return returnVal;
    }

    private Host transformHost(HostSystem hostSystem, Cluster cluster, List<VirtualMachine> virtualMachines)
    {
        final Host returnVal = new Host();
        returnVal.setId(hostSystem.getId());
        returnVal.setName(hostSystem.getName());

        returnVal.setPowerState(hostSystem.getPowerState());
        returnVal.setConnectionState(hostSystem.getConnectionState());
        returnVal.setMaintenanceMode(hostSystem.getMaintenanceMode());

        // Transform and link HostDnsConfig
        Optional.ofNullable(hostSystem.getHostConfigInfo()).ifPresent(hostConfigInfo -> {

            Optional.ofNullable(hostConfigInfo.getHostNetworkInfo()).ifPresent(hostNetworkInfo -> {

                final HostDnsConfig hostDnsConfig = transformHostDnsConfig(hostNetworkInfo.getHostDnsConfig(), returnVal);
                returnVal.setHostDnsConfig(hostDnsConfig);

                // Transform and link HostIpRouteConfig
                final HostIpRouteConfig hostIpRouteConfig = transformHostIpRouteConfig(hostNetworkInfo.getHostIpRouteConfig(), returnVal);
                returnVal.setHostIpRouteConfig(hostIpRouteConfig);

                Optional.ofNullable(hostNetworkInfo.getVswitchs()).ifPresent(hostVirtualSwitches -> {

                    // Transform and link HostVirtualSwitch
                    final List<VSwitch> vSwitches = hostVirtualSwitches.stream().filter(Objects::nonNull)
                            .map(hostVirtualSwitch -> transformVSwitch(hostVirtualSwitch, returnVal)).collect(Collectors.toList());
                    returnVal.setvSwitchList(vSwitches);
                });

                Optional.ofNullable(hostNetworkInfo.getVnics()).ifPresent(hostVirtualNics -> {

                    // Transform and link VirtualNics
                    final List<VirtualNic> virtualNics = hostVirtualNics.stream().filter(Objects::nonNull)
                            .map(hostVirtualNic -> transformHostVirtualNic(hostVirtualNic, returnVal)).collect(Collectors.toList());
                    returnVal.setVirtualNicList(virtualNics);
                });

                Optional.ofNullable(hostNetworkInfo.getPnics()).ifPresent(hostPhysicalNics -> {

                    // Transform and link VirtualNics
                    final List<PhysicalNic> physicalNics = hostPhysicalNics.stream().filter(Objects::nonNull)
                            .map(physicalNic -> transformHostPhysicalNic(physicalNic, returnVal)).collect(Collectors.toList());
                    returnVal.setPhysicalNicList(physicalNics);
                });
            });

            Optional.ofNullable(hostConfigInfo.getHostDateTimeInfo()).ifPresent(hostDateTimeInfo -> returnVal.setNtpServers(hostDateTimeInfo.getNtpServers()));

            Optional.ofNullable(hostConfigInfo.getHostStorageDeviceInfo()).ifPresent(hostStorageDeviceInfo -> {

                final List<HostStorageDevice> hostStorageDeviceList = hostStorageDeviceInfo.getScsiLun().stream().map(hostScsiDisk -> transformHostStorageDevice(hostScsiDisk, returnVal))
                        .collect(Collectors.toList());
                returnVal.setHostStorageDeviceList(hostStorageDeviceList);
            });
            Optional.ofNullable(hostConfigInfo.getHostDateTimeInfo()).ifPresent(hostDateTimeInfo -> returnVal.setNtpServers(hostDateTimeInfo.getNtpServers()));
        });

        Optional.ofNullable(hostSystem.getHostHardwareInfo()).ifPresent(hostHardwareInfo -> {

            Optional.ofNullable(hostHardwareInfo.getPciDevice()).ifPresent(hostPciDevices -> {
                // Transform and link VirtualNics
                hostPciDevices.stream().filter(Objects::nonNull)
                        .forEach(pciDevice -> returnVal.getPciDevices().add(transformHostPciDevice(pciDevice, returnVal)));
            });

            Optional.ofNullable(hostHardwareInfo.getHostSystemInfo()).ifPresent(hostSystemInfo -> Optional.ofNullable(hostSystemInfo.getHostSystemIdentificationInfo()).ifPresent(hostSystemIdentificationInfo -> returnVal.setServiceTag(hostSystemIdentificationInfo.getServiceTag())));
        });
        
        String model = hostSystem.getHostHardwareInfo().getHostSystemInfo().getModel();
        if (model != null){
            if (model.contains("R6")){
                returnVal.setType(ONE_RACK_UNIT);
            }
            else if (model.contains("R7")){
                returnVal.setType(TWO_RACK_UNIT);
            } else {
                //Set to empty string
                returnVal.setType("");
            }
        }

    // One to Many Link to VMs
        final List<VirtualMachine> vmsOnHost = virtualMachines.stream()
                .filter(virtualMachine -> hostSystem.getVmIds().contains(virtualMachine.getId())).collect(Collectors.toList());
        vmsOnHost.forEach(virtualMachine -> virtualMachine.setHost(returnVal));
        returnVal.setVirtualMachineList(vmsOnHost);

        // FK Link
        returnVal.setCluster(cluster);

        return returnVal;
    }

    private HostStorageDevice transformHostStorageDevice(final HostScsiDisk hostScsiDisk, final Host host)
    {
        HostStorageDevice hostStorageDevice = new HostStorageDevice();
        hostStorageDevice.setDisplayName(hostScsiDisk.getDisplayName());
        hostStorageDevice.setSsd(hostScsiDisk.getSsd());
        hostStorageDevice.setSerialNumber(hostScsiDisk.getSerialNumber());
        hostStorageDevice.setCanonicalName(hostScsiDisk.getCanonicalName());
        hostStorageDevice.setHost(host);

        return hostStorageDevice;
    }

    private PciDevice transformHostPciDevice(final HostPciDevice pciDevice, final Host host)
    {
        PciDevice transformedPciDevice = new PciDevice();
        transformedPciDevice.setId(pciDevice.getId());
        transformedPciDevice.setDeviceId(pciDevice.getDeviceId());
        transformedPciDevice.setDeviceName(pciDevice.getDeviceName());
        transformedPciDevice.setVendorId(pciDevice.getVendorId());
        transformedPciDevice.setVendorName(pciDevice.getVendorName());
        transformedPciDevice.setSubVendorId(pciDevice.getSubVendorId());
        transformedPciDevice.setHost(host);

        return transformedPciDevice;
    }

    private HostDnsConfig transformHostDnsConfig(com.dell.cpsd.virtualization.capabilities.api.HostDnsConfig hostDnsConfig, Host host)
    {
        if (hostDnsConfig == null)
        {
            return null;
        }

        final HostDnsConfig returnVal = new HostDnsConfig();
        returnVal.setDhcp(hostDnsConfig.getDhcp());
        returnVal.setDomainName(hostDnsConfig.getDomainName());
        returnVal.setHostname(hostDnsConfig.getHostName());
        returnVal.setSearchDomains(hostDnsConfig.getSearchDomains());
        returnVal.setDnsConfigIPs(hostDnsConfig.getIpAddresses());

        // FK link
        returnVal.setHost(host);

        return returnVal;
    }

    private HostIpRouteConfig transformHostIpRouteConfig(com.dell.cpsd.virtualization.capabilities.api.HostIpRouteConfig hostIpRouteConfig,
            Host host)
    {
        if (hostIpRouteConfig == null)
        {
            return null;
        }

        final HostIpRouteConfig returnVal = new HostIpRouteConfig();
        returnVal.setDefaultGateway(hostIpRouteConfig.getDefaultGateway());
        returnVal.setDefaultGatewayDevice(hostIpRouteConfig.getDefaultGatewayDevice());
        returnVal.setIpV6defaultGateway(hostIpRouteConfig.getIpV6DefaultGateway());
        returnVal.setIpV6defaultGatewayDevice(hostIpRouteConfig.getIpV6DefaultGatewayDevice());

        // FK link
        returnVal.setHost(host);

        return returnVal;
    }

    private VSwitch transformVSwitch(HostVirtualSwitch hostVirtualSwitch, Host host)
    {
        final VSwitch returnVal = new VSwitch();
        returnVal.setId(hostVirtualSwitch.getKey());
        returnVal.setName(hostVirtualSwitch.getName());
        returnVal.setAllowPromiscuous(
                hostVirtualSwitch.getHostVirtualSwitchSpec().getHostNetworkPolicy().getHostNetworkSecurityPolicy().getAllowPromiscuous());

        // FK link
        returnVal.setHost(host);

        return returnVal;
    }

    private VirtualNic transformHostVirtualNic(HostVirtualNic virtualNic, Host host)
    {
        final VirtualNic returnVal = new VirtualNic();
        returnVal.setDevice(virtualNic.getDevice());
        returnVal.setPort(virtualNic.getPort());
        returnVal.setPortGroup(virtualNic.getPortGroup());
        returnVal.setMac(virtualNic.getHostVirtualNicSpec().getMac());
        returnVal.setDhcp(virtualNic.getHostVirtualNicSpec().getHostIpConfig().getDhcp());
        returnVal.setIp(virtualNic.getHostVirtualNicSpec().getHostIpConfig().getIpAddress());
        returnVal.setSubnetMask(virtualNic.getHostVirtualNicSpec().getHostIpConfig().getSubnetMask());
        returnVal.setVirtualNicDVPortGroup(
                transformVirtualNicPortGroup(returnVal, virtualNic.getHostVirtualNicSpec().getDistributedVirtualSwicthPortConnection()));

        // FK link
        returnVal.setHost(host);

        return returnVal;
    }

    private VirtualNicDVPortGroup transformVirtualNicPortGroup(final VirtualNic virtualNic,
            final DistributedVirtualSwicthPortConnection distributedVirtualSwicthPortConnection)
    {
        if (distributedVirtualSwicthPortConnection == null)
        {
            return null;
        }

        final VirtualNicDVPortGroup virtualNicDVPortGroup = new VirtualNicDVPortGroup();
        virtualNicDVPortGroup.setPort(distributedVirtualSwicthPortConnection.getPortKey());
        virtualNicDVPortGroup.setPortGroupId(distributedVirtualSwicthPortConnection.getPortGroupId());
        virtualNicDVPortGroup.setVirtualNic(virtualNic);

        return virtualNicDVPortGroup;
    }

    private PhysicalNic transformHostPhysicalNic(com.dell.cpsd.virtualization.capabilities.api.PhysicalNic physicalNic, Host host)
    {
        final PhysicalNic returnVal = new PhysicalNic();
        returnVal.setDevice(physicalNic.getDevice());
        returnVal.setDriver(physicalNic.getDriver());
        returnVal.setPci(physicalNic.getPci());
        returnVal.setMac(physicalNic.getMac());

        // FK link
        returnVal.setHost(host);

        return returnVal;
    }

    private VirtualMachine transformVirtualMachine(com.dell.cpsd.virtualization.capabilities.api.VirtualMachine virtualMachine)
    {
        final VirtualMachine returnVal = new VirtualMachine();
        returnVal.setName(virtualMachine.getName());
        returnVal.setId(virtualMachine.getId());
        returnVal.setGuestHostname(virtualMachine.getGuestInfo().getHostName());
        returnVal.setGuestOS(virtualMachine.getGuestInfo().getGuestFullName());

        final List<VMNetwork> vmNetworks = virtualMachine.getGuestInfo().getNet().stream().filter(Objects::nonNull)
                .map(guestNicInfo -> transformVMNetwork(guestNicInfo, returnVal)).collect(Collectors.toList());
        returnVal.setVmNetworkList(vmNetworks);

        // No FK link to hosts and datastores at this level
        return returnVal;
    }

    private VMNetwork transformVMNetwork(GuestNicInfo guestNicInfo, VirtualMachine virtualMachine)
    {
        final VMNetwork returnVal = new VMNetwork();
        returnVal.setConnected(guestNicInfo.getConnected());
        returnVal.setMacAddress(guestNicInfo.getMacAddress());

        final List<VMIP> vmips = guestNicInfo.getIpAddresses().stream().filter(Objects::nonNull).map(s -> transformVMIP(s, returnVal))
                .collect(Collectors.toList());
        returnVal.setVmip(vmips);

        // FK link
        returnVal.setVirtualMachine(virtualMachine);

        return returnVal;
    }

    private VMIP transformVMIP(String ip, VMNetwork vmNetwork)
    {
        final VMIP returnVal = new VMIP(ip);

        // FK link
        returnVal.setVmNetwork(vmNetwork);

        return returnVal;
    }

}

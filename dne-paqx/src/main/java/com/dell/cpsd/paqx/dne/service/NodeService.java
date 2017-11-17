/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.service;

import com.dell.cpsd.SetObmSettingsRequestMessage;
import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.domain.node.DiscoveredNodeInfo;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOData;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOStoragePool;
import com.dell.cpsd.paqx.dne.domain.vcenter.Host;
import com.dell.cpsd.paqx.dne.domain.vcenter.HostStorageDevice;
import com.dell.cpsd.paqx.dne.service.model.BootDeviceIdracStatus;
import com.dell.cpsd.paqx.dne.service.model.ChangeIdracCredentialsResponse;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.paqx.dne.service.model.DiscoveredNode;
import com.dell.cpsd.paqx.dne.service.model.IdracInfo;
import com.dell.cpsd.paqx.dne.service.model.IdracNetworkSettingsRequest;
import com.dell.cpsd.paqx.dne.service.model.ObmSettingsResponse;
import com.dell.cpsd.service.common.client.exception.ServiceExecutionException;
import com.dell.cpsd.service.common.client.exception.ServiceTimeoutException;
import com.dell.cpsd.service.engineering.standards.Device;
import com.dell.cpsd.service.engineering.standards.EssValidateProtectionDomainsRequestMessage;
import com.dell.cpsd.service.engineering.standards.EssValidateProtectionDomainsResponseMessage;
import com.dell.cpsd.service.engineering.standards.EssValidateStoragePoolResponseMessage;
import com.dell.cpsd.storage.capabilities.api.AddHostToProtectionDomainRequestMessage;
import com.dell.cpsd.storage.capabilities.api.CreateProtectionDomainRequestMessage;
import com.dell.cpsd.storage.capabilities.api.CreateStoragePoolRequestMessage;
import com.dell.cpsd.storage.capabilities.api.CreateStoragePoolResponseMessage;
import com.dell.cpsd.storage.capabilities.api.SioSdcUpdatePerformanceProfileRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.AddEsxiHostVSphereLicenseRequest;
import com.dell.cpsd.virtualization.capabilities.api.AddHostToDvSwitchRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.ClusterInfo;
import com.dell.cpsd.virtualization.capabilities.api.ClusterOperationRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.ConfigureVmNetworkSettingsRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.DatastoreRenameRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.DeployVMFromTemplateRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.EnablePCIPassthroughRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.HostMaintenanceModeRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.HostPowerOperationRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.ListEsxiCredentialDetailsRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.RemoteCommandExecutionRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.SoftwareVIBConfigureRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.SoftwareVIBRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.UpdatePCIPassthruSVMRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.VCenterUpdateSoftwareAcceptanceRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.ValidateVcenterClusterResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.VmPowerOperationsRequestMessage;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.persistence.NoResultException;
import java.util.List;
import java.util.Map;

/**
 * Node service interface.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public interface NodeService
{
    /**
     * List the discovered nodes
     *
     * @return
     * @throws ServiceTimeoutException
     * @throws ServiceExecutionException
     */
    List<DiscoveredNode> listDiscoveredNodes() throws ServiceTimeoutException, ServiceExecutionException;

    /**
     * List the discovered nodes with node info
     *
     * @return
     * @throws ServiceTimeoutException
     * @throws ServiceExecutionException
     */
    List<DiscoveredNodeInfo> listDiscoveredNodeInfo() throws ServiceTimeoutException, ServiceExecutionException, JsonProcessingException;

    // before we have new landing page to list all discovered node, UI will call this api to get only first node.
    DiscoveredNodeInfo getFirstDiscoveredNodeInfo() throws ServiceTimeoutException, ServiceExecutionException, JsonProcessingException;

    /**
     * List the virtualisation clusters
     *
     * @return
     * @throws ServiceTimeoutException
     * @throws ServiceExecutionException
     */
    List<ClusterInfo> listClusters() throws ServiceTimeoutException, ServiceExecutionException;

    /**
     * List the virtualisation scaleio data
     *
     * @return
     */
    List<ScaleIOData> listScaleIOData();

    /**
     * List the rackhd node inventory
     *
     * @return Node Inventory data
     * @throws ServiceTimeoutException
     * @throws ServiceExecutionException
     */
    Object listNodeInventory(String symphonyUUID) throws ServiceTimeoutException, ServiceExecutionException;

    /**
     * List the validated cluster names
     *
     * @return
     * @throws ServiceTimeoutException
     * @throws ServiceExecutionException
     */
    ValidateVcenterClusterResponseMessage validateClusters(List<ClusterInfo> clusterInfoList)
            throws ServiceTimeoutException, ServiceExecutionException;

    /**
     * Validated Storage pool names
     *
     * @param scaleIOStoragePools
     *
     * @param newDevices
     * @return
     * @throws ServiceTimeoutException
     * @throws ServiceExecutionException
     */
    EssValidateStoragePoolResponseMessage validateStoragePools(List<ScaleIOStoragePool> scaleIOStoragePools, List<Device> newDevices,
            Map<String, Map<String, HostStorageDevice>> hostToStorageDeviceMap) throws ServiceTimeoutException, ServiceExecutionException;

    /**
     * Validate protection domains
     *
     * @param essValidateProtectionDomainsRequestMessage
     *
     * @return
     * @throws ServiceTimeoutException
     * @throws ServiceExecutionException
     */
    EssValidateProtectionDomainsResponseMessage validateProtectionDomains(EssValidateProtectionDomainsRequestMessage essValidateProtectionDomainsRequestMessage) throws ServiceTimeoutException, ServiceExecutionException;

    /**
     * Notify the Node Discovery Service that node allocation is complete
     *
     * @param elementIdentifier - The node identifier
     *
     * @return true if the node allocation completed successfully, false otherwise.
     * @throws ServiceTimeoutException
     * @throws ServiceExecutionException
     */
    boolean notifyNodeAllocationStatus(String elementIdentifier,String action) throws ServiceTimeoutException, ServiceExecutionException;

    /**
     * Configure the iDRAC network settings.
     *
     * @param idracNetworkSettingsRequest - The <code>IdracNetworkSettingsRequest</code> instance.
     *
     * @return
     */
    IdracInfo idracNetworkSettings(IdracNetworkSettingsRequest idracNetworkSettingsRequest);

    /**
     * Change Idrac credentials
     *
     * @param nodeId - The <code>ChangeIdracCredentialsRequest</code> instance.
     *
     * @return
     */
    ChangeIdracCredentialsResponse changeIdracCredentials(String nodeId);

    /**
     * Configure OBM settings.
     *
     * @param setObmSettingsRequestMessage - The <code>SetObmSettingsRequestMessage</code> instance.
     *
     * @return
     */
    ObmSettingsResponse obmSettingsResponse(SetObmSettingsRequestMessage setObmSettingsRequestMessage);

    /**
     *
     *
     * @param uuid - The request message UUID
     * @param ipAddress - The IP address of the iDRAC
     *
     * @return
     */
    BootDeviceIdracStatus configurePxeBoot(String uuid, String ipAddress);

    /**
     * Request a list of ScaleIO components.
     *
     * @return
     */
    boolean requestScaleIoComponents();

    /**
     * Request a list of VCenter components.
     *
     * @return
     */
    boolean requestVCenterComponents();

    /**
     * Request a discovery of ScaleIO data.
     *
     * @param componentEndpointIds - The ScaleIO <code>ComponentEndpointIds</code>
     * @param jobId - The job id
     *
     * @return
     */
    boolean requestDiscoverScaleIo(ComponentEndpointIds componentEndpointIds, String jobId);

    /**
     * Request a discovery of VCenter data.
     *
     * @param componentEndpointIds - The VCenter <code>ComponentEndpointIds</code>
     * @param jobId - The job id
     *
     * @return
     */
    boolean requestDiscoverVCenter(ComponentEndpointIds componentEndpointIds, String jobId);

    /**
     * Request a host to be added to VCenter.
     *
     * @param requestMessage - The <code>ClusterOperationRequestMessage</code> instance
     * @return
     */
    boolean requestAddHostToVCenter(ClusterOperationRequestMessage requestMessage);

    /**
     * Request a host to be added to ProtectionDomain.
     *
     * @param requestMessage - The <code>ClusterOperationRequestMessage</code> instance
     * @return
     */
    boolean requestAddHostToProtectionDomain(AddHostToProtectionDomainRequestMessage requestMessage);

    /**
     * Request installation of a ScaleIO SDC.
     *
     * @param requestMessage - The <code>SoftwareVIBRequestMessage</code> instance
     * @return
     */
    boolean requestInstallSoftwareVib(SoftwareVIBRequestMessage requestMessage);

    /**
     * Request a configuration of the ScaleIO SDC.
     *
     * @param requestMessage - The <code>SoftwareVIBConfigureRequestMessage</code> instance
     * @return
     */
    boolean requestConfigureScaleIoVib(SoftwareVIBConfigureRequestMessage requestMessage);

    /**
     * Request a host to be added to a virtual distributed switch.
     *
     * @param requestMessage - The <code>AddHostToDvSwitchRequestMessage</code> instance
     * @return
     */
    boolean requestAddHostToDvSwitch(AddHostToDvSwitchRequestMessage requestMessage);

    /**
     * Request the deployment of a ScaleIO SDS virtual machine.
     *
     * @param requestMessage - The <code>DeployVMFromTemplateRequestMessage</code> instance
     * @return
     */
    boolean requestDeployScaleIoVm(DeployVMFromTemplateRequestMessage requestMessage);

    /**
     * Request PCI pass-through for the ScaleIO SDS.
     *
     * @param requestMessage - The <code>EnablePCIPassthroughRequestMessage</code> instance
     * @return
     */
    boolean requestEnablePciPassThrough(EnablePCIPassthroughRequestMessage requestMessage);

    /**
     * Request a host reboot.
     *
     * @param requestMessage - The <code>HostPowerOperationRequestMessage</code>
     * @return
     */
    boolean requestHostReboot(HostPowerOperationRequestMessage requestMessage);

    /**
     * Update the PCI pass-through for the ScaleIO virtual machine.
     *
     * @param requestMessage - The <code>UpdatePCIPassthruSVMRequestMessage</code> instance
     * @return
     */
    boolean requestSetPciPassThrough(UpdatePCIPassthruSVMRequestMessage requestMessage);

    /**
     * Apply the ESXi license.
     *
     * @param requestMessage - The <code>AddEsxiHostVSphereLicenseRequest</code> instance
     * @return
     */
    boolean requestInstallEsxiLicense(AddEsxiHostVSphereLicenseRequest requestMessage);

    /**
     * List the ESXi host credentials.
     *
     * @param requestMessage - The <code>ListEsxiCredentialDetailsRequestMessage</code> instance
     * @return
     */
    ComponentEndpointIds listDefaultCredentials(ListEsxiCredentialDetailsRequestMessage requestMessage);

    /**
     * Request a host to enter or exit maintenance mode.
     *
     * @param requestMessage - The <code>HostMaintenanceModeRequestMessage</code> instance
     * @return
     */
    boolean requestHostMaintenanceMode(HostMaintenanceModeRequestMessage requestMessage);

    /**
     * Rename the datastore assigned to an ESXi host to a specified name.
     *
     * @param requestMessage - The <code>DatastoreRenameRequestMessage</code> instance
     * @return
     */
    String requestDatastoreRename(DatastoreRenameRequestMessage requestMessage);

    /**
     * Update the VCenter software acceptance level, e.g. partner, community, vmware accepted, vmware certified, etc.
     *
     * @param requestMessage - The <code>VCenterUpdateSoftwareAcceptanceRequestMessage</code> instance
     * @return
     */
    boolean requestUpdateSoftwareAcceptance(VCenterUpdateSoftwareAcceptanceRequestMessage requestMessage);

    /**
     * Request a host/vm power operation, e.g. power on or off or suspend.
     *
     * @param requestMessage - The <code>VmPowerOperationsRequestMessage</code> instance
     * @return
     */
    boolean requestVmPowerOperation(VmPowerOperationsRequestMessage requestMessage);

    /**
     * Request configuration of network settings for a vm.
     *
     * @param requestMessage - The <code>ConfigureVmNetworkSettingsRequestMessage</code> instance
     * @return
     */
    boolean requestConfigureVmNetworkSettings(ConfigureVmNetworkSettingsRequestMessage requestMessage);

    /**
     * @param requestMessage - The <code>RemoteCommandExecutionRequestMessage</code> instance
     * @return
     */
    boolean requestRemoteCommandExecution(RemoteCommandExecutionRequestMessage requestMessage);

    /**
     *
     * Method to retrive the node inventory data
     *
     * @param job
     * @return
     */
    String getNodeInventoryData(Job job);

    /**
     * Retrieves vCenter hosts from H2 database
     * @return List of <code>Host</code>
     * @throws NoResultException
     */
    List<Host> findVcenterHosts() throws NoResultException;

    /**
     * Converts vCenter host data to hostName:storageDeviceName,isSsd
     * @param hosts vCenter hosts
     * @return
     */
    Map<String, Map<String, HostStorageDevice>> getHostToStorageDeviceMap(List<Host> hosts);

    /**
     * @param requestMessage - The <code>SioSdcUpdatePerformanceProfileRequestMessage</code> instance
     * @return
     */
    boolean requestUpdateSdcPerformanceProfile(SioSdcUpdatePerformanceProfileRequestMessage requestMessage);

    /**
     * Creates new storage pool
     * @param requestMessage - The <code>CreateStoragePoolRequestMessage</code> instance
     * @return {@code: CreateStoragePoolResponseMessage} containing newly created storage pool id
     * @throws ServiceTimeoutException
     * @throws ServiceExecutionException
     */
    CreateStoragePoolResponseMessage createStoragePool(CreateStoragePoolRequestMessage requestMessage) throws ServiceTimeoutException, ServiceExecutionException;

    /**
     * Create storage pool based on the given attributes
     * @param poolName Storage pool name
     * @param poolId Storage pool id returned from scale io rest api
     * @param protectionDomainId protection domain id to which this storage pool should be created
     * @return Storage pool instance
     */
    ScaleIOStoragePool createStoragePool(String poolName, String poolId, String protectionDomainId);

    String createProtectionDomain(CreateProtectionDomainRequestMessage requestMessage)
            throws ServiceTimeoutException, ServiceExecutionException;

    /**
     * Retrieves the component endpoints based on component type.
     * @param componentType VCenter/ScaleIO
     * @return
     */
    ComponentEndpointIds getComponentEndpointIds(String componentType);

}

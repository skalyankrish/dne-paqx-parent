/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
package com.dell.cpsd.paqx.dne.service.delegates.utils;

public class DelegateConstants
{

    public static final String DISCOVERED_NODES = "discoveredNodes";
    public static final String DISCOVERED_NODE = "discoveredNode";

    public static final String NODE_DETAILS = "NodeDetails";
    public static final String NODE_DETAIL = "NodeDetail";
    public static final String VCENTER_CLUSTER_ID = "VCenterClusterId";
    public static final String VCENTER_CLUSTER_NAME = "VCenterClusterName";
    public static final String STORAGE_POOL = "StoragePool";
    public static final String ESXI_CREDENTIAL_DETAILS = "ESXiCredentialDetails";
    public static final String HOSTNAME = "Hostname";
    public static final String DATASTORE_NAME = "DatastoreName";
    public static final String HOST_PCI_DEVICE_ID = "HostPCIDeviceId";
    public static final String IOCTL_INI_GUI_STR = "IoctlIniGuidStr";

    //Message IDs
    public static final String CONFIGURE_BOOT_DEVICE_MESSAGE_ID = "configureBootDeviceResponseReceived";



    public static final String VCENTER_INFORMATION_NOT_FOUND = "VCenter-Information-Missing";
    public static final String INVENTORY_VCENTER_FAILED = "Inventory-VCenter-Failed";
    public static final String INVENTORY_SCALE_IO_FAILED = "Inventory-Scale-IO-Failed";
    public static final String SCALE_IO_INFORMATION_NOT_FOUND = "Scale-IO-Information-Missing";
    public static final String RETRIEVE_SCALE_IO_COMPONENTS_FAILED = "Retrieve-Scale-IO-Components-Failed";
    public static final String RETRIEVE_VCENTER_COMPONENTS_FAILED = "Retrieve-VCenter-Components-Failed";
    public static final String NO_DISCOVERED_NODES = "No-Discovered-Nodes";
    public static final String CONFIGURE_IP_ADDRESS_FAILED = "Configure_IP_Address_Failed";
    public static final String MISSING_CONFIGURE_IP_ADDRESS_DETAILS = "Missing_Configure_IP_Address_Details";
    public static final String PING_IP_ADDRESS_FAILED = "Ping_IP_Address_Failed";
    public static final String CONFIGURE_OBM_SETTINGS_FAILED = "Configure_Obm_Settings_Failed";
    public static final String CONFIGURE_BOOT_DEVICE_FAILED = "Configure_Boot_Device_Failed";
    public static final String RETRIEVE_DEFAULT_ESXI_CREDENTIALS_FAILED = "Retrieve-Default-Esxi-Credentials";
    public static final String INSTALL_ESXI_FAILED = "Installe-Esxi-Failed";
    public static final String ADD_HOST_TO_CLUSTER_FAILED = "Add-Host-To-Cluster-Failed";
    public static final String FIND_VCLUSTER_FAILED = "Find-VCluster-Failed";
    public static final String APPLY_ESXI_LICENSE_FAILED = "Apply-Esxi-License-Failed";
    public static final String FIND_SCALE_IO_FAILED = "Find-Scale-IO-Failed";
    public static final String ENABLE_PCI_PASSTHROUGH_FAILED = "Enable-PCI-Pass-Through-Failed";
    public static final String INSTALL_SCALEIO_VIB_FAILED = "Install-ScaleIO-Vib-Failed";
    public static final String REBOOT_HOST_FAILED = "Reboot-Host-Failed";
    public static final String CONFIGURE_SCALEIO_VIB_FAILED = "Configure-ScaleIO-Vib-Failed";
    public static final String ADD_HOST_TO_DV_SWITCH_FAILED = "Add-Host-To-DV-Switch-Failed";
    public static final String VERIFY_NODE_DETAIL_FAILED = "Verify-Node-Detail-Failed";
    public static final String VERIFY_NODES_SELECTED_FAILED = "Verify-Nodes-Selected-Failed";
    public static final String SEND_CONFIGURE_BOOT_DEVICE_FAILED = "Send-Configure-Boot-Device-Failed";

    public static final String DELEGATE_STATUS_VARIABLE = "processStatusLogs";
    public static final String FAILED = "Failed";
    public static final String IN_PROGRESS = "In Progress";
}

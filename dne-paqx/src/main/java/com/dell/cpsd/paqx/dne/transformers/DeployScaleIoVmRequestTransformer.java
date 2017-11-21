/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 **/

package com.dell.cpsd.paqx.dne.transformers;

import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.virtualization.capabilities.api.AutoStartDefaults;
import com.dell.cpsd.virtualization.capabilities.api.AutoStartPowerInfo;
import com.dell.cpsd.virtualization.capabilities.api.Credentials;
import com.dell.cpsd.virtualization.capabilities.api.DeployVMFromTemplateRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.NicSetting;
import com.dell.cpsd.virtualization.capabilities.api.VirtualMachineCloneSpec;
import com.dell.cpsd.virtualization.capabilities.api.VirtualMachineConfigSpec;
import com.dell.cpsd.virtualization.capabilities.api.VmAutoStartConfig;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.HOSTNAME;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;
import static java.util.Collections.singletonList;

/**
 * Deploy ScaleIO VM Request Transformer
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@Component
public class DeployScaleIoVmRequestTransformer
{
    private static final String  VCENTER_CUSTOMER_TYPE         = "VCENTER-CUSTOMER";
    private static final String  SCALEIO_VM_PREFIX             = "ScaleIO-";
    private static final String  SCALEIO_TEMPLATE_VM_NAME      = "EMC ScaleIO SVM Template.*";
    private static final int     SCALEIO_VM_NUM_CPU            = 8;
    private static final int     SCALEIO_VM_RAM                = 8192;
    private static final boolean CONFIGURE_VM_FOR_AUTO_STARTUP = true;
    private static final boolean CONFIGURE_HOST_AUTO_STARTUP   = true;
    private static final int     START_DELAY_VM                = 20;
    private static final int     STOP_DELAY_VM                 = -1;
    private static final int     START_DELAY_HOST              = 120;
    private static final int     STOP_DELAY_HOST               = 120;
    private static final int     START_ORDER                   = 1;
    private static final boolean poweredOn                     = false;
    private static final boolean isTemplate                    = false;
    private static final char    DOT_STRING                    = '.';
    private static final char    HYPHEN_DELIMITER              = '-';

    private final DataServiceRepository   repository;
    private final ComponentIdsTransformer componentIdsTransformer;

    public DeployScaleIoVmRequestTransformer(final DataServiceRepository repository, final ComponentIdsTransformer componentIdsTransformer)
    {
        this.repository = repository;
        this.componentIdsTransformer = componentIdsTransformer;
    }

    public DeployVMFromTemplateRequestMessage buildDeployVmRequest(final DelegateExecution delegateExecution)
    {
        final ComponentEndpointIds componentEndpointIds = componentIdsTransformer
                .getVCenterComponentEndpointIdsByEndpointType(VCENTER_CUSTOMER_TYPE);
        final String hostname = (String) delegateExecution.getVariable(HOSTNAME);
        final NodeDetail nodeDetail = (NodeDetail) delegateExecution.getVariable(NODE_DETAIL);
        final String clusterName = nodeDetail.getClusterName();
        final String scaleIoSvmManagementIpAddress = nodeDetail.getScaleIoSvmManagementIpAddress();

        final String dataCenterName = getDatacenterName(clusterName);
        final String domainName = getDomainName();
        final List<String> dnsServers = getDnsServers();
        final List<NicSetting> nicSettings = getNicSettings(nodeDetail, scaleIoSvmManagementIpAddress);
        final String newScaleIoVmName = SCALEIO_VM_PREFIX.concat(scaleIoSvmManagementIpAddress);
        final String newScaleIoVmHostname = newScaleIoVmName.replace(DOT_STRING, HYPHEN_DELIMITER);

        final VirtualMachineCloneSpec virtualMachineCloneSpec = getVirtualMachineCloneSpec(domainName, dnsServers, nicSettings,
                newScaleIoVmHostname);

        final VmAutoStartConfig vmAutoStartConfig = getVmAutoStartConfig();

        return getDeployVmFromTemplateRequestMessage(componentEndpointIds, hostname, dataCenterName, newScaleIoVmName,
                virtualMachineCloneSpec, vmAutoStartConfig);
    }

    private DeployVMFromTemplateRequestMessage getDeployVmFromTemplateRequestMessage(final ComponentEndpointIds componentEndpointIds,
            final String hostname, final String dataCenterName, final String newScaleIoVmName,
            final VirtualMachineCloneSpec virtualMachineCloneSpec, final VmAutoStartConfig vmAutoStartConfig)
    {
        final DeployVMFromTemplateRequestMessage requestMessage = new DeployVMFromTemplateRequestMessage();
        requestMessage.setCredentials(new Credentials(componentEndpointIds.getEndpointUrl(), null, null));
        requestMessage.setComponentEndpointIds(
                new com.dell.cpsd.virtualization.capabilities.api.ComponentEndpointIds(componentEndpointIds.getComponentUuid(),
                        componentEndpointIds.getEndpointUuid(), componentEndpointIds.getCredentialUuid()));
        requestMessage.setHostName(hostname);
        requestMessage.setTemplateName(SCALEIO_TEMPLATE_VM_NAME);
        requestMessage.setDatacenterName(dataCenterName);
        requestMessage.setNewVMName(newScaleIoVmName);
        requestMessage.setVirtualMachineCloneSpec(virtualMachineCloneSpec);
        requestMessage.setConfigureAutoStart(CONFIGURE_VM_FOR_AUTO_STARTUP);
        requestMessage.setVmAutoStartConfig(vmAutoStartConfig);
        return requestMessage;
    }

    private VmAutoStartConfig getVmAutoStartConfig()
    {
        final VmAutoStartConfig vmAutoStartConfig = new VmAutoStartConfig();

        buildAutoStartPowerInfo(vmAutoStartConfig);
        buildAutoStartDefaults(vmAutoStartConfig);

        return vmAutoStartConfig;
    }

    private void buildAutoStartDefaults(final VmAutoStartConfig vmAutoStartConfig)
    {
        final AutoStartDefaults autoStartDefaults = new AutoStartDefaults();
        autoStartDefaults.setEnabled(CONFIGURE_HOST_AUTO_STARTUP);
        autoStartDefaults.setStartDelay(START_DELAY_HOST);
        autoStartDefaults.setStopAction(AutoStartDefaults.StopAction.POWER_OFF);
        autoStartDefaults.setStopDelay(STOP_DELAY_HOST);
        vmAutoStartConfig.setAutoStartDefaults(autoStartDefaults);
    }

    private void buildAutoStartPowerInfo(final VmAutoStartConfig vmAutoStartConfig)
    {
        final AutoStartPowerInfo autoStartPowerInfo = new AutoStartPowerInfo();
        autoStartPowerInfo.setStartAction(AutoStartPowerInfo.StartAction.POWER_ON);
        autoStartPowerInfo.setWaitForHeartBeat(AutoStartPowerInfo.WaitForHeartBeat.SYSTEM_DEFAULT);
        autoStartPowerInfo.setStartDelay(START_DELAY_VM);
        autoStartPowerInfo.setStartOrder(START_ORDER);
        autoStartPowerInfo.setStopDelay(STOP_DELAY_VM);
        autoStartPowerInfo.setStopAction(AutoStartPowerInfo.StopAction.SYSTEM_DEFAULT);
        vmAutoStartConfig.setAutoStartPowerInfo(autoStartPowerInfo);
    }

    private VirtualMachineCloneSpec getVirtualMachineCloneSpec(final String domainName, final List<String> dnsServers,
            final List<NicSetting> nicSettings, final String newScaleIoVmHostname)
    {
        final VirtualMachineCloneSpec virtualMachineCloneSpec = new VirtualMachineCloneSpec();
        virtualMachineCloneSpec.setPoweredOn(poweredOn);
        virtualMachineCloneSpec.setTemplate(isTemplate);
        virtualMachineCloneSpec.setDomain(domainName);
        buildVirtualMachineConfigSpec(dnsServers, nicSettings, newScaleIoVmHostname, virtualMachineCloneSpec);
        return virtualMachineCloneSpec;
    }

    private void buildVirtualMachineConfigSpec(final List<String> dnsServers, final List<NicSetting> nicSettings,
            final String newScaleIoVmHostname, final VirtualMachineCloneSpec virtualMachineCloneSpec)
    {
        final VirtualMachineConfigSpec virtualMachineConfigSpec = new VirtualMachineConfigSpec();
        virtualMachineConfigSpec.setHostName(newScaleIoVmHostname);
        virtualMachineConfigSpec.setNumCPUs(SCALEIO_VM_NUM_CPU);
        virtualMachineConfigSpec.setMemoryMB(SCALEIO_VM_RAM);
        virtualMachineConfigSpec.setDnsServerList(dnsServers);
        virtualMachineConfigSpec.setNicSettings(nicSettings);
        virtualMachineCloneSpec.setVirtualMachineConfigSpec(virtualMachineConfigSpec);
    }

    private List<NicSetting> getNicSettings(final NodeDetail nodeDetail, final String scaleIoSvmManagementIpAddress)
    {
        final String scaleIoSvmManagementGatewayAddress = nodeDetail.getScaleIoSvmManagementGatewayAddress();
        final String scaleIoSvmManagementSubnetMask = nodeDetail.getScaleIoSvmManagementSubnetMask();
        final String scaleIoData1SvmIpAddress = nodeDetail.getScaleIoData1SvmIpAddress();
        final String scaleIoData2SvmIpAddress = nodeDetail.getScaleIoData2SvmIpAddress();
        final String scaleIoData1SvmSubnetMask = nodeDetail.getScaleIoData1SvmSubnetMask();
        final String scaleIoData2SvmSubnetMask = nodeDetail.getScaleIoData2SvmSubnetMask();

        final NicSetting nicSettingScaleIoMgmt = buildNicSetting(scaleIoSvmManagementIpAddress, scaleIoSvmManagementGatewayAddress,
                scaleIoSvmManagementSubnetMask);
        final NicSetting nicSettingScaleIoData1 = buildNicSetting(scaleIoData1SvmIpAddress, null, scaleIoData1SvmSubnetMask);
        final NicSetting nicSettingScaleIoData2 = buildNicSetting(scaleIoData2SvmIpAddress, null, scaleIoData2SvmSubnetMask);

        return Arrays.asList(nicSettingScaleIoMgmt, nicSettingScaleIoData1, nicSettingScaleIoData2);
    }

    private NicSetting buildNicSetting(final String scaleIoSvmManagementIpAddress, final String scaleIoSvmManagementGatewayAddress,
            final String scaleIoSvmManagementSubnetMask)
    {
        final NicSetting nicSettingScaleIoMgmt = new NicSetting();
        nicSettingScaleIoMgmt.setIpAddress(scaleIoSvmManagementIpAddress);
        nicSettingScaleIoMgmt.setGateway(singletonList(scaleIoSvmManagementGatewayAddress));
        nicSettingScaleIoMgmt.setSubnetMask(scaleIoSvmManagementSubnetMask);
        return nicSettingScaleIoMgmt;
    }

    private List<String> getDnsServers()
    {
        return repository.getDnsServers();
    }

    private String getDomainName()
    {
        final String domainName = repository.getDomainName();

        if (StringUtils.isEmpty(domainName))
        {
            throw new IllegalStateException("Domain name is null");
        }
        return domainName;
    }

    private String getDatacenterName(final String clusterName)
    {
        final String dataCenterName = repository.getDataCenterName(clusterName);

        if (StringUtils.isEmpty(dataCenterName))
        {
            throw new IllegalStateException("DataCenter name is null");
        }
        return dataCenterName;
    }
}

/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.domain.vcenter.Host;
import com.dell.cpsd.paqx.dne.domain.vcenter.HostDnsConfig;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
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
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.DEPLOY_SCALEIO_NEW_VM_NAME;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.DEPLOY_SCALEIO_VM_FAILED;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.HOSTNAME;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;
import static java.util.Collections.singletonList;

/**
 * Deploy ScaleIo virtual machine.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@Component
@Scope("prototype")
@Qualifier("deployScaleIOVm")
public class DeployScaleIOVm extends BaseWorkflowDelegate
{
    /*
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DeployScaleIOVm.class);

    /*
     * The <code>NodeService</code> instance
     */
    private final NodeService nodeService;

    /*
    * The time to wait before sending the request to deploy the scaleio vm
    */
    //private final long waitTime;

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

    /**
     * The <code>DataServiceRepository</code> instance
     */
    private final DataServiceRepository repository;

    /**
     * DeployScaleIoVmTaskHandler constructor
     *
     * @param nodeService - The <code>DataServiceRepository</code> instance
     * @param repository  - The <code>NodeService</code> instance
     */
    @Autowired
    public DeployScaleIOVm(final NodeService nodeService, final DataServiceRepository repository)
    {
        this.nodeService = nodeService;
        this.repository = repository;
    }

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Execute Deploy ScaleIO VM From Template");
        final String taskMessage = "Deploy ScaleIo Vm";
        final NodeDetail nodeDetail = (NodeDetail) delegateExecution.getVariable(NODE_DETAIL);
        final String hostname = (String) delegateExecution.getVariable(HOSTNAME);

        final ComponentEndpointIds componentEndpointIds = repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER");
        final String dataCenterName = nodeDetail.getClusterName();
        final String newScaleIoVmName = SCALEIO_VM_PREFIX + nodeDetail.getScaleIoSvmManagementIpAddress();
        final String newScaleIoVmHostname = newScaleIoVmName.replace(".", "-");
        final String domainName = repository.getDomainName();
        final List<String> dnsServers = queryDnsConfigIps();
        final List<NicSetting> nicSettings = buildNicSettingsList(nodeDetail);

        final VirtualMachineCloneSpec virtualMachineCloneSpec = new VirtualMachineCloneSpec();
        virtualMachineCloneSpec.setPoweredOn(false);
        virtualMachineCloneSpec.setTemplate(false);
        virtualMachineCloneSpec.setDomain(domainName);

        final VirtualMachineConfigSpec virtualMachineConfigSpec = new VirtualMachineConfigSpec();
        virtualMachineConfigSpec.setHostName(newScaleIoVmHostname);
        virtualMachineConfigSpec.setNumCPUs(SCALEIO_VM_NUM_CPU);
        virtualMachineConfigSpec.setMemoryMB(SCALEIO_VM_RAM);
        virtualMachineConfigSpec.setDnsServerList(dnsServers);
        virtualMachineConfigSpec.setNicSettings(nicSettings);
        virtualMachineCloneSpec.setVirtualMachineConfigSpec(virtualMachineConfigSpec);

        final VmAutoStartConfig vmAutoStartConfig = new VmAutoStartConfig();

        final AutoStartPowerInfo autoStartPowerInfo = new AutoStartPowerInfo();
        autoStartPowerInfo.setStartAction(AutoStartPowerInfo.StartAction.POWER_ON);
        autoStartPowerInfo.setWaitForHeartBeat(AutoStartPowerInfo.WaitForHeartBeat.SYSTEM_DEFAULT);
        autoStartPowerInfo.setStartDelay(START_DELAY_VM);
        autoStartPowerInfo.setStartOrder(START_ORDER);
        autoStartPowerInfo.setStopDelay(STOP_DELAY_VM);
        autoStartPowerInfo.setStopAction(AutoStartPowerInfo.StopAction.SYSTEM_DEFAULT);

        final AutoStartDefaults autoStartDefaults = new AutoStartDefaults();
        autoStartDefaults.setEnabled(CONFIGURE_HOST_AUTO_STARTUP);
        autoStartDefaults.setStartDelay(START_DELAY_HOST);
        autoStartDefaults.setStopAction(AutoStartDefaults.StopAction.POWER_OFF);
        autoStartDefaults.setStopDelay(STOP_DELAY_HOST);

        vmAutoStartConfig.setAutoStartDefaults(autoStartDefaults);
        vmAutoStartConfig.setAutoStartPowerInfo(autoStartPowerInfo);

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

        boolean succeeded;
        try
        {
            succeeded = this.nodeService.requestDeployScaleIoVm(requestMessage);
        }
        catch (Exception e)
        {
            String errorMessage = taskMessage + " on Node " + nodeDetail.getServiceTag() + " failed!";
            LOGGER.error(errorMessage);
            updateDelegateStatus(errorMessage);
            throw new BpmnError(DEPLOY_SCALEIO_VM_FAILED, errorMessage);
        }

        if (!succeeded)
        {
            String errorMessage = taskMessage + ": request deploy ScaleIO VM failed";
            LOGGER.error(errorMessage);
            updateDelegateStatus(errorMessage);
            throw new BpmnError(DEPLOY_SCALEIO_VM_FAILED, errorMessage);
        }

        delegateExecution.setVariable(DEPLOY_SCALEIO_NEW_VM_NAME, newScaleIoVmName);

        String returnMessage = taskMessage + " on Node " + nodeDetail.getServiceTag() + " was successful.";
        LOGGER.info(returnMessage);
        updateDelegateStatus(returnMessage);
    }

    private List<String> queryDnsConfigIps()
    {
        final Host existingVCenterHost = repository.getExistingVCenterHost();
        final HostDnsConfig hostDnsConfig = existingVCenterHost.getHostDnsConfig();
        List<java.lang.String> dnsConfigIps = hostDnsConfig.getDnsConfigIPs();

        if (CollectionUtils.isEmpty(dnsConfigIps))
        {
            throw new IllegalStateException("No DNS config IPs");
        }

        return dnsConfigIps;
    }

    private List<NicSetting> buildNicSettingsList(NodeDetail nodeDetail)
    {
        final String scaleIoSvmManagementGatewayAddress = nodeDetail.getScaleIoSvmManagementGatewayAddress();
        final String scaleIoSvmManagementSubnetMask = nodeDetail.getScaleIoSvmManagementSubnetMask();
        final String scaleIoData1SvmIpAddress = nodeDetail.getScaleIoData1SvmIpAddress();
        final String scaleIoSvmData1SubnetMask = nodeDetail.getScaleIoData1SvmSubnetMask();
        final String scaleIoData2SvmIpAddress = nodeDetail.getScaleIoData2SvmIpAddress();
        final String scaleIoSvmData2SubnetMask = nodeDetail.getScaleIoData1EsxSubnetMask();

        final NicSetting nicSettingScaleIoMgmt = new NicSetting();
        nicSettingScaleIoMgmt.setIpAddress(nodeDetail.getScaleIoSvmManagementIpAddress());
        nicSettingScaleIoMgmt.setGateway(singletonList(scaleIoSvmManagementGatewayAddress));
        nicSettingScaleIoMgmt.setSubnetMask(scaleIoSvmManagementSubnetMask);

        final NicSetting nicSettingScaleIoData1 = new NicSetting();
        nicSettingScaleIoData1.setIpAddress(scaleIoData1SvmIpAddress);
        nicSettingScaleIoData1.setSubnetMask(scaleIoSvmData1SubnetMask);

        final NicSetting nicSettingScaleIoData2 = new NicSetting();
        nicSettingScaleIoData2.setIpAddress(scaleIoData2SvmIpAddress);
        nicSettingScaleIoData2.setSubnetMask(scaleIoSvmData2SubnetMask);

        return Arrays.asList(nicSettingScaleIoMgmt, nicSettingScaleIoData1, nicSettingScaleIoData2);
    }
}

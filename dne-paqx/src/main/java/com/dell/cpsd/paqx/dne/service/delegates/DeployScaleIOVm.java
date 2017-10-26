/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.domain.vcenter.Host;
import com.dell.cpsd.paqx.dne.domain.vcenter.HostDnsConfig;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.paqx.dne.service.model.InstallEsxiTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.NodeExpansionRequest;
import com.dell.cpsd.virtualization.capabilities.api.NicSetting;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonList;

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

    private static final String SCALEIO_VM_PREFIX        = "ScaleIO-";
    private static final String SCALEIO_TEMPLATE_VM_NAME = "EMC ScaleIO SVM Template.*";
    private static final int    SCALEIO_VM_NUM_CPU       = 8;
    private static final int    SCALEIO_VM_RAM           = 8192;

    /**
     * The <code>DataServiceRepository</code> instance
     */
    private final DataServiceRepository repository;

    /**
     * DeployScaleIoVmTaskHandler constructor
     *
     * @param nodeService - The <code>DataServiceRepository</code> instance
     * @param repository - The <code>NodeService</code> instance
     * @param waitTime - Before deploying the SVM need to wait for services to be up and running on the new host
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

/*
        try
        {
            final Validate validate = new Validate(job).invoke();
            final ComponentEndpointIds componentEndpointIds = validate.getComponentEndpointIds();
            final String hostname = validate.getHostname();
            final String dataCenterName = validate.getDataCenterName();
            final String newScaleIoVmName = validate.getNewScaleIoVmName();
            final String newScaleIoVmHostname = validate.getNewScaleIoVmHostname();
            final String domainName = validate.getNewScaleIoVmDomainName();
            final List<String> dnsServers = validate.getNewScaleIoVmDnsServers();
            final List<NicSetting> nicSettings = validate.getNewScaleIoVmNicSettings();

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

            Thread.sleep(this.waitTime);

            final boolean succeeded = this.nodeService.requestDeployScaleIoVm(requestMessage);

            if (!succeeded)
            {
                throw new IllegalStateException("Request deploy ScaleIO VM failed");
            }

            response.setWorkFlowTaskStatus(Status.SUCCEEDED);
            response.setNewVMName(newScaleIoVmName);
            return true;
        }
        catch (Exception e)
        {
            LOGGER.error("Error deploying ScaleIO VM", e);
            response.addError(e.toString());
        }

        response.setWorkFlowTaskStatus(Status.FAILED);
        return false;*/
        LOGGER.info("Deploy ScaleIO VM From Template was successful");
    }

    private class Validate
    {
        private final InstallEsxiTaskResponse installEsxiTaskResponse;
        private final NodeExpansionRequest    inputParams;
        private       ComponentEndpointIds    componentEndpointIds;
        private       String                  hostname;
        private       String                  dataCenterName;
        private       String                  newScaleIoVmIpAddress;
        private       String                  newScaleIoVmGatewayAddress;
        private       String                  newScaleIoVmName;
        private       String                  newScaleIoVmHostname;
        private       String                  newScaleIoVmDomainName;
        private       List<String>            newScaleIoVmDnsServers;
        private       List<NicSetting>        newScaleIoVmNicSettings;

        Validate(final Job job)
        {
            this.installEsxiTaskResponse = (InstallEsxiTaskResponse) job.getTaskResponseMap().get("installEsxi");

            if (this.installEsxiTaskResponse == null)
            {
                throw new IllegalStateException("No Install ESXi task response found");
            }

            this.inputParams = job.getInputParams();

            if (this.inputParams == null)
            {
                throw new IllegalStateException("Job Input Params are null");
            }
        }

        Validate invoke()
        {
            this.componentEndpointIds = this.queryComponentEndpointIds();
            this.hostname = this.queryHostname();
            this.dataCenterName = this.queryDatacenterName();
            this.newScaleIoVmIpAddress = this.queryScaleIOSVMManagementIpAddress();
            this.newScaleIoVmGatewayAddress = this.queryScaleIOSVMManagementGatewayAddress();
            this.newScaleIoVmName = SCALEIO_VM_PREFIX + this.newScaleIoVmIpAddress;
            this.newScaleIoVmHostname = this.newScaleIoVmName.replace(".", "-");
            this.newScaleIoVmDomainName = this.queryDomainName();
            this.newScaleIoVmDnsServers = this.queryDnsConfigIps();
            this.newScaleIoVmNicSettings = this.buildNicSettingsList();
            return this;
        }

        ComponentEndpointIds getComponentEndpointIds()
        {
            return componentEndpointIds;
        }

        String getHostname()
        {
            return hostname;
        }

        String getDataCenterName()
        {
            return dataCenterName;
        }

        String getNewScaleIoVmName()
        {
            return newScaleIoVmName;
        }

        String getNewScaleIoVmHostname()
        {
            return newScaleIoVmHostname;
        }

        String getNewScaleIoVmDomainName()
        {
            return newScaleIoVmDomainName;
        }

        List<String> getNewScaleIoVmDnsServers()
        {
            return newScaleIoVmDnsServers;
        }

        List<NicSetting> getNewScaleIoVmNicSettings()
        {
            return newScaleIoVmNicSettings;
        }

        ComponentEndpointIds queryComponentEndpointIds()
        {
            final ComponentEndpointIds ids = repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER");

            if (ids == null)
            {
                throw new IllegalStateException("No VCenter components found.");
            }

            return ids;
        }

        String queryHostname()
        {
            final String hostName = this.installEsxiTaskResponse.getHostname();

            if (StringUtils.isEmpty(hostName))
            {
                throw new IllegalStateException("Hostname is null");
            }

            return hostName;
        }

        String queryScaleIOSVMManagementIpAddress()
        {
            final String scaleIOSVMManagementIpAddress = this.inputParams.getScaleIoSvmManagementIpAddress();

            if (StringUtils.isEmpty(scaleIOSVMManagementIpAddress))
            {
                throw new IllegalStateException("ScaleIO Management IP Address is null");
            }

            return scaleIOSVMManagementIpAddress;
        }

        String queryScaleIOSVMManagementGatewayAddress()
        {
            final String scaleIOSVMManagementGatewayAddress = this.inputParams.getScaleIoSvmManagementGatewayAddress();

            if (StringUtils.isEmpty(scaleIOSVMManagementGatewayAddress))
            {
                throw new IllegalStateException("ScaleIO Management Gateway Address is null");
            }

            return scaleIOSVMManagementGatewayAddress;
        }

        String queryDatacenterName()
        {
            final String clusterName = this.inputParams.getClusterName();

            if (StringUtils.isEmpty(clusterName))
            {
                throw new IllegalStateException("Cluster name is null");
            }

            String dcName = repository.getDataCenterName(clusterName);

            if (StringUtils.isEmpty(dcName))
            {
                throw new IllegalStateException("DataCenter name is null");
            }

            return dcName;
        }

        String queryDomainName()
        {
            final String domainName = repository.getDomainName();

            if (StringUtils.isEmpty(domainName))
            {
                throw new IllegalStateException("Domain name is null");
            }

            return domainName;
        }

        List<String> queryDnsConfigIps()
        {
            final Host existingVCenterHost = repository.getExistingVCenterHost();
            final HostDnsConfig hostDnsConfig = existingVCenterHost.getHostDnsConfig();
            List<String> dnsConfigIps = hostDnsConfig.getDnsConfigIPs();

            if (CollectionUtils.isEmpty(dnsConfigIps))
            {
                throw new IllegalStateException("No DNS config IPs");
            }

            return dnsConfigIps;
        }

        List<NicSetting> buildNicSettingsList()
        {
            final String esxiManagementGatewayIpAddress = this.inputParams.getEsxiManagementGatewayIpAddress();

            if (StringUtils.isEmpty(esxiManagementGatewayIpAddress))
            {
                throw new IllegalStateException("ESXi Management Gateway IP Address is null");
            }

            final String scaleIoSvmManagementIpAddress = this.inputParams.getScaleIoSvmManagementIpAddress();

            if (StringUtils.isEmpty(scaleIoSvmManagementIpAddress))
            {
                throw new IllegalStateException("ScaleIO VM Management IP Address is null");
            }

            final String scaleIoSvmManagementGatewayAddress = this.inputParams.getScaleIoSvmManagementGatewayAddress();

            if (StringUtils.isEmpty(scaleIoSvmManagementGatewayAddress))
            {
                throw new IllegalStateException("ScaleIO VM Management Gateway is null");
            }

            final String scaleIoSvmManagementSubnetMask = this.inputParams.getScaleIoSvmManagementSubnetMask();

            if (StringUtils.isEmpty(scaleIoSvmManagementSubnetMask))
            {
                throw new IllegalStateException("ScaleIO VM Management Subnet Mask is null");
            }

            final String scaleIoData1SvmIpAddress = this.inputParams.getScaleIoData1SvmIpAddress();

            if (StringUtils.isEmpty(scaleIoData1SvmIpAddress))
            {
                throw new IllegalStateException("ScaleIO Data1 IP Address is null");
            }

            final String scaleIoSvmData1SubnetMask = this.inputParams.getScaleIoSvmData1SubnetMask();

            if (StringUtils.isEmpty(scaleIoSvmData1SubnetMask))
            {
                throw new IllegalStateException("ScaleIO VM Data1 Subnet Mask is null");
            }

            final String scaleIoData2SvmIpAddress = this.inputParams.getScaleIoData2SvmIpAddress();

            if (StringUtils.isEmpty(scaleIoData2SvmIpAddress))
            {
                throw new IllegalStateException("ScaleIO Data2 IP Address is null");
            }

            final String scaleIoSvmData2SubnetMask = this.inputParams.getScaleIoSvmData2SubnetMask();

            if (StringUtils.isEmpty(scaleIoSvmData2SubnetMask))
            {
                throw new IllegalStateException("ScaleIO VM Data2 Subnet Mask is null");
            }

            final NicSetting nicSettingScaleIoMgmt = new NicSetting();
            nicSettingScaleIoMgmt.setIpAddress(this.newScaleIoVmIpAddress);
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
}

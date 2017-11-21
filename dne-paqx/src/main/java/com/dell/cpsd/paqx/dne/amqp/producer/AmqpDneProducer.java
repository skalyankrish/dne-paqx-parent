/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.amqp.producer;

import com.dell.cpsd.*;
import com.dell.cpsd.common.rabbitmq.annotation.Message;
import com.dell.cpsd.hdp.capability.registry.api.ProviderEndpoint;
import com.dell.cpsd.hdp.capability.registry.client.binder.CapabilityBinder;
import com.dell.cpsd.hdp.capability.registry.client.binder.CapabilityData;
import com.dell.cpsd.hdp.capability.registry.client.helper.AmqpProviderEndpointHelper;
import com.dell.cpsd.rackhd.adapter.model.idrac.IdracNetworkSettingsRequestMessage;
import com.dell.cpsd.service.engineering.standards.EssValidateProtectionDomainsRequestMessage;
import com.dell.cpsd.service.engineering.standards.EssValidateStoragePoolRequestMessage;
import com.dell.cpsd.storage.capabilities.api.AddHostToProtectionDomainRequestMessage;
import com.dell.cpsd.storage.capabilities.api.CreateProtectionDomainRequestMessage;
import com.dell.cpsd.storage.capabilities.api.CreateStoragePoolRequestMessage;
import com.dell.cpsd.storage.capabilities.api.ListComponentRequestMessage;
import com.dell.cpsd.storage.capabilities.api.ListStorageRequestMessage;
import com.dell.cpsd.storage.capabilities.api.SioSdcUpdatePerformanceProfileRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.AddEsxiHostVSphereLicenseRequest;
import com.dell.cpsd.virtualization.capabilities.api.AddHostToDvSwitchRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.ClusterOperationRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.ConfigureVmNetworkSettingsRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.DatastoreRenameRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.DeployVMFromTemplateRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.DiscoverClusterRequestInfoMessage;
import com.dell.cpsd.virtualization.capabilities.api.DiscoveryRequestInfoMessage;
import com.dell.cpsd.virtualization.capabilities.api.EnablePCIPassthroughRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.HostMaintenanceModeRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.HostPowerOperationRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.ListComponentsRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.ListEsxiCredentialDetailsRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.RemoteCommandExecutionRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.SoftwareVIBConfigureRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.SoftwareVIBRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.UpdatePCIPassthruSVMRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.VCenterUpdateSoftwareAcceptanceRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.ValidateVcenterClusterRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.VmPowerOperationsRequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */

public class AmqpDneProducer implements DneProducer
{
    @Autowired
    private String essRequestExchange;

    @Autowired
    private String essReqRoutingKeyPrefix;

    private static final Logger LOGGER = LoggerFactory.getLogger(AmqpDneProducer.class);
    private final RabbitTemplate   rabbitTemplate;
    private final CapabilityBinder capabilityBinder;

    public AmqpDneProducer(RabbitTemplate rabbitTemplate, CapabilityBinder capabilityBinder)
    {
        this.rabbitTemplate = rabbitTemplate;
        this.capabilityBinder = capabilityBinder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishIdracNetwokSettings(IdracNetworkSettingsRequestMessage request)
    {
        AmqpProviderEndpointHelper endpointHelper = findEndpointHelper(IdracNetworkSettingsRequestMessage.class);

        if (endpointHelper != null)
        {
            LOGGER.info("Publish idrac network settings request message from DNE paqx.");
            rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishConfigureBootDeviceIdrac(ConfigureBootDeviceIdracRequestMessage request)
    {
        AmqpProviderEndpointHelper endpointHelper = findEndpointHelper(ConfigureBootDeviceIdracRequestMessage.class);

        if (endpointHelper != null)
        {
            LOGGER.info("Publish configure boot device idrac request message from DNE paqx.");
            rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishConfigurePxeBoot(ConfigurePxeBootRequestMessage request)
    {
        AmqpProviderEndpointHelper endpointHelper = findEndpointHelper(ConfigurePxeBootRequestMessage.class);

        if (endpointHelper != null)
        {
            LOGGER.info("Publish configure Pxe Boot request message from DNE paqx.");
            rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishConfigureObmSettings(SetObmSettingsRequestMessage request)
    {
        AmqpProviderEndpointHelper endpointHelper = findEndpointHelper(SetObmSettingsRequestMessage.class);

        if (endpointHelper != null)
        {
            LOGGER.info("Publish configure obm settings request message from DNE paqx.");
            rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishListScaleIoComponents(final ListComponentRequestMessage request)
    {
        AmqpProviderEndpointHelper endpointHelper = findEndpointHelper(ListComponentRequestMessage.class);

        if (endpointHelper != null)
        {
            LOGGER.info("Publish List ScaleIO Components request message from DNE paqx.");
            rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishListVCenterComponents(final ListComponentsRequestMessage request)
    {
        AmqpProviderEndpointHelper endpointHelper = findEndpointHelper(ListComponentsRequestMessage.class);

        if (endpointHelper != null)
        {
            LOGGER.info("Publish List VCenter Components request message from DNE paqx.");
            rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishDiscoverScaleIo(final ListStorageRequestMessage request)
    {
        AmqpProviderEndpointHelper endpointHelper = findEndpointHelper(ListStorageRequestMessage.class);

        if (endpointHelper != null)
        {
            LOGGER.info("Publish Discover ScaleIO request message from DNE paqx.");
            rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishDiscoverVcenter(final DiscoveryRequestInfoMessage request)
    {
        AmqpProviderEndpointHelper endpointHelper = findEndpointHelper(DiscoveryRequestInfoMessage.class);

        if (endpointHelper != null)
        {
            LOGGER.info("Publish Discover VCenter request message from DNE paqx.");
            rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishInstallEsxiRequest(final InstallESXiRequestMessage request)
    {
        AmqpProviderEndpointHelper endpointHelper = findEndpointHelper(InstallESXiRequestMessage.class);

        if (endpointHelper != null)
        {
            LOGGER.info("Publish Install ESXi License request from DNE paqx.");
            rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishAddHostToVCenter(final ClusterOperationRequestMessage request)
    {
        AmqpProviderEndpointHelper endpointHelper = findEndpointHelper(ClusterOperationRequestMessage.class);

        if (endpointHelper != null)
        {
            LOGGER.info("Publish Add Host to VCenter request from DNE paqx.");
            rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishAddHostToProtectionDomain(final AddHostToProtectionDomainRequestMessage request)
    {
        AmqpProviderEndpointHelper endpointHelper = findEndpointHelper(AddHostToProtectionDomainRequestMessage.class);

        if (endpointHelper != null)
        {
            LOGGER.info("Publish Add Host to ProtectionDomain request from DNE paqx. " + request);
            rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishInstallScaleIoVib(final SoftwareVIBRequestMessage request)
    {
        AmqpProviderEndpointHelper endpointHelper = findEndpointHelper(SoftwareVIBRequestMessage.class);

        if (endpointHelper != null)
        {
            LOGGER.info("Publish Install ScaleIo VIB request from DNE paqx.");
            rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishConfigureScaleIoVib(final SoftwareVIBConfigureRequestMessage request)
    {
        AmqpProviderEndpointHelper endpointHelper = findEndpointHelper(SoftwareVIBConfigureRequestMessage.class);

        if (endpointHelper != null)
        {
            LOGGER.info("Publish Configure ScaleIo VIB request from DNE paqx.");
            rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishAddHostToDvSwitch(final AddHostToDvSwitchRequestMessage request)
    {
        AmqpProviderEndpointHelper endpointHelper = findEndpointHelper(AddHostToDvSwitchRequestMessage.class);

        if (endpointHelper != null)
        {
            LOGGER.info("Publish Add Host to DV Switch request from DNE paqx.");
            rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishDeployVmFromTemplate(final DeployVMFromTemplateRequestMessage request)
    {
        AmqpProviderEndpointHelper endpointHelper = findEndpointHelper(DeployVMFromTemplateRequestMessage.class);

        if (endpointHelper != null)
        {
            LOGGER.info("Publish deploy VM from template request from DNE paqx.");
            rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishEnablePciPassthrough(final EnablePCIPassthroughRequestMessage request)
    {
        AmqpProviderEndpointHelper endpointHelper = findEndpointHelper(EnablePCIPassthroughRequestMessage.class);

        if (endpointHelper != null)
        {
            LOGGER.info("Publish enable PCI pass through request from DNE paqx.");
            rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishRebootHost(final HostPowerOperationRequestMessage request)
    {
        AmqpProviderEndpointHelper endpointHelper = findEndpointHelper(HostPowerOperationRequestMessage.class);

        if (endpointHelper != null)
        {
            LOGGER.info("Publish reboot host request from DNE paqx.");
            rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishSetPciPassthrough(final UpdatePCIPassthruSVMRequestMessage request)
    {
        AmqpProviderEndpointHelper endpointHelper = findEndpointHelper(UpdatePCIPassthruSVMRequestMessage.class);

        if (endpointHelper != null)
        {
            LOGGER.info("Publish Set PCI Pass through request from DNE paqx.");
            rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishApplyEsxiLicense(final AddEsxiHostVSphereLicenseRequest request)
    {
        AmqpProviderEndpointHelper endpointHelper = findEndpointHelper(AddEsxiHostVSphereLicenseRequest.class);

        if (endpointHelper != null)
        {
            LOGGER.info("Publish apply ESXi license request from DNE paqx.");
            rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishListExsiCredentialDetails(final ListEsxiCredentialDetailsRequestMessage requestMessage)
    {
        AmqpProviderEndpointHelper endpointHelper = findEndpointHelper(ListEsxiCredentialDetailsRequestMessage.class);

        if (endpointHelper != null)
        {
            LOGGER.info("Publish list esxi credential details request from DNE paqx.");
            rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), requestMessage);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishHostMaintenanceMode(final HostMaintenanceModeRequestMessage requestMessage)
    {
        AmqpProviderEndpointHelper endpointHelper = findEndpointHelper(HostMaintenanceModeRequestMessage.class);

        if (endpointHelper != null)
        {
            LOGGER.info("Publish ESXi host maintenance mode request from DNE paqx.");
            rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), requestMessage);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishListNodes(ListNodes request)
    {
        AmqpProviderEndpointHelper endpointHelper = findEndpointHelper(ListNodes.class);

        if (endpointHelper != null)
        {
            LOGGER.info("Send node discovery request message from DNE paqx.");
            rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishDiscoverClusters(DiscoverClusterRequestInfoMessage request)
    {
        AmqpProviderEndpointHelper endpointHelper = findEndpointHelper(DiscoverClusterRequestInfoMessage.class);

        if (endpointHelper != null)
        {
            LOGGER.info("Send discover cluster request message from DNE paqx.");
            rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishValidateClusters(ValidateVcenterClusterRequestMessage request)
    {
        // At this phase ESS is for DNE internal use only so no capability registry for ESS, use exchange, routing key directly.
        rabbitTemplate.convertAndSend(essRequestExchange, essReqRoutingKeyPrefix, request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishValidateStorage(EssValidateStoragePoolRequestMessage requestMessage)
    {
        // At this phase ESS is for DNE internal use only so no capability registry for ESS, use exchange, routing key directly.
        LOGGER.info("Send request to ESS validation for storage pools: " + requestMessage);
        rabbitTemplate.convertAndSend(essRequestExchange, essReqRoutingKeyPrefix, requestMessage);
    }

    @Override
    public void publishValidateProtectionDomain(EssValidateProtectionDomainsRequestMessage requestMessage)
    {
        // At this phase ESS is for DNE internal use only so no capability registry for ESS, use exchange, routing key directly.
        LOGGER.info("Send request to ESS validation for protection domains.");
        rabbitTemplate.convertAndSend(essRequestExchange, essReqRoutingKeyPrefix, requestMessage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishCompleteNodeAllocation(CompleteNodeAllocationRequestMessage request)
    {
        AmqpProviderEndpointHelper endpointHelper = findEndpointHelper(CompleteNodeAllocationRequestMessage.class);

        if (endpointHelper != null)
        {
            LOGGER.info("Send complete node allocation request message from DNE paqx.");
            rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishChangeIdracCredentials(ChangeIdracCredentialsRequestMessage request)
    {
        AmqpProviderEndpointHelper endpointHelper = findEndpointHelper(ChangeIdracCredentialsRequestMessage.class);

        if (endpointHelper != null)
        {
            LOGGER.info("Publish change idrac credentials request message from DNE paqx.");
            rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishNodeInventoryDiscovery(final NodeInventoryRequestMessage request)
    {
        AmqpProviderEndpointHelper endpointHelper = findEndpointHelper(NodeInventoryRequestMessage.class);

        if (endpointHelper != null)
        {
            LOGGER.info("Send node inventory discovery request message from DNE paqx.");
            rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishDatastoreRename(DatastoreRenameRequestMessage requestMessage)
    {
        AmqpProviderEndpointHelper endpointHelper = findEndpointHelper(DatastoreRenameRequestMessage.class);

        if (endpointHelper != null)
        {
            LOGGER.info("Send datastore rename request message from DNE paqx.");
            rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), requestMessage);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishUpdateSoftwareAcceptance(final VCenterUpdateSoftwareAcceptanceRequestMessage requestMessage)
    {
        AmqpProviderEndpointHelper endpointHelper = findEndpointHelper(VCenterUpdateSoftwareAcceptanceRequestMessage.class);

        if (endpointHelper != null)
        {
            LOGGER.info("Send update software acceptance message from DNE paqx.");
            rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), requestMessage);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishVmPowerOperation(VmPowerOperationsRequestMessage requestMessage)
    {
        AmqpProviderEndpointHelper endpointHelper = findEndpointHelper(VmPowerOperationsRequestMessage.class);

        if (endpointHelper != null)
        {
            LOGGER.info("Send vm power operation message from DNE paqx.");
            rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), requestMessage);
        }
    }

    @Override
    public void publishConfigureVmNetworkSettings(final ConfigureVmNetworkSettingsRequestMessage requestMessage)
    {
        AmqpProviderEndpointHelper endpointHelper = findEndpointHelper(ConfigureVmNetworkSettingsRequestMessage.class);

        if (endpointHelper != null)
        {
            LOGGER.info("Send configure vm setwork settings message from DNE paqx.");
            rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), requestMessage);
        }
    }

    @Override
    public void publishRemoteCommandExecution(final RemoteCommandExecutionRequestMessage requestMessage)
    {
        AmqpProviderEndpointHelper endpointHelper = findEndpointHelper(RemoteCommandExecutionRequestMessage.class);

        if (endpointHelper != null)
        {
            LOGGER.info("Send execute remote command message from DNE paqx.");
            rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), requestMessage);
        }
    }

    @Override
    public void publishUpdateSdcPerformanceProfile(final SioSdcUpdatePerformanceProfileRequestMessage requestMessage)
    {
        AmqpProviderEndpointHelper endpointHelper = findEndpointHelper(SioSdcUpdatePerformanceProfileRequestMessage.class);

        if (endpointHelper != null)
        {
            LOGGER.info("Send update ScaleIO SDC performance profile message from DNE paqx.");
            rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), requestMessage);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishCreateStoragePool(final CreateStoragePoolRequestMessage requestMessage)
    {
        AmqpProviderEndpointHelper endpointHelper = findEndpointHelper(CreateStoragePoolRequestMessage.class);

        if (endpointHelper != null)
        {
            LOGGER.info("Sending create storage pool message: " + requestMessage);
            rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), requestMessage);
        }
    }

    @Override
    public void publishCreateProtectionDomain(final CreateProtectionDomainRequestMessage requestMessage)
    {
        final AmqpProviderEndpointHelper endpointHelper = findEndpointHelper(CreateProtectionDomainRequestMessage.class);

        if (endpointHelper != null)
        {
            LOGGER.info("Sending create protection domain request message: [{}]", requestMessage);
            rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), requestMessage);
        }
    }

    @Override
    public void publishStartedNodeAllocation(final StartNodeAllocationRequestMessage requestMessage)
    {
        final AmqpProviderEndpointHelper endpointHelper = findEndpointHelper(StartNodeAllocationRequestMessage.class);

        if (endpointHelper != null)
        {
            LOGGER.info("Sending start node allocation request message: [{}]", requestMessage);
            rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), requestMessage);
        }
    }

    @Override
    public void publishFailedNodeAllocation(FailNodeAllocationRequestMessage request)
    {
        AmqpProviderEndpointHelper endpointHelper = findEndpointHelper(FailNodeAllocationRequestMessage.class);

        if (endpointHelper != null)
        {
            LOGGER.info("Send Fail node allocation request message from DNE paqx.");
            rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
        }
    }

    private AmqpProviderEndpointHelper findEndpointHelper(Class inputClass)
    {
        Collection<CapabilityData> capabilities = capabilityBinder.getCurrentCapabilities();

        if (capabilities == null)
        {
            LOGGER.error("No Capabilities found for " + inputClass.getName());
            return null;
        }

        LOGGER.info(inputClass.getName() + ": found list of capabilities with size {}", capabilities.size());

        for (CapabilityData capabilityData : capabilities)
        {
            ProviderEndpoint endpoint = capabilityData.getCapability().getProviderEndpoint();
            AmqpProviderEndpointHelper endpointHelper = new AmqpProviderEndpointHelper(endpoint);
            if (messageType(inputClass).equals(endpointHelper.getRequestMessageType()))
            {
                return endpointHelper;
            }
        }
        return null;
    }

    private String messageType(Class messageClass)
    {
        final Annotation annotation = messageClass.getAnnotation(Message.class);
        return ((Message)(annotation)).value();
    }
}

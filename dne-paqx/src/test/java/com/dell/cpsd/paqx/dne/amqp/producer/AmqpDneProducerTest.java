/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.amqp.producer;

import com.dell.cpsd.*;
import com.dell.cpsd.common.rabbitmq.annotation.Message;
import com.dell.cpsd.hdp.capability.registry.api.Capability;
import com.dell.cpsd.hdp.capability.registry.api.EndpointProperty;
import com.dell.cpsd.hdp.capability.registry.api.ProviderEndpoint;
import com.dell.cpsd.hdp.capability.registry.client.binder.CapabilityBinder;
import com.dell.cpsd.hdp.capability.registry.client.binder.CapabilityData;
import com.dell.cpsd.rackhd.adapter.model.idrac.IdracNetworkSettingsRequestMessage;
import com.dell.cpsd.service.engineering.standards.EssValidateProtectionDomainsRequestMessage;
import com.dell.cpsd.service.engineering.standards.EssValidateStoragePoolRequestMessage;
import com.dell.cpsd.storage.capabilities.api.AddHostToProtectionDomainRequestMessage;
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
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;

import org.mockito.junit.MockitoRule;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * The tests for the AmqpDneProducer class.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public class AmqpDneProducerTest
{
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private CapabilityBinder capabilityBinder;

    @Mock
    private CapabilityData capabilityData;

    @Mock
    private Capability capability;

    @Mock
    private ProviderEndpoint providerEndpoint;

    private AmqpDneProducer producer;
    private List<CapabilityData> capabilityDataList;
    private List<EndpointProperty> endpointProperties;
    private String exchange = "the_exchange";
    private String routingKey = "the_routing_key";

    @Before
    public void setUp()
    {
        this.producer = new AmqpDneProducer(this.rabbitTemplate, this.capabilityBinder);

        this.capabilityDataList = new ArrayList<>();
        this.endpointProperties = new ArrayList<>();

        this.capabilityDataList.add(this.capabilityData);

        EndpointProperty property = new EndpointProperty();
        property.setName("request-exchange");
        property.setValue(this.exchange);
        this.endpointProperties.add(property);

        property = new EndpointProperty();
        property.setName("request-routing-key");
        property.setValue(this.routingKey);
        this.endpointProperties.add(property);
    }

    @Test
    public void publishIdracNetwokSettings()
    {
        this.executeTest(mock(IdracNetworkSettingsRequestMessage.class), this.producer::publishIdracNetwokSettings);
    }

    @Test
    public void publishIdracNetwokSettings_no_capabilities()
    {
        this.executeTest_no_capabilities(mock(IdracNetworkSettingsRequestMessage.class), this.producer::publishIdracNetwokSettings);
    }

    @Test
    public void publishConfigureBootDeviceIdrac()
    {
        this.executeTest(mock(ConfigureBootDeviceIdracRequestMessage.class), this.producer::publishConfigureBootDeviceIdrac);
    }

    @Test
    public void publishConfigureBootDeviceIdrac_no_capabilities()
    {
        this.executeTest_no_capabilities(mock(ConfigureBootDeviceIdracRequestMessage.class), this.producer::publishConfigureBootDeviceIdrac);
    }
    @Test
    public void publishConfigurePxeBoot()
    {
        this.executeTest(mock(ConfigurePxeBootRequestMessage.class), this.producer::publishConfigurePxeBoot);
    }

    @Test
    public void publishConfigurePxeBoot_no_capabilities()
    {
        this.executeTest_no_capabilities(mock(ConfigurePxeBootRequestMessage.class), this.producer::publishConfigurePxeBoot);
    }

    @Test
    public void publishConfigureObmSettings()
    {
        this.executeTest(mock(SetObmSettingsRequestMessage.class), this.producer::publishConfigureObmSettings);
    }

    @Test
    public void publishConfigureObmSettings_no_capabilities()
    {
        this.executeTest_no_capabilities(mock(SetObmSettingsRequestMessage.class), this.producer::publishConfigureObmSettings);
    }


    @Test
    public void publishListScaleIoComponents()
    {
        this.executeTest(mock(ListComponentRequestMessage.class), this.producer::publishListScaleIoComponents);
    }

    @Test
    public void publishListScaleIoComponents_no_capabilities()
    {
        this.executeTest_no_capabilities(mock(ListComponentRequestMessage.class), this.producer::publishListScaleIoComponents);
    }

    @Test
    public void publishListVCenterComponents()
    {
        this.executeTest(mock(ListComponentsRequestMessage.class), this.producer::publishListVCenterComponents);
    }

    @Test
    public void publishListVCenterComponents_no_capabilities()
    {
        this.executeTest_no_capabilities(mock(ListComponentsRequestMessage.class), this.producer::publishListVCenterComponents);
    }

    @Test
    public void publishDiscoverScaleIo()
    {
        this.executeTest(mock(ListStorageRequestMessage.class), this.producer::publishDiscoverScaleIo);
    }

    @Test
    public void publishDiscoverScaleIo_no_capabilities()
    {
        this.executeTest_no_capabilities(mock(ListStorageRequestMessage.class), this.producer::publishDiscoverScaleIo);
    }

    @Test
    public void publishDiscoverVcenter()
    {
        this.executeTest(mock(DiscoveryRequestInfoMessage.class), this.producer::publishDiscoverVcenter);
    }

    @Test
    public void publishDiscoverVcenter_no_capabilities()
    {
        this.executeTest_no_capabilities(mock(DiscoveryRequestInfoMessage.class), this.producer::publishDiscoverVcenter);
    }

    @Test
    public void publishInstallEsxiRequest()
    {
        this.executeTest(mock(InstallESXiRequestMessage.class), this.producer::publishInstallEsxiRequest);
    }

    @Test
    public void publishInstallEsxiRequest_no_capabilities()
    {
        this.executeTest_no_capabilities(mock(InstallESXiRequestMessage.class), this.producer::publishInstallEsxiRequest);
    }

    @Test
    public void publishAddHostToVCenter()
    {
        this.executeTest(mock(ClusterOperationRequestMessage.class), this.producer::publishAddHostToVCenter);
    }

    @Test
    public void publishAddHostToVCenter_no_capabilities()
    {
        this.executeTest_no_capabilities(mock(ClusterOperationRequestMessage.class), this.producer::publishAddHostToVCenter);
    }

    @Test
    public void publishInstallScaleIoVib()
    {
        this.executeTest(mock(SoftwareVIBRequestMessage.class), this.producer::publishInstallScaleIoVib);
    }

    @Test
    public void publishInstallScaleIoVib_no_capabilities()
    {
        this.executeTest_no_capabilities(mock(SoftwareVIBRequestMessage.class), this.producer::publishInstallScaleIoVib);
    }

    @Test
    public void publishConfigureScaleIoVib()
    {
        this.executeTest(mock(SoftwareVIBConfigureRequestMessage.class), this.producer::publishConfigureScaleIoVib);
    }

    @Test
    public void publishConfigureScaleIoVib_no_capabilities()
    {
        this.executeTest_no_capabilities(mock(SoftwareVIBConfigureRequestMessage.class), this.producer::publishConfigureScaleIoVib);
    }

    @Test
    public void publishAddHostToDvSwitch()
    {
        this.executeTest(mock(AddHostToDvSwitchRequestMessage.class), this.producer::publishAddHostToDvSwitch);
    }

    @Test
    public void publishAddHostToDvSwitch_no_capabilities()
    {
        this.executeTest_no_capabilities(mock(AddHostToDvSwitchRequestMessage.class), this.producer::publishAddHostToDvSwitch);
    }

    @Test
    public void publishDeployVmFromTemplate()
    {
        this.executeTest(mock(DeployVMFromTemplateRequestMessage.class), this.producer::publishDeployVmFromTemplate);
    }

    @Test
    public void publishDeployVmFromTemplate_no_capabilities()
    {
        this.executeTest_no_capabilities(mock(DeployVMFromTemplateRequestMessage.class), this.producer::publishDeployVmFromTemplate);
    }

    @Test
    public void publishEnablePciPassthrough()
    {
        this.executeTest(mock(EnablePCIPassthroughRequestMessage.class), this.producer::publishEnablePciPassthrough);
    }

    @Test
    public void publishEnablePciPassthrough_no_capabilities()
    {
        this.executeTest_no_capabilities(mock(EnablePCIPassthroughRequestMessage.class), this.producer::publishEnablePciPassthrough);
    }

    @Test
    public void publishRebootHost()
    {
        this.executeTest(mock(HostPowerOperationRequestMessage.class), this.producer::publishRebootHost);
    }

    @Test
    public void publishRebootHost_no_capabilities()
    {
        this.executeTest_no_capabilities(mock(HostPowerOperationRequestMessage.class), this.producer::publishRebootHost);
    }

    @Test
    public void publishSetPciPassthrough()
    {
        this.executeTest(mock(UpdatePCIPassthruSVMRequestMessage.class), this.producer::publishSetPciPassthrough);
    }

    @Test
    public void publishSetPciPassthrough_no_capabilities()
    {
        this.executeTest_no_capabilities(mock(UpdatePCIPassthruSVMRequestMessage.class), this.producer::publishSetPciPassthrough);
    }

    @Test
    public void publishApplyEsxiLicense()
    {
        this.executeTest(mock(AddEsxiHostVSphereLicenseRequest.class), this.producer::publishApplyEsxiLicense);
    }

    @Test
    public void publishApplyEsxiLicense_no_capabilities()
    {
        this.executeTest_no_capabilities(mock(AddEsxiHostVSphereLicenseRequest.class), this.producer::publishApplyEsxiLicense);
    }

    @Test
    public void publishListExsiCredentialDetails()
    {
        this.executeTest(mock(ListEsxiCredentialDetailsRequestMessage.class), this.producer::publishListExsiCredentialDetails);
    }

    @Test
    public void publishListExsiCredentialDetails_no_capabilities()
    {
        this.executeTest_no_capabilities(mock(ListEsxiCredentialDetailsRequestMessage.class), this.producer::publishListExsiCredentialDetails);
    }

    @Test
    public void publishHostMaintenanceMode()
    {
        this.executeTest(mock(HostMaintenanceModeRequestMessage.class), this.producer::publishHostMaintenanceMode);
    }

    @Test
    public void publishHostMaintenanceMode_no_capabilities()
    {
        this.executeTest_no_capabilities(mock(HostMaintenanceModeRequestMessage.class), this.producer::publishHostMaintenanceMode);
    }

    @Test
    public void publishListNodes()
    {
        this.executeTest(mock(ListNodes.class), this.producer::publishListNodes);
    }

    @Test
    public void publishListNodes_no_capabilities()
    {
        this.executeTest_no_capabilities(mock(ListNodes.class), this.producer::publishListNodes);
    }

    @Test
    public void publishDiscoverClusters()
    {
        this.executeTest(mock(DiscoverClusterRequestInfoMessage.class), this.producer::publishDiscoverClusters);
    }

    @Test
    public void publishDiscoverClusters_no_capabilities()
    {
        this.executeTest_no_capabilities(mock(DiscoverClusterRequestInfoMessage.class), this.producer::publishDiscoverClusters);
    }

    @Test
    public void publishValidateClusters()
    {
        ValidateVcenterClusterRequestMessage request = mock(ValidateVcenterClusterRequestMessage.class);

        ReflectionTestUtils.setField(this.producer, "essRequestExchange", this.exchange);
        ReflectionTestUtils.setField(this.producer, "essReqRoutingKeyPrefix", this.routingKey);

        this.producer.publishValidateClusters(request);

        verify(this.rabbitTemplate).convertAndSend(this.exchange, this.routingKey, request);
    }

    @Test
    public void publishValidateStorage()
    {
        EssValidateStoragePoolRequestMessage request = mock(EssValidateStoragePoolRequestMessage.class);

        ReflectionTestUtils.setField(this.producer, "essRequestExchange", this.exchange);
        ReflectionTestUtils.setField(this.producer, "essReqRoutingKeyPrefix", this.routingKey);

        this.producer.publishValidateStorage(request);

        verify(this.rabbitTemplate).convertAndSend(this.exchange, this.routingKey, request);
    }

    @Test
    public void publishCompleteNodeAllocation()
    {
        CompleteNodeAllocationRequestMessage request = mock(CompleteNodeAllocationRequestMessage.class);
        String profile = "manage-node-allocation";

        EndpointProperty property = new EndpointProperty();
        property.setName("request-message-type");
        property.setValue((request.getClass().getAnnotation(Message.class)).value());
        this.endpointProperties.add(property);

        doReturn(this.capabilityDataList).when(this.capabilityBinder).getCurrentCapabilities();
        doReturn(this.capability).when(this.capabilityData).getCapability();
        doReturn(profile).when(this.capability).getProfile();
        doReturn(this.providerEndpoint).when(this.capability).getProviderEndpoint();
        doReturn(this.endpointProperties).when(this.providerEndpoint).getEndpointProperties();

        this.producer.publishCompleteNodeAllocation(request);

        verify(this.rabbitTemplate).convertAndSend(this.exchange, this.routingKey, request);
    }

    @Test
    public void publishStartedNodeAllocation()
    {
        StartNodeAllocationRequestMessage request = mock(StartNodeAllocationRequestMessage.class);
        String profile = "manage-node-allocation";

        EndpointProperty property = new EndpointProperty();
        property.setName("request-message-type");
        property.setValue((request.getClass().getAnnotation(Message.class)).value());
        this.endpointProperties.add(property);

        doReturn(this.capabilityDataList).when(this.capabilityBinder).getCurrentCapabilities();
        doReturn(this.capability).when(this.capabilityData).getCapability();
        doReturn(profile).when(this.capability).getProfile();
        doReturn(this.providerEndpoint).when(this.capability).getProviderEndpoint();
        doReturn(this.endpointProperties).when(this.providerEndpoint).getEndpointProperties();

        this.producer.publishStartedNodeAllocation(request);

        verify(this.rabbitTemplate).convertAndSend(this.exchange, this.routingKey, request);
    }

    @Test
    public void publishFailedNodeAllocation()
    {
        FailNodeAllocationRequestMessage request = mock(FailNodeAllocationRequestMessage.class);
        String profile = "manage-node-allocation";

        EndpointProperty property = new EndpointProperty();
        property.setName("request-message-type");
        property.setValue((request.getClass().getAnnotation(Message.class)).value());
        this.endpointProperties.add(property);

        doReturn(this.capabilityDataList).when(this.capabilityBinder).getCurrentCapabilities();
        doReturn(this.capability).when(this.capabilityData).getCapability();
        doReturn(profile).when(this.capability).getProfile();
        doReturn(this.providerEndpoint).when(this.capability).getProviderEndpoint();
        doReturn(this.endpointProperties).when(this.providerEndpoint).getEndpointProperties();

        this.producer.publishFailedNodeAllocation(request);

        verify(this.rabbitTemplate).convertAndSend(this.exchange, this.routingKey, request);
    }

    @Test
    public void publishCompleteNodeAllocation_no_capabilities()
    {
        this.executeTest_no_capabilities(mock(CompleteNodeAllocationRequestMessage.class), this.producer::publishCompleteNodeAllocation);
    }

    @Test
    public void publishStartedNodeAllocation_no_capabilities()
    {
        this.executeTest_no_capabilities(mock(StartNodeAllocationRequestMessage.class), this.producer::publishStartedNodeAllocation);
    }

    @Test
    public void publishFailedNodeAllocation_no_capabilities()
    {
        this.executeTest_no_capabilities(mock(FailNodeAllocationRequestMessage.class), this.producer::publishFailedNodeAllocation);
    }

    @Test
    public void publishChangeIdracCredentials()
    {
        this.executeTest(mock(ChangeIdracCredentialsRequestMessage.class), this.producer::publishChangeIdracCredentials);
    }

    @Test
    public void publishChangeIdracCredentials_no_capabilities()
    {
        this.executeTest_no_capabilities(mock(ChangeIdracCredentialsRequestMessage.class), this.producer::publishChangeIdracCredentials);
    }


    @Test
    public void publishNodeInventoryDiscovery()
    {
        this.executeTest(mock(NodeInventoryRequestMessage.class), this.producer::publishNodeInventoryDiscovery);
    }

    @Test
    public void publishDatastoreRename()
    {
        this.executeTest(mock(DatastoreRenameRequestMessage.class), this.producer::publishDatastoreRename);
    }

    @Test
    public void publishDatastoreRename_no_capabilities()
    {
        this.executeTest_no_capabilities(mock(DatastoreRenameRequestMessage.class), this.producer::publishDatastoreRename);
    }

    @Test
    public void publishUpdateSoftwareAcceptance()
    {
        this.executeTest(mock(VCenterUpdateSoftwareAcceptanceRequestMessage.class), this.producer::publishUpdateSoftwareAcceptance);
    }

    @Test
    public void publishUpdateSoftwareAcceptance_no_capabilities()
    {
        this.executeTest_no_capabilities(mock(VCenterUpdateSoftwareAcceptanceRequestMessage.class), this.producer::publishUpdateSoftwareAcceptance);
    }

    @Test
    public void publishValidateProtectionDomain()
    {
        EssValidateProtectionDomainsRequestMessage request = mock(EssValidateProtectionDomainsRequestMessage.class);

        ReflectionTestUtils.setField(this.producer, "essRequestExchange", this.exchange);
        ReflectionTestUtils.setField(this.producer, "essReqRoutingKeyPrefix", this.routingKey);

        this.producer.publishValidateProtectionDomain(request);

        verify(this.rabbitTemplate).convertAndSend(this.exchange, this.routingKey, request);
    }

    public void publishVmPowerOperation()
    {
        this.executeTest(mock(VmPowerOperationsRequestMessage.class), this.producer::publishVmPowerOperation);
    }

    @Test
    public void publishVmPowerOperation_no_capabilities()
    {
        this.executeTest_no_capabilities(mock(VmPowerOperationsRequestMessage.class), this.producer::publishVmPowerOperation);
    }

    @Test
    public void publishConfigureVmNetworkSettings()
    {
        this.executeTest(mock(ConfigureVmNetworkSettingsRequestMessage.class), this.producer::publishConfigureVmNetworkSettings);
    }

    @Test
    public void publishConfigureVmNetworkSettings_no_capabilities()
    {
        this.executeTest_no_capabilities(mock(ConfigureVmNetworkSettingsRequestMessage.class), this.producer::publishConfigureVmNetworkSettings);
    }

    @Test
    public void publishRemoteCommandExecution()
    {
        this.executeTest(mock(RemoteCommandExecutionRequestMessage.class), this.producer::publishRemoteCommandExecution);
    }

    @Test
    public void publishRemoteCommandExecution_no_capabilities()
    {
        this.executeTest_no_capabilities(mock(RemoteCommandExecutionRequestMessage.class), this.producer::publishRemoteCommandExecution);
    }

    @Test
    public void publishAddHostToProtectionDomain()
    {
        this.executeTest(mock(AddHostToProtectionDomainRequestMessage.class), this.producer::publishAddHostToProtectionDomain);
    }

    @Test
    public void publishAddHostToProtectionDomain_no_capabilities()
    {
        this.executeTest_no_capabilities(mock(AddHostToProtectionDomainRequestMessage.class), this.producer::publishAddHostToProtectionDomain);
    }

    @Test
    public void publishUpdateSdcPerformanceProfile()
    {
        this.executeTest(mock(SioSdcUpdatePerformanceProfileRequestMessage.class), this.producer::publishUpdateSdcPerformanceProfile);
    }

    @Test
    public void publishUpdateSdcPerformanceProfile_no_capabilities()
    {
        this.executeTest_no_capabilities(mock(SioSdcUpdatePerformanceProfileRequestMessage.class), this.producer::publishUpdateSdcPerformanceProfile);
    }

    @Test
    public void publishCreateStoragePool()
    {
        this.executeTest(mock(CreateStoragePoolRequestMessage.class), this.producer::publishCreateStoragePool);
    }

    @Test
    public void publishCreateStoragePool_no_capabilities()
    {
        this.executeTest_no_capabilities(mock(CreateStoragePoolRequestMessage.class), this.producer::publishCreateStoragePool);
    }

    private <T> void executeTest(T request, Consumer<T> consumer)
    {
        EndpointProperty property = new EndpointProperty();
        property.setName("request-message-type");
        property.setValue((request.getClass().getAnnotation(Message.class)).value());
        this.endpointProperties.add(property);

        doReturn(this.capabilityDataList).when(this.capabilityBinder).getCurrentCapabilities();
        doReturn(this.capability).when(this.capabilityData).getCapability();
        doReturn(this.providerEndpoint).when(this.capability).getProviderEndpoint();
        doReturn(this.endpointProperties).when(this.providerEndpoint).getEndpointProperties();

        consumer.accept(request);

        verify(this.rabbitTemplate).convertAndSend(this.exchange, this.routingKey, request);
    }

    private <T> void executeTest_no_capabilities(T request, Consumer<T> consumer)
    {
        List<Capability> nullCapabilities = null;

        doReturn(nullCapabilities).when(this.capabilityBinder).getCurrentCapabilities();

        consumer.accept(request);

        verify(this.rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(request.getClass()));
    }
}

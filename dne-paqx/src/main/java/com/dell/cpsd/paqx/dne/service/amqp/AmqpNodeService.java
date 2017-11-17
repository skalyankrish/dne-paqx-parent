/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.service.amqp;

import com.dell.cpsd.ChangeIdracCredentialsRequestMessage;
import com.dell.cpsd.ChangeIdracCredentialsResponseMessage;
import com.dell.cpsd.CompleteNodeAllocationRequestMessage;
import com.dell.cpsd.CompleteNodeAllocationResponseMessage;
import com.dell.cpsd.ConfigurePxeBootError;
import com.dell.cpsd.ConfigurePxeBootRequestMessage;
import com.dell.cpsd.ConfigurePxeBootResponseMessage;
import com.dell.cpsd.ListNodes;
import com.dell.cpsd.MessageProperties;
import com.dell.cpsd.NodeInventoryRequestMessage;
import com.dell.cpsd.NodeInventoryResponseMessage;
import com.dell.cpsd.NodesListed;
import com.dell.cpsd.PxeBootConfig;
import com.dell.cpsd.SetObmSettingsRequestMessage;
import com.dell.cpsd.SetObmSettingsResponseMessage;
import com.dell.cpsd.StartNodeAllocationResponseMessage;
import com.dell.cpsd.StartNodeAllocationRequestMessage;
import com.dell.cpsd.FailNodeAllocationResponseMessage;
import com.dell.cpsd.FailNodeAllocationRequestMessage;
import com.dell.cpsd.common.logging.ILogger;
import com.dell.cpsd.paqx.dne.amqp.config.ServiceConfig;
import com.dell.cpsd.paqx.dne.amqp.producer.DneProducer;
import com.dell.cpsd.paqx.dne.domain.ComponentDetails;
import com.dell.cpsd.paqx.dne.domain.CredentialDetails;
import com.dell.cpsd.paqx.dne.domain.EndpointDetails;
import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.domain.node.DiscoveredNodeInfo;
import com.dell.cpsd.paqx.dne.domain.node.NodeInventory;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOData;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOStoragePool;
import com.dell.cpsd.paqx.dne.domain.vcenter.Host;
import com.dell.cpsd.paqx.dne.domain.vcenter.HostStorageDevice;
import com.dell.cpsd.paqx.dne.domain.vcenter.VCenter;
import com.dell.cpsd.paqx.dne.log.DneLoggingManager;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.amqp.adapter.AddHostToDvSwitchResponseAdapter;
import com.dell.cpsd.paqx.dne.service.amqp.adapter.AddHostToProtectionDomainResponseAdapter;
import com.dell.cpsd.paqx.dne.service.amqp.adapter.AddHostToVCenterResponseAdapter;
import com.dell.cpsd.paqx.dne.service.amqp.adapter.ApplyEsxiLicenseResponseAdapter;
import com.dell.cpsd.paqx.dne.service.amqp.adapter.ChangeIdracCredentialsResponseAdapter;
import com.dell.cpsd.paqx.dne.service.amqp.adapter.ClustersListedResponseAdapter;
import com.dell.cpsd.paqx.dne.service.amqp.adapter.CompleteNodeAllocationResponseAdapter;
import com.dell.cpsd.paqx.dne.service.amqp.adapter.ConfigureObmSettingsResponseAdapter;
import com.dell.cpsd.paqx.dne.service.amqp.adapter.ConfigurePxeBootResponseAdapter;
import com.dell.cpsd.paqx.dne.service.amqp.adapter.ConfigureVmNetworkSettingsResponseAdapter;
import com.dell.cpsd.paqx.dne.service.amqp.adapter.CreateProtectionDomainResponseAdapter;
import com.dell.cpsd.paqx.dne.service.amqp.adapter.CreateStoragePoolAdapter;
import com.dell.cpsd.paqx.dne.service.amqp.adapter.DatastoreRenameResponseAdapter;
import com.dell.cpsd.paqx.dne.service.amqp.adapter.DeployScaleIoVmResponseAdapter;
import com.dell.cpsd.paqx.dne.service.amqp.adapter.DiscoverScaleIoResponseAdapter;
import com.dell.cpsd.paqx.dne.service.amqp.adapter.DiscoverVCenterResponseAdapter;
import com.dell.cpsd.paqx.dne.service.amqp.adapter.EnablePciPassthroughResponseAdapter;
import com.dell.cpsd.paqx.dne.service.amqp.adapter.HostMaintenanceModeResponseAdapter;
import com.dell.cpsd.paqx.dne.service.amqp.adapter.IdracConfigResponseAdapter;
import com.dell.cpsd.paqx.dne.service.amqp.adapter.ListESXiCredentialDetailsResponseAdapter;
import com.dell.cpsd.paqx.dne.service.amqp.adapter.ListScaleIoComponentsResponseAdapter;
import com.dell.cpsd.paqx.dne.service.amqp.adapter.ListVCenterComponentsResponseAdapter;
import com.dell.cpsd.paqx.dne.service.amqp.adapter.NodeInventoryResponseMessageAdapter;
import com.dell.cpsd.paqx.dne.service.amqp.adapter.NodesListedResponseAdapter;
import com.dell.cpsd.paqx.dne.service.amqp.adapter.RebootHostResponseAdapter;
import com.dell.cpsd.paqx.dne.service.amqp.adapter.RemoteCommandExecutionResponseAdapter;
import com.dell.cpsd.paqx.dne.service.amqp.adapter.SetPciPassthroughResponseAdapter;
import com.dell.cpsd.paqx.dne.service.amqp.adapter.SioSdcUpdatePerformanceProfileResponseAdapter;
import com.dell.cpsd.paqx.dne.service.amqp.adapter.SoftwareVibResponseAdapter;
import com.dell.cpsd.paqx.dne.service.amqp.adapter.VCenterUpdateSoftwareAcceptanceResponseAdapter;
import com.dell.cpsd.paqx.dne.service.amqp.adapter.ValidateClusterResponseAdapter;
import com.dell.cpsd.paqx.dne.service.amqp.adapter.ValidateProtectionDomainResponseAdapter;
import com.dell.cpsd.paqx.dne.service.amqp.adapter.ValidateStoragePoolResponseAdapter;
import com.dell.cpsd.paqx.dne.service.amqp.adapter.VmPowerOperationResponseAdapter;
import com.dell.cpsd.paqx.dne.service.amqp.adapter.StartNodeAllocationResponseAdapter;
import com.dell.cpsd.paqx.dne.service.amqp.adapter.FailNodeAllocationResponseAdapter;
import com.dell.cpsd.paqx.dne.service.model.BootDeviceIdracStatus;
import com.dell.cpsd.paqx.dne.service.model.ChangeIdracCredentialsResponse;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.paqx.dne.service.model.DiscoveredNode;
import com.dell.cpsd.paqx.dne.service.model.IdracInfo;
import com.dell.cpsd.paqx.dne.service.model.IdracNetworkSettingsRequest;
import com.dell.cpsd.paqx.dne.service.model.NodeStatus;
import com.dell.cpsd.paqx.dne.service.model.ObmSettingsResponse;
import com.dell.cpsd.paqx.dne.transformers.DiscoveryInfoToVCenterDomainTransformer;
import com.dell.cpsd.paqx.dne.transformers.ScaleIORestToScaleIODomainTransformer;
import com.dell.cpsd.paqx.dne.transformers.StoragePoolEssRequestTransformer;
import com.dell.cpsd.paqx.dne.util.NodeInventoryParsingUtil;
import com.dell.cpsd.rackhd.adapter.model.idrac.IdracNetworkSettings;
import com.dell.cpsd.rackhd.adapter.model.idrac.IdracNetworkSettingsRequestMessage;
import com.dell.cpsd.rackhd.adapter.model.idrac.IdracNetworkSettingsResponseMessage;
import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.exception.ServiceExecutionException;
import com.dell.cpsd.service.common.client.exception.ServiceTimeoutException;
import com.dell.cpsd.service.common.client.rpc.AbstractServiceClient;
import com.dell.cpsd.service.common.client.rpc.DelegatingMessageConsumer;
import com.dell.cpsd.service.common.client.rpc.ServiceRequestCallback;
import com.dell.cpsd.service.engineering.standards.Device;
import com.dell.cpsd.service.engineering.standards.EssValidateProtectionDomainsRequestMessage;
import com.dell.cpsd.service.engineering.standards.EssValidateProtectionDomainsResponseMessage;
import com.dell.cpsd.service.engineering.standards.EssValidateStoragePoolRequestMessage;
import com.dell.cpsd.service.engineering.standards.EssValidateStoragePoolResponseMessage;
import com.dell.cpsd.storage.capabilities.api.AddHostToProtectionDomainRequestMessage;
import com.dell.cpsd.storage.capabilities.api.AddHostToProtectionDomainResponseMessage;
import com.dell.cpsd.storage.capabilities.api.CreateProtectionDomainRequestMessage;
import com.dell.cpsd.storage.capabilities.api.CreateProtectionDomainResponseMessage;
import com.dell.cpsd.storage.capabilities.api.CreateStoragePoolRequestMessage;
import com.dell.cpsd.storage.capabilities.api.CreateStoragePoolResponseMessage;
import com.dell.cpsd.storage.capabilities.api.CredentialNameId;
import com.dell.cpsd.storage.capabilities.api.ListComponentRequestMessage;
import com.dell.cpsd.storage.capabilities.api.ListComponentResponseMessage;
import com.dell.cpsd.storage.capabilities.api.ListStorageRequestMessage;
import com.dell.cpsd.storage.capabilities.api.ListStorageResponseMessage;
import com.dell.cpsd.storage.capabilities.api.ScaleIOComponentDetails;
import com.dell.cpsd.storage.capabilities.api.ScaleIoEndpointDetails;
import com.dell.cpsd.storage.capabilities.api.SioSdcUpdatePerformanceProfileRequestMessage;
import com.dell.cpsd.storage.capabilities.api.SioSdcUpdatePerformanceProfileResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.AddEsxiHostVSphereLicenseRequest;
import com.dell.cpsd.virtualization.capabilities.api.AddEsxiHostVSphereLicenseResponse;
import com.dell.cpsd.virtualization.capabilities.api.AddHostToDvSwitchRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.AddHostToDvSwitchResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.ClusterInfo;
import com.dell.cpsd.virtualization.capabilities.api.ClusterOperationRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.ClusterOperationResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.ConfigureVmNetworkSettingsRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.ConfigureVmNetworkSettingsResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.Credentials;
import com.dell.cpsd.virtualization.capabilities.api.DatastoreRenameRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.DatastoreRenameResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.DeployVMFromTemplateRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.DeployVMFromTemplateResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.DiscoverClusterRequestInfoMessage;
import com.dell.cpsd.virtualization.capabilities.api.DiscoverClusterResponseInfo;
import com.dell.cpsd.virtualization.capabilities.api.DiscoverClusterResponseInfoMessage;
import com.dell.cpsd.virtualization.capabilities.api.DiscoveryRequestInfoMessage;
import com.dell.cpsd.virtualization.capabilities.api.DiscoveryResponseInfoMessage;
import com.dell.cpsd.virtualization.capabilities.api.EnablePCIPassthroughRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.EnablePCIPassthroughResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.HostMaintenanceModeRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.HostMaintenanceModeResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.HostPowerOperationRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.HostPowerOperationResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.ListComponentsRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.ListComponentsResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.ListEsxiCredentialDetailsRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.ListEsxiCredentialDetailsResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.RemoteCommandExecutionRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.RemoteCommandExecutionResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.SoftwareVIBConfigureRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.SoftwareVIBRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.SoftwareVIBResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.UpdatePCIPassthruSVMRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.UpdatePCIPassthruSVMResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.VCenterComponentDetails;
import com.dell.cpsd.virtualization.capabilities.api.VCenterCredentialDetails;
import com.dell.cpsd.virtualization.capabilities.api.VCenterEndpointDetails;
import com.dell.cpsd.virtualization.capabilities.api.VCenterUpdateSoftwareAcceptanceRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.VCenterUpdateSoftwareAcceptanceResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.ValidateVcenterClusterRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.ValidateVcenterClusterResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.VmPowerOperationsRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.VmPowerOperationsResponseMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public class AmqpNodeService extends AbstractServiceClient implements NodeService
{
    /*
     * The logger instance
     */
    private static ILogger LOGGER = DneLoggingManager.getLogger(ServiceConfig.class);

    /*
     * The <code>DelegatingMessageConsumer</code>
     */
    private final DelegatingMessageConsumer consumer;

    /*
     * The <code>DneProducer</code>
     */
    private final DneProducer producer;

    /*
     * The replyTo queue name
     */
    private final String replyTo;

    /*
     * The <code>DataServiceRepository</code>
     */
    private final DataServiceRepository repository;

    /*
     * The <code>DiscoveryInfoToVCenterDomainTransformer</code>
     */
    private final DiscoveryInfoToVCenterDomainTransformer discoveryInfoToVCenterDomainTransformer;

    /*
     * The <code>ScaleIORestToScaleIODomainTransformer</code>
     */
    private final ScaleIORestToScaleIODomainTransformer scaleIORestToScaleIODomainTransformer;

    /*
     * The <code>StoragePoolEssRequestTransformer</code>
     */
    private final StoragePoolEssRequestTransformer storagePoolEssRequestTransformer;

    @Value("${rackhd.boot.proto.share.name}")
    private String shareName;

    @Value("${rackhd.boot.proto.share.type}")
    private Integer shareType;

    @Value("${rackhd.boot.proto.fqdds}")
    private String[] fqdds;

    @Value("${rackhd.boot.proto.name}")
    private String bootProtoName;

    @Value("${rackhd.boot.proto.value}")
    private String bootProtoValue;

    private static final long timeout            = 240000L;

    /**
     * AmqpNodeService constructor.
     *
     * @param consumer                                - The <code>DelegatingMessageConsumer</code> instance.
     * @param producer                                - The <code>DneProducer</code> instance.
     * @param replyTo                                 - The replyTo queue name.
     * @param repository                              repository
     * @param discoveryInfoToVCenterDomainTransformer discoveryInfoToVCenterDomainTransformer
     * @param scaleIORestToScaleIODomainTransformer   scaleIORestToScaleIODomainTransformer
     * @since 1.0
     */
    public AmqpNodeService( DelegatingMessageConsumer consumer, DneProducer producer, String replyTo,
            final DataServiceRepository repository, final DiscoveryInfoToVCenterDomainTransformer discoveryInfoToVCenterDomainTransformer,
            final ScaleIORestToScaleIODomainTransformer scaleIORestToScaleIODomainTransformer,
            final StoragePoolEssRequestTransformer storagePoolEssRequestTransformer)
    {
        super(LOGGER);

        this.consumer = consumer;
        this.producer = producer;
        this.replyTo = replyTo;
        this.repository = repository;
        this.discoveryInfoToVCenterDomainTransformer = discoveryInfoToVCenterDomainTransformer;
        this.scaleIORestToScaleIODomainTransformer = scaleIORestToScaleIODomainTransformer;
        this.storagePoolEssRequestTransformer = storagePoolEssRequestTransformer;
        initCallbacks();
    }

    /*
     * Initialize message consumer adapters.
     *
     * @since 1.0
     */
    private void initCallbacks()
    {
        this.consumer.addAdapter(new NodesListedResponseAdapter(this));
        this.consumer.addAdapter(new ClustersListedResponseAdapter(this));
        this.consumer.addAdapter(new CompleteNodeAllocationResponseAdapter(this));
        this.consumer.addAdapter(new StartNodeAllocationResponseAdapter(this));
        this.consumer.addAdapter(new FailNodeAllocationResponseAdapter(this));
        this.consumer.addAdapter(new IdracConfigResponseAdapter(this));
        this.consumer.addAdapter(new ChangeIdracCredentialsResponseAdapter(this));
        this.consumer.addAdapter(new ConfigurePxeBootResponseAdapter(this));
        this.consumer.addAdapter(new ConfigureObmSettingsResponseAdapter(this));
        this.consumer.addAdapter(new ValidateClusterResponseAdapter(this));
        this.consumer.addAdapter(new ValidateStoragePoolResponseAdapter(this));
        this.consumer.addAdapter(new ValidateProtectionDomainResponseAdapter(this));
        this.consumer.addAdapter(new ListScaleIoComponentsResponseAdapter(this));
        this.consumer.addAdapter(new ListVCenterComponentsResponseAdapter(this));
        this.consumer.addAdapter(new DiscoverScaleIoResponseAdapter(this));
        this.consumer.addAdapter(new DiscoverVCenterResponseAdapter(this));
        this.consumer.addAdapter(new AddHostToVCenterResponseAdapter(this));
        this.consumer.addAdapter(new SoftwareVibResponseAdapter(this));
        this.consumer.addAdapter(new AddHostToDvSwitchResponseAdapter(this));
        this.consumer.addAdapter(new DeployScaleIoVmResponseAdapter(this));
        this.consumer.addAdapter(new EnablePciPassthroughResponseAdapter(this));
        this.consumer.addAdapter(new RebootHostResponseAdapter(this));
        this.consumer.addAdapter(new SetPciPassthroughResponseAdapter(this));
        this.consumer.addAdapter(new ApplyEsxiLicenseResponseAdapter(this));
        this.consumer.addAdapter(new ListESXiCredentialDetailsResponseAdapter(this));
        this.consumer.addAdapter(new HostMaintenanceModeResponseAdapter(this));
        this.consumer.addAdapter(new NodeInventoryResponseMessageAdapter(this));
        this.consumer.addAdapter(new DatastoreRenameResponseAdapter(this));
        this.consumer.addAdapter(new VCenterUpdateSoftwareAcceptanceResponseAdapter(this));
        this.consumer.addAdapter(new VmPowerOperationResponseAdapter(this));
        this.consumer.addAdapter(new ConfigureVmNetworkSettingsResponseAdapter(this));
        this.consumer.addAdapter(new RemoteCommandExecutionResponseAdapter(this));
        this.consumer.addAdapter(new AddHostToProtectionDomainResponseAdapter(this));
        this.consumer.addAdapter(new SioSdcUpdatePerformanceProfileResponseAdapter(this));
        this.consumer.addAdapter(new CreateStoragePoolAdapter(this));
        this.consumer.addAdapter(new CreateProtectionDomainResponseAdapter(this));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IdracInfo idracNetworkSettings(IdracNetworkSettingsRequest idracNetworkSettingsRequest)
    {
        IdracInfo idracInfo = new IdracInfo();

        try
        {
            IdracNetworkSettingsRequestMessage idracNetworkSettingsRequestMessage = new IdracNetworkSettingsRequestMessage();
            com.dell.cpsd.rackhd.adapter.rabbitmq.MessageProperties messageProperties = new com.dell.cpsd.rackhd.adapter.rabbitmq.MessageProperties();
            messageProperties.setCorrelationId(UUID.randomUUID().toString());
            messageProperties.setTimestamp(Calendar.getInstance().getTime());
            messageProperties.setReplyTo(replyTo);

            idracNetworkSettingsRequestMessage.setMessageProperties(messageProperties);

            IdracNetworkSettings idracNetworkSettings = new IdracNetworkSettings();

            idracNetworkSettings.setUuid(idracNetworkSettingsRequest.getUuid());
            idracNetworkSettings.setIpAddress(idracNetworkSettingsRequest.getIdracIpAddress());
            idracNetworkSettings.setGateway(idracNetworkSettingsRequest.getIdracGatewayIpAddress());
            idracNetworkSettings.setNetmask(idracNetworkSettingsRequest.getIdracSubnetMask());

            idracNetworkSettingsRequestMessage.setIdracNetworkSettings(idracNetworkSettings);

            ServiceResponse<?> response = processRequest(timeout, new ServiceRequestCallback()
            {
                @Override
                public String getRequestId()
                {
                    return messageProperties.getCorrelationId();
                }

                @Override
                public void executeRequest(String requestId) throws Exception
                {
                    producer.publishIdracNetwokSettings(idracNetworkSettingsRequestMessage);
                }
            });
            IdracNetworkSettingsResponseMessage resp = processResponse(response, IdracNetworkSettingsResponseMessage.class);

            if (resp != null)
            {
                if (resp.getMessageProperties() != null)
                {
                    if (resp.getIdracNetworkSettingsResponse() != null)
                    {
                        LOGGER.info("Response from amqp ipAddress: " + resp.getIdracNetworkSettingsResponse().getIpAddress());
                        LOGGER.info("Response from amqp subnet: " + resp.getIdracNetworkSettingsResponse().getNetmask());
                        LOGGER.info("Response from amqp gateway: " + resp.getIdracNetworkSettingsResponse().getGateway());
                        LOGGER.info("Response from amqp uuId: " + resp.getIdracNetworkSettingsResponse().getUuid());

                        if ("SUCCESS".equalsIgnoreCase(resp.getIdracNetworkSettingsResponse().getMessage()))
                        {
                            idracInfo.setIdracIpAddress(resp.getIdracNetworkSettingsResponse().getIpAddress());
                            idracInfo.setIdracSubnetMask(resp.getIdracNetworkSettingsResponse().getNetmask());
                            idracInfo.setIdracGatewayIpAddress(resp.getIdracNetworkSettingsResponse().getGateway());
                            idracInfo.setNodeId(resp.getIdracNetworkSettingsResponse().getUuid());

                        }
                        else
                        {
                            LOGGER.error(
                                    "Error response from configure idrac settings: " + resp.getIdracNetworkSettingsResponse().getMessage());
                        }
                        idracInfo.setMessage(resp.getIdracNetworkSettingsResponse().getMessage());
                    }
                }
            }
        }
        catch (Exception e)
        {
            LOGGER.error("Exception in idracNetworkSettings: ", e);
        }

        return idracInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DiscoveredNode> listDiscoveredNodes() throws ServiceTimeoutException, ServiceExecutionException
    {
        com.dell.cpsd.MessageProperties messageProperties = new com.dell.cpsd.MessageProperties();
        messageProperties.setCorrelationId(UUID.randomUUID().toString());
        messageProperties.setTimestamp(Calendar.getInstance().getTime());
        messageProperties.setReplyTo(replyTo);

        ListNodes request = new ListNodes(messageProperties, Collections.emptyList());
        ServiceResponse<?> response = processRequest(timeout, new ServiceRequestCallback()
        {
            @Override
            public String getRequestId()
            {
                return messageProperties.getCorrelationId();
            }

            @Override
            public void executeRequest(String requestId) throws Exception
            {
                producer.publishListNodes(request);
            }
        });

        NodesListed nodes = processResponse(response, NodesListed.class);

        if (nodes != null)
        {
            if (nodes.getDiscoveredNodes() != null)
            {
                return nodes.getDiscoveredNodes().stream()
                        .filter(d -> d.getAllocationStatus() == com.dell.cpsd.DiscoveredNode.AllocationStatus.DISCOVERED)
                        .map(d -> new DiscoveredNode(d.getConvergedUuid(), d.getAllocationStatus(), d.getSerial(), d.getProduct(), d.getVendor()))
                        .collect(Collectors.toList());
            }
        }

        return Collections.emptyList();
    }

    @Override
    @Transactional
    public List<DiscoveredNodeInfo> listDiscoveredNodeInfo()
            throws ServiceTimeoutException, ServiceExecutionException, JsonProcessingException
    {
        List<DiscoveredNode> discoveredNodes = listDiscoveredNodes();
        List<DiscoveredNodeInfo> retList = new ArrayList<>();

        if (discoveredNodes != null && !discoveredNodes.isEmpty())
        {
            for (DiscoveredNode node : discoveredNodes)
            {
                DiscoveredNodeInfo discoveredNodeInfo = new DiscoveredNodeInfo(node.getProduct(), null, node.getProduct(), null,
                        node.getSerial(), node.getConvergedUuid());
                discoveredNodeInfo.setNodeStatus(NodeStatus.valueOf(node.getNodeStatus().toString()));
                discoveredNodeInfo.setVendor(node.getVendor());
                retList.add(discoveredNodeInfo);
            }

            LOGGER.info("Listing DiscoveredNodeInfo data ...");
        }
        else
        {
            LOGGER.info("There are no Discovered Nodes");
        }


        return retList;
    }

    @Override
    public DiscoveredNodeInfo getFirstDiscoveredNodeInfo() throws ServiceTimeoutException, ServiceExecutionException, JsonProcessingException {
        // since symphony doesn't have mechanism to auto delete obsolete node,
        // there will be dangling node in node discovery paqx and if it's the first one,
        // it will cause failure to get nodeInventory so just get all and return first one.
        List<DiscoveredNodeInfo> nodeList = this.listDiscoveredNodeInfo();

        if ( nodeList.size() != 0)
            return nodeList.get(0);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ClusterInfo> listClusters() throws ServiceTimeoutException, ServiceExecutionException
    {
        com.dell.cpsd.virtualization.capabilities.api.MessageProperties messageProperties = new com.dell.cpsd.virtualization.capabilities.api.MessageProperties();
        messageProperties.setCorrelationId(UUID.randomUUID().toString());
        messageProperties.setTimestamp(Calendar.getInstance().getTime());
        messageProperties.setReplyTo(replyTo);

        final ComponentEndpointIds componentEndpointIds = repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER");
        DiscoverClusterRequestInfoMessage request = new DiscoverClusterRequestInfoMessage();
        if (componentEndpointIds != null)
        {
            request.setComponentEndpointIds(
                    new com.dell.cpsd.virtualization.capabilities.api.ComponentEndpointIds(componentEndpointIds.getComponentUuid(),
                            componentEndpointIds.getEndpointUuid(), componentEndpointIds.getCredentialUuid()));
            request.setCredentials(new Credentials(componentEndpointIds.getEndpointUrl(), null, null));
        }

        request.setMessageProperties(messageProperties);
        ServiceResponse<?> response = processRequest(timeout, new ServiceRequestCallback()
        {
            @Override
            public String getRequestId()
            {
                return messageProperties.getCorrelationId();
            }

            @Override
            public void executeRequest(String requestId) throws Exception
            {
                LOGGER.info("publish list cluster request message");
                producer.publishDiscoverClusters(request);
            }
        });

        DiscoverClusterResponseInfoMessage responseInfo = processResponse(response, DiscoverClusterResponseInfoMessage.class);

        if ( responseInfo!= null && responseInfo.getStatus() == DiscoverClusterResponseInfoMessage.Status.SUCCESS)
        {
            DiscoverClusterResponseInfo clusterResponseInfo = responseInfo.getDiscoverClusterResponseInfo();
            return clusterResponseInfo != null ? clusterResponseInfo.getClusters() : Collections.emptyList();
        }
        else
        {
            LOGGER.error("Failed to get cluster from vcenter");
            throw new ServiceExecutionException("Failed to get cluster from vcenter");
        }
    }

    @Override
    public List<ScaleIOData> listScaleIOData()
    {
        LOGGER.info("Listing scaleIO data ...");

        ScaleIOData scaleIOData = repository.getScaleIoData();

        return Collections.singletonList(scaleIOData);
    }

    /**
     * Implementation of storage pool validation
     *
     * @param scaleIOStoragePools
     * @param newDevices
     * @return
     * @throws ServiceTimeoutException
     * @throws ServiceExecutionException
     */
    @Override
    public EssValidateStoragePoolResponseMessage validateStoragePools(List<ScaleIOStoragePool> scaleIOStoragePools, List<Device> newDevices,
            Map<String, Map<String, HostStorageDevice>> hostToStorageDeviceMap) throws ServiceTimeoutException, ServiceExecutionException
    {
        com.dell.cpsd.service.engineering.standards.MessageProperties messageProperties = new com.dell.cpsd.service.engineering.standards.MessageProperties();
        messageProperties.setCorrelationId(UUID.randomUUID().toString());
        messageProperties.setTimestamp(Calendar.getInstance().getTime());
        messageProperties.setReplyTo(replyTo);

        EssValidateStoragePoolRequestMessage storageRequestMessage = storagePoolEssRequestTransformer
                .transform(scaleIOStoragePools, hostToStorageDeviceMap);
        storageRequestMessage.setMessageProperties(messageProperties);
        storageRequestMessage.setNewDevices(newDevices);

        ServiceResponse<?> response = processRequest(timeout, new ServiceRequestCallback()
        {
            @Override
            public String getRequestId()
            {
                return messageProperties.getCorrelationId();
            }

            @Override
            public void executeRequest(String requestId) throws Exception
            {
                LOGGER.info("publish validate ess storage request message");
                producer.publishValidateStorage(storageRequestMessage);
            }
        });

        return processResponse(response, EssValidateStoragePoolResponseMessage.class);

    }

    /**
     * Implementation of protection domain validation
     *
     * @param essValidateProtectionDomainsRequestMessage
     * @return
     * @throws ServiceTimeoutException
     * @throws ServiceExecutionException
     */
    @Override
    public EssValidateProtectionDomainsResponseMessage validateProtectionDomains(EssValidateProtectionDomainsRequestMessage essValidateProtectionDomainsRequestMessage)
            throws ServiceTimeoutException, ServiceExecutionException
    {
        com.dell.cpsd.service.engineering.standards.MessageProperties messageProperties = new com.dell.cpsd.service.engineering.standards.MessageProperties();
        messageProperties.setCorrelationId(UUID.randomUUID().toString());
        messageProperties.setTimestamp(Calendar.getInstance().getTime());
        messageProperties.setReplyTo(replyTo);

        essValidateProtectionDomainsRequestMessage.setMessageProperties(messageProperties);
        ServiceResponse<?> response = processRequest(timeout, new ServiceRequestCallback()
        {
            @Override
            public String getRequestId()
            {
                return messageProperties.getCorrelationId();
            }

            @Override
            public void executeRequest(String requestId) throws Exception
            {
                LOGGER.info("publish validate ess protection domain request message");
                producer.publishValidateProtectionDomain(essValidateProtectionDomainsRequestMessage);
            }
        });

        return processResponse(response, EssValidateProtectionDomainsResponseMessage.class);
    }


    @Override
    public ValidateVcenterClusterResponseMessage validateClusters(List<ClusterInfo> clusterInfoList)
            throws ServiceTimeoutException, ServiceExecutionException
    {
        com.dell.cpsd.virtualization.capabilities.api.MessageProperties messageProperties = new com.dell.cpsd.virtualization.capabilities.api.MessageProperties();
        messageProperties.setCorrelationId(UUID.randomUUID().toString());
        messageProperties.setTimestamp(Calendar.getInstance().getTime());
        messageProperties.setReplyTo(replyTo);

        ValidateVcenterClusterRequestMessage request = new ValidateVcenterClusterRequestMessage();

        final ComponentEndpointIds componentEndpointIds = repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER");
        if (componentEndpointIds != null)
        {
            request.setComponentEndpointIds(
                    new com.dell.cpsd.virtualization.capabilities.api.ComponentEndpointIds(componentEndpointIds.getComponentUuid(),
                            componentEndpointIds.getEndpointUuid(), componentEndpointIds.getCredentialUuid()));
        }

        request.setMessageProperties(messageProperties);
        request.setDiscoverClusterResponseInfo(new DiscoverClusterResponseInfo(clusterInfoList));
        ServiceResponse<?> response = processRequest(timeout, new ServiceRequestCallback()
        {
            @Override
            public String getRequestId()
            {
                return messageProperties.getCorrelationId();
            }

            @Override
            public void executeRequest(String requestId) throws Exception
            {
                LOGGER.info("publish validate cluster request message");
                producer.publishValidateClusters(request);
            }
        });

        return processResponse(response, ValidateVcenterClusterResponseMessage.class);
    }

    @Override
    public boolean notifyNodeAllocationStatus(String elementIdentifier,String action) throws ServiceTimeoutException, ServiceExecutionException
    {
        com.dell.cpsd.MessageProperties messageProperties = new com.dell.cpsd.MessageProperties();
        messageProperties.setCorrelationId(UUID.randomUUID().toString());
        messageProperties.setTimestamp(Calendar.getInstance().getTime());
        messageProperties.setReplyTo(replyTo);

        if("Completed".equalsIgnoreCase(action))
        {
            return notifyNodeAllocationComplete(elementIdentifier,messageProperties);
        } else if ("Started".equalsIgnoreCase(action))
        {
            return notifyNodeAllocationStarted(elementIdentifier,messageProperties);
        } else if ("failed".equalsIgnoreCase(action))
        {
            return notifyNodeAllocationFailed(elementIdentifier,messageProperties);
        }

        return false;
    }

    private boolean notifyNodeAllocationStarted(String elementIdentifier, com.dell.cpsd.MessageProperties messageProperties) throws ServiceTimeoutException, ServiceExecutionException
    {

        StartNodeAllocationRequestMessage request = new StartNodeAllocationRequestMessage(messageProperties, elementIdentifier, null);

        ServiceResponse<?> response = processRequest(timeout, new ServiceRequestCallback()
        {
            @Override
            public String getRequestId()
            {
                return messageProperties.getCorrelationId();
            }

            @Override
            public void executeRequest(String requestId) throws Exception
            {
                producer.publishStartedNodeAllocation(request);
            }
        });

        StartNodeAllocationResponseMessage responseInfo = processResponse(response, StartNodeAllocationResponseMessage.class);

        if ( responseInfo != null && StartNodeAllocationResponseMessage.Status.FAILED.equals(responseInfo.getStatus()))
        {
            LOGGER.error("Error response from notify node allocation started: " + responseInfo.getNodeAllocationErrors());
        }

        return responseInfo!=null && StartNodeAllocationResponseMessage.Status.SUCCESS.equals(responseInfo.getStatus());
    }

    private boolean notifyNodeAllocationComplete(String elementIdentifier, com.dell.cpsd.MessageProperties messageProperties) throws ServiceTimeoutException, ServiceExecutionException
    {

        CompleteNodeAllocationRequestMessage request = new CompleteNodeAllocationRequestMessage(messageProperties, elementIdentifier, null);

        ServiceResponse<?> response = processRequest(timeout, new ServiceRequestCallback()
        {
            @Override
            public String getRequestId()
            {
                return messageProperties.getCorrelationId();
            }

            @Override
            public void executeRequest(String requestId) throws Exception
            {
                producer.publishCompleteNodeAllocation(request);
            }
        });

        CompleteNodeAllocationResponseMessage responseInfo = processResponse(response, CompleteNodeAllocationResponseMessage.class);

        if ( responseInfo != null && CompleteNodeAllocationResponseMessage.Status.FAILED.equals(responseInfo.getStatus()))
        {
            LOGGER.error("Error response from notify node allocation complete: " + responseInfo.getNodeAllocationErrors());
        }

        return responseInfo!=null && CompleteNodeAllocationResponseMessage.Status.SUCCESS.equals(responseInfo.getStatus());
    }


    private boolean notifyNodeAllocationFailed(String elementIdentifier, com.dell.cpsd.MessageProperties messageProperties) throws ServiceTimeoutException, ServiceExecutionException
    {

        FailNodeAllocationRequestMessage request = new FailNodeAllocationRequestMessage(messageProperties, elementIdentifier, null);

        ServiceResponse<?> response = processRequest(timeout, new ServiceRequestCallback()
        {
            @Override
            public String getRequestId()
            {
                return messageProperties.getCorrelationId();
            }

            @Override
            public void executeRequest(String requestId) throws Exception
            {
                producer.publishFailedNodeAllocation(request);
            }
        });

        FailNodeAllocationResponseMessage responseInfo = processResponse(response, FailNodeAllocationResponseMessage.class);

        if ( responseInfo != null && FailNodeAllocationResponseMessage.Status.FAILED.equals(responseInfo.getStatus()))
        {
            LOGGER.error("Error response from notify node allocation failed: " + responseInfo.getNodeAllocationErrors());
        }

        return responseInfo!=null && FailNodeAllocationResponseMessage.Status.SUCCESS.equals(responseInfo.getStatus());
    }

    /**
     * Process a RPC response message.
     *
     * @param response         - The <code>ServiceResponse</code> to process.
     * @param expectedResponse - The expected response <code>Class</code>
     * @return The response.
     * @since 1.0
     */
    private <R> R processResponse(ServiceResponse<?> response, Class<R> expectedResponse)
    {
        Object responseMessage = response.getResponse();
        if (responseMessage == null)
        {
            return null;
        }

        if (expectedResponse.isAssignableFrom(responseMessage.getClass()))
        {
            return (R) responseMessage;
        }
        else
        {
            throw new UnsupportedOperationException("Unexpected response message: " + responseMessage);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ChangeIdracCredentialsResponse changeIdracCredentials(String uuid)
    {
        ChangeIdracCredentialsResponse responseMessage = new ChangeIdracCredentialsResponse();

        try
        {
            ChangeIdracCredentialsRequestMessage changeIdracCredentialsRequestMessage = new ChangeIdracCredentialsRequestMessage();
            com.dell.cpsd.MessageProperties messageProperties = new com.dell.cpsd.MessageProperties();
            messageProperties.setCorrelationId(UUID.randomUUID().toString());
            messageProperties.setTimestamp(Calendar.getInstance().getTime());
            messageProperties.setReplyTo(replyTo);

            changeIdracCredentialsRequestMessage.setMessageProperties(messageProperties);
            changeIdracCredentialsRequestMessage.setUuid(uuid);

            LOGGER.info("Sending Change Idrac Credentials request with correlation id: " + messageProperties.getCorrelationId());

            ServiceResponse<?> response = processRequest(timeout, new ServiceRequestCallback()
            {
                @Override
                public String getRequestId()
                {
                    return messageProperties.getCorrelationId();
                }

                @Override
                public void executeRequest(String requestId) throws Exception
                {
                    producer.publishChangeIdracCredentials(changeIdracCredentialsRequestMessage);
                }
            });

            ChangeIdracCredentialsResponseMessage resp = processResponse(response, ChangeIdracCredentialsResponseMessage.class);

            if (resp != null)
            {
                if (resp.getMessageProperties() != null)
                {
                    if (resp.getStatus() != null)
                    {
                        LOGGER.info("Response for Change Idrac Credentials: " + resp.getStatus());
                        responseMessage.setNodeId(uuid);

                        if ("SUCCESS".equalsIgnoreCase(resp.getStatus().toString()))
                        {
                            responseMessage.setMessage("SUCCESS");
                        }
                        else
                        {
                            LOGGER.error("Error response from change idrac credentials: " + resp.getChangeIdracCredentialsErrors());
                            responseMessage.setMessage("Error while setting new credentials to the node " + uuid);
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            LOGGER.error("Exception in change idrac credentials: ", e);
        }

        return responseMessage;
    }

    @Override
    public BootDeviceIdracStatus configurePxeBoot(String uuid, String ipAddress)
    {
        BootDeviceIdracStatus bootDeviceIdracStatus = new BootDeviceIdracStatus();

        try
        {
            ConfigurePxeBootRequestMessage configurePxeBootRequestMessage = new ConfigurePxeBootRequestMessage();

            com.dell.cpsd.MessageProperties messageProperties = new com.dell.cpsd.MessageProperties();
            messageProperties.setCorrelationId(UUID.randomUUID().toString());
            messageProperties.setTimestamp(Calendar.getInstance().getTime());
            messageProperties.setReplyTo(replyTo);
            configurePxeBootRequestMessage.setMessageProperties(messageProperties);

            configurePxeBootRequestMessage.setUuid(uuid);
            configurePxeBootRequestMessage.setIpAddress(ipAddress);

            PxeBootConfig pxeBootConfig = new PxeBootConfig();
            pxeBootConfig.setProtoValue(bootProtoValue);
            pxeBootConfig.setShareType(shareType);
            pxeBootConfig.setShareName(shareName);
            pxeBootConfig.setProtoName(bootProtoName);
            pxeBootConfig.setNicFqdds(Arrays.asList(fqdds));

            configurePxeBootRequestMessage.setPxeBootConfig(pxeBootConfig);

            ServiceResponse<?> response = processRequest(timeout, new ServiceRequestCallback()
            {
                @Override
                public String getRequestId()
                {
                    return messageProperties.getCorrelationId();
                }

                @Override
                public void executeRequest(String requestId) throws Exception
                {
                    producer.publishConfigurePxeBoot(configurePxeBootRequestMessage);
                }
            });

            ConfigurePxeBootResponseMessage resp = processResponse(response, ConfigurePxeBootResponseMessage.class);
            if (resp != null)
            {
                if (resp.getMessageProperties() != null)
                {
                    if (resp.getStatus() != null)
                    {
                        LOGGER.info("Response message is: " + resp.getStatus().toString());

                        bootDeviceIdracStatus.setStatus(resp.getStatus().toString());
                        List<ConfigurePxeBootError> errors = resp.getConfigurePxeBootErrors();
                        if (!CollectionUtils.isEmpty(errors))
                        {
                            List<String> errorMsgs = errors.stream().map(ConfigurePxeBootError::getMessage).collect(Collectors.toList());
                            bootDeviceIdracStatus.setErrors(errorMsgs);
                        }
                    }
                }

            }
        }
        catch (Exception ex)
        {
            LOGGER.error("Exception in boot order sequence: ", ex);
            bootDeviceIdracStatus.setStatus(ConfigurePxeBootResponseMessage.Status.FAILED.toString());
            bootDeviceIdracStatus.setErrors(Arrays.asList(ex.getMessage()));
        }

        return bootDeviceIdracStatus;
    }


    @Override
    public ObmSettingsResponse obmSettingsResponse(SetObmSettingsRequestMessage setObmSettingsRequestMessage)
    {

        ObmSettingsResponse obmSettingsResponse = new ObmSettingsResponse();

        try
        {
            MessageProperties messageProperties = new MessageProperties();
            messageProperties.setCorrelationId(UUID.randomUUID().toString());
            messageProperties.setTimestamp(Calendar.getInstance().getTime());
            messageProperties.setReplyTo(replyTo);
            setObmSettingsRequestMessage.setMessageProperties(messageProperties);

            ServiceResponse<?> response = processRequest(timeout, new ServiceRequestCallback()
            {
                @Override
                public String getRequestId()
                {
                    return messageProperties.getCorrelationId();
                }

                @Override
                public void executeRequest(String requestId) throws Exception
                {
                    producer.publishConfigureObmSettings(setObmSettingsRequestMessage);
                }
            });

            SetObmSettingsResponseMessage resp = processResponse(response, SetObmSettingsResponseMessage.class);
            if (resp != null)
            {
                if (resp.getMessageProperties() != null)
                {
                    if (resp.getStatus() != null)
                    {
                        LOGGER.info("Response message is: " + resp.getStatus().toString());

                        obmSettingsResponse.setStatus(resp.getStatus().toString());
                    }
                }
            }
        }
        catch (Exception e)
        {
            LOGGER.error("Exception in boot order sequence: ", e);
        }

        return obmSettingsResponse;
    }

    @Override
    public boolean requestScaleIoComponents()
    {
        final List<ComponentDetails> componentEndpointDetailsListResponse = new ArrayList<>();

        try
        {
            final ListComponentRequestMessage requestMessage = new ListComponentRequestMessage();
            final String correlationId = UUID.randomUUID().toString();
            requestMessage
                    .setMessageProperties(new com.dell.cpsd.storage.capabilities.api.MessageProperties(new Date(), correlationId, replyTo));

            ServiceResponse<?> callbackResponse = processRequest(timeout, new ServiceRequestCallback()
            {
                @Override
                public String getRequestId()
                {
                    return correlationId;
                }

                @Override
                public void executeRequest(String requestId) throws Exception
                {
                    producer.publishListScaleIoComponents(requestMessage);
                }
            });

            ListComponentResponseMessage responseMessage = processResponse(callbackResponse, ListComponentResponseMessage.class);

            if (responseMessage != null && responseMessage.getMessageProperties() != null)
            {
                final List<ScaleIOComponentDetails> scaleIOComponentDetailsList = responseMessage.getComponents();

                if (scaleIOComponentDetailsList != null)
                {
                    scaleIOComponentDetailsList.stream().filter(Objects::nonNull).forEach(scaleIOComponentDetails -> {

                        final ComponentDetails componentDetails = new ComponentDetails();

                        componentDetails.setComponentUuid(scaleIOComponentDetails.getComponentUuid());
                        componentDetails.setElementType(scaleIOComponentDetails.getElementType());
                        componentDetails.setComponentType(scaleIOComponentDetails.getElementType());
                        componentDetails.setIdentifier(scaleIOComponentDetails.getIdentifier());

                        final List<ScaleIoEndpointDetails> endpointDetailsList = scaleIOComponentDetails.getEndpoints();

                        if (endpointDetailsList != null)
                        {
                            endpointDetailsList.stream().filter(Objects::nonNull).forEach(scaleIoEndpointDetails -> {
                                final EndpointDetails endpointDetails = new EndpointDetails();
                                endpointDetails.setEndpointUrl(scaleIoEndpointDetails.getEndpointUrl());
                                endpointDetails.setEndpointUuid(scaleIoEndpointDetails.getEndpointUuid());
                                endpointDetails.setIdentifier(scaleIoEndpointDetails.getIdentifier());
                                endpointDetails.setType(scaleIoEndpointDetails.getElementType());
                                final List<CredentialNameId> credentialUuids = scaleIoEndpointDetails.getCredentialNameIds();
                                credentialUuids.forEach(
                                        credential -> addCredentialsToEndpoint(endpointDetails, credential.getCredentialName(),
                                                credential.getCredentialUuid()));
                                endpointDetails.setComponentDetails(componentDetails);
                                componentDetails.getEndpointDetails().add(endpointDetails);
                            });
                        }

                        componentEndpointDetailsListResponse.add(componentDetails);

                    });

                    return repository.saveScaleIoComponentDetails(componentEndpointDetailsListResponse);
                }
            }
            else
            {
                LOGGER.error("Response was null");
            }

        }
        catch (Exception e)
        {
            LOGGER.error("Exception occurred", e);
        }

        return false;
    }

    private void addCredentialsToEndpoint(final EndpointDetails endpointDetails, final String credentialName, final String credentialUuid)
    {
        final CredentialDetails credentialDetails = new CredentialDetails();
        credentialDetails.setCredentialName(credentialName);
        credentialDetails.setCredentialUuid(credentialUuid);
        credentialDetails.setEndpointDetails(endpointDetails);
        endpointDetails.getCredentialDetailsList().add(credentialDetails);
    }

    @Override
    public boolean requestVCenterComponents()
    {
        final List<ComponentDetails> componentEndpointDetailsListResponse = new ArrayList<>();

        try
        {
            final ListComponentsRequestMessage requestMessage = new ListComponentsRequestMessage();
            final String correlationId = UUID.randomUUID().toString();
            requestMessage.setMessageProperties(
                    new com.dell.cpsd.virtualization.capabilities.api.MessageProperties(new Date(), correlationId, replyTo));

            ServiceResponse<?> callbackResponse = processRequest(timeout, new ServiceRequestCallback()
            {
                @Override
                public String getRequestId()
                {
                    return correlationId;
                }

                @Override
                public void executeRequest(String requestId) throws Exception
                {
                    producer.publishListVCenterComponents(requestMessage);
                }
            });

            ListComponentsResponseMessage responseMessage = processResponse(callbackResponse, ListComponentsResponseMessage.class);

            if (responseMessage != null && responseMessage.getMessageProperties() != null)
            {
                final List<VCenterComponentDetails> vCenterComponentDetailsList = responseMessage.getVcenterComponentDetails();

                if (vCenterComponentDetailsList != null)
                {
                    vCenterComponentDetailsList.stream().filter(Objects::nonNull).forEach(vcenterComponentDetails -> {

                        final ComponentDetails componentDetails = new ComponentDetails();

                        componentDetails.setComponentUuid(vcenterComponentDetails.getComponentUuid());
                        componentDetails.setElementType(vcenterComponentDetails.getElementType());
                        componentDetails.setComponentType("VCENTER");
                        componentDetails.setIdentifier(vcenterComponentDetails.getIdentifier());

                        final List<VCenterEndpointDetails> endpointDetailsList = vcenterComponentDetails.getEndpoints();

                        if (endpointDetailsList != null)
                        {
                            endpointDetailsList.stream().filter(Objects::nonNull).forEach(vCenterEndpointDetails -> {
                                final EndpointDetails endpointDetails = new EndpointDetails();
                                endpointDetails.setEndpointUrl(vCenterEndpointDetails.getEndpointUrl());
                                endpointDetails.setEndpointUuid(vCenterEndpointDetails.getEndpointUuid());
                                endpointDetails.setIdentifier(vCenterEndpointDetails.getIdentifier());
                                endpointDetails.setType(vCenterEndpointDetails.getType());
                                final List<VCenterCredentialDetails> credentialDetailsList = vCenterEndpointDetails.getCredentialDetails();
                                credentialDetailsList.stream().filter(Objects::nonNull).forEach(
                                        credential -> addCredentialsToEndpoint(endpointDetails, credential.getCredentialName(),
                                                credential.getCredentialUuid()));
                                endpointDetails.setComponentDetails(componentDetails);
                                componentDetails.getEndpointDetails().add(endpointDetails);
                            });
                        }

                        componentEndpointDetailsListResponse.add(componentDetails);

                    });

                    return repository.saveVCenterComponentDetails(componentEndpointDetailsListResponse);
                }
            }
            else
            {
                LOGGER.error("Response was null");
            }

        }
        catch (Exception e)
        {
            LOGGER.error("Exception occurred", e);
        }

        return false;
    }

    @Override
    public boolean requestDiscoverScaleIo(final ComponentEndpointIds componentEndpointIds, final String jobId)
    {
        try
        {
            final ListStorageRequestMessage requestMessage = new ListStorageRequestMessage();
            final String correlationId = UUID.randomUUID().toString();
            requestMessage
                    .setMessageProperties(new com.dell.cpsd.storage.capabilities.api.MessageProperties(new Date(), correlationId, replyTo));
            requestMessage.setEndpointURL("https://" + componentEndpointIds.getEndpointUrl());
            requestMessage.setComponentUuid(componentEndpointIds.getComponentUuid());
            requestMessage.setEndpointUuid(componentEndpointIds.getEndpointUuid());
            requestMessage.setCredentialUuid(componentEndpointIds.getCredentialUuid());

            ServiceResponse<?> callbackResponse = processRequest(timeout, new ServiceRequestCallback()
            {
                @Override
                public String getRequestId()
                {
                    return correlationId;
                }

                @Override
                public void executeRequest(String requestId) throws Exception
                {
                    producer.publishDiscoverScaleIo(requestMessage);
                }
            });

            ListStorageResponseMessage responseMessage = processResponse(callbackResponse, ListStorageResponseMessage.class);

            if (responseMessage != null && responseMessage.getMessageProperties() != null)
            {
                final ScaleIOData scaleIOData = scaleIORestToScaleIODomainTransformer
                        .transform(responseMessage.getScaleIOSystemDataRestRep());

                return scaleIOData != null && repository.saveScaleIoData(jobId, scaleIOData);
            }
            else
            {
                LOGGER.error("Message is null");
            }
        }
        catch (Exception e)
        {
            LOGGER.error("Exception occurred", e);
        }

        return false;
    }

    @Override
    public boolean requestDiscoverVCenter(final ComponentEndpointIds componentEndpointIds, final String jobId)
    {
        try
        {
            final DiscoveryRequestInfoMessage requestMessage = new DiscoveryRequestInfoMessage();
            final String correlationId = UUID.randomUUID().toString();
            requestMessage.setMessageProperties(
                    new com.dell.cpsd.virtualization.capabilities.api.MessageProperties(new Date(), correlationId, replyTo));
            requestMessage.setCredentials(new Credentials(componentEndpointIds.getEndpointUrl(), null, null));
            requestMessage.setComponentEndpointIds(
                    new com.dell.cpsd.virtualization.capabilities.api.ComponentEndpointIds(componentEndpointIds.getComponentUuid(),
                            componentEndpointIds.getEndpointUuid(), componentEndpointIds.getCredentialUuid()));

            ServiceResponse<?> callbackResponse = processRequest(timeout, new ServiceRequestCallback()
            {
                @Override
                public String getRequestId()
                {
                    return correlationId;
                }

                @Override
                public void executeRequest(String requestId) throws Exception
                {
                    producer.publishDiscoverVcenter(requestMessage);
                }
            });

            DiscoveryResponseInfoMessage responseMessage = processResponse(callbackResponse, DiscoveryResponseInfoMessage.class);

            if (responseMessage != null && responseMessage.getMessageProperties() != null)
            {
                final VCenter vCenterData = discoveryInfoToVCenterDomainTransformer.transform(responseMessage);

                return vCenterData != null && repository.saveVCenterData(jobId, vCenterData);
            }
            else
            {
                LOGGER.error("Message is null");
            }
        }
        catch (Exception e)
        {
            LOGGER.error("Exception occurred", e);
        }

        return false;
    }

    @Override
    public boolean requestAddHostToVCenter(final ClusterOperationRequestMessage requestMessage)
    {
        try
        {
            final String correlationId = UUID.randomUUID().toString();
            requestMessage.setMessageProperties(
                    new com.dell.cpsd.virtualization.capabilities.api.MessageProperties(new Date(), correlationId, replyTo));

            ServiceResponse<?> callbackResponse = processRequest(timeout, new ServiceRequestCallback()
            {
                @Override
                public String getRequestId()
                {
                    return correlationId;
                }

                @Override
                public void executeRequest(String requestId) throws Exception
                {
                    producer.publishAddHostToVCenter(requestMessage);
                }
            });

            ClusterOperationResponseMessage responseMessage = processResponse(callbackResponse, ClusterOperationResponseMessage.class);

            if (responseMessage != null && responseMessage.getMessageProperties() != null)
            {
                return responseMessage.getStatus().equals(ClusterOperationResponseMessage.Status.SUCCESS);
            }
            else
            {
                LOGGER.error("Response message is null");
            }
        }
        catch (Exception e)
        {
            LOGGER.error("Exception occurred", e);
        }
        return false;
    }

    @Override
    public boolean requestAddHostToProtectionDomain(final AddHostToProtectionDomainRequestMessage requestMessage)
    {
        try
        {
            com.dell.cpsd.storage.capabilities.api.MessageProperties messageProperties = new com.dell.cpsd.storage.capabilities.api.MessageProperties();
            messageProperties.setCorrelationId(UUID.randomUUID().toString());
            messageProperties.setTimestamp(Calendar.getInstance().getTime());
            messageProperties.setReplyTo(replyTo);

            requestMessage.setMessageProperties(messageProperties);
            ServiceResponse<?> callbackResponse = processRequest(timeout, new ServiceRequestCallback()
            {
                @Override
                public String getRequestId()
                {
                    return messageProperties.getCorrelationId();
                }

                @Override
                public void executeRequest(String requestId) throws Exception
                {
                    LOGGER.info("request add host to protection domain");
                    producer.publishAddHostToProtectionDomain(requestMessage);
                }
            });

            AddHostToProtectionDomainResponseMessage responseMessage = processResponse(callbackResponse, AddHostToProtectionDomainResponseMessage.class);

            if (responseMessage != null && responseMessage.getMessageProperties() != null)
            {
                return responseMessage.getStatus().equals(AddHostToProtectionDomainResponseMessage.Status.SUCCESS);
            }
            else
            {
                LOGGER.error("Response message is null");
            }
        }
        catch (Exception e)
        {
            LOGGER.error("Exception occurred", e);
        }
        return false;
    }

    @Override
    public boolean requestInstallSoftwareVib(final SoftwareVIBRequestMessage requestMessage)
    {
        try
        {
            final String correlationId = UUID.randomUUID().toString();
            requestMessage.setMessageProperties(
                    new com.dell.cpsd.virtualization.capabilities.api.MessageProperties(new Date(), correlationId, replyTo));

            ServiceResponse<?> callbackResponse = processRequest(timeout, new ServiceRequestCallback()
            {
                @Override
                public String getRequestId()
                {
                    return correlationId;
                }

                @Override
                public void executeRequest(String requestId) throws Exception
                {
                    producer.publishInstallScaleIoVib(requestMessage);
                }
            });

            SoftwareVIBResponseMessage responseMessage = processResponse(callbackResponse, SoftwareVIBResponseMessage.class);

            if (responseMessage != null && responseMessage.getMessageProperties() != null)
            {
                return responseMessage.getStatus().equals(SoftwareVIBResponseMessage.Status.SUCCESS);
            }
            else
            {
                LOGGER.error("Response message is null");
            }
        }
        catch (Exception e)
        {
            LOGGER.error("Exception occurred", e);
        }
        return false;
    }

    @Override
    public boolean requestConfigureScaleIoVib(final SoftwareVIBConfigureRequestMessage requestMessage)
    {
        try
        {
            final String correlationId = UUID.randomUUID().toString();
            requestMessage.setMessageProperties(
                    new com.dell.cpsd.virtualization.capabilities.api.MessageProperties(new Date(), correlationId, replyTo));

            LOGGER.info("Setting Request Call Back for Software Vib Configure");

            ServiceResponse<?> callbackResponse = processRequest(timeout, new ServiceRequestCallback()
            {
                @Override
                public String getRequestId()
                {
                    return correlationId;
                }

                @Override
                public void executeRequest(String requestId) throws Exception
                {
                    producer.publishConfigureScaleIoVib(requestMessage);
                }
            });

            SoftwareVIBResponseMessage responseMessage = processResponse(callbackResponse, SoftwareVIBResponseMessage.class);

            if (responseMessage != null && responseMessage.getMessageProperties() != null)
            {
                return responseMessage.getStatus().equals(SoftwareVIBResponseMessage.Status.SUCCESS);
            }
            else
            {
                LOGGER.error("Response message is null");
            }
        }
        catch (Exception e)
        {
            LOGGER.error("Exception occurred", e);
        }
        return false;
    }

    @Override
    public boolean requestAddHostToDvSwitch(final AddHostToDvSwitchRequestMessage requestMessage)
    {
        try
        {
            final String correlationId = UUID.randomUUID().toString();
            requestMessage.setMessageProperties(
                    new com.dell.cpsd.virtualization.capabilities.api.MessageProperties(new Date(), correlationId, replyTo));

            ServiceResponse<?> callbackResponse = processRequest(timeout, new ServiceRequestCallback()
            {
                @Override
                public String getRequestId()
                {
                    return correlationId;
                }

                @Override
                public void executeRequest(String requestId) throws Exception
                {
                    producer.publishAddHostToDvSwitch(requestMessage);
                }
            });

            AddHostToDvSwitchResponseMessage responseMessage = processResponse(callbackResponse, AddHostToDvSwitchResponseMessage.class);

            if (responseMessage != null && responseMessage.getMessageProperties() != null)
            {
                return responseMessage.getStatus().equals(AddHostToDvSwitchResponseMessage.Status.SUCCESS);
            }
            else
            {
                LOGGER.error("Response message is null");
            }
        }
        catch (Exception e)
        {
            LOGGER.error("Exception occurred", e);
        }
        return false;
    }

    @Override
    public boolean requestDeployScaleIoVm(final DeployVMFromTemplateRequestMessage requestMessage)
    {
        try
        {
            final String correlationId = UUID.randomUUID().toString();
            requestMessage.setMessageProperties(
                    new com.dell.cpsd.virtualization.capabilities.api.MessageProperties(new Date(), correlationId, replyTo));

            ServiceResponse<?> callbackResponse = processRequest(timeout, new ServiceRequestCallback()
            {
                @Override
                public String getRequestId()
                {
                    return correlationId;
                }

                @Override
                public void executeRequest(String requestId) throws Exception
                {
                    producer.publishDeployVmFromTemplate(requestMessage);
                }
            });

            DeployVMFromTemplateResponseMessage responseMessage = processResponse(callbackResponse,
                    DeployVMFromTemplateResponseMessage.class);

            if (responseMessage != null && responseMessage.getMessageProperties() != null)
            {
                return responseMessage.getStatus().equals(DeployVMFromTemplateResponseMessage.Status.SUCCESS);
            }
            else
            {
                LOGGER.error("Response message is null");
            }
        }
        catch (Exception e)
        {
            LOGGER.error("Exception occurred", e);
        }
        return false;
    }

    @Override
    public boolean requestEnablePciPassThrough(final EnablePCIPassthroughRequestMessage requestMessage)
    {
        try
        {
            final String correlationId = UUID.randomUUID().toString();
            requestMessage.setMessageProperties(
                    new com.dell.cpsd.virtualization.capabilities.api.MessageProperties(new Date(), correlationId, replyTo));

            ServiceResponse<?> callbackResponse = processRequest(timeout, new ServiceRequestCallback()
            {
                @Override
                public String getRequestId()
                {
                    return correlationId;
                }

                @Override
                public void executeRequest(String requestId) throws Exception
                {
                    producer.publishEnablePciPassthrough(requestMessage);
                }
            });

            EnablePCIPassthroughResponseMessage responseMessage = processResponse(callbackResponse,
                    EnablePCIPassthroughResponseMessage.class);

            if (responseMessage != null && responseMessage.getMessageProperties() != null)
            {
                return responseMessage.getStatus().equals(EnablePCIPassthroughResponseMessage.Status.SUCCESS_REBOOT_REQUIRED);
            }
            else
            {
                LOGGER.error("Response message is null");
            }
        }
        catch (Exception e)
        {
            LOGGER.error("Exception occurred", e);
        }
        return false;
    }

    @Override
    public boolean requestHostReboot(final HostPowerOperationRequestMessage requestMessage)
    {
        try
        {
            final String correlationId = UUID.randomUUID().toString();
            requestMessage.setMessageProperties(
                    new com.dell.cpsd.virtualization.capabilities.api.MessageProperties(new Date(), correlationId, replyTo));

            ServiceResponse<?> callbackResponse = processRequest(timeout, new ServiceRequestCallback()
            {
                @Override
                public String getRequestId()
                {
                    return correlationId;
                }

                @Override
                public void executeRequest(String requestId) throws Exception
                {
                    producer.publishRebootHost(requestMessage);
                }
            });

            HostPowerOperationResponseMessage responseMessage = processResponse(callbackResponse, HostPowerOperationResponseMessage.class);

            if (responseMessage != null && responseMessage.getMessageProperties() != null)
            {
                return responseMessage.getStatus().equals(HostPowerOperationResponseMessage.Status.SUCCESS);
            }
            else
            {
                LOGGER.error("Response message is null");
            }
        }
        catch (Exception e)
        {
            LOGGER.error("Exception occurred", e);
        }
        return false;
    }

    @Override
    public boolean requestSetPciPassThrough(final UpdatePCIPassthruSVMRequestMessage requestMessage)
    {
        try
        {
            final String correlationId = UUID.randomUUID().toString();
            requestMessage.setMessageProperties(
                    new com.dell.cpsd.virtualization.capabilities.api.MessageProperties(new Date(), correlationId, replyTo));

            ServiceResponse<?> callbackResponse = processRequest(timeout, new ServiceRequestCallback()
            {
                @Override
                public String getRequestId()
                {
                    return correlationId;
                }

                @Override
                public void executeRequest(String requestId) throws Exception
                {
                    producer.publishSetPciPassthrough(requestMessage);
                }
            });

            UpdatePCIPassthruSVMResponseMessage responseMessage = processResponse(callbackResponse,
                    UpdatePCIPassthruSVMResponseMessage.class);

            if (responseMessage != null && responseMessage.getMessageProperties() != null)
            {
                return responseMessage.getStatus().equals(UpdatePCIPassthruSVMResponseMessage.Status.SUCCESS);
            }
            else
            {
                LOGGER.error("Response message is null");
            }
        }
        catch (Exception e)
        {
            LOGGER.error("Exception occurred", e);
        }
        return false;
    }

    @Override
    public boolean requestInstallEsxiLicense(final AddEsxiHostVSphereLicenseRequest requestMessage)
    {
        try
        {
            final String correlationId = UUID.randomUUID().toString();
            requestMessage.setMessageProperties(
                    new com.dell.cpsd.virtualization.capabilities.api.MessageProperties(new Date(), correlationId, replyTo));

            ServiceResponse<?> callbackResponse = processRequest(timeout, new ServiceRequestCallback()
            {
                @Override
                public String getRequestId()
                {
                    return correlationId;
                }

                @Override
                public void executeRequest(String requestId) throws Exception
                {
                    producer.publishApplyEsxiLicense(requestMessage);
                }
            });

            AddEsxiHostVSphereLicenseResponse responseMessage = processResponse(callbackResponse, AddEsxiHostVSphereLicenseResponse.class);

            if (responseMessage != null && responseMessage.getMessageProperties() != null)
            {
                return responseMessage.getStatus().equals(AddEsxiHostVSphereLicenseResponse.Status.SUCCESS);
            }
            else
            {
                LOGGER.error("Response message is null");
            }
        }
        catch (Exception e)
        {
            LOGGER.error("Exception occurred", e);
        }
        return false;
    }

    @Override
    public ComponentEndpointIds listDefaultCredentials(final ListEsxiCredentialDetailsRequestMessage requestMessage)
    {
        ComponentEndpointIds returnData = null;
        try
        {
            final String correlationId = UUID.randomUUID().toString();
            requestMessage.setMessageProperties(
                    new com.dell.cpsd.virtualization.capabilities.api.MessageProperties(new Date(), correlationId, replyTo));

            ServiceResponse<?> callbackResponse = processRequest(timeout, new ServiceRequestCallback()
            {
                @Override
                public String getRequestId()
                {
                    return correlationId;
                }

                @Override
                public void executeRequest(String requestId) throws Exception
                {
                    producer.publishListExsiCredentialDetails(requestMessage);
                }
            });

            ListEsxiCredentialDetailsResponseMessage responseMessage = processResponse(callbackResponse,
                    ListEsxiCredentialDetailsResponseMessage.class);

            boolean validResponse = responseMessage != null;
            validResponse = validResponse && responseMessage.getMessageProperties() != null;
            validResponse = validResponse && responseMessage.getComponentUuid() != null;
            validResponse = validResponse && responseMessage.getEndpointUuid() != null;
            validResponse = validResponse && responseMessage.getCredentialUuid() != null;

            if (validResponse)
            {
                returnData = new ComponentEndpointIds(responseMessage.getComponentUuid(), responseMessage.getEndpointUuid(), null,
                        responseMessage.getCredentialUuid());
            }
            else
            {
                LOGGER.error("Response message is null");
            }
        }
        catch (Exception e)
        {
            LOGGER.error("Exception occurred", e);
        }
        return returnData;
    }

    @Override
    public boolean requestHostMaintenanceMode(final HostMaintenanceModeRequestMessage requestMessage)
    {
        try
        {
            final String correlationId = UUID.randomUUID().toString();
            requestMessage.setMessageProperties(
                    new com.dell.cpsd.virtualization.capabilities.api.MessageProperties(new Date(), correlationId, replyTo));

            ServiceResponse<?> callbackResponse = processRequest(timeout, new ServiceRequestCallback()
            {
                @Override
                public String getRequestId()
                {
                    return correlationId;
                }

                @Override
                public void executeRequest(String requestId) throws Exception
                {
                    producer.publishHostMaintenanceMode(requestMessage);
                }
            });

            HostMaintenanceModeResponseMessage responseMessage = processResponse(callbackResponse,
                    HostMaintenanceModeResponseMessage.class);

            if (responseMessage != null && responseMessage.getMessageProperties() != null)
            {
                return responseMessage.getStatus().equals(HostMaintenanceModeResponseMessage.Status.SUCCESS);
            }
            else
            {
                LOGGER.error("Response message is null");
            }
        }
        catch (Exception e)
        {
            LOGGER.error("Exception occurred", e);
        }
        return false;
    }

    @Override
    public Object listNodeInventory(final String symphonyUUID) throws ServiceTimeoutException, ServiceExecutionException
    {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setCorrelationId(UUID.randomUUID().toString());
        messageProperties.setTimestamp(Calendar.getInstance().getTime());
        messageProperties.setReplyTo(replyTo);

        NodeInventoryRequestMessage nodeInventoryRequestMessage = new NodeInventoryRequestMessage();
        nodeInventoryRequestMessage.setMessageProperties(messageProperties);
        nodeInventoryRequestMessage.setSymphonyUUID(symphonyUUID);

        ServiceResponse<?> response = processRequest(timeout, new ServiceRequestCallback()
        {
            @Override
            public String getRequestId()
            {
                return messageProperties.getCorrelationId();
            }

            @Override
            public void executeRequest(String requestId) throws Exception
            {
                producer.publishNodeInventoryDiscovery(nodeInventoryRequestMessage);
            }
        });

        NodeInventoryResponseMessage nodeInventoryResponseMessage = processResponse(response, NodeInventoryResponseMessage.class);
        if (nodeInventoryResponseMessage != null)
        {
            return nodeInventoryResponseMessage.getNodeInventory();
        }

        return null;
    }

    @Override
    public String requestDatastoreRename(final DatastoreRenameRequestMessage requestMessage)
    {
        try
        {
            com.dell.cpsd.virtualization.capabilities.api.MessageProperties messageProperties =
                    new com.dell.cpsd.virtualization.capabilities.api.MessageProperties();
            messageProperties.setCorrelationId(UUID.randomUUID().toString());
            messageProperties.setTimestamp(Calendar.getInstance().getTime());
            messageProperties.setReplyTo(replyTo);

            requestMessage.setMessageProperties(messageProperties);

            ServiceResponse<?> response = processRequest(timeout, new ServiceRequestCallback()
            {
                @Override
                public String getRequestId()
                {
                    return messageProperties.getCorrelationId();
                }

                @Override
                public void executeRequest(String requestId) throws Exception
                {
                    producer.publishDatastoreRename(requestMessage);
                }
            });

            DatastoreRenameResponseMessage datastoreRenameResponseMessage = processResponse(response, DatastoreRenameResponseMessage.class);

            if (datastoreRenameResponseMessage != null)
            {
                return datastoreRenameResponseMessage.getDatastoreName();
            }
        }
        catch (Exception ex)
        {
            LOGGER.error("Exception occurred", ex);
        }

        return null;
    }

    @Override
    public boolean requestUpdateSoftwareAcceptance(final VCenterUpdateSoftwareAcceptanceRequestMessage requestMessage)
    {
        try
        {
            com.dell.cpsd.virtualization.capabilities.api.MessageProperties messageProperties =
                    new com.dell.cpsd.virtualization.capabilities.api.MessageProperties();
            messageProperties.setCorrelationId(UUID.randomUUID().toString());
            messageProperties.setTimestamp(Calendar.getInstance().getTime());
            messageProperties.setReplyTo(replyTo);

            requestMessage.setMessageProperties(messageProperties);

            ServiceResponse<?> response = processRequest(timeout, new ServiceRequestCallback()
            {
                @Override
                public String getRequestId()
                {
                    return messageProperties.getCorrelationId();
                }

                @Override
                public void executeRequest(String requestId) throws Exception
                {
                    producer.publishUpdateSoftwareAcceptance(requestMessage);
                }
            });

            VCenterUpdateSoftwareAcceptanceResponseMessage vCenterUpdateSoftwareAcceptanceResponseMessage =
                    processResponse(response, VCenterUpdateSoftwareAcceptanceResponseMessage.class);

            if (vCenterUpdateSoftwareAcceptanceResponseMessage != null)
            {
                return vCenterUpdateSoftwareAcceptanceResponseMessage.getStatus().equals(VCenterUpdateSoftwareAcceptanceResponseMessage.Status.SUCCESS);
            }
        }
        catch (Exception ex)
        {
            LOGGER.error("Exception occurred", ex);
        }

        return false;
    }

    @Override
    public boolean requestVmPowerOperation(final VmPowerOperationsRequestMessage requestMessage)
    {
        try
        {
            com.dell.cpsd.virtualization.capabilities.api.MessageProperties messageProperties =
                    new com.dell.cpsd.virtualization.capabilities.api.MessageProperties();
            messageProperties.setCorrelationId(UUID.randomUUID().toString());
            messageProperties.setTimestamp(Calendar.getInstance().getTime());
            messageProperties.setReplyTo(replyTo);

            requestMessage.setMessageProperties(messageProperties);

            ServiceResponse<?> response = processRequest(timeout, new ServiceRequestCallback()
            {
                @Override
                public String getRequestId()
                {
                    return messageProperties.getCorrelationId();
                }

                @Override
                public void executeRequest(String requestId) throws Exception
                {
                    producer.publishVmPowerOperation(requestMessage);
                }
            });

            VmPowerOperationsResponseMessage vmPowerOperationsResponseMessage = processResponse(response,
                    VmPowerOperationsResponseMessage.class);

            if (vmPowerOperationsResponseMessage != null)
            {
                return vmPowerOperationsResponseMessage.getStatus()
                        .equals(VmPowerOperationsResponseMessage.Status.SUCCESS);
            }
        }
        catch (Exception ex)
        {
            LOGGER.error("Exception occurred", ex);
        }

        return false;
    }

    @Override
    public boolean requestConfigureVmNetworkSettings(final ConfigureVmNetworkSettingsRequestMessage requestMessage)
    {
        try
        {
            com.dell.cpsd.virtualization.capabilities.api.MessageProperties messageProperties =
                    new com.dell.cpsd.virtualization.capabilities.api.MessageProperties();
            messageProperties.setCorrelationId(UUID.randomUUID().toString());
            messageProperties.setTimestamp(Calendar.getInstance().getTime());
            messageProperties.setReplyTo(replyTo);

            requestMessage.setMessageProperties(messageProperties);

            ServiceResponse<?> response = processRequest(timeout, new ServiceRequestCallback()
            {
                @Override
                public String getRequestId()
                {
                    return messageProperties.getCorrelationId();
                }

                @Override
                public void executeRequest(String requestId) throws Exception
                {
                    producer.publishConfigureVmNetworkSettings(requestMessage);
                }
            });

            ConfigureVmNetworkSettingsResponseMessage configureVmNetworkSettingsResponseMessage = processResponse(response,
                    ConfigureVmNetworkSettingsResponseMessage.class);

            if (configureVmNetworkSettingsResponseMessage != null)
            {
                return configureVmNetworkSettingsResponseMessage.getStatus()
                        .equals(ConfigureVmNetworkSettingsResponseMessage.Status.SUCCESS);
            }
        }
        catch (Exception ex)
        {
            LOGGER.error("Exception occurred", ex);
        }

        return false;
    }

    @Override
    public boolean requestRemoteCommandExecution(final RemoteCommandExecutionRequestMessage requestMessage)
    {
        try
        {
            com.dell.cpsd.virtualization.capabilities.api.MessageProperties messageProperties = new com.dell.cpsd.virtualization.capabilities.api.MessageProperties();
            messageProperties.setCorrelationId(UUID.randomUUID().toString());
            messageProperties.setTimestamp(Calendar.getInstance().getTime());
            messageProperties.setReplyTo(replyTo);

            requestMessage.setMessageProperties(messageProperties);

            ServiceResponse<?> response = processRequest(timeout, new ServiceRequestCallback()
            {
                @Override
                public String getRequestId()
                {
                    return messageProperties.getCorrelationId();
                }

                @Override
                public void executeRequest(String requestId) throws Exception
                {
                    producer.publishRemoteCommandExecution(requestMessage);
                }
            });

            RemoteCommandExecutionResponseMessage remoteCommandExecutionResponseMessage = processResponse(response,
                    RemoteCommandExecutionResponseMessage.class);

            if (remoteCommandExecutionResponseMessage != null)
            {
                return remoteCommandExecutionResponseMessage.getStatus().equals(RemoteCommandExecutionResponseMessage.Status.SUCCESS);
            }
        }
        catch (Exception ex)
        {
            LOGGER.error("Exception occurred", ex);
        }

        return false;
    }

    /**
     * get node inventory json string from Node_info table
     * @param job
     * @return
     */
    public String getNodeInventoryData(Job job) {
        String symphonyUUID = job.getInputParams().getSymphonyUuid();
        LOGGER.info("Node Inventory UUID="+symphonyUUID);
        NodeInventory nodeInventory = repository.getNodeInventory(symphonyUUID);

        return nodeInventory == null ? null : nodeInventory.getNodeInventory();
    }

    /*
     * {@inheritDoc}
     */
    public List<Host> findVcenterHosts() throws NoResultException
    {
        List<Host> vCenterHosts;
        try
        {
            vCenterHosts = repository.getVCenterHosts();
        }
        catch (NoResultException e)
        {
            LOGGER.error("Could not find any vCenter.", e);
            throw new IllegalStateException("Could not find any vCenter.");
        }

        return vCenterHosts;
    }

    public Map<String, Map<String, HostStorageDevice>> getHostToStorageDeviceMap(List<Host> hosts)
    {
        return storagePoolEssRequestTransformer.getHostToStorageDeviceMap(hosts);
    }

    @Override
    public boolean requestUpdateSdcPerformanceProfile(final SioSdcUpdatePerformanceProfileRequestMessage requestMessage)
    {
        try
        {
            com.dell.cpsd.storage.capabilities.api.MessageProperties messageProperties = new com.dell.cpsd.storage.capabilities.api.MessageProperties();
            messageProperties.setCorrelationId(UUID.randomUUID().toString());
            messageProperties.setTimestamp(Calendar.getInstance().getTime());
            messageProperties.setReplyTo(replyTo);

            requestMessage.setMessageProperties(messageProperties);

            ServiceResponse<?> response = processRequest(timeout, new ServiceRequestCallback()
            {
                @Override
                public String getRequestId()
                {
                    return messageProperties.getCorrelationId();
                }

                @Override
                public void executeRequest(String requestId) throws Exception
                {
                    producer.publishUpdateSdcPerformanceProfile(requestMessage);
                }
            });

            SioSdcUpdatePerformanceProfileResponseMessage sioSdcUpdatePerformanceProfileResponseMessage = processResponse(response,
                    SioSdcUpdatePerformanceProfileResponseMessage.class);

            if (sioSdcUpdatePerformanceProfileResponseMessage != null)
            {
                return sioSdcUpdatePerformanceProfileResponseMessage.getStatus().equals(SioSdcUpdatePerformanceProfileResponseMessage.Status.SUCCESS);
            }
        }
        catch (Exception ex)
        {
            LOGGER.error("Exception occurred", ex);
        }

        return false;
    }

    @Override
    public CreateStoragePoolResponseMessage createStoragePool(CreateStoragePoolRequestMessage requestMessage)
            throws ServiceTimeoutException, ServiceExecutionException
    {
        com.dell.cpsd.storage.capabilities.api.MessageProperties messageProperties = new com.dell.cpsd.storage.capabilities.api.MessageProperties();
        messageProperties.setCorrelationId(UUID.randomUUID().toString());
        messageProperties.setTimestamp(Calendar.getInstance().getTime());
        messageProperties.setReplyTo(replyTo);

        requestMessage.setMessageProperties(messageProperties);

        ServiceResponse<?> response = processRequest(timeout, new ServiceRequestCallback()
        {
            @Override
            public String getRequestId()
            {
                return messageProperties.getCorrelationId();
            }

            @Override
            public void executeRequest(String requestId) throws Exception
            {
                LOGGER.info("publish create storage pool message.");
                producer.publishCreateStoragePool(requestMessage);
            }
        });

        return processResponse(response, CreateStoragePoolResponseMessage.class);
    }

    @Override
    public ScaleIOStoragePool createStoragePool(final String storagePoolName, final String storagePoolId, String protectionDomainId)
    {
        return repository.createStoragePool(protectionDomainId, storagePoolId, storagePoolName);
    }

    @Override
    public String createProtectionDomain(final CreateProtectionDomainRequestMessage requestMessage)
            throws ServiceTimeoutException, ServiceExecutionException
    {
        com.dell.cpsd.storage.capabilities.api.MessageProperties messageProperties = new com.dell.cpsd.storage.capabilities.api.MessageProperties();
        messageProperties.setCorrelationId(UUID.randomUUID().toString());
        messageProperties.setTimestamp(Calendar.getInstance().getTime());
        messageProperties.setReplyTo(replyTo);

        requestMessage.setMessageProperties(messageProperties);

        ServiceResponse<?> response = processRequest(timeout, new ServiceRequestCallback()
        {
            @Override
            public String getRequestId()
            {
                return messageProperties.getCorrelationId();
            }

            @Override
            public void executeRequest(String requestId) throws Exception
            {
                producer.publishCreateProtectionDomain(requestMessage);
            }
        });

        final CreateProtectionDomainResponseMessage createProtectionDomainResponseMessage = processResponse(response,
                CreateProtectionDomainResponseMessage.class);

        if (createProtectionDomainResponseMessage == null)
        {
            LOGGER.error("Create Protection Domain Response Message is null");
            return null;
        }

        return createProtectionDomainResponseMessage.getProtectionDomainId();
     }

    /*
     * {@inheritDoc}
     */
    @Override
    public ComponentEndpointIds getComponentEndpointIds(String componentType)
    {
        return repository.getComponentEndpointIds(componentType);
    }
}

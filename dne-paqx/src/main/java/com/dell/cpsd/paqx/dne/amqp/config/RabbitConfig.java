/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */
package com.dell.cpsd.paqx.dne.amqp.config;

import com.dell.cpsd.*;
import com.dell.cpsd.common.rabbitmq.MessageAnnotationProcessor;
import com.dell.cpsd.common.rabbitmq.message.DefaultMessageConverterFactory;
import com.dell.cpsd.common.rabbitmq.retrypolicy.DefaultRetryPolicyFactory;
import com.dell.cpsd.rackhd.adapter.model.idrac.IdracNetworkSettings;
import com.dell.cpsd.rackhd.adapter.model.idrac.IdracNetworkSettingsRequestMessage;
import com.dell.cpsd.rackhd.adapter.model.idrac.IdracNetworkSettingsResponse;
import com.dell.cpsd.rackhd.adapter.model.idrac.IdracNetworkSettingsResponseMessage;
import com.dell.cpsd.service.engineering.standards.EssValidateProtectionDomainsRequestMessage;
import com.dell.cpsd.service.engineering.standards.EssValidateProtectionDomainsResponseMessage;
import com.dell.cpsd.storage.capabilities.api.*;
import com.dell.cpsd.storage.capabilities.api.ListComponentRequestMessage;
import com.dell.cpsd.storage.capabilities.api.ListComponentResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.AddEsxiHostVSphereLicenseRequest;
import com.dell.cpsd.virtualization.capabilities.api.AddEsxiHostVSphereLicenseResponse;
import com.dell.cpsd.virtualization.capabilities.api.AddHostToDvSwitchRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.AddHostToDvSwitchResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.ClusterOperationRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.ClusterOperationResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.ConfigureVmNetworkSettingsRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.ConfigureVmNetworkSettingsResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.DatastoreRenameRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.DatastoreRenameResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.DeployVMFromTemplateRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.DeployVMFromTemplateResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.DiscoverClusterRequestInfoMessage;
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
import com.dell.cpsd.virtualization.capabilities.api.VCenterUpdateSoftwareAcceptanceRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.VCenterUpdateSoftwareAcceptanceResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.ValidateVcenterClusterRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.ValidateVcenterClusterResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.VmPowerOperationsRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.VmPowerOperationsResponseMessage;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.ClassMapper;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.retry.support.RetryTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is the configuration for the RabbitMQ artifacts used by the service.
 * 
 * <p/>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * <p/>
 * 
 * @since    1.0
 */
@Configuration
@Import({ProductionConfig.class, PropertiesConfig.class})
public class RabbitConfig
{
    /*
     * The binding key to the dne service response message queue.
     */
    public static final String QUEUE_GENERAL_RESPONSE = "queue.dell.cpsd.dne-paqx.response";

    // following envs are for ESS communication. For now, ESS is internal service so no capability registration.
    // DNE gets exchanges, queue, routing key from properties file.
    @Value("${ess.req.exchange.name}")
    private String              essRequestExchange;

    @Value("${ess.req.routing.prefix}")
    private String              essReqRoutingKeyPrefix;

    @Value("${ess.res.exchange.name}")
    private String              essResponseExchange;

    @Value("${ess.res.queue}")
    private String              essResQueue;

    @Value("${ess.res.routing.prefix}")
    private String              essRespRoutingKeyPrefix;
    /*
     * The RabbitMQ connection factory
     */
    @Autowired
    @Qualifier("rabbitConnectionFactory")
    private ConnectionFactory rabbitConnectionFactory;

    /*
     * The configuration properties for the service.
     */
    @Autowired
    private PropertiesConfig propertiesConfig;

    /*
     * The support system-integration-sdk class mappings
     */
    @Autowired
    @Qualifier("supportedIdClassMapping")
    private Map<String, Class<?>> sdkSupportedIdClassMapping;

    /**
     * This returns the RabbitMQ template.
     *
     * @return  The <code>RabbitTemplate</code>.
     *
     * @since   1.0
     */
    @Bean
    RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(rabbitConnectionFactory);
        template.setMessageConverter(dneMessageConverter());
        template.setRetryTemplate(retryTemplate());
        return template;
    }

    /**
     * This returns the <code>RetryTemplate</code> for the <code>RabbitTemplate
     * </code>.
     *
     * @return  The <code>RetryTemplate</code>.
     * 
     * @since   1.0
     */
    @Bean
    RetryTemplate retryTemplate()
    {
        return DefaultRetryPolicyFactory.makeRabbitTemplateRetry();
    }

    /**
     * This returns the <code>MessageConverter</code> for the 
     * <code>RabbitTemplate</code>.
     * 
     * @return  The <code>MessageConverter</code> for the template.
     * 
     * @since   1.0
     */
    @Bean
    public MessageConverter dneMessageConverter()
    {
        return DefaultMessageConverterFactory.makeMessageConverter(classMapper());
    }

    /**
     * This returns the hostname.
     * 
     * @return The host name.
     */
    @Bean
    String hostName() {
        try {
            return System.getProperty("container.id");
        } catch (Exception e) {
            throw new RuntimeException("Unable to identify containerId", e);
        }
    }

    /**
     * This returns the name of the replyTo queue for RPC responses.
     * 
     * @return The name of the replyTo queue.
     */
    @Bean
    String replyTo() {
        return propertiesConfig.applicationName() + "." + hostName();
    }

    /**
     * This returns the <code>RabbitAdmin</code> instance.
     * 
     * @return  The <code>RabbitAdmin</code>.
     * 
     * @since   1.0
     */
    @Bean (name="nodeExpansionAmqpAdmin")
    AmqpAdmin nodeExpansionAmqpAdmin() {
        return new RabbitAdmin(rabbitConnectionFactory);
    }

    /**
     * This returns the <code>ClassMapper</code> for the message converter.
     * 
     * @return  The <code>ClassMapper</code> for the message converter.
     * 
     * @since   1.0
     */
    @Bean
    ClassMapper classMapper() {
        DefaultClassMapper classMapper = new DefaultClassMapper();
        Map<String, Class<?>> classMappings = new HashMap<>();
        List<Class<?>> messageClasses = new ArrayList<>();

        messageClasses.add(ListNodes.class);
        messageClasses.add(NodesListed.class);
        messageClasses.add(DiscoveredNodeError.class);

        messageClasses.add(DiscoverClusterRequestInfoMessage.class);
        messageClasses.add(DiscoverClusterResponseInfoMessage.class);

        messageClasses.add(StartNodeAllocationRequestMessage.class);
        messageClasses.add(StartNodeAllocationResponseMessage.class);

        messageClasses.add(CompleteNodeAllocationRequestMessage.class);
        messageClasses.add(CompleteNodeAllocationResponseMessage.class);

        messageClasses.add(FailNodeAllocationRequestMessage.class);
        messageClasses.add(FailNodeAllocationResponseMessage.class);

        messageClasses.add(ChangeIdracCredentialsRequestMessage.class);
        messageClasses.add(ChangeIdracCredentialsResponseMessage.class);

        messageClasses.add(ConfigureBootDeviceIdracRequestMessage.class);
        messageClasses.add(ConfigureBootDeviceIdracResponseMessage.class);

        messageClasses.add(ConfigurePxeBootRequestMessage.class);
        messageClasses.add(ConfigurePxeBootResponseMessage.class);

        messageClasses.add(IdracNetworkSettings.class);
        messageClasses.add(IdracNetworkSettingsRequestMessage.class);
        messageClasses.add(IdracNetworkSettingsResponse.class);
        messageClasses.add(IdracNetworkSettingsResponseMessage.class);

        messageClasses.add(SetObmSettingsRequestMessage.class);
        messageClasses.add(SetObmSettingsResponseMessage.class);

        messageClasses.add(ValidateVcenterClusterResponseMessage.class);
        messageClasses.add(ValidateVcenterClusterRequestMessage.class);

        messageClasses.add(ListComponentRequestMessage.class);
        messageClasses.add(ListComponentResponseMessage.class);
        messageClasses.add(ListComponentsRequestMessage.class);
        messageClasses.add(ListComponentsResponseMessage.class);

        messageClasses.add(ListStorageRequestMessage.class);
        messageClasses.add(ListStorageResponseMessage.class);
        messageClasses.add(DiscoveryRequestInfoMessage.class);
        messageClasses.add(DiscoveryResponseInfoMessage.class);

        messageClasses.add(AddEsxiHostVSphereLicenseRequest.class);
        messageClasses.add(AddEsxiHostVSphereLicenseResponse.class);

        messageClasses.add(ClusterOperationRequestMessage.class);
        messageClasses.add(ClusterOperationResponseMessage.class);

        messageClasses.add(InstallESXiRequestMessage.class);
        messageClasses.add(InstallESXiResponseMessage.class);

        messageClasses.add(AddHostToDvSwitchRequestMessage.class);
        messageClasses.add(AddHostToDvSwitchResponseMessage.class);

        messageClasses.add(SoftwareVIBConfigureRequestMessage.class);
        messageClasses.add(SoftwareVIBRequestMessage.class);
        messageClasses.add(SoftwareVIBResponseMessage.class);

        messageClasses.add(DeployVMFromTemplateRequestMessage.class);
        messageClasses.add(DeployVMFromTemplateResponseMessage.class);

        messageClasses.add(EnablePCIPassthroughRequestMessage.class);
        messageClasses.add(EnablePCIPassthroughResponseMessage.class);

        messageClasses.add(HostPowerOperationRequestMessage.class);
        messageClasses.add(HostPowerOperationResponseMessage.class);

        messageClasses.add(UpdatePCIPassthruSVMRequestMessage.class);
        messageClasses.add(UpdatePCIPassthruSVMResponseMessage.class);

        messageClasses.add(ListEsxiCredentialDetailsRequestMessage.class);
        messageClasses.add(ListEsxiCredentialDetailsResponseMessage.class);

        messageClasses.add(HostMaintenanceModeRequestMessage.class);
        messageClasses.add(HostMaintenanceModeResponseMessage.class);

        messageClasses.add(NodeInventoryRequestMessage.class);
        messageClasses.add(NodeInventoryResponseMessage.class);

        messageClasses.add(DatastoreRenameRequestMessage.class);
        messageClasses.add(DatastoreRenameResponseMessage.class);

        messageClasses.add(EssValidateProtectionDomainsRequestMessage.class);
        messageClasses.add(EssValidateProtectionDomainsResponseMessage.class);

        messageClasses.add(VCenterUpdateSoftwareAcceptanceRequestMessage.class);
        messageClasses.add(VCenterUpdateSoftwareAcceptanceResponseMessage.class);

        messageClasses.add(ConfigureVmNetworkSettingsRequestMessage.class);
        messageClasses.add(ConfigureVmNetworkSettingsResponseMessage.class);

        messageClasses.add(VmPowerOperationsRequestMessage.class);
        messageClasses.add(VmPowerOperationsResponseMessage.class);

        messageClasses.add(RemoteCommandExecutionRequestMessage.class);
        messageClasses.add(RemoteCommandExecutionResponseMessage.class);

        messageClasses.add(AddHostToProtectionDomainRequestMessage.class);
        messageClasses.add(AddHostToProtectionDomainResponseMessage.class);

        messageClasses.add(SioSdcUpdatePerformanceProfileRequestMessage.class);
        messageClasses.add(SioSdcUpdatePerformanceProfileResponseMessage.class);

        messageClasses.add(CreateStoragePoolRequestMessage.class);
        messageClasses.add(CreateStoragePoolResponseMessage.class);

        messageClasses.add(CreateProtectionDomainRequestMessage.class);
        messageClasses.add(CreateProtectionDomainResponseMessage.class);

        MessageAnnotationProcessor messageAnnotationProcessor = new MessageAnnotationProcessor();
        messageAnnotationProcessor.process(classMappings::put, messageClasses);
        
        if (this.sdkSupportedIdClassMapping != null)
        {
            classMappings.putAll(this.sdkSupportedIdClassMapping);
        }
        
        classMapper.setIdClassMapping(classMappings);
        
        return classMapper;
    }

    /**
     * This returns the name of the dne service response queue.
     * 
     * @return The name of the dne response queue.
     * 
     * @since   1.0
     */
    @Bean (name="nodeExpansionResponseQueue")
    Queue nodeExpansionResponseQueue() {
        return new Queue(QUEUE_GENERAL_RESPONSE);
    }

    @Bean
    public String essRequestExchange()
    {
        return essRequestExchange;
    }

    @Bean
    public String essReqRoutingKeyPrefix()
    {
        return essReqRoutingKeyPrefix;
    }

    @Bean
    public TopicExchange essResponseExchange()
    {
        return new TopicExchange(essResponseExchange);
    }

    @Bean
    public Queue essResponseQueue()
    {
        return new Queue(essResQueue);
    }

    @Bean
    public Binding essBinding()
    {
        return BindingBuilder.bind(essResponseQueue()).to(essResponseExchange()).with(essRespRoutingKeyPrefix + ".#");
    }

}


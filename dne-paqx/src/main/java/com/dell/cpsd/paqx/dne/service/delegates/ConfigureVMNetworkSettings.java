/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.virtualization.capabilities.api.ConfigureVmNetworkSettingsRequestMessage;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.CONFIGURE_VM_NETWORK_SETTINGS;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.DEPLOY_SCALEIO_NEW_VM_NAME;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.HOSTNAME;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;

/**
 * Configure virtual machine network settings.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@Component
@Scope("prototype")
@Qualifier("configureVMNetworkSettings")
public class ConfigureVMNetworkSettings extends BaseWorkflowDelegate
{
    /**
     * The <code>Logger</code> instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigureVMNetworkSettings.class);

    /**
     * The <code>NodeService</code> instance
     */
    private final NodeService nodeService;

    /**
     * The <code>DataServiceRepository</code> instance
     */
    private final DataServiceRepository repository;

    /**
     * ConfigureVmNetworkSettingsTaskHandler constructor.
     *
     * @param nodeService - The <code>NodeService</code> instance
     * @param repository  - The <code>DataServiceRepository</code> instance
     */
    @Autowired
    public ConfigureVMNetworkSettings(final NodeService nodeService, final DataServiceRepository repository)
    {
        this.nodeService = nodeService;
        this.repository = repository;
    }

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Execute Configure vm network settings");
        final String taskMessage = "Configure VM Network Settings";

        final NodeDetail nodeDetail = (NodeDetail) delegateExecution.getVariable(NODE_DETAIL);
        final String hostname = (String) delegateExecution.getVariable(HOSTNAME);
        final String vmName = (String) delegateExecution.getVariable(DEPLOY_SCALEIO_NEW_VM_NAME);

        ComponentEndpointIds componentEndpointIds;
        try
        {
            componentEndpointIds = repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER");
        }
        catch (Exception e)
        {
            String errorMessage = "An Unexpected Exception occurred attempting to retrieve VCenter Component Endpoints.  Reason: ";
            LOGGER.error(errorMessage, e);
            updateDelegateStatus(errorMessage + e.getMessage());
            throw new BpmnError(CONFIGURE_VM_NETWORK_SETTINGS, errorMessage + e.getMessage());
        }

        Map<String, String> dvSwitchNames;
        try
        {
            dvSwitchNames = repository.getDvSwitchNames();
        } catch(Exception e)
        {
            String errorMessage = "An Unexpected Exception occurred attempting to retrieve DV Switch names.  Reason: ";
            LOGGER.error(errorMessage, e);
            updateDelegateStatus(errorMessage + e.getMessage());
            throw new BpmnError(CONFIGURE_VM_NETWORK_SETTINGS, errorMessage + e.getMessage());
        }

        Map<String, String> scaleIoNetworkNamesMap;
        try
        {
            scaleIoNetworkNamesMap = repository.getScaleIoNetworkNames(dvSwitchNames);
        } catch(Exception e)
        {
            String errorMessage = "An Unexpected Exception occurred attempting to retrieve ScaleIO DVSwitch-Network Names Map.  Reason: ";
            LOGGER.error(errorMessage, e);
            updateDelegateStatus(errorMessage + e.getMessage());
            throw new BpmnError(CONFIGURE_VM_NETWORK_SETTINGS, errorMessage + e.getMessage());
        }

        final ConfigureVmNetworkSettingsRequestMessage requestMessage = new ConfigureVmNetworkSettingsRequestMessage();
        requestMessage.setHostname(hostname);
        requestMessage.setVmName(vmName);
        requestMessage.setNetworkSettingsMap(scaleIoNetworkNamesMap);
        requestMessage.setEndpointUrl(componentEndpointIds.getEndpointUrl());
        requestMessage.setComponentEndpointIds(
                new com.dell.cpsd.virtualization.capabilities.api.ComponentEndpointIds(componentEndpointIds.getComponentUuid(),
                        componentEndpointIds.getEndpointUuid(), componentEndpointIds.getCredentialUuid()));

        boolean succeeded;
        try
        {
            succeeded = this.nodeService.requestConfigureVmNetworkSettings(requestMessage);
        }
        catch (Exception ex)
        {
            String errorMessage = "An Unexpected Exception occurred attempting to request " + taskMessage + ".  Reason: ";
            LOGGER.error(errorMessage, ex);
            updateDelegateStatus(errorMessage + ex.getMessage());
            throw new BpmnError(CONFIGURE_VM_NETWORK_SETTINGS, errorMessage + ex.getMessage());

        }

        if (!succeeded)
        {
            String errorMessage = taskMessage + ": Configure VM network settings request failed";
            LOGGER.error(errorMessage);
            updateDelegateStatus(errorMessage);
            throw new BpmnError(CONFIGURE_VM_NETWORK_SETTINGS, errorMessage);
        }

        String returnMessage = taskMessage + " on Node " + nodeDetail.getServiceTag() + " was successful.";
        LOGGER.info(returnMessage);
        updateDelegateStatus(returnMessage);
    }
}

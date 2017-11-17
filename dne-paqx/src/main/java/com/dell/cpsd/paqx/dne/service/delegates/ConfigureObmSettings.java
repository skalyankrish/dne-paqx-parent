/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.ObmConfig;
import com.dell.cpsd.SetObmSettingsRequestMessage;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import com.dell.cpsd.paqx.dne.service.model.ObmSettingsResponse;
import java.util.Arrays;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.CONFIGURE_OBM_SETTINGS_FAILED;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;

@Component
@Scope("prototype")
@Qualifier("configureObmSettings")
public class ConfigureObmSettings extends BaseWorkflowDelegate
{

    /**
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigureObmSettings.class);

    /**
     * The <code>NodeService</code> instance
     */
    private NodeService nodeService;

    /**
     * The obm services to set
     */
    @Value("${obm.services}")
    private String[] obmServices;

    @Autowired
    public ConfigureObmSettings(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Execute Configuring Obm Settings");

        NodeDetail nodeDetail = (NodeDetail) delegateExecution.getVariable(NODE_DETAIL);

        ObmSettingsResponse obmSettingsResponse = null;
        try
        {
            String uuid = nodeDetail.getId();
            String ipAddress = nodeDetail.getIdracIpAddress();

            SetObmSettingsRequestMessage configureObmSettingsRequest = new SetObmSettingsRequestMessage();
            configureObmSettingsRequest.setServices(Arrays.asList(obmServices));
            configureObmSettingsRequest.setUuid(uuid);

            ObmConfig obmConfig = new ObmConfig();
            obmConfig.setHost(ipAddress);
            configureObmSettingsRequest.setObmConfig(obmConfig);

            obmSettingsResponse = nodeService.obmSettingsResponse(configureObmSettingsRequest);
        }
        catch (Exception e)
        {
            LOGGER.error("An Unexpected Exception occurred while attempting to Configure the Obm Settings on Node " +
                         nodeDetail.getServiceTag(), e);
            updateDelegateStatus(
                    "An Unexpected Exception occurred while attempting to Configure the Obm Settings on Node " +
                    nodeDetail.getServiceTag());
            throw new BpmnError(CONFIGURE_OBM_SETTINGS_FAILED,
                                "An Unexpected Exception occurred while attempting to Configure the Obm Settings on Node " +
                                nodeDetail.getServiceTag() + ".  Reason: " + e.getMessage());
        }
        if (obmSettingsResponse == null || !"SUCCESS".equalsIgnoreCase(obmSettingsResponse.getStatus()))
        {
            LOGGER.error("Obm Settings on Node " + nodeDetail.getServiceTag() + " were not configured.");
            updateDelegateStatus("Obm Settings on Node " + nodeDetail.getServiceTag() + " were not configured.");
            throw new BpmnError(CONFIGURE_OBM_SETTINGS_FAILED,
                                "Obm Settings on Node " + nodeDetail.getServiceTag() + " were not configured.");
        }
        LOGGER.info("Obm Settings on Node " + nodeDetail.getServiceTag() + " were configured successfully.");
        updateDelegateStatus("Obm Settings on Node " + nodeDetail.getServiceTag() + " were configured successfully.");

    }
}

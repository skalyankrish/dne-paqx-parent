/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 **/

package com.dell.cpsd.paqx.dne.transformers;

import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.virtualization.capabilities.api.RemoteCommandExecutionRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.VmPasswordUpdateRequest;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;

/**
 * Performance Tune ScaleIO VM Transformer that builds the
 * {@link com.dell.cpsd.virtualization.capabilities.api.RemoteCommandExecutionRequestMessage}
 * request message
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@Component
public class RemoteCommandExecutionRequestTransformer
{
    private static final String COMPONENT_TYPE      = "COMMON-SERVER";
    private static final String ENDPOINT_TYPE       = "COMMON-SVM";
    private static final String COMMON_CREDENTIALS  = "SVM-COMMON";
    private static final String FACTORY_CREDENTIALS = "SVM-FACTORY";

    private final DataServiceRepository repository;

    public RemoteCommandExecutionRequestTransformer(final DataServiceRepository repository)
    {
        this.repository = repository;
    }

    public RemoteCommandExecutionRequestMessage buildRemoteCodeExecutionRequest(final DelegateExecution delegateExecution,
            final RemoteCommandExecutionRequestMessage.RemoteCommand remoteCommand)
    {
        final ComponentEndpointIds componentEndpointIds = getComponentEndpointIds(COMMON_CREDENTIALS);
        final NodeDetail nodeDetail = (NodeDetail) delegateExecution.getVariable(NODE_DETAIL);
        final String scaleIoSvmManagementIpAddress = nodeDetail.getScaleIoSvmManagementIpAddress();

        final RemoteCommandExecutionRequestMessage requestMessage = new RemoteCommandExecutionRequestMessage();
        requestMessage.setRemoteCommand(remoteCommand);
        requestMessage.setRemoteHost(scaleIoSvmManagementIpAddress);
        requestMessage.setOsType(RemoteCommandExecutionRequestMessage.OsType.LINUX);
        requestMessage.setComponentEndpointIds(
                new com.dell.cpsd.virtualization.capabilities.api.ComponentEndpointIds(componentEndpointIds.getComponentUuid(),
                        componentEndpointIds.getEndpointUuid(), componentEndpointIds.getCredentialUuid()));

        if (remoteCommand.equals(RemoteCommandExecutionRequestMessage.RemoteCommand.CHANGE_PASSWORD))
        {
            setChangePasswordRequest(requestMessage);
        }

        return requestMessage;
    }

    private void setChangePasswordRequest(final RemoteCommandExecutionRequestMessage requestMessage)
    {
        final ComponentEndpointIds factoryComponentEndpointIds = getComponentEndpointIds(FACTORY_CREDENTIALS);
        final VmPasswordUpdateRequest vmPasswordUpdateRequest = new VmPasswordUpdateRequest();
        vmPasswordUpdateRequest.setCredentialName(VmPasswordUpdateRequest.CredentialName.SVM_FACTORY);
        vmPasswordUpdateRequest.setComponentEndpointIds(
                new com.dell.cpsd.virtualization.capabilities.api.ComponentEndpointIds(factoryComponentEndpointIds.getComponentUuid(),
                        factoryComponentEndpointIds.getEndpointUuid(), factoryComponentEndpointIds.getCredentialUuid()));
        requestMessage.setVmPasswordUpdateRequest(vmPasswordUpdateRequest);
    }

    private ComponentEndpointIds getComponentEndpointIds(final String credentialType)
    {
        final ComponentEndpointIds componentEndpointIds = repository.getComponentEndpointIds(COMPONENT_TYPE, ENDPOINT_TYPE, credentialType);

        if (componentEndpointIds == null)
        {
            throw new IllegalStateException("No component ids found.");
        }
        return componentEndpointIds;
    }
}

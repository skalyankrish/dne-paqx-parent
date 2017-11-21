/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 **/

package com.dell.cpsd.paqx.dne.transformers;

import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.virtualization.capabilities.api.Credentials;
import com.dell.cpsd.virtualization.capabilities.api.DatastoreRenameRequestMessage;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.HOSTNAME;

/**
 * Datastore rename request transformer
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@Component
public class DatastoreRenameRequestTransformer
{
    private static final String VCENTER_CUSTOMER_TYPE = "VCENTER-CUSTOMER";

    private final ComponentIdsTransformer componentIdsTransformer;

    public DatastoreRenameRequestTransformer(final ComponentIdsTransformer componentIdsTransformer)
    {
        this.componentIdsTransformer = componentIdsTransformer;
    }

    public DatastoreRenameRequestMessage buildDatastoreRenameRequest(final DelegateExecution delegateExecution)
    {
        final String hostname = (String) delegateExecution.getVariable(HOSTNAME);
        final ComponentEndpointIds componentEndpointIds = componentIdsTransformer
                .getVCenterComponentEndpointIdsByEndpointType(VCENTER_CUSTOMER_TYPE);

        return getDatastoreRenameRequestMessage(hostname, componentEndpointIds);
    }

    private DatastoreRenameRequestMessage getDatastoreRenameRequestMessage(final String hostname,
            final ComponentEndpointIds componentEndpointIds)
    {
        final DatastoreRenameRequestMessage requestMessage = new DatastoreRenameRequestMessage();
        requestMessage.setHostname(hostname);
        requestMessage.setCredentials(new Credentials(componentEndpointIds.getEndpointUrl(), null, null));
        requestMessage.setComponentEndpointIds(
                new com.dell.cpsd.virtualization.capabilities.api.ComponentEndpointIds(componentEndpointIds.getComponentUuid(),
                        componentEndpointIds.getEndpointUuid(), componentEndpointIds.getCredentialUuid()));

        return requestMessage;
    }
}

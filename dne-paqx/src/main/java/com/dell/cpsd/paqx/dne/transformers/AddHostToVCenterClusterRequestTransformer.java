/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 **/

package com.dell.cpsd.paqx.dne.transformers;

import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.delegates.model.ESXiCredentialDetails;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.virtualization.capabilities.api.ClusterOperationRequest;
import com.dell.cpsd.virtualization.capabilities.api.ClusterOperationRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.Credentials;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.ESXI_CREDENTIAL_DETAILS;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.HOSTNAME;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.VCENTER_CLUSTER_NAME;

/**
 * Add Host To VCenter Cluster request message transformer
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@Component
public class AddHostToVCenterClusterRequestTransformer
{
    private static final String VCENTER_CUSTOMER_TYPE = "VCENTER-CUSTOMER";

    private final DataServiceRepository   repository;
    private final ComponentIdsTransformer componentIdsTransformer;

    public AddHostToVCenterClusterRequestTransformer(final DataServiceRepository repository,
            final ComponentIdsTransformer componentIdsTransformer)
    {
        this.repository = repository;
        this.componentIdsTransformer = componentIdsTransformer;
    }

    public ClusterOperationRequestMessage buildAddHostToVCenterRequest(final DelegateExecution delegateExecution)
    {
        final String clusterName = (String) delegateExecution.getVariable(VCENTER_CLUSTER_NAME);
        final String hostname = (String) delegateExecution.getVariable(HOSTNAME);
        final ComponentEndpointIds componentEndpointIds = componentIdsTransformer
                .getVCenterComponentEndpointIdsByEndpointType(VCENTER_CUSTOMER_TYPE);
        final ESXiCredentialDetails esxiCredentialDetails = (ESXiCredentialDetails) delegateExecution.getVariable(ESXI_CREDENTIAL_DETAILS);
        final String clusterId = repository.getClusterId(clusterName);

        return getClusterOperationRequestMessage(componentEndpointIds, hostname, clusterId, esxiCredentialDetails);
    }

    private ClusterOperationRequestMessage getClusterOperationRequestMessage(final ComponentEndpointIds componentEndpointIds,
            final String hostname, final String clusterId, final ESXiCredentialDetails esxiCredentialDetails)
    {
        final ClusterOperationRequestMessage requestMessage = new ClusterOperationRequestMessage();
        requestMessage.setCredentials(new Credentials(componentEndpointIds.getEndpointUrl(), null, null));
        requestMessage.setComponentEndpointIds(
                new com.dell.cpsd.virtualization.capabilities.api.ComponentEndpointIds(componentEndpointIds.getComponentUuid(),
                        componentEndpointIds.getEndpointUuid(), componentEndpointIds.getCredentialUuid()));
        getClusterOperationRequest(hostname, clusterId, esxiCredentialDetails, requestMessage);
        return requestMessage;
    }

    private void getClusterOperationRequest(final String hostname, final String clusterId,
            final ESXiCredentialDetails esxiCredentialDetails, final ClusterOperationRequestMessage requestMessage)
    {
        final ClusterOperationRequest clusterOperationRequest = new ClusterOperationRequest();
        clusterOperationRequest.setHostName(hostname);
        clusterOperationRequest.setComponentEndpointIds(
                new com.dell.cpsd.virtualization.capabilities.api.ComponentEndpointIds(esxiCredentialDetails.getComponentUuid(),
                        esxiCredentialDetails.getEndpointUuid(), esxiCredentialDetails.getCredentialUuid()));
        clusterOperationRequest.setClusterOperation(ClusterOperationRequest.ClusterOperation.ADD_HOST);
        clusterOperationRequest.setClusterID(clusterId);
        requestMessage.setClusterOperationRequest(clusterOperationRequest);
    }
}

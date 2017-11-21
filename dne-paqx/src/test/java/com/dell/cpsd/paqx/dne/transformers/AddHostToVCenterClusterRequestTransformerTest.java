/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 **/

package com.dell.cpsd.paqx.dne.transformers;

import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.delegates.model.ESXiCredentialDetails;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.virtualization.capabilities.api.ClusterOperationRequestMessage;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.ESXI_CREDENTIAL_DETAILS;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.HOSTNAME;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.VCENTER_CLUSTER_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Test class for Add Host to VCenter Cluster request transformer
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class AddHostToVCenterClusterRequestTransformerTest
{
    @Mock
    private DataServiceRepository dataServiceRepository;

    @Mock
    private ComponentIdsTransformer componentIdsTransformer;

    @Mock
    private DelegateExecution delegateExecution;

    @Mock
    private ComponentEndpointIds componentEndpointIds;

    @Mock
    private ESXiCredentialDetails esxiCredentialDetails;

    private AddHostToVCenterClusterRequestTransformer transformer;
    private static final String VCENTER_CUSTOMER_TYPE = "VCENTER-CUSTOMER";
    private              String clusterName           = "clustername-1";
    private              String clusterId             = "clustername-1d-1";
    private              String hostName              = "hostname-1";

    @Before
    public void setUp() throws Exception
    {
        this.transformer = new AddHostToVCenterClusterRequestTransformer(this.dataServiceRepository, this.componentIdsTransformer);
    }

    @Test
    public void testBuildAddHostToVCenterRequestIsValid() throws Exception
    {
        when(this.delegateExecution.getVariable(VCENTER_CLUSTER_NAME)).thenReturn(this.clusterName);
        when(this.delegateExecution.getVariable(HOSTNAME)).thenReturn(this.hostName);
        when(componentIdsTransformer.getVCenterComponentEndpointIdsByEndpointType(VCENTER_CUSTOMER_TYPE))
                .thenReturn(this.componentEndpointIds);
        when(this.delegateExecution.getVariable(ESXI_CREDENTIAL_DETAILS)).thenReturn(this.esxiCredentialDetails);
        when(this.dataServiceRepository.getClusterId(anyString())).thenReturn(this.clusterId);

        final ClusterOperationRequestMessage clusterOperationRequestMessage = this.transformer
                .buildAddHostToVCenterRequest(this.delegateExecution);

        assertNotNull(clusterOperationRequestMessage);
        assertNotNull(clusterOperationRequestMessage.getClusterOperationRequest());
        assertEquals(this.hostName, clusterOperationRequestMessage.getClusterOperationRequest().getHostName());
        assertEquals(this.clusterId, clusterOperationRequestMessage.getClusterOperationRequest().getClusterID());
        assertNotNull(clusterOperationRequestMessage.getComponentEndpointIds());
        assertNotNull(clusterOperationRequestMessage.getCredentials());
    }
}
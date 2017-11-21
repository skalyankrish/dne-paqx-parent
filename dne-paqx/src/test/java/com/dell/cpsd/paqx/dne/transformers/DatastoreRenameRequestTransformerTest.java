/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 **/

package com.dell.cpsd.paqx.dne.transformers;

import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.virtualization.capabilities.api.DatastoreRenameRequestMessage;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.HOSTNAME;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Unit test for datastore rename request transformer.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class DatastoreRenameRequestTransformerTest
{
    @Mock
    private ComponentIdsTransformer componentIdsTransformer;

    @Mock
    private DelegateExecution delegateExecution;

    @Mock
    private ComponentEndpointIds componentEndpointIds;

    private DatastoreRenameRequestTransformer datastoreRenameRequestTransformer;
    private static final String VCENTER_CUSTOMER_TYPE        = "VCENTER-CUSTOMER";
    private final        String hostname                     = "hostname";


    @Before
    public void setup() throws Exception
    {
        datastoreRenameRequestTransformer = new DatastoreRenameRequestTransformer(componentIdsTransformer);
    }

    @Test
    public void testBuildDatastoreRenameRequestIsValid() throws Exception
    {
        when(delegateExecution.getVariable(HOSTNAME)).thenReturn(hostname);
        when(componentIdsTransformer.getVCenterComponentEndpointIdsByEndpointType(VCENTER_CUSTOMER_TYPE)).thenReturn(componentEndpointIds);

        final DatastoreRenameRequestMessage requestMessage = datastoreRenameRequestTransformer
                .buildDatastoreRenameRequest(delegateExecution);

        assertNotNull(requestMessage);
        assertEquals(hostname, requestMessage.getHostname());
        assertNotNull(requestMessage.getCredentials());
        assertNotNull(requestMessage.getComponentEndpointIds());
    }
}

/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 **/

package com.dell.cpsd.paqx.dne.transformers;

import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.virtualization.capabilities.api.HostPowerOperationRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.PowerOperationRequest;
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
 * Unit tests for reboot host request transformer
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class RebootHostRequestTransformerTest
{
    @Mock
    private ComponentIdsTransformer componentIdsTransformer;

    @Mock
    private DelegateExecution delegateExecution;

    @Mock
    private ComponentEndpointIds componentEndpointIds;

    private RebootHostRequestTransformer rebootHostRequestTransformer;

    private final String hostname = "hostname";

    @Before
    public void setup() throws Exception
    {
        rebootHostRequestTransformer = new RebootHostRequestTransformer(componentIdsTransformer);
    }

    @Test
    public void testRebootHostBuildRequestMessageIsValid() throws Exception
    {
        when(delegateExecution.getVariable(HOSTNAME)).thenReturn(hostname);
        when(componentIdsTransformer.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER")).thenReturn(componentEndpointIds);

        final HostPowerOperationRequestMessage requestMessage = rebootHostRequestTransformer
                .buildHostPowerOperationsRequestMessage(delegateExecution, PowerOperationRequest.PowerOperation.REBOOT);

        assertNotNull(requestMessage);

        final PowerOperationRequest powerOperationRequest = requestMessage.getPowerOperationRequest();

        assertNotNull(powerOperationRequest);
        assertNotNull(requestMessage.getComponentEndpointIds());
        assertEquals(hostname, powerOperationRequest.getHostName());
        assertEquals(PowerOperationRequest.PowerOperation.REBOOT, powerOperationRequest.getPowerOperation());
    }
}

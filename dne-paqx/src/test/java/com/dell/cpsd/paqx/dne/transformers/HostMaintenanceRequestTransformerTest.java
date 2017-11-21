/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 **/

package com.dell.cpsd.paqx.dne.transformers;

import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.virtualization.capabilities.api.HostMaintenanceModeRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.MaintenanceModeRequest;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.HOSTNAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * Unit tests for Host Maintenance request transformer
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class HostMaintenanceRequestTransformerTest
{
    @Mock
    private ComponentIdsTransformer componentIdsTransformer;

    @Mock
    private DelegateExecution delegateExecution;

    @Mock
    private ComponentEndpointIds componentEndpointIds;

    private HostMaintenanceRequestTransformer hostMaintenanceRequestTransformer;

    private final        String hostname              = "hostname";
    private static final String VCENTER_CUSTOMER_TYPE = "VCENTER-CUSTOMER";

    @Before
    public void setup() throws Exception
    {
        hostMaintenanceRequestTransformer = new HostMaintenanceRequestTransformer(componentIdsTransformer);
    }

    @Test
    public void testBuildMaintenanceModeEnableRequestMessageIsValid() throws Exception
    {
        when(delegateExecution.getVariable(HOSTNAME)).thenReturn(hostname);
        when(componentIdsTransformer.getVCenterComponentEndpointIdsByEndpointType(VCENTER_CUSTOMER_TYPE)).thenReturn(componentEndpointIds);

        final HostMaintenanceModeRequestMessage requestMessage = hostMaintenanceRequestTransformer
                .buildHostMaintenanceRequest(delegateExecution, true);

        assertNotNull(requestMessage);
        assertNotNull(requestMessage.getComponentEndpointIds());

        final MaintenanceModeRequest maintenanceModeRequest = requestMessage.getMaintenanceModeRequest();

        assertNotNull(maintenanceModeRequest);

        assertEquals(true, maintenanceModeRequest.getMaintenanceModeEnable());
        assertEquals(hostname, maintenanceModeRequest.getHostName());
    }

    @Test
    public void testBuildMaintenanceModeExitRequestMessageIsValid() throws Exception
    {
        when(delegateExecution.getVariable(HOSTNAME)).thenReturn(hostname);
        when(componentIdsTransformer.getVCenterComponentEndpointIdsByEndpointType(VCENTER_CUSTOMER_TYPE)).thenReturn(componentEndpointIds);

        final HostMaintenanceModeRequestMessage requestMessage = hostMaintenanceRequestTransformer
                .buildHostMaintenanceRequest(delegateExecution, false);

        assertNotNull(requestMessage);
        assertNotNull(requestMessage.getComponentEndpointIds());

        final MaintenanceModeRequest maintenanceModeRequest = requestMessage.getMaintenanceModeRequest();

        assertNotNull(maintenanceModeRequest);

        assertEquals(false, maintenanceModeRequest.getMaintenanceModeEnable());
        assertEquals(hostname, maintenanceModeRequest.getHostName());
    }
}

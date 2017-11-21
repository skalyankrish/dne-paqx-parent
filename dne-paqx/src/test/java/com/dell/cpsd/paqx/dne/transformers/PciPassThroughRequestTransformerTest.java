/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 **/

package com.dell.cpsd.paqx.dne.transformers;

import com.dell.cpsd.paqx.dne.domain.vcenter.PciDevice;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.virtualization.capabilities.api.EnablePCIPassthroughRequestMessage;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.HOSTNAME;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * Unit tests for PCI Passthrough request transformer
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class PciPassThroughRequestTransformerTest
{
    @Mock
    private ComponentIdsTransformer componentIdsTransformer;

    @Mock
    private DelegateExecution delegateExecution;

    @Mock
    private ComponentEndpointIds componentEndpointIds;

    @Mock
    private DataServiceRepository repository;

    @Mock
    private PciDevice pciDevice;

    private final String hostname = "hostname";

    private PciPassThroughRequestTransformer pciPassThroughRequestTransformer;
    private static final String VCENTER_CUSTOMER_TYPE = "VCENTER-CUSTOMER";
    private static final String PCI_BUS_DEVICE_ID     = "0000:02:00.0";

    @Before
    public void setup() throws Exception
    {
        pciPassThroughRequestTransformer = new PciPassThroughRequestTransformer(repository, componentIdsTransformer);
    }

    @Test
    public void testBuildEnablePciPassThroughWhenEmptyPciDevicesRequestMessageIsValid() throws Exception
    {
        when(delegateExecution.getVariable(HOSTNAME)).thenReturn(hostname);
        when(componentIdsTransformer.getVCenterComponentEndpointIdsByEndpointType(VCENTER_CUSTOMER_TYPE)).thenReturn(componentEndpointIds);

        final EnablePCIPassthroughRequestMessage requestMessage = pciPassThroughRequestTransformer
                .buildEnablePciPassThroughRequest(delegateExecution);

        assertNotNull(requestMessage);
        assertNotNull(requestMessage.getComponentEndpointIds());

        assertEquals(hostname, requestMessage.getHostname());
        assertEquals(PCI_BUS_DEVICE_ID, requestMessage.getHostPciDeviceId());
    }

    @Test
    public void testBuildEnablePciPassThroughWhenValidPciDeviceFoundRequestMessageIsValid() throws Exception
    {
        when(delegateExecution.getVariable(HOSTNAME)).thenReturn(hostname);
        when(componentIdsTransformer.getVCenterComponentEndpointIdsByEndpointType(VCENTER_CUSTOMER_TYPE)).thenReturn(componentEndpointIds);
        when(repository.getPciDeviceList()).thenReturn(singletonList(pciDevice));
        when(pciDevice.getDeviceName()).thenReturn("Dell HBA330 Mini");
        when(pciDevice.getId()).thenReturn(PCI_BUS_DEVICE_ID);

        final EnablePCIPassthroughRequestMessage requestMessage = pciPassThroughRequestTransformer
                .buildEnablePciPassThroughRequest(delegateExecution);

        assertNotNull(requestMessage);
        assertNotNull(requestMessage.getComponentEndpointIds());

        assertEquals(hostname, requestMessage.getHostname());
        assertEquals(PCI_BUS_DEVICE_ID, requestMessage.getHostPciDeviceId());
    }
}

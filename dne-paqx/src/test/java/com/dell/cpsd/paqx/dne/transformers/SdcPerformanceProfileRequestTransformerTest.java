/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 **/

package com.dell.cpsd.paqx.dne.transformers;

import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.storage.capabilities.api.PerformanceProfileRequest;
import com.dell.cpsd.storage.capabilities.api.SioSdcUpdatePerformanceProfileRequestMessage;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * Unit test for SDC Performance Profile Request Transformer
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class SdcPerformanceProfileRequestTransformerTest
{
    @Mock
    private ComponentIdsTransformer componentIdsTransformer;

    @Mock
    private DelegateExecution delegateExecution;

    @Mock
    private ComponentEndpointIds componentEndpointIds;

    @Mock
    private NodeDetail nodeDetail;

    private SdcPerformanceProfileRequestTransformer sdcPerformanceProfileRequestTransformer;

    private final String guidString              = "12234fjsd2323";
    private final String esxiManagementIpAddress = "1.1.1.1";
    private final String endpointUrl             = "hello.com";

    @Before
    public void setup() throws Exception
    {
        sdcPerformanceProfileRequestTransformer = new SdcPerformanceProfileRequestTransformer(componentIdsTransformer);
    }

    @Test
    public void testMessageBuildRequestIsValid() throws Exception
    {
        when(componentIdsTransformer.getComponentEndpointIdsByComponentType("SCALEIO-CLUSTER")).thenReturn(componentEndpointIds);
        when(delegateExecution.getVariable(DelegateConstants.NODE_DETAIL)).thenReturn(nodeDetail);
        when(delegateExecution.getVariable(DelegateConstants.IOCTL_INI_GUI_STR)).thenReturn(guidString);
        when(nodeDetail.getEsxiManagementIpAddress()).thenReturn(esxiManagementIpAddress);
        when(componentEndpointIds.getEndpointUrl()).thenReturn(endpointUrl);

        final SioSdcUpdatePerformanceProfileRequestMessage requestMessage = sdcPerformanceProfileRequestTransformer
                .buildSdcPerformanceProfileRequest(delegateExecution);

        assertNotNull(requestMessage);

        assertNotNull(requestMessage.getComponentEndpointIds());
        assertNotNull(requestMessage.getEndpointUrl());

        assertEquals("https://" + endpointUrl, requestMessage.getEndpointUrl());

        final PerformanceProfileRequest performanceProfileRequest = requestMessage.getPerformanceProfileRequest();

        assertNotNull(performanceProfileRequest);
        assertEquals(guidString, performanceProfileRequest.getSdcGuid());
        assertEquals(esxiManagementIpAddress, performanceProfileRequest.getSdcIp());
        assertEquals(PerformanceProfileRequest.PerfProfile.HIGH_PERFORMANCE, performanceProfileRequest.getPerfProfile());
    }
}

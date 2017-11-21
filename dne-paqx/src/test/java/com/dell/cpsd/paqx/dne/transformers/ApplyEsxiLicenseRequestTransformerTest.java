/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 **/

package com.dell.cpsd.paqx.dne.transformers;

import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.virtualization.capabilities.api.AddEsxiHostVSphereLicenseRequest;
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
 * Unit test class for apply ESXi host license request transformer
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class ApplyEsxiLicenseRequestTransformerTest
{
    @Mock
    ComponentIdsTransformer componentIdsTransformer;

    @Mock
    private DelegateExecution delegateExecution;

    @Mock
    private ComponentEndpointIds componentEndpointIds;

    private ApplyEsxiLicenseRequestTransformer transformer;
    private static final String VCENTER_CUSTOMER_TYPE = "VCENTER-CUSTOMER";
    private              String hostName              = "hostname-1";

    @Before
    public void setUp() throws Exception
    {
        this.transformer = new ApplyEsxiLicenseRequestTransformer(this.componentIdsTransformer);
    }

    @Test
    public void testBuildApplyEsxiLicenseRequestIsValid() throws Exception
    {
        when(this.delegateExecution.getVariable(HOSTNAME)).thenReturn(this.hostName);
        when(componentIdsTransformer.getVCenterComponentEndpointIdsByEndpointType(VCENTER_CUSTOMER_TYPE))
                .thenReturn(this.componentEndpointIds);

        final AddEsxiHostVSphereLicenseRequest addEsxiHostVSphereLicenseRequest = this.transformer
                .buildApplyEsxiLicenseRequest(this.delegateExecution);

        assertNotNull(addEsxiHostVSphereLicenseRequest);
        assertNotNull(addEsxiHostVSphereLicenseRequest.getComponentEndpointIds());
        assertNotNull(addEsxiHostVSphereLicenseRequest.getCredentials());
        assertEquals(this.hostName, addEsxiHostVSphereLicenseRequest.getHostname());
    }
}
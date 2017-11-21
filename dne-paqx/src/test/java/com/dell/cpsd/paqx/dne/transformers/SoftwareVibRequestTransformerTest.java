/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 **/

package com.dell.cpsd.paqx.dne.transformers;

import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOData;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOIP;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOMdmCluster;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOSDSElementInfo;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.delegates.model.ESXiCredentialDetails;
import com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.virtualization.capabilities.api.SoftwareVIBConfigureRequest;
import com.dell.cpsd.virtualization.capabilities.api.SoftwareVIBConfigureRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.SoftwareVIBRequest;
import com.dell.cpsd.virtualization.capabilities.api.SoftwareVIBRequestMessage;
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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Test class for SoftwareVibRequestTransformer
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class SoftwareVibRequestTransformerTest
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
    private ScaleIOData scaleIOData;

    @Mock
    private ESXiCredentialDetails esXiCredentialDetails;

    @Mock
    private ScaleIOMdmCluster scaleIOMdmCluster;

    @Mock
    private ScaleIOSDSElementInfo masterElementInfo;

    @Mock
    private ScaleIOSDSElementInfo slaveElementInfo;

    @Mock
    private ScaleIOIP masterIp;

    @Mock
    private ScaleIOIP slaveIp;

    private SoftwareVibRequestTransformer softwareVibRequestTransformer;

    private final        String remoteVibUrl          = "https://remote.vib.url";
    private final        String hostname              = "host.name";
    private static final String VCENTER_CUSTOMER_TYPE = "VCENTER-CUSTOMER";
    private final        String masterIpAddress       = "2.2.2.2";
    private final        String slaveIpAddress        = "3.3.3.3";

    @Before
    public void setup() throws Exception
    {
        softwareVibRequestTransformer = new SoftwareVibRequestTransformer(dataServiceRepository, remoteVibUrl, componentIdsTransformer);
    }

    @Test
    public void testBuildInstallSoftwareVibRequestIsValid() throws Exception
    {
        when(delegateExecution.getVariable(HOSTNAME)).thenReturn(hostname);
        when(componentIdsTransformer.getVCenterComponentEndpointIdsByEndpointType(VCENTER_CUSTOMER_TYPE)).thenReturn(componentEndpointIds);

        final SoftwareVIBRequestMessage softwareVIBRequestMessage = softwareVibRequestTransformer
                .buildInstallSoftwareVibRequest(delegateExecution);

        assertNotNull(softwareVIBRequestMessage);

        assertNotNull(softwareVIBRequestMessage.getComponentEndpointIds());
        assertNotNull(softwareVIBRequestMessage.getCredentials());

        final SoftwareVIBRequest softwareVibInstallRequest = softwareVIBRequestMessage.getSoftwareVibInstallRequest();

        assertNotNull(softwareVibInstallRequest);

        assertEquals(hostname, softwareVibInstallRequest.getHostName());
        assertEquals(SoftwareVIBRequest.VibOperation.INSTALL, softwareVibInstallRequest.getVibOperation());
        assertEquals(singletonList(remoteVibUrl), softwareVibInstallRequest.getVibUrls());
    }

    @Test(expected = IllegalStateException.class)
    public void testBuildConfigureSoftwareVibRequestNullMdmClusterThrowsIllegalStateException() throws Exception
    {
        when(delegateExecution.getVariable(HOSTNAME)).thenReturn(hostname);
        when(componentIdsTransformer.getVCenterComponentEndpointIdsByEndpointType(VCENTER_CUSTOMER_TYPE)).thenReturn(componentEndpointIds);
        when(delegateExecution.getVariable(DelegateConstants.ESXI_CREDENTIAL_DETAILS)).thenReturn(esXiCredentialDetails);
        when(dataServiceRepository.getScaleIoData()).thenReturn(scaleIOData);

        softwareVibRequestTransformer.buildConfigureSoftwareVibRequest(delegateExecution);
    }

    @Test
    public void testBuildConfigureSoftwareVibRequestIsValid() throws Exception
    {
        when(delegateExecution.getVariable(HOSTNAME)).thenReturn(hostname);
        when(componentIdsTransformer.getVCenterComponentEndpointIdsByEndpointType(VCENTER_CUSTOMER_TYPE)).thenReturn(componentEndpointIds);
        when(delegateExecution.getVariable(DelegateConstants.ESXI_CREDENTIAL_DETAILS)).thenReturn(esXiCredentialDetails);

        when(dataServiceRepository.getScaleIoData()).thenReturn(scaleIOData);
        when(scaleIOData.getMdmCluster()).thenReturn(scaleIOMdmCluster);

        when(masterIp.getType()).thenReturn("master");
        when(scaleIOMdmCluster.getMasterElementInfo()).thenReturn(singletonList(masterElementInfo));
        when(masterElementInfo.getRole()).thenReturn("Master");
        when(masterElementInfo.getIps()).thenReturn(singletonList(masterIp));
        when(masterIp.getIp()).thenReturn(masterIpAddress);
        when(masterIp.getSdsElementInfo()).thenReturn(masterElementInfo);

        when(slaveIp.getType()).thenReturn("slave");
        when(scaleIOMdmCluster.getSlaveElementInfo()).thenReturn(singletonList(slaveElementInfo));
        when(slaveElementInfo.getRole()).thenReturn("Slave");
        when(slaveElementInfo.getIps()).thenReturn(singletonList(slaveIp));
        when(slaveIp.getIp()).thenReturn(slaveIpAddress);
        when(slaveIp.getSdsElementInfo()).thenReturn(slaveElementInfo);

        final SoftwareVIBConfigureRequestMessage requestMessage = softwareVibRequestTransformer
                .buildConfigureSoftwareVibRequest(delegateExecution);

        assertNotNull(requestMessage);

        final SoftwareVIBConfigureRequest softwareVIBConfigureRequest = requestMessage.getSoftwareVIBConfigureRequest();

        assertNotNull(softwareVIBConfigureRequest);
        assertEquals(hostname, softwareVIBConfigureRequest.getHostName());
        assertEquals("scini", softwareVIBConfigureRequest.getModuleName());
        assertNotNull(softwareVIBConfigureRequest.getModuleOptions());
        assertTrue(softwareVIBConfigureRequest.getModuleOptions().contains(masterIpAddress));
        assertTrue(softwareVIBConfigureRequest.getModuleOptions().contains(slaveIpAddress));
    }

}
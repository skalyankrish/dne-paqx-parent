/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 **/

package com.dell.cpsd.paqx.dne.transformers;

import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.virtualization.capabilities.api.AddHostToDvSwitchRequestMessage;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.HOSTNAME;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit Tests for Configure DV Switches Request Transformer
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class ConfigureDvSwitchesTransformerTest
{
    @Mock
    private DataServiceRepository dataServiceRepository;

    @Mock
    private ComponentIdsTransformer componentIdsTransformer;

    @Mock
    private DelegateExecution delegateExecution;

    @Mock
    private ComponentEndpointIds componentEndpointIds;

    private ConfigureDvSwitchesTransformer transformer;

    private String hostName = "hostname-1";

    private NodeDetail          nodeDetail;
    private Map<String, String> dvSwitchNames;
    private Map<String, String> dvPortGroupNames;

    @Before
    public void setUp() throws Exception
    {
        this.transformer = new ConfigureDvSwitchesTransformer(this.dataServiceRepository, this.componentIdsTransformer);

        this.nodeDetail = new NodeDetail();
        this.nodeDetail.setId("id");
        this.nodeDetail.setServiceTag("servicetag-1");
        this.nodeDetail.setIdracIpAddress("1.1.1.1");
        this.nodeDetail.setIdracGatewayIpAddress("1.1.1.2");
        this.nodeDetail.setIdracSubnetMask("255.255.255.0");
        this.nodeDetail.setEsxiManagementIpAddress("1.1.1.3");
        this.nodeDetail.setEsxiManagementGatewayIpAddress("1.1.1.4");
        this.nodeDetail.setEsxiManagementSubnetMask("255.255.255.0");
        this.nodeDetail.setEsxiManagementHostname("esxi-h1");
        this.nodeDetail.setScaleIoData1SvmIpAddress("1.1.1.5");
        this.nodeDetail.setScaleIoData1SvmSubnetMask("255.255.255.0");
        this.nodeDetail.setScaleIoData2SvmIpAddress("1.1.1.6");
        this.nodeDetail.setScaleIoData2SvmSubnetMask("255.255.255.0");
        this.nodeDetail.setScaleIoData1EsxIpAddress("1.1.1.7");
        this.nodeDetail.setScaleIoData1EsxSubnetMask("255.255.255.0");
        this.nodeDetail.setScaleIoData2EsxIpAddress("1.1.1.8");
        this.nodeDetail.setScaleIoData2EsxSubnetMask("255.255.255.0");
        this.nodeDetail.setScaleIoSvmManagementIpAddress("1.1.1.9");
        this.nodeDetail.setScaleIoSvmManagementGatewayAddress("1.1.1.10");
        this.nodeDetail.setScaleIoSvmManagementSubnetMask("255.255.255.0");
        this.nodeDetail.setEsxiManagementHostname(this.nodeDetail.getEsxiManagementHostname());
        this.nodeDetail.setClusterName("cluster-1");
        this.nodeDetail.setvMotionManagementIpAddress("1.1.1.8");
        this.nodeDetail.setvMotionManagementSubnetMask("255.255.255.0");
        this.nodeDetail.setProtectionDomainName("protection-domain-1");

        this.dvSwitchNames = new HashMap<>();
        this.dvSwitchNames.put("dvswitch0", "dvswitch0");
        this.dvSwitchNames.put("dvswitch1", "dvswitch1");
        this.dvSwitchNames.put("dvswitch2", "dvswitch2");

        this.dvPortGroupNames = new HashMap<>();
        this.dvPortGroupNames.put("esx-mgmt", "esx-mgmt");
        this.dvPortGroupNames.put("sio-data1", "sio-data1");
        this.dvPortGroupNames.put("sio-data2", "sio-data2");
        this.dvPortGroupNames.put("vmotion", "vmotion");
    }

    @Test
    public void testBuildAddHostToDvSwitchRequestIsValid() throws Exception
    {
        when(this.delegateExecution.getVariable(HOSTNAME)).thenReturn(this.hostName);
        when(this.delegateExecution.getVariable(NODE_DETAIL)).thenReturn(this.nodeDetail);
        when(this.componentIdsTransformer.getVCenterComponentEndpointIdsByEndpointType(anyString())).thenReturn(this.componentEndpointIds);
        when(this.dataServiceRepository.getDvSwitchNames()).thenReturn(this.dvSwitchNames);
        when(this.dataServiceRepository.getDvPortGroupNames(this.dvSwitchNames)).thenReturn(this.dvPortGroupNames);

        final AddHostToDvSwitchRequestMessage addHostToDvSwitchRequestMessage = this.transformer
                .buildAddHostToDvSwitchRequest(this.delegateExecution);

        assertNotNull(addHostToDvSwitchRequestMessage);
        assertNotNull(addHostToDvSwitchRequestMessage.getComponentEndpointIds());
        assertNotNull(addHostToDvSwitchRequestMessage.getCredentials());
        assertNotNull(addHostToDvSwitchRequestMessage.getDvsnames());
        assertNotNull(addHostToDvSwitchRequestMessage.getDvsPnicConnections());
        assertNotNull(addHostToDvSwitchRequestMessage.getDvSwitchConfigList());
        assertNotNull(addHostToDvSwitchRequestMessage.getPNicNames());
        assertEquals(this.hostName, addHostToDvSwitchRequestMessage.getHostname());
    }

    @Test
    public void testBuildAddHostToDvSwitchRequestGetDvSwitchNamesMapException() throws Exception
    {
        this.verifyDvSwitchNameException(null, "DV Switch Names are null");
    }

    @Test
    public void testBuildAddHostToDvSwitchRequestGetDvSwitchManagementNameException() throws Exception
    {
        this.dvSwitchNames.remove("dvswitch0");
        this.verifyDvSwitchNameException(this.dvSwitchNames, "DVSwitch0 name is null or empty");
    }

    @Test
    public void testBuildAddHostToDvSwitchRequestGetDvSwitchScaleIoData1NameException() throws Exception
    {
        this.dvSwitchNames.remove("dvswitch1");
        this.verifyDvSwitchNameException(this.dvSwitchNames, "DVSwitch1 name is null or empty");
    }

    @Test
    public void testBuildAddHostToDvSwitchRequestGetDvSwitchScaleIoData2NameException() throws Exception
    {
        this.dvSwitchNames.remove("dvswitch2");
        this.verifyDvSwitchNameException(this.dvSwitchNames, "DVSwitch2 name is null or empty");
    }

    @Test
    public void testBuildAddHostToDvSwitchRequestGetDvPortGroupsMapException() throws Exception
    {
        this.verifyDvPortGroupNameException(null, "DV Port Group Names are null");
    }

    @Test
    public void testBuildAddHostToDvSwitchRequestGetEsxiManagementPortGroupNameException() throws Exception
    {
        this.dvPortGroupNames.remove("esx-mgmt");
        this.verifyDvPortGroupNameException(this.dvPortGroupNames, "DV Port Group name for ESXI-MGMT is null");
    }

    @Test
    public void testBuildAddHostToDvSwitchRequestGetVMotionPortGroupNameException() throws Exception
    {
        this.dvPortGroupNames.remove("vmotion");
        this.verifyDvPortGroupNameException(this.dvPortGroupNames, "DV Port Group name for VMOTION is null");
    }

    @Test
    public void testBuildAddHostToDvSwitchRequestGetScaleIoData1PortGroupNameException() throws Exception
    {
        this.dvPortGroupNames.remove("sio-data1");
        this.verifyDvPortGroupNameException(this.dvPortGroupNames, "DV Port Group name for ScaleIO Data1 is null");
    }

    @Test
    public void testBuildAddHostToDvSwitchRequestGetScaleIoData2PortGroupNameException() throws Exception
    {
        this.dvPortGroupNames.remove("sio-data2");
        this.verifyDvPortGroupNameException(this.dvPortGroupNames, "DV Port Group name for ScaleIO Data2 is null");
    }

    private void verifyDvSwitchNameException(Map<String, String> dvSwitchNamesMap, String expectedExceptionMsg)
    {
        when(this.delegateExecution.getVariable(HOSTNAME)).thenReturn(this.hostName);
        when(this.delegateExecution.getVariable(NODE_DETAIL)).thenReturn(this.nodeDetail);
        when(this.componentIdsTransformer.getVCenterComponentEndpointIdsByEndpointType(anyString())).thenReturn(this.componentEndpointIds);
        when(this.dataServiceRepository.getDvSwitchNames()).thenReturn(dvSwitchNamesMap);

        try
        {
            this.transformer.buildAddHostToDvSwitchRequest(this.delegateExecution);
            fail("Expected Exception to be thrown but was not");
        }
        catch (IllegalStateException ex)
        {
            assertThat(ex.getMessage(), containsString(expectedExceptionMsg));
        }
    }

    private void verifyDvPortGroupNameException(Map<String, String> dvPortGroupNamesMap, String expectedExceptionMsg)
    {
        when(this.delegateExecution.getVariable(HOSTNAME)).thenReturn(this.hostName);
        when(this.delegateExecution.getVariable(NODE_DETAIL)).thenReturn(this.nodeDetail);
        when(this.componentIdsTransformer.getVCenterComponentEndpointIdsByEndpointType(anyString())).thenReturn(this.componentEndpointIds);
        when(this.dataServiceRepository.getDvSwitchNames()).thenReturn(this.dvSwitchNames);
        when(this.dataServiceRepository.getDvPortGroupNames(this.dvSwitchNames)).thenReturn(dvPortGroupNamesMap);

        try
        {
            this.transformer.buildAddHostToDvSwitchRequest(this.delegateExecution);
            fail("Expected Exception to be thrown but was not");
        }
        catch (IllegalStateException ex)
        {
            assertThat(ex.getMessage(), containsString(expectedExceptionMsg));
        }
    }
}
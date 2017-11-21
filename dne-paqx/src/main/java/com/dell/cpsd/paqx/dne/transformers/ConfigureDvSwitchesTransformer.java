/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 **/

package com.dell.cpsd.paqx.dne.transformers;

import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.virtualization.capabilities.api.AddHostToDvSwitchRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.Credentials;
import com.dell.cpsd.virtualization.capabilities.api.DvSwitchConfigList;
import com.dell.cpsd.virtualization.capabilities.api.PortGroupConfigList;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.HOSTNAME;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;

/**
 * Configure DV Switches transformer which builds the
 * {@link com.dell.cpsd.virtualization.capabilities.api.AddHostToDvSwitchRequestMessage}
 * request message.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@Component
public class ConfigureDvSwitchesTransformer
{
    private static final String VCENTER_CUSTOMER_TYPE = "VCENTER-CUSTOMER";

    private final DataServiceRepository   repository;
    private final ComponentIdsTransformer componentIdsTransformer;

    public ConfigureDvSwitchesTransformer(final DataServiceRepository repository, final ComponentIdsTransformer componentIdsTransformer)
    {
        this.repository = repository;
        this.componentIdsTransformer = componentIdsTransformer;
    }

    public AddHostToDvSwitchRequestMessage buildAddHostToDvSwitchRequest(final DelegateExecution delegateExecution)
    {
        final String hostname = (String) delegateExecution.getVariable(HOSTNAME);
        final NodeDetail nodeDetail = (NodeDetail) delegateExecution.getVariable(NODE_DETAIL);
        final String vMotionManagementIpAddress = nodeDetail.getvMotionManagementIpAddress();
        final String vMotionManagementSubnetMask = nodeDetail.getvMotionManagementSubnetMask();
        final String scaleIoData1EsxIpAddress = nodeDetail.getScaleIoData1EsxIpAddress();
        final String scaleIoData2EsxIpAddress = nodeDetail.getScaleIoData2EsxIpAddress();
        final String scaleIoData1EsxSubnetMask = nodeDetail.getScaleIoData1EsxSubnetMask();
        final String scaleIoData2EsxSubnetMask = nodeDetail.getScaleIoData2EsxSubnetMask();

        final ComponentEndpointIds componentEndpointIds = componentIdsTransformer
                .getVCenterComponentEndpointIdsByEndpointType(VCENTER_CUSTOMER_TYPE);
        final Map<String, String> dvSwitchNames = getDvSwitchNamesMap();
        final String dvSwitchManagementName = getDvSwitchManagementName(dvSwitchNames);
        final String dvSwitchScaleIoData1Name = getDvSwitchScaleIoData1Name(dvSwitchNames);
        final String dvSwitchScaleIoData2Name = getDvSwitchScaleIoData2Name(dvSwitchNames);
        final Map<String, String> dvPortGroupNames = getDvPortGroupsMap(dvSwitchNames);
        final String esxiManagementPortGroupName = getEsxiManagementPortGroupName(dvPortGroupNames);
        final String vMotionPortGroupName = getVMotionPortGroupName(dvPortGroupNames);
        final String scaleIoData1PortGroupName = getScaleIoData1PortGroupName(dvPortGroupNames);
        final String scaleIoData2PortGroupName = getScaleIoData2PortGroupName(dvPortGroupNames);

        final AddHostToDvSwitchRequestMessage requestMessage = getRequestMessage(hostname, componentEndpointIds);

        final List<DvSwitchConfigList> dvSwitchConfigList = new ArrayList<>();

        getDvSwitchManagementConfig(vMotionManagementIpAddress, vMotionManagementSubnetMask, dvSwitchNames, dvSwitchManagementName,
                dvPortGroupNames, esxiManagementPortGroupName, vMotionPortGroupName, dvSwitchConfigList);

        //DV Switch 1 - SIO Data1
        getDvSwitchScaleIoDataConfig(scaleIoData1EsxIpAddress, scaleIoData1EsxSubnetMask, dvSwitchNames, dvSwitchScaleIoData1Name,
                dvPortGroupNames, scaleIoData1PortGroupName, dvSwitchConfigList);

        //DV Switch 2 - SIO Data2
        getDvSwitchScaleIoDataConfig(scaleIoData2EsxIpAddress, scaleIoData2EsxSubnetMask, dvSwitchNames, dvSwitchScaleIoData2Name,
                dvPortGroupNames, scaleIoData2PortGroupName, dvSwitchConfigList);

        requestMessage.setDvSwitchConfigList(dvSwitchConfigList);

        return requestMessage;
    }

    private void getDvSwitchScaleIoDataConfig(final String scaleIoData1EsxIpAddress, final String scaleIoData1EsxSubnetMask,
            final Map<String, String> dvSwitchNames, final String dvSwitchScaleIoData1Name, final Map<String, String> dvPortGroupNames,
            final String scaleIoData1PortGroupName, final List<DvSwitchConfigList> dvSwitchConfigList)
    {
        final DvSwitchConfigList dvSwitchConfig1 = new DvSwitchConfigList();
        final List<PortGroupConfigList> portGroupConfigListDvSwitch1 = new ArrayList<>();
        portGroupConfigListDvSwitch1
                .add(new PortGroupConfigList(dvPortGroupNames.get(scaleIoData1PortGroupName), scaleIoData1EsxIpAddress, null,
                        scaleIoData1EsxSubnetMask));

        dvSwitchConfig1.setPortGroupConfigList(portGroupConfigListDvSwitch1);
        dvSwitchConfig1.setSwitchName(dvSwitchNames.get(dvSwitchScaleIoData1Name));

        dvSwitchConfigList.add(dvSwitchConfig1);
    }

    private void getDvSwitchManagementConfig(final String vMotionManagementIpAddress, final String vMotionManagementSubnetMask,
            final Map<String, String> dvSwitchNames, final String dvSwitchManagementName, final Map<String, String> dvPortGroupNames,
            final String esxiManagementPortGroupName, final String vMotionPortGroupName, final List<DvSwitchConfigList> dvSwitchConfigList)
    {
        //DV Switch 0 - Management and VMotion
        final DvSwitchConfigList dvSwitchConfig0 = new DvSwitchConfigList();
        final List<PortGroupConfigList> portGroupConfigListDvSwitch0 = new ArrayList<>();
        portGroupConfigListDvSwitch0.add(new PortGroupConfigList(dvPortGroupNames.get(esxiManagementPortGroupName), null, null, null));
        portGroupConfigListDvSwitch0
                .add(new PortGroupConfigList(dvPortGroupNames.get(vMotionPortGroupName), vMotionManagementIpAddress, null,
                        vMotionManagementSubnetMask));
        dvSwitchConfig0.setPortGroupConfigList(portGroupConfigListDvSwitch0);
        dvSwitchConfig0.setSwitchName(dvSwitchNames.get(dvSwitchManagementName));

        dvSwitchConfigList.add(dvSwitchConfig0);
    }

    private AddHostToDvSwitchRequestMessage getRequestMessage(final String hostname, final ComponentEndpointIds componentEndpointIds)
    {
        final AddHostToDvSwitchRequestMessage requestMessage = new AddHostToDvSwitchRequestMessage();

        requestMessage.setCredentials(new Credentials(componentEndpointIds.getEndpointUrl(), null, null));
        requestMessage.setComponentEndpointIds(
                new com.dell.cpsd.virtualization.capabilities.api.ComponentEndpointIds(componentEndpointIds.getComponentUuid(),
                        componentEndpointIds.getEndpointUuid(), componentEndpointIds.getCredentialUuid()));
        requestMessage.setHostname(hostname);
        return requestMessage;
    }

    private String getScaleIoData2PortGroupName(final Map<String, String> dvPortGroupNames)
    {
        final String DVPORT_GROUP_SIO_DATA2 = "sio-data2";

        if (StringUtils.isEmpty(dvPortGroupNames.get(DVPORT_GROUP_SIO_DATA2)))
        {
            throw new IllegalStateException("DV Port Group name for ScaleIO Data2 is null");
        }
        return DVPORT_GROUP_SIO_DATA2;
    }

    private String getScaleIoData1PortGroupName(final Map<String, String> dvPortGroupNames)
    {
        final String DVPORT_GROUP_SIO_DATA1 = "sio-data1";

        if (StringUtils.isEmpty(dvPortGroupNames.get(DVPORT_GROUP_SIO_DATA1)))
        {
            throw new IllegalStateException("DV Port Group name for ScaleIO Data1 is null");
        }
        return DVPORT_GROUP_SIO_DATA1;
    }

    private String getVMotionPortGroupName(final Map<String, String> dvPortGroupNames)
    {
        final String DVPORT_GROUP_VMOTION = "vmotion";

        if (StringUtils.isEmpty(dvPortGroupNames.get(DVPORT_GROUP_VMOTION)))
        {
            throw new IllegalStateException("DV Port Group name for VMOTION is null");
        }
        return DVPORT_GROUP_VMOTION;
    }

    private String getEsxiManagementPortGroupName(final Map<String, String> dvPortGroupNames)
    {
        final String DVPORT_GROUP_ESXI_MGMT = "esx-mgmt";

        if (StringUtils.isEmpty(dvPortGroupNames.get(DVPORT_GROUP_ESXI_MGMT)))
        {
            throw new IllegalStateException("DV Port Group name for ESXI-MGMT is null");
        }
        return DVPORT_GROUP_ESXI_MGMT;
    }

    private String getDvSwitchScaleIoData2Name(final Map<String, String> dvSwitchNames)
    {
        final String DVSWITCH2_NAME = "dvswitch2";

        if (StringUtils.isEmpty(dvSwitchNames.get(DVSWITCH2_NAME)))
        {
            throw new IllegalStateException("DVSwitch2 name is null or empty");
        }
        return DVSWITCH2_NAME;
    }

    private String getDvSwitchScaleIoData1Name(final Map<String, String> dvSwitchNames)
    {
        final String DVSWITCH1_NAME = "dvswitch1";

        if (StringUtils.isEmpty(dvSwitchNames.get(DVSWITCH1_NAME)))
        {
            throw new IllegalStateException("DVSwitch1 name is null or empty");
        }
        return DVSWITCH1_NAME;
    }

    private String getDvSwitchManagementName(final Map<String, String> dvSwitchNames)
    {
        final String DVSWITCH0_NAME = "dvswitch0";

        if (StringUtils.isEmpty(dvSwitchNames.get(DVSWITCH0_NAME)))
        {
            throw new IllegalStateException("DVSwitch0 name is null or empty");
        }
        return DVSWITCH0_NAME;
    }

    private Map<String, String> getDvPortGroupsMap(final Map<String, String> dvSwitchNames)
    {
        final Map<String, String> dvPortGroupNames = repository.getDvPortGroupNames(dvSwitchNames);

        if (dvPortGroupNames == null)
        {
            throw new IllegalStateException("DV Port Group Names are null");
        }
        return dvPortGroupNames;
    }

    private Map<String, String> getDvSwitchNamesMap()
    {
        final Map<String, String> dvSwitchNames = repository.getDvSwitchNames();
        if (dvSwitchNames == null)
        {
            throw new IllegalStateException("DV Switch Names are null");
        }
        return dvSwitchNames;
    }
}

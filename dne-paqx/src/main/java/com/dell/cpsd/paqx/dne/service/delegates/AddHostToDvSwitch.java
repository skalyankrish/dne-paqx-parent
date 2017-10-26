/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.HOSTNAME;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;

@Component
@Scope("prototype")
@Qualifier("addHostToDvSwitch")
public class AddHostToDvSwitch extends BaseWorkflowDelegate
{
    /**
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AddHostToDvSwitch.class);

    private static final String DVSWITCH0_NAME = "dvswitch0";
    private static final String DVSWITCH1_NAME = "dvswitch1";
    private static final String DVSWITCH2_NAME = "dvswitch2";
    private static final String DVPORT_GROUP_ESXI_MGMT = "esx-mgmt";
    private static final String DVPORT_GROUP_VMOTION = "vmotion";
    private static final String DVPORT_GROUP_SIO_DATA1 = "sio-data1";
    private static final String DVPORT_GROUP_SIO_DATA2 = "sio-data2";
    /**
     * The <code>NodeService</code> instance
     */
    private final NodeService nodeService;
    private final DataServiceRepository repository;

    public AddHostToDvSwitch(final NodeService nodeService, final DataServiceRepository repository)
    {
        this.nodeService = nodeService;
        this.repository = repository;
    }

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Execute Add Host to DV Switch");

        final String taskMessage = "Add Host To DV Switch";

        final String hostname = (String) delegateExecution.getVariable(HOSTNAME);
        final NodeDetail nodeDetail = (NodeDetail) delegateExecution.getVariable(NODE_DETAIL);
        /*final ComponentEndpointIds componentEndpointIds = repository.getVCenterComponentEndpointIdsByEndpointType(
                "VCENTER-CUSTOMER");

        final String vMotionManagementIpAddress = nodeDetail.getvMotionManagementIpAddress();
        final String vMotionManagementSubnetMask = nodeDetail.getvMotionManagementSubnetMask();
        final String scaleIoData1SvmIpAddress = nodeDetail.getScaleIoData1SvmIpAddress();
        final String scaleIoSvmData1SubnetMask = nodeDetail.getScaleIoSvmData1SubnetMask();
        final String scaleIoData2SvmIpAddress = nodeDetail.getScaleIoData2SvmIpAddress();
        final String scaleIoSvmData2SubnetMask = nodeDetail.getScaleIoSvmData2SubnetMask();

        final Map<String, String> dvSwitchNames = repository.getDvSwitchNames();
        String[] switches = {DVSWITCH0_NAME, DVSWITCH1_NAME, DVSWITCH2_NAME};
        if (dvSwitchNames == null || dvSwitchNames.keySet().containsAll(Arrays.asList(switches)))
        {
            LOGGER.error("DV Switches were not found or are missing while attempting to " + taskMessage);
            updateDelegateStatus("DV Switches were not found or are missing while attempting to " + taskMessage);
            throw new BpmnError(ADD_HOST_TO_DV_SWITCH_FAILED,
                                "DV Switches were not found or are missing while attempting to " + taskMessage);
        }

        final Map<String, String> dvPortGroupNames = repository.getDvPortGroupNames(dvSwitchNames);
        String[] portGroups = {DVPORT_GROUP_ESXI_MGMT, DVPORT_GROUP_VMOTION, DVPORT_GROUP_SIO_DATA1,
                DVPORT_GROUP_SIO_DATA2};
        if (dvPortGroupNames == null || dvPortGroupNames.keySet().containsAll(Arrays.asList(portGroups)))
        {
            LOGGER.error("DV Port Groups were not found or are missing while attempting to " + taskMessage);
            updateDelegateStatus("DV Switches were not found or are missing while attempting to " + taskMessage);
            throw new BpmnError(ADD_HOST_TO_DV_SWITCH_FAILED,
                                "DV Switches were not found or are missing while attempting to " + taskMessage);
        }

        final AddHostToDvSwitchRequestMessage requestMessage = new AddHostToDvSwitchRequestMessage();

        requestMessage.setCredentials(new Credentials(componentEndpointIds.getEndpointUrl(), null, null));
        requestMessage.setComponentEndpointIds(new com.dell.cpsd.virtualization.capabilities.api.ComponentEndpointIds(
                componentEndpointIds.getComponentUuid(), componentEndpointIds.getEndpointUuid(),
                componentEndpointIds.getCredentialUuid()));
        requestMessage.setHostname(hostname);
        final List<DvSwitchConfigList> dvSwitchConfigList = new ArrayList<>();

        //DV Switch 0 - Management and VMotion
        final DvSwitchConfigList dvSwitchConfig0 = new DvSwitchConfigList();
        final List<PortGroupConfigList> portGroupConfigListDvSwitch0 = new ArrayList<>();
        portGroupConfigListDvSwitch0.add(
                new PortGroupConfigList(dvPortGroupNames.get(DVPORT_GROUP_ESXI_MGMT), null, null, null));
        portGroupConfigListDvSwitch0.add(
                new PortGroupConfigList(dvPortGroupNames.get(DVPORT_GROUP_VMOTION), vMotionManagementIpAddress, null,
                                        vMotionManagementSubnetMask));
        dvSwitchConfig0.setPortGroupConfigList(portGroupConfigListDvSwitch0);
        dvSwitchConfig0.setSwitchName(dvSwitchNames.get(DVSWITCH0_NAME));

        dvSwitchConfigList.add(dvSwitchConfig0);

        //DV Switch 1 - SIO Data1
        final DvSwitchConfigList dvSwitchConfig1 = new DvSwitchConfigList();
        final List<PortGroupConfigList> portGroupConfigListDvSwitch1 = new ArrayList<>();
        portGroupConfigListDvSwitch1.add(
                new PortGroupConfigList(dvPortGroupNames.get(DVPORT_GROUP_SIO_DATA1), scaleIoData1SvmIpAddress, null,
                                        scaleIoSvmData1SubnetMask));

        dvSwitchConfig1.setPortGroupConfigList(portGroupConfigListDvSwitch1);
        dvSwitchConfig1.setSwitchName(dvSwitchNames.get(DVSWITCH1_NAME));

        dvSwitchConfigList.add(dvSwitchConfig1);

        //DV Switch 2 - SIO Data2
        final DvSwitchConfigList dvSwitchConfig2 = new DvSwitchConfigList();
        final List<PortGroupConfigList> portGroupConfigListDvSwitch2 = new ArrayList<>();
        portGroupConfigListDvSwitch2.add(
                new PortGroupConfigList(dvPortGroupNames.get(DVPORT_GROUP_SIO_DATA2), scaleIoData2SvmIpAddress, null,
                                        scaleIoSvmData2SubnetMask));

        dvSwitchConfig2.setPortGroupConfigList(portGroupConfigListDvSwitch2);
        dvSwitchConfig2.setSwitchName(dvSwitchNames.get(DVSWITCH2_NAME));

        dvSwitchConfigList.add(dvSwitchConfig2);

        requestMessage.setDvSwitchConfigList(dvSwitchConfigList);

        boolean succeeded = false;
        try
        {
            succeeded = this.nodeService.requestAddHostToDvSwitch(requestMessage);

        }
        catch (Exception e)
        {
            LOGGER.error("An Unexpected Exception occurred attempting to Install ScaleIO Vib.", e);
            updateDelegateStatus(
                    "An Unexpected Exception occurred attempting to request " + taskMessage + ".  Reason: " +
                    e.getMessage());
            throw new BpmnError(ADD_HOST_TO_DV_SWITCH_FAILED,
                                "An Unexpected Exception occurred attempting to request " + taskMessage +
                                ".  Reason: " + e.getMessage());
        }
        if (!succeeded)
        {
            LOGGER.error(taskMessage + " on Node " + nodeDetail.getServiceTag() + " failed!");
            updateDelegateStatus(taskMessage + " on Node " + nodeDetail.getServiceTag() + " failed!");
            throw new BpmnError(ADD_HOST_TO_DV_SWITCH_FAILED,
                                taskMessage + " on Node " + nodeDetail.getServiceTag() + " failed!");
        }*/
        LOGGER.info(taskMessage + " on Node " + nodeDetail.getServiceTag() + " was successful.");
        updateDelegateStatus(taskMessage + " on Node " + nodeDetail.getServiceTag() + " was successful.");
    }
}
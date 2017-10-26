/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.task.handler.addnode;

import com.dell.cpsd.paqx.dne.domain.IWorkflowTaskHandler;
import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.model.AddHostToDvSwitchTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.paqx.dne.service.model.InstallEsxiTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.NodeExpansionRequest;
import com.dell.cpsd.paqx.dne.service.model.Status;
import com.dell.cpsd.paqx.dne.service.task.handler.BaseTaskHandler;
import com.dell.cpsd.virtualization.capabilities.api.AddHostToDvSwitchRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.Credentials;
import com.dell.cpsd.virtualization.capabilities.api.DvSwitchConfigList;
import com.dell.cpsd.virtualization.capabilities.api.PortGroupConfigList;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Add Host to Dv Switch Task Handler
 * <p>
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public class AddHostToDvSwitchTaskHandler extends BaseTaskHandler implements IWorkflowTaskHandler
{
    /**
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AddHostToDvSwitchTaskHandler.class);

    /**
     * The <code>NodeService</code> instance
     */
    private final NodeService           nodeService;
    private final DataServiceRepository repository;

    public AddHostToDvSwitchTaskHandler(final NodeService nodeService, final DataServiceRepository repository)
    {
        this.nodeService = nodeService;
        this.repository = repository;
    }

    @Override
    public boolean executeTask(final Job job)
    {
        LOGGER.info("Execute Add Host to DV Switch task");

        final AddHostToDvSwitchTaskResponse response = initializeResponse(job);

        try
        {
            final ComponentEndpointIds componentEndpointIds = repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER");

            if (componentEndpointIds == null)
            {
                throw new IllegalStateException("No VCenter components found.");
            }

            final InstallEsxiTaskResponse installEsxiTaskResponse = (InstallEsxiTaskResponse) job.getTaskResponseMap().get("installEsxi");

            if (installEsxiTaskResponse == null)
            {
                throw new IllegalStateException("No Install ESXi task response found");
            }

            final String hostname = installEsxiTaskResponse.getHostname();

            if (StringUtils.isEmpty(hostname))
            {
                throw new IllegalStateException("Hostname is null or empty");
            }

            final NodeExpansionRequest inputParams = job.getInputParams();

            if (inputParams == null)
            {
                throw new IllegalStateException("Input Params are null");
            }

            //VMotion Config
            final String vMotionManagementIpAddress = inputParams.getvMotionManagementIpAddress();
            if (StringUtils.isEmpty(vMotionManagementIpAddress))
            {
                throw new IllegalStateException("VMotion Management IP Address is null or empty");
            }

            final String vMotionManagementSubnetMask = inputParams.getvMotionManagementSubnetMask();
            if (StringUtils.isEmpty(vMotionManagementSubnetMask))
            {
                throw new IllegalStateException("VMotion Management Subnet Mask is null or empty");
            }

            //SIO DATA1 Port Config
            final String scaleIoData1SvmIpAddress = inputParams.getScaleIoData1SvmIpAddress();
            if (StringUtils.isEmpty(scaleIoData1SvmIpAddress))
            {
                throw new IllegalStateException("ScaleIO Data1 IP Address is null or empty");
            }

            final String scaleIoSvmData1SubnetMask = inputParams.getScaleIoSvmData1SubnetMask();
            if (StringUtils.isEmpty(scaleIoSvmData1SubnetMask))
            {
                throw new IllegalStateException("ScaleIO Data1 Subnet Mask is null or empty");
            }

            //SIO DATA2 Port Config
            final String scaleIoData2SvmIpAddress = inputParams.getScaleIoData2SvmIpAddress();
            if (StringUtils.isEmpty(scaleIoData2SvmIpAddress))
            {
                throw new IllegalStateException("ScaleIO Data2 IP Address is null or empty");
            }

            final String scaleIoSvmData2SubnetMask = inputParams.getScaleIoSvmData2SubnetMask();
            if (StringUtils.isEmpty(scaleIoSvmData2SubnetMask))
            {
                throw new IllegalStateException("ScaleIO Data2 Subnet Mask is null or empty");
            }

            final Map<String, String> dvSwitchNames = repository.getDvSwitchNames();

            if (dvSwitchNames == null)
            {
                throw new IllegalStateException("DV Switch Names are null");
            }

            String DVSWITCH0_NAME = "dvswitch0";
            if (StringUtils.isEmpty(dvSwitchNames.get(DVSWITCH0_NAME)))
            {
                throw new IllegalStateException("DVSwitch0 name is null or empty");
            }

            String DVSWITCH1_NAME = "dvswitch1";
            if (StringUtils.isEmpty(dvSwitchNames.get(DVSWITCH1_NAME)))
            {
                throw new IllegalStateException("DVSwitch1 name is null or empty");
            }

            String DVSWITCH2_NAME = "dvswitch2";
            if (StringUtils.isEmpty(dvSwitchNames.get(DVSWITCH2_NAME)))
            {
                throw new IllegalStateException("DVSwitch2 name is null or empty");
            }

            final Map<String, String> dvPortGroupNames = repository.getDvPortGroupNames(dvSwitchNames);

            if (dvPortGroupNames == null)
            {
                throw new IllegalStateException("DV Port Group Names are null");
            }

            String DVPORT_GROUP_ESXI_MGMT = "esx-mgmt";
            if (StringUtils.isEmpty(dvPortGroupNames.get(DVPORT_GROUP_ESXI_MGMT)))
            {
                throw new IllegalStateException("DV Port Group name for ESXI-MGMT is null");
            }

            String DVPORT_GROUP_VMOTION = "vmotion";
            if (StringUtils.isEmpty(dvPortGroupNames.get(DVPORT_GROUP_VMOTION)))
            {
                throw new IllegalStateException("DV Port Group name for VMOTION is null");
            }

            String DVPORT_GROUP_SIO_DATA1 = "sio-data1";
            if (StringUtils.isEmpty(dvPortGroupNames.get(DVPORT_GROUP_SIO_DATA1)))
            {
                throw new IllegalStateException("DV Port Group name for ScaleIO Data1 is null");
            }

            String DVPORT_GROUP_SIO_DATA2 = "sio-data2";
            if (StringUtils.isEmpty(dvPortGroupNames.get(DVPORT_GROUP_SIO_DATA2)))
            {
                throw new IllegalStateException("DV Port Group name for ScaleIO Data2 is null");
            }

            final AddHostToDvSwitchRequestMessage requestMessage = new AddHostToDvSwitchRequestMessage();

            requestMessage.setCredentials(new Credentials(componentEndpointIds.getEndpointUrl(), null, null));
            requestMessage.setComponentEndpointIds(
                    new com.dell.cpsd.virtualization.capabilities.api.ComponentEndpointIds(componentEndpointIds.getComponentUuid(),
                            componentEndpointIds.getEndpointUuid(), componentEndpointIds.getCredentialUuid()));
            requestMessage.setHostname(hostname);
            final List<DvSwitchConfigList> dvSwitchConfigList = new ArrayList<>();

            //DV Switch 0 - Management and VMotion
            final DvSwitchConfigList dvSwitchConfig0 = new DvSwitchConfigList();
            final List<PortGroupConfigList> portGroupConfigListDvSwitch0 = new ArrayList<>();
            portGroupConfigListDvSwitch0.add(new PortGroupConfigList(dvPortGroupNames.get(DVPORT_GROUP_ESXI_MGMT), null, null, null));
            portGroupConfigListDvSwitch0.add(new PortGroupConfigList(dvPortGroupNames.get(DVPORT_GROUP_VMOTION), vMotionManagementIpAddress, null,
                    vMotionManagementSubnetMask));
            dvSwitchConfig0.setPortGroupConfigList(portGroupConfigListDvSwitch0);
            dvSwitchConfig0.setSwitchName(dvSwitchNames.get(DVSWITCH0_NAME));

            dvSwitchConfigList.add(dvSwitchConfig0);

            //DV Switch 1 - SIO Data1
            final DvSwitchConfigList dvSwitchConfig1 = new DvSwitchConfigList();
            final List<PortGroupConfigList> portGroupConfigListDvSwitch1 = new ArrayList<>();
            portGroupConfigListDvSwitch1.add(new PortGroupConfigList(dvPortGroupNames.get(DVPORT_GROUP_SIO_DATA1), scaleIoData1SvmIpAddress, null,
                    scaleIoSvmData1SubnetMask));

            dvSwitchConfig1.setPortGroupConfigList(portGroupConfigListDvSwitch1);
            dvSwitchConfig1.setSwitchName(dvSwitchNames.get(DVSWITCH1_NAME));

            dvSwitchConfigList.add(dvSwitchConfig1);

            //DV Switch 2 - SIO Data2
            final DvSwitchConfigList dvSwitchConfig2 = new DvSwitchConfigList();
            final List<PortGroupConfigList> portGroupConfigListDvSwitch2 = new ArrayList<>();
            portGroupConfigListDvSwitch2.add(new PortGroupConfigList(dvPortGroupNames.get(DVPORT_GROUP_SIO_DATA2), scaleIoData2SvmIpAddress, null,
                    scaleIoSvmData2SubnetMask));

            dvSwitchConfig2.setPortGroupConfigList(portGroupConfigListDvSwitch2);
            dvSwitchConfig2.setSwitchName(dvSwitchNames.get(DVSWITCH2_NAME));

            dvSwitchConfigList.add(dvSwitchConfig2);

            requestMessage.setDvSwitchConfigList(dvSwitchConfigList);

            final boolean succeeded = this.nodeService.requestAddHostToDvSwitch(requestMessage);

            if (!succeeded)
            {
                throw new IllegalStateException("Request add host to DV switch failed");
            }

            response.setWorkFlowTaskStatus(Status.SUCCEEDED);
            return true;
        }
        catch (Exception e)
        {
            LOGGER.error("Error adding host to DV switch", e);
            response.addError(e.toString());
        }

        response.setWorkFlowTaskStatus(Status.FAILED);
        return false;
    }

    @Override
    public AddHostToDvSwitchTaskResponse initializeResponse(Job job)
    {
        final AddHostToDvSwitchTaskResponse response = new AddHostToDvSwitchTaskResponse();
        setupResponse(job, response);
        return response;
    }
}
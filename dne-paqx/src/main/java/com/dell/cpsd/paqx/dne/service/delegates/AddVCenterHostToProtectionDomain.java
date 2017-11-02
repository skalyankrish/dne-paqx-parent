/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import com.dell.cpsd.storage.capabilities.api.DeviceInfo;
import com.dell.cpsd.storage.capabilities.api.HostToProtectionDomain;
import com.dell.cpsd.storage.capabilities.api.SdsIp;
import com.dell.cpsd.storage.capabilities.api.SdsIpDetails;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;

@Component
@Scope("prototype")
@Qualifier("addVCenterHostToProtectionDomain")
public class AddVCenterHostToProtectionDomain extends BaseWorkflowDelegate
{
    /**
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AddVCenterHostToProtectionDomain.class);

    /**
     * The <code>NodeService</code> instance
     */
    private NodeService nodeService;

    private final DataServiceRepository repository;

    @Autowired
    public AddVCenterHostToProtectionDomain(final NodeService nodeService, final DataServiceRepository repository)
    {
        this.nodeService = nodeService;
        this.repository = repository;
    }

    /**
     * set Host to protection domain
     *
     * @param protectionDomain
     * @param name
     * @param sdsIpList
     * @param deviceInfoList
     * @return
     */
    private HostToProtectionDomain setHostToProtectionDomain(String protectionDomain, String name, List<SdsIp> sdsIpList,
            final List<DeviceInfo> deviceInfoList) {
        /**
         * Creating the request message
         */
        HostToProtectionDomain hostToProtectionDomain = new HostToProtectionDomain();
        hostToProtectionDomain.setProtectionDomainId(protectionDomain);
        hostToProtectionDomain.setName(name);
        hostToProtectionDomain.setSdsIpList(sdsIpList);
        hostToProtectionDomain.setDeviceInfoList(deviceInfoList);
        return hostToProtectionDomain;
    }

    /**
     * get Sds names
     *
     * @param job
     * @return
     */
    private String getSdsName(Job job) {
        String name = null;
        if (job.getInputParams().getEsxiManagementHostname() != null){
            name = (job.getInputParams().getEsxiManagementHostname() + "-ESX");
        }
        else if (job.getInputParams().getEsxiManagementIpAddress() != null){
            name = (job.getInputParams().getEsxiManagementIpAddress() + "-ESX");
        }
        return name;
    }

    /**
     * Create Sds Ip list from input
     * @param job
     * @return
     */
    private List<SdsIp> createSdsIps(Job job) {
        String ScaleIoData1IP = job.getInputParams().getScaleIoData1SvmIpAddress();
        String ScaleIoData2IP = job.getInputParams().getScaleIoData2SvmIpAddress();

        SdsIp sdsIp1 = new SdsIp();
        SdsIpDetails sdsIpDetails1 = new SdsIpDetails();
        sdsIpDetails1.setIp(ScaleIoData1IP);
        sdsIpDetails1.setRole(SdsIpDetails.Role.ALL);
        sdsIp1.setSdsIpDetails(sdsIpDetails1);

        SdsIp sdsIp2 = new SdsIp();
        SdsIpDetails sdsIpDetails2 = new SdsIpDetails();
        sdsIpDetails2.setIp(ScaleIoData2IP);
        sdsIpDetails2.setRole(SdsIpDetails.Role.ALL);
        sdsIp2.setSdsIpDetails(sdsIpDetails2);

        List<SdsIp> sdsIpList = new ArrayList<>();
        sdsIpList.add(0,sdsIp1);
        sdsIpList.add(1,sdsIp2);
        return sdsIpList;
    }

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Execute AddVCenterHostToProtectionDomain task");
        final String taskMessage = "Add Host To Protection Domain";
        final NodeDetail nodeDetail = (NodeDetail) delegateExecution.getVariable(NODE_DETAIL);

        /*try{
            AddHostToProtectionDomainRequestMessage requestMessage = new AddHostToProtectionDomainRequestMessage();

            *//**
             * Getting the protection domain id from FindProtectionDomainTaskHandler
             *//*
            final NodeExpansionRequest inputParams = job.getInputParams();
            final String protectionDomain = inputParams.getProtectionDomain();

            if (protectionDomain == null)
            {
                throw new IllegalStateException("No Protection domain provided");
            }

            *//**
             * Getting the SdsName
             *//*
            String name = getSdsName(job);

            *//**
             * Getting the sdsList
             *//*
            List<SdsIp> sdsIpList = createSdsIps(job);

            List<DeviceInfo> deviceInfoList = createDeviceAssignmentList(job);

            final com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds componentEndpointIds = repository.getComponentEndpointIds("SCALEIO-CLUSTER");

            *//**
             * Adding component endpoint ids
             *//*
            com.dell.cpsd.storage.capabilities.api.ComponentEndpointIds componentEndpoints = new com.dell.cpsd.storage.capabilities.api.ComponentEndpointIds();
            componentEndpoints.setEndpointUuid(componentEndpointIds.getEndpointUuid());
            componentEndpoints.setComponentUuid(componentEndpointIds.getComponentUuid());
            componentEndpoints.setCredentialUuid(componentEndpointIds.getCredentialUuid());

            HostToProtectionDomain hostToProtectionDomain = setHostToProtectionDomain(protectionDomain, name, sdsIpList, deviceInfoList);

            *//**
             * Adding the endpoint url
             *//*
            String endpointUrl = ("https://" + componentEndpointIds.getEndpointUrl() +":443");
            requestMessage.setHostToProtectionDomain(hostToProtectionDomain);
            requestMessage.setComponentEndpointIds(componentEndpoints);
            requestMessage.setEndpointUrl(endpointUrl);

            *//**
             * Creating the response message
             *//*
            final boolean success = this.nodeService.requestAddHostToProtectionDomain(requestMessage);
            if (!success)
            {
                throw new IllegalStateException("Request add host to protection domain failed");
            }
            response.setWorkFlowTaskStatus(Status.SUCCEEDED);

            return true;
        }
        catch(Exception e)
        {
            LOGGER.error("Exception occurred", e);
            response.addError(e.toString());
        }
        response.setWorkFlowTaskStatus(Status.FAILED);
        return false;
*/
        LOGGER.info(taskMessage + " on Node " + nodeDetail.getServiceTag() + " was successful.");
        updateDelegateStatus(taskMessage + " on Node " + nodeDetail.getServiceTag() + " was successful.");
    }
}

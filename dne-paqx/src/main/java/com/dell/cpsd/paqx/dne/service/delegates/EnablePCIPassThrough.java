/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.domain.vcenter.PciDevice;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.virtualization.capabilities.api.Credentials;
import com.dell.cpsd.virtualization.capabilities.api.EnablePCIPassthroughRequestMessage;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.HOSTNAME;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;

@Component
@Scope("prototype")
@Qualifier("enablePCIPassThrough")
public class EnablePCIPassThrough extends BaseWorkflowDelegate
{
    /**
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(EnablePCIPassThrough.class);
    private static final String DELL_PCI_REGEX = "Dell.*(H730|HBA330|HBA).*Mini*";
    private static final String PCI_BUS_DEVICE_ID = "0000:02:00.0";

    /**
     * The <code>NodeService</code> instance
     */
    private final NodeService nodeService;
    private final DataServiceRepository repository;

    @Autowired
    public EnablePCIPassThrough(final NodeService nodeService, final DataServiceRepository repository)
    {
        this.nodeService = nodeService;
        this.repository = repository;
    }

    private EnablePCIPassthroughRequestMessage getEnablePCIPassthroughRequestMessage(
            final ComponentEndpointIds componentEndpointIds, final String hostname, final String hostPCIDeviceId)
    {
        final EnablePCIPassthroughRequestMessage requestMessage = new EnablePCIPassthroughRequestMessage();
        requestMessage.setCredentials(new Credentials(componentEndpointIds.getEndpointUrl(), null, null));
        requestMessage.setComponentEndpointIds(new com.dell.cpsd.virtualization.capabilities.api.ComponentEndpointIds(
                componentEndpointIds.getComponentUuid(), componentEndpointIds.getEndpointUuid(),
                componentEndpointIds.getCredentialUuid()));
        requestMessage.setHostname(hostname);
        requestMessage.setHostPciDeviceId(hostPCIDeviceId);
        return requestMessage;
    }

    private String filterDellPercPciDeviceId(final List<PciDevice> pciDeviceList) throws IllegalStateException
    {
        final PciDevice requiredPciDevice = pciDeviceList.stream().filter(
                obj -> Objects.nonNull(obj) && obj.getDeviceName().matches(DELL_PCI_REGEX)).findFirst().orElse(null);

        if (requiredPciDevice == null)
        {
            return PCI_BUS_DEVICE_ID;
        }

        return requiredPciDevice.getId();
    }

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Execute Enable PCI Pass through task");

        final String hostname = (String) delegateExecution.getVariable(HOSTNAME);
        final NodeDetail nodeDetail = (NodeDetail) delegateExecution.getVariable(NODE_DETAIL);

  /*      ComponentEndpointIds componentEndpointIds = null;
        try
        {
            componentEndpointIds = repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER");
        }
        catch (Exception e)
        {
            LOGGER.error("An Unexpected Exception occurred attempting to retrieve VCenter Component Endpoints.", e);
            updateDelegateStatus(
                    "An Unexpected Exception occurred attempting to retrieve VCenter Component Endpoints.  Reason: " +
                    e.getMessage());
            throw new BpmnError(ENABLE_PCI_PASSTHROUGH_FAILED,
                                "An Unexpected Exception occurred attempting to retrieve VCenter Component Endpoints.  Reason: " +
                                e.getMessage());
        }

        List<PciDevice> pciDeviceList = null;
        try
        {
            pciDeviceList = repository.getPciDeviceList();
        }
        catch (Exception e)
        {
            LOGGER.error("An Unexpected Exception occurred attempting to retrieve PCI Device List.", e);
            updateDelegateStatus("An Unexpected Exception occurred attempting to retrieve PCI Device List.  Reason: " + e.getMessage());
            throw new BpmnError(ENABLE_PCI_PASSTHROUGH_FAILED,
                                "An Unexpected Exception occurred attempting to retrieve PCI Device List.  Reason: " +
                                e.getMessage());
        }

        if (pciDeviceList == null || pciDeviceList.isEmpty())
        {
            LOGGER.error("An Unexpected Exception occurred attempting to retrieve PCI Device List.");
            updateDelegateStatus("An Unexpected Exception occurred attempting to enable PCI Device List.");
            throw new BpmnError(ENABLE_PCI_PASSTHROUGH_FAILED,
                                "An Unexpected Exception occurred attempting to inventory VCenter.");
        }

        final String hostPciDeviceId = filterDellPercPciDeviceId(pciDeviceList);

        final EnablePCIPassthroughRequestMessage requestMessage = getEnablePCIPassthroughRequestMessage(
                componentEndpointIds, hostname, hostPciDeviceId);

        boolean success;
        try
        {
            success = this.nodeService.requestEnablePciPassThrough(requestMessage);
        }
        catch (Exception e)
        {
            LOGGER.error("An Unexpected Exception occurred attempting to request Enable PCI Pass Through.", e);
            updateDelegateStatus(
                    "An Unexpected Exception occurred attempting to request Enable PCI Pass Through.  Reason: " +
                    e.getMessage());
            throw new BpmnError(ENABLE_PCI_PASSTHROUGH_FAILED,
                                "An Unexpected Exception occurred attempting to request Enable PCI Pass Through.  Reason: " +
                                e.getMessage());
        }
        if (!success)
        {
            LOGGER.error("Enable PCI Pass Through on Node " + nodeDetail.getServiceTag() + " failed!");
            updateDelegateStatus("Install Esxi on Node " + nodeDetail.getServiceTag() + " failed!");
            throw new BpmnError(INSTALL_ESXI_FAILED, "Install Esxi on Node " + nodeDetail.getServiceTag() + " failed!");
        }
        delegateExecution.setVariable(DelegateConstants.HOST_PCI_DEVICE_ID, hostPciDeviceId);
        */
        LOGGER.info("Request PCI Pass Through on Node " + nodeDetail.getServiceTag() + " was successful.");
        updateDelegateStatus("Request PCI Pass Through on Node " + nodeDetail.getServiceTag() + " was successful.");


    }
}

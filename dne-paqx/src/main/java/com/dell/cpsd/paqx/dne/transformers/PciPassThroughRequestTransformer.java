/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 **/

package com.dell.cpsd.paqx.dne.transformers;

import com.dell.cpsd.paqx.dne.domain.vcenter.PciDevice;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.virtualization.capabilities.api.Credentials;
import com.dell.cpsd.virtualization.capabilities.api.EnablePCIPassthroughRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.UpdatePCIPassthruSVMRequestMessage;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.HOSTNAME;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.HOST_PCI_DEVICE_ID;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.VIRTUAL_MACHINE_NAME;

/**
 * PCI Request Transformer and builds the Enable PCI Passthrough
 * and update PCI Passthrough request message.
 * It takes the delegation execution instance.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@Component
public class PciPassThroughRequestTransformer
{
    private static final String DELL_PCI_REGEX        = "Dell.*(H730|HBA330|HBA).*Mini*";
    private static final String PCI_BUS_DEVICE_ID     = "0000:02:00.0";
    private static final String VCENTER_CUSTOMER_TYPE = "VCENTER-CUSTOMER";

    private final DataServiceRepository   repository;
    private final ComponentIdsTransformer componentIdsTransformer;

    public PciPassThroughRequestTransformer(final DataServiceRepository repository, final ComponentIdsTransformer componentIdsTransformer)
    {
        this.repository = repository;
        this.componentIdsTransformer = componentIdsTransformer;
    }

    public EnablePCIPassthroughRequestMessage buildEnablePciPassThroughRequest(final DelegateExecution delegateExecution)
    {
        final String hostname = (String) delegateExecution.getVariable(HOSTNAME);
        final ComponentEndpointIds componentEndpointIds = componentIdsTransformer
                .getVCenterComponentEndpointIdsByEndpointType(VCENTER_CUSTOMER_TYPE);
        final List<PciDevice> pciDeviceList = getPciDeviceList();
        final String hostPciDeviceId = filterDellPercPciDeviceId(pciDeviceList);

        return getEnablePCIPassthroughRequestMessage(componentEndpointIds, hostname, hostPciDeviceId);
    }

    public UpdatePCIPassthruSVMRequestMessage buildUpdatePciPassThroughRequest(final DelegateExecution delegateExecution)
    {
        final String hostname = (String) delegateExecution.getVariable(HOSTNAME);
        final ComponentEndpointIds componentEndpointIds = componentIdsTransformer
                .getVCenterComponentEndpointIdsByEndpointType(VCENTER_CUSTOMER_TYPE);
        final String hostPciDeviceId = (String) delegateExecution.getVariable(HOST_PCI_DEVICE_ID);
        final String virtualMachineName = (String) delegateExecution.getVariable(VIRTUAL_MACHINE_NAME);

        return getUpdatePciPassThroughRequestMessage(hostname, componentEndpointIds, hostPciDeviceId, virtualMachineName);
    }

    private UpdatePCIPassthruSVMRequestMessage getUpdatePciPassThroughRequestMessage(final String hostname,
            final ComponentEndpointIds componentEndpointIds, final String hostPciDeviceId, final String virtualMachineName)
    {
        final UpdatePCIPassthruSVMRequestMessage requestMessage = new UpdatePCIPassthruSVMRequestMessage();
        requestMessage.setHostname(hostname);
        requestMessage.setHostPciDeviceId(hostPciDeviceId);
        requestMessage.setVmName(virtualMachineName);
        requestMessage.setCredentials(new Credentials(componentEndpointIds.getEndpointUrl(), null, null));
        requestMessage.setComponentEndpointIds(
                new com.dell.cpsd.virtualization.capabilities.api.ComponentEndpointIds(componentEndpointIds.getComponentUuid(),
                        componentEndpointIds.getEndpointUuid(), componentEndpointIds.getCredentialUuid()));
        return requestMessage;
    }

    private List<PciDevice> getPciDeviceList()
    {
        final List<PciDevice> pciDeviceList = repository.getPciDeviceList();

        if (pciDeviceList == null || pciDeviceList.isEmpty())
        {
            return null;
        }
        return pciDeviceList;
    }

    private EnablePCIPassthroughRequestMessage getEnablePCIPassthroughRequestMessage(final ComponentEndpointIds componentEndpointIds,
            final String hostname, final String hostPciDeviceId)
    {
        final EnablePCIPassthroughRequestMessage requestMessage = new EnablePCIPassthroughRequestMessage();
        requestMessage.setCredentials(new Credentials(componentEndpointIds.getEndpointUrl(), null, null));
        requestMessage.setComponentEndpointIds(
                new com.dell.cpsd.virtualization.capabilities.api.ComponentEndpointIds(componentEndpointIds.getComponentUuid(),
                        componentEndpointIds.getEndpointUuid(), componentEndpointIds.getCredentialUuid()));
        requestMessage.setHostname(hostname);
        requestMessage.setHostPciDeviceId(hostPciDeviceId);
        return requestMessage;
    }

    private String filterDellPercPciDeviceId(final List<PciDevice> pciDeviceList) throws IllegalStateException
    {
        if (!CollectionUtils.isEmpty(pciDeviceList))
        {
            final PciDevice requiredPciDevice = pciDeviceList.stream()
                    .filter(obj -> Objects.nonNull(obj) && obj.getDeviceName().matches(DELL_PCI_REGEX)).findFirst().orElse(null);

            if (requiredPciDevice == null)
            {
                return PCI_BUS_DEVICE_ID;
            }

            return requiredPciDevice.getId();
        }
        return PCI_BUS_DEVICE_ID;
    }
}

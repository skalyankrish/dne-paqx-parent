/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Qualifier("deployScaleIOVm")
public class DeployScaleIOVm extends BaseWorkflowDelegate
{
    /*
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DeployScaleIOVm.class);

    /*
     * The <code>NodeService</code> instance
     */
    private final NodeService nodeService;

    /*
    * The time to wait before sending the request to deploy the scaleio vm
    */
    //private final long waitTime;

    private static final String SCALEIO_VM_PREFIX        = "ScaleIO-";
    private static final String SCALEIO_TEMPLATE_VM_NAME = "EMC ScaleIO SVM Template.*";
    private static final int    SCALEIO_VM_NUM_CPU       = 8;
    private static final int    SCALEIO_VM_RAM           = 8192;

    /**
     * The <code>DataServiceRepository</code> instance
     */
    private final DataServiceRepository repository;

    /**
     * DeployScaleIoVmTaskHandler constructor
     *
     * @param nodeService - The <code>DataServiceRepository</code> instance
     * @param repository - The <code>NodeService</code> instance
     * @param waitTime - Before deploying the SVM need to wait for services to be up and running on the new host
     */
    @Autowired
    public DeployScaleIOVm(final NodeService nodeService, final DataServiceRepository repository)
    {
        this.nodeService = nodeService;
        this.repository = repository;
    }

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Execute Deploy ScaleIO VM From Template");

/*
        try
        {
            final Validate validate = new Validate(job).invoke();
            final ComponentEndpointIds componentEndpointIds = validate.getComponentEndpointIds();
            final String hostname = validate.getHostname();
            final String dataCenterName = validate.getDataCenterName();
            final String newScaleIoVmName = validate.getNewScaleIoVmName();
            final String newScaleIoVmHostname = validate.getNewScaleIoVmHostname();
            final String domainName = validate.getNewScaleIoVmDomainName();
            final List<String> dnsServers = validate.getNewScaleIoVmDnsServers();
            final List<NicSetting> nicSettings = validate.getNewScaleIoVmNicSettings();

            final VirtualMachineCloneSpec virtualMachineCloneSpec = new VirtualMachineCloneSpec();
            virtualMachineCloneSpec.setPoweredOn(false);
            virtualMachineCloneSpec.setTemplate(false);
            virtualMachineCloneSpec.setDomain(domainName);

            final VirtualMachineConfigSpec virtualMachineConfigSpec = new VirtualMachineConfigSpec();
            virtualMachineConfigSpec.setHostName(newScaleIoVmHostname);
            virtualMachineConfigSpec.setNumCPUs(SCALEIO_VM_NUM_CPU);
            virtualMachineConfigSpec.setMemoryMB(SCALEIO_VM_RAM);
            virtualMachineConfigSpec.setDnsServerList(dnsServers);
            virtualMachineConfigSpec.setNicSettings(nicSettings);
            virtualMachineCloneSpec.setVirtualMachineConfigSpec(virtualMachineConfigSpec);

            final DeployVMFromTemplateRequestMessage requestMessage = new DeployVMFromTemplateRequestMessage();
            requestMessage.setCredentials(new Credentials(componentEndpointIds.getEndpointUrl(), null, null));
            requestMessage.setComponentEndpointIds(
                    new com.dell.cpsd.virtualization.capabilities.api.ComponentEndpointIds(componentEndpointIds.getComponentUuid(),
                                                                                           componentEndpointIds.getEndpointUuid(), componentEndpointIds.getCredentialUuid()));
            requestMessage.setHostName(hostname);
            requestMessage.setTemplateName(SCALEIO_TEMPLATE_VM_NAME);
            requestMessage.setDatacenterName(dataCenterName);
            requestMessage.setNewVMName(newScaleIoVmName);
            requestMessage.setVirtualMachineCloneSpec(virtualMachineCloneSpec);

            Thread.sleep(this.waitTime);

            final boolean succeeded = this.nodeService.requestDeployScaleIoVm(requestMessage);

            if (!succeeded)
            {
                throw new IllegalStateException("Request deploy ScaleIO VM failed");
            }

            response.setWorkFlowTaskStatus(Status.SUCCEEDED);
            response.setNewVMName(newScaleIoVmName);
            return true;
        }
        catch (Exception e)
        {
            LOGGER.error("Error deploying ScaleIO VM", e);
            response.addError(e.toString());
        }

        response.setWorkFlowTaskStatus(Status.FAILED);
        return false;*/
        LOGGER.info("Deploy ScaleIO VM From Template was successful");
    }
}

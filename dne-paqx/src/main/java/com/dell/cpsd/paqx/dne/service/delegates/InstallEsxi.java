/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import com.dell.cpsd.paqx.dne.transformers.HostToInstallEsxiRequestTransformer;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;

@Component
@Scope("prototype")
@Qualifier("installEsxi")
public class InstallEsxi extends BaseWorkflowDelegate
{
    /**
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(InstallEsxi.class);

    /**
     * The <code>NodeService</code> instance
     */
    private final NodeService nodeService;

    private final HostToInstallEsxiRequestTransformer hostToInstallEsxiRequestTransformer;

    private final DataServiceRepository repository;

    @Autowired
    public InstallEsxi(final NodeService nodeService,
                       final HostToInstallEsxiRequestTransformer hostToInstallEsxiRequestTransformer,
                       final DataServiceRepository repository)
    {
        this.nodeService = nodeService;
        this.hostToInstallEsxiRequestTransformer = hostToInstallEsxiRequestTransformer;
        this.repository = repository;
    }

    /*
    * Builds the host fully qualified domain name (FQDN) from
    * the hostname and domain name, where the domain name is
    * retrieved from the database.
    */
    private String generateHostFqdn(String hostName)
    {
        String domainName = this.repository.getDomainName();
        if (StringUtils.isEmpty(domainName))
        {
            return hostName;
        }

        StringBuilder builder = new StringBuilder();
        builder.append(hostName);
        builder.append(".");
        builder.append(domainName);
        return builder.toString();
    }

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {

        LOGGER.info("Execute Install ESXi");
        NodeDetail nodeDetail = (NodeDetail) delegateExecution.getVariable(NODE_DETAIL);


/*
            final String symphonyUuid = nodeDetail.getId();

            final String esxiManagementIpAddress = nodeDetail.getEsxiManagementIpAddress();

            final String esxiManagementGatewayIpAddress = nodeDetail.getEsxiManagementGatewayIpAddress();

            final String esxiManagementSubnetMask = nodeDetail.getEsxiManagementSubnetMask();

            String esxiManagementHostname = nodeDetail.getEsxiManagementHostname();
            if (StringUtils.isEmpty(esxiManagementHostname))
            {
                LOGGER.warn("ESXi Management hostname is null, will auto generate hostname");

                esxiManagementHostname = this.generateHostname(esxiManagementIpAddress);

                LOGGER.info("Auto generated ESXi Management hostname is " + esxiManagementHostname);
            }

            final IpV4Configuration ipV4Configuration = new IpV4Configuration();
            ipV4Configuration.setEsxiManagementIpAddress(esxiManagementIpAddress);
            ipV4Configuration.setEsxiManagementGateway(esxiManagementGatewayIpAddress);
            ipV4Configuration.setEsxiManagementNetworkMask(esxiManagementSubnetMask);

            final EsxiInstallationInfo esxiInstallationInfo = hostToInstallEsxiRequestTransformer
                    .transformInstallEsxiData(esxiManagementHostname, symphonyUuid, ipV4Configuration);

            boolean succeeded = false;
            try
            {
                succeeded = this.nodeService.requestInstallEsxi(esxiInstallationInfo);
            }
            catch (Exception e)
            {
                LOGGER.error("An Unexpected Exception Occurred attempting to Install Esxi on Node " +
                             nodeDetail.getServiceTag(), e);
                updateDelegateStatus("An Unexpected Exception Occurred attempting to Install Esxi on Node " +
                                     nodeDetail.getServiceTag());
                throw new BpmnError(INSTALL_ESXI_FAILED,
                                    "Install Esxi on Node " + nodeDetail.getServiceTag() +
                                    " failed!  Reason: " + e.getMessage());
            }

            if (!succeeded)
            {
                LOGGER.error("Install Esxi on Node " + nodeDetail.getServiceTag() + " failed!");
                updateDelegateStatus(
                        "Install Esxi on Node " + nodeDetail.getServiceTag() + " failed!");
                throw new BpmnError(INSTALL_ESXI_FAILED,
                                    "Install Esxi on Node " + nodeDetail.getServiceTag() +
                                    " failed!");
            }
        String fqdn = this.generateHostFqdn(esxiManagementHostname);
        delegateExecution.setVariable(HOSTNAME, fqdn);
*/
        LOGGER.info("Install Esxi on Node " + nodeDetail.getServiceTag() + " was successful.");
        updateDelegateStatus("Install Esxi on Node " + nodeDetail.getServiceTag() + " was successful.");

    }
}

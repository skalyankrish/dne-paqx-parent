/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 **/

package com.dell.cpsd.paqx.dne.transformers;

import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOData;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOMdmCluster;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.delegates.model.ESXiCredentialDetails;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.virtualization.capabilities.api.Credentials;
import com.dell.cpsd.virtualization.capabilities.api.SoftwareVIBConfigureRequest;
import com.dell.cpsd.virtualization.capabilities.api.SoftwareVIBConfigureRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.SoftwareVIBRequest;
import com.dell.cpsd.virtualization.capabilities.api.SoftwareVIBRequestMessage;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.ESXI_CREDENTIAL_DETAILS;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.HOSTNAME;
import static java.util.Collections.singletonList;

/**
 * Software VIB Request transformer that builds the request message for
 * Install SDC VIB and configure SDC VIB.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@Component
public class SoftwareVibRequestTransformer
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SoftwareVibRequestTransformer.class);

    private static final String IOCTL_INI_GUID_STRING = "IoctlIniGuidStr";
    private static final String IOCTL_MDM_IP_STRING   = "IoctlMdmIPStr";
    private static final String EQUALS_STRING         = "=";
    private static final String SPACE_STRING          = " ";
    private static final String COMMA_DELIMITER       = ",";
    private static final String SOFTWARE_VIB_MODULE   = "scini";
    private static final String VCENTER_CUSTOMER_TYPE = "VCENTER-CUSTOMER";
    private static final String MDM_TYPE_TIEBREAKER   = "TieBreaker";
    private static final String MDM_TYPE_MASTER       = "master";
    private static final String MDM_TYPE_SLAVE        = "slave";

    private final DataServiceRepository   repository;
    private final String                  sdcVibRemoteUrl;
    private final ComponentIdsTransformer componentIdsTransformer;

    public SoftwareVibRequestTransformer(final DataServiceRepository repository,
            @Value("${rackhd.sdc.vib.install.repo.url}") final String sdcVibRemoteUrl,
            final ComponentIdsTransformer componentIdsTransformer)
    {
        this.repository = repository;
        this.sdcVibRemoteUrl = sdcVibRemoteUrl;
        this.componentIdsTransformer = componentIdsTransformer;
    }

    /**
     * This method builds the Install Software SDC VIB request message.
     *
     * @param delegateExecution delegateExecution
     * @return SoftwareVIBRequestMessage
     */
    public SoftwareVIBRequestMessage buildInstallSoftwareVibRequest(final DelegateExecution delegateExecution)
    {
        final String hostname = (String) delegateExecution.getVariable(HOSTNAME);
        final ComponentEndpointIds componentEndpointIds = componentIdsTransformer
                .getVCenterComponentEndpointIdsByEndpointType(VCENTER_CUSTOMER_TYPE);

        return getInstallSdcSoftwareVibRequestMessage(hostname, componentEndpointIds);
    }

    /**
     * This method builds the configure ScaleIO VIB request message.
     *
     * @param delegateExecution delegateExecution
     * @return SoftwareVIBConfigureRequestMessage
     * @throws Exception Exception
     */
    public SoftwareVIBConfigureRequestMessage buildConfigureSoftwareVibRequest(final DelegateExecution delegateExecution) throws Exception
    {
        final String hostname = (String) delegateExecution.getVariable(HOSTNAME);
        final ComponentEndpointIds componentEndpointIds = componentIdsTransformer
                .getVCenterComponentEndpointIdsByEndpointType(VCENTER_CUSTOMER_TYPE);
        final ESXiCredentialDetails esxiCredentialDetails = (ESXiCredentialDetails) delegateExecution.getVariable(ESXI_CREDENTIAL_DETAILS);
        final String ioctlIniGuidStr = UUID.randomUUID().toString();

        return getConfigureSoftwareVibRequestMessage(hostname, componentEndpointIds, esxiCredentialDetails, ioctlIniGuidStr);
    }

    private SoftwareVIBRequestMessage getInstallSdcSoftwareVibRequestMessage(final String hostname,
            final ComponentEndpointIds componentEndpointIds)
    {
        final SoftwareVIBRequestMessage requestMessage = new SoftwareVIBRequestMessage();
        getSoftwareVibInstallRequest(hostname, requestMessage);
        requestMessage.setCredentials(new Credentials(componentEndpointIds.getEndpointUrl(), null, null));
        requestMessage.setComponentEndpointIds(
                new com.dell.cpsd.virtualization.capabilities.api.ComponentEndpointIds(componentEndpointIds.getComponentUuid(),
                        componentEndpointIds.getEndpointUuid(), componentEndpointIds.getCredentialUuid()));
        return requestMessage;
    }

    private void getSoftwareVibInstallRequest(final String hostname, final SoftwareVIBRequestMessage requestMessage)
    {
        final SoftwareVIBRequest softwareVIBRequest = new SoftwareVIBRequest();
        softwareVIBRequest.setVibOperation(SoftwareVIBRequest.VibOperation.INSTALL);
        softwareVIBRequest.setHostName(hostname);
        softwareVIBRequest.setVibUrls(singletonList(sdcVibRemoteUrl));
        requestMessage.setSoftwareVibInstallRequest(softwareVIBRequest);
    }

    private SoftwareVIBConfigureRequestMessage getConfigureSoftwareVibRequestMessage(final String hostname,
            final ComponentEndpointIds componentEndpointIds, final ESXiCredentialDetails esxiCredentialDetails,
            final String ioctlIniGuidStr) throws Exception
    {
        final SoftwareVIBConfigureRequestMessage requestMessage = new SoftwareVIBConfigureRequestMessage();
        requestMessage.setCredentials(new Credentials(componentEndpointIds.getEndpointUrl(), null, null));
        requestMessage.setComponentEndpointIds(
                new com.dell.cpsd.virtualization.capabilities.api.ComponentEndpointIds(componentEndpointIds.getComponentUuid(),
                        componentEndpointIds.getEndpointUuid(), componentEndpointIds.getCredentialUuid()));

        getConfigureSoftwareVibRequest(hostname, esxiCredentialDetails, ioctlIniGuidStr, requestMessage);
        return requestMessage;
    }

    private void getConfigureSoftwareVibRequest(final String hostname, final ESXiCredentialDetails esxiCredentialDetails,
            final String ioctlIniGuidStr, final SoftwareVIBConfigureRequestMessage requestMessage) throws Exception
    {
        final SoftwareVIBConfigureRequest softwareVIBConfigureRequest = new SoftwareVIBConfigureRequest();
        softwareVIBConfigureRequest.setHostName(hostname);
        softwareVIBConfigureRequest.setModuleName(SOFTWARE_VIB_MODULE);
        softwareVIBConfigureRequest.setModuleOptions(buildModuleOptions(ioctlIniGuidStr));
        softwareVIBConfigureRequest.setComponentEndpointIds(
                new com.dell.cpsd.virtualization.capabilities.api.ComponentEndpointIds(esxiCredentialDetails.getComponentUuid(),
                        esxiCredentialDetails.getEndpointUuid(), esxiCredentialDetails.getCredentialUuid()));
        requestMessage.setSoftwareVIBConfigureRequest(softwareVIBConfigureRequest);
    }

    private String buildModuleOptions(final String ioctlIniGuidStr) throws IllegalStateException
    {
        try
        {
            final ScaleIOData scaleIOData = getScaleIOData();

            final Set<String> mdmIpList = new HashSet<>();

            getMdmScaleIoDataIps(scaleIOData, mdmIpList);

            final StringBuilder builder = new StringBuilder();
            builder.append(IOCTL_INI_GUID_STRING);
            builder.append(EQUALS_STRING);

            LOGGER.info("IoctlIniGuidStr is [{}]", ioctlIniGuidStr);

            builder.append(ioctlIniGuidStr);
            builder.append(SPACE_STRING);
            builder.append(IOCTL_MDM_IP_STRING);
            builder.append(EQUALS_STRING);
            builder.append(String.join(COMMA_DELIMITER, mdmIpList));

            return builder.toString();

        }
        catch (Exception exception)
        {
            throw new IllegalStateException("Exception occurred", exception);
        }
    }

    private void getMdmScaleIoDataIps(final ScaleIOData scaleIOData, final Set<String> mdmIpList)
    {
        final ScaleIOMdmCluster scaleIOMdmCluster = scaleIOData.getMdmCluster();

        if (scaleIOMdmCluster == null)
        {
            final String error = "ScaleIO MDM Cluster is null";
            LOGGER.error(error);
            throw new IllegalStateException(error);
        }

        getMasterMdmDataIps(mdmIpList, scaleIOMdmCluster);
        getSlaveMdmsDataIps(mdmIpList, scaleIOMdmCluster);
    }

    private void getSlaveMdmsDataIps(final Set<String> mdmIpList, final ScaleIOMdmCluster scaleIOMdmCluster)
    {
        // Scan the slave element info for ips
        scaleIOMdmCluster.getSlaveElementInfo().stream().filter(Objects::nonNull)
                .forEach(scaleIOSDSElementInfo -> scaleIOSDSElementInfo.getIps().forEach(scaleIOIP -> {
                    if (!scaleIOIP.getSdsElementInfo().getRole().equalsIgnoreCase(MDM_TYPE_TIEBREAKER) && MDM_TYPE_SLAVE
                            .equalsIgnoreCase(scaleIOIP.getType()))
                    {
                        mdmIpList.add(scaleIOIP.getIp());
                    }
                }));
    }

    private void getMasterMdmDataIps(final Set<String> mdmIpList, final ScaleIOMdmCluster scaleIOMdmCluster)
    {
        // Scan the master element info the ips
        scaleIOMdmCluster.getMasterElementInfo().stream().filter(Objects::nonNull)
                .forEach(scaleIOSDSElementInfo -> scaleIOSDSElementInfo.getIps().forEach(scaleIOIP -> {
                    if (!scaleIOIP.getSdsElementInfo().getRole().equalsIgnoreCase(MDM_TYPE_TIEBREAKER) && MDM_TYPE_MASTER
                            .equalsIgnoreCase(scaleIOIP.getType()))
                    {
                        mdmIpList.add(scaleIOIP.getIp());
                    }
                }));
    }

    private ScaleIOData getScaleIOData() throws Exception
    {
        final ScaleIOData scaleIOData = repository.getScaleIoData();

        if (scaleIOData == null)
        {
            throw new IllegalStateException("ScaleIO Data is null");
        }

        return scaleIOData;
    }
}

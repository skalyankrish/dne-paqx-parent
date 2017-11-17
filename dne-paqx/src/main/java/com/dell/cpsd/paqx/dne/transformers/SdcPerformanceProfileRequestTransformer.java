/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 **/

package com.dell.cpsd.paqx.dne.transformers;

import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.storage.capabilities.api.PerformanceProfileRequest;
import com.dell.cpsd.storage.capabilities.api.SioSdcUpdatePerformanceProfileRequestMessage;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;

/**
 * Update SDC Performance Profile Transformer, builds the
 * {@link com.dell.cpsd.storage.capabilities.api.SioSdcUpdatePerformanceProfileRequestMessage}
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
public class SdcPerformanceProfileRequestTransformer
{
    private static final String COMPONENT_TYPE   = "SCALEIO-CLUSTER";
    private static final String HTTPS_URL_SCHEME = "https://";

    private final DataServiceRepository repository;

    public SdcPerformanceProfileRequestTransformer(final DataServiceRepository repository)
    {
        this.repository = repository;
    }

    public SioSdcUpdatePerformanceProfileRequestMessage buildSdcPerformanceProfileRequest(final DelegateExecution delegateExecution)
    {
        final ComponentEndpointIds componentEndpointIds = getComponentEndpointIds();
        final NodeDetail nodeDetail = (NodeDetail) delegateExecution.getVariable(NODE_DETAIL);
        final String sdcGUID = (String) delegateExecution.getVariable(DelegateConstants.IOCTL_INI_GUI_STR);
        final String scaleIoSdcIpAddress = nodeDetail.getEsxiManagementIpAddress();

        return getRequestMessage(componentEndpointIds, sdcGUID,
                scaleIoSdcIpAddress);
    }

    private SioSdcUpdatePerformanceProfileRequestMessage getRequestMessage(final ComponentEndpointIds componentEndpointIds,
            final String sdcGUID, final String scaleIoSdcIpAddress)
    {
        final PerformanceProfileRequest performanceProfileRequest = new PerformanceProfileRequest();
        performanceProfileRequest.setSdcIp(scaleIoSdcIpAddress);
        performanceProfileRequest.setSdcGuid(sdcGUID);
        performanceProfileRequest.setPerfProfile(PerformanceProfileRequest.PerfProfile.HIGH_PERFORMANCE);

        final SioSdcUpdatePerformanceProfileRequestMessage requestMessage = new SioSdcUpdatePerformanceProfileRequestMessage();
        requestMessage.setPerformanceProfileRequest(performanceProfileRequest);
        requestMessage.setEndpointUrl(HTTPS_URL_SCHEME.concat(componentEndpointIds.getEndpointUrl()));
        requestMessage.setComponentEndpointIds(
                new com.dell.cpsd.storage.capabilities.api.ComponentEndpointIds(componentEndpointIds.getComponentUuid(),
                        componentEndpointIds.getEndpointUuid(), componentEndpointIds.getCredentialUuid()));
        return requestMessage;
    }

    private ComponentEndpointIds getComponentEndpointIds()
    {
        final ComponentEndpointIds componentEndpointIds = repository.getComponentEndpointIds(COMPONENT_TYPE);

        if (componentEndpointIds == null)
        {
            throw new IllegalStateException("No component ids found.");
        }
        return componentEndpointIds;
    }
}

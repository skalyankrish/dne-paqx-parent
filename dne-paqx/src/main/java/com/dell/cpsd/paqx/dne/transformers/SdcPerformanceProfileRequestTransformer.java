/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 **/

package com.dell.cpsd.paqx.dne.transformers;

import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.storage.capabilities.api.PerformanceProfileRequest;
import com.dell.cpsd.storage.capabilities.api.SioSdcUpdatePerformanceProfileRequestMessage;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.IOCTL_INI_GUI_STR;
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

    private final ComponentIdsTransformer componentIdsTransformer;

    public SdcPerformanceProfileRequestTransformer(final ComponentIdsTransformer componentIdsTransformer)
    {
        this.componentIdsTransformer = componentIdsTransformer;
    }

    public SioSdcUpdatePerformanceProfileRequestMessage buildSdcPerformanceProfileRequest(final DelegateExecution delegateExecution)
    {
        final ComponentEndpointIds componentEndpointIds = componentIdsTransformer.getComponentEndpointIdsByComponentType(COMPONENT_TYPE);
        final NodeDetail nodeDetail = (NodeDetail) delegateExecution.getVariable(NODE_DETAIL);
        final String sdcGUID = (String) delegateExecution.getVariable(IOCTL_INI_GUI_STR);
        final String scaleIoSdcIpAddress = nodeDetail.getEsxiManagementIpAddress();

        return getRequestMessage(componentEndpointIds, sdcGUID, scaleIoSdcIpAddress);
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
}

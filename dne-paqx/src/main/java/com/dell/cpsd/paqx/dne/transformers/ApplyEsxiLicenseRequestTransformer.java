/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 **/

package com.dell.cpsd.paqx.dne.transformers;

import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.virtualization.capabilities.api.AddEsxiHostVSphereLicenseRequest;
import com.dell.cpsd.virtualization.capabilities.api.Credentials;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.HOSTNAME;

/**
 * Apply ESXi License request message transformer
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@Component
public class ApplyEsxiLicenseRequestTransformer
{
    private static final String VCENTER_CUSTOMER_TYPE = "VCENTER-CUSTOMER";

    private final ComponentIdsTransformer componentIdsTransformer;

    public ApplyEsxiLicenseRequestTransformer(final ComponentIdsTransformer componentIdsTransformer)
    {
        this.componentIdsTransformer = componentIdsTransformer;
    }

    public AddEsxiHostVSphereLicenseRequest buildApplyEsxiLicenseRequest(final DelegateExecution delegateExecution)
    {
        final String hostname = (String) delegateExecution.getVariable(HOSTNAME);
        final ComponentEndpointIds componentEndpointIds = componentIdsTransformer
                .getVCenterComponentEndpointIdsByEndpointType(VCENTER_CUSTOMER_TYPE);

        return getLicenseRequest(componentEndpointIds, hostname);
    }

    private AddEsxiHostVSphereLicenseRequest getLicenseRequest(final ComponentEndpointIds componentEndpointIds, final String hostname)
    {
        final AddEsxiHostVSphereLicenseRequest requestMessage = new AddEsxiHostVSphereLicenseRequest();
        requestMessage.setHostname(hostname);
        requestMessage.setCredentials(new Credentials(componentEndpointIds.getEndpointUrl(), null, null));
        requestMessage.setComponentEndpointIds(
                new com.dell.cpsd.virtualization.capabilities.api.ComponentEndpointIds(componentEndpointIds.getComponentUuid(),
                        componentEndpointIds.getEndpointUuid(), componentEndpointIds.getCredentialUuid()));
        return requestMessage;
    }

}

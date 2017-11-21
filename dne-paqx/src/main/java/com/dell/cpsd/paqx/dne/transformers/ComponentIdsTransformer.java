/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 **/

package com.dell.cpsd.paqx.dne.transformers;

import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import org.springframework.stereotype.Component;

/**
 * This class can be used to fetch any component endpoint ids
 * from the repository.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@Component
public class ComponentIdsTransformer
{
    private final DataServiceRepository repository;

    public ComponentIdsTransformer(final DataServiceRepository repository)
    {
        this.repository = repository;
    }

    /**
     * This method returns the Component Endpoint Ids based on the
     * component type.
     *
     * @param componentType componentType
     * @return ComponentEndpointIds
     */
    protected ComponentEndpointIds getComponentEndpointIdsByComponentType(final String componentType)
    {
        final ComponentEndpointIds componentEndpointIds = repository.getComponentEndpointIds(componentType);

        if (componentEndpointIds == null)
        {
            throw new IllegalStateException("No component ids found.");
        }
        return componentEndpointIds;
    }

    /**
     * This method fetches the component endpoint and credential ids
     * based on the endpoint type.
     *
     * @param endpointType endpointType
     * @return ComponentEndpointIds
     */
    protected ComponentEndpointIds getVCenterComponentEndpointIdsByEndpointType(final String endpointType)
    {
        final ComponentEndpointIds componentEndpointIds = repository.getVCenterComponentEndpointIdsByEndpointType(endpointType);

        if (componentEndpointIds == null)
        {
            throw new IllegalStateException("No component endpoint ids found for endpoint with type" + endpointType);
        }

        return componentEndpointIds;
    }

    /**
     * This method fetches the component endpoint and credential ids
     * based on the component, endpoint, and credential type.
     *
     * @param componentType  componentType
     * @param endpointType   endpointType
     * @param credentialType credentialType
     * @return ComponentEndpointIds
     */
    protected ComponentEndpointIds getComponentEndpointIdsByCredentialType(final String componentType, final String endpointType,
            final String credentialType)
    {
        final ComponentEndpointIds componentEndpointIds = repository.getComponentEndpointIds(componentType, endpointType, credentialType);

        if (componentEndpointIds == null)
        {
            throw new IllegalStateException("No component ids found.");
        }
        return componentEndpointIds;
    }
}

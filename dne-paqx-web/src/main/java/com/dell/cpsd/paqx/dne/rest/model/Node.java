/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
package com.dell.cpsd.paqx.dne.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Node
{
    @JsonProperty
    private String id;

    @JsonProperty
    private String serviceTag;

    public Node()
    {
    }

    public Node(final String id, final String serviceTag)
    {
        this.id = id;
        this.serviceTag = serviceTag;
    }

    public String getId()
    {
        return id;
    }

    public void setId(final String id)
    {
        this.id = id;
    }

    public String getServiceTag()
    {
        return serviceTag;
    }

    public void setServiceTag(final String serviceTag)
    {
        this.serviceTag = serviceTag;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof Node))
        {
            return false;
        }

        final Node node = (Node) o;

        if (!getId().equals(node.getId()))
        {
            return false;
        }
        return getServiceTag().equals(node.getServiceTag());
    }

    @Override
    public int hashCode()
    {
        int result = getId().hashCode();
        result = 31 * result + getServiceTag().hashCode();
        return result;
    }
}

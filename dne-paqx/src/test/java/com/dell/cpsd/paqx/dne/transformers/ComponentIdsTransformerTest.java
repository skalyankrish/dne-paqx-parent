/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 **/

package com.dell.cpsd.paqx.dne.transformers;

import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Component Endpoint Ids Transformer Unit Test class
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class ComponentIdsTransformerTest
{
    @Mock
    private DataServiceRepository dataServiceRepository;

    @Mock
    private ComponentEndpointIds componentEndpointIds;

    private ComponentIdsTransformer transformer;

    @Before
    public void setUp() throws Exception
    {
        this.transformer = new ComponentIdsTransformer(this.dataServiceRepository);
    }

    @Test
    public void testGetComponentEndpointIdsByComponentTypeIsValid() throws Exception
    {
        when(this.dataServiceRepository.getComponentEndpointIds(anyString())).thenReturn(this.componentEndpointIds);

        final ComponentEndpointIds componentEndpointIdsByComponentType = this.transformer
                .getComponentEndpointIdsByComponentType(anyString());

        assertNotNull(componentEndpointIdsByComponentType);
        assertSame(this.componentEndpointIds, componentEndpointIdsByComponentType);
    }

    @Test
    public void testGetComponentEndpointIdsByComponentTypeException() throws Exception
    {
        when(this.dataServiceRepository.getComponentEndpointIds(anyString())).thenReturn(null);

        try
        {
            this.transformer.getComponentEndpointIdsByComponentType(anyString());
            fail("Expected exception to be thrown but was not");
        }
        catch (IllegalStateException ex)
        {
            assertThat(ex.getMessage(), containsString("No component ids found"));
        }
    }

    @Test
    public void testGetVCenterComponentEndpointIdsByEndpointTypeIsValid() throws Exception
    {
        when(this.dataServiceRepository.getVCenterComponentEndpointIdsByEndpointType(anyString())).thenReturn(this.componentEndpointIds);

        final ComponentEndpointIds componentEndpointIdsByComponentType = this.transformer
                .getVCenterComponentEndpointIdsByEndpointType(anyString());

        assertNotNull(componentEndpointIdsByComponentType);
        assertSame(this.componentEndpointIds, componentEndpointIdsByComponentType);
    }

    @Test
    public void testGetVCenterComponentEndpointIdsByEndpointTypeException() throws Exception
    {
        when(this.dataServiceRepository.getVCenterComponentEndpointIdsByEndpointType(anyString())).thenReturn(null);

        try
        {
            this.transformer.getVCenterComponentEndpointIdsByEndpointType(anyString());
            fail("Expected exception to be thrown but was not");
        }
        catch (IllegalStateException ex)
        {
            assertThat(ex.getMessage(), containsString("No component endpoint ids found"));
        }
    }

    @Test
    public void testGetComponentEndpointIdsByCredentialTypeIsValid() throws Exception
    {
        when(this.dataServiceRepository.getComponentEndpointIds(anyString(), anyString(), anyString()))
                .thenReturn(this.componentEndpointIds);

        final ComponentEndpointIds componentEndpointIdsByComponentType = this.transformer
                .getComponentEndpointIdsByCredentialType(anyString(), anyString(), anyString());

        assertNotNull(componentEndpointIdsByComponentType);
        assertSame(this.componentEndpointIds, componentEndpointIdsByComponentType);
    }

    @Test
    public void testGetComponentEndpointIdsByCredentialTypeException() throws Exception
    {
        when(this.dataServiceRepository.getComponentEndpointIds(anyString(), anyString(), anyString())).thenReturn(null);

        try
        {
            this.transformer.getComponentEndpointIdsByCredentialType(anyString(), anyString(), anyString());
            fail("Expected exception to be thrown but was not");
        }
        catch (IllegalStateException ex)
        {
            assertThat(ex.getMessage(), containsString("No component ids found"));
        }
    }
}
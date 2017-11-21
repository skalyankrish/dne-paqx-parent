/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 **/

package com.dell.cpsd.paqx.dne.transformers;

import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.virtualization.capabilities.api.RemoteCommandExecutionRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.VmPasswordUpdateRequest;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * Unit tests for remote command execution request transformer
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class RemoteCommandExecutionRequestTransformerTest
{
    @Mock
    private ComponentIdsTransformer componentIdsTransformer;

    @Mock
    private NodeDetail nodeDetail;

    @Mock
    private DelegateExecution delegateExecution;

    @Mock
    private ComponentEndpointIds componentEndpointIds;

    @Mock
    private ComponentEndpointIds factoryComponentEndpointIds;

    private RemoteCommandExecutionRequestTransformer remoteCommandExecutionRequestTransformer;

    private static final String VCENTER_CUSTOMER_TYPE        = "VCENTER-CUSTOMER";
    private final        String scaleIoVmManagementIpAddress = "1.1.1.1";

    @Before
    public void setup() throws Exception
    {
        remoteCommandExecutionRequestTransformer = new RemoteCommandExecutionRequestTransformer(componentIdsTransformer);
    }

    @Test
    public void testRemoteCommandExecMessageBuildingOtherThanChangePasswordIsValid() throws Exception
    {
        when(delegateExecution.getVariable(DelegateConstants.NODE_DETAIL)).thenReturn(nodeDetail);
        when(componentIdsTransformer.getComponentEndpointIdsByCredentialType("COMMON-SERVER", "COMMON-SVM", "SVM-COMMON"))
                .thenReturn(componentEndpointIds);
        when(nodeDetail.getScaleIoSvmManagementIpAddress()).thenReturn(scaleIoVmManagementIpAddress);

        final RemoteCommandExecutionRequestMessage requestMessage = remoteCommandExecutionRequestTransformer
                .buildRemoteCodeExecutionRequest(delegateExecution,
                        RemoteCommandExecutionRequestMessage.RemoteCommand.INSTALL_PACKAGE_SDS_LIA);

        assertNotNull(requestMessage);
        assertNotNull(requestMessage.getComponentEndpointIds());

        assertEquals(RemoteCommandExecutionRequestMessage.OsType.LINUX, requestMessage.getOsType());
        assertEquals(scaleIoVmManagementIpAddress, requestMessage.getRemoteHost());
        assertEquals(RemoteCommandExecutionRequestMessage.RemoteCommand.INSTALL_PACKAGE_SDS_LIA, requestMessage.getRemoteCommand());
    }

    @Test
    public void testRemoteCommandExecMessageBuildingChangePasswordIsValid() throws Exception
    {
        when(delegateExecution.getVariable(DelegateConstants.NODE_DETAIL)).thenReturn(nodeDetail);
        when(componentIdsTransformer.getComponentEndpointIdsByCredentialType("COMMON-SERVER", "COMMON-SVM", "SVM-COMMON"))
                .thenReturn(componentEndpointIds);
        when(nodeDetail.getScaleIoSvmManagementIpAddress()).thenReturn(scaleIoVmManagementIpAddress);
        when(componentIdsTransformer.getComponentEndpointIdsByCredentialType("COMMON-SERVER", "COMMON-SVM", "SVM-FACTORY"))
                .thenReturn(factoryComponentEndpointIds);

        final RemoteCommandExecutionRequestMessage requestMessage = remoteCommandExecutionRequestTransformer
                .buildRemoteCodeExecutionRequest(delegateExecution, RemoteCommandExecutionRequestMessage.RemoteCommand.CHANGE_PASSWORD);

        assertNotNull(requestMessage);
        assertNotNull(requestMessage.getComponentEndpointIds());

        assertEquals(RemoteCommandExecutionRequestMessage.OsType.LINUX, requestMessage.getOsType());
        assertEquals(scaleIoVmManagementIpAddress, requestMessage.getRemoteHost());
        assertEquals(RemoteCommandExecutionRequestMessage.RemoteCommand.CHANGE_PASSWORD, requestMessage.getRemoteCommand());

        final VmPasswordUpdateRequest vmPasswordUpdateRequest = requestMessage.getVmPasswordUpdateRequest();

        assertNotNull(vmPasswordUpdateRequest);
        assertNotNull(vmPasswordUpdateRequest.getComponentEndpointIds());
        assertEquals(VmPasswordUpdateRequest.CredentialName.SVM_FACTORY, vmPasswordUpdateRequest.getCredentialName());
    }
}

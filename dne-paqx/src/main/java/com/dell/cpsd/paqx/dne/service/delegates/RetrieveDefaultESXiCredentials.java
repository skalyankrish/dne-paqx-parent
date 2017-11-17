/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.delegates.model.ESXiCredentialDetails;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.virtualization.capabilities.api.ListEsxiCredentialDetailsRequestMessage;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.ESXI_CREDENTIAL_DETAILS;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.RETRIEVE_DEFAULT_ESXI_CREDENTIALS_FAILED;

@Component
@Scope("prototype")
@Qualifier("retrieveDefaultESXiCredentials")
public class RetrieveDefaultESXiCredentials extends BaseWorkflowDelegate
{
    /**
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RetrieveDefaultESXiCredentials.class);

    /**
     * The <code>NodeService</code> instance
     */
    private final NodeService nodeService;

    @Autowired
    public RetrieveDefaultESXiCredentials(final NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Execute Retrieve ESXi Credential Details");

        ComponentEndpointIds returnData = null;
        try
        {
            final ListEsxiCredentialDetailsRequestMessage requestMessage = getListDefaultCredentialsRequestMessage();
            returnData = this.nodeService.listDefaultCredentials(requestMessage);
        }
        catch (Exception e)
        {
            LOGGER.error("An Unexpected Exception Occurred attempting to retrieve the ESXi Default Credentials.", e);
            updateDelegateStatus(
                    "An Unexpected Exception Occurred attempting to retrieve the ESXi Default Credentials.  Reason: " + e.getMessage());
            throw new BpmnError(RETRIEVE_DEFAULT_ESXI_CREDENTIALS_FAILED, "An Unexpected Exception Occurred attempting to retrieve the ESXi Default Credentials.  Reason: " + e.getMessage());
        }
        if (returnData == null)
        {
            throw new IllegalStateException("List default credentials failed");
        }

        ESXiCredentialDetails esXiCredentialDetails = new ESXiCredentialDetails();
        esXiCredentialDetails.setComponentUuid(returnData.getComponentUuid());
        esXiCredentialDetails.setCredentialUuid(returnData.getCredentialUuid());
        esXiCredentialDetails.setEndpointUuid(returnData.getEndpointUuid());

        delegateExecution.setVariable(ESXI_CREDENTIAL_DETAILS, esXiCredentialDetails);
    }

    private ListEsxiCredentialDetailsRequestMessage getListDefaultCredentialsRequestMessage()
    {
        final ListEsxiCredentialDetailsRequestMessage requestMessage = new ListEsxiCredentialDetailsRequestMessage();
        requestMessage.setComponentElementType(
                ListEsxiCredentialDetailsRequestMessage.ComponentElementType.COMMON_SERVER);
        requestMessage.setEndpointElementType(
                ListEsxiCredentialDetailsRequestMessage.EndpointElementType.COMMON_DELL_POWEREDGE_ESXI_HOST_EP);
        requestMessage.setCredentialName(ListEsxiCredentialDetailsRequestMessage.CredentialName.ESXI_HOST_DEFAULT);

        return requestMessage;
    }

}
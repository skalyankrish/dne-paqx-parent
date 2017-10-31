/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import com.dell.cpsd.paqx.dne.service.model.BootDeviceIdracStatus;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;

/**
 * Task responsible for handling Boot Order Sequence
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
@Component
@Scope("prototype")
@Qualifier("disablePXEBoot")
public class DisablePXEBoot extends BaseWorkflowDelegate
{
    /**
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DisablePXEBoot.class);

    /**
     * The <code>NodeService</code> instance
     */
    private NodeService nodeService;

    /**
     *  SetBootOrderAndDisablePXETaskHandler constructor.
     *
     * @param nodeService
     *            - The <code>NodeService</code> instance.
     *
     * @since 1.0
     */
    @Autowired
    public DisablePXEBoot(NodeService nodeService){
        this.nodeService = nodeService;
    }

    /*
     * This method add all the node information to the response object
     */
    private Map<String, String> buildResponseResult(BootDeviceIdracStatus bootDeviceIdracStatus)
    {
        Map<String, String> result = new HashMap<>();

        if (bootDeviceIdracStatus == null)
        {
            return result;
        }

        if (bootDeviceIdracStatus.getStatus() != null)
        {
            result.put("bootDeviceIdracStatusRequest", bootDeviceIdracStatus.getStatus());
        }

        if (bootDeviceIdracStatus.getErrors() != null)
        {
            result.put("bootDeviceIdracErrorsList", bootDeviceIdracStatus.getErrors().toString());
        }

        return result;
    }

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Execute Config Pxe Boot Task");
        final String taskMessage = "Configure PXE Boot";
        final NodeDetail nodeDetail = (NodeDetail) delegateExecution.getVariable(NODE_DETAIL);
/*        try
        {
            String uuid = job.getInputParams().getSymphonyUuid();
            String ipAddress = job.getInputParams().getIdracIpAddress();

            LOGGER.info("uuid:" + uuid);
            LOGGER.info("ipAddress:" + ipAddress);

            BootDeviceIdracStatus bootDeviceIdracStatusRequest = nodeService.configurePxeBoot(uuid, ipAddress);
            if ("SUCCESS".equalsIgnoreCase(bootDeviceIdracStatusRequest.getStatus()))
            {
                response.setResults(buildResponseResult(bootDeviceIdracStatusRequest));
                response.setWorkFlowTaskStatus(Status.SUCCEEDED);
                return true;
            }
            else
            {
                response.addError(bootDeviceIdracStatusRequest.getErrors().toString());
            }
        }
        catch(Exception e)
        {
            LOGGER.error("Error showing boot order status", e);
            response.addError(e.toString());
        }
        response.setWorkFlowTaskStatus(Status.FAILED);
        return false;*/

        LOGGER.info(taskMessage + " on Node " + nodeDetail.getServiceTag() + " was successful.");
        updateDelegateStatus(taskMessage + " on Node " + nodeDetail.getServiceTag() + " was successful.");
    }
}

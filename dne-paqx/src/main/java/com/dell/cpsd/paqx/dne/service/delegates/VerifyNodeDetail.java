/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.VERIFY_NODE_DETAIL_FAILED;

public class VerifyNodeDetail extends BaseWorkflowDelegate
{
    private static final Logger LOGGER = LoggerFactory.getLogger(VerifyNodeDetail.class);

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Execute Verify Node Detail");
        final String taskMessage = "Verify Node Detail";

        final NodeDetail nodeDetail = (NodeDetail) delegateExecution.getVariable(NODE_DETAIL);

        List<String> missingFields = new ArrayList<>();

        if (nodeDetail == null) {
            final String message = "Node details were not found!  Please add Node details and try again.";
            LOGGER.error(message);
            updateDelegateStatus(message);
            throw new BpmnError(VERIFY_NODE_DETAIL_FAILED,
                                message);
        }

        if (StringUtils.isBlank(nodeDetail.getId())) {
            missingFields.add("id");
        }
        if (StringUtils.isBlank(nodeDetail.getServiceTag())) {
            missingFields.add("serviceTag");
        }
        if (StringUtils.isBlank(nodeDetail.getIdracIpAddress())) {
            missingFields.add("idracIpAddress");
        }
        if (StringUtils.isBlank(nodeDetail.getIdracGatewayIpAddress())) {
            missingFields.add("idracGatewayIpAddress");
        }
        if (StringUtils.isBlank(nodeDetail.getIdracSubnetMask())) {
            missingFields.add("idracSubnetMask");
        }
        if (StringUtils.isBlank(nodeDetail.getEsxiManagementIpAddress())) {
            missingFields.add("esxiManagementIpAddress");
        }
        if (StringUtils.isBlank(nodeDetail.getEsxiManagementGatewayIpAddress())) {
            missingFields.add("esxiManagementGatewayIpAddress");
        }
        if (StringUtils.isBlank(nodeDetail.getEsxiManagementSubnetMask())) {
            missingFields.add("esxiManagementSubnetMask");
        }
        if (StringUtils.isBlank(nodeDetail.getEsxiManagementHostname())) {
            missingFields.add("esxiManagementHostname");
        }
        if (StringUtils.isBlank(nodeDetail.getScaleIoData1SvmIpAddress())) {
            missingFields.add("scaleIoData1SvmIpAddress");
        }

        if (StringUtils.isBlank(nodeDetail.getScaleIoData1SvmSubnetMask())) {
            missingFields.add("scaleIoData1SvmSubnetMask");
        }
        if (StringUtils.isBlank(nodeDetail.getScaleIoData2SvmIpAddress())) {
            missingFields.add("scaleIoData2SvmIpAddress");
        }
        if (StringUtils.isBlank(nodeDetail.getScaleIoData2SvmSubnetMask())) {
            missingFields.add("scaleIoData2SvmSubnetMask");
        }
        if (StringUtils.isBlank(nodeDetail.getScaleIoData1EsxIpAddress())) {
            missingFields.add("scaleIoData1EsxIpAddress");
        }
        if (StringUtils.isBlank(nodeDetail.getScaleIoData1EsxSubnetMask())) {
            missingFields.add("scaleIoData1EsxSubnetMask");
        }
        if (StringUtils.isBlank(nodeDetail.getScaleIoData2EsxIpAddress())) {
            missingFields.add("scaleIoData2EsxIpAddress");
        }
        if (StringUtils.isBlank(nodeDetail.getScaleIoData2EsxSubnetMask())) {
            missingFields.add("scaleIoData2EsxSubnetMask");
        }

        if (StringUtils.isBlank(nodeDetail.getScaleIoSvmManagementIpAddress())) {
            missingFields.add("scaleIoSvmManagementIpAddress");
        }
        if (StringUtils.isBlank(nodeDetail.getScaleIoSvmManagementGatewayAddress())) {
            missingFields.add("scaleIoSvmManagementGatewayAddress");
        }
        if (StringUtils.isBlank(nodeDetail.getScaleIoSvmManagementSubnetMask())) {
            missingFields.add("scaleIoSvmManagementSubnetMask");
        }
        //if (StringUtils.isBlank(nodeDetail.getHostname())) {
        //    missingFields.add("hostname");
        //}
        if (StringUtils.isBlank(nodeDetail.getClusterName())) {
            missingFields.add("clusterName");
        }
        if (StringUtils.isBlank(nodeDetail.getvMotionManagementIpAddress())) {
            missingFields.add("vMotionManagementIpAddress");
        }
        if (StringUtils.isBlank(nodeDetail.getvMotionManagementSubnetMask())) {
            missingFields.add("vMotionManagementSubnetMask");
        }
        //if (StringUtils.isBlank(nodeDetail.getProtectionDomain())) {
        //    missingFields.add("protectionDomain");
        //}
        if (CollectionUtils.isNotEmpty(missingFields)) {
            final String message = "Node details are incomplete!  Please update Node details with the following information and try again.  Missing values for " + StringUtils.join(missingFields, ", ") + ".";
            LOGGER.error(message);
            updateDelegateStatus(message);
            throw new BpmnError(VERIFY_NODE_DETAIL_FAILED,
                                message);
        }

        LOGGER.info("Verification of Details on Node " + nodeDetail.getServiceTag() + " was successful.");
        updateDelegateStatus("Verification of Details on Node " + nodeDetail.getServiceTag() + " was successful.");
    }
}

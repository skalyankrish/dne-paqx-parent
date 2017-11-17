/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service;

import com.dell.cpsd.paqx.dne.service.delegates.HandleError;
import com.dell.cpsd.paqx.dne.service.delegates.exception.JobNotFoundException;
import com.dell.cpsd.paqx.dne.service.model.multinode.Activity;
import com.dell.cpsd.paqx.dne.service.model.multinode.Error;
import com.dell.cpsd.paqx.dne.service.model.multinode.Status;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.camunda.bpm.engine.ActivityTypes;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.impl.cfg.IdGenerator;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CamundaWorkflowServiceImpl implements ICamundaWorkflowService
{

    private RuntimeService runtimeService;
    private RepositoryService repositoryService;
    private HistoryService historyService;
    private IdGenerator idGenerator;


    private static final Log LOGGER = LogFactory.getLog(CamundaWorkflowServiceImpl.class);

    @Autowired
    public CamundaWorkflowServiceImpl(final RuntimeService runtimeService, final HistoryService historyService,
                                      final RepositoryService repositoryService)
    {
        this.runtimeService = runtimeService;
        this.historyService = historyService;
        this.repositoryService = repositoryService;
    }

    @Override
    public String startWorkflow(final String processId, final Map<String, Object> inputVariables)
    {
        String businessKeyId = getIdGenerator().getNextId();
        Thread processThread = new Thread(() -> runtimeService.startProcessInstanceByKey(processId, businessKeyId, inputVariables));
        processThread.start();
        return businessKeyId;
    }

    /**
     * Get status for workflow by Job ID
     *
     * @param jobId Job ID (Camunda busines key)
     * @return
     */
    @Override
    public Status getStatus(final String jobId) throws JobNotFoundException
    {
        if (StringUtils.isEmpty(jobId))
        {
            return null;
        }

        Status workflowStatus = null;

        String processInstanceId = null;
        String processDefinitionId = null;
        try
        {
            ProcessInstance parentProcessInstance = runtimeService.createProcessInstanceQuery()
                                                                  .processInstanceBusinessKey(jobId).singleResult();
            if (parentProcessInstance != null)
            {
                processInstanceId = parentProcessInstance.getProcessInstanceId();
                processDefinitionId = parentProcessInstance.getProcessDefinitionId();
            }
        }
        catch (Exception e)
        {
            LOGGER.warn("Exception caught due to no process instances found for business key " + jobId);
        }

        if (processInstanceId == null)
        {
            try
            {
                HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                                                                                .processInstanceBusinessKey(jobId)
                                                                                .singleResult();
                if (historicProcessInstance != null)
                {
                    processInstanceId = historicProcessInstance.getId();
                    processDefinitionId = historicProcessInstance.getProcessDefinitionId();
                }
            }
            catch (Exception e)
            {
                LOGGER.warn("Exception caught due to no historic process instances found for business key " + jobId);
            }
        }

        /*ProcessInstance processInstance = */

        if (processInstanceId != null)
        {
            // get bpmn model of the process instance
            BpmnModelInstance bpmnModelInstance = repositoryService.getBpmnModelInstance(processDefinitionId);

            final Status currentStatus = getWorkflowStatusByProcessInstanceId(jobId, processInstanceId,
                                                                              bpmnModelInstance);

            Map<String, String> subprocessIdsMap = new HashMap<>();
            List<ProcessInstance> subProcesses = runtimeService.createProcessInstanceQuery().superProcessInstanceId(
                    processInstanceId).list();
            if (subProcesses != null && subProcesses.size() > 0)
            {
                subProcesses.stream().forEach(subProcess -> {
                    subprocessIdsMap.put(subProcess.getProcessInstanceId(), subProcess.getProcessDefinitionId());
                });
            }

            List<HistoricProcessInstance> historicSubProcesses = historyService.createHistoricProcessInstanceQuery()
                                                                               .superProcessInstanceId(
                                                                                       processInstanceId).list();
            if (historicSubProcesses != null && historicSubProcesses.size() > 0)
            {
                historicSubProcesses.forEach(historicSubProcess -> {
                    subprocessIdsMap.put(historicSubProcess.getId(), historicSubProcess.getProcessDefinitionId());
                });
            }

            if (subprocessIdsMap.size() > 0)
            {
                subprocessIdsMap.entrySet().forEach(entry -> {
                    BpmnModelInstance subBpmnModelInstance = repositoryService.getBpmnModelInstance(entry.getValue());
                    Status subProcessStatus = getWorkflowStatusByProcessInstanceId(jobId, entry.getKey(),
                                                                                   subBpmnModelInstance);
                    if (subProcessStatus != null)
                    {
                        currentStatus.getSubProcesses().add(subProcessStatus);
                    }
                });
            }
            workflowStatus = currentStatus;
        } else {
            String errMsg = "Can not find the Job with jobId " + jobId;
            LOGGER.error(errMsg);
            throw new JobNotFoundException(HttpStatus.NOT_FOUND.value(), errMsg);
        }
        return workflowStatus;
    }

    private Status getWorkflowStatusByProcessInstanceId(final String jobId, final String processInstanceId,
                                                        final BpmnModelInstance bpmnModelInstance)
    {
        Status status = null;

        if (processInstanceId != null)
        {
            final Status currentStatus = new Status();
            currentStatus.setJobId(jobId);
            currentStatus.setId(processInstanceId);

            final HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                                                                                  .processInstanceId(processInstanceId)
                                                                                  .singleResult();

            if (historicProcessInstance != null)
            {
                currentStatus.setState(Status.StatusState.fromValue(historicProcessInstance.getState()));

                List<HistoricActivityInstance> historicActivityInstances = historyService
                        .createHistoricActivityInstanceQuery().processInstanceId(processInstanceId)
                        .orderByHistoricActivityInstanceStartTime().asc().list();

                List<Activity> completed = processHistoryActivities(historicProcessInstance, historicActivityInstances,
                                                                    bpmnModelInstance);
                currentStatus.setCompletedSteps(completed);

                List<HistoricVariableInstance> details = historyService.createHistoricVariableInstanceQuery()
                                                                       .processInstanceId(processInstanceId).list();

                final Map<String, Object> properties = new HashMap<>();
                if (details != null && !details.isEmpty())
                {
                    details.stream().forEach(detail -> {
                        properties.put(detail.getName(), detail.getValue());
                    });
                }
                if (properties.size() > 0)
                {
                    final String errorCode = (String) properties.get("errorCode");
                    if (errorCode != null)
                    {
                        final String errorMessage = (String) properties.get("errorMessage");
                        Error error = new Error();
                        error.setErrorCode(errorCode);
                        error.setErrorMessage(errorMessage);
                        currentStatus.getErrors().add(error);
                        currentStatus.setState(Status.StatusState.FAILED);
                    }
                    currentStatus.setAdditionalProperties(properties);
                }
            }

            if (Status.StatusState.ACTIVE.equals(currentStatus.getState())) {
                try
                {
                    // get all active activities of the process instance (unique values)
                    List<String> activeActivityIds = runtimeService.getActiveActivityIds(processInstanceId).
                            stream().distinct().collect(Collectors.toList());

                    if (activeActivityIds != null && activeActivityIds.size() > 0)
                    {
                        List<Activity> currentActivities = new ArrayList<>();
                        for (String activeActivityId : activeActivityIds)
                        {
                            final String activityName = getActivityName(activeActivityId, bpmnModelInstance);
                            Activity activity = new Activity();
                            activity.setId(activeActivityId);
                            activity.setName(activityName);
                            currentActivities.add(activity);
                        }
                        currentStatus.setCurrentActivities(currentActivities);
                    }
                }
                catch (Exception e)
                {
                    LOGGER.warn("Exception occurred trying to get current activities for process " + processInstanceId);
                }
            }

            status = currentStatus;

        }
        return status;
    }

    /**
     * Recursive: make a UI readable presentation of completed activities, in order of executed tasks.
     *
     * @param historicProcessInstance
     * @param historicActivityInstances
     * @return
     */
    private List<Activity> processHistoryActivities(HistoricProcessInstance historicProcessInstance,
                                                    List<HistoricActivityInstance> historicActivityInstances,
                                                    BpmnModelInstance bpmnModelInstance)
    {

        if (historicProcessInstance.getId() == null)
        {
            throw new IllegalArgumentException("Parent activity ID cannot be null!");
        }
        final String parentId = historicProcessInstance.getId();
        Set<Activity> ret = new LinkedHashSet<>();

        String activityName = null;
        for (HistoricActivityInstance hai : historicActivityInstances)
        {
            // skip some processes
            if (!ActivityTypes.SUB_PROCESS.equals(hai.getActivityType()) && !ActivityTypes.TASK_SERVICE.equals(
                    hai.getActivityType()) && !ActivityTypes.MULTI_INSTANCE_BODY.equals(hai.getActivityType()))
            {
                continue;
            }

            if (parentId.equals(hai.getParentActivityInstanceId()))
            {

                activityName = getActivityName(hai.getActivityId(), bpmnModelInstance);

                Activity activity = new Activity();
                activity.setId(hai.getId());
                activity.setName(activityName);
                activity.setStartTime(hai.getStartTime());
                activity.setEndTime(hai.getEndTime());
                activity.setDurationInMillis(hai.getDurationInMillis());

                if (!isErrorHandler(hai, bpmnModelInstance))
                {
                    ret.add(activity);
                }
            }
        }

        return new ArrayList<>(ret);
    }

    private String getActivityName(final String activityId, final BpmnModelInstance bpmnModelInstance)
    {

        String result = activityId;
        ModelElementInstance modelElementInstance = bpmnModelInstance.getModelElementById(activityId);
        if (modelElementInstance == null)
        {
            modelElementInstance = bpmnModelInstance.getModelElementById(
                    activityId.substring(0, activityId.indexOf("#multi")));
        }
        if (modelElementInstance != null)
        {
            result = modelElementInstance.getAttributeValue("name");
        }
        return result;
    }

    /**
     * Check if this task is error handler - we don't want those to show up in status.
     * Assumes all error handlers extend HandleError,
     *
     * @param hai
     * @return
     */
    private boolean isErrorHandler(HistoricActivityInstance hai, BpmnModelInstance bpmnModelInstance)
    {

        ModelElementInstance modelElementById = bpmnModelInstance.getModelElementById(hai.getActivityId());
        if (modelElementById != null)
        {
            String className = modelElementById.getAttributeValueNs("http://camunda.org/schema/1.0/bpmn", "class");

            if (className != null)
            {
                try
                {
                    Class claz = Class.forName(className);
                    if (HandleError.class.isAssignableFrom(claz))
                    {
                        return true;
                    }
                }
                catch (ClassNotFoundException e)
                {
                    LOGGER.warn("Activity ID= " + hai.getActivityId() + " has unknown Java class: " + className);
                    return false;
                }
            }
        }
        return false;
    }


    private synchronized IdGenerator getIdGenerator()
    {
        if (null == idGenerator)
        {
            idGenerator = ((SpringProcessEngineConfiguration) ProcessEngines.getDefaultProcessEngine()
                                                                            .getProcessEngineConfiguration())
                    .getIdGenerator();
        }
        return idGenerator;
    }

    public void setIdGenerator(final IdGenerator idGenerator)
    {
        this.idGenerator = idGenerator;
    }

}


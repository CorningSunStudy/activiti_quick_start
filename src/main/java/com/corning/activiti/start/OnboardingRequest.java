package com.corning.activiti.start;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.*;
import org.activiti.engine.form.FormData;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.FormType;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.activiti.engine.impl.form.DateFormType;
import org.activiti.engine.impl.form.LongFormType;
import org.activiti.engine.impl.form.StringFormType;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class OnboardingRequest {

    private static final String ENGINE_TYPE_MEM = "mem";
    private static final String ENGINE_TYPE_PG = "pg";

    public static void main(String[] args) throws ParseException {
        ProcessEngineConfiguration cfg = getProcessEngineConfiguration(ENGINE_TYPE_MEM);

        ProcessEngine processEngine = cfg.buildProcessEngine();
        String pName = processEngine.getName();
        String ver = ProcessEngine.VERSION;
        log.info("ProcessEngine [{}] Version: [{}]", pName, ver);

        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deployment = repositoryService.createDeployment().addClasspathResource("onboarding.bpmn20.xml").deploy();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
        log.info("Found process definition [{}] with id [{}]", processDefinition.getName(), processDefinition.getId());

        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("onboarding");
        log.info("Onboarding process started with process instance id [" + processInstance.getProcessInstanceId() + "] key [" + processInstance.getProcessDefinitionKey() + "]");

        TaskService taskService = processEngine.getTaskService();
        FormService formService = processEngine.getFormService();
        HistoryService historyService = processEngine.getHistoryService();

        Scanner scanner = new Scanner(System.in);
        while (processInstance != null && !processInstance.isEnded()) {
            List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("managers").list();
            log.info("Active outstanding tasks: [" + tasks.size() + "]");

            for (Task task : tasks) {
                log.info("Processing Task [" + task.getName() + "]");

                Map<String, Object> variables = inputVariables(formService, scanner, task);

                taskService.complete(task.getId(), variables);

                printActivityHis(processDefinition, processInstance, historyService);
            }

            // Re-query the process instance, making sure the latest state is available
            processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstance.getId()).singleResult();
        }
        scanner.close();
    }

    private static void printActivityHis(ProcessDefinition processDefinition, ProcessInstance processInstance, HistoryService historyService) {
        HistoricActivityInstance endActivity = null;

        List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstance.getId()).finished()
                .orderByHistoricActivityInstanceEndTime().asc()
                .list();
        for (HistoricActivityInstance activity : activities) {
            if ("startEvent".equals(activity.getActivityType())) {
                log.info("BEGIN " + processDefinition.getName() + " [" + processInstance.getProcessDefinitionKey() + "] " + activity.getStartTime());
            }

            if ("endEvent".equals(activity.getActivityType())) {
                // Handle edge case where end step happens so fast that the end step
                // and previous step(s) are sorted the same. So, cache the end step
                // and display it last to represent the logical sequence.
                endActivity = activity;
            } else {
                log.info("-- " + activity.getActivityName() + " [" + activity.getActivityId() + "] " + activity.getDurationInMillis() + " ms");
            }
        }

        if (endActivity != null) {
            log.info("-- " + endActivity.getActivityName() + " [" + endActivity.getActivityId() + "] " + endActivity.getDurationInMillis() + " ms");
            log.info("COMPLETE " + processDefinition.getName() + " [" + processInstance.getProcessDefinitionKey() + "] " + endActivity.getEndTime());
        }
    }

    private static Map<String, Object> inputVariables(FormService formService, Scanner scanner, Task task) throws ParseException {
        Map<String, Object> variables = new HashMap<>(16);

        FormData formData = formService.getTaskFormData(task.getId());
        for (FormProperty formProperty : formData.getFormProperties()) {
            FormType type = formProperty.getType();
            String name = formProperty.getName();
            if (type instanceof StringFormType) {
                log.info(name + "?");
                String value = scanner.nextLine();
                variables.put(formProperty.getId(), value);
            } else if (type instanceof LongFormType) {
                log.info(name + "? (Must be a whole number)");
                Long value = Long.valueOf(scanner.nextLine());
                variables.put(formProperty.getId(), value);
            } else if (type instanceof DateFormType) {
                log.info(name + "? (Must be a date yyyy-mm-dd)");
                DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
                Date value = dateFormat.parse(scanner.nextLine());
                variables.put(formProperty.getId(), value);
            } else {
                log.info("<form type not supported>");
            }
        }
        return variables;
    }

    private static ProcessEngineConfiguration getProcessEngineConfiguration(String engineType) {
        switch (engineType) {
            case ENGINE_TYPE_MEM:
                return new StandaloneProcessEngineConfiguration()
                        .setJdbcUrl("jdbc:h2:mem:activiti;DB_CLOSE_DELAY=1000")
                        .setJdbcUsername("sa")
                        .setJdbcPassword("")
                        .setJdbcDriver("org.h2.Driver")
                        .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
            case ENGINE_TYPE_PG:
                return new StandaloneProcessEngineConfiguration()
                        .setJdbcUrl("jdbc:postgresql://localhost:5432/activiti")
                        .setJdbcUsername("")
                        .setJdbcPassword("")
                        .setJdbcDriver("org.postgresql.Driver")
                        .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
            default:
                throw new IllegalArgumentException("不支持的类型");
        }
    }
}

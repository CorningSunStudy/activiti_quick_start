package com.corning.activiti.start;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

import java.util.Date;

@Slf4j
public class AutomatedDataDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        Date now = new Date();
        execution.setVariable("autoWelcomeTime", now);
        log.info("Faux call to backend for [" + execution.getVariable("fullName") + "]");
    }
}

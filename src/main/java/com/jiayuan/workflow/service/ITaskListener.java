package com.jiayuan.workflow.service;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.stereotype.Component;

/**
 * @author luoyj
 * @date 2019/11/26.
 */
@Component
public class ITaskListener implements TaskListener {

    @Override
    public void notify(DelegateTask delegateTask) {
        String leader = (String) delegateTask.getVariable("leader");
        System.out.println(leader);
        delegateTask.setAssignee(leader);
    }
}

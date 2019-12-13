package com.jiayuan.workflow.service;

import org.activiti.engine.impl.persistence.entity.HistoricProcessInstanceEntityImpl;
import org.activiti.engine.impl.persistence.entity.HistoricVariableInstanceEntity;

import java.util.List;

/**
 * @author luoyj
 * @date 2019/11/26.
 */
public class IHistoricProcessInstanceEntityImpl extends HistoricProcessInstanceEntityImpl {
    private String endActivityId;
    private String businessKey;
    private String startUserId;
    private String startActivityId;
    private String superProcessInstanceId;
    private String tenantId = "";
    private String name;
    private String localizedName;
    private String description;
    private String localizedDescription;
    private String processDefinitionKey;
    private String processDefinitionName;
    private Integer processDefinitionVersion;
    private String deploymentId;
    private List<HistoricVariableInstanceEntity> queryVariables;

    public IHistoricProcessInstanceEntityImpl() {
        this.endActivityId = super.endActivityId;
        this.businessKey = super.businessKey;
        this.startUserId = super.startUserId;
        this.startActivityId = super.startActivityId;
        this.superProcessInstanceId = super.superProcessInstanceId;
        this.tenantId = super.tenantId;
        this.name = super.name;
        this.localizedName = super.localizedName;
        this.description = super.description;
        this.localizedDescription = super.localizedDescription;
        this.processDefinitionKey = super.processDefinitionKey;
        this.processDefinitionName = super.processDefinitionName;
        this.processDefinitionVersion = super.processDefinitionVersion;
        this.deploymentId = super.deploymentId;
        this.queryVariables = super.queryVariables;
    }

    @Override
    public String toString() {
        return "IHistoricProcessInstanceEntityImpl{" +
                "endActivityId='" + endActivityId + '\'' +
                ", businessKey='" + businessKey + '\'' +
                ", startUserId='" + startUserId + '\'' +
                ", startActivityId='" + startActivityId + '\'' +
                ", superProcessInstanceId='" + superProcessInstanceId + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", name='" + name + '\'' +
                ", localizedName='" + localizedName + '\'' +
                ", description='" + description + '\'' +
                ", localizedDescription='" + localizedDescription + '\'' +
                ", processDefinitionKey='" + processDefinitionKey + '\'' +
                ", processDefinitionName='" + processDefinitionName + '\'' +
                ", processDefinitionVersion=" + processDefinitionVersion +
                ", deploymentId='" + deploymentId + '\'' +
                ", queryVariables=" + queryVariables +
                '}';
    }
}

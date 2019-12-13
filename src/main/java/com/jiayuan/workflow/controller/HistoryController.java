package com.jiayuan.workflow.controller;

import com.jiayuan.workflow.service.BusinessService;
import com.jiayuan.workflow.utils.RestMessgae;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author luoyj
 * @date 2019/11/26.
 */
@Api(tags = "个人流程记录查询")
@RestController
public class HistoryController {
    @Autowired
    private HistoryService historyService;
    @Autowired
    private BusinessService businessService;

    @GetMapping("findProcessByUserId")
    @ApiOperation(value = "查询流程记录",notes = "根据用户ID查询个人流程记录")
    @ApiImplicitParam(name = "userId",value = "用户ID",dataType = "String",paramType = "query",example = "")
    public RestMessgae findProcessByUserId(String userId){
        List<HistoricProcessInstance> historicProcessInstanceList = historyService.createHistoricProcessInstanceQuery()
                .startedBy(userId).list();
        RestMessgae restMessgae = new RestMessgae();
        ArrayList<Object> list = new ArrayList<>();
        if (historicProcessInstanceList.size() > 0){
            for (HistoricProcessInstance hpi : historicProcessInstanceList) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("getBusinessKey",hpi.getBusinessKey());
                //关联业务表查询
                hashMap.put("business",businessService.getById(hpi.getBusinessKey()));
                hashMap.put("getDeleteReason",hpi.getDeleteReason());
                hashMap.put("getDeploymentId",hpi.getDeploymentId());
                hashMap.put("getDescription",hpi.getDescription());
                hashMap.put("getDurationInMillis",hpi.getDurationInMillis());
                hashMap.put("getEndActivityId",hpi.getEndActivityId());
                hashMap.put("getEndTime",hpi.getEndTime());
                hashMap.put("getId",hpi.getId());
                hashMap.put("getName",hpi.getName());
                hashMap.put("getProcessDefinitionId",hpi.getProcessDefinitionId());
                hashMap.put("getProcessDefinitionKey",hpi.getProcessDefinitionKey());
                hashMap.put("getProcessDefinitionName",hpi.getProcessDefinitionName());
                hashMap.put("getProcessVariables",hpi.getProcessVariables());
                hashMap.put("getStartTime",hpi.getStartTime());
                hashMap.put("getStartActivityId",hpi.getStartActivityId());
                list.add(hashMap);
            }
            restMessgae=  RestMessgae.success("查询成功",list);
        }
        return restMessgae;
    }
}

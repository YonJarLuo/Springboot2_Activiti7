package com.jiayuan.workflow.controller;

import com.jiayuan.workflow.entity.Business;
import com.jiayuan.workflow.service.BusinessService;
import com.jiayuan.workflow.utils.RestMessgae;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author luoyj
 * @date 2019/11/26.
 */

@RestController
@Api(tags="启动流程实例")
public class StartController {

    @Autowired
    private  RuntimeService runtimeService;
    @Autowired
    private BusinessService businessService;

    @PostMapping(path = "/startProcess")
    @ApiOperation(value = "启动流程申请",notes = "流程申请入口")
    @ApiImplicitParam(name = "business",value = "申请流程对象",dataType = "Business",required = true,paramType = "body")
    @Transactional(rollbackFor = Exception.class)
    public RestMessgae startProcess(@RequestBody Business business) {

        //variables 对应流程图中定义的变量， business表示关联的业务ID ，userID启动流程的用户ID
        String cbusinessID = UUID.randomUUID().toString().replace("-", "");

        //TODO 此处可以根据申请用户的角色来启动不同的流程图，processKey就是流程图.bpmn里面的唯一id的值
        String processKey = "myProcess_2";
        //设置提流程的用户ID
        Authentication.setAuthenticatedUserId(business.getUserID());

        //设置参数，流程启动时流程图中定义了EL表达式，可以获取到所设置的参数
        HashMap<String, Object> variables=new HashMap<>();
        variables.put("inputUser", business.getUserID());
        //TODO 模拟获取领导，每个系统都有自己的用户管理功能，此处可以获取领导ID。inputUser/leader/approver 都是在流程图中定义的参数变量
        variables.put("leader", "manager");
        //模拟超级管理员
        variables.put("approver", "sup_manager");

        //关联业务
        business.setBusinessID(cbusinessID);
        business.setCreateTime(new Date());
        business.setUpdateTime(new Date());

        RestMessgae restMessgae = new RestMessgae();
        ProcessInstance instance = null;
        try {
//            instance = runtimeService.startProcessInstanceByKey(processKey, variables);
            //TODO 关联业务id来启动流程 cbusinessID就是activity里的BusinessKey
            instance = runtimeService.startProcessInstanceByKey(processKey, cbusinessID,variables);
            if (instance != null) {
                Map<String, String> result = new HashMap<>(2);
                // 获取流程实例ID 并设置流程实例ID到业务表中
                business.setProcessID(instance.getId());
                result.put("processID", instance.getId());
                result.put("processDefinitionKey", instance.getProcessDefinitionKey());
                restMessgae = RestMessgae.success("启动成功", result);
                //使用mybatisPlus存储business到数据库中
                businessService.save(business);
                System.out.println("存储business到数据库中");
                System.out.println(business.toString());
            }
        } catch (Exception e) {
            restMessgae = RestMessgae.fail("启动失败", e.getMessage());
            e.printStackTrace();
        }
        return restMessgae;
    }


    @PostMapping(path = "searchByKey")
    @ApiOperation(value = "根据流程key查询流程实例",notes = "一个流程图下可能跑了多个流程实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processKey",value = "流程key",dataType = "String",paramType = "query",example = ""),
    })
    public RestMessgae searchProcessInstance(@RequestParam("processKey") String processDefinitionKey){
        RestMessgae restMessgae = new RestMessgae();
        List<ProcessInstance> runningList = new ArrayList<>();
        try {
            ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery();
            runningList = processInstanceQuery.processDefinitionKey(processDefinitionKey).list();
        } catch (Exception e) {
            restMessgae = RestMessgae.fail("查询失败", e.getMessage());
            e.printStackTrace();
        }

        int size = runningList.size();
        if (size > 0) {
            List<Map<String, String>> resultList = new ArrayList<>();
            for (ProcessInstance pi : runningList) {
                Map<String, String> resultMap = new HashMap<>(2);
                // 流程实例ID
                resultMap.put("processID", pi.getId());
                // 流程定义ID
                resultMap.put("processDefinitionKey", pi.getProcessDefinitionId());
                resultList.add(resultMap);
            }
            restMessgae = RestMessgae.success("查询成功", resultList);
        }
        return restMessgae;
    }


    @PostMapping(path = "searchByID")
    @ApiOperation(value = "根据流程实例ID查询流程实例",notes = "每个流程都会有自己的唯一ID，查询流程实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processID",value = "流程实例ID",dataType = "String",paramType = "query",example = ""),
    })
    public RestMessgae searchByID(@RequestParam("processID") String processDefinitionID){
        RestMessgae restMessgae  = new RestMessgae();
        ProcessInstance pi = null;
        try {
            pi = runtimeService.createProcessInstanceQuery().processInstanceId(processDefinitionID).singleResult();

            if (pi != null) {
                Map<String, Object> resultMap = new HashMap<>(2);
                // 流程实例ID
                resultMap.put("processID", pi.getId());
                // 流程定义ID
                resultMap.put("processDefinitionKey", pi.getProcessDefinitionId());

                Map<String, Object> processVariables = pi.getProcessVariables();
                System.out.println(processVariables.size());
                resultMap.put("Name", pi.getName());
                resultMap.put("BusinessKey", pi.getBusinessKey());
                resultMap.put("Description", pi.getDescription());
                resultMap.put("StartUserId", pi.getStartUserId());
                resultMap.put("ProcessVariables", pi.getProcessVariables());
                resultMap.put("StartTime", pi.getStartTime());
                resultMap.put("ProcessInstanceId", pi.getProcessInstanceId());
                restMessgae = RestMessgae.success("查询成功", resultMap);
            }
        } catch (Exception e) {
            restMessgae = RestMessgae.fail("查询失败", e.getMessage());
            e.printStackTrace();
        }
        return restMessgae;
    }

    @PostMapping(path = "deleteProcessInstanceByKey")
    @ApiOperation(value = "根据流程实例key删除流程实例",notes = "根据流程实例key删除流程实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processKey",value = "流程实例Key",dataType = "String",paramType = "query",example = ""),
    })
    public RestMessgae deleteProcessInstanceByKey(@RequestParam("processKey") String processDefinitionKey){
        RestMessgae restMessgae = new RestMessgae();
        List<ProcessInstance> runningList = new ArrayList<>();
        try {
            ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery();
            runningList = processInstanceQuery.processDefinitionKey(processDefinitionKey).list();
        } catch (Exception e) {
            restMessgae = RestMessgae.fail("删除失败", e.getMessage());
            e.printStackTrace();
        }

        int size = runningList.size();
        if (size > 0) {
            List<Map<String, String>> resultList = new ArrayList<>();
            for (ProcessInstance pi : runningList) {
                runtimeService.deleteProcessInstance(pi.getId(), "删除");
            }
            restMessgae = RestMessgae.success("删除成功", resultList);
        }
        return  restMessgae;
    }

    @PostMapping(path = "deleteProcessInstanceByID")
    @ApiOperation(value = "根据流程实例ID删除流程实例",notes = "根据流程实例ID删除流程实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processID",value = "流程实例ID",dataType = "String",paramType = "query",example = ""),
    })
    public RestMessgae deleteProcessInstanceByID(@RequestParam("processID") String processDefinitionID){
        RestMessgae restMessgae = new RestMessgae();
        try {
            runtimeService.deleteProcessInstance(processDefinitionID,"删除" + processDefinitionID);
        } catch (Exception e) {
            restMessgae = RestMessgae.fail("删除失败", e.getMessage());
            return  restMessgae;
        }
        restMessgae = RestMessgae.success("删除成功");
        return  restMessgae;
    }
}

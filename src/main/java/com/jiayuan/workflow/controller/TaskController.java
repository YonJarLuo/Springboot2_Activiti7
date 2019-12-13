package com.jiayuan.workflow.controller;

import com.jiayuan.workflow.enums.FlowStatus;
import com.jiayuan.workflow.service.BusinessService;
import com.jiayuan.workflow.utils.RestMessgae;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author luoyj
 * @date 2019/11/26.
 */

@RestController
@Api(tags = "任务相关接口")
public class TaskController {

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private BusinessService businessService;
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping(path = "/findTaskByAssignee")
    @ApiOperation(value = "根据流程用户ID查询当前人的个人任务", notes = "但是为了方便测试，在设置leader时，值是manager/sup_manger查询当前人的个人任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "assignee", value = "代理人（当前用户）", dataType = "String", paramType = "query", example = ""),
    })
    public RestMessgae findTaskByAssignee(@RequestParam("assignee") String assignee) {
        RestMessgae restMessgae = new RestMessgae();

        //创建任务查询对象
        List<Task> taskList;
        try {
            //指定个人任务查询
            taskList = taskService.createTaskQuery().taskAssignee(assignee).list();
            //支持分页
//            taskList = taskService.createTaskQuery().taskAssignee(assignee).listPage(pageNum - 1,pageSize);
            if (taskList != null && taskList.size() > 0) {
                List<Map<String, Object>> resultList = new ArrayList<>();
                for (Task task : taskList) {
                    Map<String, Object> resultMap = new HashMap<>(7);
                    /* 任务ID */
                    resultMap.put("taskID", task.getId());
                    resultMap.put("taskName", task.getName());
                    resultMap.put("taskCreateTime", task.getCreateTime().toString());
                    resultMap.put("processInstanceId", task.getProcessInstanceId());
                    resultMap.put("business",businessService.selectByProcessID(task.getProcessInstanceId()));
                    resultList.add(resultMap);
                }
                restMessgae = RestMessgae.success("查询成功", resultList);
            } else {
                restMessgae = RestMessgae.success("查询成功,没有代办任务");
            }
        } catch (Exception e) {
            restMessgae = RestMessgae.fail("查询失败", e.getMessage());
            e.printStackTrace();
            return restMessgae;
        }

        return restMessgae;
    }

    @PostMapping(path = "/dealTask")
    @ApiOperation(value = "审批任务", notes = "流程审批，通过进入下一个节点，不通过则驳回")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskId", value = "任务ID", dataType = "String", paramType = "query", example = ""),
            @ApiImplicitParam(name = "flag", value = "通过/驳回", dataType = "int", paramType = "query", example = "1"),
    })
    @Transactional(rollbackFor = Exception.class)
    public RestMessgae completeTask2(@RequestParam("taskId") String taskId, @RequestParam("flag") int flag) {
        RestMessgae restMessgae = new RestMessgae();
        try {
            HashMap<String, Object> variables = new HashMap<>(1);
            variables.put("flag", flag);

            // 使用任务id,获取任务对象，获取流程实例id
            Task task=taskService.createTaskQuery().taskId(taskId).singleResult();
            //利用任务对象，获取流程实例id
            String processInstancesId=task.getProcessInstanceId();
            //当前任务处理人
            String assignee = task.getAssignee();
            if ("sup_manager".equals(assignee)){
                if (flag == 1){
                    taskService.complete(taskId,variables);
                    //TODO 超级管理员/最终节点 审批之后，修改对应业务表的状态
                    boolean b = businessService.updateStatusByProcessID(processInstancesId, FlowStatus.ONE.getValue());
                    restMessgae = RestMessgae.success("超级管理员审核通过");
                }else{
                    //超级管理员驳回，可添加备注
                    taskService.addComment(taskId,processInstancesId,"remark");
                    taskService.complete(taskId,variables);
                    restMessgae = RestMessgae.success("超级管理员驳回");
                }
            }else{
                if (flag == 1){
                    variables.put("approver","sup_manager");
                    //审批任务
                    taskService.complete(taskId, variables);
                    restMessgae = RestMessgae.success();
                }else {
                    //直属领导驳回
                    //使用任务id,获取实例对象，进而获取用户ID
                    ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processInstancesId).singleResult();
                    String startUserId = pi.getStartUserId();
                    System.out.println("startUserId: "+startUserId);
                    //TODO 驳回给申请流程的用户
                    variables.put("approver",startUserId);
                    //添加备注 String taskId, String processInstance, String message
                    taskService.addComment(taskId,processInstancesId,"备注信息");
                    //审批任务
                    taskService.complete(taskId, variables);
                    //TODO 修改业务表状态
                    boolean b = businessService.updateStatusByProcessID(processInstancesId, FlowStatus.TWO.getValue());
                    restMessgae = RestMessgae.success();
                }
            }
        } catch (Exception e) {
            restMessgae = RestMessgae.fail("提交失败", e.getMessage());
            e.printStackTrace();
            return restMessgae;
        }
        return restMessgae;
    }
}

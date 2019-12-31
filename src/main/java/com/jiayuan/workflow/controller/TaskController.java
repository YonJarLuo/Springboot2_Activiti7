package com.jiayuan.workflow.controller;

import com.jiayuan.workflow.controller.vo.CompleteTask;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

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
            //TODO 支持分页 特别注意 分页的第一个参数不是页码，是表示从第几行开始！！！ 和mysql的分页是不同的；每页展示10条：listPage(10*pageNum - 10,pageSize)
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

            //TODO 还可以查询已办理的任务
            /*int size = historyService.createHistoricActivityInstanceQuery().taskAssignee(assignee).finished().list().size();
            resultMap.put("total",total+size);
            List<HistoricActivityInstance> historicActivityInstances = historyService.createHistoricActivityInstanceQuery().taskAssignee(assignee).finished().listPage(pageNum - 1, pageSize);
            if (historicActivityInstances != null){
                for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
                    TaskVo taskVo = new TaskVo();
                    taskVo.setTaskID(historicActivityInstance.getTaskId());
                    Business business = businessService.selectByProcessID(historicActivityInstance.getProcessInstanceId());
                    if (business != null){
                        BeanUtils.copyProperties(business,taskVo);
                    }
                    taskVos.add(taskVo);
                }
            }*/
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
                    //获取下一个节点审批人
                    Task task2 =taskService.createTaskQuery().processInstanceId(processInstancesId).singleResult();
                    String assignee2 = task2.getAssignee();
                    System.out.println(assignee2);
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

    @PostMapping(path = "/completeTask")
    @ApiOperation(value = "正确的审批任务方式", notes = "管理员流程审批，通过进入下一个节点，不通过则驳回")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "completeTask", value = "条件接收对象", dataType = "CompleteTask", paramType = "body",required = true)
    })
    public RestMessgae completeTask(@RequestBody CompleteTask completeTask) {
        //定义接收的属性
        String remark = completeTask.getRemark();
        String taskId=completeTask.getTaskId();
        Integer flag=completeTask.getFlag();
        if (remark != null && remark.length() > 85){
            return RestMessgae.fail("备注不符合规则");
        }

        HashMap<String, Object> variables = new HashMap<>(1);
        try {
            variables.put("flag", flag);
            // 使用任务id,获取任务对象，进而获取流程实例id
            Task task=taskService.createTaskQuery().taskId(taskId).singleResult();
            //利用任务对象，获取流程实例id
            String processInstancesId=task.getProcessInstanceId();
            //当前任务处理人
            String assignee = task.getAssignee();
            String userId = completeTask.getUserId();
            ProcessInstance pri = runtimeService.createProcessInstanceQuery().processInstanceId(processInstancesId).singleResult();
            String startUserId = pri.getStartUserId();
            if (userId.equals(startUserId)){
                return RestMessgae.fail("无法审批自己提的流程");
            }
            if (!userId.equals(assignee)){
                return RestMessgae.fail("无法操作他人待审批！");
            }

            if (flag == 1){
                //通过
                taskService.addComment(taskId,processInstancesId,remark);
                taskService.complete(taskId,variables);
                ProcessInstance pri2 = runtimeService.createProcessInstanceQuery().processInstanceId(processInstancesId).singleResult();
                if (pri2 == null) {
                    //流程结束
                    String presentBy = null;
                    businessService.updateStatusByProcessID2(processInstancesId, FlowStatus.ONE.getValue(), new Date(),presentBy);
                }else {
                    //中间审批人通过时，流程未结束，修改业务表
                    Task task2 =taskService.createTaskQuery().processInstanceId(processInstancesId).singleResult();
                    String nextAssignee = task2.getAssignee();
                    businessService.updateStatusByProcessID2(processInstancesId, FlowStatus.ZERO.getValue(), new Date(),nextAssignee);
                }
            }else {
                taskService.addComment(taskId,processInstancesId,remark);
                taskService.complete(taskId,variables);
                Task task2 =taskService.createTaskQuery().processInstanceId(processInstancesId).singleResult();
                String nextAssignee = task2.getAssignee();
                businessService.updateStatusByProcessID2(processInstancesId,FlowStatus.TWO.getValue(),new Date(),nextAssignee);
            }
        } catch (Exception e) {
            return RestMessgae.fail("发生异常",e.getMessage());
        }
        return RestMessgae.success();
    }
}

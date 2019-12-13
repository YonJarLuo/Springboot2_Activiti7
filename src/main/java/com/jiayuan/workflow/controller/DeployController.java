package com.jiayuan.workflow.controller;

import com.jiayuan.workflow.utils.RestMessgae;
import io.swagger.annotations.*;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author luoyj
 * @date 2019/11/26.
 */
@RestController
@Api(tags="部署流程、删除流程")
public class DeployController {

    private final RepositoryService repositoryService;

    public DeployController(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    @PostMapping(path = "deploy")
    @ApiOperation(value = "根据bpmnName部署流程",notes = "根据bpmnName部署流程")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bpmnName",value = "设计的流程图名称",dataType = "String",paramType = "query",example = "flow")
    })
    public RestMessgae deploy(@RequestParam("bpmnName") String bpmnName){

        RestMessgae restMessgae = new RestMessgae();
        //创建一个部署对象
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().name("申请资源流程");
        Deployment deployment = null;
        try {
            deployment = deploymentBuilder.addClasspathResource("processes/"+ bpmnName +".bpmn").deploy();
            if (deployment != null) {
                Map<String, String> result = new HashMap<>(2);
                result.put("deployID", deployment.getId());
                result.put("deployName", deployment.getName());
                restMessgae = RestMessgae.success("部署成功", result);
            }
        } catch (Exception e) {
            restMessgae = RestMessgae.fail("部署失败", e.getMessage());
            e.printStackTrace();
        }

        return restMessgae;
    }

    @DeleteMapping(path = "deleteProcess")
    @ApiOperation(value = "根据部署ID删除流程",notes = "根据部署ID删除流程")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deploymentId",value = "部署ID",dataType = "String",paramType = "query")
    })
    public RestMessgae deleteProcess(@RequestParam("deploymentId") String deploymentId){
        RestMessgae restMessgae = new RestMessgae();
        /**不带级联的删除：只能删除没有启动的流程，如果流程启动，就会抛出异常*/
        try {
//            repositoryService.deleteDeployment(deploymentId);
            //级联删除：不管流程是否启动，都能可以删除
            repositoryService.deleteDeployment(deploymentId, true);
            restMessgae = RestMessgae.success("删除成功", null);
        } catch (Exception e) {
            restMessgae = RestMessgae.fail("删除失败", e.getMessage());
            e.printStackTrace();
        }
        return  restMessgae;
    }
}

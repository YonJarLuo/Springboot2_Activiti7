package com.jiayuan.workflow.controller.vo;

import lombok.Data;

import java.util.List;

/**
 * @version 1.0
 * @author: zhouhongshuo
 * @date: 2019/12/16 20:08
 */
@Data
public class CompleteTask {
    String taskId;
    List<String> taskIds;
    Integer flag;
    String userId;
    String remark;
}

package com.jiayuan.workflow.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author luoyj
 * @date 2019/11/29.
 */
@Data
@TableName("WORKFLOW_BUSINESS")
@ApiModel
public class Business extends BaseEntity implements Serializable {
    //uuid
    @ApiModelProperty("唯一业务ID")
    @TableId(value = "BUSINESS_ID")
    private String businessID;

    @ApiModelProperty("流程申请名称")
    @TableField(value = "TITLE")
    private String title;

    //功能类型
    @ApiModelProperty("功能类型")
    @TableField(value = "BUSINESS_TYPE")
    private Integer businessType;

    @ApiModelProperty("申请功能ID列表，多个使用逗号分隔")
    @TableField(value = "FUNCTION_IDS")
    private String functionIDs;

    @ApiModelProperty("申请原因")
    @TableField(value = "REASON")
    private String reason;

    @ApiModelProperty("流程实例ID")
    @TableField(value = "PROCESS_ID")
    private String processID;

    @ApiModelProperty("流程申请用户ID")
    @TableField(value = "USER_ID")
    private String userID;

    @ApiModelProperty("流程申请状态,0审核中,1通过,2不通过")
    @TableField(value = "STATUS")
    private Integer status;
}

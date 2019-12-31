package com.jiayuan.workflow.utils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author luoyj
 * @date 2019/11/26.
 */
@ApiModel(description = "返回响应数据")
@Data
public class RestMessgae {

    @ApiModelProperty(value = "错误信息")
    private String message;
    @ApiModelProperty(value = "状态码")
    private String code;
    @ApiModelProperty(value = "返回的数据")
    private Object data;

    public static  RestMessgae success(String message, Object data){
        RestMessgae restMessgae = new RestMessgae();
        restMessgae.setCode("200");
        restMessgae.setMessage(message);
        restMessgae.setData(data);
        return restMessgae;
    }

    public static  RestMessgae success(String message){
        RestMessgae restMessgae = new RestMessgae();
        restMessgae.setCode("200");
        restMessgae.setMessage(message);
        return restMessgae;
    }

    public static  RestMessgae success(){
        RestMessgae restMessgae = new RestMessgae();
        restMessgae.setCode("200");
        return restMessgae;
    }

    public static  RestMessgae fail(String message, Object data){
        RestMessgae restMessgae = new RestMessgae();
        restMessgae.setCode("500");
        restMessgae.setMessage(message);
        restMessgae.setData(data);
        return restMessgae;
    }

    public static  RestMessgae fail(String message){
        RestMessgae restMessgae = new RestMessgae();
        restMessgae.setCode("500");
        restMessgae.setMessage(message);
        return restMessgae;
    }

}
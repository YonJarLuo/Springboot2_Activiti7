package com.jiayuan.workflow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiayuan.workflow.entity.Business;

import java.util.Date;

/**
 * @author luoyj
 * @date 2019/12/2.
 */
public interface BusinessService extends IService<Business> {
    Business selectByProcessID(String processID);
    boolean updateStatusByProcessID(String processID,int status);

    /**
     * @param processID 流程实例ID
     * @param status    流程状态
     * @param updateTime    更新时间
     * @param presentBy     当前处理人，任务结束的时候为null
     * @return
     */
    boolean updateStatusByProcessID2(String processID, int status,Date updateTime,String presentBy);
}

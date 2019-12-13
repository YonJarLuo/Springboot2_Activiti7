package com.jiayuan.workflow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiayuan.workflow.entity.Business;

/**
 * @author luoyj
 * @date 2019/12/2.
 */
public interface BusinessService extends IService<Business> {
    Business selectByProcessID(String processID);
    boolean updateStatusByProcessID(String processID, int status);
}

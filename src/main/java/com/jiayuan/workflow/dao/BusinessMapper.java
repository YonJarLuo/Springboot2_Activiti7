package com.jiayuan.workflow.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiayuan.workflow.entity.Business;
import org.apache.ibatis.annotations.Param;

/**
 * @author luoyj
 * @date 2019/12/2.
 */
public interface BusinessMapper extends BaseMapper<Business> {
    boolean updateStatusByProcessID(@Param("processID") String processID, @Param("status") int status);
}

package com.jiayuan.workflow.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiayuan.workflow.entity.Business;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * @author luoyj
 * @date 2019/12/2.
 */
public interface BusinessMapper extends BaseMapper<Business> {
    boolean updateStatusByProcessID(@Param("processID") String processID, @Param("status") int status);
    boolean updateStatusByProcessID2(@Param("processID") String processID, @Param("status") int status, @Param("update") Date update, @Param("presentBy") String presentBy);
}

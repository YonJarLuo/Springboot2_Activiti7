package com.jiayuan.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiayuan.workflow.dao.BusinessMapper;
import com.jiayuan.workflow.entity.Business;
import com.jiayuan.workflow.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author luoyj
 * @date 2019/12/2.
 */
@Service
public class BusinessServiceImpl extends ServiceImpl<BusinessMapper, Business> implements BusinessService {

    @Autowired
    private BusinessMapper businessDao;

    @Override
    public Business selectByProcessID(String processID){
        //创建查询构造器
        QueryWrapper qw = new QueryWrapper();
        qw.eq("PROCESS_ID",processID);
        return this.baseMapper.selectOne(qw);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatusByProcessID(String processID,int status) {
        return businessDao.updateStatusByProcessID(processID,status);
    }

}

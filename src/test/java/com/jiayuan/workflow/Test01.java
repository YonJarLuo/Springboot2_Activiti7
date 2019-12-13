package com.jiayuan.workflow;

import com.jiayuan.workflow.entity.Business;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author luoyj
 * @date 2019/11/29.
 */
public class Test01 {
    @Test
    public void test01(){
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String format = simpleDateFormat.format(date);
        System.out.println(format);
        System.out.println(date);
    }

    @Test
    public void test02(){
        Business business = new Business();
        business.setTitle("广州");
        System.out.println(business.toString());
    }
}

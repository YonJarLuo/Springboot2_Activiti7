package com.jiayuan.workflow.enums;

/**
 * @author luoyj
 * @date 2019/12/5.
 */
public enum FlowStatus {
    ZERO("审核中",0),ONE("已通过",1),TWO("未通过",2);
    private int value;
    private String desc;


    FlowStatus(String desc, int value) {
        this.desc = desc;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

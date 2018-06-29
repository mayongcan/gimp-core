package com.gimplatform.core.annotation;

public enum LogConfOperateType {
    NONE(""),
    GET("1"), 
    ADD("2"), 
    EDIT("3"), 
    DELETE("4");
    
    private final String value;

    LogConfOperateType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

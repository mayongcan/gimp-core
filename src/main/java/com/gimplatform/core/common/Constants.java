package com.gimplatform.core.common;

/**
 * 全局常量
 * @author zzd
 */
public interface Constants {

    public static final String IS_VALID_VALID = "Y";
    public static final String IS_VALID_INVALID = "N";

    // ehcache相关全局常量
    public static final String CACHE_REDIS_KEY_DICT = "GIMP:DICT";
    public static final String CACHE_REDIS_KEY_DISTRICT = "GIMP:DISTRICT";
    public static final String CACHE_REDIS_KEY_FUNC = "GIMP:FUNC";

    // redis相关全局常量
    public static final String CACHE_REDIS_KEY_PREFIX = "GIMP";

    // 默认树的循环次数
    public static final int DEFAULT_TREE_DEEP = 50;

    public static final String THEME_DEFAULT = "default";

    public static final String SPLIT_CHAR_0 = String.valueOf((char) 0);
    public static final String SPLIT_CHAR_1 = String.valueOf((char) 1);
    public static final String SPLIT_CHAR_2 = String.valueOf((char) 2);
    public static final String SPLIT_CHAR_3 = String.valueOf((char) 3);
    public static final String SPLIT_CHAR_4 = String.valueOf((char) 4);
    public static final String SPLIT_CHAR_5 = String.valueOf((char) 5);
    public static final String SPLIT_CHAR_6 = String.valueOf((char) 6);
    public static final String SPLIT_CHAR_7 = String.valueOf((char) 7);
    
    public static final String WORK_FLOW_KEY = "WORK_FLOW_KEY";
    public static final String WORK_FLOW_BUSINESS_KEY = "WORK_FLOW_BUSINESS_KEY";
}

package com.gimplatform.core.utils;

import java.util.List;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * restful接口通用返回码
 * @author zzd
 */
public class RestfulRetUtils {

    private static final String RET_CODE = "RetCode";
    private static final String RET_MSG = "RetMsg";
    private static final String RET_DATA = "RetData";
    private static final String RET_ROWS = "rows";
    private static final String RET_TOTAL = "total";

    private static final String CODE_OK = "000000";
    private static final String CODE_NO_USER = "10010";
    private static final String CODE_NO_CONTENT = "000001";
    private static final String CODE_NO_PARAMS = "000002";

    private static final String MESSAGE_NO_USER = "缺少用户对象";
    private static final String MESSAGE_NO_CONTENT = "获取内容为空";
    private static final String MESSAGE_NO_PARAMS = "传输参数有误";

    /**
     * 获取调用成功json
     * @return
     */
    public static JSONObject getRetSuccess() {
        JSONObject json = new JSONObject();
        json.put(RET_CODE, CODE_OK);
        json.put(RET_MSG, "操作成功");
        return json;
    }

    /**
     * 获取调用成功json
     * @return
     */
    public static JSONObject getRetSuccess(String msg) {
        JSONObject json = new JSONObject();
        json.put(RET_CODE, CODE_OK);
        json.put(RET_DATA, msg);
        return json;
    }

    /**
     * 获取调用成功json
     * @param jsonArray
     * @return
     */
    public static JSONObject getRetSuccess(JSONArray jsonArray) {
        JSONObject json = new JSONObject();
        if (jsonArray != null) {
            json.put(RET_CODE, CODE_OK);
            json.put(RET_DATA, jsonArray);
        } else {
            json.put(RET_CODE, CODE_NO_CONTENT);
            json.put(RET_MSG, MESSAGE_NO_CONTENT);
        }
        return json;
    }

    /**
     * 获取调用成功json
     * @param jsonArray
     * @return
     */
    public static JSONObject getRetSuccess(Object obj) {
        JSONObject json = new JSONObject();
        JSON.DEFFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
        if (obj != null) {
            json.put(RET_CODE, CODE_OK);
            // json.put(RET_DATA, obj);
            json.put(RET_DATA, JSON.parse(JSON.toJSONString(obj, SerializerFeature.WriteDateUseDateFormat)));
        } else {
            json.put(RET_CODE, CODE_NO_CONTENT);
            json.put(RET_MSG, MESSAGE_NO_CONTENT);
        }
        return json;
    }

    /**
     * 获取调用成功json
     * @param list
     * @return
     */
    public static JSONObject getRetSuccess(List<?> list) {
        JSONObject json = new JSONObject();
        JSON.DEFFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
        if (list != null) {
            json.put(RET_CODE, CODE_OK);
            json.put(RET_DATA, JSON.parse(JSON.toJSONString(list, SerializerFeature.WriteDateUseDateFormat)));
        } else {
            json.put(RET_CODE, CODE_NO_CONTENT);
            json.put(RET_MSG, MESSAGE_NO_CONTENT);
        }
        return json;
    }

    /**
     * 获取调用成功json(返回分页数据)
     * @param list
     * @param total
     * @return
     */
    public static JSONObject getRetSuccessWithPage(List<?> list, long total) {
        JSONObject json = new JSONObject();
        JSON.DEFFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
        if (list != null) {
            json.put(RET_CODE, CODE_OK);
            json.put(RET_ROWS, JSON.parse(JSON.toJSONString(list, SerializerFeature.WriteDateUseDateFormat)));
            json.put(RET_TOTAL, total);
        } else {
            json.put(RET_CODE, CODE_NO_CONTENT);
            json.put(RET_MSG, MESSAGE_NO_CONTENT);
        }
        return json;
    }

    /**
     * 获取失败返回信息
     * @param code
     * @param msg
     * @return
     */
    public static JSONObject getErrorMsg(String code, String msg) {
        JSONObject json = new JSONObject();
        json.put(RET_CODE, code);
        json.put(RET_MSG, msg);
        return json;
    }

    /**
     * 获取失败返回信息(错误参数)
     * @param code
     * @param msg
     * @return
     */
    public static JSONObject getErrorParams() {
        JSONObject json = new JSONObject();
        json.put(RET_CODE, CODE_NO_PARAMS);
        json.put(RET_MSG, MESSAGE_NO_PARAMS);
        return json;
    }

    /**
     * 获取用户失败
     * @return
     */
    public static JSONObject getErrorNoUser() {
        JSONObject json = new JSONObject();
        json.put(RET_CODE, CODE_NO_USER);
        json.put(RET_MSG, MESSAGE_NO_USER);
        return json;
    }

    /**
     * 判断json是否返回成功
     * @param json
     * @return
     */
    public static boolean isRetSuccess(JSONObject json) {
        if (json != null && CODE_OK.equals(json.getString(RET_CODE))) {
            return true;
        } else
            return false;
    }
}

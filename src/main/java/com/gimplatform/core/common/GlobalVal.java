package com.gimplatform.core.common;

import java.util.ArrayList;
import java.util.List;

import com.gimplatform.core.entity.FuncInfo;

/**
 * 全局变量
 * @author zzd
 */
public class GlobalVal {

    // 程序启动后将权限列表加载到内存中
    public static List<FuncInfo> funcInfoList = new ArrayList<FuncInfo>();
}

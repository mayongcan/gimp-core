package com.gimplatform.core.common;

public interface SchedulerConstants {

    // 任务类
    public static final String JOB_CLASS_PROC = "com.gimplatform.core.scheduler.ProcJob";
    public static final String JOB_CLASS_RESTFUL = "com.gimplatform.core.scheduler.RestfulJob";

    public static final String MYCAT_GBL_NOTESQL = "/*!mycat:sql=select 1 from sys_user_info where 1=0 */";

    public static final String DB_DRIVER_ORACLE = "oracle.jdbc.driver.OracleDriver";
    public static final String DB_DRIVER_SQLSERVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    public static final String DB_DRIVER_MYSQL = "com.mysql.jdbc.Driver";

    public static final String DB_TYPE_ORACLE = "ORACLE";
    public static final String DB_TYPE_SQLSERVER = "SQL Server";
    public static final String DB_TYPE_MYSQL = "MySQL";
    public static final String DB_TYPE_MYCAT = "Mycat";

    public static final String PARAM_TYPE_INT = ",int,integer,";
    public static final String PARAM_TYPE_INT_DESC = "整数类型";
    public static final String PARAM_TYPE_FLOAT = ",numeric,number,float,";
    public static final String PARAM_TYPE_FLOAT_DESC = "小数类型";
    public static final String PARAM_TYPE_STRING = ",varchar2,varchar,nchar,string,";
    public static final String PARAM_TYPE_STRING_DESC = "字符类型";
    public static final String PARAM_TYPE_DATE = ",date,datetime,";
    public static final String PARAM_TYPE_DATE_DESC = "日期类型";

    public static final String JOB_TYPE_PROC = "Proc";
    public static final String JOB_TYPE_PROC_DESC = "存储过程";
    public static final String JOB_TYPE_RESTFUL = "Restful";
    public static final String JOB_TYPE_RESTFUL_DESC = "Restful接口";
    public static final String JOB_TYPE_CUSTOM = "Custom";
    public static final String JOB_TYPE_CUSTOM_DESC = "自定义";

}

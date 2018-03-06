package com.gimplatform.core.generator.provider.db.sql.model;

import com.gimplatform.core.generator.provider.db.table.model.Column;
import com.gimplatform.core.generator.utils.type.JavaPrimitiveTypeMapping;
import com.gimplatform.core.utils.StringUtils;

public class SqlParameter extends Column {

    String parameterClass;

    String paramName;

    boolean isListParam = false;

    public SqlParameter() {
    }

    public SqlParameter(Column param) {
        super(param);
    }

    public SqlParameter(SqlParameter param) {
        super(param);
        this.isListParam = param.isListParam;
        this.paramName = param.paramName;
    }

    public String getParameterClass() {
        if (StringUtils.isNotBlank(parameterClass))
            return parameterClass;
        return getSimpleJavaType();
    }

    public String getPrimitiveParameterClass() {
        if (StringUtils.isNotBlank(parameterClass))
            return JavaPrimitiveTypeMapping.getPrimitiveType(parameterClass);
        return getPrimitiveJavaType();
    }

    public void setParameterClass(String parameterClass) {
        this.parameterClass = parameterClass;
    }

    public String getPreferredParameterJavaType() {
        return toListParam(getParameterClass());
    }

    public String getPreferredPrimitiveParameterJavaType() {
        return toListParam(getPrimitiveParameterClass());
    }

    private String toListParam(String parameterClassName) {
        if (isListParam) {
            if (parameterClassName.indexOf("[]") >= 0) {
                return parameterClassName;
            }
            if (parameterClassName.indexOf("List") >= 0) {
                return parameterClassName;
            }
            if (parameterClassName.indexOf("Set") >= 0) {
                return parameterClassName;
            }
            return "java.util.List<" + parameterClassName + ">";
        } else {
            return parameterClassName;
        }
    }

    /**
     * 参数名称
     */
    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    /**
     * 是否是列表参数,主要是in语句,如 username in (:usernames) 则为true, username = :username则false
     * @return
     */
    public boolean isListParam() {
        return isListParam;
    }

    public void setListParam(boolean isListParam) {
        this.isListParam = isListParam;
    }

    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null)
            return false;
        if (obj instanceof SqlParameter) {
            SqlParameter other = (SqlParameter) obj;
            return paramName.equals(other.getParamName());
        } else {
            return false;
        }
    }

    public int hashCode() {
        return paramName.hashCode();
    }

    public String toString() {
        return "paramName:" + paramName + " preferredParameterJavaType:" + getPreferredParameterJavaType();
    }
}
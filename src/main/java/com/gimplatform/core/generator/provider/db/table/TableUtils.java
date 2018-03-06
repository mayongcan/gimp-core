package com.gimplatform.core.generator.provider.db.table;

import com.gimplatform.core.generator.GeneratorProperties;
import com.gimplatform.core.utils.StringUtils;

public class TableUtils {

    public static String firstLower(String str) {
        if (null == str) {
            return null;
        }
        str = str.trim();
        if (str.isEmpty()) {
            return "";
        }
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    public static String removeTableSqlNamePrefix(String sqlName) {
        String prefixs = GeneratorProperties.getProperty("tableRemovePrefixes", "");
        for (String prefix : prefixs.split(",")) {
            String removedPrefixSqlName = StringUtils.removePrefix(sqlName, prefix, true);
            if (!removedPrefixSqlName.equals(sqlName)) {
                return removedPrefixSqlName;
            }
        }
        return sqlName;
    }
}

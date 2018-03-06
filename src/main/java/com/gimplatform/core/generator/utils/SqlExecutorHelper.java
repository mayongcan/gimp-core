package com.gimplatform.core.generator.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlExecutorHelper {

    public static List<Map<String, Object>> queryForList(Connection conn, String sql, int limit) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sql.trim());
        ps.setMaxRows(limit);
        ps.setFetchDirection(ResultSet.FETCH_FORWARD);
        ResultSet rs = ps.executeQuery();
        return toListMap(limit, rs);
    }

    public static List<Map<String, Object>> toListMap(int limit, ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int count = 0;
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        while (rs.next()) {
            Map<String, Object> row = new HashMap<String, Object>();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                row.put(rsmd.getColumnName(i), rs.getObject(i));
            }
            list.add(row);
            count++;
            if (count >= limit) {
                break;
            }
        }
        return list;
    }

}

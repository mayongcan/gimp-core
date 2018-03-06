package com.gimplatform.core.generator.provider.db.table;

import java.io.File;
import java.sql.*;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.generator.GeneratorProperties;
import com.gimplatform.core.generator.provider.db.table.model.Column;
import com.gimplatform.core.generator.provider.db.table.model.Table;
import com.gimplatform.core.generator.utils.XMLHelper;
import com.gimplatform.core.generator.utils.XMLHelper.NodeData;
import com.gimplatform.core.utils.BeanUtils;
import com.gimplatform.core.utils.FileUtils;
import com.gimplatform.core.utils.StringUtils;

/**
 * 根据数据库表的元数据(metadata)创建Table对象
 * 
 * <pre>
 * getTable(sqlName) : 根据数据库表名,得到table对象
 * getAllTable() : 搜索数据库的所有表,并得到table对象列表
 * </pre>
 */
public class TableFactory {

    private static final Logger logger = LogManager.getLogger(TableFactory.class);

    private DbHelper dbHelper = new DbHelper();
    private Connection connection;
    private static TableFactory instance = null;

    private TableFactory() {
    }

    /**
     * 加载JDBC驱动
     */
    private void loadJdbcDriver() {
        String driver = GeneratorProperties.getRequiredProperty("jdbc.driver");
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("not found jdbc driver class:[" + driver + "]", e);
        }
    }

    public synchronized static TableFactory getInstance() {
        if (instance == null)
            instance = new TableFactory();
        return instance;
    }

    public String getCatalog() {
        return GeneratorProperties.getNullIfBlank("jdbc.catalog");
    }

    public String getSchema() {
        return GeneratorProperties.getNullIfBlank("jdbc.schema");
    }

    /**
     * 获取数据库连接
     * @return
     */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                loadJdbcDriver();
                connection = DriverManager.getConnection(GeneratorProperties.getRequiredProperty("jdbc.url"), GeneratorProperties.getRequiredProperty("jdbc.username"), GeneratorProperties.getProperty("jdbc.password"));
            }
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Table> getAllTables() {
        try {
            Connection conn = getConnection();
            return getAllTables(conn);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Table getTable(String tableName) {
        return getTable(getSchema(), tableName);
    }

    private Table getTable(String schema, String tableName) {
        return getTable(getCatalog(), schema, tableName);
    }

    private Table getTable(String catalog, String schema, String tableName) {
        Table t = null;
        try {
            t = _getTable(catalog, schema, tableName);
            if (t == null && !tableName.equals(tableName.toUpperCase())) {
                t = _getTable(catalog, schema, tableName.toUpperCase());
            }
            if (t == null && !tableName.equals(tableName.toLowerCase())) {
                t = _getTable(catalog, schema, tableName.toLowerCase());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (t == null) {
            throw new NotFoundTableException("not found table with give name:" + tableName + (dbHelper.isOracleDataBase() ? " \n databaseStructureInfo:" + getDatabaseStructureInfo() : ""));
        }
        return t;
    }

    private Table _getTable(String catalog, String schema, String tableName) throws SQLException {
        if (tableName == null || tableName.trim().length() == 0)
            throw new IllegalArgumentException("tableName must be not empty");
        catalog = StringUtils.toString(catalog, null);
        schema = StringUtils.toString(schema, null);

        Connection conn = getConnection();
        DatabaseMetaData dbMetaData = conn.getMetaData();
        ResultSet rs = dbMetaData.getTables(catalog, schema, tableName, null);
        while (rs.next()) {
            Table table = createTable(conn, rs);
            return table;
        }
        return null;
    }

    private Table createTable(Connection conn, ResultSet rs) throws SQLException {
        String realTableName = null;
        try {
            realTableName = rs.getString("TABLE_NAME");
            String tableType = rs.getString("TABLE_TYPE");
            String remarks = rs.getString("REMARKS");
            if (remarks == null && dbHelper.isOracleDataBase()) {
                remarks = getOracleTableComments(realTableName);
            }

            Table table = new Table();
            table.setSqlName(realTableName);
            table.setRemarks(remarks);

            if ("SYNONYM".equals(tableType) && dbHelper.isOracleDataBase()) {
                table.setOwnerSynonymName(getSynonymOwner(realTableName));
            }

            retriveTableColumns(table);

            table.initExportedKeys(conn.getMetaData());
            table.initImportedKeys(conn.getMetaData());
            // 获取自定义表配置信息
            BeanUtils.copyProperties(table, TableOverrideValuesProvider.getTableOverrideValues(table.getSqlName()));
            return table;
        } catch (SQLException e) {
            throw new RuntimeException("create table object error,tableName:" + realTableName, e);
        }
    }

    private List<Table> getAllTables(Connection conn) throws SQLException {
        DatabaseMetaData dbMetaData = conn.getMetaData();
        ResultSet rs = dbMetaData.getTables(getCatalog(), getSchema(), null, null);
        List<Table> tables = new ArrayList<Table>();
        while (rs.next()) {
            tables.add(createTable(conn, rs));
        }
        return tables;
    }

    private String getSynonymOwner(String synonymName) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String ret = null;
        try {
            ps = getConnection().prepareStatement("select table_owner from sys.all_synonyms where table_name=? and owner=?");
            ps.setString(1, synonymName);
            ps.setString(2, getSchema());
            rs = ps.executeQuery();
            if (rs.next()) {
                ret = rs.getString(1);
            } else {
                String databaseStructure = getDatabaseStructureInfo();
                throw new RuntimeException("Wow! Synonym " + synonymName + " not found. How can it happen? " + databaseStructure);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            String databaseStructure = getDatabaseStructureInfo();
            throw new RuntimeException("Exception in getting synonym owner " + databaseStructure);
        } finally {
            dbHelper.close(rs, ps);
        }
        return ret;
    }

    private String getDatabaseStructureInfo() {
        ResultSet schemaRs = null;
        ResultSet catalogRs = null;
        String nl = System.getProperty("line.separator");
        StringBuffer sb = new StringBuffer(nl);
        sb.append("Configured schema:").append(getSchema()).append(nl);
        sb.append("Configured catalog:").append(getCatalog()).append(nl);

        try {
            schemaRs = getMetaData().getSchemas();
            sb.append("Available schemas:").append(nl);
            while (schemaRs.next()) {
                sb.append("  ").append(schemaRs.getString("TABLE_SCHEM")).append(nl);
            }
        } catch (SQLException e2) {
            logger.error(e2.getMessage(), e2);
            sb.append("  ?? Couldn't get schemas ??").append(nl);
        } finally {
            dbHelper.close(schemaRs, null);
        }

        try {
            catalogRs = getMetaData().getCatalogs();
            sb.append("Available catalogs:").append(nl);
            while (catalogRs.next()) {
                sb.append("  ").append(catalogRs.getString("TABLE_CAT")).append(nl);
            }
        } catch (SQLException e2) {
            logger.error(e2.getMessage(), e2);
            sb.append("  ?? Couldn't get catalogs ??").append(nl);
        } finally {
            dbHelper.close(catalogRs, null);
        }
        return sb.toString();
    }

    private DatabaseMetaData getMetaData() throws SQLException {
        return getConnection().getMetaData();
    }

    private void retriveTableColumns(Table table) throws SQLException {
        List<String> primaryKeys = getTablePrimaryKeys(table);
        // get the indices and unique columns
        List<String> indices = new LinkedList<String>();
        // maps index names to a list of columns in the index
        Map<String, String> uniqueIndices = new HashMap<String, String>();
        // maps column names to the index name.
        Map<String, List<String>> uniqueColumns = new HashMap<String, List<String>>();
        ResultSet indexRs = null;

        try {
            if (table.getOwnerSynonymName() != null) {
                indexRs = getMetaData().getIndexInfo(getCatalog(), table.getOwnerSynonymName(), table.getSqlName(), false, true);
            } else {
                indexRs = getMetaData().getIndexInfo(getCatalog(), getSchema(), table.getSqlName(), false, true);
            }
            while (indexRs.next()) {
                String columnName = indexRs.getString("COLUMN_NAME");
                if (columnName != null) {
                    indices.add(columnName);
                }

                // now look for unique columns
                String indexName = indexRs.getString("INDEX_NAME");
                boolean nonUnique = indexRs.getBoolean("NON_UNIQUE");

                if (!nonUnique && columnName != null && indexName != null) {
                    List<String> l = (List<String>) uniqueColumns.get(indexName);
                    if (l == null) {
                        l = new ArrayList<String>();
                        uniqueColumns.put(indexName, l);
                    }
                    l.add(columnName);
                    uniqueIndices.put(columnName, indexName);
                }
            }
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
        } finally {
            dbHelper.close(indexRs, null);
        }

        List<Column> columns = getTableColumns(table, primaryKeys, indices, uniqueIndices, uniqueColumns);
        JSONObject json = new JSONObject();
        JSONArray jsonColumnArray = new JSONArray();
        JSONObject jsonTmp = JSONObject.parseObject(GeneratorProperties.getProperty("tableColumn", ""));
        if (jsonTmp != null)
            jsonColumnArray = jsonTmp.getJSONArray("rows");
        for (Iterator<Column> i = columns.iterator(); i.hasNext();) {
            Column column = (Column) i.next();
            // 添加额外属性
            if (jsonColumnArray != null && jsonColumnArray.size() > 0) {
                // 找到对应的字段json对象
                for (int j = 0; j < jsonColumnArray.size(); j++) {
                    JSONObject obj = jsonColumnArray.getJSONObject(j);
                    if (column.getSqlName().equalsIgnoreCase(obj.getString("columnName"))) {
                        json = obj;
                        break;
                    }
                }
            }
            column.initPageProperties(json);
            table.addColumn(column);
        }
        if (primaryKeys.size() == 0) {
            logger.warn("WARNING: The JDBC driver didn't report any primary key columns in " + table.getSqlName());
        }
    }

    private List<Column> getTableColumns(Table table, List<String> primaryKeys, List<String> indices, Map<String, String> uniqueIndices, Map<String, List<String>> uniqueColumns) throws SQLException {
        List<Column> columns = new LinkedList<Column>();
        ResultSet columnRs = getColumnsResultSet(table);

        while (columnRs.next()) {
            int sqlType = columnRs.getInt("DATA_TYPE");
            String sqlTypeName = columnRs.getString("TYPE_NAME");
            String columnName = columnRs.getString("COLUMN_NAME");
            String columnDefaultValue = columnRs.getString("COLUMN_DEF");

            String remarks = columnRs.getString("REMARKS");
            if (remarks == null && dbHelper.isOracleDataBase()) {
                remarks = getOracleColumnComments(table.getSqlName(), columnName);
            }

            boolean isNullable = (DatabaseMetaData.columnNullable == columnRs.getInt("NULLABLE"));
            int size = columnRs.getInt("COLUMN_SIZE");
            int decimalDigits = columnRs.getInt("DECIMAL_DIGITS");

            boolean isPk = primaryKeys.contains(columnName);
            boolean isIndexed = indices.contains(columnName);
            String uniqueIndex = (String) uniqueIndices.get(columnName);
            List<String> columnsInUniqueIndex = null;
            if (uniqueIndex != null) {
                columnsInUniqueIndex = (List<String>) uniqueColumns.get(uniqueIndex);
            }

            boolean isUnique = columnsInUniqueIndex != null && columnsInUniqueIndex.size() == 1;
            if (isUnique) {
                logger.info("unique column:" + columnName);
            }
            Column column = new Column(table, sqlType, sqlTypeName, columnName, size, decimalDigits, isPk, isNullable, isIndexed, isUnique, columnDefaultValue, remarks);
            // 获取自定义栏配置信息
            BeanUtils.copyProperties(column, TableOverrideValuesProvider.getColumnOverrideValues(table, column));
            columns.add(column);
        }
        columnRs.close();
        return columns;
    }

    private ResultSet getColumnsResultSet(Table table) throws SQLException {
        ResultSet columnRs = null;
        if (table.getOwnerSynonymName() != null) {
            columnRs = getMetaData().getColumns(getCatalog(), table.getOwnerSynonymName(), table.getSqlName(), null);
        } else {
            columnRs = getMetaData().getColumns(getCatalog(), getSchema(), table.getSqlName(), null);
        }
        return columnRs;
    }

    private List<String> getTablePrimaryKeys(Table table) throws SQLException {
        List<String> primaryKeys = new LinkedList<String>();
        ResultSet primaryKeyRs = null;
        if (table.getOwnerSynonymName() != null) {
            primaryKeyRs = getMetaData().getPrimaryKeys(getCatalog(), table.getOwnerSynonymName(), table.getSqlName());
        } else {
            primaryKeyRs = getMetaData().getPrimaryKeys(getCatalog(), getSchema(), table.getSqlName());
        }
        while (primaryKeyRs.next()) {
            String columnName = primaryKeyRs.getString("COLUMN_NAME");
            logger.info("primary key:" + columnName);
            primaryKeys.add(columnName);
        }
        primaryKeyRs.close();
        return primaryKeys;
    }

    private String getOracleTableComments(String table) {
        String sql = "SELECT comments FROM user_tab_comments WHERE table_name='" + table + "'";
        return dbHelper.queryForString(sql);
    }

    private String getOracleColumnComments(String table, String column) {
        String sql = "SELECT comments FROM user_col_comments WHERE table_name='" + table + "' AND column_name = '" + column + "'";
        return dbHelper.queryForString(sql);
    }

    public static class NotFoundTableException extends RuntimeException {
        private static final long serialVersionUID = 5976869128012158628L;

        public NotFoundTableException(String message) {
            super(message);
        }
    }

    /**
     * 得到表的自定义配置信息
     */
    public static class TableOverrideValuesProvider {

        private static Map<String, String> getTableOverrideValues(String tableSqlName) {
            NodeData nd = getTableConfigXmlNodeData(tableSqlName, "");
            if (nd == null) {
                return new HashMap<String, String>();
            }
            return nd == null ? new HashMap<String, String>() : nd.attributes;
        }

        private static Map<String, String> getColumnOverrideValues(Table table, Column column) {
            NodeData root = getTableConfigXmlNodeData(table.getSqlName(), column.getColumnName());
            if (root != null) {
                for (NodeData item : root.childs) {
                    if (item.nodeName.equals("column")) {
                        if (column.getSqlName().equalsIgnoreCase(item.attributes.get("sqlName"))) {
                            return item.attributes;
                        }
                    }
                }
            }
            return new HashMap<String, String>();
        }

        private static NodeData getTableConfigXmlNodeData(String tableSqlName, String columnName) {
            NodeData nd = getTableConfigXmlNodeData0(tableSqlName, columnName);
            if (nd == null) {
                nd = getTableConfigXmlNodeData0(tableSqlName.toLowerCase(), columnName);
                if (nd == null) {
                    nd = getTableConfigXmlNodeData0(tableSqlName.toUpperCase(), columnName);
                }
            }
            return nd;
        }

        private static NodeData getTableConfigXmlNodeData0(String tableSqlName, String columnName) {
            String fileName = "generator_config/table/" + tableSqlName + ".xml";
            try {
                File file = FileUtils.getFileByClassLoader(GeneratorProperties.class, fileName);
                logger.info("getTableConfigXml() load nodeData by tableSqlName:" + tableSqlName + ".xml");
                return new XMLHelper().parseXML(file);
            } catch (Exception e) {
                if (StringUtils.isBlank(columnName))
                    logger.debug("找不到自定义配置信息[" + fileName + "],使用默认属性[table=" + tableSqlName + "]");
                else
                    logger.debug("找不到自定义配置信息[" + fileName + "],使用默认属性[table=" + tableSqlName + "][clumnName=" + columnName + "]");
                return null;
            }
        }
    }

    class DbHelper {
        public void close(ResultSet rs, PreparedStatement ps, Statement... statements) {
            try {
                if (ps != null)
                    ps.close();
                if (rs != null)
                    rs.close();
                for (Statement s : statements) {
                    s.close();
                }
            } catch (Exception e) {
            }
        }

        public boolean isOracleDataBase() {
            boolean ret = false;
            try {
                ret = (getMetaData().getDatabaseProductName().toLowerCase().indexOf("oracle") != -1);
            } catch (Exception ignore) {
            }
            return ret;
        }

        public String queryForString(String sql) {
            Statement s = null;
            ResultSet rs = null;
            try {
                s = getConnection().createStatement();
                rs = s.executeQuery(sql);
                if (rs.next()) {
                    return rs.getString(1);
                }
                return null;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            } finally {
                close(rs, null, s);
            }
        }
    }
}

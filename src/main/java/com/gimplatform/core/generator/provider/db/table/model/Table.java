package com.gimplatform.core.generator.provider.db.table.model;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.generator.GeneratorProperties;
import com.gimplatform.core.generator.provider.db.table.TableFactory;
import com.gimplatform.core.generator.provider.db.table.TableUtils;
import com.gimplatform.core.utils.StringUtils;

/**
 * 用于生成代码的Table对象.对应数据库的table
 */
public class Table {

    public static final String PKTABLE_NAME = "PKTABLE_NAME";
    public static final String PKCOLUMN_NAME = "PKCOLUMN_NAME";
    public static final String FKTABLE_NAME = "FKTABLE_NAME";
    public static final String FKCOLUMN_NAME = "FKCOLUMN_NAME";
    public static final String KEY_SEQ = "KEY_SEQ";

    String catalog = TableFactory.getInstance().getCatalog();
    String schema = TableFactory.getInstance().getSchema();

    String sqlName;
    String remarks;
    String className;
    String classNameLower;
    String classNameDash;
    private String ownerSynonymName = null;
    LinkedHashSet<Column> columns = new LinkedHashSet<Column>();

    private String tableAlias;
    private ForeignKeys exportedKeys;
    private ForeignKeys importedKeys;

    public Table() {
    }

    public Table(Table t) {
        setSqlName(t.getSqlName());
        this.remarks = t.getRemarks();
        this.className = t.getSqlName();
        this.classNameLower = TableUtils.firstLower(className);
        this.classNameDash = sqlName.toLowerCase().replace("_", "-");
        this.ownerSynonymName = t.getOwnerSynonymName();
        this.columns = t.getColumns();
        this.tableAlias = t.getTableAlias();
        this.exportedKeys = t.exportedKeys;
        this.importedKeys = t.importedKeys;
    }

    public void initImportedKeys(DatabaseMetaData dbmd) throws java.sql.SQLException {
        ResultSet fkeys = dbmd.getImportedKeys(catalog, schema, this.sqlName);
        while (fkeys.next()) {
            String pktable = fkeys.getString(PKTABLE_NAME);
            String pkcol = fkeys.getString(PKCOLUMN_NAME);
            // String fktable = fkeys.getString(FKTABLE_NAME);
            String fkcol = fkeys.getString(FKCOLUMN_NAME);
            String seq = fkeys.getString(KEY_SEQ);
            Integer iseq = new Integer(seq);
            getImportedKeys().addForeignKey(pktable, pkcol, fkcol, iseq);
        }
        fkeys.close();
    }

    public void initExportedKeys(DatabaseMetaData dbmd) throws java.sql.SQLException {
        ResultSet fkeys = dbmd.getExportedKeys(catalog, schema, this.sqlName);
        while (fkeys.next()) {
            // String pktable = fkeys.getString(PKTABLE_NAME);
            String pkcol = fkeys.getString(PKCOLUMN_NAME);
            String fktable = fkeys.getString(FKTABLE_NAME);
            String fkcol = fkeys.getString(FKCOLUMN_NAME);
            String seq = fkeys.getString(KEY_SEQ);
            Integer iseq = new Integer(seq);
            getExportedKeys().addForeignKey(fktable, fkcol, pkcol, iseq);
        }
        fkeys.close();
    }

    /**
     * 数据库中表的表名称,其它属性很多都是根据此属性派生
     * @return
     */
    public String getSqlName() {
        return sqlName;
    }

    public void setSqlName(String sqlName) {
        this.sqlName = sqlName;
    }

    /**
     * 数据库中表的表备注
     * @return
     */
    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * 根据sqlName得到的类名称，示例值: UserInfo
     * @return
     */
    public String getClassName() {
        if (StringUtils.isBlank(className)) {
            String removedPrefixSqlName = TableUtils.removeTableSqlNamePrefix(sqlName);
            className = StringUtils.makeAllWordFirstLetterUpperCase(StringUtils.toUnderscoreName(removedPrefixSqlName));
        }
        return className;
    }

    public void setClassName(String customClassName) {
        this.className = customClassName;
        setClassNameLower(className);
    }

    public String getClassNameLower() {
        return TableUtils.firstLower(getClassName());
    }

    public void setClassNameLower(String classNameLower) {
        this.classNameLower = classNameLower;
    }

    public String getClassNameDash() {
        return sqlName.toLowerCase().replace("_", "-");
    }

    public void setClassNameDash(String classNameDash) {
        this.classNameDash = classNameDash;
    }

    public String getOwnerSynonymName() {
        return ownerSynonymName;
    }

    public void setOwnerSynonymName(String ownerSynonymName) {
        this.ownerSynonymName = ownerSynonymName;
    }

    public LinkedHashSet<Column> getColumns() {
        return columns;
    }

    public void setColumns(LinkedHashSet<Column> columns) {
        this.columns = columns;
    }

    /**
     * 数据库中表的别名，等价于: getRemarks().isEmpty() ? getClassName() : getRemarks()
     * @return
     */
    public String getTableAlias() {
        if (StringUtils.isNotBlank(tableAlias))
            return tableAlias;
        return StringUtils.join(StringUtils.toString(getRemarks(), getClassName()).split("\t\n\r\f"), " ");
    }

    public void setTableAlias(String v) {
        this.tableAlias = v;
    }

    public ForeignKeys getExportedKeys() {
        if (exportedKeys == null) {
            exportedKeys = new ForeignKeys(this);
        }
        return exportedKeys;
    }

    public ForeignKeys getImportedKeys() {
        if (importedKeys == null) {
            importedKeys = new ForeignKeys(this);
        }
        return importedKeys;
    }

    public void addColumn(Column column) {
        columns.add(column);
    }

    /**
     * 得到主键总数
     * @return
     */
    public int getPkCount() {
        int pkCount = 0;
        for (Column c : columns) {
            if (c.isPk()) {
                pkCount++;
            }
        }
        return pkCount;
    }

    /**
     * 得到是主键的全部column
     * @return
     */
    public List<Column> getPkColumns() {
        List<Column> results = new ArrayList<Column>();
        for (Column c : getColumns()) {
            if (c.isPk())
                results.add(c);
        }
        return results;
    }

    /**
     * 得到单主键，等价于getPkColumns().get(0)
     * @return
     */
    public Column getPkColumn() {
        if (getPkColumns().isEmpty()) {
            throw new IllegalStateException("not found primary key on table:" + getSqlName());
        }
        return getPkColumns().get(0);
    }

    /**
     * 得到不是主键的全部column
     * @return
     */
    public List<Column> getNotPkColumns() {
        List<Column> results = new ArrayList<Column>();
        for (Column c : getColumns()) {
            if (!c.isPk())
                results.add(c);
        }
        return results;
    }

    public List<Column> getEnumColumns() {
        List<Column> results = new ArrayList<Column>();
        for (Column c : getColumns()) {
            if (!c.isEnumColumn())
                results.add(c);
        }
        return results;
    }

    public Column getColumnByName(String name) {
        Column c = getColumnBySqlName(name);
        if (c == null) {
            c = getColumnBySqlName(StringUtils.toUnderscoreName(name));
        }
        return c;
    }

    public Column getColumnBySqlName(String sqlName) {
        for (Column c : getColumns()) {
            if (c.getSqlName().equalsIgnoreCase(sqlName)) {
                return c;
            }
        }
        return null;
    }

    public Column getRequiredColumnBySqlName(String sqlName) {
        if (getColumnBySqlName(sqlName) == null) {
            throw new IllegalArgumentException("not found column with sqlName:" + sqlName + " on table:" + getSqlName());
        }
        return getColumnBySqlName(sqlName);
    }

    /**
     * 忽略过滤掉某些关键字的列,关键字不区分大小写,以逗号分隔
     * @param ignoreKeywords
     * @return
     */
    public List<Column> getIgnoreKeywordsColumns(String ignoreKeywords) {
        List<Column> results = new ArrayList<Column>();
        for (Column c : getColumns()) {
            String sqlname = c.getSqlName().toLowerCase();
            if (StringUtils.contains(sqlname, ignoreKeywords.split(","))) {
                continue;
            }
            results.add(c);
        }
        return results;
    }

    /**
     * 等价于getClassName().toLowerCase()
     * @return
     */
    public String getClassNameLowerCase() {
        return getClassName().toLowerCase();
    }

    /**
     * 返回值为getClassName()的第一个字母小写,如className=UserInfo,则ClassNameFirstLower=userInfo
     * @return
     */
    public String getClassNameFirstLower() {
        return StringUtils.changeFirstCharacterCase(getClassName(), false);
    }

    /**
     * 得到用下划线分隔的类名称，如className=UserInfo,则underscoreName=user_info
     * @return
     */
    public String getUnderscoreName() {
        return StringUtils.toUnderscoreName(getClassName()).toLowerCase();
    }

    /**
     * 根据getClassName()计算而来,用于得到常量名,如className=UserInfo,则constantName=USER_INFO
     * @return
     */
    public String getConstantName() {
        return StringUtils.toUnderscoreName(getClassName()).toUpperCase();
    }

    public String getShortName() {
        return TableUtils.removeTableSqlNamePrefix(sqlName);
    }

    /**
     * 判断是否存在IS_VALID字段
     * @return
     */
    public boolean getHasIsValid() {
        boolean ret = false;
        for (Column column : columns) {
            if ("IS_VALID".equals(column.getSqlName())) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    public boolean getHasCreateBy() {
        boolean ret = false;
        for (Column column : columns) {
            if ("CREATE_BY".equals(column.getSqlName())) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    public boolean getHasCreateDate() {
        boolean ret = false;
        for (Column column : columns) {
            if ("CREATE_DATE".equals(column.getSqlName())) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    public boolean getHasModifyBy() {
        boolean ret = false;
        for (Column column : columns) {
            if ("MODIFY_BY".equals(column.getSqlName())) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    public boolean getHasModifyDate() {
        boolean ret = false;
        for (Column column : columns) {
            if ("MODIFY_DATE".equals(column.getSqlName())) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    public boolean getHasSearch() {
        boolean ret = false;
        for (Column column : columns) {
            if (column.isSearch()) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    public String getAjaxUploadType() {
        String retType = "0";
        for (Column column : columns) {
            if (column.isEdit() && "5".equals(column.getEditType())) {
                retType = "1";
                break;
            }
            if (column.isEdit() && "6".equals(column.getEditType())) {
                retType = "2";
                break;
            }
        }
        return retType;
    }

    /**
     * 计算编辑框高度
     * @return
     */
    public String getEditBoxHeight() {
        int uploadCnt = 0, textArea = 0;
        for (Column column : columns) {
            if (column.isEdit() && "5".equals(column.getEditType())) {
                uploadCnt++;
            }
            if (column.isEdit() && "6".equals(column.getEditType())) {
                uploadCnt++;
            }
            if (column.isEdit() && "8".equals(column.getEditType())) {
                textArea++;
            }
        }
        int height = (columns.size() - uploadCnt - textArea) / 2 * 60 + uploadCnt * 60 + textArea * 100 + 100;
        return height + "px";
    }

    public boolean getHasEditTextArea() {
        int textArea = 0;
        for (Column column : columns) {
            if (column.isEdit() && "8".equals(column.getEditType())) {
                textArea++;
            }
        }
        if (textArea > 0)
            return true;
        else
            return false;
    }

    public boolean getHasParentId() {
        boolean ret = false;
        for (Column column : columns) {
            if ("PARENT_ID".equals(column.getSqlName())) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    /**
     * 获取树列表显示模式时的节点名称,格式：UserInfo
     * @return
     */
    public String getTreeNodeName() {
        JSONObject jsonTmp = JSONObject.parseObject(GeneratorProperties.getProperty("treeInfo", ""));
        if (jsonTmp == null)
            return "";
        String colunmName = jsonTmp.getString("treeNodeName");
        if (StringUtils.isBlank(colunmName))
            return "";
        for (Column column : columns) {
            if (column.getSqlName().equalsIgnoreCase(colunmName)) {
                return column.getColumnName();
            }
        }
        return "";
    }
    

    public String getTreeNodeNameFirstLower() {
        return TableUtils.firstLower(getTreeNodeName());
    }

    public String getTreeNodeOrder() {
        JSONObject jsonTmp = JSONObject.parseObject(GeneratorProperties.getProperty("treeInfo", ""));
        if (jsonTmp == null)
            return "";
        String treeNodeOrder = jsonTmp.getString("treeNodeOrder");
        if (StringUtils.isBlank(treeNodeOrder))
            return "";
        else
            return ", " + treeNodeOrder;
    }

    public String toString() {
        return "Database Table:" + getSqlName() + " to ClassName:" + getClassName();
    }
}

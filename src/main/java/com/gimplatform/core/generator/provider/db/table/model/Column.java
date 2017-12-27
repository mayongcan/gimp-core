package com.gimplatform.core.generator.provider.db.table.model;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.generator.GeneratorProperties;
import com.gimplatform.core.generator.GeneratorTestData;
import com.gimplatform.core.generator.provider.db.table.ColumnUtils;
import com.gimplatform.core.generator.provider.db.table.ColumnUtils.EnumMetaDada;
import com.gimplatform.core.generator.provider.db.table.model.ForeignKey.ReferenceKey;
import com.gimplatform.core.generator.utils.type.DatabaseDataTypesUtils;
import com.gimplatform.core.generator.utils.type.JavaPrimitiveTypeMapping;
import com.gimplatform.core.generator.utils.type.JdbcType;
import com.gimplatform.core.utils.StringUtils;

/**
 * 用于生成代码的Column对象.对应数据库表column
 * 
 * @author zzd
 *
 */
public class Column {
	
	//所属数据表
	private Table _table;

	//字段名称，如：COLUMN_ID
	private String _sqlName;

	//数据库字段类型
	private int _sqlType;

	//数据库字段类型，如：DECIMAL
	private String _sqlTypeName;

	//是否主键
	private boolean _isPk;

	//是否外键
	private boolean _isFk;

	//字段长度
	private int _size;

	//字段小数点长度
	private int _decimalDigits;

	//允许null值
	private boolean _isNullable;

	//字段是否indexed
	private boolean _isIndexed;

	//字段是否唯一性约束
	private boolean _isUnique;

	//字段默认值
	private String _defaultValue;

	//字段备注
	private String _remarks;
	
	//字段名称，如数据库字段为USER_ID ==> UserId
	private String columnName;
	
	//字段的别名，等价于：getRemarks().isEmpty() ? getColumnNameFirstLower() : getRemarks()
	private String columnAlias;
	
	//java与数据库字段映射类型，对应配置文件中java_typemapping开头的key
	private String javaType;

	//many-to-one
	private ReferenceKey hasOne;

	//one-to-many
	private ReferenceKey hasMany = null;
	
	//是否显示
	private boolean isDisplay = true;
	
	//显示名称
	private String displayName = "";
	
	//页面显示的取值类型
	private String valueType = "";
	
	//取值类型如果是字典，这里是字典内容
	private String valueTypeDict = "";
	
	//是否可以搜索
	private boolean isSearch = false;
	
	//查询类型
	private String searchType = "";
	
	//查询下拉框字典
	private String searchDict = "";
	
	//是否编辑
	private boolean isEdit = true;
	
	//编辑类型
	private String editType = "";
	
	//编辑下拉框字典
	private String editDict = "";
	
	//是否校验
	private boolean isVaildata = true;
	
	//校验类型
	private String vaildataRule = "";
	
	private String enumString = "";
	
	private String enumClassName;

	/**
	 * @param table
	 * @param sqlType
	 * @param sqlTypeName
	 * @param sqlName
	 * @param size
	 * @param decimalDigits
	 * @param isPk
	 * @param isNullable
	 * @param isIndexed
	 * @param isUnique
	 * @param defaultValue
	 * @param remarks
	 */
	public Column(Table table, int sqlType, String sqlTypeName, String sqlName, int size, int decimalDigits,
			boolean isPk, boolean isNullable, boolean isIndexed, boolean isUnique, String defaultValue,
			String remarks) {
		_table = table;
		_sqlType = sqlType;
		_sqlName = sqlName;
		_sqlTypeName = sqlTypeName;
		_size = size;
		_decimalDigits = decimalDigits;
		_isPk = isPk;
		_isNullable = isNullable;
		_isIndexed = isIndexed;
		_isUnique = isUnique;
		_defaultValue = defaultValue;
		_remarks = remarks;
		initOtherProperties();
	}

	public Column(Column c) {
		this(c.getTable(), c.getSqlType(), c.getSqlTypeName(), c.getColumnName(), c.getSize(), c.getDecimalDigits(),
				c.isPk(), c.isNullable(), c.isIndexed(), c.isUnique(), c.getDefaultValue(), c.getRemarks());
	}

	public Column() {
	}

	/**
	 * 初始化其他额外属性
	 */
	private void initOtherProperties() {
		String removedPrefixColName = trimSkipPrefix(getSqlName());
		columnName = StringUtils.makeAllWordFirstLetterUpperCase(StringUtils.toUnderscoreName(removedPrefixColName));
		columnAlias = StringUtils.toString(getRemarks(), getColumnNameFirstLower());
		columnAlias = StringUtils.join(columnAlias.split("\t\n\r\f"), " ");
		String normalJdbcJavaType = DatabaseDataTypesUtils.getPreferredJavaType(getSqlType(), getSize(), getDecimalDigits());
		javaType = GeneratorProperties.getProperty("java_typemapping." + normalJdbcJavaType, normalJdbcJavaType).trim();
		enumClassName = getColumnName() + "Enum";
		//设置默认值
		displayName = _remarks;
	}
	
	/**
	 * 初始化页面属性
	 */
	public void initPageProperties(JSONObject json) {
		if(json == null) return;
		if("N".equals(json.getString("isDisplay"))) this.setDisplay(false);
		
		String displayName = json.getString("displayName");
		if(!StringUtils.isBlank(displayName)) this.displayName = displayName;
		
		String valueType = json.getString("valueType");
		if(!StringUtils.isBlank(valueType)) this.valueType = valueType;
		String valueTypeDict = json.getString("valueTypeDict");
		if(!StringUtils.isBlank(valueTypeDict)) this.valueTypeDict = valueTypeDict;
		
		if("Y".equals(json.getString("isSearch"))) this.isSearch = true;
		String searchType = json.getString("searchType");
		if(!StringUtils.isBlank(searchType)) this.searchType = searchType;
		String searchDict = json.getString("searchDict");
		if(!StringUtils.isBlank(searchDict)) this.searchDict = searchDict;
		
		if("N".equals(json.getString("isEdit"))) this.isEdit = false;
		String editType = json.getString("editType");
		if(!StringUtils.isBlank(editType)) this.editType = editType;
		String editDict = json.getString("editDict");
		if(!StringUtils.isBlank(editDict)) this.editDict = editDict;
		
		if("N".equals(json.getString("isVaildata"))) this.isVaildata = false;
		String vaildataRule = json.getString("vaildataRule");
		if(!StringUtils.isBlank(vaildataRule)) this.vaildataRule = vaildataRule;
		
		//如果是主键，则默认不编辑不搜索不校验
		if(isPk()){
			this.isSearch = false;
			this.isEdit = false;
			this.isVaildata = false;
		}
	}

	public Table getTable() {
		return _table;
	}
	
	public String getSqlName() {
		return _sqlName;
	}

	public int getSqlType() {
		return _sqlType;
	}

	public String getSqlTypeName() {
		return _sqlTypeName;
	}

	public boolean isPk() {
		return _isPk;
	}

	public void setPk(boolean isPk) {
		this._isPk = isPk;
	}
	
	public boolean isFk() {
		return _isFk;
	}
	
	void setFk(boolean isFk) {
		_isFk = isFk;
	}

	public int getSize() {
		return _size;
	}

	public int getDecimalDigits() {
		return _decimalDigits;
	}

	public boolean isNullable() {
		return _isNullable;
	}

	public void setNullable(boolean v) {
		this._isNullable = v;
	}

	public boolean isIndexed() {
		return _isIndexed;
	}

	public void setIndexed(boolean isIndexed) {
		_isIndexed = isIndexed;
	}

	public boolean isUnique() {
		return _isUnique;
	}

	public void setUnique(boolean unique) {
		_isUnique = unique;
	}

	public String getDefaultValue() {
		return _defaultValue;
	}

	public String getRemarks() {
		return _remarks;
	}

	/**
	 * 根据列名，根据sqlName计算得出，示例值： BirthDate
	 **/
	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	/**
	 * 第一个字母小写的columName,示例值: birthDate
	 **/
	public String getColumnNameFirstLower() {
		return StringUtils.changeFirstCharacterCase(getColumnName(), false);
	}

	/**
	 * 全部小写的columName,等价于: getColumnName().toLowerCase(),示例值: birthdate
	 **/
	public String getColumnNameLowerCase() {
		return getColumnName().toLowerCase();
	}

	/**
	 * 列的别名，等价于：getRemarks().isEmpty() ? getColumnNameFirstLower() : getRemarks()
	 * 示例值: birthDate
	 */
	public String getColumnAlias() {
		return columnAlias;
	}

	public void setColumnAlias(String columnAlias) {
		this.columnAlias = columnAlias;
	}

	/**
	 * 得到对应的javaType,如java.lang.String,
	 * @return
	 */
	public String getJavaType() {
		return javaType;
	}

	public void setJavaType(String javaType) {
		this.javaType = javaType;
	}

	/**
	 * 得到简短的java.lang.javaType,如java.lang.String将返回String,而非java.lang包的, 将直接返回getJavaType()
	 * @return
	 */
	public String getSimpleJavaType() {
		return StringUtils.removePrefix(getJavaType(), "java.lang.", false);
	}

	/**
	 * 得到原生类型的javaType,如java.lang.Integer将返回int,而非原生类型,将直接返回getSimpleJavaType()
	 * @return
	 */
	public String getPrimitiveJavaType() {
		return JavaPrimitiveTypeMapping.getPrimitiveType(getSimpleJavaType());
	}

	public String getHasOne() {
		return ReferenceKey.toString(hasOne);
	}

	/**
	 * 设置many-to-one,foreignKey格式: fk_table_name(fk_column) 或者 schema_name.fk_table_name(fk_column)
	 * @param foreignKey
	 * @return
	 */
	public void setHasOne(String foreignKey) {
		hasOne = ReferenceKey.fromString(foreignKey);
		if (hasOne != null && _table != null) {
			_table.getImportedKeys().addForeignKey(hasOne.tableName, hasOne.columnSqlName, getSqlName(),
					hasOne.columnSqlName.toLowerCase().hashCode());
		}
	}

	public String getHasMany() {
		return ReferenceKey.toString(hasMany);
	}

	/**
	 * 设置one-to-many,foreignKey格式: fk_table_name(fk_column) 或者  schema_name.fk_table_name(fk_column)
	 * @param foreignKey
	 * @return
	 */
	public void setHasMany(String foreignKey) {
		hasMany = ReferenceKey.fromString(foreignKey);
		if (hasMany != null && _table != null) {
			_table.getExportedKeys().addForeignKey(hasMany.tableName, hasMany.columnSqlName, getSqlName(),
					hasMany.columnSqlName.toLowerCase().hashCode());
		}
	}

	public boolean isDisplay() {
		return isDisplay;
	}

	public void setDisplay(boolean isDisplay) {
		this.isDisplay = isDisplay;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getValueType() {
		return valueType;
	}

	public void setValueType(String valueType) {
		this.valueType = valueType;
	}

	public String getValueTypeDict() {
		return valueTypeDict;
	}

	public void setValueTypeDict(String valueTypeDict) {
		this.valueTypeDict = valueTypeDict;
	}

	public boolean isSearch() {
		return isSearch;
	}

	public void setSearch(boolean isSearch) {
		this.isSearch = isSearch;
	}

	public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	public String getSearchDict() {
		return searchDict;
	}

	public void setSearchDict(String searchDict) {
		this.searchDict = searchDict;
	}

	public boolean isEdit() {
		return isEdit;
	}

	public void setEdit(boolean isEdit) {
		this.isEdit = isEdit;
	}

	public String getEditType() {
		return editType;
	}

	public void setEditType(String editType) {
		this.editType = editType;
	}

	public String getEditDict() {
		return editDict;
	}

	public void setEditDict(String editDict) {
		this.editDict = editDict;
	}

	public boolean isVaildata() {
		return isVaildata;
	}

	public void setVaildata(boolean isVaildata) {
		this.isVaildata = isVaildata;
	}

	public String getVaildataRule() {
		return vaildataRule;
	}

	public void setVaildataRule(String vaildataRule) {
		this.vaildataRule = vaildataRule;
	}

	/**
	 * 枚举值,以分号分隔,示例值:M(1,男);F(0,女) 或者是:M(男);F(女)
	 * @return
	 */
	public String getEnumString() {
		return enumString;
	}

	/**
	 * 枚举值,以分号分隔,示例值:M(1,男);F(0,女) 或者是:M(男);F(女)
	 * @param str
	 */
	public void setEnumString(String str) {
		this.enumString = str;
	}

	/**
	 * 得到枚举(enum)的类名称，示例值：SexEnum
	 * @return
	 */
	public String getEnumClassName() {
		return enumClassName;
	}

	public void setEnumClassName(String enumClassName) {
		this.enumClassName = enumClassName;
	}
	
	/**
	 * 获取JPA实体类注解
	 * @return
	 */
	public String getJapAnnotation() {
		StringBuffer sb = new StringBuffer();
		//处理主键
		if(_isPk){
			sb.append("@Id");
			sb.append("\r\n\t");
			//如果为DECIMAL才增加自增主键
			if(_sqlTypeName.equals("DECIMAL") || _sqlTypeName.equals("INT")){
				sb.append("@GeneratedValue(strategy = GenerationType.TABLE, generator = \"" + _table.getClassName() + "IdGenerator\")");
				sb.append("\r\n\t");
				sb.append("@TableGenerator(name = \"" + _table.getClassName() + "IdGenerator\", table = \"sys_tb_generator\", "
						+ "pkColumnName = \"GEN_NAME\", valueColumnName = \"GEN_VALUE\", pkColumnValue = \"" + _table.getSqlName().toUpperCase() +"_PK\", allocationSize = 1)");
				sb.append("\r\n\t");
			}
		}
		//处理时间类型
		if(_sqlTypeName.equals("DATETIME") || _sqlTypeName.equals("TIMESTAMP") 
				|| _sqlTypeName.equals("DATE") || _sqlTypeName.equals("TIME")){
			sb.append("@JsonDeserialize(using = CustomerDateAndTimeDeserialize.class)");
			sb.append("\r\n\t");
			sb.append("@Temporal(TemporalType.TIMESTAMP)");
			sb.append("\r\n\t");
		}
		//处理大文本类型
		if(_sqlTypeName.equals("TEXT")){
			sb.append("@Lob");
			sb.append("\r\n\t");
			sb.append("@Basic(fetch = FetchType.LAZY)");
			sb.append("\r\n\t");
		}
		sb.append("@Column(name = ");
		sb.append("\"" + _sqlName + "\", ");
		if(_isUnique){
			sb.append("unique = true, ");
		}
		if(!_isNullable){
			sb.append("nullable = false, ");
		}
		//处理VARCHAR
		if(_sqlTypeName.equals("VARCHAR")){
			sb.append("length = " + _size + ", ");
		}
		//处理DECIMAL
		if(_sqlTypeName.equals("DECIMAL")){
			sb.append("precision = " + _size + ", scale = " + _decimalDigits + ", ");
		}
		String retVal = sb.substring(0, sb.length() - 2);
		return retVal + ")";
	}

	/**
	 * 返回SqlName的小写，如user_id
	 * @return
	 */
	public String getUnderscoreName() {
		return getSqlName().toLowerCase();
	}

	/**
	 * 使用 jdbcSqlType类型名称，示例值:VARCHAR,DECIMAL, 现Ibatis3使用该属性
	 * @return
	 */
	public String getJdbcSqlTypeName() {
		return JdbcType.getJdbcSqlTypeName(getSqlType());
	}

	/**
	 * 列的常量名称
	 * 示例值: BIRTH_DATE
	 */
	public String getConstantName() {
		return StringUtils.toUnderscoreName(getColumnName()).toUpperCase();
	}

	/**
	 * 列是否是String类型
	 * @return
	 */
	public boolean getIsStringColumn() {
		return DatabaseDataTypesUtils.isString(getJavaType());
	}

	/**
	 * 列是否是日期类型
	 * @return
	 */
	public boolean getIsDateTimeColumn() {
		return DatabaseDataTypesUtils.isDate(getJavaType());
	}

	/**
	 * 列是否是Number类型
	 * @return
	 */
	public boolean getIsNumberColumn() {
		return DatabaseDataTypesUtils.isFloatNumber(getJavaType())
				|| DatabaseDataTypesUtils.isIntegerNumber(getJavaType());
	}

	protected String prefsPrefix() {
		return "tables/" + getTable().getSqlName() + "/columns/" + getSqlName();
	}

	/**
	 * 检查是否包含某些关键字,关键字以逗号分隔
	 * 
	 * @param keywords
	 * @return
	 */
	public boolean contains(String keywords) {
		if (keywords == null)
			throw new IllegalArgumentException("'keywords' must be not null");
		return StringUtils.contains(getSqlName(), keywords.split(","));
	}

	public boolean isHtmlHidden() {
		return isPk() && (_table.getPkCount() == 1);
	}

	/**
	 * 解析getEnumString()字符串转换为List<EnumMetaDada>对象
	 * @return
	 */
	public List<EnumMetaDada> getEnumList() {
		return ColumnUtils.string2EnumMetaData(getEnumString());
	}

	/**
	 * 是否是枚举列，等价于:return getEnumList() != null && !getEnumList().isEmpty()
	 * 
	 * @return
	 */
	public boolean isEnumColumn() {
		return getEnumList() != null && !getEnumList().isEmpty();
	}

	/**
	 * 得到列的测试数据
	 * @return
	 */
	public String getTestData() {
		return new GeneratorTestData().getDBUnitTestData(getColumnName(), getJavaType(), getSize());
	}

	/**
	 * 去除列名前缀
	 * 
	 * @param sqlName
	 * @return
	 */
	private String trimSkipPrefix(String sqlName) {
		String prefixs = GeneratorProperties.getProperty("rowRemovePrefixes", "").trim();
		for (String prefix : prefixs.split(",")) {
			String removedPrefixColName = StringUtils.removePrefix(sqlName, prefix, true);
			if (!removedPrefixColName.equals(sqlName)) {
				return removedPrefixColName;
			}
		}
		return sqlName;
	}

	public int hashCode() {
		if (getTable() != null) {
			return (getTable().getSqlName() + "#" + getSqlName()).hashCode();
		} else {
			return (getSqlName()).hashCode();
		}
	}
	
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o instanceof Column) {
			Column other = (Column) o;
			if (getSqlName().equals(other.getSqlName())) {
				return true;
			}
		}
		return false;
	}
	
	public String toString() {
		return getSqlName();
	}

}

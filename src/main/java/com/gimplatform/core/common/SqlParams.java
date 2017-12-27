package com.gimplatform.core.common;

import java.util.ArrayList;
import java.util.List;

public class SqlParams {

	public List<String> paramsList;
	
	public List<Object> valueList;
	
	public StringBuffer querySql;
	
	public SqlParams(){
		paramsList = new ArrayList<String>();
		valueList = new ArrayList<Object>();
		querySql = new StringBuffer();
	}
}

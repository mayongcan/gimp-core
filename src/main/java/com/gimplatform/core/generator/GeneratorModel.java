package com.gimplatform.core.generator;

import java.util.Map;

public class GeneratorModel implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public Map<String, Object> templateModel;
	
	public Map<String, Object> filePathModel;

	public GeneratorModel(Map<String, Object> templateModel, Map<String, Object> filePathModel) {
		this.templateModel = templateModel;
		this.filePathModel = filePathModel;
	}
}

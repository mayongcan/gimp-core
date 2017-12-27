package com.gimplatform.core.generator;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gimplatform.core.generator.utils.PropertyPlaceholderHelper;
import com.gimplatform.core.generator.utils.PropertyPlaceholderHelper.PropertyPlaceholderConfigurerResolver;
import com.gimplatform.core.utils.PropertiesUtils;

/**
 * 生成器配置类 用于装载generator.properties,generator.xml文件
 * 
 */
public class GeneratorProperties {

	private static final Logger logger = LogManager.getLogger(GeneratorProperties.class);

	static PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper("${", "}", ":", false);

	static final List<String> propertiesFileList = new ArrayList<String>();

	static PropertiesUtils props = new PropertiesUtils(new Properties(), true);

	private GeneratorProperties() {
	}

	static {
		resetPropertiesFile();
	}

	public static void reload() {
		try {
			logger.info("开始加载生成器配置文件:" + Arrays.toString(propertiesFileList.toArray()));
			Properties properties = new Properties();
			String[] loadedFiles = PropertiesUtils.loadAllPropertiesFromClassLoader(properties, GeneratorProperties.class, propertiesFileList);
			logger.info("加载生成器配置文件成功,files:" + Arrays.toString(loadedFiles));
			setProperties(properties);
		} catch (IOException e) {
			throw new RuntimeException("Load " + Arrays.toString(propertiesFileList.toArray()) + " error", e);
		}
	}
	
	/**
	 * 重置配置文件
	 */
	public static void resetPropertiesFile(){
		propertiesFileList.clear();
		propertiesFileList.add("generator.properties");
		propertiesFileList.add("generator.xml");
		propertiesFileList.add("custom-generator.properties");
		propertiesFileList.add("custom-generator.xml");
		reload();
	}
	
	public static void addPropertiesFile(InputStream inputStream) throws IOException{
		logger.info("以文件流的方式重新加载配置文件");
		Properties properties = new Properties();
		try {
			properties.loadFromXML(inputStream);
		}
		finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
		setProperties(properties);
	}

	/**
	 * 自动替换所有value从 com.company 替换为 com/company,并设置key = key+"_dir"后缀
	 * 
	 * @param props
	 * @return
	 */
	private static Properties autoReplacePropertiesValue2DirValue(Properties props) {
		Properties autoReplaceProperties = new Properties();
		for (Object key : getProperties().keySet()) {
			String dir_key = key.toString() + "_dir";
			String value = props.getProperty(key.toString());
			String dir_value = value.toString().replace('.', '/');
			autoReplaceProperties.put(dir_key, dir_value);
		}
		return autoReplaceProperties;
	}

	public static Properties getProperties() {
		return props.getProperties();
	}

	public static String getProperty(String key, String defaultValue) {
		return props.getProperty(key, defaultValue);
	}

	public static String getProperty(String key) {
		return props.getProperty(key);
	}

	public static boolean getProperty(String key, boolean defaultValue) {
		return props.getBoolean(key, defaultValue);
	}

	public static String getRequiredProperty(String key) {
		return props.getRequiredProperty(key);
	}

	public static int getRequiredInt(String key) {
		return props.getRequiredInt(key);
	}

	public static boolean getRequiredBoolean(String key) {
		return props.getRequiredBoolean(key);
	}

	public static String getNullIfBlank(String key) {
		return props.getNullIfBlank(key);
	}

	public static void setProperty(String key, String value) {
		value = resolveProperty(value, getProperties());
		key = resolveProperty(key, getProperties());
		logger.info("[setProperty()] " + key + "=" + value);
		props.setProperty(key, value);
		String dir_value = value.toString().replace('.', '/');
		props.getProperties().put(key + "_dir", dir_value);
	}

	private static Properties resolveProperties(Properties props) {
		Properties result = new Properties();
		for (Object s : props.keySet()) {
			String sourceKey = s.toString();
			String key = resolveProperty(sourceKey, props);
			String value = resolveProperty(props.getProperty(sourceKey), props);
			result.setProperty(key, value);
		}
		return result;
	}

	private static String resolveProperty(String v, Properties props) {
		PropertyPlaceholderConfigurerResolver propertyPlaceholderConfigurerResolver = new PropertyPlaceholderConfigurerResolver(
				props);
		return helper.replacePlaceholders(v, propertyPlaceholderConfigurerResolver);
	}

	@SuppressWarnings("rawtypes")
	public static void setProperties(Properties inputProps) {
		props = new PropertiesUtils(resolveProperties(inputProps), true);
		for (Iterator it = props.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			logger.info("[Property] " + entry.getKey() + " = " + entry.getValue());
		}
		logger.info("[自动替换] [.] => [/] on generator.properties, key=source_key+'_dir', 如: pkg=com.gimplatform ==> pkg_dir=com/gimplatform");
		Properties dirProperties = autoReplacePropertiesValue2DirValue(props.getProperties());
		props.getProperties().putAll(dirProperties);
	}

}

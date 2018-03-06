package com.gimplatform.core.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

public class PropertiesUtils {

    boolean isSearchSystemProperty = false;

    Properties properties;

    public PropertiesUtils(Properties p) {
        this.properties = p;
    }

    public PropertiesUtils(Properties p, boolean isSearchSystemProperty) {
        this.properties = p;
        this.isSearchSystemProperty = isSearchSystemProperty;
    }

    public Properties getProperties() {
        return properties;
    }

    public String getProperty(String key, String defaultValue) {
        String value = null;
        if (isSearchSystemProperty) {
            value = System.getProperty(key);
        }
        if (value == null || "".equals(value.trim())) {
            value = getProperties().getProperty(key);
        }
        return value == null || "".equals(value.trim()) ? defaultValue : value;
    }

    public String getProperty(String key) {
        return getProperty(key, null);
    }

    public String getRequiredProperty(String key) {
        String value = getProperty(key);
        if (value == null || "".equals(value.trim())) {
            throw new IllegalStateException("required property is blank by key=" + key);
        }
        return value;
    }

    public Integer getInt(String key) {
        if (getProperty(key) == null) {
            return null;
        }
        return Integer.parseInt(getRequiredProperty(key));
    }

    public int getInt(String key, int defaultValue) {
        if (getProperty(key) == null) {
            return defaultValue;
        }
        return Integer.parseInt(getRequiredProperty(key));
    }

    public int getRequiredInt(String key) {
        return Integer.parseInt(getRequiredProperty(key));
    }

    public Boolean getBoolean(String key) {
        if (getProperty(key) == null) {
            return false;
        }
        return Boolean.parseBoolean(getRequiredProperty(key));
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        if (getProperty(key) == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(getRequiredProperty(key));
    }

    public boolean getRequiredBoolean(String key) {
        return Boolean.parseBoolean(getRequiredProperty(key));
    }

    public String getNullIfBlank(String key) {
        String value = getProperty(key);
        if (value == null || "".equals(value.trim())) {
            return null;
        }
        return value;
    }

    public PropertiesUtils setProperty(String key, String value) {
        properties.setProperty(key, value);
        return this;
    }

    public void clear() {
        properties.clear();
    }

    public Set<Entry<Object, Object>> entrySet() {
        return properties.entrySet();
    }

    public Enumeration<?> propertyNames() {
        return properties.propertyNames();
    }

    @SuppressWarnings("rawtypes")
    public static String[] loadAllPropertiesFromClassLoader(Properties properties, Class classz, List<String> resourceNames) throws IOException {
        List<String> successLoadProperties = new ArrayList<String>();
        Enumeration urls = null;
        for (String resourceName : resourceNames) {
            urls = classz.getClassLoader().getResources(resourceName);
            while (urls.hasMoreElements()) {
                URL url = (URL) urls.nextElement();
                successLoadProperties.add(url.getFile());
                InputStream input = null;
                try {
                    URLConnection con = url.openConnection();
                    con.setUseCaches(false);
                    input = con.getInputStream();
                    if (resourceName.endsWith(".xml")) {
                        properties.loadFromXML(input);
                    } else {
                        properties.load(input);
                    }
                } finally {
                    if (input != null) {
                        input.close();
                    }
                }
            }
        }
        return successLoadProperties.toArray(new String[successLoadProperties.size()]);
    }
}

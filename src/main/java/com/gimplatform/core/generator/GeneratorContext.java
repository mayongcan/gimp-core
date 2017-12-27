package com.gimplatform.core.generator;

import java.util.HashMap;
import java.util.Map;

/**
 * 生成器的上下文，存放的变量将可以在模板中引用
 * @author zzd
 *
 */
public class GeneratorContext {

	static ThreadLocal<Map<String, Object>> context = new ThreadLocal<Map<String, Object>>();

	public static void clear() {
		Map<String, Object> m = context.get();
		if (m != null)
			m.clear();
	}

	public static Map<String, Object> getContext() {
		Map<String, Object> map = context.get();
		if (map == null) {
			setContext(new HashMap<String, Object>());
		}
		return context.get();
	}

	public static void setContext(Map<String, Object> map) {
		context.set(map);
	}

	public static void put(String key, Object value) {
		getContext().put(key, value);
	}
}

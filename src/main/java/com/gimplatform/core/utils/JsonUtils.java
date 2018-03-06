package com.gimplatform.core.utils;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.InputSource;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class JsonUtils {

    private static final Logger logger = LogManager.getLogger(JsonUtils.class);

    /**
     * xml转json字符串 注意:路径和字符串二传一另外一个传null<br>
     * @param xmlPath xml路径(和字符串二传一,两样都传优先使用路径)
     * @param xmlStr xml字符串(和路径二传一,两样都传优先使用路径)
     * @return String
     * @throws IOException
     * @throws JDOMException
     */
    public static String xmlToJson(String xmlPath, String xmlStr) {
        SAXBuilder sbder = new SAXBuilder();
        Map<String, Object> map = new HashMap<String, Object>();
        Document document;
        try {
            if (xmlPath != null) {
                // 路径
                document = sbder.build(new File(xmlPath));
            } else if (xmlStr != null) {
                // xml字符
                StringReader reader = new StringReader(xmlStr);
                InputSource ins = new InputSource(reader);
                document = sbder.build(ins);
            } else {
                return "{}";
            }
            // 获取根节点
            Element el = document.getRootElement();
            List<Element> eList = el.getChildren();
            Map<String, Object> rootMap = new HashMap<String, Object>();
            // 得到递归组装的map
            rootMap = xmlToMap(eList, rootMap);
            map.put(el.getName(), rootMap);
            // 将map转换为json 返回
            return JSON.toJSONString(map);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "{}";
        }
    }

    /**
     * json转xml
     * @param jObj
     * @return
     */
    public static String jsonToXml(JSONObject jObj) {
        try {
            StringBuffer buffer = new StringBuffer();
            buffer.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            jsonToXmlstr(jObj, buffer);
            return buffer.toString();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "";
        }
    }

    /**
     * json转xml<br>
     * @param json
     * @return String
     */
    public static String jsonToXml(String json) {
        try {
            StringBuffer buffer = new StringBuffer();
            buffer.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            JSONObject jObj = JSON.parseObject(json);
            jsonToXmlstr(jObj, buffer);
            return buffer.toString();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "";
        }
    }

    /**
     * json转str<br>
     * @param jObj
     * @param buffer
     * @return String
     */
    private static String jsonToXmlstr(JSONObject jObj, StringBuffer buffer) {
        Set<Entry<String, Object>> se = jObj.entrySet();
        for (Iterator<Entry<String, Object>> it = se.iterator(); it.hasNext();) {
            Entry<String, Object> en = it.next();
            if (en.getValue().getClass().getName().equals("com.alibaba.fastjson.JSONObject")) {
                buffer.append("<" + en.getKey() + ">");
                JSONObject jo = jObj.getJSONObject(en.getKey());
                jsonToXmlstr(jo, buffer);
                buffer.append("</" + en.getKey() + ">");
            } else if (en.getValue().getClass().getName().equals("com.alibaba.fastjson.JSONArray")) {
                JSONArray jarray = jObj.getJSONArray(en.getKey());
                for (int i = 0; i < jarray.size(); i++) {
                    buffer.append("<" + en.getKey() + ">");
                    JSONObject jsonobject = jarray.getJSONObject(i);
                    jsonToXmlstr(jsonobject, buffer);
                    buffer.append("</" + en.getKey() + ">");
                }
            } else if (en.getValue().getClass().getName().equals("java.lang.String")) {
                buffer.append("<" + en.getKey() + ">" + en.getValue());
                buffer.append("</" + en.getKey() + ">");
            }
        }
        return buffer.toString();
    }

    /**
     * 节点转map<br>
     * @param eList
     * @param map
     * @return Map<String,Object>
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> xmlToMap(List<Element> eList, Map<String, Object> map) {
        for (Element e : eList) {
            Map<String, Object> eMap = new HashMap<String, Object>();
            List<Element> elementList = e.getChildren();
            if (elementList != null && elementList.size() > 0) {
                eMap = xmlToMap(elementList, eMap);
                Object obj = map.get(e.getName());
                if (obj != null) {
                    List<Object> olist = new ArrayList<Object>();
                    if (obj.getClass().getName().equals("java.util.HashMap")) {
                        olist.add(obj);
                        olist.add(eMap);

                    } else if (obj.getClass().getName().equals("java.util.ArrayList")) {
                        olist = (List<Object>) obj;
                        olist.add(eMap);
                    }
                    map.put(e.getName(), olist);
                } else {
                    map.put(e.getName(), eMap);
                }
            } else {
                map.put(e.getName(), e.getValue());
            }
        }
        return map;
    }

    /**
     * JSON to List<Map>
     * @param json
     * @return
     */
    public static List<Map<String, Object>> jsonToListMap(Object json) {
        JSONArray jsonArr = (JSONArray) json;
        List<Map<String, Object>> arrList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < jsonArr.size(); ++i) {
            arrList.add(jsonToMap(jsonArr.getString(i)));
        }
        return arrList;
    }

    /**
     * JSON to List<String>
     * @param json
     * @return
     */
    public static List<String> jsonToListStr(Object json) {
        JSONArray jsonArr = (JSONArray) json;
        List<String> arrList = new ArrayList<String>();
        for (int i = 0; i < jsonArr.size(); ++i) {
            arrList.add(jsonArr.getString(i));
        }
        return arrList;
    }

    /**
     * JSON to Map
     * @param json
     * @return
     */
    public static Map<String, Object> jsonToMap(String json) {
        JSONObject jsonObject = JSONObject.parseObject(json);
        return jsonToMap(jsonObject);
    }

    /**
     * JSON to Map
     * @param jsonObject
     * @return
     */
    public static Map<String, Object> jsonToMap(JSONObject jsonObject) {
        Map<String, Object> resMap = new HashMap<String, Object>();
        Iterator<Entry<String, Object>> it = jsonObject.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> param = (Map.Entry<String, Object>) it.next();
            if (param.getValue() instanceof JSONObject) {
                resMap.put(param.getKey(), jsonToMap(param.getValue().toString()));
            } else if (param.getValue() instanceof JSONArray) {
                JSONArray jsonArr = (JSONArray) param.getValue();
                if (jsonArr != null && jsonArr.size() > 0) {
                    if (jsonArr.get(0) instanceof String) {
                        resMap.put(param.getKey(), jsonToListStr(param.getValue()));
                    } else {
                        resMap.put(param.getKey(), jsonToListMap(param.getValue()));
                    }
                }
            } else {
                // resMap.put(param.getKey(), JSONObject.toJSONString(param.getValue(), SerializerFeature.WriteClassName));
                resMap.put(param.getKey(), param.getValue());
            }
        }
        return resMap;
    }
}

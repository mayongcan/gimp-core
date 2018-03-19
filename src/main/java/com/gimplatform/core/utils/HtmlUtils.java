package com.gimplatform.core.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.MapUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Html内容处理类
 * @author zzd
 */
public class HtmlUtils {

    protected static final Logger logger = LogManager.getLogger(HtmlUtils.class);
    
    /**
     * 获取分页源码的第一页（基于.net的分页）
     * @param url
     * @return
     */
    public static Map<String, Object> getFirstPageHtml(String url) {
        // 设置用户代理
        String user_Agent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.87 Safari/537.36";
        // 获取登录框的隐含参数 type="hidden"
        Connection connection = Jsoup.connect(url).timeout(60000);
        connection.header("User-Agent", user_Agent);// 配置模拟浏览器
        Connection.Response response = null;
        try {
            response = connection.method(Connection.Method.POST).execute();// 获取响应
        } catch (ClientProtocolException e) {
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        // 应用JsoupHtml解析包解析html包含参数
        Map<String, Object> retVal = new HashMap<String, Object>();
        retVal.put("htmlBody", response.body());
        retVal.put("cookies", response.cookies());
        return retVal;
    }

    /**
     * 获取分页源码的分页内容（基于.net的分页）
     * @param url
     * @param params
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Map<String, Object> getPaginationHtml(String url, Map<String, Object> params) {
        String htmlBody = MapUtils.getString(params, "htmlBody");
        Map<String, String> cookies = (Map<String, String>) MapUtils.getMap(params, "cookies");
        if(cookies == null) cookies = new HashMap<String, String>();
        String pageName = MapUtils.getString(params, "pageName");
        String pageValue = MapUtils.getString(params, "pageValue");
        String __EVENTTARGET = MapUtils.getString(params, "__EVENTTARGET");
        String __EVENTARGUMENT = MapUtils.getString(params, "__EVENTARGUMENT");
        Document doc = Jsoup.parse(htmlBody);// 转换为Dom树  
        
        // 存放post时的数据  
        Map<String, String> datas = new HashMap<>();  
        Elements inputElemets = doc.select("form[method=post]").first().select("input[name]");  
        for (Iterator it = inputElemets.iterator(); it.hasNext();) {  
            Element inputElement = (Element) it.next();  
            if("btnOK".equals(inputElement.attr("name")) || "Button1".equals(inputElement.attr("name"))){   
            }else{  
                datas.put(inputElement.attr("name"), inputElement.attr("value"));  
            }  
        }  
        datas.put("__EVENTTARGET", __EVENTTARGET);  
        datas.put("__EVENTARGUMENT", __EVENTARGUMENT); 
        datas.put(pageName, pageValue); 
        Connection connection2 = Jsoup.connect(url).timeout(60000);  

        // 应用JsoupHtml解析包解析html包含参数
        Map<String, Object> retVal = new HashMap<String, Object>();
        try {    
            
            Connection connection1 = connection2.ignoreContentType(true).method(Connection.Method.POST).data(datas).cookies(cookies);  
            Connection.Response response =  connection1.execute(); 
            retVal.put("htmlBody", response.body());
            retVal.put("cookies", response.cookies());
        } catch (IOException e) {  
            logger.error(e.getMessage(), e);
        }  
        return retVal;
    }

    /**
     * 替换掉HTML标签方法
     */
    public static String replaceHtml(String html) {
        if (StringUtils.isBlank(html)) {
            return "";
        }
        String regEx = "<.+?>";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(html);
        String s = m.replaceAll("");
        return s;
    }

    /**
     * 替换为手机识别的HTML，去掉样式及属性，保留回车。
     * @param html
     * @return
     */
    public static String replaceMobileHtml(String html) {
        if (html == null) {
            return "";
        }
        return html.replaceAll("<([a-z]+?)\\s+?.*?>", "<$1>");
    }

    /**
     * 替换为手机识别的HTML，去掉样式及属性，保留回车。
     * @param txt
     * @return
     */
    public static String toHtml(String txt) {
        if (txt == null) {
            return "";
        }
        return StringUtils.replace(StringUtils.replace(Encodes.escapeHtml(txt), "\n", "<br/>"), "\t", "&nbsp; &nbsp; ");
    }

    /**
     * 查找所有指定的DIV的input里面的ID
     * @param html
     * @param className
     * @return
     */
    public static List<String> findAllFromId(String html, String className) {
        List<String> list = new ArrayList<String>();
        Document doc = Jsoup.parse(html);
        Elements elements = doc.getElementsByClass(className);
        if (elements != null && elements.size() > 0) {
            for (Element e : elements) {
                Element idElement = e.select("input").first();
                if (idElement == null) {
                    idElement = e.select("select").first();
                    if (idElement == null) {
                        idElement = e.select("textarea").first();
                        if (idElement != null)
                            list.add(idElement.id());
                    } else {
                        list.add(idElement.id());
                    }
                } else {
                    list.add(idElement.id());
                }
            }
        }
        return list;
    }

    /**
     * 返回ID和Name的集合
     * @param html
     * @param className
     * @return
     */
    public static List<Map<String, String>> findAllFromIdAndName(String html, String className) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Document doc = Jsoup.parse(html);
        Elements elements = doc.getElementsByClass(className);
        Map<String, String> tmpMap = null;
        if (elements != null && elements.size() > 0) {
            for (Element e : elements) {
                tmpMap = new HashMap<String, String>();
                Element labelElement = e.select("label").first();
                tmpMap.put("name", labelElement.text());
                Element idElement = e.select("input").first();
                if (idElement == null) {
                    idElement = e.select("select").first();
                    if (idElement == null) {
                        idElement = e.select("textarea").first();
                        if (idElement != null) {
                            tmpMap.put("id", idElement.id());
                            list.add(tmpMap);
                        }
                    } else {
                        tmpMap.put("id", idElement.id());
                        list.add(tmpMap);
                    }
                } else {
                    tmpMap.put("id", idElement.id());
                    list.add(tmpMap);
                }
            }
        }
        return list;
    }

    /**
     * 查找可写的属性
     * @param html
     * @param className
     * @return
     */
    public static Map<String, String> findAllFromlabelWithWriteable(String html, String className) {
        Document doc = Jsoup.parse(html);
        Elements elements = doc.getElementsByClass(className);
        Map<String, String> tmpMap = new HashMap<String, String>();
        if (elements != null && elements.size() > 0) {
            for (Element e : elements) {
                Element labelElement = e.select("label").first();
                Element idElement = e.select("input").first();
                if (idElement == null) {
                    idElement = e.select("select").first();
                    if (idElement == null) {
                        idElement = e.select("textarea").first();
                        if (idElement != null && !idElement.hasAttr("readonly")) {
                            tmpMap.put(idElement.id(), labelElement.text());
                        }
                    } else {
                        if (!idElement.hasAttr("readonly")) {
                            tmpMap.put(idElement.id(), labelElement.text());
                        }
                    }
                } else {
                    if (!idElement.hasAttr("readonly")) {
                        tmpMap.put(idElement.id(), labelElement.text());
                    }
                }
            }
        }
        return tmpMap;
    }
}

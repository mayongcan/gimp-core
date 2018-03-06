package com.gimplatform.core.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Html内容处理类
 * @author zzd
 */
public class HtmlUtils {

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

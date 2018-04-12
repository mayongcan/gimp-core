package com.gimplatform.core.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.text.StringEscapeUtils;
import com.google.common.collect.Lists;

/**
 * 字符串工具类, 继承org.apache.commons.lang3.StringUtils类
 * @author zzd
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    public static boolean isWindowsOS = System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0;

    private static final char SEPARATOR = '_';
    
    private static final String CHARSET_NAME = "UTF-8";

    /**
     * 转换为字节数组
     * @param str
     * @return
     */
    public static byte[] getBytes(String str) {
        if (str != null) {
            try {
                return str.getBytes(CHARSET_NAME);
            } catch (UnsupportedEncodingException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 转换为Boolean类型 'true', 'on', 'y', 't', 'yes' or '1' (case insensitive) will return true. Otherwise, false is returned.
     */
    public static Boolean toBoolean(final Object val) {
        if (val == null) {
            return false;
        }
        return BooleanUtils.toBoolean(val.toString()) || "1".equals(val.toString());
    }

    /**
     * 转换为Boolean类型 'true', 'on', 'y', 't', 'yes' or '1' (case insensitive) will return true. Otherwise, false is returned.
     */
    public static Boolean toBoolean(final Object val, boolean defaultVal) {
        if (val == null) {
            return defaultVal;
        }
        if (isBlank(val.toString()))
            return defaultVal;
        return BooleanUtils.toBoolean(val.toString()) || "1".equals(val.toString());
    }

    /**
     * 转换为字节数组
     * @param str
     * @return
     */
    public static String toString(byte[] bytes) {
        try {
            return new String(bytes, CHARSET_NAME);
        } catch (UnsupportedEncodingException e) {
            return EMPTY;
        }
    }

    /**
     * 如果对象为空，则使用defaultVal值 see: ObjectUtils.toString(obj, defaultVal)
     * @param obj
     * @param defaultVal
     * @return
     */
    public static String toString(final Object obj, final String defaultVal) {
        return obj == null ? defaultVal : obj.toString();
    }

    /**
     * 是否包含字符串
     * @param str 验证字符串
     * @param strs 字符串组
     * @return 包含返回true
     */
    public static boolean inString(String str, String... strs) {
        if (str != null) {
            for (String s : strs) {
                if (str.equals(trim(s))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 转换为Double类型
     */
    public static Double toDouble(Object val) {
        if (val == null) {
            return null;
        }
        try {
            return Double.valueOf(trim(val.toString()));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 转换为Double类型
     */
    public static Double toDouble(Object val, Double def) {
        if (val == null) {
            return def;
        }
        try {
            return Double.valueOf(trim(val.toString()));
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * 转换为Float类型
     */
    public static Float toFloat(Object val) {
        Double d = toDouble(val);
        if (d == null)
            return null;
        else
            return toDouble(val).floatValue();
    }

    /**
     * 转换为Float类型
     */
    public static Float toFloat(Object val, Float def) {
        Double d = toDouble(val);
        if (d == null)
            return def;
        else
            return toDouble(val).floatValue();
    }

    /**
     * 转换为Long类型
     */
    public static Long toLong(Object val) {
        Double d = toDouble(val);
        if (d == null)
            return null;
        else
            return toDouble(val).longValue();
    }

    /**
     * 转换为Long类型
     */
    public static Long toLong(Object val, Long def) {
        Double d = toDouble(val);
        if (d == null)
            return def;
        else
            return toDouble(val).longValue();
    }

    /**
     * 转换为Integer类型
     */
    public static Integer toInteger(Object val) {
        Double d = toDouble(val);
        if (d == null)
            return null;
        else
            return toDouble(val).intValue();
    }

    /**
     * 转换为Integer类型
     */
    public static Integer toInteger(Object val, Integer def) {
        Double d = toDouble(val);
        if (d == null)
            return def;
        else
            return toDouble(val).intValue();
    }

    /**
     * 驼峰命名法工具
     * @return toCamelCase("hello_world") == "helloWorld" 
     * toCapitalizeCamelCase("hello_world") == "HelloWorld" 
     * toUnderScoreCase("helloWorld") = "hello_world"
     */
    public static String toCamelCase(String s) {
        if (s == null) {
            return null;
        }
        s = s.toLowerCase();
        StringBuilder sb = new StringBuilder(s.length());
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == SEPARATOR) {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 驼峰命名法工具
     * @return toCamelCase("hello_world") == "helloWorld" 
     * toCapitalizeCamelCase("hello_world") == "HelloWorld" 
     * toUnderScoreCase("helloWorld") = "hello_world"
     */
    public static String toCapitalizeCamelCase(String s) {
        if (s == null) {
            return null;
        }
        s = toCamelCase(s);
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    /**
     * 驼峰命名法工具
     * @return toCamelCase("hello_world") == "helloWorld" 
     * toCapitalizeCamelCase("hello_world") == "HelloWorld" 
     * toUnderScoreCase("helloWorld") = "hello_world"
     */
    public static String toUnderScoreCase(String s) {
        if (s == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            boolean nextUpperCase = true;
            if (i < (s.length() - 1)) {
                nextUpperCase = Character.isUpperCase(s.charAt(i + 1));
            }
            if ((i > 0) && Character.isUpperCase(c)) {
                if (!upperCase || !nextUpperCase) {
                    sb.append(SEPARATOR);
                }
                upperCase = true;
            } else {
                upperCase = false;
            }
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }

    /**
     * 转换为JS获取对象值，生成三目运算返回结果
     * @param objectString 对象串 例如：row.user.id 返回：!row?'':!row.user?'':!row.user.id?'':row.user.id
     */
    public static String jsGetVal(String objectString) {
        StringBuilder result = new StringBuilder();
        StringBuilder val = new StringBuilder();
        String[] vals = split(objectString, ".");
        for (int i = 0; i < vals.length; i++) {
            val.append("." + vals[i]);
            result.append("!" + (val.substring(1)) + "?'':");
        }
        result.append(val.substring(1));
        return result.toString();
    }

    /**
     * 将字符串的首字母更换为大写或小写
     * @param str
     * @param capitalize(true=大写，false=小写)
     * @return
     */
    public static String changeFirstCharacterCase(String str, boolean capitalize) {
        if (str == null || str.length() == 0) {
            return str;
        }
        StringBuffer buf = new StringBuffer(str.length());
        if (capitalize) {
            buf.append(Character.toUpperCase(str.charAt(0)));
        } else {
            buf.append(Character.toLowerCase(str.charAt(0)));
        }
        buf.append(str.substring(1));
        return buf.toString();
    }

    /**
     * 转换为带下划线的变量,例：userInfo => user_info
     * @param name
     * @return
     */
    public static String toUnderscoreName(String name) {
        if (name == null)
            return null;
        String filteredName = name;
        if (filteredName.indexOf("_") >= 0 && filteredName.equals(filteredName.toUpperCase())) {
            filteredName = filteredName.toLowerCase();
        }
        if (filteredName.indexOf("_") == -1 && filteredName.equals(filteredName.toUpperCase())) {
            filteredName = filteredName.toLowerCase();
        }
        StringBuffer result = new StringBuffer();
        if (filteredName != null && filteredName.length() > 0) {
            result.append(filteredName.substring(0, 1).toLowerCase());
            for (int i = 1; i < filteredName.length(); i++) {
                String preChart = filteredName.substring(i - 1, i);
                String c = filteredName.substring(i, i + 1);
                if (c.equals("_")) {
                    result.append("_");
                    continue;
                }
                if (preChart.equals("_")) {
                    result.append(c.toLowerCase());
                    continue;
                }
                if (c.matches("\\d")) {
                    result.append(c);
                } else if (c.equals(c.toUpperCase())) {
                    result.append("_");
                    result.append(c.toLowerCase());
                } else {
                    result.append(c);
                }
            }
        }
        return result.toString();
    }

    /**
     * 生成字符串如：USER_INFO => UserInfo (生成结果的首字母为大写)
     * @param sqlName
     * @return
     */
    public static String makeAllWordFirstLetterUpperCase(String sqlName) {
        String[] strs = sqlName.toLowerCase().split("_");
        String result = "";
//        String preStr = "";
        for (int i = 0; i < strs.length; i++) {
//            if (preStr.length() == 1) {
//                result += strs[i];
//            } else {
//                result += capitalize(strs[i]);
//            }
//            preStr = strs[i];
            //上面注释的原因：c_doll生成Cdoll, 新代码c_doll生成CDoll
            result += capitalize(strs[i]);
        }
        return result;
    }

    /**
     * 是否包含字符串
     * @param str
     * @param keywords
     * @return
     */
    public static boolean contains(String str, String... keywords) {
        if (str == null)
            return false;
        if (keywords == null)
            throw new IllegalArgumentException("'keywords' must be not null");
        for (String keyword : keywords) {
            if (str.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 移除存在的前缀
     * @param str
     * @param prefix
     * @param ignoreCase
     * @return
     */
    public static String removePrefix(String str, String prefix, boolean ignoreCase) {
        if (str == null)
            return null;
        if (prefix == null)
            return str;
        if (ignoreCase) {
            if (str.toLowerCase().startsWith(prefix.toLowerCase())) {
                return str.substring(prefix.length());
            }
        } else {
            if (str.startsWith(prefix)) {
                return str.substring(prefix.length());
            }
        }
        return str;
    }

    /**
     * 连接字符串
     * @param array
     * @param seperator
     * @return
     */
    public static String join(Object[] array, String seperator) {
        if (array == null)
            return null;
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < array.length; i++) {
            result.append(array[i]);
            if (i != array.length - 1) {
                result.append(seperator);
            }
        }
        return result.toString();
    }

    /**
     * 计算字符串出现次数
     * @param str
     * @param sub
     * @return
     */
    public static int countOccurrencesOf(String str, String sub) {
        if (str == null || sub == null || str.length() == 0 || sub.length() == 0) {
            return 0;
        }
        int count = 0;
        int pos = 0;
        int idx;
        while ((idx = str.indexOf(sub, pos)) != -1) {
            ++count;
            pos = idx + sub.length();
        }
        return count;
    }

    /**
     * 移除多个字符串
     * @param inString
     * @param keywords
     * @return
     */
    public static String removeMany(String inString, String... keywords) {
        if (inString == null) {
            return null;
        }
        for (String k : keywords) {
            inString = replace(inString, k, "");
        }
        return inString;
    }

    /**
     * 包含个数
     * @param string
     * @param keyword
     * @return
     */
    public static int containsCount(String string, String keyword) {
        if (string == null)
            return 0;
        int count = 0;
        for (int i = 0; i < string.length(); i++) {
            int indexOf = string.indexOf(keyword, i);
            if (indexOf < 0) {
                break;
            }
            count++;
            i = indexOf;
        }
        return count;
    }

    /**
     * @param input
     * @param regex
     * @return
     */
    public static int indexOfByRegex(String input, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);
        if (m.find()) {
            return m.start();
        }
        return -1;
    }

    /**
     * Map转换为String
     * @param paramMap
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static String mapToString(Map paramMap) {
        if (paramMap == null) {
            return "";
        }
        StringBuilder params = new StringBuilder();
        for (Map.Entry<String, String[]> param : ((Map<String, String[]>) paramMap).entrySet()) {
            params.append(("".equals(params.toString()) ? "" : "&") + param.getKey() + "=");
            String paramValue = (param.getValue() != null && param.getValue().length > 0 ? param.getValue()[0] : "");
            params.append(StringUtils.abbr(StringUtils.endsWithIgnoreCase(param.getKey(), "password") ? "" : paramValue, 100));
        }
        return params.toString();
    }

    /**
     * @param str
     * @param delimiter
     * @param total
     * @return
     */
    public static List<String> splitToList(String str, String delimiter, int total) {
        List<String> list = new ArrayList<String>();
        if (isBlank(str)) {
            for (int i = 0; i < total; i++) {
                list.add("");
            }
            return list;
        }
        String array[] = str.split(delimiter);
        if (array.length == 0) {
            for (int i = 0; i < total; i++) {
                list.add("");
            }
        } else if (total == 0 || total == array.length) {
            for (String tmp : array) {
                list.add(tmp);
            }
        } else if (total > array.length) {
            for (String tmp : array) {
                list.add(tmp);
            }
            for (int i = array.length; i < total; i++) {
                list.add("");
            }
        } else if (total < array.length) {
            for (int i = 0; i < total; i++) {
                list.add(array[i]);
            }
        }
        return list;
    }

    /**
     * 字符串数组转换int数组
     * @param array
     * @param def
     * @return
     */
    public static int[] arrayToInts(String array[], int def) {
        int[] n = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            n[i] = StringUtils.toInteger(array[i], def);
        }
        return n;
    }
    
    public static boolean isInList(List<Integer> list, int value) {
        for (int i = 0; i < list.size(); i++) {
            if(value == list.get(i)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 缩略字符串（不区分中英文字符）
     * @param str 目标字符串
     * @param length 截取长度
     * @return
     */
    public static String abbr(String str, int length) {
        if (str == null) {
            return "";
        }
        try {
            StringBuilder sb = new StringBuilder();
            int currentLength = 0;
            for (char c : HtmlUtils.replaceHtml(StringEscapeUtils.unescapeHtml4(str)).toCharArray()) {
                currentLength += String.valueOf(c).getBytes("GBK").length;
                if (currentLength <= length - 3) {
                    sb.append(c);
                } else {
                    sb.append("...");
                    break;
                }
            }
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String abbr2(String param, int length) {
        if (param == null) {
            return "";
        }
        StringBuffer result = new StringBuffer();
        int n = 0;
        char temp;
        boolean isCode = false; // 是不是HTML代码
        boolean isHTML = false; // 是不是HTML特殊字符,如&nbsp;
        for (int i = 0; i < param.length(); i++) {
            temp = param.charAt(i);
            if (temp == '<') {
                isCode = true;
            } else if (temp == '&') {
                isHTML = true;
            } else if (temp == '>' && isCode) {
                n = n - 1;
                isCode = false;
            } else if (temp == ';' && isHTML) {
                isHTML = false;
            }
            try {
                if (!isCode && !isHTML) {
                    n += String.valueOf(temp).getBytes("GBK").length;
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if (n <= length - 3) {
                result.append(temp);
            } else {
                result.append("...");
                break;
            }
        }
        // 取出截取字符串中的HTML标记
        String temp_result = result.toString().replaceAll("(>)[^<>]*(<?)", "$1$2");
        // 去掉不需要结素标记的HTML标记
        temp_result = temp_result.replaceAll(
                "</?(AREA|BASE|BASEFONT|BODY|BR|COL|COLGROUP|DD|DT|FRAME|HEAD|HR|HTML|IMG|INPUT|ISINDEX|LI|LINK|META|OPTION|P|PARAM|TBODY|TD|TFOOT|TH|THEAD|TR|area|base|basefont|body|br|col|colgroup|dd|dt|frame|head|hr|html|img|input|isindex|li|link|meta|option|p|param|tbody|td|tfoot|th|thead|tr)[^<>]*/?>",
                "");
        // 去掉成对的HTML标记
        temp_result = temp_result.replaceAll("<([a-zA-Z]+)[^<>]*>(.*?)</\\1>", "$2");
        // 用正则表达式取出标记
        Pattern p = Pattern.compile("<([a-zA-Z]+)[^<>]*>");
        Matcher m = p.matcher(temp_result);
        List<String> endHTML = Lists.newArrayList();
        while (m.find()) {
            endHTML.add(m.group(1));
        }
        // 补全不成对的HTML标记
        for (int i = endHTML.size() - 1; i >= 0; i--) {
            result.append("</");
            result.append(endHTML.get(i));
            result.append(">");
        }
        return result.toString();
    }

    public static void main(String[] args) {
        System.out.println(makeAllWordFirstLetterUpperCase("userinfo"));
    }
}

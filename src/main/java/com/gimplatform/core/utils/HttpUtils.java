package com.gimplatform.core.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSONObject;

/**
 * 提供通过HTTP协议获取内容的方法 <br/>
 * 所有提供方法中的params参数在内部不会进行自动的url encode，如果提交参数需要进行url encode，请调用方自行处理
 * @Description: HTTP请求代理工具
 * @author zzd
 */
public class HttpUtils {

    protected static final Logger logger = LogManager.getLogger(HttpUtils.class);
    
    private static int CONNECT_TIMEOUT = 60 * 1000;
    
    private static int READ_TIMEOUT = 60 * 1000;

    /**
     * 支持的Http method
     */
    private static enum HttpMethod {
        POST, DELETE, GET, PUT, HEAD;
    };

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static String invokeUrl(String url, Map params, Map<String, String> headers, int connectTimeout, int readTimeout, String encoding, HttpMethod method) {
        // 构造请求参数字符串
        StringBuilder paramsStr = null;
        if (params != null) {
            paramsStr = new StringBuilder();
            Set<Map.Entry> entries = params.entrySet();
            for (Map.Entry entry : entries) {
                String value = (entry.getValue() != null) ? (String.valueOf(entry.getValue())) : "";
                paramsStr.append(entry.getKey() + "=" + value + "&");
            }
            // 只有POST方法才能通过OutputStream(即form的形式)提交参数
            if (method != HttpMethod.POST) {
                url += "?" + paramsStr.toString();
            }
        }
        logger.info("Http请求:[URL=" + url + "][method=" + method.toString() + "][params=" + paramsStr + "]");

        URL uUrl = null;
        HttpURLConnection conn = null;
        BufferedWriter out = null;
        BufferedReader in = null;
        try {
            // 创建和初始化连接
            uUrl = new URL(url);
            conn = (HttpURLConnection) uUrl.openConnection();
            conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            conn.setRequestMethod(method.toString());
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 设置连接超时时间
            conn.setConnectTimeout(connectTimeout);
            // 设置读取超时时间
            conn.setReadTimeout(readTimeout);
            // 指定请求header参数
            if (headers != null && headers.size() > 0) {
                Set<String> headerSet = headers.keySet();
                for (String key : headerSet) {
                    conn.setRequestProperty(key, headers.get(key));
                }
            }

            if (paramsStr != null && method == HttpMethod.POST) {
                // 发送请求参数
                out = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), encoding));
                out.write(paramsStr.toString());
                out.flush();
            }

            // 返回成功码
            if (conn.getResponseCode() == 200) {
                // 接收返回结果
                StringBuilder result = new StringBuilder();
                in = new BufferedReader(new InputStreamReader(conn.getInputStream(), encoding));
                if (in != null) {
                    String line = "";
                    while ((line = in.readLine()) != null) {
                        result.append(line);
                    }
                }
                return result.toString();
            } else {
                logger.error("调用接口[" + url + "]失败！请求URL：" + url + "，返回码：" + conn.getResponseCode() + "，参数：" + params);
            }
        } catch (Exception e) {
            logger.error("调用接口[" + url + "]失败！请求URL：" + url + "，参数：" + params, e);
            // 处理错误流，提高http连接被重用的几率
            try {
                if (conn != null) {
                    byte[] buf = new byte[100];
                    InputStream es = conn.getErrorStream();
                    if (es != null) {
                        while (es.read(buf) > 0) {
                            ;
                        }
                        es.close();
                    }
                }
            } catch (Exception e1) {
                logger.error(e1.getMessage(), e1);
            }
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            // 关闭连接
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }

    /**
     * post json 数据
     * @param url
     * @param json
     * @param headers
     * @param connectTimeout
     * @param readTimeout
     * @param encoding
     * @param method
     * @return
     */
    private static String invokePostJson(String url, JSONObject json, Map<String, String> headers, int connectTimeout, int readTimeout, String encoding, HttpMethod method) {
        if (json != null)
            logger.info("Http请求:[URL=" + url + "][method=" + method.toString() + "][params=" + json.toString() + "]");
        else
            logger.info("Http请求:[URL=" + url + "][method=" + method.toString() + "]");
        URL uUrl = null;
        HttpURLConnection conn = null;
        BufferedWriter out = null;
        BufferedReader in = null;
        try {
            // 创建和初始化连接
            uUrl = new URL(url);
            conn = (HttpURLConnection) uUrl.openConnection();
            conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            conn.setRequestMethod(method.toString());
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 设置连接超时时间
            conn.setConnectTimeout(connectTimeout);
            // 设置读取超时时间
            conn.setReadTimeout(readTimeout);
            // 指定请求header参数
            if (headers != null && headers.size() > 0) {
                Set<String> headerSet = headers.keySet();
                for (String key : headerSet) {
                    conn.setRequestProperty(key, headers.get(key));
                }
            }

            if (json != null && method == HttpMethod.POST) {
                // 发送请求参数
                out = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), encoding));
                out.write(json.toString());
                out.flush();
            }

            // 返回成功码
            if (conn.getResponseCode() == 200) {
                // 接收返回结果
                StringBuilder result = new StringBuilder();
                in = new BufferedReader(new InputStreamReader(conn.getInputStream(), encoding));
                if (in != null) {
                    String line = "";
                    while ((line = in.readLine()) != null) {
                        result.append(line);
                    }
                }
                return result.toString();
            } else {
                logger.error("调用接口[" + url + "]失败！请求URL：" + url + "，返回码：" + conn.getResponseCode() + "，参数：" + json.toString());
            }
        } catch (Exception e) {
            logger.error("调用接口[" + url + "]失败！请求URL：" + url + "，参数：" + json.toString(), e);
            // 处理错误流，提高http连接被重用的几率
            try {
                if (conn != null) {
                    byte[] buf = new byte[100];
                    InputStream es = conn.getErrorStream();
                    if (es != null) {
                        while (es.read(buf) > 0) {
                            ;
                        }
                        es.close();
                    }
                }
            } catch (Exception e1) {
                logger.error(e1.getMessage(), e1);
            }
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            // 关闭连接
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }

    /**
     * POST方法提交Http请求，语义为“增加” <br/>
     * @param url 资源路径（如果url中已经包含参数，则params应该为null）
     * @param params 参数
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static String post(String url, Map params) {
        return invokeUrl(url, params, null, CONNECT_TIMEOUT, READ_TIMEOUT, "UTF-8", HttpMethod.POST);
    }

    /**
     * POST方法提交Http请求，语义为“增加” <br/>
     * @param url
     * @param params
     * @param headers
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static String post(String url, Map params, Map<String, String> headers) {
        return invokeUrl(url, params, headers, CONNECT_TIMEOUT, READ_TIMEOUT, "UTF-8", HttpMethod.POST);
    }

    /**
     * 使用post发送json数据
     * @param url
     * @param json
     * @param headers
     * @return
     */
    public static String post(String url, JSONObject json, Map<String, String> headers) {
        return invokePostJson(url, json, headers, CONNECT_TIMEOUT, READ_TIMEOUT, "UTF-8", HttpMethod.POST);
    }

    /**
     * POST方法提交Http请求，语义为“增加” <br/>
     * 注意：Http方法中只有POST方法才能使用body来提交内容
     * @param url 资源路径（如果url中已经包含参数，则params应该为null）
     * @param params 参数
     * @param connectTimeout 连接超时时间（单位为ms）
     * @param readTimeout 读取超时时间（单位为ms）
     * @param charset 字符集（一般该为“utf-8”）
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static String post(String url, Map params, int connectTimeout, int readTimeout, String charset) {
        return invokeUrl(url, params, null, connectTimeout, readTimeout, charset, HttpMethod.POST);
    }

    /**
     * POST方法提交Http请求，语义为“增加” <br/>
     * 注意：Http方法中只有POST方法才能使用body来提交内容
     * @param url 资源路径（如果url中已经包含参数，则params应该为null）
     * @param params 参数
     * @param headers 请求头参数
     * @param connectTimeout 连接超时时间（单位为ms）
     * @param readTimeout 读取超时时间（单位为ms）
     * @param charset 字符集（一般该为“utf-8”）
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static String post(String url, Map params, Map<String, String> headers, int connectTimeout, int readTimeout, String charset) {
        return invokeUrl(url, params, headers, connectTimeout, readTimeout, charset, HttpMethod.POST);
    }

    /**
     * GET方法提交Http请求，语义为“查询”
     * @param url 资源路径（如果url中已经包含参数，则params应该为null）
     * @return
     */
    public static String get(String url) {
        return invokeUrl(url, null, null, CONNECT_TIMEOUT, READ_TIMEOUT, "UTF-8", HttpMethod.GET);
    }

    /**
     * GET方法提交Http请求，语义为“查询”
     * @param url
     * @param headers
     * @return
     */
    public static String get(String url, Map<String, String> headers) {
        return invokeUrl(url, null, headers, CONNECT_TIMEOUT, READ_TIMEOUT, "UTF-8", HttpMethod.GET);
    }

    public static String get(String url, Map<String, String> params, Map<String, String> headers) {
        return invokeUrl(url, params, headers, CONNECT_TIMEOUT, READ_TIMEOUT, "UTF-8", HttpMethod.GET);
    }

    /**
     * GET方法提交Http请求，语义为“查询”
     * @param url 资源路径（如果url中已经包含参数，则params应该为null）
     * @param params 参数
     * @param connectTimeout 连接超时时间（单位为ms）
     * @param readTimeout 读取超时时间（单位为ms）
     * @param charset 字符集（一般该为“utf-8”）
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static String get(String url, Map params, int connectTimeout, int readTimeout, String charset) {
        return invokeUrl(url, params, null, connectTimeout, readTimeout, charset, HttpMethod.GET);
    }

    /**
     * GET方法提交Http请求，语义为“查询”
     * @param url 资源路径（如果url中已经包含参数，则params应该为null）
     * @param params 参数
     * @param headers 请求头参数
     * @param connectTimeout 连接超时时间（单位为ms）
     * @param readTimeout 读取超时时间（单位为ms）
     * @param charset 字符集（一般该为“utf-8”）
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static String get(String url, Map params, Map<String, String> headers, int connectTimeout, int readTimeout, String charset) {
        return invokeUrl(url, params, headers, connectTimeout, readTimeout, charset, HttpMethod.GET);
    }

    /**
     * PUT方法提交Http请求，语义为“更改” <br/>
     * 注意：PUT方法也是使用url提交参数内容而非body，所以参数最大长度收到服务器端实现的限制，Resin大概是8K
     * @param url 资源路径（如果url中已经包含参数，则params应该为null）
     * @param params 参数
     * @param connectTimeout 连接超时时间（单位为ms）
     * @param readTimeout 读取超时时间（单位为ms）
     * @param charset 字符集（一般该为“utf-8”）
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static String put(String url, Map params, int connectTimeout, int readTimeout, String charset) {
        return invokeUrl(url, params, null, connectTimeout, readTimeout, charset, HttpMethod.PUT);
    }

    /**
     * PUT方法提交Http请求，语义为“更改” <br/>
     * 注意：PUT方法也是使用url提交参数内容而非body，所以参数最大长度收到服务器端实现的限制，Resin大概是8K
     * @param url 资源路径（如果url中已经包含参数，则params应该为null）
     * @param params 参数
     * @param headers 请求头参数
     * @param connectTimeout 连接超时时间（单位为ms）
     * @param readTimeout 读取超时时间（单位为ms）
     * @param charset 字符集（一般该为“utf-8”）
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static String put(String url, Map params, Map<String, String> headers, int connectTimeout, int readTimeout, String charset) {
        return invokeUrl(url, params, headers, connectTimeout, readTimeout, charset, HttpMethod.PUT);
    }

    /**
     * DELETE方法提交Http请求，语义为“删除”
     * @param url 资源路径（如果url中已经包含参数，则params应该为null）
     * @param params 参数
     * @param connectTimeout 连接超时时间（单位为ms）
     * @param readTimeout 读取超时时间（单位为ms）
     * @param charset 字符集（一般该为“utf-8”）
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static String delete(String url, Map params, int connectTimeout, int readTimeout, String charset) {
        return invokeUrl(url, params, null, connectTimeout, readTimeout, charset, HttpMethod.DELETE);
    }

    /**
     * DELETE方法提交Http请求，语义为“删除”
     * @param url 资源路径（如果url中已经包含参数，则params应该为null）
     * @param params 参数
     * @param headers 请求头参数
     * @param connectTimeout 连接超时时间（单位为ms）
     * @param readTimeout 读取超时时间（单位为ms）
     * @param charset 字符集（一般该为“utf-8”）
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static String delete(String url, Map params, Map<String, String> headers, int connectTimeout, int readTimeout, String charset) {
        return invokeUrl(url, params, headers, connectTimeout, readTimeout, charset, HttpMethod.DELETE);
    }

    /**
     * HEAD方法提交Http请求，语义同GET方法 <br/>
     * 跟GET方法不同的是，用该方法请求，服务端不返回message body只返回头信息，能节省带宽
     * @param url 资源路径（如果url中已经包含参数，则params应该为null）
     * @param params 参数
     * @param connectTimeout 连接超时时间（单位为ms）
     * @param readTimeout 读取超时时间（单位为ms）
     * @param charset 字符集（一般该为“utf-8”）
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static String head(String url, Map params, int connectTimeout, int readTimeout, String charset) {
        return invokeUrl(url, params, null, connectTimeout, readTimeout, charset, HttpMethod.HEAD);
    }

    /**
     * HEAD方法提交Http请求，语义同GET方法 <br/>
     * 跟GET方法不同的是，用该方法请求，服务端不返回message body只返回头信息，能节省带宽
     * @param url 资源路径（如果url中已经包含参数，则params应该为null）
     * @param params 参数
     * @param headers 请求头参数
     * @param connectTimeout 连接超时时间（单位为ms）
     * @param readTimeout 读取超时时间（单位为ms）
     * @param charset 字符集（一般该为“utf-8”）
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static String head(String url, Map params, Map<String, String> headers, int connectTimeout, int readTimeout, String charset) {
        return invokeUrl(url, params, headers, connectTimeout, readTimeout, charset, HttpMethod.HEAD);
    }

    /**
     * 向浏览器发送文件下载，支持断点续传
     * @param file 要下载的文件
     * @param request 请求对象
     * @param response 响应对象
     * @return 返回错误信息，无错误信息返回null
     */
    public static String downFile(File file, HttpServletRequest request, HttpServletResponse response) {
        return downFile(file, request, response, null);
    }

    /**
     * 向浏览器发送文件下载，支持断点续传
     * @param file 要下载的文件
     * @param request 请求对象
     * @param response 响应对象
     * @param fileName 指定下载的文件名
     * @return 返回错误信息，无错误信息返回null
     */
    public static String downFile(File file, HttpServletRequest request, HttpServletResponse response, String fileName) {
        String error = null;
        if (file != null && file.exists()) {
            if (file.isFile()) {
                if (file.length() <= 0) {
                    error = "该文件是一个空文件。";
                }
                if (!file.canRead()) {
                    error = "该文件没有读取权限。";
                }
            } else {
                error = "该文件是一个文件夹。";
            }
        } else {
            error = "文件已丢失或不存在！";
        }
        if (error != null) {
            logger.error("文件下载失败：【File=" + file + "】" + error);
            return error;
        }

        long fileLength = file.length(); // 记录文件大小
        long pastLength = 0; // 记录已下载文件大小
        int rangeSwitch = 0; // 0：从头开始的全文下载；1：从某字节开始的下载（bytes=27000-）；2：从某字节开始到某字节结束的下载（bytes=27000-39000）
        long toLength = 0; // 记录客户端需要下载的字节段的最后一个字节偏移量（比如bytes=27000-39000，则这个值是为39000）
        long contentLength = 0; // 客户端请求的字节总量
        String rangeBytes = ""; // 记录客户端传来的形如“bytes=27000-”或者“bytes=27000-39000”的内容
        RandomAccessFile raf = null; // 负责读取数据
        OutputStream os = null; // 写出数据
        OutputStream out = null; // 缓冲
        byte b[] = new byte[1024]; // 暂存容器

        if (request.getHeader("Range") != null) { // 客户端请求的下载的文件块的开始字节
            response.setStatus(javax.servlet.http.HttpServletResponse.SC_PARTIAL_CONTENT);
            logger.debug("request.getHeader(\"Range\") = " + request.getHeader("Range"));
            rangeBytes = request.getHeader("Range").replaceAll("bytes=", "");
            if (rangeBytes.indexOf('-') == rangeBytes.length() - 1) {// bytes=969998336-
                rangeSwitch = 1;
                rangeBytes = rangeBytes.substring(0, rangeBytes.indexOf('-'));
                pastLength = Long.parseLong(rangeBytes.trim());
                contentLength = fileLength - pastLength; // 客户端请求的是 969998336 之后的字节
            } else { // bytes=1275856879-1275877358
                rangeSwitch = 2;
                String temp0 = rangeBytes.substring(0, rangeBytes.indexOf('-'));
                String temp2 = rangeBytes.substring(rangeBytes.indexOf('-') + 1, rangeBytes.length());
                pastLength = Long.parseLong(temp0.trim()); // bytes=1275856879-1275877358，从第 1275856879 个字节开始下载
                toLength = Long.parseLong(temp2); // bytes=1275856879-1275877358，到第 1275877358 个字节结束
                contentLength = toLength - pastLength; // 客户端请求的是 1275856879-1275877358 之间的字节
            }
        } else { // 从开始进行下载
            contentLength = fileLength; // 客户端要求全文下载
        }
        // 如果设设置了Content-Length，则客户端会自动进行多线程下载。如果不希望支持多线程，则不要设置这个参数。 响应的格式是:
        // Content-Length: [文件的总大小] - [客户端请求的下载的文件块的开始字节]
        // ServletActionContext.getResponse().setHeader("Content- Length", new
        // Long(file.length() - p).toString());
        response.reset(); // 告诉客户端允许断点续传多线程连接下载,响应的格式是:Accept-Ranges: bytes
        if (pastLength != 0) {
            response.setHeader("Accept-Ranges", "bytes");// 如果是第一次下,还没有断点续传,状态是默认的 200,无需显式设置;响应的格式是:HTTP/1.1 200 OK
            // 不是从最开始下载, 响应的格式是: Content-Range: bytes [文件块的开始字节]-[文件的总大小 - 1]/[文件的总大小]
            logger.info("服务器即将开始断点续传...");
            switch (rangeSwitch) {
                case 1: { // 针对 bytes=27000- 的请求
                    String contentRange = new StringBuffer("bytes ").append(new Long(pastLength).toString()).append("-").append(new Long(fileLength - 1).toString()).append("/").append(new Long(fileLength).toString()).toString();
                    response.setHeader("Content-Range", contentRange);
                    break;
                }
                case 2: { // 针对 bytes=27000-39000 的请求
                    String contentRange = rangeBytes + "/" + new Long(fileLength).toString();
                    response.setHeader("Content-Range", contentRange);
                    break;
                }
                default: {
                    break;
                }
            }
        } else {
            logger.info("开始下载文件...");
        }
        try {
            response.addHeader("Content-Disposition", "attachment; filename=\"" + Encodes.urlEncode(StringUtils.isBlank(fileName) ? file.getName() : fileName) + "\"");
            response.setContentType(FileUtils.getContentType(file.getName()));
            response.addHeader("Content-Length", String.valueOf(contentLength));
            os = response.getOutputStream();
            out = new BufferedOutputStream(os);
            raf = new RandomAccessFile(file, "r");
            try {
                switch (rangeSwitch) {
                    case 0: { // 普通下载，或者从头开始的下载 同1
                    }
                    case 1: { // 针对 bytes=27000- 的请求
                        raf.seek(pastLength); // 形如 bytes=969998336- 的客户端请求，跳过 969998336 个字节
                        int n = 0;
                        while ((n = raf.read(b, 0, 1024)) != -1) {
                            out.write(b, 0, n);
                        }
                        break;
                    }
                    case 2: { // 针对 bytes=27000-39000 的请求
                        raf.seek(pastLength); // 形如 bytes=1275856879-1275877358 的客户端请求，找到第 1275856879 个字节
                        int n = 0;
                        long readLength = 0; // 记录已读字节数
                        while (readLength <= contentLength - 1024) {// 大部分字节在这里读取
                            n = raf.read(b, 0, 1024);
                            readLength += 1024;
                            out.write(b, 0, n);
                        }
                        if (readLength <= contentLength) { // 余下的不足 1024 个字节在这里读取
                            n = raf.read(b, 0, (int) (contentLength - readLength));
                            out.write(b, 0, n);
                        }
                        break;
                    }
                    default: {
                        break;
                    }
                }
                out.flush();
                logger.info("文件下载结束");
            } catch (IOException ie) {
                /**
                 * 在写数据的时候， 对于 ClientAbortException 之类的异常， 是因为客户端取消了下载，而服务器端继续向浏览器写入数据时， 抛出这个异常，这个是正常的。 尤其是对于迅雷这种吸血的客户端软件， 明明已经有一个线程在读取 bytes=1275856879-1275877358， 如果短时间内没有读取完毕，迅雷会再启第二个、第三个。。。线程来读取相同的字节段，
                 * 直到有一个线程读取完毕，迅雷会 KILL 掉其他正在下载同一字节段的线程， 强行中止字节读出，造成服务器抛 ClientAbortException。 所以，我们忽略这种异常
                 */
                logger.debug("提醒：向客户端传输时出现IO异常，但此异常是允许的，有可能客户端取消了下载，导致此异常，不用关心！");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return null;
    }
    
    /**
     * 查询JDK默认支持的SSL/TSL版本
     */
    public static void checkSSLAndTSLVersion() {
        try {
            SSLContext context = SSLContext.getInstance("TLS");  
            context.init(null, null, null);  
            SSLSocketFactory factory = (SSLSocketFactory) context.getSocketFactory();  
            SSLSocket socket = (SSLSocket) factory.createSocket();  
            String[] protocols = socket.getSupportedProtocols();  
            System.out.println("Supported Protocols: " + protocols.length);  
            for (int i = 0; i < protocols.length; i++) {  
                System.out.println(" " + protocols[i]);  
            }  
            protocols = socket.getEnabledProtocols();  
            System.out.println("Enabled Protocols: " + protocols.length);  
            for (int i = 0; i < protocols.length; i++) {  
                System.out.println(" " + protocols[i]);  
            }  
        } catch (Exception e) {
        }  
    }
}

package com.gimplatform.core.generator;

import freemarker.ext.dom.NodeModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.InputSource;

import com.gimplatform.core.generator.provider.db.table.TableFactory;
import com.gimplatform.core.generator.utils.SqlExecutorHelper;
import com.gimplatform.core.generator.utils.XMLHelper;
import com.gimplatform.core.utils.FileUtils;
import com.gimplatform.core.utils.StringUtils;

import javax.swing.*;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 生成器模板控制器,用于模板中可以控制生成器执行相关控制操作 如: 是否覆盖目标文件
 * 
 * <pre>
 * 使用方式:
 * 可以在freemarker或是veloctiy中直接控制模板的生成
 * ${gg.generateFile('d:/g_temp.log','info_from_generator')}
 * ${gg.setIgnoreOutput(true)}
 * </pre>
 * 
 * ${gg.setIgnoreOutput(true)}将设置为true如果不生成
 */
public class GeneratorControl {

    private static final Logger logger = LogManager.getLogger(GeneratorControl.class);

    private boolean isOverride = GeneratorProperties.getProperty("isOverride", true);
    // private boolean isAppend = false; // no pass
    private boolean ignoreOutput = false;
    private boolean isMergeIfExists = true; // no pass
    private String mergeLocation;
    private String outRoot;
    private String outputEncoding;
    private String sourceFile;
    private String sourceDir;
    private String sourceFileName;
    private String sourceEncoding; // no pass //? 难道process两次确定sourceEncoding

    public NodeModel loadXml(String file) {
        return loadXml(file, true);
    }

    public NodeModel loadXml(String file, boolean removeXmlNamespace) {
        try {
            if (removeXmlNamespace) {
                InputStream forEncodingInput = FileUtils.getInputStream(file);
                String encoding = XMLHelper.getXMLEncoding(forEncodingInput);
                forEncodingInput.close();

                InputStream input = FileUtils.getInputStream(file);
                String xml = FileUtils.toString(encoding, input);
                xml = XMLHelper.removeXmlns(xml);
                input.close();
                return NodeModel.parse(new InputSource(new StringReader(xml.trim())));
            } else {
                return NodeModel.parse(new InputSource(FileUtils.getInputStream(file)));
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("loadXml error,file:" + file, e);
        }
    }

    public Properties loadProperties(String file) {
        try {
            Properties p = new Properties();
            InputStream in = FileUtils.getInputStream(file);
            if (file.endsWith(".xml")) {
                p.loadFromXML(in);
            } else {
                p.load(in);
            }
            in.close();
            return p;
        } catch (Exception e) {
            throw new IllegalArgumentException("loadProperties error,file:" + file, e);
        }
    }

    public void generateFile(String outputFile, String content) {
        generateFile(outputFile, content, false);
    }

    /**
     * 生成文件
     * @param outputFile
     * @param content
     * @param append
     */
    public void generateFile(String outputFile, String content, boolean append) {
        try {
            String realOutputFile = null;
            if (new File(outputFile).isAbsolute()) {
                realOutputFile = outputFile;
            } else {
                realOutputFile = new File(getOutRoot(), outputFile).getAbsolutePath();
            }

            if (deleteGeneratedFile) {
                logger.info("[delete gg.generateFile()] file:" + realOutputFile + " by template:" + getSourceFile());
                new File(realOutputFile).delete();
            } else {
                FileUtils.createFile(realOutputFile);
                logger.info("[gg.generateFile()] outputFile:" + realOutputFile + " append:" + append + " by template:" + getSourceFile());
                FileUtils.writeToFile(realOutputFile, content, getOutputEncoding(), append);
            }
        } catch (Exception e) {
            logger.error("gg.generateFile() occer error,outputFile:" + outputFile + " caused by:" + e, e);
            throw new RuntimeException("gg.generateFile() occer error,outputFile:" + outputFile + " caused by:" + e, e);
        }
    }

    public boolean isOverride() {
        return isOverride;
    }

    /**
     * 如果目标文件存在,控制是否要覆盖文件
     * @param isOverride
     */
    public void setOverride(boolean isOverride) {
        this.isOverride = isOverride;
    }

    public boolean isIgnoreOutput() {
        return ignoreOutput;
    }

    /**
     * 控制是否要生成文件
     * @param ignoreOutput
     */
    public void setIgnoreOutput(boolean ignoreOutput) {
        this.ignoreOutput = ignoreOutput;
    }

    public boolean isMergeIfExists() {
        return isMergeIfExists;
    }

    public void setMergeIfExists(boolean isMergeIfExists) {
        this.isMergeIfExists = isMergeIfExists;
    }

    public String getMergeLocation() {
        return mergeLocation;
    }

    public void setMergeLocation(String mergeLocation) {
        this.mergeLocation = mergeLocation;
    }

    public String getOutRoot() {
        return outRoot;
    }

    /**
     * 生成的输出根目录
     * @param outRoot
     */
    public void setOutRoot(String outRoot) {
        this.outRoot = outRoot;
    }

    public String getOutputEncoding() {
        return outputEncoding;
    }

    /**
     * 设置输出encoding
     * @param outputEncoding
     */
    public void setOutputEncoding(String outputEncoding) {
        this.outputEncoding = outputEncoding;
    }

    /**
     * 得到源文件
     * @return
     */
    public String getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    /**
     * 得到源文件所在目录
     * @return
     */
    public String getSourceDir() {
        return sourceDir;
    }

    public void setSourceDir(String sourceDir) {
        this.sourceDir = sourceDir;
    }

    /**
     * 得到源文件的文件名称
     * @return
     */
    public String getSourceFileName() {
        return sourceFileName;
    }

    public void setSourceFileName(String sourceFileName) {
        this.sourceFileName = sourceFileName;
    }

    /**
     * 得到源文件的encoding
     * @return
     */
    public String getSourceEncoding() {
        return sourceEncoding;
    }

    public void setSourceEncoding(String sourceEncoding) {
        this.sourceEncoding = sourceEncoding;
    }

    /**
     * 得到property,查到不到则使用defaultValue
     * @param key
     * @param defaultValue
     * @return
     */
    public String getProperty(String key, String defaultValue) {
        return GeneratorProperties.getProperty(key, defaultValue);
    }

    /**
     * 让用户输入property,windows则弹出输入框，linux则为命令行输入
     * @param key
     * @return
     * @throws IOException
     */
    public String getInputProperty(String key) throws IOException {
        return getInputProperty(key, "Please input value for " + key + ":");
    }

    public String getInputProperty(String key, String message) throws IOException {
        String v = GeneratorProperties.getProperty(key);
        if (v == null) {
            if (StringUtils.isWindowsOS) {
                v = JOptionPane.showInputDialog(null, message, "template:" + getSourceFileName(), JOptionPane.OK_OPTION);
            } else {
                System.out.print("template:" + getSourceFileName() + "," + message);
                v = new BufferedReader(new InputStreamReader(System.in)).readLine();
            }
            GeneratorProperties.setProperty(key, v);
        }
        return v;
    }

    public List<Map<String, Object>> queryForList(String sql, int limit) throws SQLException {
        Connection conn = TableFactory.getInstance().getConnection();
        return SqlExecutorHelper.queryForList(conn, sql, limit);
    }

    boolean deleteGeneratedFile = false;
}

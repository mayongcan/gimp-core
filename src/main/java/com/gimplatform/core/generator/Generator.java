package com.gimplatform.core.generator;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.gimplatform.core.generator.utils.FreemarkerUtils;
import com.gimplatform.core.generator.utils.GeneratorException;
import com.gimplatform.core.generator.utils.GeneratorUtils;
import com.gimplatform.core.utils.BeanUtils;
import com.gimplatform.core.utils.FileUtils;
import com.gimplatform.core.utils.StringUtils;

/**
 * 代码生成器核心引擎 主要提供以下两个方法供外部使用
 * 
 * <pre>
 * generateBy() 用于生成文件
 * deleteBy() 用于删除生成的文件
 * </pre>
 * 
 * @author zzd
 */
public class Generator {

    private static final Logger logger = LogManager.getLogger(Generator.class);

    private List<String> ignoreList = new ArrayList<String>();
    private static final String GENERATOR_INSERT_LOCATION = "generator-insert-location";
    private List<File> templateRootDirs = new ArrayList<File>();
    private String outRootDir;
    private boolean ignoreTemplateGenerateException = true;
    private String removeExtensions = GeneratorProperties.getProperty("removeExtensions", "");
    private boolean isCopyBinaryFile = true;

    private String includes = GeneratorProperties.getProperty("includes"); // 需要处理的模板，使用逗号分隔符,示例值:
                                                                           // java_src/**,java_test/**
    private String excludes = GeneratorProperties.getProperty("excludes"); // 不需要处理的模板，使用逗号分隔符,示例值:
                                                                           // java_src/**,java_test/**
    private String sourceEncoding = GeneratorProperties.getProperty("sourceEncoding", "UTF-8");
    private String outputEncoding = GeneratorProperties.getProperty("outputEncoding", "UTF-8");

    public Generator() {
        ignoreList.add(".svn");
        ignoreList.add("CVS");
        ignoreList.add(".cvsignore");
        ignoreList.add(".copyarea.db");
        ignoreList.add("SCCS");
        ignoreList.add("vssver.scc");
        ignoreList.add(".DS_Store");
        ignoreList.add(".git");
        ignoreList.add(".gitignore");
    }

    public void setTemplateRootDir(File templateRootDir) {
        setTemplateRootDirs(new File[] { templateRootDir });
    }

    public void setTemplateRootDirs(File[] templateRootDirs) {
        this.templateRootDirs = Arrays.asList(templateRootDirs);
    }

    public void addTemplateRootDir(File f) {
        templateRootDirs.add(f);
    }

    public boolean isIgnoreTemplateGenerateException() {
        return ignoreTemplateGenerateException;
    }

    public void setIgnoreTemplateGenerateException(boolean ignoreTemplateGenerateException) {
        this.ignoreTemplateGenerateException = ignoreTemplateGenerateException;
    }

    public boolean isCopyBinaryFile() {
        return isCopyBinaryFile;
    }

    public void setCopyBinaryFile(boolean isCopyBinaryFile) {
        this.isCopyBinaryFile = isCopyBinaryFile;
    }

    public String getSourceEncoding() {
        return sourceEncoding;
    }

    public void setSourceEncoding(String sourceEncoding) {
        if (sourceEncoding == null)
            throw new IllegalArgumentException("sourceEncoding must be not null");
        this.sourceEncoding = sourceEncoding;
    }

    public String getOutputEncoding() {
        return outputEncoding;
    }

    public void setOutputEncoding(String outputEncoding) {
        if (outputEncoding == null)
            throw new IllegalArgumentException("outputEncoding must be not null");
        this.outputEncoding = outputEncoding;
    }

    public void setIncludes(String includes) {
        this.includes = includes;
    }

    /** 设置不处理的模板路径,可以使用ant类似的值,使用逗号分隔，示例值： **\*.ignore */
    public void setExcludes(String excludes) {
        this.excludes = excludes;
    }

    public void setOutRootDir(String rootDir) {
        if (StringUtils.isBlank(rootDir))
            throw new IllegalArgumentException("outRootDir must be not null");
        this.outRootDir = rootDir;
        FileUtils.createDirectory(rootDir);
    }

    public String getOutRootDir() {
        return outRootDir;
    }

    public void setRemoveExtensions(String removeExtensions) {
        this.removeExtensions = removeExtensions;
    }

    public void deleteOutRootDir() throws IOException {
        if (StringUtils.isBlank(getOutRootDir()))
            throw new IllegalStateException("'outRootDir' property must be not null.");
        FileUtils.deleteDirectory(getOutRootDir());
    }

    /**
     * 生成文件
     * @param templateModel 生成器模板可以引用的变量
     * @param filePathModel 文件路径可以引用的变量
     * @throws Exception
     */
    public Generator generateBy(Map<String, Object> templateModel, Map<String, Object> filePathModel) throws Exception {
        processTemplateRootDirs(templateModel, filePathModel, false);
        return this;
    }

    /**
     * 删除生成的文件
     * @param templateModel 生成器模板可以引用的变量
     * @param filePathModel 文件路径可以引用的变量
     * @return
     * @throws Exception
     */
    public Generator deleteBy(Map<String, Object> templateModel, Map<String, Object> filePathModel) throws Exception {
        processTemplateRootDirs(templateModel, filePathModel, true);
        return this;
    }

    private void processTemplateRootDirs(Map<String, Object> templateModel, Map<String, Object> filePathModel, boolean isDelete) throws Exception {
        if (StringUtils.isBlank(getOutRootDir()))
            throw new IllegalStateException("'outRootDir'参数不能为空");
        if (templateRootDirs.size() == 0)
            throw new IllegalStateException("'templateRootDirs'参数不能为空");
        GeneratorException ge = new GeneratorException("generator occer error, Generator BeanInfo:" + BeanUtils.describe(this));
        for (int i = 0; i < this.templateRootDirs.size(); i++) {
            File templateRootDir = (File) templateRootDirs.get(i);
            if (templateRootDir.exists()) {
                List<Exception> exceptions = scanTemplatesAndProcess(templateRootDir, templateModel, filePathModel, isDelete);
                ge.addAll(exceptions);
            } else {
                logger.error("模板目录不存在:" + templateRootDir.getAbsolutePath());
                continue;
            }
        }
        if (!ge.exceptions.isEmpty())
            throw ge;
    }

    private List<Exception> scanTemplatesAndProcess(File templateRootDir, Map<String, Object> templateModel, Map<String, Object> filePathModel, boolean isDelete) throws Exception {
        if (templateRootDir == null)
            throw new IllegalStateException("'templateRootDir' must be not null");
        logger.info("开始扫描所有模板文件[templateRootDir=" + templateRootDir.getAbsolutePath() + "][outRootDir=" + new File(outRootDir).getAbsolutePath() + "]");

        List<File> srcFiles = searchAllNotIgnoreFile(templateRootDir);
        List<Exception> exceptions = new ArrayList<Exception>();
        for (int i = 0; i < srcFiles.size(); i++) {
            File srcFile = srcFiles.get(i);
            try {
                if (isDelete) {
                    new TemplateProcessor().executeDelete(templateRootDir, templateModel, filePathModel, srcFile);
                } else {
                    new TemplateProcessor().executeGenerate(templateRootDir, templateModel, filePathModel, srcFile);
                }
            } catch (Exception e) {
                if (ignoreTemplateGenerateException) {
                    logger.error("iggnore generate error,template is:" + srcFile + " cause:" + e);
                    exceptions.add(e);
                } else {
                    throw e;
                }
            }
        }
        return exceptions;
    }

    private List<File> searchAllNotIgnoreFile(File dir) throws IOException {
        ArrayList<File> arrayList = new ArrayList<File>();
        searchAllNotIgnoreFile(dir, arrayList);
        Collections.sort(arrayList, new Comparator<File>() {
            public int compare(File o1, File o2) {
                return o1.getAbsolutePath().compareTo(o2.getAbsolutePath());
            }
        });
        return arrayList;
    }

    private void searchAllNotIgnoreFile(File dir, List<File> collector) throws IOException {
        collector.add(dir);
        if ((!dir.isHidden() && dir.isDirectory()) && !isIgnoreFile(dir)) {
            File[] subFiles = dir.listFiles();
            if (subFiles != null) {
                for (int i = 0; i < subFiles.length; i++) {
                    searchAllNotIgnoreFile(subFiles[i], collector);
                }
            }
        }
    }

    private boolean isIgnoreFile(File file) {
        for (int i = 0; i < ignoreList.size(); i++) {
            if (file.getName().equals(ignoreList.get(i))) {
                return true;
            }
        }
        return false;
    }

    private class TemplateProcessor {
        private GeneratorControl generatorControl = new GeneratorControl();

        private void executeGenerate(File templateRootDir, Map<String, Object> templateModel, Map<String, Object> filePathModel, File srcFile) throws SQLException, IOException, TemplateException {
            String templateFile = FileUtils.getRelativePath(templateRootDir, srcFile);
            if (GeneratorUtils.isIgnoreTemplateProcess(srcFile, templateFile, includes, excludes)) {
                return;
            }

            if (isCopyBinaryFile && FileUtils.isBinaryFile(srcFile)) {
                String outputFilepath = proceeForOutputFilepath(filePathModel, templateFile);
                logger.info("[copy binary file by extention] from:" + srcFile + " => " + new File(getOutRootDir(), outputFilepath));
                FileUtils.copyAndClose(new FileInputStream(srcFile), new FileOutputStream(new File(getOutRootDir(), outputFilepath)));
                return;
            }

            String outputFilepath = null;
            try {
                outputFilepath = proceeForOutputFilepath(filePathModel, templateFile);

                initGeneratorControlProperties(srcFile);
                processTemplateForGeneratorControl(templateModel, templateFile);

                if (generatorControl.isIgnoreOutput()) {
                    logger.info("[not generate] by generatorControl.isIgnoreOutput()=true on template:" + templateFile);
                    return;
                }

                if (outputFilepath != null) {
                    generateNewFileOrInsertIntoFile(templateFile, outputFilepath, templateModel);
                }
            } catch (Exception e) {
                throw new RuntimeException("generate oucur error,templateFile is:" + templateFile + " => " + outputFilepath + " cause:" + e, e);
            }
        }

        private void executeDelete(File templateRootDir, Map<String, Object> templateModel, Map<String, Object> filePathModel, File srcFile) throws SQLException, IOException, TemplateException {
            String templateFile = FileUtils.getRelativePath(templateRootDir, srcFile);
            if (GeneratorUtils.isIgnoreTemplateProcess(srcFile, templateFile, includes, excludes)) {
                return;
            }
            initGeneratorControlProperties(srcFile);
            generatorControl.deleteGeneratedFile = true;
            processTemplateForGeneratorControl(templateModel, templateFile);
            String outputFilepath = proceeForOutputFilepath(filePathModel, templateFile);
            logger.info("[delete file] file:" + new File(generatorControl.getOutRoot(), outputFilepath).getAbsolutePath());
            new File(generatorControl.getOutRoot(), outputFilepath).delete();
        }

        private void initGeneratorControlProperties(File srcFile) throws SQLException {
            generatorControl.setSourceFile(srcFile.getAbsolutePath());
            generatorControl.setSourceFileName(srcFile.getName());
            generatorControl.setSourceDir(srcFile.getParent());
            generatorControl.setOutRoot(getOutRootDir());
            generatorControl.setOutputEncoding(outputEncoding);
            generatorControl.setSourceEncoding(sourceEncoding);
            generatorControl.setMergeLocation(GENERATOR_INSERT_LOCATION);
        }

        private void processTemplateForGeneratorControl(Map<String, Object> templateModel, String templateFile) throws IOException, TemplateException {
            templateModel.put("generatorControl", generatorControl);
            Template template = getFreeMarkerTemplate(templateFile);
            template.process(templateModel, FileUtils.NULL_WRITER);
        }

        /**
         * 处理文件路径的变量变成输出路径
         * @param filePathModel
         * @param templateFile
         * @return
         * @throws IOException
         */
        private String proceeForOutputFilepath(Map<String, Object> filePathModel, String templateFile) throws IOException {
            String outputFilePath = templateFile;

            // TODO 删除兼容性的@testExpression
            int testExpressionIndex = -1;
            if ((testExpressionIndex = templateFile.indexOf('@')) != -1) {
                outputFilePath = templateFile.substring(0, testExpressionIndex);
                String testExpressionKey = templateFile.substring(testExpressionIndex + 1);
                Object expressionValue = filePathModel.get(testExpressionKey);
                if (expressionValue == null) {
                    System.err.println("[not-generate] WARN: test expression is null by key:[" + testExpressionKey + "] on template:[" + templateFile + "]");
                    return null;
                }
                if (!"true".equals(String.valueOf(expressionValue))) {
                    logger.info("[not-generate]\t test expression '@" + testExpressionKey + "' is false,template:" + templateFile);
                    return null;
                }
            }

            for (String removeExtension : removeExtensions.split(",")) {
                if (outputFilePath.endsWith(removeExtension)) {
                    outputFilePath = outputFilePath.substring(0, outputFilePath.length() - removeExtension.length());
                    break;
                }
            }
            Configuration conf = GeneratorUtils.newFreeMarkerConfiguration(templateRootDirs, sourceEncoding, "/filepath/processor/");
            return FreemarkerUtils.processTemplateString(outputFilePath, filePathModel, conf);
        }

        private Template getFreeMarkerTemplate(String templateName) throws IOException {
            return GeneratorUtils.newFreeMarkerConfiguration(templateRootDirs, sourceEncoding, templateName).getTemplate(templateName);
        }

        private void generateNewFileOrInsertIntoFile(String templateFile, String outputFilepath, Map<String, Object> templateModel) throws Exception {
            Template template = getFreeMarkerTemplate(templateFile);
            template.setOutputEncoding(generatorControl.getOutputEncoding());

            FileUtils.createDirectory(generatorControl.getOutRoot());
            File absoluteOutputFilePath = new File(generatorControl.getOutRoot(), outputFilepath);
            if (absoluteOutputFilePath.exists()) {
                StringWriter newFileContentCollector = new StringWriter();
                // 判断是否需要插入内容
                if (GeneratorUtils.isFoundInsertLocation(generatorControl, template, templateModel, absoluteOutputFilePath, newFileContentCollector)) {
                    logger.info("插入内容到文件:" + outputFilepath);
                    FileUtils.writeToFile(absoluteOutputFilePath.getAbsolutePath(), newFileContentCollector.toString(), generatorControl.getOutputEncoding(), false);
                    return;
                }
            }
            // 此处决定是否覆盖
            if (absoluteOutputFilePath.exists() && !generatorControl.isOverride()) {
                logger.info("文件已存在，不进行覆盖文件操作(若要覆盖文件，则设置isOverride=true) " + outputFilepath);
                return;
            }

            // 这里再判断，文件不存在则生成一个,否则会报文件不存在的错误
            if (!absoluteOutputFilePath.exists()) {
                FileUtils.createFile(absoluteOutputFilePath.getAbsolutePath());
            }
            logger.info("根据模板生成文件 " + templateFile + " ==> " + outputFilepath);
            FreemarkerUtils.processTemplate(template, templateModel, absoluteOutputFilePath, generatorControl.getOutputEncoding());
        }
    }
}

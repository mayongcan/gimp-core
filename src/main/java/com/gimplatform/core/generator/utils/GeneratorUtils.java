package com.gimplatform.core.generator.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.gimplatform.core.generator.GeneratorContext;
import com.gimplatform.core.generator.GeneratorControl;
import com.gimplatform.core.generator.GeneratorModel;
import com.gimplatform.core.generator.GeneratorProperties;
import com.gimplatform.core.generator.provider.db.sql.model.Sql;
import com.gimplatform.core.generator.provider.db.table.model.Table;
import com.gimplatform.core.generator.provider.model.JavaClass;
import com.gimplatform.core.generator.utils.type.DatabaseTypeUtils;
import com.gimplatform.core.utils.BeanUtils;

import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class GeneratorUtils {

    private static final Logger logger = LogManager.getLogger(GeneratorUtils.class);

    public static GeneratorModel newFromTable(Table table) {
        Map<String, Object> templateModel = new HashMap<String, Object>();
        templateModel.put("table", table);
        setShareVars(templateModel);

        Map<String, Object> filePathModel = new HashMap<String, Object>();
        setShareVars(filePathModel);
        filePathModel.putAll(BeanUtils.describe(table));
        return new GeneratorModel(templateModel, filePathModel);
    }

    public static GeneratorModel newFromSql(Sql sql) throws Exception {
        Map<String, Object> templateModel = new HashMap<String, Object>();
        templateModel.put("sql", sql);
        setShareVars(templateModel);

        Map<String, Object> filePathModel = new HashMap<String, Object>();
        setShareVars(filePathModel);
        filePathModel.putAll(BeanUtils.describe(sql));
        return new GeneratorModel(templateModel, filePathModel);
    }

    @SuppressWarnings("rawtypes")
    public static GeneratorModel newFromClass(Class clazz) {
        Map<String, Object> templateModel = new HashMap<String, Object>();
        templateModel.put("clazz", new JavaClass(clazz));
        setShareVars(templateModel);

        Map<String, Object> filePathModel = new HashMap<String, Object>();
        setShareVars(filePathModel);
        filePathModel.putAll(BeanUtils.describe(new JavaClass(clazz)));
        return new GeneratorModel(templateModel, filePathModel);
    }

    public static GeneratorModel newFromMap(Map<String, Object> params) {
        Map<String, Object> templateModel = new HashMap<String, Object>();
        templateModel.putAll(params);
        setShareVars(templateModel);

        Map<String, Object> filePathModel = new HashMap<String, Object>();
        setShareVars(filePathModel);
        filePathModel.putAll(params);
        return new GeneratorModel(templateModel, filePathModel);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void setShareVars(Map templateModel) {
        templateModel.putAll(GeneratorProperties.getProperties());
        templateModel.putAll(System.getProperties());
        templateModel.put("env", System.getenv());
        templateModel.put("now", new Date());
        templateModel.put("databaseType", getDatabaseType("databaseType"));
        templateModel.putAll(GeneratorContext.getContext());
    }

    private static String getDatabaseType(String key) {
        return GeneratorProperties.getProperty(key, DatabaseTypeUtils.getDatabaseTypeByJdbcDriver(GeneratorProperties.getProperty("jdbc.driver")));
    }

    public static boolean isIgnoreTemplateProcess(File srcFile, String templateFile, String includes, String excludes) {
        // 判断是否是目录和隐藏文件
        if (srcFile.isDirectory() || srcFile.isHidden())
            return true;
        // 判断是否为空
        if (templateFile.trim().equals(""))
            return true;
        if (srcFile.getName().toLowerCase().endsWith(".include")) {
            logger.info("忽略以[.include]为后缀的模板文件[templateFile=" + templateFile + "]");
            return true;
        }
        templateFile = templateFile.replace('\\', '/');
        if (excludes != null) {
            String[] excludeArray = excludes.split(",");
            for (String exclude : excludeArray) {
                if (new AntPathMatcher().match(exclude.replace('\\', '/'), templateFile))
                    return true;
            }
        }
        if (includes == null)
            return false;
        String[] includesArray = includes.split(",");
        for (String include : includesArray) {
            if (new AntPathMatcher().match(include.replace('\\', '/'), templateFile))
                return false;
        }
        return true;
    }

    /**
     * 根据标记"generator-insert-location" 插入生成内容
     * @param generatorControl
     * @param template
     * @param model
     * @param outputFile
     * @param newFileContent
     * @return
     * @throws IOException
     * @throws TemplateException
     */
    public static boolean isFoundInsertLocation(GeneratorControl generatorControl, Template template, Map<String, Object> model, File outputFile, StringWriter newFileContent) throws IOException, TemplateException {
        LineNumberReader reader = new LineNumberReader(new FileReader(outputFile));
        String line = null;
        boolean isFoundInsertLocation = false;

        // FIXME 持续性的重复生成会导致out of memory
        PrintWriter writer = new PrintWriter(newFileContent);
        while ((line = reader.readLine()) != null) {
            writer.println(line);
            // only insert once
            if (!isFoundInsertLocation && line.indexOf(generatorControl.getMergeLocation()) >= 0) {
                template.process(model, writer);
                writer.println();
                isFoundInsertLocation = true;
            }
        }
        writer.close();
        reader.close();
        return isFoundInsertLocation;
    }

    public static Configuration newFreeMarkerConfiguration(List<File> templateRootDirs, String defaultEncoding, String templateName) throws IOException {
        Configuration conf = new Configuration(Configuration.getVersion());

        FileTemplateLoader[] templateLoaders = new FileTemplateLoader[templateRootDirs.size()];
        for (int i = 0; i < templateRootDirs.size(); i++) {
            templateLoaders[i] = new FileTemplateLoader((File) templateRootDirs.get(i));
        }
        MultiTemplateLoader multiTemplateLoader = new MultiTemplateLoader(templateLoaders);

        conf.setTemplateLoader(multiTemplateLoader);
        conf.setNumberFormat("###############");
        conf.setBooleanFormat("true,false");
        conf.setDefaultEncoding(defaultEncoding);
        List<String> autoIncludes = getParentPaths(templateName, "macro.include");
        List<String> availableAutoInclude = FreemarkerUtils.getAvailableAutoInclude(conf, autoIncludes);
        conf.setAutoIncludes(availableAutoInclude);
        logger.info("set Freemarker.autoIncludes:" + availableAutoInclude + " for templateName:" + templateName + " autoIncludes:" + autoIncludes);
        return conf;
    }

    public static List<String> getParentPaths(String templateName, String suffix) {
        String array[] = templateName.split("\\/");
        List<String> list = new ArrayList<String>();
        list.add(suffix);
        list.add(File.separator + suffix);
        String path = "";
        for (int i = 0; i < array.length; i++) {
            path = path + File.separator + array[i];
            list.add(path + File.separator + suffix);
        }
        return list;
    }
}

package com.gimplatform.core.generator;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.gimplatform.core.generator.provider.db.sql.model.Sql;
import com.gimplatform.core.generator.provider.db.table.TableFactory;
import com.gimplatform.core.generator.provider.db.table.model.Table;
import com.gimplatform.core.generator.utils.GeneratorException;
import com.gimplatform.core.generator.utils.GeneratorUtils;

public class GeneratorApi {

	private static final Logger logger = LogManager.getLogger(GeneratorApi.class);

	public Generator generator = new Generator();

	public GeneratorApi() {
		generator.setOutRootDir(GeneratorProperties.getProperty("outRoot"));
	}

	/**
	 * 输出所有数据表名
	 * @throws Exception
	 */
	public void printAllTableNames() throws Exception {
		PrintUtils.printAllTableNames(TableFactory.getInstance().getAllTables());
	}

	/**
	 * 删除输出目录
	 * @throws IOException
	 */
	public void deleteOutRootDir() throws IOException {
		generator.deleteOutRootDir();
	}

	public void generateByAllTable(String templateRootDir) throws Exception {
		new ProcessUtils().processByAllTable(templateRootDir, false);
	}

	public void generateByAllTable(String templateRootDir, String wildcard) throws Exception {
		new ProcessUtils().processByAllTable(templateRootDir, false, wildcard);
	}

	public void deleteByAllTable(String templateRootDir) throws Exception {
		new ProcessUtils().processByAllTable(templateRootDir, true);
	}

	public void generateByTable(String tableName, String templateRootDir) throws Exception {
		new ProcessUtils().processByTable(tableName, templateRootDir, false);
	}

	public void deleteByTable(String tableName, String templateRootDir) throws Exception {
		new ProcessUtils().processByTable(tableName, templateRootDir, true);
	}

	@SuppressWarnings("rawtypes")
	public void generateByClass(Class clazz, String templateRootDir) throws Exception {
		new ProcessUtils().processByClass(clazz, templateRootDir, false);
	}

	@SuppressWarnings("rawtypes")
	public void deleteByClass(Class clazz, String templateRootDir) throws Exception {
		new ProcessUtils().processByClass(clazz, templateRootDir, true);
	}

	public void generateBySql(Sql sql, String templateRootDir) throws Exception {
		new ProcessUtils().processBySql(sql, templateRootDir, false);
	}

	public void deleteBySql(Sql sql, String templateRootDir) throws Exception {
		new ProcessUtils().processBySql(sql, templateRootDir, true);
	}

	public void generateByMap(Map<String, Object> map, String templateRootDir) throws Exception {
		new ProcessUtils().processByMap(map, templateRootDir, false);
	}

	public void deleteByMap(Map<String, Object> map, String templateRootDir) throws Exception {
		new ProcessUtils().processByMap(map, templateRootDir, true);
	}

	private Generator getGenerator(String templateRootDir) {
		generator.setTemplateRootDir(new File(templateRootDir).getAbsoluteFile());
		return generator;
	}

	public class ProcessUtils {

		public void processByTable(String tableName, String templateRootDir, boolean isDelete) throws Exception {
			if ("*".equals(tableName)) {
				generateByAllTable(templateRootDir);
				return;
			} else if (null != tableName && tableName.contains("*")) {
				generateByAllTable(templateRootDir, tableName);
				return;
			}
			Generator g = getGenerator(templateRootDir);
			Table table = TableFactory.getInstance().getTable(tableName);
			try {
				processByTable(g, table, isDelete);
			} catch (GeneratorException ge) {
				PrintUtils.printExceptionsSumary(ge.getMessage(), getGenerator(templateRootDir).getOutRootDir(), ge.getExceptions());
			}
		}

		public void processByAllTable(String templateRootDir, boolean isDelete) throws Exception {
			List<Table> tables = TableFactory.getInstance().getAllTables();
			List<Exception> exceptions = new ArrayList<Exception>();
			for (int i = 0; i < tables.size(); i++) {
				try {
					processByTable(getGenerator(templateRootDir), tables.get(i), isDelete);
				} catch (GeneratorException ge) {
					exceptions.addAll(ge.getExceptions());
				}
			}
			PrintUtils.printExceptionsSumary("", getGenerator(templateRootDir).getOutRootDir(), exceptions);
		}

		public void processByAllTable(String templateRootDir, boolean isDelete, String wildcard) throws Exception {
			List<Table> tables = TableFactory.getInstance().getAllTables();
			List<Exception> exceptions = new ArrayList<Exception>();
			for (int i = 0; i < tables.size(); i++) {
				try {
					Table table = tables.get(i);
					String sqlName = table.getSqlName();
					if (null != sqlName && matchWildCard(sqlName, wildcard)) {
						processByTable(getGenerator(templateRootDir), table, isDelete);
					} else {
						logger.info("通配符[" + wildcard + "]与当前表名不匹配: " + sqlName);
					}
				} catch (GeneratorException ge) {
					exceptions.addAll(ge.getExceptions());
				}
			}
			PrintUtils.printExceptionsSumary("", getGenerator(templateRootDir).getOutRootDir(), exceptions);
		}

		/**
		 * 是否匹配通配符
		 * @param sqlName
		 * @param wildcard
		 * @return
		 */
		private boolean matchWildCard(String sqlName, String wildcard) {
			Pattern regex = Pattern.compile("[^*]+|(\\*)");
			Matcher m = regex.matcher(wildcard);
			StringBuffer b = new StringBuffer();
			while (m.find()) {
				if (m.group(1) != null)
					m.appendReplacement(b, ".*");
				else
					m.appendReplacement(b, "\\\\Q" + m.group(0) + "\\\\E");
			}
			m.appendTail(b);
			String targetRegexStr = b.toString();
			return sqlName.matches(targetRegexStr);
		}

		public void processByTable(Generator g, Table table, boolean isDelete) throws Exception {
			String sqlName = table.getSqlName();
			String prefixs = GeneratorProperties.getProperty("skipTablePrefixes", "");
			for (String prefix : prefixs.split(",")) {
				if (null != prefix && !prefix.trim().isEmpty() && sqlName.startsWith(prefix)) {
					logger.info("忽略带前缀的表[prefix=" + prefix + "][skipTable=" + sqlName + "]");
					return;
				}
			}
			GeneratorModel m = GeneratorUtils.newFromTable(table);
			PrintUtils.printBeginProcess(table.getSqlName() + " => " + table.getClassName(), isDelete);
			if (isDelete)
				g.deleteBy(m.templateModel, m.filePathModel);
			else
				g.generateBy(m.templateModel, m.filePathModel);
			PrintUtils.printEndProcess(table.getSqlName() + " => " + table.getClassName(), isDelete);
		}

		@SuppressWarnings("rawtypes")
		public void processByClass(Class clazz, String templateRootDir, boolean isDelete)
				throws Exception, FileNotFoundException {
			Generator g = getGenerator(templateRootDir);
			GeneratorModel m = GeneratorUtils.newFromClass(clazz);
			PrintUtils.printBeginProcess("JavaClass:" + clazz.getSimpleName(), isDelete);
			try {
				if (isDelete)
					g.deleteBy(m.templateModel, m.filePathModel);
				else
					g.generateBy(m.templateModel, m.filePathModel);
			} catch (GeneratorException ge) {
				PrintUtils.printExceptionsSumary(ge.getMessage(), getGenerator(templateRootDir).getOutRootDir(), ge.getExceptions());
			}
			PrintUtils.printEndProcess("JavaClass:" + clazz.getSimpleName(), isDelete);
		}

		public void processBySql(Sql sql, String templateRootDir, boolean isDelete) throws Exception {
			Generator g = getGenerator(templateRootDir);
			GeneratorModel m = GeneratorUtils.newFromSql(sql);
			PrintUtils.printBeginProcess("sql:" + sql.getSourceSql(), isDelete);
			try {
				if (isDelete) {
					g.deleteBy(m.templateModel, m.filePathModel);
				} else {
					g.generateBy(m.templateModel, m.filePathModel);
				}
			} catch (GeneratorException ge) {
				PrintUtils.printExceptionsSumary(ge.getMessage(), getGenerator(templateRootDir).getOutRootDir(), ge.getExceptions());
			}
			PrintUtils.printEndProcess("sql:" + sql.getSourceSql(), isDelete);
		}
		
		public void processByMap(Map<String, Object> params, String templateRootDir, boolean isDelete) throws Exception, FileNotFoundException {
			Generator g = getGenerator(templateRootDir);
			GeneratorModel m = GeneratorUtils.newFromMap(params);
			try {
				if (isDelete)
					g.deleteBy(m.templateModel, m.filePathModel);
				else
					g.generateBy(m.templateModel, m.filePathModel);
			} catch (GeneratorException ge) {
				PrintUtils.printExceptionsSumary(ge.getMessage(), getGenerator(templateRootDir).getOutRootDir(), ge.getExceptions());
			}
		}
	}

	private static class PrintUtils {

		private static void printExceptionsSumary(String msg, String outRoot, List<Exception> exceptions)
				throws FileNotFoundException {
			File errorFile = new File(outRoot, "generator_error.log");
			if (exceptions != null && exceptions.size() > 0) {
				logger.error("[Generate Error Summary] : " + msg);
				PrintStream output = new PrintStream(new FileOutputStream(errorFile));
				for (int i = 0; i < exceptions.size(); i++) {
					Exception e = exceptions.get(i);
					logger.error("[GENERATE ERROR]:" + e.getMessage(), e);
					if (i == 0) e.printStackTrace();
					e.printStackTrace(output);
				}
				output.close();
				logger.info("################################################################");
				logger.info("# 已生成错误文件generator_error.log ");
				logger.info("################################################################");
			}
		}

		/**
		 * 输出生成过程信息
		 * @param displayText
		 * @param isDatele
		 */
		private static void printBeginProcess(String displayText, boolean isDatele) {
			logger.info("################################################################");
			logger.info("# 开始" + (isDatele ? "删除 " : "生成 ") + displayText);
			logger.info("################################################################");
		}

		/**
		 * 输出生成过程信息
		 * @param displayText
		 * @param isDatele
		 */
		private static void printEndProcess(String displayText, boolean isDatele) {
			logger.info("################################################################");
			logger.info("# 结束" + (isDatele ? "删除 " : "生成 ") + displayText);
			logger.info("################################################################");
		}

		/**
		 * 输出数据库所有表
		 * @param tables
		 * @throws Exception
		 */
		public static void printAllTableNames(List<Table> tables) throws Exception {
			logger.info("################输出所有数据表开始################");
			for (int i = 0; i < tables.size(); i++) {
				String sqlName = ((Table) tables.get(i)).getSqlName();
				logger.info(sqlName);
			}
			logger.info("################输出所有数据表结束################");
		}
	}

}

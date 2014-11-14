package com.ganesha.minimarket.db;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.quartz.SchedulerException;

import com.ganesha.core.SystemSetting;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.utils.GeneralConstants;
import com.ganesha.core.utils.ResourceUtils;
import com.ganesha.desktop.component.permissionutils.PermissionChecker;
import com.ganesha.minimarket.Main;
import com.ganesha.minimarket.utils.CompanyConsistencyChecker;
import com.ganesha.minimarket.utils.SimplePermissionChecker;

public class DbInitializer {

	public static void initial() throws AppException, SchedulerException {
		Connection conn = null;
		try {
			conn = openConnection();
			boolean mySQLInstalled = isMySQLInstalled();
			if (mySQLInstalled) {
				boolean dbExist = isDbExist(conn);
				if (dbExist) {
					/*
					 * Do Nothing. Anggap saja berarti sudah terinstall
					 */
				} else {
					createDb(conn);
					initialTablesUsingHibernate();
					runDMLScripts(conn);
				}

				checkDbConsistency();

			} else {
				throw new AppException("MySQL is not found on directory ");
			}

		} catch (SQLException e) {
			throw new AppException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					throw new AppException(e);
				}
			}
		}
	}

	private static void checkDbConsistency() throws SchedulerException,
			AppException {

		CompanyConsistencyChecker companyChecker = new CompanyConsistencyChecker();
		companyChecker.check();
		Main.setCompany(companyChecker.getCompany());

		PermissionChecker.register(new SimplePermissionChecker());
		Main.runQuartz();
	}

	private static void createDb(Connection conn) throws SQLException {

		String dbName = SystemSetting
				.getProperty(GeneralConstants.SYSTEM_PROPERTY_DB_SCHEMA);

		Statement statement = conn.createStatement();
		statement.execute("CREATE DATABASE " + dbName);
	}

	private static void initialTablesUsingHibernate() throws AppException {
		try {
			Class.forName("com.ganesha.hibernate.HibernateUtils");
		} catch (ClassNotFoundException e) {
			throw new AppException(e);
		}
	}

	private static boolean isDbExist(Connection conn) throws SQLException {
		ResultSet resultSet = conn.getMetaData().getCatalogs();
		while (resultSet.next()) {
			String dbName = resultSet.getString(1);
			if (dbName.equalsIgnoreCase(SystemSetting
					.getProperty(GeneralConstants.SYSTEM_PROPERTY_DB_SCHEMA))) {
				return true;
			}
		}

		return false;

	}

	private static boolean isMySQLInstalled() {
		File binDir = new File(
				SystemSetting
						.getProperty(GeneralConstants.SYSTEM_PROPERTY_MYSQL_LOCATION_EXE));

		File mySqlExe = new File(binDir, "mysql.exe");
		return mySqlExe.exists();
	}

	private static Connection openConnection() throws AppException {

		Connection conn = null;

		try {
			Class.forName("com.mysql.jdbc.Driver");

			String url = SystemSetting
					.getProperty(GeneralConstants.SYSTEM_PROPERTY_DB_URL);
			String username = SystemSetting
					.getProperty(GeneralConstants.SYSTEM_PROPERTY_DB_USERNAME);
			String password = SystemSetting
					.getProperty(GeneralConstants.SYSTEM_PROPERTY_DB_PASSWORD);

			conn = DriverManager.getConnection(url, username, password);

			return conn;

		} catch (ClassNotFoundException e) {
			throw new AppException(e);
		} catch (SQLException e) {
			throw new AppException(e);
		}

	}

	private static void runDMLScripts(Connection conn) throws SQLException,
			AppException {
		try {
			Statement statement = conn.createStatement();
			statement
					.execute("USE "
							+ SystemSetting
									.getProperty(GeneralConstants.SYSTEM_PROPERTY_DB_SCHEMA));

			File configBase = ResourceUtils.getConfigBase();
			File dmlDir = new File(configBase, "DML");

			File[] dmlFiles = dmlDir.listFiles();
			for (File dmlFile : dmlFiles) {

				List<String> strings = Files.readAllLines(dmlFile.toPath(),
						StandardCharsets.UTF_8);

				for (String string : strings) {
					statement.execute(string);
				}
			}
		} catch (IOException e) {
			throw new AppException(e);
		}
	}
}

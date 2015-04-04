package com.ganesha.minimarket.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.LoggerFactory;

import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.coreapps.constants.Loggers;

public class BackupDB {

	public static void backup(File mysqldump, File backupFile, String username,
			String password, String dbname) throws AppException, UserException {
		try {
			String commands = "\"" + mysqldump.getAbsolutePath() + "\""
					+ " -u " + username + " -p" + password + " " + dbname
					+ " -r " + "\"" + backupFile.getAbsolutePath() + "\"";

			LoggerFactory.getLogger(Loggers.UTILS).debug(
					"Running command: " + commands);

			Process p = Runtime.getRuntime().exec(commands);
			int processComplete = p.waitFor();
			if (processComplete != 0) {
				InputStream inputStream = null;
				InputStreamReader inputStreamReader = null;
				try {
					inputStream = p.getErrorStream();
					inputStreamReader = new InputStreamReader(inputStream);
					BufferedReader bufferedReader = new BufferedReader(
							inputStreamReader);
					StringBuilder builder = new StringBuilder();
					String line = null;
					while ((line = bufferedReader.readLine()) != null) {
						builder.append(line);
					}
					throw new UserException(builder.toString());
				} finally {
					if (inputStreamReader != null) {
						inputStreamReader.close();
					}
					if (inputStream != null) {
						inputStream.close();
					}
				}
			}
		} catch (IOException e) {
			throw new AppException(e);
		} catch (InterruptedException e) {
			throw new AppException(e);
		}
	}
}

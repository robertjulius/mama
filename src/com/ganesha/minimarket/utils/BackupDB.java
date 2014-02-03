package com.ganesha.minimarket.utils;

import java.io.File;
import java.io.IOException;

import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;

public class BackupDB {

	public static void backup(File mysqldump, File backupFile, String username,
			String password, String dbname) throws AppException, UserException {
		try {
			String commands = mysqldump.getAbsolutePath() + " -u " + username
					+ " -p" + password + " " + dbname + " -r "
					+ backupFile.getAbsolutePath();
			Process p = Runtime.getRuntime().exec(commands);
			int processComplete = p.waitFor();
			if (processComplete != 0) {
				throw new UserException("Backup DB Failed with command: "
						+ commands);
			}
		} catch (IOException e) {
			throw new AppException(e);
		} catch (InterruptedException e) {
			throw new AppException(e);
		}
	}
}

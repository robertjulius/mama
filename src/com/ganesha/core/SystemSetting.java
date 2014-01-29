package com.ganesha.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;

import com.ganesha.core.desktop.ExceptionHandler;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.utils.ResourceUtils;

public class SystemSetting {

	private static HashMap<String, Object> settings = new HashMap<String, Object>();
	private static final String FILE_PATH = "config";

	static {
		try {
			readSettingFromFile();
		} catch (AppException e) {
			ExceptionHandler.handleException(null, e);
		}
	}

	public static Object get(String key) throws AppException {
		if (settings.get(key) == null) {
			save(key, null);
		}
		return settings.get(key);
	}

	public static void save(String key, Object value) throws AppException {
		settings.put(key, value);
		saveSettingToFile();
	}

	private static void readSettingFromFile() throws AppException {
		InputStream is = null;
		try {
			File configBase = ResourceUtils.getConfigBase();
			File configFile = new File(configBase, FILE_PATH);
			if (configFile.exists()) {
				is = new FileInputStream(configFile);
				ObjectInputStream ois = new ObjectInputStream(is);

				@SuppressWarnings("unchecked")
				HashMap<String, Object> map = (HashMap<String, Object>) ois
						.readObject();

				Iterator<String> iterator = map.keySet().iterator();
				while (iterator.hasNext()) {
					String key = iterator.next();
					settings.put(key, map.get(key));
				}
				ois.close();
			}
		} catch (FileNotFoundException e) {
			throw new AppException(e);
		} catch (IOException e) {
			throw new AppException(e);
		} catch (ClassNotFoundException e) {
			throw new AppException(e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					throw new AppException(e);
				}
			}
		}
	}

	private static void saveSettingToFile() throws AppException {
		OutputStream os = null;
		try {
			File configBase = ResourceUtils.getConfigBase();
			File configFile = new File(configBase, FILE_PATH);
			if (configFile.exists()) {
				configFile.delete();
			}

			os = new FileOutputStream(configFile);
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(settings);
			oos.flush();
			oos.close();
		} catch (FileNotFoundException e) {
			throw new AppException(e);
		} catch (IOException e) {
			throw new AppException(e);
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					throw new AppException(e);
				}
			}
		}
	}
}

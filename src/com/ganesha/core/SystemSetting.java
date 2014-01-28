package com.ganesha.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;

import com.ganesha.core.desktop.ExceptionHandler;
import com.ganesha.core.utils.ResourceUtils;

public class SystemSetting {

	private static HashMap<String, String> settings = new HashMap<String, String>();
	private static final String FILE_PATH = "resources/config";

	static {
		try {
			readSettingFromFile();
		} catch (IOException e) {
			ExceptionHandler.handleException(null, e);
		} catch (URISyntaxException e) {
			ExceptionHandler.handleException(null, e);
		} catch (ClassNotFoundException e) {
			ExceptionHandler.handleException(null, e);
		}
	}

	public static String get(String key) throws IOException, URISyntaxException {
		if (settings.get(key) == null) {
			save(key, null);
		}
		return settings.get(key);
	}

	public static void save(String key, String value) throws IOException,
			URISyntaxException {
		settings.put(key, value);
		saveSettingToFile();
	}

	private static void readSettingFromFile() throws IOException,
			URISyntaxException, ClassNotFoundException {
		InputStream is = null;
		try {
			URI uri = SystemSetting.class.getProtectionDomain().getCodeSource()
					.getLocation().toURI();
			File configBase = ResourceUtils.getConfigBase();
			;
			File configFile = new File(configBase, FILE_PATH);
			if (configFile.exists()) {
				is = new FileInputStream(configFile);
				ObjectInputStream ois = new ObjectInputStream(is);

				@SuppressWarnings("unchecked")
				HashMap<String, String> map = (HashMap<String, String>) ois
						.readObject();

				Iterator<String> iterator = map.keySet().iterator();
				while (iterator.hasNext()) {
					String key = iterator.next();
					settings.put(key, map.get(key));
				}
				ois.close();
			}
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}

	private static void saveSettingToFile() throws IOException,
			URISyntaxException {
		OutputStream os = null;
		try {
			URI uri = SystemSetting.class.getProtectionDomain().getCodeSource()
					.getLocation().toURI();
			File parent = new File(uri).getParentFile();
			File configFile = new File(parent, FILE_PATH);
			if (configFile.exists()) {
				configFile.delete();
			}

			os = new FileOutputStream(configFile);
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(settings);
			oos.flush();
			oos.close();
		} finally {
			if (os != null) {
				os.close();
			}
		}
	}
}

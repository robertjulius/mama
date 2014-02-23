package com.ganesha.core.utils;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import com.ganesha.desktop.exeptions.ExceptionHandler;

public class ResourceUtils {

	private static final File resourceBase;

	static {
		URI uri = null;
		try {
			uri = ResourceUtils.class.getProtectionDomain().getCodeSource()
					.getLocation().toURI();
		} catch (URISyntaxException e) {
			ExceptionHandler.handleException(null, e);
			System.exit(1);
		} finally {
			if (uri == null) {
				resourceBase = null;
			} else {
				resourceBase = new File(new File(uri).getParentFile(),
						"resources");
			}
		}
	}

	public static final File getConfigBase() {
		return new File(resourceBase, "configurations");
	}

	public static final File getImageBase() {
		return new File(resourceBase, "images");
	}

	public static final File getResourceBase() {
		return resourceBase;
	}
}

package com.gearreald.tullfileclient.utils;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

public class ResourceLoader {
	public static String getAbsoluteResourcePath(String resourcePath){
		System.out.println(resourcePath);
		return getResourceURL(resourcePath).toExternalForm();
	}
	public static File getResourceFile(String resourcePath){
		return new File(getAbsoluteResourcePath(resourcePath));
	}
	public static URL getResourceURL(String resourcePath){
		return getLoader().getResource(resourcePath);
	}
	public static InputStream getResourceStream(String resourcePath){
		return getLoader().getResourceAsStream(resourcePath);
	}
	private static ClassLoader getLoader(){
		return ResourceLoader.class.getClassLoader();
	}
}
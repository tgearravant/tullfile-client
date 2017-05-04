package com.gearreald.tullfileclient.utils;

import java.net.URL;

import javafx.scene.image.Image;

public class ImageUtils {
	
	private static final String RESOURCE_SUBFOLDER = "img/";
	
	public static Image getImage(String s){
		String resourceLocation = RESOURCE_SUBFOLDER+s;
		URL resourceURL = ImageUtils.class.getClassLoader().getResource(resourceLocation);
		return new Image(resourceURL.toString());
	}
}

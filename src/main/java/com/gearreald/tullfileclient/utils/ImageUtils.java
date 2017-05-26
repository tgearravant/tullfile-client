package com.gearreald.tullfileclient.utils;

import java.util.HashMap;

import javafx.scene.image.Image;

public class ImageUtils {
	
	private static final String IMAGE_SUBFOLDER = "img/";
	private static final HashMap<String, Image> imageCache = new HashMap<String, Image>();
	
	public static Image getImage(String s){
		return getImage(s,false);
	}
	public static Image getImage(String s, boolean reload){
		if(!reload && imageCache.containsKey(s)){
			return imageCache.get(s);
		}
		String resourceLocation = IMAGE_SUBFOLDER+s;
		Image i = new Image(ResourceLoader.getAbsoluteResourcePath(resourceLocation));
		imageCache.put(s, i);
		return i;
	}
}

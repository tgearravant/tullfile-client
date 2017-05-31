package com.gearreald.tullfileclient.models;

import java.io.File;

import com.gearreald.tullfileclient.utils.SystemUtils;

import net.tullco.tullutils.StringUtils;

public class TempHandler {
	public static File getTempDirectoryForFile(String localPath, String name){
		String suffix = SystemUtils.getProperty("tullfile_suffix");
		String subFolder = SystemUtils.getProperty("home_directory_subfolder_name");
		String cleanPath = StringUtils.assureEndsWith(StringUtils.assureStartsWith(localPath, "/"),"/");
		String fullPath = SystemUtils.getUserDirectory() + "/"+subFolder+"/"+cleanPath+name+suffix;
		File f = new File(fullPath);
		f.mkdirs();
		return f;
	}
}

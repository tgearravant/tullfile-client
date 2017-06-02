package com.gearreald.tullfileclient.models;

import java.io.File;

import com.gearreald.tullfileclient.Environment;
import com.gearreald.tullfileclient.utils.SystemUtils;

import net.tullco.tullutils.StringUtils;

public class TempHandler {
	public static File getTempDirectoryForFile(String localPath, String name){
		String suffix = Environment.getConfiguration("tullfile_suffix");
		String subFolder = Environment.getConfiguration("home_directory_subfolder_name");
		String cleanPath = StringUtils.assureEndsWith(StringUtils.assureStartsWith(localPath, "/"),"/");
		String fullPath = SystemUtils.getUserDirectory() + "/"+subFolder+"/"+cleanPath+name+suffix;
		File f = new File(fullPath);
		f.mkdirs();
		return f;
	}
}

package com.gearreald.tullfileclient.models;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.gearreald.tullfileclient.Environment;

import net.tullco.tullutils.NetworkUtils;

public class ServerConnection {
	
	public static List<TullFile> FileCache;
	public static boolean checkKey() throws MalformedURLException, IOException{
		NetworkUtils.getDataFromURL(Environment.getConfiguration("HOSTNAME"), false, NetworkUtils.HEAD, Pair.of("Authorization", Environment.getConfiguration("API_KEY")));
		return true;
	}
	public static TullFile[] getFileListing(String key){
		return null;
	}
	public static File downloadFile(TullFile f){
		return null;
	}
}

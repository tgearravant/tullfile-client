package com.gearreald.tullfileclient.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SystemUtils {
	private static Boolean inTesting=false;
	final private static Boolean IS_WINDOWS=(System.getProperty("os.name").contains("Windows"));
	final private static String[] requiredProperties={"admin_username","admin_password","s3_access_key_id","s3_secret_key","backup_key"};
	private static Properties properties=null;

	public static boolean inProduction(){
		return !IS_WINDOWS;
	}
	public static boolean inTesting(){
		return inTesting;
	}
	public static void setTesting(boolean testing){
		inTesting=testing;
	}

	/**
	 * This function loads properties from the location in the string into the
	 * passed in properties object.
	 * 
	 * If the System Properties are already set, it returns the properties file.
	 * @param p The properties object to load properties into
	 * @param s The location in the resources folder of the properties file.
	 * @return The properties object passed in loaded with the values in the file.
	 * Will be the global properties file if it has already been set.
	 */
	private static Properties loadProperties(Properties p,String s){
		if (SystemUtils.properties!=null)
			return SystemUtils.properties;
		InputStream input=null;
		try{
			String filename="config/"+s;
			input=SystemUtils.class.getClassLoader().getResourceAsStream(filename);
			if (input==null) {
				System.out.println("Could not load properties...");
				return p;
			}
			p.load(input);
		}catch (IOException e){
			e.printStackTrace();
		}finally{
			try{
				input.close();
			}catch(IOException e){}catch(NullPointerException e){}
		}
		return p;
	}

	/**
	 * This function loads the property files for the program from the
	 * default locations, with the default properties inside the per set properties files.
	 */
	private static void loadPropertiesWithDefaults(){
		Properties defaultProps = new Properties();
		//System.out.println(SystemUtils.class.getClassLoader().getResource("config.properties"));
		loadProperties(defaultProps,"config.properties.default");
		if(inTesting()){
			SystemUtils.properties=defaultProps;
			return;
		}
		Properties p=new Properties(defaultProps);
		loadProperties(p,"config.properties");
		SystemUtils.properties=p;
	}
	
	/**
	 * Gets a property from the configuration files.
	 * Will pull from the defaults if not all properties are set.
	 * Defaults to the string passed in if the property isn't found anywhere
	 * @param s The property key.
	 * @param d The default value.
	 * @return The value of the property, or the default if nothing is found.
	 */
	public static String getProperty(String s,String d){
		if(SystemUtils.properties == null){
			loadPropertiesWithDefaults();
		}
		return SystemUtils.properties.getProperty(s,d);
	}

	/**
	 * Gets a property from the configuration files.
	 * Will pull from the defaults if not all properties are set.
	 * @param s The property key.
	 * @return The value of the property.
	 */
	public static String getProperty(String s){
		if(SystemUtils.properties == null){
			loadPropertiesWithDefaults();
		}
		return SystemUtils.properties.getProperty(s);
	}
	
	/**
	 * This function checks to make sure that the configuration
	 * properties that are required for function are set. These shouldn't be
	 * missing as the config.properties.default file should have default values
	 * for all of these items set, so this is just a precaution. Unless you've
	 * been messing with the default config file... How dare you?!
	 * 
	 * @throws RuntimeException Thrown if a required property is missing.
	 */
	public static void checkForRequiredProperties(){ 
		if(SystemUtils.properties == null){
			loadPropertiesWithDefaults();
		}
		for(String s:requiredProperties){
			if (SystemUtils.getProperty(s)==null)
				throw new RuntimeException("Required Property "+s+" is undefined");
		}
	}
	
	public static String getUserDirectory(){
		if(IS_WINDOWS){
			return System.getProperty("user.home");
		}else{
			return System.getProperty("user.home");
		}
	}
}

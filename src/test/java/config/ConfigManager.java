package config;

import java.io.FileInputStream;
import java.util.Properties;

public class ConfigManager {
	private static Properties properties;
	
	static {
		try {
			properties = new Properties();
			properties.load(new FileInputStream("src/test/resources/config.properties"));
		}
		catch(Exception e) {
			throw new RuntimeException("Config file not loaded");
		}
	}
	
	public static String get(String key) {
		return properties.getProperty(key);
	}
}

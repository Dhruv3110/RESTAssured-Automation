package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {

	private static final Properties properties = new Properties();
	private static final String CONFIG_PATH = "src/test/resources/config.properties";

	static {
		try (InputStream input = new FileInputStream(CONFIG_PATH)) {
			properties.load(input);
		} catch (IOException e) {
			throw new RuntimeException("Failed to load configuration file: " + CONFIG_PATH, e);
		}
	}

	private ConfigManager() {
	}

	public static String get(String key) {
		String value = properties.getProperty(key);
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException("Missing or empty configuration key: '" + key + "' in " + CONFIG_PATH);
		}
		return value.trim();
	}

	public static String getOrDefault(String key, String defaultValue) {
		String value = properties.getProperty(key);
		return (value != null && !value.isBlank()) ? value.trim() : defaultValue;
	}
}
package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

public class LoggerUtils {

	private static final String TEST_KEY = "testName";

	private LoggerUtils() {
	}

	public static Logger getLogger(Class<?> clazz) {
		return LogManager.getLogger(clazz);
	}

	public static void setTestContext(String testName) {
		ThreadContext.put(TEST_KEY, testName);
	}

	public static void clearContext() {
		ThreadContext.remove(TEST_KEY);
	}
}
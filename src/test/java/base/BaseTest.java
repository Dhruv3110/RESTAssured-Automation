package base;

import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.specification.RequestSpecification;

import org.apache.logging.log4j.Logger;
import org.testng.ITestResult;
import org.testng.annotations.*;

import specs.RequestSpecFactory;
import utils.ExtentManager;
import utils.LoggerUtils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Method;

public abstract class BaseTest {

	private static final ThreadLocal<RequestSpecification> requestSpec = new ThreadLocal<>();
	private static final ThreadLocal<Logger> logger = new ThreadLocal<>();
	private static final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

	private static ExtentReports extent;

	@BeforeSuite(alwaysRun = true)
	public synchronized void setupReport() {
		if (extent == null) {
			extent = ExtentManager.getInstance();
		}

		RestAssured.config = RestAssured.config().objectMapperConfig(
				ObjectMapperConfig.objectMapperConfig().jackson2ObjectMapperFactory((cls, charset) -> {
					ObjectMapper mapper = new ObjectMapper();
					mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
					return mapper;
				}));
	}

	@AfterSuite(alwaysRun = true)
	public void tearDownReport() {
		if (extent != null) {
			extent.flush();
		}
	}

	@BeforeMethod(alwaysRun = true)
	public void setup(Method method) {
		requestSpec.set(RequestSpecFactory.getRequestSpec());

		Logger log = LoggerUtils.getLogger(method.getDeclaringClass());
		logger.set(log);

		LoggerUtils.setTestContext(method.getName());

		ExtentTest test;
		synchronized (ExtentManager.class) {
			test = extent.createTest(method.getDeclaringClass().getSimpleName() + " → " + method.getName());
		}
		extentTest.set(test);

		logger.get().info("STARTING TEST: {}", method.getName());
		extentTest.get().info("STARTING TEST: " + method.getName());
	}

	@AfterMethod(alwaysRun = true)
	public void tearDown(ITestResult result) {
		String testName = result.getMethod().getMethodName();

		switch (result.getStatus()) {
		case ITestResult.SUCCESS:
			logger.get().info("ENDING TEST: {} - PASSED", testName);
			extentTest.get().pass("TEST PASSED");
			break;

		case ITestResult.FAILURE:
			logger.get().error("ENDING TEST: {} - FAILED | Cause: {}", testName, result.getThrowable().getMessage());
			extentTest.get().fail(result.getThrowable());
			break;

		case ITestResult.SKIP:
			logger.get().warn("ENDING TEST: {} - SKIPPED", testName);
			extentTest.get().skip("TEST SKIPPED");
			break;

		default:
			logger.get().warn("ENDING TEST: {} - UNKNOWN STATUS ({})", testName, result.getStatus());
			break;
		}

		LoggerUtils.clearContext();

		requestSpec.remove();
		logger.remove();
		extentTest.remove();
	}

	protected RequestSpecification getRequestSpec() {
		return requestSpec.get();
	}

	protected ExtentTest getExtentTest() {
		return extentTest.get();
	}

	protected Logger getLogger() {
		return logger.get();
	}
}
package base;

import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.Logger;
import org.testng.ITestResult;
import org.testng.annotations.*;

import specs.RequestSpecFactory;
import utils.ExtentManager;
import utils.LoggerUtils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

import java.lang.reflect.Method;

public abstract class BaseTest {

    private static ThreadLocal<RequestSpecification> requestSpec = new ThreadLocal<>();

    private static ThreadLocal<Logger> logger = new ThreadLocal<>();

    private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    private static ExtentReports extent;


    @BeforeSuite(alwaysRun = true)
    public void setupReport() {
        extent = ExtentManager.getInstance();
    }


    @BeforeMethod(alwaysRun = true)
    public void setup(Method method) {

        requestSpec.set(RequestSpecFactory.getRequestSpec());

        Logger log = LoggerUtils.getLogger(method.getDeclaringClass());
        logger.set(log);

        ExtentTest test = extent.createTest(method.getName());
        extentTest.set(test);

        logger.get().info("STARTING TEST: {}", method.getName());
        extentTest.get().info("STARTING TEST: " + method.getName());
    }


    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {

        String testName = result.getMethod().getMethodName();

        if (result.getStatus() == ITestResult.SUCCESS) {
            logger.get().info("ENDING TEST: {} - PASSED", testName);
            extentTest.get().pass("TEST PASSED");
        }

        else if (result.getStatus() == ITestResult.FAILURE) {
            logger.get().error("ENDING TEST: {} - FAILED", testName);
            extentTest.get().fail(result.getThrowable());
        }

        else if (result.getStatus() == ITestResult.SKIP) {
            logger.get().warn("ENDING TEST: {} - SKIPPED", testName);
            extentTest.get().skip("TEST SKIPPED");
        }

        requestSpec.remove();
        logger.remove();
        extentTest.remove();
    }


    @AfterSuite(alwaysRun = true)
    public void tearDownReport() {
        if (extent != null) {
            extent.flush();
        }
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
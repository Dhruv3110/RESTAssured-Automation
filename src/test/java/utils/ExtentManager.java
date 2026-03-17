package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentManager {

	private static volatile ExtentReports extent;

	private ExtentManager() {
	}

	public static ExtentReports getInstance() {
		if (extent == null) {
			synchronized (ExtentManager.class) {
				if (extent == null) {
					extent = createInstance();
				}
			}
		}
		return extent;
	}

	private static ExtentReports createInstance() {
		ExtentSparkReporter spark = new ExtentSparkReporter("reports/ExtentReport.html");
		spark.config().setDocumentTitle("API Test Report");
		spark.config().setReportName("REST Assured Automation Suite");
		spark.config().setTheme(Theme.STANDARD);
		spark.config().setEncoding("UTF-8");

		ExtentReports reports = new ExtentReports();
		reports.attachReporter(spark);
		reports.setSystemInfo("Framework", "REST Assured + TestNG");
		reports.setSystemInfo("Environment", "QA");
		return reports;
	}
}
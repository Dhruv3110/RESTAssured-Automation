package specs;

import config.ConfigManager;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class RequestSpecFactory {

	private static final int CONNECT_TIMEOUT_MS = 10_000; // 10 seconds
	private static final int SOCKET_TIMEOUT_MS = 30_000; // 30 seconds

	private RequestSpecFactory() {
	}

	public static RequestSpecification getRequestSpec() {
		return new RequestSpecBuilder().setBaseUri(ConfigManager.get("base.url")).setContentType(ContentType.JSON)
				.setAccept(ContentType.JSON).log(LogDetail.ALL)
				.setConfig(io.restassured.RestAssured.config()
						.httpClient(io.restassured.config.HttpClientConfig.httpClientConfig()
								.setParam("http.connection.timeout", CONNECT_TIMEOUT_MS)
								.setParam("http.socket.timeout", SOCKET_TIMEOUT_MS)))
				.build();
	}
}
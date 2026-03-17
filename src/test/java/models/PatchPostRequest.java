package models;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PatchPostRequest {

	private String title;
	private String body;

	public PatchPostRequest() {
	}

	public PatchPostRequest(String title, String body) {
		this.title = title;
		this.body = body;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	@Override
	public String toString() {
		return "PatchPostRequest{" + "title='" + title + '\'' + ", body='" + body + '\'' + '}';
	}
}
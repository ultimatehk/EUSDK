/**
 * 返回结果实体类
 * @author huangke
 */
package cn.eugames.extension.utils;

public class RestResult {
	private int statusCode;
	private String content;

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}

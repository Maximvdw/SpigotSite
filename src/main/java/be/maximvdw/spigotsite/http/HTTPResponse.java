package be.maximvdw.spigotsite.http;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class HTTPResponse {
	private Map<String, String> cookies = new HashMap<String, String>();
	private String html = "";
	private Document document = null;
	private URL responseURL = null;
	private HTTPRequest originalRequest = null;

	public HTTPResponse() {

	}

	public HTTPResponse(String html, Map<String, String> cookies) {
		setHtml(html);
		setCookies(cookies);
	}

	public Map<String, String> getCookies() {
		return cookies;
	}

	public void setCookies(Map<String, String> cookies) {
		this.cookies = cookies;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public Document getDocument() {
		return document == null ? this.document = Jsoup.parse( getHtml() ) : document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public URL getResponseURL() {
		return responseURL;
	}

	public void setResponseURL(URL responseURL) {
		this.responseURL = responseURL;
	}

	public HTTPRequest getOriginalRequest() {
		return originalRequest;
	}

	public void setOriginalRequest(HTTPRequest originalRequest) {
		this.originalRequest = originalRequest;
	}
}

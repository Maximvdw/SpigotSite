package be.maximvdw.spigotsite.http;

import java.io.IOException;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.nodes.Document;

public class JsoupRequest {
	public static HTTPResponse get(String url, Map<String, String> cookies,
			Map<String, String> params) {
		HTTPResponse response = new HTTPResponse();
		try {
			Connection.Response res = Jsoup
					.connect(url)
					.cookies(cookies)
					.method(Method.GET)
					.data(params)
					.userAgent(
							"Mozilla/5.0 (Windows NT 6.3; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0")
					.execute();
			Document doc = res.parse();
			response.setCookies(res.cookies());
			response.setDocument(doc);
			response.setHtml(doc.html());
		} catch (IOException e) {

		}
		return response;
	}

	public static HTTPResponse post(String url, Map<String, String> cookies,
			Map<String, String> params) {
		HTTPResponse response = new HTTPResponse();
		try {
			Connection.Response res = Jsoup
					.connect(url)
					.cookies(cookies)
					.method(Method.POST)
					.data(params)
					.userAgent(
							"Mozilla/5.0 (Windows NT 6.3; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0")
					.execute();
			Document doc = res.parse();
			response.setCookies(res.cookies());
			response.setDocument(doc);
			response.setHtml(doc.html());
		} catch (IOException e) {

		}
		return response;
	}
}

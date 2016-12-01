package be.maximvdw.spigotsite.http;

import java.io.IOException;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.UnsupportedMimeTypeException;
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
                            "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.107 Safari/537.36")
                    .execute();
            Document doc = res.parse();
            if (doc.html().contains("DDoS protection by CloudFlare")) {
                Request.setDdosBypass(true);
                return null;
            }
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
                            "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.107 Safari/537.36")
                    .execute();
            Document doc = res.parse();
            if (doc.html().contains("DDoS protection by CloudFlare")) {
                Request.setDdosBypass(true);
                return null;
            }
            response.setDocument(doc);
            response.setHtml(doc.html());
            response.setCookies(res.cookies());
        } catch (IOException e) {

        }
        return response;
    }
}

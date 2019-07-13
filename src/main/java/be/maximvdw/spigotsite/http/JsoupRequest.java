package be.maximvdw.spigotsite.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;

public class JsoupRequest {
    public static HTTPResponse get(HTTPRequest request) {
        HTTPResponse response = new HTTPResponse();
        try {
            Connection.Response res = Jsoup
                    .connect(request.getUrl())
                    .cookies(request.getCookies())
                    .method(Method.GET)
                    .followRedirects(true)
                    .maxBodySize(0)
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .data(request.getParams())
                    .userAgent(BrowserVersion.CHROME.getUserAgent())
                    .execute();
            Document doc = res.parse();
            if (doc.html().contains("DDoS protection by CloudFlare")) {
                Request.setDdosBypass(true);
                return null;
            }
            Map<String,String> cookiesCombined = new HashMap<String,String>();
            cookiesCombined.putAll(request.getCookies());
            cookiesCombined.putAll(res.cookies());
            response.setCookies(cookiesCombined);
            response.setDocument(doc);
            response.setHtml(doc.html());
            response.setResponseURL(res.url());
            response.setOriginalRequest(request);
        } catch (IOException e) {

        }
        return response;
    }

    public static HTTPResponse post(HTTPRequest request) {
        HTTPResponse response = new HTTPResponse();
        try {
            Connection.Response res = Jsoup
                    .connect(request.getUrl())
                    .cookies(request.getCookies())
                    .method(Method.POST)
                    .maxBodySize(0)
                    .data(request.getParams())
                    .ignoreContentType(true)
                    .userAgent(BrowserVersion.CHROME.getUserAgent())
                    .execute();
            Document doc = res.parse();
            if (doc.html().contains("DDoS protection by CloudFlare")) {
                Request.setDdosBypass(true);
                return null;
            }
            response.setDocument(doc);
            response.setHtml(doc.html());
            Map<String,String> cookiesCombined = new HashMap<String,String>();
            cookiesCombined.putAll(request.getCookies());
            cookiesCombined.putAll(res.cookies());
            response.setCookies(cookiesCombined);
            response.setResponseURL(res.url());
            response.setOriginalRequest(request);
        } catch (IOException e) {

        }
        return response;
    }
}

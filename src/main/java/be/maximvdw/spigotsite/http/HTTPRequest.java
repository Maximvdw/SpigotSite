package be.maximvdw.spigotsite.http;

import java.util.HashMap;
import java.util.Map;

/**
 * HTTPRequest
 * Created by maxim on 10-Jan-17.
 */
public class HTTPRequest {
    private Method method = Method.GET;
    private String url = null;
    private Map<String, String> cookies = new HashMap<String, String>();
    private Map<String, String> params = new HashMap<String, String>();

    public HTTPRequest(String url, Map<String, String> cookies,
                       Map<String, String> params) {
        setUrl(url);
        setParams(params);
        setCookies(cookies);
    }

    public HTTPRequest() {

    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public void setCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public enum Method {
        GET, POST
    }

    public HTTPResponse execute() {
        switch (method) {
            case GET:
                return Request.get(url, cookies, params);
            case POST:
                return Request.post(url, cookies, params);
        }
        return null;
    }
}

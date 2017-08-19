package be.maximvdw.spigotsite.http;

import be.maximvdw.spigotsite.SpigotSiteCore;
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class HTTPUnitRequest {
    private static WebClient webClient = null;

    public static void initialize() {
        webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setTimeout(15000);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setRedirectEnabled(true);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setPrintContentOnFailingStatusCode(false);
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
    }

    public static HTTPDownloadResponse downloadFile(String url, Map<String, String> cookies) {
        try {
            WebRequest wr = new WebRequest(new URL(url), HttpMethod.GET);
            for (Map.Entry<String, String> entry : cookies.entrySet())
                webClient.getCookieManager().addCookie(new Cookie("spigotmc.org", entry.getKey(), entry.getValue()));
            InputStream stream = null;
            Page page = webClient.getPage(wr);
            if (page instanceof HtmlPage)
                System.out.println(((HtmlPage) page).asXml());
                if (((HtmlPage) page).asXml().contains("DDoS protection by Cloudflare")) {
                    // DDOS protection
                    try {
                        Thread.sleep(9000);
                        Request.setDdosBypass(false);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); // restore
                        // interrupted
                        // status
                    }

                }
            URL outputURL = webClient.getCurrentWindow().getEnclosedPage().getUrl();
            stream = webClient.getCurrentWindow().getEnclosedPage().getWebResponse().getContentAsStream();
            return new HTTPDownloadResponse(stream, outputURL);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static HTTPResponse get(String url, Map<String, String> cookies, Map<String, String> params) {
        HTTPResponse response = new HTTPResponse();

        try {
            WebRequest wr = new WebRequest(new URL(url), HttpMethod.GET);
            for (Map.Entry<String, String> entry : cookies.entrySet())
                webClient.getCookieManager().addCookie(new Cookie("spigotmc.org", entry.getKey(), entry.getValue()));
            List<NameValuePair> paramsPair = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> entry : params.entrySet())
                paramsPair.add(new NameValuePair(entry.getKey(), entry.getValue()));
            wr.setRequestParameters(paramsPair);
            Page page = webClient.getPage(wr);
            if (page instanceof HtmlPage)
                if (((HtmlPage) page).asXml().contains("DDoS protection by Cloudflare")) {
                    // DDOS protection
                    try {
                        Thread.sleep(9000);
                        Request.setDdosBypass(false);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); // restore
                        // interrupted
                        // status
                    }
                    if (webClient.getPage(wr) instanceof UnexpectedPage) {
                        UnexpectedPage unexpectedPage = webClient.getPage(wr);
                        System.out.println("UNEXPECTED PAGE: " + unexpectedPage.getWebResponse().getStatusMessage());
                    } else
                        page = webClient.getPage(wr);
                }
            Map<String, String> cookiesMap = new HashMap<String, String>();
            for (Cookie cookie : webClient.getCookieManager().getCookies()) {
                cookiesMap.put(cookie.getName(), cookie.getValue());
            }

            Document doc = Jsoup.parse(((HtmlPage) page).asXml());
            response.setDocument(doc);
            response.setHtml(doc.html());

            Map<String, String> resultCookies = new HashMap<String, String>();
            for (Cookie cookie : webClient.getCookieManager().getCookies()) {
                resultCookies.put(cookie.getName(), cookie.getValue());
            }
            response.setCookies(resultCookies);
            webClient.closeAllWindows();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return response;
    }

    public static HTTPResponse post(String url, Map<String, String> cookies, Map<String, String> params) {
        try {
            if (Request.isRateLimit()) {
                Thread.sleep(SpigotSiteCore.getRateLimitTimeout());
                Request.setRateLimit(false);
            }
        } catch (InterruptedException e1) {
            Request.setRateLimit(false);
        }
        HTTPResponse response = new HTTPResponse();

        try {
            WebRequest wr = new WebRequest(new URL(url), HttpMethod.POST);
            for (Map.Entry<String, String> entry : cookies.entrySet())
                webClient.getCookieManager().addCookie(new Cookie("spigotmc.org", entry.getKey(), entry.getValue()));
            List<NameValuePair> paramsPair = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> entry : params.entrySet())
                paramsPair.add(new NameValuePair(entry.getKey(), entry.getValue()));
            wr.setRequestParameters(paramsPair);
            Page page = webClient.getPage(wr);
            if (page instanceof HtmlPage)
                if (((HtmlPage) page).asXml().contains("DDoS protection by Cloudflare")) {
                    // DDOS protection
                    try {
                        Thread.sleep(9000);
                        Request.setDdosBypass(false);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); // restore
                        // interrupted
                        // status
                    }
                    wr = new WebRequest(new URL(url), HttpMethod.POST);
                    paramsPair = new ArrayList<NameValuePair>();
                    for (Map.Entry<String, String> entry : params.entrySet())
                        paramsPair.add(new NameValuePair(entry.getKey(), entry.getValue()));
                    wr.setRequestParameters(paramsPair);

                    if (webClient.getPage(wr) instanceof UnexpectedPage) {
                        UnexpectedPage unexpectedPage = webClient.getPage(wr);
                        System.out.println("UNEXPECTED PAGE: " + unexpectedPage.getWebResponse().getStatusMessage());
                    } else
                        page = webClient.getPage(wr);
                }

            Map<String, String> cookiesMap = new HashMap<String, String>();
            for (Cookie cookie : webClient.getCookieManager().getCookies()) {
                cookiesMap.put(cookie.getName(), cookie.getValue());
            }
            if (page instanceof HtmlPage) {
                Document doc = Jsoup.parse(((HtmlPage) page).asXml());
                response.setDocument(doc);
                response.setHtml(doc.html());
            } else {
                response.setHtml(page.getWebResponse().getContentAsString());
                Document doc = Jsoup.parse(response.getHtml());
                response.setDocument(doc);
            }

            Map<String, String> resultCookies = new HashMap<String, String>();
            for (Cookie cookie : webClient.getCookieManager().getCookies()) {
                resultCookies.put(cookie.getName(), cookie.getValue());
            }
            response.setCookies(resultCookies);
            webClient.closeAllWindows();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return response;
    }
}

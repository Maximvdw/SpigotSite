package be.maximvdw.spigotsite.http;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.gargoylesoftware.htmlunit.util.NameValuePair;

public class HTTPUnitRequest {
	public static HTTPResponse get(String url, Map<String, String> cookies,
			Map<String, String> params) {
		HTTPResponse response = new HTTPResponse();

		try {
			WebClient webClient = new WebClient(BrowserVersion.CHROME);
			webClient.getOptions().setJavaScriptEnabled(true);
			webClient.getOptions().setTimeout(30000);
			webClient.getOptions().setCssEnabled(false);
			webClient.getOptions().setRedirectEnabled(true);
			webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			webClient.getOptions().setPrintContentOnFailingStatusCode(false);
			java.util.logging.Logger.getLogger("com.gargoylesoftware")
					.setLevel(Level.OFF);
			WebRequest wr = new WebRequest(new URL(url), HttpMethod.GET);
			for (Map.Entry<String, String> entry : cookies.entrySet())
				webClient.getCookieManager().addCookie(
						new Cookie("spigotmc.org", entry.getKey(), entry
								.getValue()));
			List<NameValuePair> paramsPair = new ArrayList<NameValuePair>();
			for (Map.Entry<String, String> entry : params.entrySet())
				paramsPair.add(new NameValuePair(entry.getKey(), entry
						.getValue()));
			wr.setRequestParameters(paramsPair);
			Page page = webClient.getPage(wr);
			if (page instanceof HtmlPage)
				if (((HtmlPage) page).asXml().contains(
						"DDoS protection by CloudFlare")) {

					// DDOS protection
					try {
						Thread.sleep(9000);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt(); // restore
															// interrupted
															// status
					}
					if (webClient.getPage(wr) instanceof UnexpectedPage) {
						UnexpectedPage unexpectedPage = webClient.getPage(wr);
						System.out.println("UNEXPECTED PAGE: "
								+ unexpectedPage.getWebResponse()
										.getStatusMessage());
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

	public static HTTPResponse post(String url, Map<String, String> cookies,
			Map<String, String> params) {
		HTTPResponse response = new HTTPResponse();

		try {
			WebClient webClient = new WebClient(BrowserVersion.CHROME);
			webClient.getOptions().setJavaScriptEnabled(true);
			webClient.getOptions().setTimeout(30000);
			webClient.getOptions().setCssEnabled(false);
			webClient.getOptions().setRedirectEnabled(true);
			webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			webClient.getOptions().setPrintContentOnFailingStatusCode(false);
			webClient.getOptions().setCssEnabled(false);
			java.util.logging.Logger.getLogger("com.gargoylesoftware")
					.setLevel(Level.OFF);
			WebRequest wr = new WebRequest(new URL(url), HttpMethod.POST);
			for (Map.Entry<String, String> entry : cookies.entrySet())
				webClient.getCookieManager().addCookie(
						new Cookie("spigotmc.org", entry.getKey(), entry
								.getValue()));
			List<NameValuePair> paramsPair = new ArrayList<NameValuePair>();
			for (Map.Entry<String, String> entry : params.entrySet())
				paramsPair.add(new NameValuePair(entry.getKey(), entry
						.getValue()));
			wr.setRequestParameters(paramsPair);
			Page page = webClient.getPage(wr);
			if (page instanceof HtmlPage)
				if (((HtmlPage) page).asXml().contains(
						"DDoS protection by CloudFlare")) {
					// DDOS protection
					try {
						Thread.sleep(9000);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt(); // restore
															// interrupted
															// status
					}
					wr = new WebRequest(new URL(url), HttpMethod.POST);
					paramsPair = new ArrayList<NameValuePair>();
					for (Map.Entry<String, String> entry : params.entrySet())
						paramsPair.add(new NameValuePair(entry.getKey(), entry
								.getValue()));
					wr.setRequestParameters(paramsPair);

					if (webClient.getPage(wr) instanceof UnexpectedPage) {
						UnexpectedPage unexpectedPage = webClient.getPage(wr);
						System.out.println("UNEXPECTED PAGE: "
								+ unexpectedPage.getWebResponse()
										.getStatusMessage());
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
}

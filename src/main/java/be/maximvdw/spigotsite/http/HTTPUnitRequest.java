package be.maximvdw.spigotsite.http;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import be.maximvdw.spigotsite.SpigotSiteCore;

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
	private static boolean rateLimit = false;

	public static HTTPDownloadResponse downloadFile(String url,
			Map<String, String> cookies) {
//		try {
//			if (rateLimit == false) {
//				rateLimit = true;
//				Thread.sleep(SpigotSiteCore.getRateLimitTimeout());
//				rateLimit = false;
//			} else {
//				while (rateLimit) {
//					Thread.sleep(SpigotSiteCore.getRateLimitTimeout());
//				}
//				rateLimit = true;
//				Thread.sleep(SpigotSiteCore.getRateLimitTimeout());
//				rateLimit = false;
//			}
//		} catch (InterruptedException e1) {
//			rateLimit = false;
//		}
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
			InputStream stream = null;
			Page page = webClient.getPage(wr);
			if (page instanceof HtmlPage)
				if (((HtmlPage) page).asXml().contains(
						"DDoS protection by CloudFlare")
						|| ((HtmlPage) page)
								.asXml()
								.contains(
										"Checking your browser before accessing mc-market.org. This process is automatic. Your browser will redirect to your requested content shortly. Please allow up to 5 seconds")) {

					// DDOS protection
					try {
						Thread.sleep(9000);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt(); // restore
															// interrupted
															// status
					}

				}
			URL outputURL = webClient.getCurrentWindow().getEnclosedPage()
					.getUrl();
			stream = webClient.getCurrentWindow().getEnclosedPage()
					.getWebResponse().getContentAsStream();
			return new HTTPDownloadResponse(stream, outputURL);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static HTTPResponse get(String url, Map<String, String> cookies,
			Map<String, String> params) {
//		try {
//			if (rateLimit == false) {
//				rateLimit = true;
//				Thread.sleep(SpigotSiteCore.getRateLimitTimeout());
//				rateLimit = false;
//			} else {
//				while (rateLimit) {
//					Thread.sleep(SpigotSiteCore.getRateLimitTimeout());
//				}
//				rateLimit = true;
//				Thread.sleep(SpigotSiteCore.getRateLimitTimeout());
//				rateLimit = false;
//			}
//		} catch (InterruptedException e1) {
//			rateLimit = false;
//		}
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
						"DDoS protection by CloudFlare")
						|| ((HtmlPage) page)
								.asXml()
								.contains(
										"Checking your browser before accessing mc-market.org. This process is automatic. Your browser will redirect to your requested content shortly. Please allow up to 5 seconds")) {

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
		try {
			if (rateLimit == false) {
				rateLimit = true;
				Thread.sleep(SpigotSiteCore.getRateLimitTimeout());
				rateLimit = false;
			} else {
				while (rateLimit) {
					Thread.sleep(SpigotSiteCore.getRateLimitTimeout());
				}
				rateLimit = true;
				Thread.sleep(SpigotSiteCore.getRateLimitTimeout());
				rateLimit = false;
			}
		} catch (InterruptedException e1) {
			rateLimit = false;
		}
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
						"DDoS protection by CloudFlare")
						|| ((HtmlPage) page)
								.asXml()
								.contains(
										"Checking your browser before accessing mc-market.org. This process is automatic. Your browser will redirect to your requested content shortly. Please allow up to 5 seconds")) {

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

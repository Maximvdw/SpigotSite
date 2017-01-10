package be.maximvdw.spigotsite.http;

import java.util.Map;

public abstract class Request {
	private static boolean ddosBypass = true;
	private static boolean rateLimit = false;

	public static HTTPResponse get(String url, Map<String, String> cookies,
			Map<String, String> params) {
		if (isDdosBypass()) {
			return HTTPUnitRequest.get(url, cookies, params);
		} else {
			HTTPResponse response = JsoupRequest.get(new HTTPRequest(url, cookies, params));
			if (response == null && isDdosBypass()){
                return get(url,cookies,params);
			}
			return response;
		}
	}

	public static HTTPResponse post(String url, Map<String, String> cookies,
			Map<String, String> params) {
		if (isDdosBypass()) {
			return HTTPUnitRequest.post(url, cookies, params);
		} else {
			HTTPResponse response = JsoupRequest.post(new HTTPRequest(url, cookies, params));
			if (response == null && isDdosBypass()){
				return post(url,cookies,params);
			}
            return response;
		}
	}

	public static boolean isDdosBypass() {
		return ddosBypass;
	}

	public static void setDdosBypass(boolean ddosBypass) {
		Request.ddosBypass = ddosBypass;
	}

	public static boolean isRateLimit() {
		return rateLimit;
	}

	public static void setRateLimit(boolean rateLimit) {
		Request.rateLimit = rateLimit;
	}
}

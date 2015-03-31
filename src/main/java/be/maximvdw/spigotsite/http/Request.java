package be.maximvdw.spigotsite.http;

import java.util.Map;

public abstract class Request {
	private static boolean ddosBypass = false;

	public static HTTPResponse get(String url, Map<String, String> cookies,
			Map<String, String> params) {
		if (isDdosBypass()) {
			return HTTPUnitRequest.get(url, cookies, params);
		} else {
			return JsoupRequest.get(url, cookies, params);
		}
	}

	public static HTTPResponse post(String url, Map<String, String> cookies,
			Map<String, String> params) {
		if (isDdosBypass()) {
			return HTTPUnitRequest.post(url, cookies, params);
		} else {
			return JsoupRequest.post(url, cookies, params);
		}
	}

	public static boolean isDdosBypass() {
		return ddosBypass;
	}

	public static void setDdosBypass(boolean ddosBypass) {
		Request.ddosBypass = ddosBypass;
	}
}

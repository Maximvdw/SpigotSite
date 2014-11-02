package be.maximvdw.spigotsite.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Map;

public class HttpUtils {
	/**
	 * User Agent
	 */
	private final static String USER_AGENT = "Mozilla/5.0";

	/**
	 * Get the body contents of an url
	 * 
	 * @param url
	 *            URL Link
	 * @return String with the body
	 * @throws IOException
	 */
	public static String getHtmlSource(String url) throws IOException {
		// The URL address of the page to open.
		URL address = new URL(url);

		// Open the address and create a BufferedReader with the source code.
		InputStreamReader pageInput = new InputStreamReader(
				address.openStream());
		BufferedReader source = new BufferedReader(pageInput);

		return source.readLine();
	}

	/**
	 * Send a get request
	 * 
	 * @param url
	 *            Url
	 * @return Response
	 * @throws Exception
	 *             Exception
	 */
	public static HttpResponse sendGetRequest(String url, String[] inputcookies)
			throws Exception {
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		// add request header
		con.setRequestProperty("User-Agent", USER_AGENT);

		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		String[] cookies = new String[0];
		if (con.getHeaderField("Set-Cookie") != null)
			cookies = con.getHeaderField("Set-Cookie").split(";");

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		return new HttpResponse(response.toString(), con.getResponseCode(),
				cookies);
	}

	/**
	 * Send post request
	 * 
	 * @param url
	 *            Url
	 * @param params
	 *            Params
	 * @return Response
	 * @throws Exception
	 *             Exception
	 */
	public static HttpResponse sendPostRequest(String url,
			Map<String, String> params, String[] inputcookies) throws Exception {

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		String urlParameters = "";
		for (String key : params.keySet()) {
			String value = params.get(key);
			urlParameters += key + "=" + value + "&";
		}
		urlParameters = urlParameters.substring(0, urlParameters.length() - 1);

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		String[] cookies = new String[0];
		if (con.getHeaderField("Set-Cookie") != null)
			cookies = con.getHeaderField("Set-Cookie").split(";");

		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		return new HttpResponse(response.toString(), con.getResponseCode(),
				cookies);

	}

	/**
	 * Download a file
	 * 
	 * @param url
	 *            URL
	 * @param location
	 *            Output location
	 * @throws IOException
	 *             Input Output exception
	 */
	public static void downloadFile(String url, String location)
			throws IOException {
		URL website = new URL(url);
		ReadableByteChannel rbc = Channels.newChannel(website.openStream());
		FileOutputStream fos = new FileOutputStream(location);
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		fos.close();
	}
}

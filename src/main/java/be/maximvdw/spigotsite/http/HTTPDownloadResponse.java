package be.maximvdw.spigotsite.http;

import java.io.InputStream;
import java.net.URL;

public class HTTPDownloadResponse {
	private InputStream stream = null;
	private URL url = null;

	public HTTPDownloadResponse(InputStream stream, URL url) {
		setStream(stream);
		setUrl(url);
	}

	public InputStream getStream() {
		return stream;
	}

	public void setStream(InputStream stream) {
		this.stream = stream;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}
}

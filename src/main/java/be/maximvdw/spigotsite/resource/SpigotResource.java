package be.maximvdw.spigotsite.resource;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

import be.maximvdw.spigotsite.api.resource.Resource;
import be.maximvdw.spigotsite.api.resource.ResourceCategory;
import be.maximvdw.spigotsite.api.user.User;

public final class SpigotResource implements Resource {
	private int id = 0;
	private String name = "";
	private String version = "";
	private User author = null;
	private ResourceCategory category = null;
	private boolean deleted = false;
	private String downloadURL = "";

	public SpigotResource() {

	}

	public SpigotResource(String name) {
		setResourceName(name);
	}

	public int getResourceId() {
		return id;
	}

	public void setResourceId(int id) {
		this.id = id;
	}

	public String getResourceName() {
		return name;
	}

	public String getLastVersion() {
		return this.version;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public void setResourceName(String name) {
		this.name = name;
	}

	public void setLastVersion(String version) {
		this.version = version;
	}

	public ResourceCategory getResourceCategory() {
		return category;
	}

	public String getDownloadURL() {
		return downloadURL;
	}

	public void setDownloadURL(String downloadURL) {
		this.downloadURL = downloadURL;
	}

	public File downloadResource(File output) {
		BufferedInputStream in = null;
		FileOutputStream fout = null;
		try {
			// Download the file
			final URL url = new URL(getDownloadURL());
			final int fileLength = url.openConnection().getContentLength();
			in = new BufferedInputStream(url.openStream());
			fout = new FileOutputStream(output.getAbsolutePath()
					+ File.separator + output);

			final byte[] data = new byte[1024];
			int count;
			long downloaded = 0;
			while ((count = in.read(data, 0, 1024)) != -1) {
				downloaded += count;
				fout.write(data, 0, count);
				final int percent = (int) ((downloaded * 100) / fileLength);
				if (((percent % 10) == 0)) {
					// Event
				}
			}
			return null;
		} catch (final Exception ex) {
			return null;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (fout != null) {
					fout.close();
				}
			} catch (final Exception ex) {
			}
		}
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
}

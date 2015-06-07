package be.maximvdw.spigotsite.resource;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

import be.maximvdw.spigotsite.api.resource.Rating;
import be.maximvdw.spigotsite.api.resource.Resource;
import be.maximvdw.spigotsite.api.resource.ResourceCategory;
import be.maximvdw.spigotsite.api.resource.ResourceUpdate;
import be.maximvdw.spigotsite.api.user.User;
import be.maximvdw.spigotsite.user.SpigotUser;

public class SpigotResource implements Resource {
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

	public File downloadResource(User user, File output) {
		try {
			if (output.exists()) {
				output.delete();
			}
			// Open a URL Stream
			Response resultImageResponse = Jsoup
					.connect(getDownloadURL())
					.cookies(
							user != null ? ((SpigotUser) user).getCookies()
									: new HashMap<String, String>())
					.ignoreContentType(true).execute();

			// output here
			FileOutputStream out = (new FileOutputStream(output));
			out.write(resultImageResponse.bodyAsBytes()); // resultImageResponse.body()
															// is where the
															// image's
															// contents are.
			out.close();
			return output;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public int getAverageRating() {
		// TODO Auto-generated method stub
		return 0;
	}

	public List<Rating> getRatings() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ResourceUpdate> gerResourceUpdates() {
		// TODO Auto-generated method stub
		return null;
	}
}

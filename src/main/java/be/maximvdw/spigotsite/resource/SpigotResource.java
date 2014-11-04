package be.maximvdw.spigotsite.resource;

import java.io.File;

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
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
}

package be.maximvdw.spigotsite.resource;

import be.maximvdw.spigotsite.api.resource.Resource;
import be.maximvdw.spigotsite.api.resource.ResourceCategory;
import be.maximvdw.spigotsite.api.user.User;

public final class SpigotResource implements Resource {
	private int id = 0;
	private String name = "";
	private String version = "";
	private User author = null;
	private ResourceCategory category = null;

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
}

package be.maximvdw.spigotsite;

import be.maximvdw.spigotsite.api.SpigotSite;
import be.maximvdw.spigotsite.api.SpigotSiteAPI;
import be.maximvdw.spigotsite.api.forum.ForumManager;
import be.maximvdw.spigotsite.api.resource.ResourceManager;
import be.maximvdw.spigotsite.api.user.UserManager;
import be.maximvdw.spigotsite.forum.SpigotForumManager;
import be.maximvdw.spigotsite.resource.SpigotResourceManager;
import be.maximvdw.spigotsite.user.SpigotUserManager;

public class SpigotSiteCore implements SpigotSiteAPI {
	/* Spigot User Manager */
	private UserManager userManager = null;
	/* Spigot Resource Manager */
	private ResourceManager resourceManager = null;
	/* Spigot Forum Manager */
	private ForumManager forumManager = null;

	public SpigotSiteCore() {
		// Set managers
		userManager = new SpigotUserManager();
		resourceManager = new SpigotResourceManager();
		forumManager = new SpigotForumManager();

		// Set Site API
		SpigotSite.setAPI(this);

	}

	public UserManager getUserManager() {
		return userManager;
	}

	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	public ResourceManager getResourceManager() {
		return resourceManager;
	}

	public void setResourceManager(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}

	public ForumManager getForumManager() {
		return forumManager;
	}

	public void setForumManager(ForumManager forumManager) {
		this.forumManager = forumManager;
	}
}

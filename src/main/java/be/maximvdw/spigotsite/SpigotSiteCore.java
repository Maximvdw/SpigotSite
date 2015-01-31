package be.maximvdw.spigotsite;

import java.util.HashMap;
import java.util.Map;

import be.maximvdw.spigotsite.api.SpigotSite;
import be.maximvdw.spigotsite.api.SpigotSiteAPI;
import be.maximvdw.spigotsite.api.forum.ForumManager;
import be.maximvdw.spigotsite.api.resource.ResourceManager;
import be.maximvdw.spigotsite.api.user.ConversationManager;
import be.maximvdw.spigotsite.api.user.UserManager;
import be.maximvdw.spigotsite.forum.SpigotForumManager;
import be.maximvdw.spigotsite.resource.SpigotResourceManager;
import be.maximvdw.spigotsite.user.SpigotConversationManager;
import be.maximvdw.spigotsite.user.SpigotUserManager;

public class SpigotSiteCore implements SpigotSiteAPI {
	/* Spigot User Manager */
	private UserManager userManager = null;
	/* Spigot Resource Manager */
	private ResourceManager resourceManager = null;
	/* Spigot Forum Manager */
	private ForumManager forumManager = null;
	/* Spigot Conversation Manager */
	private ConversationManager conversationManager = null;

	private static Map<String, String> baseCookies = new HashMap<String, String>();

	public SpigotSiteCore() {
		// Set managers
		userManager = new SpigotUserManager();
		resourceManager = new SpigotResourceManager();
		forumManager = new SpigotForumManager();
		conversationManager = new SpigotConversationManager();

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

	public static Map<String, String> getBaseCookies() {
		return baseCookies;
	}

	public static void setBaseCookies(Map<String, String> baseCookies) {
		SpigotSiteCore.baseCookies = baseCookies;
	}

	public ConversationManager getConversationManager() {
		return conversationManager;
	}
}

package be.maximvdw.spigotsite;

import org.bukkit.plugin.java.JavaPlugin;

import be.maximvdw.spigotsite.api.SpigotSite;
import be.maximvdw.spigotsite.api.SpigotSiteAPI;
import be.maximvdw.spigotsite.api.resource.ResourceManager;
import be.maximvdw.spigotsite.api.user.UserManager;
import be.maximvdw.spigotsite.resource.SpigotResourceManager;
import be.maximvdw.spigotsite.user.SpigotUserManager;

public class SpigotSitePlugin extends JavaPlugin implements SpigotSiteAPI {
	/* Spigot User Manager */
	private UserManager userManager = null;
	/* Spigot Resource Manager */
	private ResourceManager resourceManager = null;

	@Override
	public void onEnable() {
		super.onEnable();
		// Set managers
		userManager = new SpigotUserManager();
		resourceManager = new SpigotResourceManager();

		// Set Site API
		SpigotSite.setAPI(this);
	}

	public UserManager getUserManager() {
		return userManager;
	}

	public ResourceManager getResourceManager() {
		return resourceManager;
	}

}

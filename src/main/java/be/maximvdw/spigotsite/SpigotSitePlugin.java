package be.maximvdw.spigotsite;

import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import be.maximvdw.spigotsite.api.SpigotSite;
import be.maximvdw.spigotsite.api.SpigotSiteAPI;
import be.maximvdw.spigotsite.api.resource.Resource;
import be.maximvdw.spigotsite.api.resource.ResourceCategory;
import be.maximvdw.spigotsite.api.resource.ResourceManager;
import be.maximvdw.spigotsite.api.user.User;
import be.maximvdw.spigotsite.api.user.UserManager;
import be.maximvdw.spigotsite.api.user.exceptions.InvalidCredentialsException;
import be.maximvdw.spigotsite.resource.SpigotResourceManager;
import be.maximvdw.spigotsite.ui.SendConsole;
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

		try {
			ResourceManager resourceManager = SpigotSite.getAPI()
					.getResourceManager();
			List<ResourceCategory> categories = resourceManager
					.getResourceCategories();
			for (ResourceCategory category : categories) {
				SendConsole.info(category.getCategoryName() + "  "
						+ category.getCategoryId() + "   "
						+ category.getResourceCount());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public UserManager getUserManager() {
		return userManager;
	}

	public ResourceManager getResourceManager() {
		return resourceManager;
	}

}

package be.maximvdw.spigotsite;

import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import be.maximvdw.spigotsite.api.SpigotSite;
import be.maximvdw.spigotsite.api.resource.Resource;
import be.maximvdw.spigotsite.api.resource.ResourceCategory;
import be.maximvdw.spigotsite.api.resource.ResourceManager;
import be.maximvdw.spigotsite.ui.SendConsole;

public class SpigotSitePlugin extends JavaPlugin {
	@Override
	public void onEnable() {
		super.onEnable();
		new SpigotSiteCore();

		try {
			ResourceManager resourceManager = SpigotSite.getAPI()
					.getResourceManager();
			List<ResourceCategory> categories = resourceManager
					.getResourceCategories();
			for (ResourceCategory category : categories) {
				SendConsole.info(category.getCategoryName() + "   Count: "
						+ category.getResourceCount());
				List<Resource> resources = resourceManager
						.getResourcesByCategory(category);
				for (Resource resource : resources) {
					SendConsole.info("\t" + resource.getResourceName()
							+ "  Version: " + resource.getLastVersion()
							+ "   By " + resource.getAuthor().getUsername());
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

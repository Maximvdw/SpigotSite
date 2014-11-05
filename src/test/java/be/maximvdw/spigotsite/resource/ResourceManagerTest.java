package be.maximvdw.spigotsite.resource;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import be.maximvdw.spigotsite.SpigotSiteCore;
import be.maximvdw.spigotsite.api.SpigotSite;
import be.maximvdw.spigotsite.api.resource.Resource;
import be.maximvdw.spigotsite.api.resource.ResourceCategory;
import be.maximvdw.spigotsite.api.resource.ResourceManager;

public class ResourceManagerTest {

	@Before
	public void init() {
		new SpigotSiteCore();
	}

	@Test(timeout = 5000)
	public void getResourceByIdTest() {
		ResourceManager resourceManager = SpigotSite.getAPI()
				.getResourceManager();
		// Test Tab plugin
		Resource resource = resourceManager.getResourceById(1448);
		assertNotNull(resource);
		assertEquals("Tab", resource.getResourceName());
	}

	@Test(timeout = 5000)
	public void getResourceCategories() {
		ResourceManager resourceManager = SpigotSite.getAPI()
				.getResourceManager();
		List<ResourceCategory> categories = resourceManager
				.getResourceCategories();
		assertNotNull(categories);
	}

}

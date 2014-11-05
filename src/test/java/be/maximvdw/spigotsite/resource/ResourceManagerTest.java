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
		System.out.println("Testing 'getResourceById 1448' ...");
		ResourceManager resourceManager = SpigotSite.getAPI()
				.getResourceManager();
		// Test Tab plugin
		Resource resource = resourceManager.getResourceById(1448);
		assertNotNull(resource);
		assertNotNull(resource.getAuthor());

		System.out.println("Resource name: " + resource.getResourceName());
		System.out.println("Resource id: " + resource.getResourceId());
		System.out.println("Resource author: "
				+ resource.getAuthor().getUsername() + " ["
				+ resource.getAuthor().getUserId() + "]");
		System.out.println("Resource version: " + resource.getLastVersion());
		assertEquals("Tab", resource.getResourceName());
		assertEquals(1448, resource.getResourceId());
		assertEquals("Maximvdw", resource.getAuthor().getUsername());
	}

	@Test(timeout = 5000)
	public void getResourceCategoriesTest() {
		System.out.println("Testing 'getResourceCategories' ...");
		ResourceManager resourceManager = SpigotSite.getAPI()
				.getResourceManager();
		List<ResourceCategory> categories = resourceManager
				.getResourceCategories();
		for (ResourceCategory category : categories) {
			System.out.println(category.getCategoryName() + " ["
					+ category.getCategoryId() + "] - Count: "
					+ category.getResourceCount());
		}
		assertNotNull(categories);
	}

	@Test(timeout = 5000)
	public void getResourceCategoryByIdTest() {
		System.out.println("Testing 'getResourceCategoryById 2' ...");
		ResourceManager resourceManager = SpigotSite.getAPI()
				.getResourceManager();
		ResourceCategory category = resourceManager.getResourceCategoryById(2);
		assertNotNull(category);
	}

	@Test(timeout = 15000)
	public void getResourcesByCategoryTest() {
		System.out.println("Testing 'getResourcesByCategory 2' ...");
		ResourceManager resourceManager = SpigotSite.getAPI()
				.getResourceManager();
		ResourceCategory category = resourceManager.getResourceCategoryById(2);
		List<Resource> resources = resourceManager
				.getResourcesByCategory(category);
		assertNotNull(resources);
		System.out.println("Expected count: " + category.getResourceCount()
				+ "  Fetched count: " + resources.size());
		assertEquals(category.getResourceCount(), resources.size());
		for (Resource resource : resources) {
			System.out.println(resource.getResourceName() + " ["
					+ resource.getResourceId() + "]" + " "
					+ resource.getLastVersion() + "\n\tBy "
					+ resource.getAuthor().getUsername() + " ["
					+ resource.getAuthor().getUserId() + "]");
			assertNotNull(resource.getAuthor());
		}
	}

	@Test(timeout = 5000)
	public void getResourcesByUserTest() {
		System.out.println("Testing 'getResourcesByUser 6687' ...");
		ResourceManager resourceManager = SpigotSite.getAPI()
				.getResourceManager();
		List<Resource> resources = resourceManager.getResourcesByUser(6687);
		assertNotNull(resources);
		System.out.println("Resources by User 6687:");
		for (Resource resource : resources) {
			System.out.println(resource.getResourceName() + " ["
					+ resource.getResourceId() + "]" + " "
					+ resource.getLastVersion());
			assertNotNull(resource.getAuthor());
		}
	}
}

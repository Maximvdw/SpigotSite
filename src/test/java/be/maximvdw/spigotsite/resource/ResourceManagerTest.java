package be.maximvdw.spigotsite.resource;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import be.maximvdw.spigotsite.SpigotSiteCore;
import be.maximvdw.spigotsite.api.SpigotSite;
import be.maximvdw.spigotsite.api.resource.PremiumResource;
import be.maximvdw.spigotsite.api.resource.Resource;
import be.maximvdw.spigotsite.api.resource.ResourceCategory;
import be.maximvdw.spigotsite.api.resource.ResourceManager;
import be.maximvdw.spigotsite.api.user.User;
import be.maximvdw.spigotsite.api.user.UserManager;
import be.maximvdw.spigotsite.api.user.exceptions.InvalidCredentialsException;

public class ResourceManagerTest {
	private String username = "";
	private String password = "";

	@Before
	public void init() {
		new SpigotSiteCore();
		BufferedReader br = null;
		try {
			if (new File("/var/lib/jenkins/credentials.txt").exists())
				br = new BufferedReader(new FileReader(
						"/var/lib/jenkins/credentials.txt"));
			else
				br = new BufferedReader(new FileReader(
						"C:\\Users\\Maxim\\Documents\\credentials.txt"));
			username = br.readLine();
			password = br.readLine();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
		// assertEquals(category.getResourceCount(), resources.size());
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

	@Test(timeout = 15000)
	public void getBuyers() throws InvalidCredentialsException {
		System.out.println("Testing 'getBuyers 2691' ...");
		UserManager userManager = SpigotSite.getAPI().getUserManager();
		ResourceManager resourceManager = SpigotSite.getAPI()
				.getResourceManager();
		User user = userManager.authenticate(username, password);
		PremiumResource resource = (PremiumResource) resourceManager
				.getResourceById(2691);
		List<User> buyers = resource.getBuyers();
		for (User buyer : buyers) {
			System.out.println("\t" + buyer.getUsername());
		}
	}
}

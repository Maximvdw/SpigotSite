package be.maximvdw.spigotsite.resource;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import be.maximvdw.spigotsite.SpigotSiteCore;
import be.maximvdw.spigotsite.api.SpigotSite;
import be.maximvdw.spigotsite.api.exceptions.ConnectionFailedException;
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
				br = new BufferedReader(new FileReader("/var/lib/jenkins/credentials.txt"));
			else
				br = new BufferedReader(new FileReader("D:\\maxim\\Documents\\credentials.txt"));
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

	@Test(timeout = 15000)
	public void getResourceByIdTest() {
		System.out.println("Testing 'getResourceById 578' ...");
		ResourceManager resourceManager = SpigotSite.getAPI().getResourceManager();
		// Test Tab plugin
		Resource resource = resourceManager.getResourceById(578);
		assertNotNull(resource);
		assertNotNull(resource.getAuthor());

		System.out.println("Resource name: " + resource.getResourceName());
		System.out.println("Resource id: " + resource.getResourceId());
		System.out.println("Resource author: " + resource.getAuthor().getUsername() + " ["
				+ resource.getAuthor().getUserId() + "]");
		System.out.println("Resource version: " + resource.getLastVersion());
		System.out.println("Resource URL: " + resource.getDownloadURL());
		assertEquals("Auto-In", resource.getResourceName());
		assertEquals(578, resource.getResourceId());
		assertEquals("GoToFinal", resource.getAuthor().getUsername());
	}

	@Test(timeout = 15000)
	public void getResourceCategoriesTest() {
		System.out.println("Testing 'getResourceCategories' ...");
		ResourceManager resourceManager = SpigotSite.getAPI().getResourceManager();
		List<ResourceCategory> categories = resourceManager.getResourceCategories();
		for (ResourceCategory category : categories) {
			System.out.println(category.getCategoryName() + " [" + category.getCategoryId() + "] - Count: "
					+ category.getResourceCount());
		}
		assertNotNull(categories);
	}

	@Test(timeout = 15000)
	public void getResourceCategoryByIdTest() {
		System.out.println("Testing 'getResourceCategoryById 2' ...");
		ResourceManager resourceManager = SpigotSite.getAPI().getResourceManager();
		ResourceCategory category = resourceManager.getResourceCategoryById(2);
		assertNotNull(category);
	}

	@Test(timeout = 150000)
	public void getResourcesByCategoryTest() {
		System.out.println("Testing 'getResourcesByCategory 2' ...");
		ResourceManager resourceManager = SpigotSite.getAPI().getResourceManager();
		ResourceCategory category = resourceManager.getResourceCategoryById(2);
		List<Resource> resources = resourceManager.getResourcesByCategory(category);
		assertNotNull(resources);
		System.out.println("Expected count: " + category.getResourceCount() + " Fetched count: " + resources.size());
		// assertEquals(category.getResourceCount(), resources.size());
		for (Resource resource : resources) {
			System.out.println(resource.getResourceName() + " [" + resource.getResourceId() + "]" + " "
					+ resource.getLastVersion() + "\n\tBy " + resource.getAuthor().getUsername() + " ["
					+ resource.getAuthor().getUserId() + "]");
			assertNotNull(resource.getAuthor());
		}
	}

	@Test(timeout = 15000)
	public void getResourcesByUserTest() {
		System.out.println("Testing 'getResourcesByUser 6687' ...");
		ResourceManager resourceManager = SpigotSite.getAPI().getResourceManager();
		List<Resource> resources = resourceManager.getResourcesByUser(6687);
		assertNotNull(resources);
		System.out.println("Resources by User 6687:");
		for (Resource resource : resources) {
			System.out.println(resource.getResourceName() + " [" + resource.getResourceId() + "]" + " "
					+ resource.getLastVersion());
			assertNotNull(resource.getAuthor());
		}
	}

	@Test(timeout = 15000)
	public void getPurchasedResourcesByUserTest() throws InvalidCredentialsException, ConnectionFailedException {
		System.out.println("Testing 'getPurchasedResourcesByUser' ...");
		ResourceManager resourceManager = SpigotSite.getAPI().getResourceManager();
		UserManager userManager = SpigotSite.getAPI().getUserManager();
		// Log in
		User user = userManager.authenticate(username, password);

		List<Resource> resources = resourceManager.getPurchasedResources(user);
		assertNotNull(resources);
		System.out.println("Purchased Resources by User:");
		for (Resource resource : resources) {
			System.out.println(resource.getResourceName() + " [" + resource.getResourceId() + "]" + " "
					+ resource.getLastVersion());
		}
	}

	@Test
	public void downloadResource() throws IOException {
		System.out.println("Testing 'downloadResource 578' ...");
		ResourceManager resourceManager = SpigotSite.getAPI().getResourceManager();
		Resource res = resourceManager.getResourceById(578);
		File tmpFile = File.createTempFile("resource-", ".jar");
		res.downloadResource(null, tmpFile);
		tmpFile.delete();
	}
	
	@Test
	public void downloadPremiumResource() throws InvalidCredentialsException, IOException {
		System.out.println("Testing 'downloadPremiumResource 1458' ...");
		UserManager userManager = SpigotSite.getAPI().getUserManager();
		// Log in
		User user = userManager.authenticate(username, password);
		ResourceManager resourceManager = SpigotSite.getAPI().getResourceManager();
		Resource res = resourceManager.getResourceById(1458,user);
		File tmpFile = File.createTempFile("resource-", ".jar");
		res.downloadResource(user, tmpFile);
		tmpFile.delete();
	}

	@Test(timeout = 15000)
	public void getBuyers() throws InvalidCredentialsException, ConnectionFailedException {
		System.out.println("Testing 'getBuyers 2691' ...");
		UserManager userManager = SpigotSite.getAPI().getUserManager();
		ResourceManager resourceManager = SpigotSite.getAPI().getResourceManager();
		User user = userManager.authenticate(username, password);
		Resource resource = resourceManager.getResourceById(2691);
		PremiumResource premiumResource = (SpigotPremiumResource) resource;
		List<User> buyers = resourceManager.getPremiumResourceBuyers(premiumResource, user);
		List<User> buyers2 = resourceManager.getPremiumResourceBuyers(premiumResource, user);

		if (!buyers.isEmpty())
			assertEquals(buyers.get(0), buyers2.get(0));

		System.out.println("Buyers of " + resource.getResourceName() + ":");
		for (User buyer : buyers) {
			System.out.println("\t" + buyer.getUsername() + " [" + buyer.getUserId() + "]");
		}
	}

	@Test(timeout = 700000)
	public void getTopBuyers() throws InvalidCredentialsException, ConnectionFailedException {
		System.out.println("Testing 'get the buyers that bought all my plugins'");
		UserManager userManager = SpigotSite.getAPI().getUserManager();
		ResourceManager resourceManager = SpigotSite.getAPI().getResourceManager();
		// Log in
		User user = userManager.authenticate(username, password);

		List<User> favoriteBuyers = new ArrayList<User>();

		// Get your resources
		for (Resource resource : resourceManager.getResourcesByUser(user)) {
			// Check if the resource is premium
			if (resource instanceof PremiumResource) {
				PremiumResource premiumResource = (PremiumResource) resource;
				// Get the people who bought that plugin
				List<User> buyers = resourceManager.getPremiumResourceBuyers(premiumResource, user);
				System.out.println(
						"The plugin " + premiumResource.getResourceName() + " got " + buyers.size() + " buyers.");
				if (favoriteBuyers.size() == 0)
					favoriteBuyers = buyers;
				else {
					List<User> newFavorites = new ArrayList<User>();
					for (User buyer : buyers) {
						if (favoriteBuyers.contains(buyer))
							newFavorites.add(buyer);
					}
					favoriteBuyers = newFavorites;
				}
			}
		}

		// Your favorite buyers
		System.out.println("People who bought all your plugins:");
		for (User favBuyer : favoriteBuyers) {
			System.out.println("\t" + favBuyer.getUsername() + "  <3");
		}
	}
}

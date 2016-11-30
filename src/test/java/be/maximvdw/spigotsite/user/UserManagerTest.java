package be.maximvdw.spigotsite.user;

import be.maximvdw.spigotsite.SpigotSiteCore;
import be.maximvdw.spigotsite.UserDebugging;
import be.maximvdw.spigotsite.api.SpigotSite;
import be.maximvdw.spigotsite.api.user.User;
import be.maximvdw.spigotsite.api.user.UserManager;
import be.maximvdw.spigotsite.api.user.exceptions.InvalidCredentialsException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserManagerTest {
	private String username = "";
	private String password = "";

	@Before
	public void init() {
		new SpigotSiteCore();

		this.username = UserDebugging.username;
		this.password = UserDebugging.password;
	}

	@Test(timeout = 15000)
	public void getUsersByNameTest() {
		System.out.println("Testing 'getUsersByName' ...");
		UserManager userManager = SpigotSite.getAPI().getUserManager();
		userManager.getUsersByName("Max");
	}

	@Test(timeout = 15000)
	public void getUserByIdTest() {
		System.out.println("Testing 'getUserById 1' ...");
		UserManager userManager = SpigotSite.getAPI().getUserManager();
		User user = userManager.getUserById(1);
		System.out.println("Username: " + user.getUsername());
		System.out.println("User Id: " + user.getUserId());
		assertEquals(1, user.getUserId());
		assertEquals("md_5", user.getUsername());
	}

	@Test(timeout = 15000)
	public void getUserActivityTest() {
		System.out.println("Testing 'getUserActivityTest' ...");
		UserManager userManager = SpigotSite.getAPI().getUserManager();
		User user = userManager.getUserById(6687);
		System.out.println("Username: " + user.getUsername());
		System.out.println("User Id: " + user.getUserId());
		System.out.println("Activity: " + user.getLastActivity());
	}

	public void getUsersByRankTest() {

	}

	public void getUserRanksTest() {

	}

	@Test(timeout = 60000)
	public void logInUserTest() throws InvalidCredentialsException {
		System.out.println("Testing 'authenticate' ...");
		UserManager userManager = SpigotSite.getAPI().getUserManager();
		User user = userManager.authenticate(username, password);
		assertEquals(user.getUsername(), "Maximvdw");
		System.out.println("Logged in: " + user.getUsername() + " [" + user.getUserId() + "]");
	}
}

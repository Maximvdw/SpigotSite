package be.maximvdw.spigotsite.user;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import be.maximvdw.spigotsite.SpigotSiteCore;
import be.maximvdw.spigotsite.api.SpigotSite;
import be.maximvdw.spigotsite.api.user.User;
import be.maximvdw.spigotsite.api.user.UserManager;

public class UserManagerTest {
	@Before
	public void init() {
		new SpigotSiteCore();
	}

	@Test(timeout = 5000)
	public void getUserByIdTest() {
		UserManager userManager = SpigotSite.getAPI().getUserManager();
		User user = userManager.getUserById(1);
		assertEquals("md_5", user.getUsername());
	}
}

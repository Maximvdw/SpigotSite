package be.maximvdw.spigotsite.user;

import be.maximvdw.spigotsite.SpigotSiteCore;
import be.maximvdw.spigotsite.UserDebugging;
import be.maximvdw.spigotsite.api.SpigotSite;
import be.maximvdw.spigotsite.api.user.User;
import be.maximvdw.spigotsite.api.user.UserManager;
import be.maximvdw.spigotsite.api.user.exceptions.InvalidCredentialsException;
import be.maximvdw.spigotsite.api.user.exceptions.TwoFactorAuthenticationException;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class UserManagerTest {

	@Before
	public void init() {
		new SpigotSiteCore();
	}

	@Test(timeout = 15000)
	public void getUsernamesByNameTest() {
		System.out.println("Testing 'getUsernamesByName' ...");
		UserManager userManager = SpigotSite.getAPI().getUserManager();
		List<String> users = userManager.getUsernamesByName("Max");
		System.out.println("Found: ");
		for (String user : users){
			System.out.println("\t" + user);
		}
	}

	@Test
	public void logOutTest() throws InvalidCredentialsException, TwoFactorAuthenticationException {
        System.out.println("Testing 'Logout' ...");
        SpigotUserManager userManager = ((SpigotUserManager) SpigotSite.getAPI().getUserManager());
        User user = UserDebugging.getUser();
        assertTrue(userManager.isLoggedIn(user));
        userManager.logOff(user);
        assertFalse(userManager.isLoggedIn(user));
        user = userManager.authenticate(UserDebugging.username,UserDebugging.password,user);
        assertTrue(userManager.isLoggedIn(user));
    }

	@Test(timeout = 15000)
	public void getUserByNameTest() {
		System.out.println("Testing 'getUserByName' ...");
		UserManager userManager = SpigotSite.getAPI().getUserManager();
		User u1 = userManager.getUserByName("JamesJ");
		System.out.println("Found: " + u1.getUserId());
        assertEquals(8614,u1.getUserId());
        User u2 = userManager.getUserByName("clip");
        System.out.println("Found: " + u2.getUserId());
        assertEquals(1001,u2.getUserId());
        User u3 = userManager.getUserByName("$#$G#$");
        assertNull(u3);
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

	@Test
	public void untrustTest() throws InvalidCredentialsException, TwoFactorAuthenticationException {
		User user = UserDebugging.getUser();
		SpigotUserManager userManager = ((SpigotUserManager) SpigotSite.getAPI().getUserManager());
		userManager.untrustThisDevice(user);
		assertTrue(userManager.isLoggedIn(user));
	}

	public void getUsersByRankTest() {

	}

	public void getUserRanksTest() {

	}

	@Test(timeout = 60000)
	public void logInUserTest() throws InvalidCredentialsException, TwoFactorAuthenticationException {
		System.out.println("Testing 'authenticate' ...");
		UserManager userManager = SpigotSite.getAPI().getUserManager();
		User user = userManager.authenticate(UserDebugging.username, UserDebugging.password,UserDebugging.totpSecret);
		assertEquals(user.getUsername(), "Maximvdw");
		System.out.println("Logged in: " + user.getUsername() + " [" + user.getUserId() + "]");
        if (user.hasTwoFactorAuthentication()){
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
	}
}

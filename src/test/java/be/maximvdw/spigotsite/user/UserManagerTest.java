package be.maximvdw.spigotsite.user;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import be.maximvdw.spigotsite.SpigotSiteCore;
import be.maximvdw.spigotsite.api.SpigotSite;
import be.maximvdw.spigotsite.api.user.Conversation;
import be.maximvdw.spigotsite.api.user.User;
import be.maximvdw.spigotsite.api.user.UserManager;
import be.maximvdw.spigotsite.api.user.exceptions.InvalidCredentialsException;

public class UserManagerTest {
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
	public void getUsersByNameTest() {
		System.out.println("Testing 'getUsersByName' ...");
		UserManager userManager = SpigotSite.getAPI().getUserManager();
		userManager.getUsersByName("Max");
	}

	@Test(timeout = 5000)
	public void getUserByIdTest() {
		System.out.println("Testing 'getUserById 1' ...");
		UserManager userManager = SpigotSite.getAPI().getUserManager();
		User user = userManager.getUserById(1);
		System.out.println("Username: " + user.getUsername());
		System.out.println("User Id: " + user.getUserId());
		assertEquals(1, user.getUserId());
		assertEquals("md_5", user.getUsername());
	}

	public void getUsersByRankTest() {

	}

	public void getUserRanksTest() {

	}

	@Test(timeout = 5000)
	public void logInUserTest() throws InvalidCredentialsException {
		System.out.println("Testing 'authenticate' ...");
		UserManager userManager = SpigotSite.getAPI().getUserManager();
		User user = userManager.authenticate(username, password);
		assertEquals(user.getUsername(), "Maximvdw");
		System.out.println("Logged in: " + user.getUsername() + " ["
				+ user.getUserId() + "]");
	}
}

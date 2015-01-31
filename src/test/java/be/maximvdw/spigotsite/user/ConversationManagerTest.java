package be.maximvdw.spigotsite.user;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import be.maximvdw.spigotsite.SpigotSiteCore;
import be.maximvdw.spigotsite.api.SpigotSite;
import be.maximvdw.spigotsite.api.user.Conversation;
import be.maximvdw.spigotsite.api.user.ConversationManager;
import be.maximvdw.spigotsite.api.user.User;
import be.maximvdw.spigotsite.api.user.UserManager;
import be.maximvdw.spigotsite.api.user.exceptions.InvalidCredentialsException;

public class ConversationManagerTest {
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
	public void conversationsTest() throws InvalidCredentialsException {
		System.out.println("Testing 'getConversations' ...");
		UserManager userManager = SpigotSite.getAPI().getUserManager();
		User user = userManager.authenticate(username, password);
		ConversationManager conversationManager = SpigotSite.getAPI()
				.getConversationManager();
		List<Conversation> conversations = conversationManager
				.getConversations(user, 20);
		for (Conversation conv : conversations) {
			System.out.println(conv.getTitle() + "[" + conv.getRepliesCount()
					+ "]   BY " + conv.getAuthor().getUsername());
			if (conv.getTitle().equals("Hello World!")) {
				System.out.println("Sending reply ...");
				// conv.reply(user,
				// "This conversation has " + conv.getRepliesCount()
				// + " replies. LEAVING NOW");
				// conv.leave(user);
			}
		}
	}

	@Test(timeout = 5000)
	public void conversationSendTest() throws InvalidCredentialsException {
		System.out.println("Testing 'createConversation' ...");
		UserManager userManager = SpigotSite.getAPI().getUserManager();
		User user = userManager.authenticate(username, password);
		ConversationManager conversationManager = SpigotSite.getAPI()
				.getConversationManager();
		Set<String> recipents = new HashSet<String>();
		recipents.add("MVdWSoftware");
		conversationManager.createConversation(user, recipents, "Hello",
				"World", true, false, false);
	}
}

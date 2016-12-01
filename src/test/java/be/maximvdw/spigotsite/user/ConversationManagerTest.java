package be.maximvdw.spigotsite.user;

import be.maximvdw.spigotsite.SpigotSiteCore;
import be.maximvdw.spigotsite.UserDebugging;
import be.maximvdw.spigotsite.api.SpigotSite;
import be.maximvdw.spigotsite.api.exceptions.ConnectionFailedException;
import be.maximvdw.spigotsite.api.exceptions.SpamWarningException;
import be.maximvdw.spigotsite.api.user.Conversation;
import be.maximvdw.spigotsite.api.user.ConversationManager;
import be.maximvdw.spigotsite.api.user.User;
import be.maximvdw.spigotsite.api.user.UserManager;
import be.maximvdw.spigotsite.api.user.exceptions.InvalidCredentialsException;
import be.maximvdw.spigotsite.api.user.exceptions.TwoFactorAuthenticationException;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConversationManagerTest {

	@Before
	public void init() {
		new SpigotSiteCore();
	}

	@Test(timeout = 20000)
	public void conversationsTest() throws InvalidCredentialsException,
			ConnectionFailedException, TwoFactorAuthenticationException {
		System.out.println("Testing 'getConversations' ...");
		UserManager userManager = SpigotSite.getAPI().getUserManager();
		User user = userManager.authenticate(UserDebugging.username, UserDebugging.password,UserDebugging.totpSecret);
		ConversationManager conversationManager = SpigotSite.getAPI()
				.getConversationManager();
		List<Conversation> conversations = conversationManager
				.getConversations(user, 20);
		for (Conversation conv : conversations) {
			System.out.println(conv.getTitle() + "[" + conv.getRepliesCount()
					+ "]   BY " + conv.getAuthor().getUsername());
			if (conv.getTitle().equals("Hello")
					&& conv.getAuthor().getUsername().equals("Maximvdw")) {
				System.out.println("Sending reply ...");
				try {
					conv.reply(user,
							"This conversation has " + conv.getRepliesCount()
									+ " replies. LEAVING NOW");
				} catch (SpamWarningException ex) {

				}
				conv.leave(user);
			}
		}
	}
//
//	@Test(timeout = 5000, expected = SpamWarningException.class)
//	public void spamConversationTest() throws InvalidCredentialsException,
//			InterruptedException {
//		System.out.println("Testing 'Spam detection' ...");
//		UserManager userManager = SpigotSite.getAPI().getUserManager();
//		User user = userManager.authenticate(username, password);
//		ConversationManager conversationManager = SpigotSite.getAPI()
//				.getConversationManager();
//		Set<String> recipents = new HashSet<String>();
//		recipents.add("MVdWSoftware");
//		conversationManager.createConversation(user, recipents, "Hello",
//				"World", true, false, false);
//		conversationManager.createConversation(user, recipents, "Hello",
//				"World", true, false, false);
//		Thread.sleep(15000);
//	}

	@Test(timeout = 20000)
	public void conversationSendTest() throws InvalidCredentialsException, TwoFactorAuthenticationException {
		System.out.println("Testing 'createConversation' ...");
		UserManager userManager = SpigotSite.getAPI().getUserManager();
		User user = userManager.authenticate(UserDebugging.username, UserDebugging.password,UserDebugging.totpSecret);
		ConversationManager conversationManager = SpigotSite.getAPI()
				.getConversationManager();
		Set<String> recipents = new HashSet<String>();
		recipents.add("MVdWSoftware");
		try {
			conversationManager.createConversation(user, recipents, "Hello",
					"World", true, false, false);
		} catch (SpamWarningException ex) {

		}
		SpigotUser spigotUser = (SpigotUser) user;
		for (String cookie : spigotUser.getCookies().keySet())
			System.out.println("Return cookie: " + cookie);

		spigotUser.refresh();
	}
}

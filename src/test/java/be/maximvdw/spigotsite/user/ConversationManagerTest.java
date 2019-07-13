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

    @Test
    public void conversationsTest() throws InvalidCredentialsException,
            ConnectionFailedException, TwoFactorAuthenticationException, InterruptedException {
        System.out.println("Testing 'getConversations' ...");
        User user = UserDebugging.getUser();
        ConversationManager conversationManager = SpigotSite.getAPI()
                .getConversationManager();
        List<Conversation> conversations = conversationManager
                .getConversations(user, 10000);
        for (Conversation conv : conversations) {
            System.out.println(conv.getTitle() + "[" + conv.getRepliesCount()
                    + "]   BY " + conv.getAuthor().getUsername());
            if (conv.getTitle().equals("Hello")
                    && conv.getAuthor().getUsername().equals("Maximvdw") && conv.getParticipants().size() == 1) {
                if (conv.getParticipants().get(0).getUsername().equals("MVdWSoftware")) {
                    Thread.sleep(12000);
                    System.out.println("Sending reply ...");
                    conv.reply(user,
                            "This conversation has " + conv.getRepliesCount()
                                    + " replies. LEAVING NOW");
                    conv.leave(user);
                }
            }
        }
    }

    @Test
    public void markAllAsReadTest() throws InvalidCredentialsException, TwoFactorAuthenticationException, ConnectionFailedException {
        System.out.println("Testing 'mark all as read conversation' ...");
        User user = UserDebugging.getUser();
        ConversationManager conversationManager = SpigotSite.getAPI()
                .getConversationManager();
        List<Conversation> conversations = conversationManager
                .getConversations(user, 10000);
        for (Conversation conv : conversations) {
            if (conv.isUnread()) {
                System.out.println("Unread conversation: " + conv.getTitle() + " by " + conv.getAuthor().getUsername());
                conv.markAsRead(user);
            }
        }
    }

    @Test(timeout = 30000, expected = SpamWarningException.class)
    public void spamConversationTest() throws InvalidCredentialsException,
            InterruptedException, TwoFactorAuthenticationException, ConnectionFailedException {
        System.out.println("Testing 'Spam detection' ...");
        User user = UserDebugging.getUser();
        ConversationManager conversationManager = SpigotSite.getAPI()
                .getConversationManager();
        Set<String> recipents = new HashSet<String>();
        recipents.add("MVdWSoftware");
        conversationManager.createConversation(user, recipents, "Hello",
                "World", true, false, false);
        conversationManager.createConversation(user, recipents, "Hello",
                "World", true, false, false);
    }

    @Test(timeout = 20000)
    public void conversationSendTest() throws InvalidCredentialsException, TwoFactorAuthenticationException, ConnectionFailedException {
        System.out.println("Testing 'createConversation' ...");
        User user = UserDebugging.getUser();
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

    @Test(timeout = 20000)
    public void conversationSendMarkReadTest() throws InvalidCredentialsException, TwoFactorAuthenticationException, ConnectionFailedException {
        System.out.println("Testing 'conversationSendMarkReadTest' ...");
        User user = UserDebugging.getUser();
        ConversationManager conversationManager = SpigotSite.getAPI()
                .getConversationManager();
        Set<String> recipents = new HashSet<String>();
        recipents.add("MVdWSoftware");
        try {
            Conversation conversation = conversationManager.createConversation(user, recipents, "Hello",
                    "World", true, false, false);
            conversation.markAsUnread(user);
        } catch (SpamWarningException ex) {

        }
        SpigotUser spigotUser = (SpigotUser) user;
        for (String cookie : spigotUser.getCookies().keySet())
            System.out.println("Return cookie: " + cookie);

        spigotUser.refresh();
    }
}

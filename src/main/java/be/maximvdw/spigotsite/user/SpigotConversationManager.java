package be.maximvdw.spigotsite.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import be.maximvdw.spigotsite.SpigotSiteCore;
import be.maximvdw.spigotsite.api.exceptions.SpamWarningException;
import be.maximvdw.spigotsite.api.user.Conversation;
import be.maximvdw.spigotsite.api.user.ConversationManager;
import be.maximvdw.spigotsite.api.user.User;
import be.maximvdw.spigotsite.utils.StringUtils;

public class SpigotConversationManager implements ConversationManager {

	public List<Conversation> getConversations(User user, int count) {
		List<Conversation> conversations = new ArrayList<Conversation>();
		try {
			String url = SpigotSiteCore.getBaseURL() + "conversations/";
			Map<String, String> params = new HashMap<String, String>();

			Connection.Response res = Jsoup
					.connect(url)
					.method(Method.GET)
					.data(params)
					.cookies(((SpigotUser) user).getCookies())
					.userAgent(
							"Mozilla/5.0 (Windows NT 6.3; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0")
					.execute();
			Document doc = res.parse();
			Elements conversationBlocks = doc.select("li.discussionListItem");
			for (Element conversationBlock : conversationBlocks) {
				SpigotConversation conversation = new SpigotConversation();
				int id = Integer.parseInt(conversationBlock.id().replace(
						"conversation-", ""));
				Element conversationLink = conversationBlock.select("h3.title")
						.get(0).getElementsByTag("a").get(0);
				conversation.setTitle(conversationLink.text());
				conversation.setConversationId(id);
				Element username = conversationBlock.select("a.username")
						.first();
				SpigotUser author = new SpigotUser();
				author.setUsername(username.text());
				author.setUserId(Integer.parseInt(StringUtils.getStringBetween(
						username.attr("href"), "\\.(.*?)/")));
				conversation.setAuthor(author);
				conversation.setRepliesCount(Integer.parseInt(conversationBlock
						.select("dd").get(0).text()));
				conversations.add(conversation);
			}
		} catch (HttpStatusException ex) {

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return conversations;
	}

	public void replyToConversation(Conversation conversation, User user,
			String reply) throws SpamWarningException {
		try {
			String url = SpigotSiteCore.getBaseURL() + "conversations/"
					+ conversation.getConverationId() + "/insert-reply";

			if (((SpigotUser) user).requiresRefresh())
				((SpigotUser) user).refresh();

			Map<String, String> params = new HashMap<String, String>();
			params.put("message", reply);
			params.put("last_date", String.valueOf(new Date().getTime()));
			params.put("last_known_date", "");
			params.put("_xfToken", ((SpigotUser) user).getToken());
			params.put("_xfRelativeResolver", url);
			params.put("_xfRequestUri", url);
			params.put("_xfNoRedirect", "1");
			params.put("_xfResponseType", "json");

			Connection.Response res = Jsoup
					.connect(url)
					.method(Method.POST)
					.data(params)
					.ignoreContentType(true)
					.cookies(((SpigotUser) user).getCookies())
					.userAgent(
							"Mozilla/5.0 (Windows NT 6.3; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0")
					.execute();
			Document doc = res.parse();

			if (doc.text().contains("\"error\":")) {
				throw new SpamWarningException();
			}
		} catch (HttpStatusException ex) {
			ex.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void leaveConversation(Conversation conversation, User user) {
		try {
			String url = SpigotSiteCore.getBaseURL() + "conversations/"
					+ conversation.getConverationId() + "/leave";

			if (((SpigotUser) user).requiresRefresh())
				((SpigotUser) user).refresh();

			Map<String, String> params = new HashMap<String, String>();
			params.put("deletetype", "delete");
			params.put("_xfConfirm", "1");
			params.put("_xfToken", ((SpigotUser) user).getToken());
			Jsoup.connect(url)
					.method(Method.POST)
					.data(params)
					.ignoreContentType(true)
					.cookies(((SpigotUser) user).getCookies())
					.userAgent(
							"Mozilla/5.0 (Windows NT 6.3; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0")
					.execute();
		} catch (HttpStatusException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Conversation createConversation(User user, Set<String> recipents,
			String title, String body, boolean locked, boolean invite,
			boolean sticky) throws SpamWarningException {
		Conversation conversation = new SpigotConversation();
		try {
			String url = SpigotSiteCore.getBaseURL() + "conversations/insert";
			String recipentsStr = recipents.iterator().next();

			if (((SpigotUser) user).requiresRefresh())
				((SpigotUser) user).refresh();
			Map<String, String> params = new HashMap<String, String>();
			params.put("title", title);
			params.put("message", body);
			params.put("recipients", recipentsStr);
			params.put("_xfToken", ((SpigotUser) user).getToken());
			params.put("_xfRelativeResolver", url);
			params.put("_xfRequestUri", url);
			params.put("_xfNoRedirect", "1");
			params.put("_xfResponseType", "json");
			params.put("conversation_locked", locked ? "1" : "0");
			params.put("conversation_sticky", sticky ? "1" : "0");
			params.put("open_invite", invite ? "1" : "0");
			Connection.Response res = Jsoup
					.connect(url)
					.method(Method.POST)
					.data(params)
					.ignoreContentType(true)
					.cookies(((SpigotUser) user).getCookies())
					.userAgent(
							"Mozilla/5.0 (Windows NT 6.3; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0")
					.execute();
			Document doc = res.parse();

			if (doc.text().contains("\"error\":")) {
				throw new SpamWarningException();
			}

			doc.select("div.titleBar");
		} catch (HttpStatusException ex) {
			ex.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return conversation;
	}

}

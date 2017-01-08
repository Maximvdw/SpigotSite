package be.maximvdw.spigotsite.user;

import be.maximvdw.spigotsite.SpigotSiteCore;
import be.maximvdw.spigotsite.api.exceptions.SpamWarningException;
import be.maximvdw.spigotsite.api.user.Conversation;
import be.maximvdw.spigotsite.api.user.ConversationManager;
import be.maximvdw.spigotsite.api.user.User;
import be.maximvdw.spigotsite.http.HTTPResponse;
import be.maximvdw.spigotsite.http.Request;
import be.maximvdw.spigotsite.utils.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

public class SpigotConversationManager implements ConversationManager {

    public List<Conversation> getConversations(User user, int count) {
        List<Conversation> conversations = new ArrayList<Conversation>();
        try {
            String url = SpigotSiteCore.getBaseURL() + "conversations/";
            Map<String, String> params = new HashMap<String, String>();

            HTTPResponse req = Request.get(url, ((SpigotUser) user).getCookies(), params);
            ((SpigotUser) user).getCookies().putAll(req.getCookies());

            Document doc = req.getDocument();
            Element pagesElement = doc.getElementsByClass("contentSummary").first();
            String[] data = pagesElement.text().split(" ");
            String numberStr = data[data.length - 1].replace(",", "");
            Integer conversationCount = Integer.parseInt(numberStr);
            int totalPages = (int) Math.ceil(conversationCount / 20.);
            int pages = (int) Math.ceil(count / 20.);
            if (pages > totalPages) {
                pages = totalPages;
            }

            conversations.addAll(loadConversationsOnPage(doc));
            for (int i = 2; i <= pages; i++) {
                url = SpigotSiteCore.getBaseURL() + "conversations/?page=" + i;
                req = Request.get(url, ((SpigotUser) user).getCookies(), params);

                doc = req.getDocument();
                conversations.addAll(loadConversationsOnPage(doc));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return conversations;
    }

    private List<Conversation> loadConversationsOnPage(Document doc) {
        List<Conversation> conversations = new ArrayList<Conversation>();
        Elements conversationBlocks = doc.select("li.discussionListItem");
        for (Element conversationBlock : conversationBlocks) {
            SpigotConversation conversation = new SpigotConversation();
            int id = Integer.parseInt(conversationBlock.id().replace("conversation-", ""));
            conversation.setUnread(conversationBlock.hasClass("unread"));
            Element conversationLink = conversationBlock.select("h3.title").get(0).getElementsByTag("a").get(0);
            conversation.setTitle(conversationLink.text());
            conversation.setConversationId(id);
            Element username = conversationBlock.select("a.username").first();
            SpigotUser author = new SpigotUser();
            author.setUsername(username.text());
            author.setUserId(Integer.parseInt(StringUtils.getStringBetween(username.attr("href"), "\\.(.*?)/")));
            conversation.setAuthor(author);

            username = conversationBlock.select("div.listBlock.lastPost > dl > dt > span > a").first();
            SpigotUser replier = new SpigotUser();
            replier.setUsername(username.text());
            replier.setUserId(Integer.parseInt(StringUtils.getStringBetween(username.attr("href"), "\\.(.*?)/")));
            conversation.setLastReplier(replier);

            // Get participants
            Elements participantsSpans = conversationBlock.select(".username.convess");
            for (Element participantSpan : participantsSpans){
                String participantUsername = participantSpan.text();
                String participantHref = participantSpan.attr("href");
                String participantIdStr = participantHref.substring(participantHref.lastIndexOf(".") + 1, participantHref.lastIndexOf("/"));
                int participantId = Integer.parseInt(participantIdStr);
                SpigotUser participant = new SpigotUser();
                participant.setUsername(participantUsername);
                participant.setUserId(participantId);
                conversation.getParticipants().add(participant);
            }

            Elements abbr = conversationBlock.select("div.listBlock.lastPost > dl > dd > a > abbr");
            if (abbr != null && abbr.first() != null && abbr.first().hasAttr("data-time")) {
                String unixTime = abbr.first().attr("data-time");
                if (unixTime != null)
                    conversation.setLastReplyDate(Long.parseLong(unixTime));
            }

            conversation.setRepliesCount(Integer.parseInt(conversationBlock.select("dd").get(0).text()));
            conversations.add(conversation);
        }
        return conversations;
    }

    public void replyToConversation(Conversation conversation, User user, String reply) throws SpamWarningException {
        try {
            String url = SpigotSiteCore.getBaseURL() + "conversations/" + conversation.getConverationId()
                    + "/insert-reply";

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

            HTTPResponse req = Request.post(url, ((SpigotUser) user).getCookies(), params);
            ((SpigotUser) user).getCookies().putAll(req.getCookies());

            Document doc = req.getDocument();
            if (doc.text().contains("\"error\":")) {
                throw new SpamWarningException();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void leaveConversation(Conversation conversation, User user) {
        try {
            String url = SpigotSiteCore.getBaseURL() + "conversations/" + conversation.getConverationId() + "/leave";

            if (((SpigotUser) user).requiresRefresh())
                ((SpigotUser) user).refresh();

            Map<String, String> params = new HashMap<String, String>();
            params.put("deletetype", "delete");
            params.put("_xfConfirm", "1");
            params.put("_xfToken", ((SpigotUser) user).getToken());
            HTTPResponse req = Request.post(url, ((SpigotUser) user).getCookies(), params);
            ((SpigotUser) user).getCookies().putAll(req.getCookies());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Conversation createConversation(User user, Set<String> recipents, String title, String body, boolean locked,
                                           boolean invite, boolean sticky) throws SpamWarningException {
        SpigotConversation conversation = new SpigotConversation();
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

            HTTPResponse req = Request.post(url, ((SpigotUser) user).getCookies(), params);
            ((SpigotUser) user).getCookies().putAll(req.getCookies());

            Document doc = req.getDocument();

            if (doc.text().contains("\"error\":")) {
                throw new SpamWarningException();
            } else {
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(doc.text());
                String redirectUrl = (String) jsonObject.get("_redirectTarget");
                conversation.setAuthor(user);
                conversation.setTitle(title);
                String idStr = redirectUrl.substring(redirectUrl.lastIndexOf(".") + 1, redirectUrl.lastIndexOf("/"));
                conversation.setConversationId(Integer.parseInt(idStr));
            }

            doc.select("div.titleBar");
        } catch (SpamWarningException e) {
            throw new SpamWarningException();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return conversation;
    }

    public void markConversationAsRead(User user, Conversation conversation) {
        if (!conversation.isUnread()) {
            return;
        }
        toggleConversationRead(user, conversation);
        ((SpigotConversation) conversation).setRead(true);
    }

    public void markConversationAsUnread(User user, Conversation conversation) {
        if (conversation.isUnread()) {
            return;
        }
        toggleConversationRead(user, conversation);
        ((SpigotConversation) conversation).setRead(false);
    }


    private void toggleConversationRead(User user, Conversation conversation) {
        try {
            String url = SpigotSiteCore.getBaseURL() + "conversations/" + conversation.getConverationId() + "/toggle-read";

            if (((SpigotUser) user).requiresRefresh())
                ((SpigotUser) user).refresh();

            Map<String, String> params = new HashMap<String, String>();
            params.put("_xfConfirm", "1");
            params.put("_xfToken", ((SpigotUser) user).getToken());
            HTTPResponse req = Request.post(url, ((SpigotUser) user).getCookies(), params);
            ((SpigotUser) user).getCookies().putAll(req.getCookies());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

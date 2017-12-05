package be.maximvdw.spigotsite.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import be.maximvdw.spigotsite.api.SpigotSite;
import be.maximvdw.spigotsite.api.exceptions.ConnectionFailedException;
import be.maximvdw.spigotsite.api.exceptions.SpamWarningException;
import be.maximvdw.spigotsite.api.user.Conversation;
import be.maximvdw.spigotsite.api.user.User;

public class SpigotConversation implements Conversation, Serializable {
    private User author = null;
    private List<User> participants = new ArrayList<User>();
    private String title = "";
    private int id = 0;
    private int repliesCount = 0;
    private boolean unread;
    private User lastReplier = null;
    private Date lastReplyDate = null;

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getConverationId() {
        return id;
    }

    public void setConversationId(int id) {
        this.id = id;
    }

    public void reply(User user, String bbCode) throws SpamWarningException, ConnectionFailedException {
        SpigotSite.getAPI().getConversationManager()
                .replyToConversation(this, user, bbCode);
    }

    public int getRepliesCount() {
        return repliesCount;
    }

    public void leave(User user) throws ConnectionFailedException {
        SpigotSite.getAPI().getConversationManager()
                .leaveConversation(this, user);

    }

    public void markAsRead(User user) throws ConnectionFailedException {
        SpigotSite.getAPI().getConversationManager().markConversationAsRead(user, this);
    }

    public void markAsUnread(User user) throws ConnectionFailedException {
        SpigotSite.getAPI().getConversationManager().markConversationAsUnread(user, this);
    }

    public void setRepliesCount(int repliesCount) {
        this.repliesCount = repliesCount;
    }

    public boolean isUnread() {
        return unread;
    }

    public void setRead(boolean read) {
        this.unread = !read;
    }

    public void setUnread(boolean unread) {
        this.unread = unread;
    }

    public User getLastReplier() {
        return lastReplier;
    }

    public void setLastReplier(User lastReplier) {
        this.lastReplier = lastReplier;
    }

    public Date getLastReplyDate() {
        return this.lastReplyDate;
    }

    public void setLastReplyDate(Date lastReplyDate) {
        this.lastReplyDate = lastReplyDate;
    }

    public void setLastReplyDate(long unixTimeStamp) {
        this.lastReplyDate = new Date(unixTimeStamp * 1000);
    }

    @Override
    public boolean equals(Object object) {
        boolean sameSame = false;

        if (object != null && object instanceof SpigotConversation) {
            sameSame = this.id == ((SpigotConversation) object).id;
        }

        return sameSame;
    }

}

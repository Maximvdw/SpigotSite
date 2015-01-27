package be.maximvdw.spigotsite.user;

import java.util.ArrayList;
import java.util.List;

import be.maximvdw.spigotsite.api.SpigotSite;
import be.maximvdw.spigotsite.api.user.Conversation;
import be.maximvdw.spigotsite.api.user.User;

public class SpigotConversation implements Conversation {
	private User author = null;
	private List<User> participants = new ArrayList<User>();
	private String title = "";
	private int id = 0;
	private int repliesCount = 0;

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

	public void reply(User user, String bbCode) {
		SpigotSite.getAPI().getUserManager()
				.replyToConversation(this, user, bbCode);
	}

	public int getRepliesCount() {
		return repliesCount;
	}

	public void leave(User user) {
		SpigotSite.getAPI().getUserManager().leaveConversation(this, user);

	}

	public void setRepliesCount(int repliesCount) {
		this.repliesCount = repliesCount;
	}

	public boolean equals(Conversation conv) {
		if (conv.getConverationId() == getConverationId())
			return true;

		return false;
	}

}

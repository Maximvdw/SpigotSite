package be.maximvdw.spigotsite.user;

import java.util.ArrayList;
import java.util.List;

import be.maximvdw.spigotsite.api.user.Conversation;
import be.maximvdw.spigotsite.api.user.User;

public class SpigotConversation implements Conversation {
	private User author = null;
	private List<User> participants = new ArrayList<User>();
	private String title = "";
	private int id = 0;

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

}

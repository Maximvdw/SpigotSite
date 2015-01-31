package be.maximvdw.spigotsite.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.maximvdw.spigotsite.api.SpigotSite;
import be.maximvdw.spigotsite.api.resource.Resource;
import be.maximvdw.spigotsite.api.user.Conversation;
import be.maximvdw.spigotsite.api.user.User;
import be.maximvdw.spigotsite.api.user.UserStatistics;

public class SpigotUser implements User {
	private int id = 0;
	private String username = "";
	private Map<String, String> cookies = new HashMap<String, String>();
	private boolean authenticated = false;
	private UserStatistics statistics = null;
	private String token = "";

	public SpigotUser() {

	}

	public SpigotUser(String username) {
		setUsername(username);
	}

	public int getUserId() {
		return id;
	}

	public void setUserId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Map<String, String> getCookies() {
		return cookies;
	}

	public void setCookies(Map<String, String> cookies) {
		this.cookies = cookies;
		this.authenticated = true;
	}

	public List<Resource> getPurchasedResources() {
		return SpigotSite.getAPI().getResourceManager()
				.getPurchasedResources(this);
	}

	public List<Resource> getCreatedResources() {
		return SpigotSite.getAPI().getResourceManager()
				.getResourcesByUser(this);
	}

	public UserStatistics getUserStatistics() {
		return statistics;
	}

	public void setUserStatistics(UserStatistics statistics) {
		this.statistics = statistics;
	}

	public boolean isAuthenticated() {
		return authenticated;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof User)) {
			return false;
		}
		User user = (User) obj;
		if (user.getUserId() != getUserId())
			return false;
		return true;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public List<Conversation> getConversations() {
		// TODO Auto-generated method stub
		return null;
	}

}

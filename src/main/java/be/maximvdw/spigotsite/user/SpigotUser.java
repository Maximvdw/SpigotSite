package be.maximvdw.spigotsite.user;

import java.util.HashMap;
import java.util.Map;

import be.maximvdw.spigotsite.api.user.User;

public class SpigotUser implements User {
	private int id = 0;
	private String username = "";
	private Map<String, String> cookies = new HashMap<String, String>();

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
	}

}

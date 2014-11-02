package be.maximvdw.spigotsite.user;

import be.maximvdw.spigotsite.api.user.User;

public class SpigotUser implements User {
	private int id = 0;
	private String username = "";
	private String[] cookies = new String[] {};

	public SpigotUser() {

	}

	public SpigotUser(String username) {
		setUsername(username);
	}

	public int getUserId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String[] getCookies() {
		return cookies;
	}

	public void setCookies(String[] cookies) {
		this.cookies = cookies;
	}

}

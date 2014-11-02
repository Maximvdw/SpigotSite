package be.maximvdw.spigotsite.user;

import be.maximvdw.spigotsite.api.user.User;

public class SpigotUser implements User {
	private int id = 0;
	private String username = "";

	public int getUserId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

}

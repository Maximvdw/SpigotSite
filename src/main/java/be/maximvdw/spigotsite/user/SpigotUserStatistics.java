package be.maximvdw.spigotsite.user;

import java.util.Date;

import be.maximvdw.spigotsite.api.user.UserStatistics;

public class SpigotUserStatistics implements UserStatistics {
	private int postCount = 0;
	private Date joinDate = new Date();

	public Date getJoinDate() {
		return joinDate;
	}

	public void setJoinDate(Date date) {
		this.joinDate = date;
	}

	public int getPostCount() {
		return postCount;
	}

	public void setPostCount(int count) {
		this.postCount = count;
	}
}

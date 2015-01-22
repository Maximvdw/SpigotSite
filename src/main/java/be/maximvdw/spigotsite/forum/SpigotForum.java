package be.maximvdw.spigotsite.forum;

import java.util.ArrayList;
import java.util.List;

import be.maximvdw.spigotsite.api.forum.Forum;

public class SpigotForum implements Forum {
	private List<Forum> subForums = new ArrayList<Forum>();
	private Forum parrent = null;

	public List<Forum> getSubForums() {
		// TODO Auto-generated method stub
		return null;
	}

}

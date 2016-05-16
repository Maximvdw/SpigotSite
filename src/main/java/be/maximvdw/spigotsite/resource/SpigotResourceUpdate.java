package be.maximvdw.spigotsite.resource;

import be.maximvdw.spigotsite.api.resource.ResourceUpdate;

public class SpigotResourceUpdate implements ResourceUpdate {
	private String updateID = "";
	private String textHeading = "";
	private String article = "";
	private String messageMeta = "";
	private String updateLink = "";

	public String getUpdateID() {
		return updateID;
	}

	public void setUpdateID(String updateID) {
		this.updateID = updateID;
	}

	public String getTextHeading() {
		return textHeading;
	}

	public void setTextHeading(String textHeading) {
		this.textHeading = textHeading;
	}

	public String getArticle() {
		return article;
	}

	public void setArticle(String article) {
		this.article = article;
	}

	public String getMessageMeta() {
		return messageMeta;
	}

	public void setMessageMeta(String messageMeta) {
		this.messageMeta = messageMeta;
	}

	public String getUpdateLink() {
		return updateLink;
	}

	public void setUpdateLink(String updateLink) {
		this.updateLink = updateLink;
	}
}

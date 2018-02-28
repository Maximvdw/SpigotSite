package be.maximvdw.spigotsite.forum;

import be.maximvdw.spigotsite.api.forum.ProfilePost;
import be.maximvdw.spigotsite.api.user.User;

import java.util.Date;

/**
 * Created by Maxim on 28/02/2018.
 */
public class SpigotProfilePost implements ProfilePost {
    private User author = null;
    private Date postDate = null;
    private String message = null;

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

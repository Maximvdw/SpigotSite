package be.maximvdw.spigotsite.resource;

import be.maximvdw.spigotsite.SpigotSiteCore;
import be.maximvdw.spigotsite.api.resource.Rating;
import be.maximvdw.spigotsite.api.resource.Resource;
import be.maximvdw.spigotsite.api.resource.ResourceCategory;
import be.maximvdw.spigotsite.api.resource.ResourceUpdate;
import be.maximvdw.spigotsite.api.user.User;
import be.maximvdw.spigotsite.http.HTTPDownloadResponse;
import be.maximvdw.spigotsite.http.HTTPUnitRequest;
import be.maximvdw.spigotsite.http.Request;
import be.maximvdw.spigotsite.user.SpigotUser;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

import java.io.*;
import java.net.*;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.List;

public class SpigotResource implements Resource, Serializable {
    private int id = 0;
    private String name = "";
    private String version = "";
    private User author = null;
    private ResourceCategory category = null;
    private boolean deleted = false;
    private String downloadURL = "";
    private String externalURL = "";
    private List<ResourceUpdate> resourceUpdates = null;

    public SpigotResource() {

    }

    public SpigotResource(String name) {
        setResourceName(name);
    }

    public int getResourceId() {
        return id;
    }

    public void setResourceId(int id) {
        this.id = id;
    }

    public String getResourceName() {
        return name;
    }

    public String getLastVersion() {
        return this.version;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public void setResourceName(String name) {
        this.name = name;
    }

    public void setLastVersion(String version) {
        this.version = version;
    }

    public void setResourceUpdates(List<ResourceUpdate> updates) {
        this.resourceUpdates = updates;
    }

    public ResourceCategory getResourceCategory() {
        return category;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }

    public File downloadResource(User user, File output) {
        try {
            if (output.exists()) {
                output.delete();
            }
            output.getParentFile().mkdirs();

            HTTPDownloadResponse dlResponse = HTTPUnitRequest.downloadFile(
                    getDownloadURL(),
                    user != null ? ((SpigotUser) user).getCookies()
                            : SpigotSiteCore.getBaseCookies());
            setExternalURL(dlResponse.getUrl().toString());
            InputStream stream = dlResponse.getStream();
            FileOutputStream fos = new FileOutputStream(output);
            byte[] buffer = new byte[stream.available()];
            stream.read(buffer);
            fos.write(buffer);
            fos.close();
            return output;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public int getAverageRating() {
        // TODO Auto-generated method stub
        return 0;
    }

    public List<Rating> getRatings() {
        // TODO Auto-generated method stub
        return null;
    }

    public List<ResourceUpdate> getResourceUpdates() {
        return resourceUpdates;
    }

    public String getExternalURL() {
        return externalURL;
    }

    public void setExternalURL(String externalURL) {
        this.externalURL = externalURL;
    }
}

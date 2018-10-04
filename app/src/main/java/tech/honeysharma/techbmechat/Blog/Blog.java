package tech.honeysharma.techbmechat.Blog;

/**
 * Created by honey on 31/1/18.
 */

public class Blog {

    private String image, title, desc, username, Date;

    public Blog() {

    }

    public Blog(String image, String title, String desc, String username, String date) {
        this.image = image;
        this.title = title;
        this.desc = desc;
        this.username = username;
        Date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }
}

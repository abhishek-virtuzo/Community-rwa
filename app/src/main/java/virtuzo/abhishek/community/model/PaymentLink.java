package virtuzo.abhishek.community.model;

import io.realm.RealmObject;

/**
 * Created by virtuzo on 8/29/2018.
 */

public class PaymentLink extends RealmObject {

    private int Id;
    private String Title;
    private String WebLink;
    private String ImageUrl;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getWebLink() {
        return WebLink;
    }

    public void setWebLink(String webLink) {
        WebLink = webLink;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }
}

package virtuzo.abhishek.community.adapter;

import io.realm.RealmObject;

/**
 * Created by Abhishek Aggarwal on 7/9/2018.
 */

public class Panchang extends RealmObject {

    private String Id;
    private String Title;
    private String Image;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}

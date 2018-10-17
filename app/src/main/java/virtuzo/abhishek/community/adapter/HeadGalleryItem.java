package virtuzo.abhishek.community.adapter;

/**
 * Created by Abhishek Aggarwal on 5/8/2018.
 */

public class HeadGalleryItem {

    private String imageUrl = "";

    public HeadGalleryItem(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

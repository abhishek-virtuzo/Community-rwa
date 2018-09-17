package virtuzo.abhishek.community.adapter;

/**
 * Created by virtuzo on 4/26/2018.
 */

public class Event {

    private String Id;
    private String EventTitle;
    private String Date;
    private String Venue;
    private String Image;
    private String Latitude;
    private String Longitude;
    private String Description;
    private boolean isInterested;
    private String shareUrl;

    public Event() {}

    public Event(String id, String eventTitle, String date, String venue, String Image) {
        Id = id;
        EventTitle = eventTitle;
        Date = date;
        Venue = venue;
        this.Image = Image;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getEventTitle() {
        return EventTitle;
    }

    public void setEventTitle(String eventTitle) {
        EventTitle = eventTitle;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getVenue() {
        return Venue;
    }

    public void setVenue(String venue) {
        Venue = venue;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        this.Image = image;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public boolean isInterested() {
        return isInterested;
    }

    public void setInterested(boolean interested) {
        isInterested = interested;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }
}

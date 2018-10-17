package virtuzo.abhishek.community.adapter;

/**
 * Created by Abhishek Aggarwal on 4/25/2018.
 */

public class BranchOfOrganisation {

    private String Id;
    private String BranchName;
    private String Address;
    private String Image;
    private String Date;
    private String Longitude;
    private String Latitude;
    private String Description;
    private String ContactNumber;
    private String EmaiId;

    public BranchOfOrganisation() {}

    public BranchOfOrganisation(String BranchName, String Address, String imageUrl) {
        this.BranchName = BranchName;
        this.Address = Address;
        this.Image = imageUrl;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getBranchName() {
        return BranchName;
    }

    public void setBranchName(String branchName) {
        BranchName = branchName;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getImageUrl() {
        return Image;
    }

    public void setImageUrl(String imageUrl) {
        this.Image = imageUrl;
    }

    public String getContactNumber() {
        return ContactNumber;
    }

    public void setContactNumber(String contactNumber) {
        ContactNumber = contactNumber;
    }

    public String getEmaiId() {
        return EmaiId;
    }

    public void setEmaiId(String emaiId) {
        EmaiId = emaiId;
    }
}

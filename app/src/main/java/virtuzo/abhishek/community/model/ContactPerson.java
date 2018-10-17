package virtuzo.abhishek.community.model;

import io.realm.Realm;
import io.realm.RealmObject;

/**
 * Created by Abhishek Aggarwal on 7/27/2018.
 */

public class ContactPerson extends RealmObject {

    private int ContactID;
    private int CategoryId;
    private String ContactName;
    private String Designation;
    private String ContactNumber;
    private String EmailId;
    private String ProfileUrl;
    private String Address;

    public int getContactID() {
        return ContactID;
    }

    public void setContactID(int contactID) {
        ContactID = contactID;
    }

    public int getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(int categoryId) {
        CategoryId = categoryId;
    }

    public String getContactName() {
        return ContactName;
    }

    public void setContactName(String contactName) {
        ContactName = contactName;
    }

    public String getDesignation() {
        return Designation;
    }

    public void setDesignation(String designation) {
        Designation = designation;
    }

    public String getContactNumber() {
        return ContactNumber;
    }

    public void setContactNumber(String contactNumber) {
        ContactNumber = contactNumber;
    }

    public String getEmailId() {
        return EmailId;
    }

    public void setEmailId(String emailId) {
        EmailId = emailId;
    }

    public String getProfileUrl() {
        return ProfileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        ProfileUrl = profileUrl;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
}

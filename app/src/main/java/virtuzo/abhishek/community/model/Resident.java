package virtuzo.abhishek.community.model;

import io.realm.RealmObject;

/**
 * Created by virtuzo on 7/31/2018.
 */

public class Resident extends RealmObject {

    private int ID;
    private String ResidentName;
    private int ResidentBlockID;
    private String HouseNumber;
    private String ContactNumber;
    private String ProfileUrl;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getResidentName() {
        return ResidentName;
    }

    public void setResidentName(String residentName) {
        ResidentName = residentName;
    }

    public int getResidentBlockID() {
        return ResidentBlockID;
    }

    public void setResidentBlockID(int residentBlockID) {
        ResidentBlockID = residentBlockID;
    }

    public String getHouseNumber() {
        return HouseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        HouseNumber = houseNumber;
    }

    public String getContactNumber() {
        return ContactNumber;
    }

    public void setContactNumber(String contactNumber) {
        ContactNumber = contactNumber;
    }

    public String getProfileUrl() {
        return ProfileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        ProfileUrl = profileUrl;
    }
}

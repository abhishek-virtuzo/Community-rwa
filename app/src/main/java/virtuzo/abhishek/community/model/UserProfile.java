package virtuzo.abhishek.community.model;

/**
 * Created by Abhishek Aggarwal on 8/2/2018.
 */

public class UserProfile {

    private String Name;
    private String MobileNo;
    private String Address;
    private String DOB;
    private String Profession;
    private String AboutMe;
    private int BloodDonate;
    private int BloodGroupID;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getMobileNo() {
        return MobileNo;
    }

    public void setMobileNo(String mobileNo) {
        MobileNo = mobileNo;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getProfession() {
        return Profession;
    }

    public void setProfession(String profession) {
        Profession = profession;
    }

    public String getAboutMe() {
        return AboutMe;
    }

    public void setAboutMe(String aboutMe) {
        AboutMe = aboutMe;
    }

    public int getBloodDonate() {
        return BloodDonate;
    }

    public void setBloodDonate(int bloodDonate) {
        BloodDonate = bloodDonate;
    }

    public int getBloodGroupID() {
        return BloodGroupID;
    }

    public void setBloodGroupID(int bloodGroupID) {
        BloodGroupID = bloodGroupID;
    }

}

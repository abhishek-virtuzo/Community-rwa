package virtuzo.abhishek.community.config;

/**
 * Created by ARPIT on 19-03-2017.
 */

public class UrlConfig {


    //TEST SERVER
//    private static String testServer = "http://www.sirrat.com/riverSanskiriti/";
    private static final String testServer = "http://sirrat.com/community/app/";

    //TEST SERVER
//    private static final String productionServer = "http://apps.prarang.in/";

    //SERVER
    private static final String server = testServer;

    //STARTER
    public static String getCityReagion = server + "cityRegionListApi.php";
    public static String userLocation = server + "userLocation.php";

    //REG
    public static String login = server + "registrationResponse.php";
    public static String profilePic = server + "profilePic.php";

    //POST
    public static String cityPost = server + "cityWiseChitti.php";
    public static String regionPost = server + "regionWiseChitti.php";
    public static String countryPost = server + "countryWiseChitti.php";

    //LIKE & COMMENT
    public static String chittiLike = server + "chittiLike.php";
    public static String getChittiComment = server + "getChittiComment.php";
    public static String postChittiComment = server + "postChittiComment.php";

    //TAGS
    public static String tagcount = server + "tagcount.php";
    public static String taglist = server + "taglist.php";
    public static String tagWisePost = server + "tagWisePost.php";

    //OTHERS
    public static String chittiDetails = server + "ChittiDetails.php";

    //PLAYSTORE
    public static String playStoreURL = "https://play.google.com/store/apps/details";

    // CIRCULAR
    public static String CircularNotificationDetails = server + "CircularNotificationDetails.php";

    // EVENT
//    public static String EventList = server + "Eventlist.php";
    public static String EventList = server + "EventlistWithInterest.php";
    public static String EventInterested = server + "eventInterested.php";

    // Branch of org.
    public static String BranchList = server + "branchList.php";

    // Panchang
    public static String PanchangList = server + "PanchangList.php";

    // Access Code
    public static String validateAccessCode = server + "validateAccessCode.php";

    // Contacts
    public static final String ContactCategory = server + "ContactCategory.php";
    public static final String ContactCategoryList = server + "ContactCategoryList.php";
    public static final String ContactDetails = server + "ContactDetails.php";

    // OfficeBearers
    public static final String OfficeBearersList = server + "OfficeBearersList.php";

    // Messages
    public static final String MessageList = server + "MessageList.php";

    // Sync Data
    public static final String SyncData = server + "SyncData.php";

    // ProfileData
    public static final String GetProfileData = server + "GetProfileData.php";
    public static final String SaveProfileData = server + "SaveProfileData.php";

    // Complaint
    public static final String AddNewComplaint = server + "AddNewComplaint.php";
    public static final String GetComplaintList = server + "GetComplaintList.php";
    public static final String SyncComplaintList = server + "SyncComplaintList.php";
    public static final String complaintsPic = server + "complaintsPic.php";

}

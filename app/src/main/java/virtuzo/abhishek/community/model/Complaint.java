package virtuzo.abhishek.community.model;

import io.realm.RealmObject;

/**
 * Created by Abhishek Aggarwal on 8/23/2018.
 */

public class Complaint extends RealmObject{

    private int ComplaintId;
    private String Title;
    private String Description;
    private String CreatedDtTm;
    private int StatusId;
    private String StatusName;
    private String picUrl;
    private String FinalReply;
    private int Synced = 0;

    public int getComplaintId() {
        return ComplaintId;
    }

    public void setComplaintId(int complaintId) {
        ComplaintId = complaintId;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getCreatedDtTm() {
        return CreatedDtTm;
    }

    public void setCreatedDtTm(String createdDtTm) {
        CreatedDtTm = createdDtTm;
    }

    public int getStatusId() {
        return StatusId;
    }

    public void setStatusId(int statusId) {
        StatusId = statusId;
    }

    public String getStatusName() {
        return StatusName;
    }

    public void setStatusName(String statusName) {
        StatusName = statusName;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getFinalReply() {
        return FinalReply;
    }

    public void setFinalReply(String finalReply) {
        FinalReply = finalReply;
    }

    public int getSynced() {
        return Synced;
    }

    public void setSynced(int synced) {
        Synced = synced;
    }

    public ComplaintToSend getComplaintToSend(Complaint complaint) {
        ComplaintToSend complaintToSend = new ComplaintToSend(complaint);
        return complaintToSend;
    }

    // Only few parameters are required to send to server, thus reducing the size of url json (while syncing)
    public class ComplaintToSend {

        private String Title;
        private String Description;
        private String CreatedDtTm;
        private String picUrl;

        public ComplaintToSend() {}

        public ComplaintToSend(Complaint complaint) {
            Title = complaint.getTitle();
            Description = complaint.getDescription();
            CreatedDtTm = complaint.getCreatedDtTm();
            this.picUrl = complaint.getPicUrl();
        }

    }

}

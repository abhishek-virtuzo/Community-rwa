package virtuzo.abhishek.community.adapter;

/**
 * Created by Abhishek Aggarwal on 5/11/2018.
 */

public class Circular {

    private long CircularId;
    private String Subject;
    private String Content;
    private String shareUrl;
    private String CreatedDtTm;
    private String Seen;

    public Circular() {
    }

    public Circular(String subject, String content) {
        Subject = subject;
        Content = content;
    }

    public long getCircularId() {
        return CircularId;
    }

    public void setCircularId(long circularId) {
        CircularId = circularId;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public String getCreatedDtTm() {
        return CreatedDtTm;
    }

    public void setCreatedDtTm(String createdDtTm) {
        CreatedDtTm = createdDtTm;
    }

    public String getSeen() {
        return Seen;
    }

    public void setSeen(String seen) {
        Seen = seen;
    }
}

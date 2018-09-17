package virtuzo.abhishek.community.model;

import io.realm.RealmObject;

/**
 * Created by virtuzo on 7/30/2018.
 */

public class Message extends RealmObject {

    private int ID;
    private String Name;
    private String MessageImageUrl;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getMessageImageUrl() {
        return MessageImageUrl;
    }

    public void setMessageImageUrl(String messageImageUrl) {
        MessageImageUrl = messageImageUrl;
    }
}

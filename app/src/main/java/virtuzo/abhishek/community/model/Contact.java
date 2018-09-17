package virtuzo.abhishek.community.model;

import io.realm.RealmObject;

/**
 * Created by virtuzo on 8/6/2018.
 */

public class Contact extends RealmObject { // from Phone book

    private String Name;
    private String Number;

    public Contact() {}

    public Contact(String name, String number) {
        Name = name;
        Number = number;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }
}

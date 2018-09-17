package virtuzo.abhishek.community.model;

import io.realm.RealmObject;

/**
 * Created by virtuzo on 8/23/2018.
 */

public class BloodGroup extends RealmObject {

    private int ID;
    private String Name;

    public BloodGroup() {}

    public BloodGroup(String name) {
        Name = name;
    }

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

    @Override
    public String toString() {
        return getName();
    }
}

package virtuzo.abhishek.community.model;

import io.realm.RealmObject;

/**
 * Created by Abhishek Aggarwal on 7/27/2018.
 */

public class ContactCategory extends RealmObject {

    private int ID;
    private String CategoryName;
    private int ParentID;
    private int ChildCount;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }

    public int getParentID() {
        return ParentID;
    }

    public void setParentID(int parentID) {
        ParentID = parentID;
    }

    public int getChildCount() {
        return ChildCount;
    }

    public void setChildCount(int childCount) {
        ChildCount = childCount;
    }
}

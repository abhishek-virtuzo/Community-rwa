package virtuzo.abhishek.community.model;

import io.realm.RealmObject;

/**
 * Created by Abhishek Aggarwal on 7/31/2018.
 */

public class ResidentBlock extends RealmObject {

    private int ID;
    private String BlockName;

    public ResidentBlock() {}

    public ResidentBlock(int ID, String blockName) {
        this.ID = ID;
        BlockName = blockName;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getBlockName() {
        return BlockName;
    }

    public void setBlockName(String blockName) {
        BlockName = blockName;
    }
}

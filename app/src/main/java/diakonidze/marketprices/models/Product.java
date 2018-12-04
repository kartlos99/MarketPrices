package diakonidze.marketprices.models;

import java.util.ArrayList;
import java.util.List;

public class Product {
    private int id, typeID;
    private String name, typeName, groupName;
    private String image;
    private int[] params;
    private int[] packs;

    public Product(int id, String name) {
        this.id = id;
        this.name = name;
    }


    public String allToString() {
        return "Product{" +
                "id=" + id +
                ", typeID=" + typeID +
                ", name='" + name + '\'' +
                ", typeName='" + typeName + '\'' +
                ", groupName='" + groupName + '\'' +
                ", image='" + image + '\'' +
                '}';
    }

    public int[] getParams() {
        return params;
    }

    public void setParams(int[] params) {
        this.params = params;
    }

    public int[] getPacks() {
        return packs;
    }

    public void setPacks(int[] packs) {
        this.packs = packs;
    }

    @Override
    public String toString() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTypeID() {
        return typeID;
    }

    public void setTypeID(int typeID) {
        this.typeID = typeID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}

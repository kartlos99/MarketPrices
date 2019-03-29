package diakonidze.marketprices.models;

import java.io.Serializable;
import java.util.Arrays;

import diakonidze.marketprices.util.GlobalConstants;

public class Product implements Serializable {
    private int id, typeID, packID = 10, brandID = 9;
    private String name = "", typeName, groupName, qrCode = "";
    private String image = "a";
    private int[] paramIDs;
    private String[] paramValues, paramNames;


    public Product(int id, String name) {
        this.id = id;
        this.name = name;
        paramIDs = new int[0];
        paramValues = new String[0];
    }

    public Product() {
        paramIDs = new int[0];
        paramValues = new String[0];
    }

    public String allToString() {
        return "Product{" +
                "id=" + id +
                ", typeID=" + typeID +
                ", packID=" + packID +
                ", brandID=" + brandID +
                ", name='" + name + '\'' +
                ", typeName='" + typeName + '\'' +
                ", qrCode='" + qrCode + '\'' +
                ", image='" + image + '\'' +
                ", paramIDs=" + Arrays.toString(paramIDs) +
                ", paramValues=" + Arrays.toString(paramValues) +
                ", paramNames=" + Arrays.toString(paramNames) +
                '}';
    }

    public int[] getParamIDs() {
        return paramIDs;
    }

    public void setParamIDs(int[] paramIDs) {
        this.paramIDs = paramIDs;
    }

    @Override
    public String toString() {
        Brand brand = GlobalConstants.findBrandByID(brandID);
        if (brand != null){
            return name + ", " + brand.getBrandName();
        }
        return name ;
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

    public int getPackID() {
        return packID;
    }

    public void setPackID(int packID) {
        this.packID = packID;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String[] getParamValues() {
        return paramValues;
    }

    public void setParamValues(String[] paramValues) {
        this.paramValues = paramValues;
    }

    public String[] getParamNames() {
        return paramNames;
    }

    public void setParamNames(String[] paramNames) {
        this.paramNames = paramNames;
    }

    public int getBrandID() {
        return brandID;
    }

    public void setBrandID(int brandID) {
        this.brandID = brandID;
    }
}

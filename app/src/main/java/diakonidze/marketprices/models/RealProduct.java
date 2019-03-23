package diakonidze.marketprices.models;

import java.io.Serializable;
import java.util.Arrays;

public class RealProduct implements Serializable {
    private int id, productID, brandID, marketID, addUserID;
    private String prAddDate, comment, product_name, marketName, brandName, packing, image = "";
    private Float price;
    private int[] paramIDs;
    private String[] paramValues, paramNames;
    private Boolean isInMyList = false;
    private Boolean checked = false;
    private Product product;

    public RealProduct() {
        product = new Product(0, "");
        product.setParamIDs(new int[0]);
        product.setParamValues(new String[0]);
        product.setParamNames(new String[0]);
    }

    @Override
    public String toString() {
        return "RealProduct{" +
                "id=" + id +
                ", productID=" + productID +
                ", brandID=" + brandID +
                ", marketID=" + marketID +
                ", addUserID=" + addUserID +
                ", prAddDate='" + prAddDate + '\'' +
                ", comment='" + comment + '\'' +
                ", product_name='" + product_name + '\'' +
                ", marketName='" + marketName + '\'' +
                ", brandName='" + brandName + '\'' +
                ", packing='" + packing + '\'' +
                ", image='" + image + '\'' +
                ", price=" + price +
                ", paramIDs=" + Arrays.toString(paramIDs) +
                ", paramValues=" + Arrays.toString(paramValues) +
                ", paramNames=" + Arrays.toString(paramNames) +
                ", isInMyList=" + isInMyList +
                ", checked=" + checked +
                ", product='" + product.allToString() + '\'' +
                '}';
    }

    public String toStringOld() {
        return "RealProduct{" +
                "id=" + id +
                ", productID=" + productID +
                ", brandID=" + brandID +
                ", marketID=" + marketID +
                ", prAddDate='" + prAddDate + '\'' +
                ", comment='" + comment + '\'' +
                ", product_name='" + product_name + '\'' +
                ", marketName='" + marketName + '\'' +
                ", brandName='" + brandName + '\'' +
                ", packing='" + packing + '\'' +
                ", image='" + image + '\'' +
                ", price=" + price +
                ", paramIDs=" + Arrays.toString(paramIDs) +
                ", paramValues=" + Arrays.toString(paramValues) +
                ", paramNames=" + Arrays.toString(paramNames) +
                ", isInMyList=" + isInMyList +
                ", checked=" + checked +
                '}';
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public String[] getParamNames() {
        return paramNames;
    }

    public void setParamNames(String[] paramNames) {
        this.paramNames = paramNames;
    }

    public Boolean getInMyList() {
        return isInMyList;
    }

    public void setInMyList(Boolean inMyList) {
        isInMyList = inMyList;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getPacking() {
        return packing;
    }

    public void setPacking(String packing) {
        this.packing = packing;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int[] getParamIDs() {
        return paramIDs;
    }

    public void setParamIDs(int[] paramIDs) {
        this.paramIDs = paramIDs;
    }

    public String[] getParamValues() {
        return paramValues;
    }

    public void setParamValues(String[] paramValues) {
        this.paramValues = paramValues;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public int getBrandID() {
        return brandID;
    }

    public void setBrandID(int brandID) {
        this.brandID = brandID;
    }

    public int getMarketID() {
        return marketID;
    }

    public void setMarketID(int marketID) {
        this.marketID = marketID;
    }

    public String getPrAddDate() {
        return prAddDate;
    }

    public void setPrAddDate(String prAddDate) {
        this.prAddDate = prAddDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }
}

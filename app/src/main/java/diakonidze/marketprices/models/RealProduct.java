package diakonidze.marketprices.models;

import java.io.Serializable;
import java.util.Arrays;

public class RealProduct implements Serializable {
    int id, productID, brandID, marketID, packingID, countryID;
    String prAddDate, comment;
    Float price;
    private int[] paramIDs;
    private String[] paramValues;

    public RealProduct() {
    }

    @Override
    public String toString() {
        return "RealProduct{" +
                "id=" + id +
                ", productID=" + productID +
                ", brandID=" + brandID +
                ", marketID=" + marketID +
                ", packingID=" + packingID +
                ", countryID=" + countryID +
                ", prAddDate='" + prAddDate + '\'' +
                ", comment='" + comment + '\'' +
                ", price=" + price +
                ", paramIDs=" + Arrays.toString(paramIDs) +
                ", paramValues=" + Arrays.toString(paramValues) +
                '}';
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

    public int getPackingID() {
        return packingID;
    }

    public void setPackingID(int packingID) {
        this.packingID = packingID;
    }

    public int getCountryID() {
        return countryID;
    }

    public void setCountryID(int countryID) {
        this.countryID = countryID;
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

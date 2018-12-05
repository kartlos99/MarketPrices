package diakonidze.marketprices.models;

public class Market {
    int id;
    String marketName, sn, logo, image, address, comment;
    Double locationX, locationY;

    @Override
    public String toString() {
        return marketName;
    }

    public String fulltoString() {
        return "Market{" +
                "id=" + id +
                ", marketName='" + marketName + '\'' +
                ", sn='" + sn + '\'' +
                ", logo='" + logo + '\'' +
                ", image='" + image + '\'' +
                ", address='" + address + '\'' +
                ", comment='" + comment + '\'' +
                ", locationX=" + locationX +
                ", locationY=" + locationY +
                '}';
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Double getLocationX() {
        return locationX;
    }

    public void setLocationX(Double locationX) {
        this.locationX = locationX;
    }

    public Double getLocationY() {
        return locationY;
    }

    public void setLocationY(Double locationY) {
        this.locationY = locationY;
    }

    public Market(int id, String marketName) {
        this.id = id;
        this.marketName = marketName;
    }
}

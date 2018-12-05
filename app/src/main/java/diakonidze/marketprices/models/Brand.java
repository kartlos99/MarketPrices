package diakonidze.marketprices.models;

public class Brand {
    int id;
    String brandName, brandNameEng;

    public Brand(int id, String brandName, String brandNameEng) {
        this.id = id;
        this.brandName = brandName;
        this.brandNameEng = brandNameEng;
    }

    @Override
    public String toString() {
        return "Brand{" +
                "id=" + id +
                ", brandName='" + brandName + '\'' +
                ", brandNameEng='" + brandNameEng + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getBrandNameEng() {
        return brandNameEng;
    }

    public void setBrandNameEng(String brandNameEng) {
        this.brandNameEng = brandNameEng;
    }
}

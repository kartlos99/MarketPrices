package diakonidze.marketprices.models;

public class ProductType {
    private int id;
    private String code, name, image;
    private int[] all_param, all_pack;

    public ProductType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int[] getAll_param() {
        return all_param;
    }

    public void setAll_param(int[] all_param) {
        this.all_param = all_param;
    }

    public int[] getAll_pack() {
        return all_pack;
    }

    public void setAll_pack(int[] all_pack) {
        this.all_pack = all_pack;
    }
}

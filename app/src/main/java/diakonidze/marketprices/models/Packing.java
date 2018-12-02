package diakonidze.marketprices.models;

public class Packing {
    private int id;
    private String code, value;

    public Packing(int id, String code, String value) {
        this.id = id;
        this.code = code;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Packing{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}

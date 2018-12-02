package diakonidze.marketprices.models;

public class Paramiter {
    private int id;
    private String code, name, measureUnit;

    public Paramiter(int id, String code, String name, String measureUnit) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.measureUnit = measureUnit;
    }

    @Override
    public String toString() {
        return "Paramiter{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", measureUnit='" + measureUnit + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getMeasureUnit() {
        return measureUnit;
    }
}

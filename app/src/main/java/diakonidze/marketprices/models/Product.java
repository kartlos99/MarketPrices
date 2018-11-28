package diakonidze.marketprices.models;

public class Product {
    private int id, typeID;
    private String name, typeName, groupName;

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
                '}';
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
}

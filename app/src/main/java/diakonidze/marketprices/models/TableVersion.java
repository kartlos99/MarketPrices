package diakonidze.marketprices.models;

public class TableVersion {
    private String tableName;
    private int version, maxID;
    private int needUpdate = -1; // -1 - not need, 0 - Full, any numb - add from this

    public TableVersion() {  }

    public TableVersion(String tableName, int version, int maxID) {
        this.tableName = tableName;
        this.version = version;
        this.maxID = maxID;
    }

    @Override
    public String toString() {
        return "TableVersion{" +
                "tableName='" + tableName + '\'' +
                ", version=" + version +
                ", maxID=" + maxID +
                ", needUpdate=" + needUpdate +
                '}';
    }

    public String inQuery(){
        return tableName + "=" + needUpdate;
    }

    public int getNeedUpdate() {
        return needUpdate;
    }

    public void setNeedUpdate(int needUpdate) {
        this.needUpdate = needUpdate;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getMaxID() {
        return maxID;
    }

    public void setMaxID(int maxID) {
        this.maxID = maxID;
    }
}

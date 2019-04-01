package diakonidze.marketprices.models;

public class MyListItem {
    private int realProdID;
    private Boolean isChecked;

    public MyListItem() {

    }

    @Override
    public String toString() {
        return "MyListItem{" +
                "realProdID=" + realProdID +
                ", isChecked=" + isChecked +
                '}';
    }

    public int getRealProdID() {
        return realProdID;
    }

    public void setRealProdID(int realProdID) {
        this.realProdID = realProdID;
    }

    public Boolean getChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }
}

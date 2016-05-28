/**
 * Created by Christopher on 5/25/2016.
 */
public enum ItemStatus {
    Default(0),
    Found(1),
    NotFound(2);

    private int ItemStatus;

    private ItemStatus(int itemStatus) {
        this.ItemStatus = itemStatus;
    }

    public int getItemStatus() {
        return ItemStatus;
    }
}

/**
 * Created by Christopher on 9/2/2015.
 */
public class ItemQueries {
    private ItemQueries(){}
    public static final String GetItemById(int itemId) {
        return String.format("SELECT * " +
                "FROM Items " +
                "WHERE ItemId = %d", itemId);
    }
    public static final String GetItemByName(String itemName) {
        String rv = String.format("SELECT * " +
                "FROM items " +
                "WHERE ItemName = '%s'", itemName);
        return rv;
    }
    public static final String AddItem(String itemName) {
        return String.format("INSERT INTO Items (ItemId, ItemName, ListActive) " +
                "VALUES (nextval('Item_Seq'), '%s', '%b') " +
                "RETURNING ItemId, ItemName, BestPrice, ListActive, StoreId", itemName, String.valueOf(true));
    }
    public static final String AddStoreToItem(int itemId, int storeId) {
        return String.format("UPDATE Items " +
                "SET StoreId = %d " +
                "WHERE ItemId = %d", storeId, itemId);
    }
    public static final String RemoveItemFromList(int itemId) {
        return String.format("UPDATE Items " +
                "SET ListActive = FALSE " +
                "WHERE ItemId = %d", itemId);
    }
    public static final String RemoveItemFromLibrary(int itemId) {
        return String.format("UPDATE Items " +
                "SET LibraryActive = FALSE " +
                "WHERE ItemId = %d", itemId);
    }
    public static final String UpdateItemById(int itemId, Item newItem) throws Exception {
        if(newItem.getStore() != null)
        {
            return String.format("UPDATE Items " +
                            "SET (ItemName, BestPrice, ListActive, LibraryActive, StoreId) = ('%s', %f, %b, %b, %d) WHERE ItemId = %d",
                    newItem.getName(), newItem.getBestPrice(),
                    newItem.isListActive(), newItem.isLibraryActive(),
                    newItem.getStore().getStoreId(), itemId);
        }
        return String.format("UPDATE Items " +
                        "SET (ItemName, BestPrice, ListActive, LibraryActive) = ('%s', %f, %b, %b) WHERE ItemId = %d",
                newItem.getName(), newItem.getBestPrice(),
                newItem.isListActive(), newItem.isLibraryActive(), itemId);
    }
    public static final String GetCountFromItemList (int count) {
        return String.format("SELECT * " +
                "FROM Items " +
                "WHERE ListActive = TRUE", count);
    }
    public static final String GetAllItemsFromLibrary = "SELECT * " +
            "FROM Items " +
            "WHERE LibraryActive = TRUE";

}

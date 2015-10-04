/**
 * Created by Christopher on 9/2/2015.
 */
public class itemQueries {
    public static final String getItemById(int itemId) {
        return String.format("SELECT * " +
                "FROM Items " +
                "WHERE ItemId = %d", itemId);
    }
    public static final String getItemByName(String itemName) {
        String rv = String.format("SELECT * " +
                "FROM Items " +
                "WHERE ItemName = '%s'", itemName);
        return rv;
    }
    public static final String addItem(Item item) {
        if(item.getStore() != null) {
            return String.format("INSERT INTO Items (ItemId, ItemName, BestPrice, ListActive, LibraryActive, StoreId) " +
                    "VALUES (nextval('Item_Seq'), '%s', %f, %b, %b, (SELECT StoreId FROM Stores WHERE StoreName = '%s')) " +
                    "RETURNING ItemId, ItemName, BestPrice, StoreId", item.getName(), item.getBestPrice(), true, true, item.getStore().getName());
        } else {
            return String.format("INSERT INTO Items (ItemId, ItemName, BestPrice, ListActive, LibraryActive) " +
                    "VALUES (nextval('Item_Seq'), '%s', %f, %b, %b) " +
                    "RETURNING ItemId, ItemName, BestPrice", item.getName(), item.getBestPrice(), true, true);
        }
    }
    public static final String addStoreToItem(int itemId, int storeId) {
        return String.format("UPDATE Items " +
                "SET StoreId = %d " +
                "WHERE ItemId = %d", storeId, itemId);
    }
    public static final String removeItemFromList(int itemId) {
        return String.format("UPDATE Items " +
                "SET ListActive = FALSE " +
                "WHERE ItemId = %d", itemId);
    }
    public static final String removeItemFromLibrary(int itemId) {
        return String.format("UPDATE Items " +
                "SET LibraryActive = FALSE " +
                "WHERE ItemId = %d", itemId);
    }
    public static final String updateItemById(Item newItem) {
        if(newItem.getStore() != null)
        {
            return String.format("UPDATE Items " +
                            "SET (ItemName, BestPrice, StoreId, ListActive, LibraryActive) = ('%s', %f, %d, %b, %b) WHERE ItemId = %d",
                    newItem.getName(), newItem.getBestPrice(),
                    newItem.getStore().getId(), true, true, newItem.getId());
        }
        return String.format("UPDATE Items " +
                        "SET (ItemName, BestPrice, ListActive, LibraryActive) = ('%s', %f, %b, %b) WHERE ItemId = %d",
                newItem.getName(), newItem.getBestPrice(), true, true, newItem.getId());
    }
    public static final String updateItemByName(Item newItem) {
        if(newItem.getStore() != null)
        {
            return String.format("UPDATE Items " +
                            "SET (BestPrice, StoreId, ListActive, LibraryActive) = (%f, %d, %b, %b) WHERE ItemName = '%s'",
                    newItem.getBestPrice(), newItem.getStore().getId(), true, true, newItem.getName());
        }
        return String.format("UPDATE Items " +
                        "SET (BestPrice, ListActive, LibraryActive) = (%f, %b, %b) WHERE ItemName = '%s'",
                newItem.getBestPrice(), true, true, newItem.getName());
    }
    public static final String makeActiveById(Item newItem) {
        return String.format("UPDATE Items " +
                        "SET (ListActive) = (%b) WHERE ItemId = %d",
                true, newItem.getId());
    }
    public static final String makeActiveByName(Item newItem) {
        return String.format("UPDATE Items " +
                        "SET (ListActive) = (%b) WHERE ItemName = '%s'",
                true, newItem.getName());
    }
    public static final String getAllItemsFromList() {
        return String.format("SELECT Items.ItemId, Items.ItemName, Items.BestPrice, Items.ListActive, Items.LibraryActive, Stores.StoreId, Stores.StoreName " +
                "FROM Items " +
                "LEFT JOIN Stores ON Items.StoreId = Stores.StoreId " +
                "WHERE Items.ListActive = TRUE");
    }
    public static final String getAllItemsFromLibrary = "SELECT * " +
            "FROM Items " +
            "WHERE LibraryActive = TRUE";

}

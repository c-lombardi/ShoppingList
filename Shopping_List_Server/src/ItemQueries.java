import java.util.List;
import java.util.UUID;

/**
 * Created by Christopher on 9/2/2015.
 */
public class itemQueries {
    public static final String getAllItemsFromLibrary = "SELECT * " +
            "FROM Items " +
            "WHERE LibraryActive = TRUE AND ListActive = FALSE";

    public static final String getItemById(final int itemId) {
        return String.format("SELECT * " +
                "FROM Items " +
                "WHERE ItemId = %d", itemId);
    }

    public static final String getItemByName(final String itemName) {
        String rv = String.format("SELECT * " +
                "FROM Items " +
                "WHERE ItemName = '%s'", itemName);
        return rv;
    }

    public static final String addItem(final Item item) {
        if (item.getStore() != null) {
            return String.format("INSERT INTO Items (ItemId, ItemName, SessionId, BestPrice, ListActive, LibraryActive, StoreId) " +
                    "VALUES (nextval('Item_Seq'), '%s', '%s', %f, %b, %b, (SELECT StoreId FROM Stores WHERE StoreName = '%s')) " +
                    "RETURNING ItemId, ItemName, BestPrice, StoreId", item.getName(), item.getSessionId().toString(), item.getBestPrice(), true, true, item.getStore().getName());
        } else {
            return String.format("INSERT INTO Items (ItemId, ItemName, SessionId, BestPrice, ListActive, LibraryActive) " +
                    "VALUES (nextval('Item_Seq'), '%s', '%s', %f, %b, %b) " +
                    "RETURNING ItemId, ItemName, BestPrice", item.getName(), item.getSessionId().toString(), item.getBestPrice(), true, true);
        }
    }

    public static final String removeItemFromList(final int itemId) {
        return String.format("UPDATE Items " +
                "SET ListActive = FALSE " +
                "WHERE ItemId = %d", itemId);
    }

    public static final String removeItemFromLibrary(final int itemId) {
        return String.format("UPDATE Items " +
                "SET LibraryActive = FALSE " +
                "WHERE ItemId = %d", itemId);
    }

    public static final String updateItemById(final Item newItem) {
        if (newItem.getStore() != null) {
            return String.format("UPDATE Items " +
                            "SET (ItemName, BestPrice, StoreId, ListActive, LibraryActive) = ('%s', %f, %d, %b, %b) WHERE ItemId = %d",
                    newItem.getName(), newItem.getBestPrice(),
                    newItem.getStore().getId(), true, true, newItem.getId());
        }
        return String.format("UPDATE Items " +
                        "SET (ItemName, BestPrice, ListActive, LibraryActive) = ('%s', %f, %b, %b) WHERE ItemId = %d",
                newItem.getName(), newItem.getBestPrice(), true, true, newItem.getId());
    }

    public static final String updateItemByName(final Item newItem) {
        if (newItem.getStore() != null) {
            return String.format("UPDATE Items " +
                            "SET (BestPrice, StoreId, ListActive, LibraryActive) = (%f, %d, %b, %b) WHERE ItemName = '%s'",
                    newItem.getBestPrice(), newItem.getStore().getId(), true, true, newItem.getName());
        }
        return String.format("UPDATE Items " +
                        "SET (BestPrice, ListActive, LibraryActive) = (%f, %b, %b) WHERE ItemName = '%s'",
                newItem.getBestPrice(), true, true, newItem.getName());
    }

    public static final String makeActiveById(final Item newItem) {
        return String.format("UPDATE Items " +
                        "SET (ListActive) = (%b) WHERE ItemId = %d",
                true, newItem.getId());
    }

    public static final String makeActiveByName(final Item newItem) {
        return String.format("UPDATE Items " +
                        "SET (ListActive) = (%b) WHERE ItemName = '%s'",
                true, newItem.getName());
    }

    public static final String getAllItemsFromListBySessionId(final UUID sId) {
        return String.format("SELECT Items.ItemId, Items.ItemName, Items.SessionId, Items.BestPrice, Items.ListActive, Items.LibraryActive, Stores.StoreId, Stores.StoreName, Items.SessionId " +
                "FROM Items " +
                "LEFT JOIN Stores ON Items.StoreId = Stores.StoreId " +
                "WHERE Items.ListActive = TRUE AND Items.SessionId = '%s'", sId);
    }

    public static final String reAddItemsByIds(final List<Integer> itemIds) {
        StringBuilder queryStringBuilder = new StringBuilder(String.format("UPDATE Items " +
                "SET (ListActive) = (%b) WHERE ", true));
        if (!itemIds.isEmpty()) {
            int count = 0;
            for (Integer itemId : itemIds) {
                count++;
                if (itemIds.size() != count) {
                    queryStringBuilder.append(String.format("ItemId = %d OR ", itemId));
                } else {
                    queryStringBuilder.append(String.format("ItemId = %d", itemId));
                }
            }
            return queryStringBuilder.toString();
        }
        return null;
    }

    public static final String removeItemsByIds(final List<Integer> itemIds) {
        StringBuilder queryStringBuilder = new StringBuilder("UPDATE Items " +
                "SET ListActive = FALSE " +
                "WHERE ");
        if (!itemIds.isEmpty()) {
            int count = 0;
            for (Integer itemId : itemIds) {
                count++;
                if (itemIds.size() != count) {
                    queryStringBuilder.append(String.format("ItemId = %d OR ", itemId));
                } else {
                    queryStringBuilder.append(String.format("ItemId = %d", itemId));
                }
            }
            return queryStringBuilder.toString();
        }
        return null;
    }

    public static final String getItemsByIds(final List<Integer> itemIds, final UUID sId) {
        StringBuilder queryStringBuilder = new StringBuilder(getAllItemsFromListBySessionId(sId));
        queryStringBuilder.append(" AND ");
        if (!itemIds.isEmpty()) {
            int count = 0;
            for (Integer itemId : itemIds) {
                count++;
                if (itemIds.size() != count) {
                    queryStringBuilder.append(String.format("ItemId = %d OR ", itemId));
                } else {
                    queryStringBuilder.append(String.format("ItemId = %d", itemId));
                }
            }
            return queryStringBuilder.toString();
        }
        return null;
    }

    public static final String getLibraryItemsWithCharactersAndSessionId(final String itemSearchString, final UUID sId) {
        try {
            if (itemSearchString.isEmpty()) {
                throw new Exception();
            }
            StringBuilder queryStringBuilder = new StringBuilder(String.format("SELECT ItemId, ItemName " +
                    "FROM Items " +
                    "WHERE (LibraryActive = TRUE AND ListActive = FALSE AND SessionId = '%s') " +
                    "AND lower(ItemName) LIKE '%%" +
                    "%s" +
                    "%%'", sId.toString(), itemSearchString.toLowerCase()));
            return queryStringBuilder.toString();
        } catch (Exception ex) {
            return getAllItemsFromLibrary;
        }
    }
}

import java.util.List;

/**
 * Created by Christopher on 9/2/2015.
 */
public class ItemQueries {

    public static final String getAllItemsFromLibrary(final String sId, final int slId) {//this should stay the same, libraries are universal across shopping lists
        return String.format("SELECT i.ItemId, i.ItemName " +
                "FROM %s i " +
                "WHERE i.SessionId = '%s' " +
                "AND i.ItemId NOT IN (SELECT ItemId FROM %s WHERE ShoppingListId = %d);" , Database.ItemsTableName, sId, Database.ShoppingListItemsTableName, slId);
    }

    public static final String getItemById(final int itemId) {
        return String.format("SELECT i.ItemId, i.ItemName, i.BestPrice, i.StoreId, i.SessionId, s.StoreName, sli.ItemStatus, sli.ShoppingListId " +
                "FROM %s i " +
                "LEFT JOIN %s sli ON sli.ItemId = i.ItemId " +
                "LEFT JOIN %s s ON s.StoreId = i.StoreId " +
                "WHERE i.ItemId = %d", Database.ItemsTableName, Database.ShoppingListItemsTableName, Database.StoresTableName, itemId);
    }

    public static final String addItem(final Item item) {
        if (item.getStore() != null) {
            return String.format("INSERT INTO %s (ItemId, ItemName, BestPrice, StoreId, SessionId) " +
                    "VALUES (nextval('Item_Seq'), '%s', %f, (SELECT StoreId FROM %s WHERE StoreName = '%s'), '%s') " +
                    "RETURNING ItemId, ItemName, BestPrice, StoreId", Database.ItemsTableName, item.getName(), item.getBestPrice(), Database.StoresTableName, item.getStore().getName(), item.getSessionId().toString());
        } else {
            return String.format("INSERT INTO %s (ItemId, ItemName, BestPrice, SessionId) " +
                    "VALUES (nextval('Item_Seq'), '%s', %f, '%s') " +
                    "RETURNING ItemId, ItemName, BestPrice", Database.ItemsTableName, item.getName(), item.getBestPrice(), item.getSessionId().toString());
        }
    }

    public static final String removeItemFromList(final int itemId, final int slId) {
        return String.format("DELETE FROM %s " +
                "WHERE ItemId = %d AND ShoppingListId = %d", Database.ShoppingListItemsTableName, itemId, slId);
    }

    public static final String removeItemsFromList(final int slId) {
        return String.format("DELETE FROM %s " +
                "WHERE ShoppingListId = %d", Database.ShoppingListItemsTableName, slId);
    }

    public static final String addItemToShoppingList(final int itemId, final int slId) {
        return String.format("INSERT INTO %s (ItemId, ShoppingListId) " +
                "VALUES ('%d', '%d')", Database.ShoppingListItemsTableName, itemId, slId);
    }

    public static final String updateItemById(final Item newItem) {
        if (newItem.getStore() != null) {
            return String.format("UPDATE %s " +
                            "SET (ItemName, BestPrice, StoreId) = ('%s', %f, %d) " +
                            "WHERE ItemId = %d", Database.ItemsTableName, newItem.getName(), newItem.getBestPrice(), newItem.getStore().getId(), newItem.getId());
        }
        return String.format("UPDATE %s " +
                        "SET (ItemName, BestPrice) = ('%s', %f) " +
                        "WHERE ItemId = %d", Database.ItemsTableName, newItem.getName(), newItem.getBestPrice(), newItem.getId());
    }

    public static final String getAllItemsFromListByShoppingListId(final int slId) {
        return String.format("SELECT i.ItemId, i.ItemName, i.SessionId, i.BestPrice, s.StoreId, s.StoreName, sli.ShoppingListId, sli.ItemStatus " +
                "FROM %s i " +
                "LEFT JOIN %s s ON i.StoreId = s.StoreId " +
                "INNER JOIN %s sli ON sli.ItemId = i.ItemId AND sli.ShoppingListId = %d;", Database.ItemsTableName, Database.StoresTableName, Database.ShoppingListItemsTableName, slId);
    }

    public static final String reAddItemsByIds(final List<Item> items, final int ShoppingListId) {
        final StringBuilder queryStringBuilder = new StringBuilder(String.format("INSERT INTO %s (ItemId, ShoppingListId)" +
                "SELECT ItemId, %d FROM Items WHERE ", Database.ShoppingListItemsTableName, ShoppingListId));
        if (!items.isEmpty()) {
            int count = 0;
            for (Item item : items) {
                count++;
                if (items.size() != count) {
                    queryStringBuilder.append(String.format("ItemId = %d OR ", item.getId()));
                } else {
                    queryStringBuilder.append(String.format("ItemId = %d ", item.getId()));
                }
            }
            return queryStringBuilder.toString();
        }
        return null;
    }

    public static final String changeItemStatusByItemId(final Item item) {
        final String queryString = String.format("UPDATE %s " +
                "SET ItemStatus = '%s' WHERE ItemId = %d", Database.ShoppingListItemsTableName, item.getItemStatus(), item.getId());
        return queryString;
    }

    public static final String removeItemsByIds(final List<Item> items, final int slId) {
        StringBuilder queryStringBuilder = new StringBuilder(String.format("DELETE FROM %s WHERE (", Database.ShoppingListItemsTableName ) );
        if (!items.isEmpty()) {
            int count = 0;
            for (Item item : items) {
                count++;
                if (items.size() != count) {
                    queryStringBuilder.append(String.format("ItemId = %d OR ", item.getId()));
                } else {
                    queryStringBuilder.append(String.format("ItemId = %d) AND ShoppingListId = %d;", item.getId(), slId));
                }
            }
            return queryStringBuilder.toString();
        }
        return null;
    }

    public static final String getItemsByIds(final List<Item> items, final int slId) {
        StringBuilder queryStringBuilder = new StringBuilder(String.format("SELECT i.ItemId, i.ItemName, i.SessionId, i.BestPrice, s.StoreId, s.StoreName, sli.ShoppingListId, sli.ItemStatus " +
                "FROM %s i " +
                "LEFT JOIN %s s ON i.StoreId = s.StoreId " +
                "INNER JOIN %s sli ON sli.ItemId = i.ItemId AND sli.ShoppingListId = %d", Database.ItemsTableName, Database.StoresTableName, Database.ShoppingListItemsTableName, slId));
        queryStringBuilder.append(" AND ");
        if (!items.isEmpty()) {
            int count = 0;
            for (Item item : items) {
                count++;
                if (items.size() != count) {
                    queryStringBuilder.append(String.format("i.ItemId = %d OR ", item.getId()));
                } else {
                    queryStringBuilder.append(String.format("i.ItemId = %d;", item.getId()));
                }
            }
            return queryStringBuilder.toString();
        }
        return null;
    }

    public static final String getLibraryItemsWithCharactersAndSessionId(final String itemSearchString, final String sId, final int slId) {
        return String.format("SELECT * " +
                "FROM %s i " +
                "WHERE i.SessionId = '%s' " +
                "AND lower(i.ItemName) LIKE '%%" +
                "%s" +
                "%%' AND i.ItemId NOT IN (SELECT ItemId FROM %s WHERE ShoppingListId = %d);" , Database.ItemsTableName, sId.toString(), itemSearchString, Database.ShoppingListItemsTableName, slId);
    }
}

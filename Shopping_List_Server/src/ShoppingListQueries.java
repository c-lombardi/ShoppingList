import java.util.UUID;

/**
 * Created by Christopher on 3/14/2016.
 */
public class ShoppingListQueries {
    public static final String CREATE_SHOPPING_LIST_BY_NAME_AND_SESSION_ID (final String shoppingListName, final UUID sId) {
        return String.format("INSERT INTO %s (ShoppingListId, ShoppingListName, SessionId) " +
                "VALUES (nextval('Shopping_List_Seq'), '%s', '%s') " +
                "RETURNING ShoppingListId;", Database.ShoppingListsTableName, shoppingListName, sId.toString());
    }

    public static final String RENAME_SHOPPING_LIST_BY_SHOPPING_LIST_ID (final Integer shoppingListId, final String newName) {
        return String.format("UPDATE %s " +
                "SET ShoppingListName = '%s' " +
                "WHERE ShoppingListId = %d;", Database.ShoppingListsTableName, newName, shoppingListId);
    }

    public static final String GET_SHOPPING_LIST_BY_SHOPPING_LIST_ID (final Integer shoppingListId) {
        return String.format("SELECT * " +
                "FROM %s " +
                "WHERE ShoppingListId = %d;", Database.ShoppingListsTableName, shoppingListId);
    }

    public static final String GET_SHOPPING_LISTS_BY_SESSION_ID (final String sessionId) {
        return String.format("SELECT * " +
                "FROM %s " +
                "WHERE SessionId = '%s';", Database.ShoppingListsTableName, sessionId.toString());
    }

    public static final String REMOVE_SHOPPING_LIST_BY_SHOPPING_LIST_ID (final Integer shoppingListId) {
        return String.format("DELETE " +
                "FROM %s " +
                "WHERE ShoppingListId = %d;", Database.ShoppingListsTableName, shoppingListId);
    }
}

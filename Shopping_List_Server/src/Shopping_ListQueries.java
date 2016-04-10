import java.util.UUID;

/**
 * Created by Christopher on 3/14/2016.
 */
public class Shopping_ListQueries {
    public static String CREATE_SHOPPING_LIST_BY_NAME_AND_SESSION_ID (String shoppingListName, UUID sId) {
        return String.format("INSERT INTO Shopping_Lists (ShoppingListId, ShoppingListName, SessionId) " +
                "VALUES (nextval('Shopping_List_Seq'), '%s', '%s') " +
                "RETURNING ShoppingListId;", shoppingListName, sId.toString());
    }

    public static String RENAME_SHOPPING_LIST_BY_SHOPPING_LIST_ID (Integer shoppingListId, String newName) {
        return String.format("UPDATE Shopping_Lists " +
                "SET ShoppingListName = '%s' " +
                "WHERE ShoppingListId = %d;", newName, shoppingListId);
    }

    public static String GET_SHOPPING_LIST_BY_SHOPPING_LIST_ID (Integer shoppingListId) {
        return String.format("SELECT * " +
                "FROM Shopping_Lists " +
                "WHERE ShoppingListId = %d;", shoppingListId);
    }

    public static String GET_SHOPPING_LISTS_BY_SESSION_ID (UUID sessionId) {
        return String.format("SELECT * " +
                "FROM Shopping_Lists " +
                "WHERE SessionId = '%s';", sessionId.toString());
    }
}

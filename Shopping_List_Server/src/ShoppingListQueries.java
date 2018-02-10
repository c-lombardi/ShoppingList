/**
 * Created by Christopher on 3/14/2016.
 */
public class ShoppingListQueries {
    public static final String CREATE_SHOPPING_LIST_BY_NAME_AND_SESSION_ID () {
        return "SELECT createShoppingList(?, ?);";
    }

    public static final String RENAME_SHOPPING_LIST_BY_SHOPPING_LIST_ID () {
        return "SELECT updateShoppingListNameByShoppingListId(?, ?);";
    }

    public static final String GET_SHOPPING_LIST_BY_SHOPPING_LIST_ID () {
        return "SELECT getShoppingListByShoppingListId(?);";
    }

    public static final String GET_SHOPPING_LISTS_BY_SESSION_ID () {
        return "SELECT getShoppingListsBySessionId(?);";
    }

    public static final String REMOVE_SHOPPING_LIST_BY_SHOPPING_LIST_ID () {
        return "SELECT deleteShoppingListByShoppingListId(?);";
    }
}

/**
 * Created by Christopher on 9/2/2015.
 */
public class ItemQueries {

    public static final String getAllItemsFromLibrary() {//this should stay the same, libraries are universal across shopping lists
        return "SELECT getAllitemsFromlibrary(?, ?);";
    }

    public static final String getItemById() {
        return "SELECT getItemById(?);";
    }

    public static final String addItem(final Item item) {
        if (item.getStore() != null) {
            return "SELECT addItemWithStore(?, ?, ?, ?);";
        } else {
            return "SELECT addItemWithStore(?, ?, ?);";
        }
    }

    public static final String removeItemFromList() {
        return "SELECT removeItemFromList(?, ?);";
    }

    public static final String removeItemsFromList() {
        return "SELECT removeItemFromList(?);";
    }

    public static final String addItemToShoppingList() {
        return "SELECT addItemToShoppingList(?, ?);";
    }

    public static final String updateItemById(final Item newItem) {
        if (newItem.getStore() != null) {
            return "SELECT updateItemByIdWithStore(?, ?, ?, ?);";
        }
        return "SELECT updateItemById(?, ?, ?);";
    }

    public static final String getAllItemsFromListByShoppingListId() {
        return "SELECT selectAllItemsFromShoppingList(?)";
    }

    public static final String reAddItemsByIds() {
        return "SELECT reAddItemsById(?, ?);";
    }

    public static final String changeItemStatusByItemId() {
        return "SELECT updateItemStatusById(?, ?);";
    }

    public static final String removeItemsByIds() {
        return "SELECT removeItemsByIds(?);";
    }

    public static final String getItemsByIds() {
        return "SELECT getItemsByIds(?, ?);";
    }

    public static final String getLibraryItemsWithCharactersAndSessionId() {
        return "SELECT getLibraryItemsWithCharactersAndSessionId(?, ?, ?);";
    }
}

package com.example.christopher.shopping_list_app.Server;

/**
 * Created by Christopher on 3/12/2016.
 */
public class ByteCommandHelper {
    public static String ByteCommandStringValue (ByteCommand command){
        switch (command) {
            case getItems:
                return "Retrieved Items";
            case addItem:
                return "Added Item";
            case updateItem:
                return "Updated Item";
            case removeItemFromList:
                return "Removed Item";
            case getLibrary:
                return "Retrieved Library";
            case reAddItems:
                return "Re-Added Item(s)";
            case removeItemsFromList:
                return "Removed Item(s)";
            case getLibraryItemsThatContain:
                return "Retrieved Library Like Term";
            case createShoppingList:
                return "Shopping List Created";
            case renameShoppingList:
                return "Shopping List Renamed";
            case removeShoppingList:
                return "Removed Shopping List";
            case getListOfShoppingLists:
                return "Retrieved Shopping Lists";
            default:
                return "Performed a command";
        }
    }
}

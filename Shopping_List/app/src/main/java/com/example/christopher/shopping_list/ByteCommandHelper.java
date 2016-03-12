package com.example.christopher.shopping_list;

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
            default:
                return "Performed a command";
        }
    }
}

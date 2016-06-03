package com.example.christopher.shopping_list_app.Models;

/**
 * Created by Christopher on 6/3/2016.
 */
public class ItemStatusHelper {
    public static String ItemStatusHelperStringValue (ItemStatus command){
        switch (command) {
            case Found:
                return "Found";
            case NotFound:
                return "Not Found";
            default:
                return "I don't know";
        }
    }
}

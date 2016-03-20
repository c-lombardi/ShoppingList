package com.example.christopher.shopping_list.Models;
import java.util.UUID;

/**
 * Created by Christopher on 3/14/2016.
 */
public class Shopping_List {
    private String ShoppingListName;
    private UUID SessionId;
    private Integer ShoppingListId;

    public Integer getShoppingListId() {
        return ShoppingListId;
    }

    public void setShoppingListId(Integer shoppingListId) {
        ShoppingListId = shoppingListId;
    }

    public String getShoppingListName() {
        return ShoppingListName;
    }

    public void setShoppingListName(String shoppingListName) {
        ShoppingListName = shoppingListName;
    }

    public UUID getSessionId() {
        return SessionId;
    }

    public void setSessionId(UUID sessionId) {
        SessionId = sessionId;
    }
}

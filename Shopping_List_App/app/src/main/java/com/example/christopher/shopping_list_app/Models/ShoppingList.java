package com.example.christopher.shopping_list_app.Models;

/**
 * Created by Christopher on 3/14/2016.
 */
public class ShoppingList {
    private String ShoppingListName;
    private String SessionId;
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

    public String getSessionId() {
        return SessionId;
    }

    public void setSessionId(String sessionId) {
        SessionId = sessionId;
    }
}

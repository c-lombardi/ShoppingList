package com.example.christopher.shopping_list.Models;

import java.util.UUID;

/**
 * Created by Christopher on 9/1/2015.
 */
public class Item implements Comparable<Item> {
    private int Id;
    private String Name;
    private com.example.christopher.shopping_list.Models.Store Store;
    private float BestPrice;
    private UUID SessionId;

    public float getBestPrice() {
        return BestPrice;
    }

    public void setBestPrice(final float bestPrice) {
        BestPrice = bestPrice;
    }

    public UUID getSessionId() {
        return SessionId;
    }

    public void setSessionId(final UUID sessionId) {
        SessionId = sessionId;
    }

    public int getId() {
        return Id;
    }

    public void setId(final int id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(final String name) {
        Name = name;
    }

    public Store getStore() {
        return Store;
    }

    public void setStore(final Store store) {
        Store = store;
    }

    @Override
    public boolean equals(final Object in) {
        boolean returnVal = false;
        if (in instanceof Item) {
            if (Id != 0 && ((Item) in).getId() != 0) {
                returnVal = (Id == ((Item) in).getId());
            } else if (Name != null && ((Item) in).getName() != null) {
                returnVal = Name.trim().equals(((Item) in).getName().trim());
            }
        }
        return returnVal;
    }

    @Override
    public int hashCode() {
        return Id;
    }

    @Override
    public int compareTo(final Item another) {
        return getName().toLowerCase().compareTo(another.getName().toLowerCase());
    }
}

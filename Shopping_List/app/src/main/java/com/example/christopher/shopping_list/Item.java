package com.example.christopher.shopping_list;

import java.sql.ResultSet;

/**
 * Created by Christopher on 9/1/2015.
 */
public class Item {
    public int getItemId() {
        return Id;
    }

    public void setItemId(int itemId) {
        Id = itemId;
    }

    //Getters and Setters
    private int Id;
    public String getName() throws Exception {
        return Name;
    }

    public void setName(String name) throws Exception {
        Name = name;
    }

    private String Name;

    public Store getStore() throws Exception {
        return Store;
    }

    public void setStore(Store store) throws Exception {
        Store = store;
    }

    private Store Store;

    public Boolean isListActive() throws Exception {
        return ListActive;
    }

    public void setListActive(Boolean listActive) throws Exception {
        ListActive = listActive;
    }

    private Boolean ListActive;

    public Boolean isLibraryActive() throws Exception {
        return LibraryActive;
    }

    public void setLibraryActive(Boolean libraryActive) throws Exception {
        LibraryActive = libraryActive;
    }

    private Boolean LibraryActive;

    public Float getBestPrice() throws Exception {
        return BestPrice;
    }

    public void setBestPrice(Float price) throws Exception {
        BestPrice = price;
    }

    private Float BestPrice;
    //End Getters and Setters

    //Constructors
    public Item(String name, Store store, Float price) throws Exception {
        Name = name;
        Store = store;
        BestPrice = price;
        ListActive = true;
        LibraryActive = true;
    }
    //End Constructors
}

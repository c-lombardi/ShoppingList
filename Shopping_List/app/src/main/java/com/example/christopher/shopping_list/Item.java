package com.example.christopher.shopping_list;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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

    public Item(int id) {
        Id = id;
        ListActive = true;
        LibraryActive = true;
    }
    public Item(String name) {
        Name = name;
        ListActive = true;
        LibraryActive = true;
    }
    /*public Item(String name, Float price) {
        Name = name;
        BestPrice = price;
        ListActive = true;
        LibraryActive = true;
    }
    public Item(String name, Store store, Float price) {
        Name = name;
        Store = store;
        BestPrice = price;
        ListActive = true;
        LibraryActive = true;
    }
    public Item(int Id, String name, Float price) {
        Name = name;
        BestPrice = price;
        ListActive = true;
        LibraryActive = true;
    }*/
    public Item(int id, String name, Store store, Float price) {
        Id = id;
        Name = name;
        Store = store;
        BestPrice = price;
        ListActive = true;
        LibraryActive = true;
    }
    //End Constructors

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(String.valueOf(Id).trim());
        sb.append(",");
        sb.append(Name.trim());
        sb.append(",");
        sb.append(String.valueOf(BestPrice).trim());
        sb.append(",");
        sb.append(String.valueOf(ListActive).trim());
        if(Store != null) {
            sb.append(",");
            sb.append(Store.toString());
        }
        return sb.toString();
    }


    public void fromString(String itemString){
        String [] partStrings = itemString.split(",");
        Id = Integer.parseInt(partStrings[0]);
        Name = partStrings[1];
        BestPrice = Float.parseFloat(partStrings[2]);
        ListActive = Boolean.parseBoolean(partStrings[3]);
        if (partStrings.length >= 6) {
            Store = new Store(partStrings[4], Integer.parseInt(partStrings[5]));
        } else if (partStrings.length >= 5) {
            Store = new Store(partStrings[4]);
        }

    }

    public Item() {}
    //End Constructors
}

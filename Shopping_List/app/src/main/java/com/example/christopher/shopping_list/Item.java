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

    private Boolean ListActive;

    public Float getBestPrice() throws Exception {
        return BestPrice;
    }

    public void setBestPrice(Float price) throws Exception {
        BestPrice = price;
    }

    private Float BestPrice;
    //End Getters and Setters

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

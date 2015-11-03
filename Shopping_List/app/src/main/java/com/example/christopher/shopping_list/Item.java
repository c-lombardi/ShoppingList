package com.example.christopher.shopping_list;

import java.sql.SQLException;
import java.util.UUID;

/**
 * Created by Christopher on 9/1/2015.
 */
public class Item implements Comparable<Item>{
    private final int Id;
    private final String Name;
    private final Store Store;
    private final boolean ListActive;
    private final boolean LibraryActive;
    private final float BestPrice;
    private final UUID SessionId;

    private Item(ItemBuilder itemBuilder) {
        Id = itemBuilder.Id;
        Name = itemBuilder.Name;
        Store = itemBuilder.Store;
        ListActive = itemBuilder.ListActive;
        LibraryActive = itemBuilder.LibraryActive;
        BestPrice = itemBuilder.BestPrice;
        SessionId = itemBuilder.SessionId;
    }

    public float getBestPrice() {
        return BestPrice;
    }

    public UUID getSessionId() {
        return SessionId;
    }

    public int getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }

    public Store getStore() {
        return Store;
    }

    @Override
    public boolean equals(Object in) {
        boolean returnVal = false;
        if(in instanceof Item){
            if(Id != 0 && ((Item)in).getId() != 0) {
                returnVal = (Id == ((Item) in).getId());
            } else if (Name != null && ((Item) in).getName() != null){
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
    public String toString(){
        final StringBuilder sb = new StringBuilder();
        sb.append(String.valueOf(Id).trim());
        sb.append(",");
        sb.append(Name.trim());
        sb.append(",");
        sb.append(SessionId.toString());
        sb.append(",");
        sb.append(String.valueOf(BestPrice).trim());
        sb.append(",");
        sb.append(String.valueOf(ListActive).trim());
        if(Store != null && Store.getName() != null) {
            sb.append(",");
            sb.append(Store.toString());
        }
        return sb.toString();
    }

    public static Item fromString(String itemString) throws SQLException, ClassNotFoundException {
        final String [] partStrings = itemString.split(",");
        final ItemBuilder ib = new ItemBuilder(Integer.parseInt(partStrings[0]), partStrings[1], UUID.fromString(partStrings[2])).bestPrice(Float.parseFloat(partStrings[3])).listActive(Boolean.parseBoolean(partStrings[4]));
        if (partStrings.length >= 7) {
            ib.store(new Store.StoreBuilder(partStrings[5], Integer.parseInt(partStrings[6])).build());
        } else if (partStrings.length >= 6) {
            ib.store(new Store.StoreBuilder(partStrings[5]).build());
        }
        return ib.build();
    }

    @Override
    public int compareTo(Item another) {
        return getName().toLowerCase().compareTo(another.getName().toLowerCase());
    }

    public static class ItemBuilder{
        private final int Id;
        private final String Name;
        private Store Store;
        private boolean ListActive = true;
        private boolean LibraryActive = true;
        private float BestPrice;
        private final UUID SessionId;

        public ItemBuilder(int id, String name, UUID sId){
            Name = name;
            Id = id;
            SessionId = sId;
        }

        public ItemBuilder(int id, UUID sId) {
            Name = null;
            Id = id;
            SessionId = sId;
        }

        public ItemBuilder bestPrice(float bestPrice){
            BestPrice = bestPrice;
            return this;
        }

        public ItemBuilder store (Store store){
            Store = store;
            return this;
        }

        public ItemBuilder listActive (boolean listActive){
            ListActive = listActive;
            return this;
        }

        public ItemBuilder libraryActive (boolean libraryActive){
            LibraryActive = libraryActive;
            return this;
        }

        public Item build() {
            return new Item(this);
        }
    }
}

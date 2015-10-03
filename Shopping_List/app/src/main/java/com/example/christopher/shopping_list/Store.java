package com.example.christopher.shopping_list;

/**
 * Created by Christopher on 9/1/2015.
 */
public class Store {
    public String getName() {
        return Name;
    }

    public Integer getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    private Integer Id;

    public void setStoreName(String storeName) {
        Name = storeName;
    }

    private String Name;

    public Store(String n, int id) {
        Name = n;
        Id = id;
    }

    public Store(String n) {
        Name = n;
    }

    public Store(int id) {
        Id = id;
    }

    public void fromString(String storeString) {
        String[] storeParts = storeString.split(",");
        Id = Integer.parseInt(storeParts[0]);
        Name = storeParts[1];
    }

    public Store(){}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Name.trim());
        if(Id != null && Id != 0) {
            sb.append(",");
            sb.append(String.valueOf(Id).trim());
        }
        return sb.toString();
    }
}

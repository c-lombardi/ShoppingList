package com.example.christopher.shopping_list;

/**
 * Created by Christopher on 9/1/2015.
 */
public class Store {
    private String Name;
    private int Id;

    private Store(StoreBuilder storeBuilder) {
        Name = storeBuilder.Name;
        Id = storeBuilder.Id;
    }

    public String getName() {
        return Name;
    }

    public int getId() {
        return Id;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(Name.trim());
        sb.append(",");
        sb.append(String.valueOf(Id).trim());
        return sb.toString();
    }

    public static Store fromString(String storeString) {
        final String[] storeParts = storeString.split(",");
        final StoreBuilder sb = new StoreBuilder(storeParts[1], Integer.parseInt(storeParts[0]));
        return sb.build();
    }

    public static class StoreBuilder {
        private final String Name;
        private final Integer Id;

        public StoreBuilder(String name, int id) {
            Name = name;
            Id = id;
        }

        public StoreBuilder(String name) {
            Name = name;
            Id = 0;
        }

        public Store build() {
            return new Store(this);
        }
    }
}

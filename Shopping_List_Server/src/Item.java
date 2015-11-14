import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christopher on 9/1/2015.
 */
public class Item {
    private final int Id;
    private final String Name;
    private final Store Store;
    private final boolean ListActive;
    private final boolean LibraryActive;
    private final float BestPrice;

    private Item(ItemBuilder itemBuilder) {
        Id = itemBuilder.Id;
        Name = itemBuilder.Name;
        Store = itemBuilder.Store;
        ListActive = itemBuilder.ListActive;
        LibraryActive = itemBuilder.LibraryActive;
        BestPrice = itemBuilder.BestPrice;
    }

    public float getBestPrice() {
        return BestPrice;
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
    public String toString(){
        final StringBuilder sb = new StringBuilder();
        sb.append(String.valueOf(Id).trim());
        sb.append(",");
        sb.append(Name.trim());
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
        final ItemBuilder ib = new ItemBuilder(Integer.parseInt(partStrings[0]), partStrings[1]).bestPrice(Float.parseFloat(partStrings[2])).listActive(Boolean.parseBoolean(partStrings[3]));
        if (partStrings.length >= 6) {
            ib.store(new Store.StoreBuilder(partStrings[4], Integer.parseInt(partStrings[5])).build());
        } else if (partStrings.length >= 5) {
            ib.store(new Store.StoreBuilder(partStrings[4]).build());
        }
        return ib.build();
    }

    public static class ItemBuilder implements CRUD<ItemBuilder> {
        private int Id;
        private String Name;
        private Store Store;
        private boolean ListActive;
        private boolean LibraryActive;
        private float BestPrice;

        public ItemBuilder(int id, String name){
            Name = name;
            Id = id;
        }

        public ItemBuilder(){
        }

        public ItemBuilder id(int id){
            Id = id;
            return this;
        }

        public ItemBuilder bestPrice(float bestPrice){
            BestPrice = bestPrice;
            return this;
        }

        public ItemBuilder store (Store store){
            Store = store;
            return this;
        }

        public ItemBuilder listActive (boolean listActive) {
            ListActive = listActive;
            return this;
        }

        public ItemBuilder libraryActive (boolean libraryActive) {
            LibraryActive = libraryActive;
            return this;
        }

        @Override
        public ItemBuilder create() {
            try (final Database db = new Database()) {
                if(Store != null) {
                    Store = new Store.StoreBuilder(Store.getName(), Store.getId()).create().build();
                }
                try (final PreparedStatement stmt = db.selectTableQuery(ItemQueries.addItem(this.build()))) {
                    try (final ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            Id = rs.getInt("ItemId");
                            ListActive = true;
                            LibraryActive = true;
                        }
                    }
                }
            } catch (Exception ex) {
                update(true);
                read();
            } finally {
                return this;
            }
        }

        @Override
        public ItemBuilder read() {
            try (final Database db = new Database()) {
                if(Id != 0) {
                    try(final PreparedStatement stmt = db.selectTableQuery(ItemQueries.getItemById(Id))) {
                        try (final ResultSet rs = stmt.executeQuery()) {
                            while (rs.next()) {
                                Name = rs.getString("ItemName");
                                BestPrice = rs.getFloat("BestPrice");
                                ListActive = rs.getBoolean("ListActive");
                                LibraryActive = rs.getBoolean("LibraryActive");
                                Store.StoreBuilder sb = new Store.StoreBuilder().Id(rs.getInt("StoreId"));
                                sb.read();
                                Store = sb.build();
                            }
                        }
                    }
                } else if(Name != null) {
                    try(final PreparedStatement stmt = db.selectTableQuery(ItemQueries.getItemByName(Name))) {
                        try (final ResultSet rs = stmt.executeQuery()) {
                            while (rs.next()) {
                                Id = rs.getInt("ItemId");
                                BestPrice = rs.getFloat("BestPrice");
                                ListActive = rs.getBoolean("ListActive");
                                LibraryActive = rs.getBoolean("LibraryActive");
                                Store.StoreBuilder sb = new Store.StoreBuilder().Id(rs.getInt("StoreId"));
                                sb.read();
                                Store = sb.build();
                            }
                        }
                    }
                }
            } catch (Exception ex) {

            } finally {
                return this;
            }
        }

        @Override
        public List<ItemBuilder> readAll(boolean fromLibrary) {
            final List<ItemBuilder> returnList = new ArrayList<>();
            try (final Database db = new Database()) {
                if(!fromLibrary) {
                    try(final PreparedStatement stmt = db.selectTableQuery(ItemQueries.getAllItemsFromList())) {
                        try(final ResultSet rs = stmt.executeQuery()) {
                            while (rs.next()) {
                                returnList.add(new ItemBuilder(rs.getInt("ItemId"), rs.getString("ItemName")).bestPrice(rs.getFloat("BestPrice")).store(new Store.StoreBuilder(rs.getString("StoreName"), rs.getInt("StoreId")).build()));
                            }
                        }
                    }
                } else {
                    try(final PreparedStatement stmt = db.selectTableQuery(ItemQueries.getAllItemsFromLibrary)) {
                        try(final ResultSet rs = stmt.executeQuery()) {
                            while (rs.next()) {
                                returnList.add(new ItemBuilder(rs.getInt("ItemId"), rs.getString("ItemName")));
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                System.out.println("Fail");
                System.out.println(ex);
            }finally {
                return returnList;
            }
        }

        public List<ItemBuilder> getLibraryItemsThatContain(String itemNameSearchString) {
            final List<ItemBuilder> returnList = new ArrayList<>();
            try (final Database db = new Database()) {
                try (final PreparedStatement stmt = db.selectTableQuery(ItemQueries.getLibraryItemsWithCharacters(itemNameSearchString))) {
                    try (final ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            returnList.add(new ItemBuilder(rs.getInt("ItemId"), rs.getString("ItemName")));
                        }
                    }
                }
            } catch (Exception ex) {
                System.out.println("Fail");
                System.out.println(ex);
            }finally {
                return returnList;
            }
        }

        @Override
        public ItemBuilder update(boolean justFlipListActive) {
            try (final Database db = new Database()) {
                if (Name != null) {
                    if(Store != null) {
                        Store = new Store.StoreBuilder(Store.getName(), Store.getId()).create().build();
                    }
                    if(!justFlipListActive) {
                        if (Id != 0) {
                            db.updateTableQuery(ItemQueries.updateItemById(this.build()));
                        } else {
                            db.updateTableQuery(ItemQueries.updateItemByName(this.build()));
                        }
                    } else {
                        if (Id != 0) {
                            db.updateTableQuery(ItemQueries.makeActiveById(this.build()));
                        } else {
                            db.updateTableQuery(ItemQueries.makeActiveByName(this.build()));
                        }
                    }
                }
            } catch (Exception ex) {
                create();
            } finally {
                return this;
            }
        }

        @Override
        public ItemBuilder delete(boolean deleteFromLibrary) {
            try (final Database db = new Database()) {
                if (Id != 0) {
                    if(!deleteFromLibrary) {
                        db.updateTableQuery(ItemQueries.removeItemFromList(Id));
                    } else {
                        db.updateTableQuery(ItemQueries.removeItemFromLibrary(Id));
                    }
                }
            } catch (Exception ex) {

            } finally {
                return this;
            }
        }

        public ItemBuilder attachStore(){
            try (final Database db = new Database()) {
                if(Store.getId() != 0) {
                    db.updateTableQuery(ItemQueries.addStoreToItem(Id, Store.getId()));
                }
            } catch(Exception ex) {

            }
            return this;
        }

        public List<ItemBuilder> reAdd(String [] itemIds) {
            final List<ItemBuilder> returnList = new ArrayList<>();
            try (final Database db = new Database()) {
                if(itemIds.length != 0) {
                    db.updateTableQuery(ItemQueries.reAddItemsByIds(itemIds));
                    try(final PreparedStatement stmt = db.selectTableQuery(ItemQueries.getItemsByIds(itemIds))) {
                        try(final ResultSet rs = stmt.executeQuery()) {
                            while (rs.next()) {
                                returnList.add(new ItemBuilder(rs.getInt("ItemId"), rs.getString("ItemName")).bestPrice(rs.getFloat("BestPrice")).store(new Store.StoreBuilder(rs.getString("StoreName"), rs.getInt("StoreId")).build()));
                            }
                        }
                    }
                }
            } catch (Exception ex) {

            }
            finally {
                return returnList;
            }
        }

        public void removeItems(String [] itemIds) {
            try (final Database db = new Database()) {
                if(itemIds.length != 0) {
                    db.updateTableQuery(ItemQueries.removeItemsByIds(itemIds));
                }
            } catch (Exception ex) {

            }
        }

        public Item build() {
            return new Item(this);
        }
    }
}

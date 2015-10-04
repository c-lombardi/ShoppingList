import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christopher on 9/1/2015.
 */
public class Item {
    private Integer Id;
    private String Name;
    private Store Store;
    private Boolean ListActive;
    private Boolean LibraryActive;
    private Float BestPrice;

    private Item(ItemBuilder itemBuilder) {
        Id = itemBuilder.Id;
        Name = itemBuilder.Name;
        Store = itemBuilder.Store;
        ListActive = itemBuilder.ListActive;
        LibraryActive = itemBuilder.LibraryActive;
        BestPrice = itemBuilder.BestPrice;
    }

    public Float getBestPrice() {
        return BestPrice;
    }

    public Integer getId() {
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
        if(Store != null && Store.getId() != 0)
        {
            sb.append(",");
            sb.append(Store.toString());
        }
        return sb.toString();
    }

    public static class ItemBuilder implements CRUD<ItemBuilder> {
        private Integer Id;
        private String Name;
        private Store Store;
        private Boolean ListActive;
        private Boolean LibraryActive;
        private Float BestPrice;

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

        public ItemBuilder bestPrice(Float bestPrice){
            BestPrice = bestPrice;
            return this;
        }

        public ItemBuilder store (Store store){
            Store = store;
            return this;
        }

        public ItemBuilder fromString(String itemString) throws SQLException, ClassNotFoundException {
            final String [] partStrings = itemString.split(",");
            Id = Integer.parseInt(partStrings[0]);
            Name = partStrings[1];
            BestPrice = Float.parseFloat(partStrings[2]);
            ListActive = Boolean.parseBoolean(partStrings[3]);
            if (partStrings.length >= 6) {
                Store = new Store.StoreBuilder(partStrings[4], Integer.parseInt(partStrings[5])).build();
            } else if (partStrings.length >= 5) {
                Store = new Store.StoreBuilder(partStrings[4]).build();
            }
            return this;
        }

        @Override
        public ItemBuilder create() {
            try (final database db = new database()) {
                if(Store != null) {
                    Store = new Store.StoreBuilder(Store.getName(), Store.getId()).create().build();
                }
                final ResultSet rs = db.selectTableQuery(itemQueries.addItem(this.build()));
                while (rs.next()) {
                    Id = rs.getInt("ItemId");
                    ListActive = true;
                    LibraryActive = true;
                }
            } catch (Exception ex) {
                if(Store != null || BestPrice != 0) {
                    update(false);
                } else {
                    update(true);
                }
                read();
            } finally {
                return this;
            }
        }

        @Override
        public ItemBuilder read() {
            try (final database db = new database()) {
                if(Id != null && Id != 0) {
                    ResultSet rs = db.selectTableQuery(itemQueries.getItemById(Id));
                    while (rs.next()) {
                        Name = rs.getString("ItemName");
                        BestPrice = rs.getFloat("BestPrice");
                        ListActive = rs.getBoolean("ListActive");
                        LibraryActive = rs.getBoolean("LibraryActive");
                        Store.StoreBuilder sb = new Store.StoreBuilder().Id(rs.getInt("StoreId"));
                        sb.read();
                        Store = sb.build();
                    }
                } else if(Name != null) {
                    final ResultSet rs = db.selectTableQuery(itemQueries.getItemByName(Name));
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
            } catch (Exception ex) {

            } finally {
                return this;
            }
        }

        @Override
        public List<ItemBuilder> readAll() {
            final List<ItemBuilder> returnList = new ArrayList<>();
            try (final database db = new database()) {
                final ResultSet rs = db.selectTableQuery(itemQueries.getAllItemsFromList());
                while(rs.next())
                {
                    returnList.add(new ItemBuilder(rs.getInt("ItemId"), rs.getString("ItemName")).bestPrice(rs.getFloat("BestPrice")).store(new Store.StoreBuilder(rs.getString("StoreName"), rs.getInt("StoreId")).build()));
                }
            } catch (Exception ex) {
                System.out.println("Fail");
            }finally {
                return returnList;
            }
        }

        @Override
        public ItemBuilder update(boolean justFlipListActive) {
            try (final database db = new database()) {
                if (Name != null) {
                    if(Store != null) {
                        Store = new Store.StoreBuilder(Store.getName(), Store.getId()).create().build();
                    }
                    if(!justFlipListActive) {
                        if (Id != null && Id != 0) {
                            db.updateTableQuery(itemQueries.updateItemById(this.build()));
                        } else {
                            db.updateTableQuery(itemQueries.updateItemByName(this.build()));
                        }
                    } else {
                        if (Id != null && Id != 0) {
                            db.updateTableQuery(itemQueries.makeActiveById(this.build()));
                        } else {
                            db.updateTableQuery(itemQueries.makeActiveByName(this.build()));
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
            try (final database db = new database()) {
                if (Id != null) {
                    if(!deleteFromLibrary) {
                        db.updateTableQuery(itemQueries.removeItemFromList(Id));
                    } else {
                        db.updateTableQuery(itemQueries.removeItemFromLibrary(Id));
                    }
                }
            } catch (Exception ex) {

            } finally {
                return this;
            }
        }

        public ItemBuilder attachStore(){
            try (final database db = new database()) {
                if(Store.getId() != null) {
                    db.updateTableQuery(itemQueries.addStoreToItem(Id, Store.getId()));
                }
            } catch(Exception ex) {

            }
            return this;
        }

        public Item build() {
            return new Item(this);
        }
    }
}

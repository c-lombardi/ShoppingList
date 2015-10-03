import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christopher on 9/1/2015.
 */
public class Item implements CRUD<Item> {
    private Integer Id;
    private String Name;
    private Store Store;
    private Boolean ListActive;
    private Boolean LibraryActive;
    private Float BestPrice;

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
    //Constructors
    public Item(int id) {
        Id = id;
        ListActive = true;
        LibraryActive = true;
    }
    public Item(int id, String name, Store store, Float price) {
        Id = id;
        Name = name;
        Store = store;
        BestPrice = price;
        ListActive = true;
        LibraryActive = true;
    }

    public void fromString(String itemString) throws SQLException, ClassNotFoundException {
        final String [] partStrings = itemString.split(",");
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

    @Override
    public void create() {
        try (final database db = new database()) {
            if(Store != null) {
                Store.create();
            }
            final ResultSet rs = db.selectTableQuery(itemQueries.addItem(this));
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
        }
    }

    @Override
    public void read() {
        try (final database db = new database()) {
            if(Id != null && Id != 0) {
                ResultSet rs = db.selectTableQuery(itemQueries.getItemById(Id));
                while (rs.next()) {
                    Name = rs.getString("ItemName");
                    BestPrice = rs.getFloat("BestPrice");
                    ListActive = rs.getBoolean("ListActive");
                    LibraryActive = rs.getBoolean("LibraryActive");
                    Store = new Store(rs.getInt("StoreId"));
                    Store.read();
                }
            } else if(Name != null) {
                final ResultSet rs = db.selectTableQuery(itemQueries.getItemByName(Name));
                while (rs.next()) {
                    Id = rs.getInt("ItemId");
                    BestPrice = rs.getFloat("BestPrice");
                    ListActive = rs.getBoolean("ListActive");
                    LibraryActive = rs.getBoolean("LibraryActive");
                    Store = new Store(rs.getInt("StoreId"));
                    Store.read();
                }
            }
        } catch (Exception ex) {

        }
    }

    @Override
    public List<Item> readAll() {
        final List<Item> returnList = new ArrayList<>();
        try (final database db = new database()) {
            final ResultSet rs = db.selectTableQuery(itemQueries.getAllItemsFromList());
            while(rs.next())
            {
                returnList.add(new Item(rs.getInt("ItemId"), rs.getString("ItemName"), new Store(rs.getString("StoreName"), rs.getInt("StoreId")), rs.getFloat("BestPrice")));
            }
        } catch (Exception ex) {
            System.out.println("Fail");
        }
        return returnList;
    }

    @Override
    public void update(boolean justFlipListActive) {
        try (final database db = new database()) {
            if (Name != null) {
                if(Store != null) {
                    Store.create();
                }
                if(!justFlipListActive) {
                    if (Id != null && Id != 0) {
                        db.updateTableQuery(itemQueries.updateItemById(this));
                    } else {
                        db.updateTableQuery(itemQueries.updateItemByName(this));
                    }
                } else {
                    if (Id != null && Id != 0) {
                        db.updateTableQuery(itemQueries.makeActiveById(this));
                    } else {
                        db.updateTableQuery(itemQueries.makeActiveByName(this));
                    }
                }
            }
        } catch (Exception ex) {
            create();
        }
    }

    @Override
    public void delete(boolean deleteFromLibrary) {
        try (final database db = new database()) {
            if (Id != null) {
                if(!deleteFromLibrary) {
                    db.updateTableQuery(itemQueries.removeItemFromList(Id));
                } else {
                    db.updateTableQuery(itemQueries.removeItemFromLibrary(Id));
                }
            }
        } catch (Exception ex) {

        }
    }

    public void attachStore(){
        try (final database db = new database()) {
            if(Store.getId() != null) {
                db.updateTableQuery(itemQueries.addStoreToItem(Id, Store.getId()));
            }
        } catch(Exception ex) {

        }
    }
    //End Constructors
}

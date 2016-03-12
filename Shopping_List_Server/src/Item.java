import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Christopher on 9/1/2015.
 */
public class Item implements CRUD<Item> {
    private int Id;
    private String Name;
    private Store Store;
    private float BestPrice;
    private UUID SessionId;

    public Item() {
    }

    private Item(final int id, final String n, final Store s, final float bp, final UUID sId) {
        setId(id);
        setName(n);
        setSessionId(sId);
        setStore(s);
        setBestPrice(bp);
    }

    private Item(final int id, final String n, final UUID sId) {
        setId(id);
        setName(n);
        setSessionId(sId);
    }

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
    public Item create() {
        try (final Database db = new Database()) {
            if (Store != null) {
                Store = Store.create();
            }
            try (final PreparedStatement stmt = db.selectTableQuery(ItemQueries.addItem(this))) {
                try (final ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Id = rs.getInt("ItemId");
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
    public Item read() {
        try (final Database db = new Database()) {
            if (Id != 0) {
                try (final PreparedStatement stmt = db.selectTableQuery(ItemQueries.getItemById(Id))) {
                    try (final ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            Name = rs.getString("ItemName");
                            SessionId = UUID.fromString(rs.getString("SessionId"));
                            BestPrice = rs.getFloat("BestPrice");
                            Store = new Store();
                            Store.setId(rs.getInt("StoreId"));
                            Store = Store.read();
                        }
                    }
                }
            } else if (Name != null) {
                try (final PreparedStatement stmt = db.selectTableQuery(ItemQueries.getItemByName(Name))) {
                    try (final ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            Id = rs.getInt("ItemId");
                            SessionId = UUID.fromString(rs.getString("SessionId"));
                            BestPrice = rs.getFloat("BestPrice");
                            Store = new Store();
                            Store.setId(rs.getInt("StoreId"));
                            Store = Store.read();
                        }
                    }
                }
            }
        } catch (Exception ex) {

        } finally {
            return this;
        }
    }

    public List<Item> readAll(final boolean fromLibrary, UUID sId) {
        final List<Item> returnList = new ArrayList<>();
        try (final Database db = new Database()) {
            if (!fromLibrary) {
                try (final PreparedStatement stmt = db.selectTableQuery(ItemQueries.getAllItemsFromListBySessionId(sId))) {
                    try (final ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            Store store = new Store();
                            store.setId(rs.getInt("StoreId"));
                            store.setName(rs.getString("StoreName"));
                            returnList.add(new Item(rs.getInt("ItemId"), rs.getString("ItemName"), store, rs.getFloat("BestPrice"), UUID.fromString(rs.getString("SessionId"))));
                        }
                    }
                }
            } else {
                try (final PreparedStatement stmt = db.selectTableQuery(ItemQueries.getAllItemsFromLibrary)) {
                    try (final ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            returnList.add(new Item(rs.getInt("ItemId"), rs.getString("ItemName"), UUID.fromString(rs.getString("SessionId"))));
                        }
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("Fail");
            System.out.println(ex);
        } finally {
            return returnList;
        }
    }

    public List<Item> getLibraryItemsThatContain(final String itemNameSearchString) {
        final List<Item> returnList = new ArrayList<>();
        try (final Database db = new Database()) {
            try (final PreparedStatement stmt = db.selectTableQuery(ItemQueries.getLibraryItemsWithCharactersAndSessionId(itemNameSearchString, SessionId))) {
                try (final ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Item foundItem = new Item();
                        foundItem.setId(rs.getInt("ItemId"));
                        foundItem.setName(rs.getString("ItemName"));
                        returnList.add(foundItem);
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("Fail");
            System.out.println(ex);
        } finally {
            return returnList;
        }
    }

    @Override
    public Item update(final boolean justFlipListActive) {
        try (final Database db = new Database()) {
            if (Name != null) {
                if (Store != null) {
                    Store = Store.create();
                }
                if (!justFlipListActive) {
                    if (Id != 0) {
                        db.updateTableQuery(ItemQueries.updateItemById(this));
                    } else {
                        db.updateTableQuery(ItemQueries.updateItemByName(this));
                    }
                } else {
                    if (Id != 0) {
                        db.updateTableQuery(ItemQueries.makeActiveById(this));
                    } else {
                        db.updateTableQuery(ItemQueries.makeActiveByName(this));
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
    public Item delete(final boolean deleteFromLibrary) {
        try (final Database db = new Database()) {
            if (Id != 0) {
                if (!deleteFromLibrary) {
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

    public List<Item> reAdd(final List<Integer> itemIds, final UUID sId) {
        final List<Item> returnList = new ArrayList<>();
        try (final Database db = new Database()) {
            db.updateTableQuery(ItemQueries.reAddItemsByIds(itemIds));
            try (final PreparedStatement stmt = db.selectTableQuery(ItemQueries.getItemsByIds(itemIds, sId))) {
                try (final ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Store store = new Store();
                        store.setId(rs.getInt("StoreId"));
                        store.setName(rs.getString("StoreName"));
                        returnList.add(new Item(rs.getInt("ItemId"), rs.getString("ItemName"), store, rs.getFloat("BestPrice"), UUID.fromString(rs.getString("SessionId"))));
                    }
                }
            }
        } catch (Exception ex) {

        } finally {
            return returnList;
        }
    }

    public void removeItems(final List<Integer> itemIds) {
        try (final Database db = new Database()) {
            db.updateTableQuery(ItemQueries.removeItemsByIds(itemIds));
        } catch (Exception ex) {

        }
    }
}



import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christopher on 9/1/2015.
 */
public class Item implements CRUD<Item> {
    private int Id;
    private String Name;
    private Store Store;
    private float BestPrice;
    private String SessionId;
    private int ShoppingListId;
    private ItemStatus ItemStatus;

    public Item() {
    }

    private Item(final int id, final String n, final Store s, final float bp, final String sId, final int slId, final ItemStatus itemStatus) {
        setId(id);
        setName(n);
        setSessionId(sId);
        setStore(s);
        setBestPrice(bp);
        setShoppingListId(slId);
        setItemStatus(itemStatus);
    }

    private Item(final int id, final String n, final String sId, final int slId, final ItemStatus itemStatus) {
        setId(id);
        setName(n);
        setSessionId(sId);
        setShoppingListId(slId);
        setItemStatus(itemStatus);
    }

    public int getShoppingListId() {return ShoppingListId; }

    public float getBestPrice() {
        return BestPrice;
    }

    public void setBestPrice(final float bestPrice) {
        BestPrice = bestPrice;
    }

    public String getSessionId() {
        return SessionId;
    }

    public void setSessionId(final String sessionId) {
        SessionId = sessionId;
    }

    public void setShoppingListId(final int slId) { ShoppingListId = slId; }

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

    public void setItemStatus(final ItemStatus itemStatus) {
        ItemStatus = itemStatus;
    }

    public ItemStatus getItemStatus() {
        return ItemStatus;
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
                        ItemStatus = ItemStatus.valueOf(rs.getString("ItemStatus"));
                    }
                }
            }
            update(true);
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
                            SessionId = rs.getString("SessionId");
                            BestPrice = rs.getFloat("BestPrice");
                            ShoppingListId = rs.getInt("ShoppingListId");
                            ItemStatus = ItemStatus.valueOf(rs.getString("ItemStatus"));
                            Store = new Store();
                            Store.setId(rs.getInt("StoreId"));
                            Store.setName(rs.getString("StoreName"));
                        }
                    }
                }
            }
        } catch (Exception ex) {

        } finally {
            return this;
        }
    }

    public List<Item> readAll(final boolean fromLibrary, final String sId, final int slId) {
        final List<Item> returnList = new ArrayList<>();
        try (final Database db = new Database()) {
            if (!fromLibrary) {
                try (final PreparedStatement stmt = db.selectTableQuery(ItemQueries.getAllItemsFromListByShoppingListId(slId))) {
                    try (final ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            Store store = new Store();
                            store.setId(rs.getInt("StoreId"));
                            store.setName(rs.getString("StoreName"));
                            returnList.add(new Item(rs.getInt("ItemId"), rs.getString("ItemName"), store, rs.getFloat("BestPrice"), sId, slId, ItemStatus.valueOf(rs.getString("ItemStatus"))));
                        }
                    }
                }
            } else {
                try (final PreparedStatement stmt = db.selectTableQuery(ItemQueries.getAllItemsFromLibrary(sId, slId))) {
                    try (final ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            returnList.add(new Item(rs.getInt("ItemId"), rs.getString("ItemName"), sId, slId, ItemStatus.Default));
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        } finally {
            return returnList;
        }
    }

    public List<Item> getLibraryItemsThatContain(final String itemNameSearchString, final int slId) {
        final List<Item> returnList = new ArrayList<>();
        try (final Database db = new Database()) {
            try (final PreparedStatement stmt = db.selectTableQuery(ItemQueries.getLibraryItemsWithCharactersAndSessionId(itemNameSearchString, getSessionId(), slId))) {
                try (final ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Item foundItem = new Item();
                        foundItem.setId(rs.getInt("ItemId"));
                        foundItem.setName(rs.getString("ItemName"));
                        foundItem.setItemStatus(ItemStatus.Default);
                        returnList.add(foundItem);
                    }
                }
            }
        } catch (Exception ignored) {
        } finally {
            return returnList;
        }
    }

    @Override
    public Item update(final boolean justFlipListActive) {
        if (Name != null) {
            if (Store != null) {
                Store = Store.create();
            }
            try (final Database db = new Database()) {
                    if (!justFlipListActive) {
                        db.updateTableQuery(ItemQueries.updateItemById(this));
                    } else {
                        db.updateTableQuery(ItemQueries.addItemToShoppingList(getId(), getShoppingListId()));
                    }
            } catch (Exception ex) {
                create();
            } finally {
                return this;
            }
        }
        return this;
    }

    @Override
    public Item delete(final boolean deleteFromLibrary) {
        try (final Database db = new Database()) {
            if (Id != 0) {
                if (!deleteFromLibrary) {
                    db.updateTableQuery(ItemQueries.removeItemFromList(Id, getShoppingListId()));
                } else {
                    db.updateTableQuery(ItemQueries.addItemToShoppingList(Id, getShoppingListId()));
                }
            }
        } catch (Exception ex) {

        } finally {
            return this;
        }
    }

    public List<Item> reAdd(final List<Item> items, final int slId) {
        final List<Item> returnList = new ArrayList<>();
        try (final Database db = new Database()) {
            db.updateTableQuery(ItemQueries.reAddItemsByIds(items, slId));
            try (final PreparedStatement stmt = db.selectTableQuery(ItemQueries.getItemsByIds(items, slId))) {
                try (final ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        final Store store = new Store();
                        store.setId(rs.getInt("StoreId"));
                        store.setName(rs.getString("StoreName"));
                        returnList.add(new Item(rs.getInt("ItemId"), rs.getString("ItemName"), store, rs.getFloat("BestPrice"), rs.getString("SessionId"), rs.getInt("ShoppingListId"), ItemStatus.valueOf(rs.getString("ItemStatus"))));
                    }
                }
            }
        } catch (Exception ignored) {
        } finally {
            return returnList;
        }
    }

    public void removeItems(final List<Item> items, final int slId) {
        try (final Database db = new Database()) {
            db.updateTableQuery(ItemQueries.removeItemsByIds(items, slId));
        } catch (Exception ignored) {
        }
    }

    public void updateItemStatus() {
        try (final Database db = new Database()) {
            db.updateTableQuery(ItemQueries.changeItemStatusByItemId(this));
        } catch (Exception ignored) {
        }
    }
}



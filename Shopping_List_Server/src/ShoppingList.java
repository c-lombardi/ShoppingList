import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Christopher on 3/14/2016.
 */
public class ShoppingList implements CRUD<ShoppingList> {
    private String ShoppingListName;
    private String SessionId;
    private Integer ShoppingListId;

    public Integer getShoppingListId() {
        return ShoppingListId;
    }

    public void setShoppingListId(Integer shoppingListId) {
        ShoppingListId = shoppingListId;
    }

    public String getShoppingListName() {
        return ShoppingListName;
    }

    public void setShoppingListName(String shoppingListName) {
        ShoppingListName = shoppingListName;
    }

    public String getSessionId() {
        return SessionId;
    }

    public void setSessionId(UUID sessionId) {
        SessionId = sessionId.toString();
    }

    @Override
    public ShoppingList create() {
        try (final Database db = new Database()) {
            try (final PreparedSelectStatement stmt = db.selectTableQuery(ShoppingListQueries.CREATE_SHOPPING_LIST_BY_NAME_AND_SESSION_ID())) {
                stmt.setString(1, ShoppingListName);
                stmt.setObject(2, UUID.fromString(SessionId));
                try (final ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        ShoppingListId = rs.getInt("ShoppingListId");
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
    public ShoppingList read() {
        try (final Database db = new Database()) {
            if (ShoppingListId != 0) {
                try (final PreparedSelectStatement stmt = db.selectTableQuery(ShoppingListQueries.GET_SHOPPING_LIST_BY_SHOPPING_LIST_ID())) {
                    stmt.setInt(1, ShoppingListId);
                    try (final ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            ShoppingListName = rs.getString("ShoppingListName");
                            SessionId = rs.getString("SessionId");
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        } finally {
            return this;
        }
    }

    public List<ShoppingList> readAll(final String sId) {
        final List<ShoppingList> returnList = new ArrayList<>();
        try (final Database db = new Database()) {
            try (final PreparedSelectStatement stmt = db.selectTableQuery(ShoppingListQueries.GET_SHOPPING_LISTS_BY_SESSION_ID())) {
                stmt.setObject(1, sId);
                try (final ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        ShoppingList shopping_List = new ShoppingList();
                        shopping_List.setShoppingListId(rs.getInt("ShoppingListId"));
                        shopping_List.setSessionId(UUID.fromString(rs.getString("SessionId")));
                        shopping_List.setShoppingListName(rs.getString("ShoppingListName"));
                        returnList.add(shopping_List);
                    }
                }
            }
        } catch (final Exception ignored) {
        } finally {
            return returnList;
        }
    }

    @Override
    public ShoppingList update(boolean justFlipListActive) {
        try (final Database db = new Database()) {
            if (ShoppingListName != null) {
                try (final PreparedUpdateStatement stmt = db.updateTableQuery(ShoppingListQueries.RENAME_SHOPPING_LIST_BY_SHOPPING_LIST_ID())){
                    stmt.setInt(1, ShoppingListId);
                    stmt.setString(2, ShoppingListName);
                    stmt.executeUpdate();
                }
            }
        } catch (Exception ignored) {
        } finally {
            return this;
        }
    }

    @Override
    public ShoppingList delete(boolean deleteFromLibrary) {
        try (final Database db = new Database()) {
            if (ShoppingListId != null) {
                try (final PreparedUpdateStatement stmt = db.updateTableQuery(ItemQueries.removeItemsFromList())) {
                    stmt.setInt(1, ShoppingListId);
                    stmt.executeUpdate();
                }
                try (final PreparedUpdateStatement stmt = db.updateTableQuery(ShoppingListQueries.REMOVE_SHOPPING_LIST_BY_SHOPPING_LIST_ID())) {
                    stmt.setInt(1, ShoppingListId);
                    stmt.executeUpdate();
                }
            }
        } catch (Exception ignored) {
        } finally {
            return this;
        }
    }
}

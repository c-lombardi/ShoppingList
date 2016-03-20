import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Christopher on 3/14/2016.
 */
public class Shopping_List implements CRUD<Shopping_List> {
    private String ShoppingListName;
    private UUID SessionId;
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

    public UUID getSessionId() {
        return SessionId;
    }

    public void setSessionId(UUID sessionId) {
        SessionId = sessionId;
    }

    @Override
    public Shopping_List create() {
        try (final Database db = new Database()) {
            try (final PreparedStatement stmt = db.selectTableQuery(Shopping_ListQueries.CREATE_SHOPPING_LIST_BY_NAME_AND_SESSION_ID(ShoppingListName, SessionId))) {
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
    public Shopping_List read() {
        try (final Database db = new Database()) {
            if (ShoppingListId != 0) {
                try (final PreparedStatement stmt = db.selectTableQuery(Shopping_ListQueries.GET_SHOPPING_LIST_BY_SHOPPING_LIST_ID(ShoppingListId))) {
                    try (final ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            ShoppingListName = rs.getString("ShoppingListName");
                            SessionId = UUID.fromString(rs.getString("SessionId"));
                        }
                    }
                }
            }
        } catch (Exception ex) {

        } finally {
            return this;
        }
    }

    public List<Shopping_List> readAll(final UUID sId) {
        final List<Shopping_List> returnList = new ArrayList<>();
        try (final Database db = new Database()) {
            try (final PreparedStatement stmt = db.selectTableQuery(Shopping_ListQueries.GET_SHOPPING_LISTS_BY_SESSION_ID(sId))) {
                try (final ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Shopping_List shopping_List = new Shopping_List();
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
    public Shopping_List update(boolean justFlipListActive) {
        try (final Database db = new Database()) {
            if (ShoppingListName != null) {
                db.updateTableQuery(Shopping_ListQueries.RENAME_SHOPPING_LIST_BY_SHOPPING_LIST_ID(ShoppingListId, ShoppingListName));
            }
        } catch (Exception ex) {
            create();
        } finally {
            return this;
        }
    }

    @Override
    public Shopping_List delete(boolean deleteFromLibrary) {
        throw new NotImplementedException();
    }
}

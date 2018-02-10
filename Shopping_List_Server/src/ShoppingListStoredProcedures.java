/**
 * Created by Christopher on 7/30/2016.
 */
public class ShoppingListStoredProcedures {
    public static String[] StoredProcedures = {
            String.format("CREATE FUNCTION createShoppingList(shoppingListName VARCHAR(50), sessionId UUID)\n" +
                    "RETURNS TABLE (%s)\n" +
                    "AS\n" +
                    "$$\n" +
                    "\n" +
                    "BEGIN\n" +
                    "RETURN QUERY\n" +
                    "INSERT INTO ShoppingLists (ShoppingListId, ShoppingListName, SessionId)\n" +
                    "VALUES (nextval('Shopping_List_Seq'), shoppingListName, sessionId)\n" +
                    "RETURNING ShoppingListId;\n" +
                    "END\n" +
                    "$$ LANGUAGE plpgsql", Database.ShoppingListIdAndType),
            String.format("CREATE FUNCTION updateShoppingListNameByShoppingListId(shoppingListId INT, shoppingListName VARCHAR(50))\n" +
                    "RETURNS VOID\n" +
                    "AS\n" +
                    "$$\n" +
                    "\n" +
                    "BEGIN\n" +
                    "UPDATE ShoppingLists\n" +
                    "SET ShoppingListName = shoppingListName\n" +
                    "WHERE ShoppingListId = shoppingListId;\n" +
                    "END\n" +
                    "$$ LANGUAGE plpgsql"),
            String.format("CREATE FUNCTION getShoppingListByShoppingListId(shoppingListId INT)\n" +
                    "RETURNS TABLE(%s, %s, %s)\n" +
                    "AS\n" +
                    "$$\n" +
                    "\n" +
                    "BEGIN\n" +
                    "RETURN QUERY\n" +
                    "SELECT ShoppingListId, ShoppingListName, SessionId\n" +
                    "FROM ShoppingLists\n" +
                    "WHERE ShoppingListId = shoppingListId;\n" +
                    "END\n" +
                    "$$ LANGUAGE plpgsql", Database.ShoppingListIdAndType, Database.ShoppingListNameAndType, Database.SessionIdAndType),
            String.format("CREATE FUNCTION getShoppingListsBySessionId(sessionId UUID)\n" +
                    "RETURNS TABLE(%s, %s, %s)\n" +
                    "AS\n" +
                    "$$\n" +
                    "\n" +
                    "BEGIN\n" +
                    "RETURN QUERY\n" +
                    "SELECT ShoppingListId, ShoppingListName, SessionId\n" +
                    "FROM ShoppingLists\n" +
                    "WHERE SessionId = sessionId;\n" +
                    "END\n" +
                    "$$ LANGUAGE plpgsql", Database.ShoppingListIdAndType, Database.ShoppingListNameAndType, Database.SessionIdAndType),
            String.format("CREATE FUNCTION deleteShoppingListByShoppingListId(shoppingListId INT)\n" +
                    "RETURNS VOID\n" +
                    "AS\n" +
                    "$$\n" +
                    "\n" +
                    "BEGIN\n" +
                    "DELETE\n" +
                    "FROM ShoppingLists\n" +
                    "WHERE ShoppingListId = shoppingListId;\n" +
                    "END\n" +
                    "$$ LANGUAGE plpgsql")
    };
}

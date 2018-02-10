/**
 * Created by Christopher on 6/12/2016.
 */

public class ItemStoredProcedures {
        public static String[] StoredProcedures = {
                String.format("CREATE FUNCTION getAllItemsFromLibrary(sId uuid, slId INT)\n" +
                        "RETURNS TABLE (%s,%s)\n" +
                        "AS\n" +
                        "$$\n" +
                        "\n" +
                        "BEGIN\n" +
                        "RETURN QUERY\n" +
                        "SELECT i.ItemId, i.ItemName\n" +
                        "FROM Items i\n" +
                        "WHERE i.SessionId = sId\n" +
                        "AND i.ItemId NOT IN (SELECT sli.ItemId FROM ShoppingListItems sli WHERE ShoppingListId = slId);\n" +
                        "END\n" +
                        "$$ LANGUAGE plpgsql", Database.ItemIdAndType, Database.ItemNameAndType),

                String.format("CREATE FUNCTION getItemById(sId INT)\n" +
                        "RETURNS TABLE (%s,%s,%s,%s,%s,%s,%s,%s)\n" +
                        "AS\n" +
                        "$$\n" +
                        "\n" +
                        "BEGIN\n" +
                        "RETURN QUERY\n" +
                        "SELECT i.ItemId, i.ItemName, i.BestPrice, i.StoreId, i.SessionId, s.StoreName, sli.ItemStatus, sli.ShoppingListId\n" +
                        "FROM Items i\n" +
                        "LEFT JOIN ShoppingListItems sli ON sli.ItemId = i.ItemId\n" +
                        "LEFT JOIN Stores s ON s.StoreId = i.StoreId\n" +
                        "WHERE i.ItemId = sId;\n" +
                        "END\n" +
                        "$$ LANGUAGE plpgsql\n", Database.ItemIdAndType, Database.ItemNameAndType, Database.BestPriceAndType, Database.StoreIdAndType, Database.SessionIdAndType, Database.StoreNameAndType, Database.ItemStatusAndType, Database.ShoppingListIdAndType),

                String.format("CREATE FUNCTION addItemWithStore(iName VARCHAR(50), bp REAL, sName VARCHAR(50), sessId UUID)\n" +
                        "RETURNS TABLE (%s,%s,%s,%s)\n" +
                        "AS\n" +
                        "$$\n" +
                        "\n" +
                        "BEGIN\n" +
                        "RETURN QUERY\n" +
                        "INSERT INTO Items (ItemId, ItemName, BestPrice, StoreId, SessionId)\n" +
                        "VALUES (nextval('Item_Seq'), iName, bp, (SELECT StoreId FROM Stores WHERE StoreName = sName), sessId)\n" +
                        "RETURNING ItemId, ItemName, BestPrice, StoreId;\n" +
                        "END\n" +
                        "$$ LANGUAGE plpgsql\n", Database.ItemIdAndType, Database.ItemNameAndType, Database.BestPriceAndType, Database.StoreIdAndType),

                String.format("CREATE FUNCTION addItem(iName VARCHAR(50), bp REAL, sessId UUID)\n" +
                        "RETURNS TABLE (%s,%s,%s)\n" +
                        "AS\n" +
                        "$$\n" +
                        "\n" +
                        "BEGIN\n" +
                        "RETURN QUERY\n" +
                        "INSERT INTO Items (ItemId, ItemName, BestPrice, SessionId)\n" +
                        "VALUES (nextval('Item_Seq'), iName, bp, sessId)\n" +
                        "RETURNING ItemId, ItemName, BestPrice;\n" +
                        "END\n" +
                        "$$ LANGUAGE plpgsql\n", Database.ItemIdAndType, Database.ItemNameAndType, Database.BestPriceAndType),

                "CREATE FUNCTION removeItemFromList(iId INT, slId INT)\n" +
                        "RETURNS VOID\n" +
                        "AS\n" +
                        "$$\n" +
                        "\n" +
                        "BEGIN\n" +
                        "DELETE FROM ShoppingListItems\n" +
                        "WHERE ItemId = iId AND ShoppingListId = slId;\n" +
                        "END\n" +
                        "$$ LANGUAGE plpgsql\n",

                "CREATE FUNCTION removeItemsFromList(slId INT)\n" +
                        "RETURNS VOID\n" +
                        "AS\n" +
                        "$$\n" +
                        "\n" +
                        "BEGIN\n" +
                        "DELETE FROM ShoppingListItems\n" +
                        "WHERE ShoppingListId = slId;\n" +
                        "END\n" +
                        "$$ LANGUAGE plpgsql\n",

                "CREATE FUNCTION addItemToShoppingList(iId INT, slId INT)\n" +
                        "RETURNS VOID\n" +
                        "AS\n" +
                        "$$\n" +
                        "\n" +
                        "BEGIN\n" +
                        "INSERT INTO ShoppingListItems (ItemId, ShoppingListId)\n" +
                        "VALUES (iId, slId);\n" +
                        "END\n" +
                        "$$ LANGUAGE plpgsql\n",

                "CREATE FUNCTION updateItemByIdWithStore(iId INT, iName VARCHAR(50), bp REAL, sId INT)\n" +
                        "RETURNS VOID\n" +
                        "AS\n" +
                        "$$\n" +
                        "\n" +
                        "BEGIN\n" +
                        "UPDATE Items\n" +
                        "SET (ItemName, BestPrice, StoreId) = (iName, bp, sId)\n" +
                        "WHERE ItemId = iId;\n" +
                        "END\n" +
                        "$$ LANGUAGE plpgsql\n",

                "CREATE FUNCTION updateItemById(iId INT, iName VARCHAR(50), bp REAL)\n" +
                        "RETURNS VOID\n" +
                        "AS\n" +
                        "$$\n" +
                        "\n" +
                        "BEGIN\n" +
                        "UPDATE Items\n" +
                        "SET (ItemName, BestPrice) = (iName, bp)\n" +
                        "WHERE ItemId = iId;\n" +
                        "END\n" +
                        "$$ LANGUAGE plpgsql\n",

                String.format("CREATE FUNCTION selectAllItemsFromShoppingList(slId INT)\n" +
                        "RETURNS TABLE(%s,%s,%s,%s,%s,%s,%s,%s)\n" +
                        "AS\n" +
                        "$$\n" +
                        "\n" +
                        "BEGIN\n" +
                        "SELECT i.ItemId, i.ItemName, i.SessionId, i.BestPrice, s.StoreId, s.StoreName, sli.ShoppingListId, sli.ItemStatus\n" +
                        "FROM Items i\n" +
                        "LEFT JOIN Stores s ON i.StoreId = s.StoreId\n" +
                        "INNER JOIN ShoppingListItems sli ON sli.ItemId = i.ItemId AND sli.ShoppingListId = slId;\n" +
                        "END\n" +
                        "$$ LANGUAGE plpgsql\n", Database.ItemIdAndType, Database.ItemNameAndType, Database.SessionIdAndType, Database.BestPriceAndType, Database.StoreIdAndType, Database.StoreNameAndType, Database.ShoppingListIdAndType, Database.ItemStatusAndType),

                "CREATE FUNCTION reAddItemsById(iIds INT[], slId INT)\n" +
                        "RETURNS VOID\n" +
                        "AS\n" +
                        "$$\n" +
                        "\n" +
                        "BEGIN\n" +
                        "INSERT INTO ShoppingListItems (ItemId, ShoppingListId)\n" +
                        "SELECT ItemId, slId FROM Items WHERE ItemId IN iIds;\n" +
                        "END\n" +
                        "$$ LANGUAGE plpgsql\n",

                "CREATE FUNCTION updateItemStatusById(iId INT, itemStatus VARCHAR(8))\n" +
                        "RETURNS VOID\n" +
                        "AS\n" +
                        "$$\n" +
                        "\n" +
                        "BEGIN\n" +
                        "UPDATE ShoppingListItems\n" +
                        "SET ItemStatus = itemStatus WHERE ItemId = iId;\n" +
                        "END\n" +
                        "$$ LANGUAGE plpgsql\n",

                "CREATE FUNCTION removeItemsByIds(iIds INT[])\n" +
                        "RETURNS VOID\n" +
                        "AS\n" +
                        "$$\n" +
                        "\n" +
                        "BEGIN\n" +
                        "DELETE FROM ShoppingListItems WHERE ItemIds IN iIds;\n" +
                        "END\n" +
                        "$$ LANGUAGE plpgsql\n",

                String.format("CREATE FUNCTION getItemsByIds(iIds INT[], slId INT)\n" +
                        "RETURNS TABLE(%s,%s,%s,%s,%s,%s,%s,%s)\n" +
                        "AS\n" +
                        "$$\n" +
                        "\n" +
                        "BEGIN\n" +
                        "SELECT i.ItemId, i.ItemName, i.SessionId, i.BestPrice, s.StoreId, s.StoreName, sli.ShoppingListId, sli.ItemStatus \" +\n" +
                        "FROM Items i\n" +
                        "LEFT JOIN Stores s ON i.StoreId = s.StoreId\n" +
                        "INNER JOIN ShoppingListItems sli ON sli.ItemId = i.ItemId AND sli.ShoppingListId = slId\n" +
                        "WHERE i.ItemId IN iIds;\n" +
                        "END\n" +
                        "$$ LANGUAGE plpgsql\n",Database.ItemIdAndType, Database.ItemNameAndType, Database.SessionIdAndType, Database.BestPriceAndType, Database.StoreIdAndType, Database.StoreNameAndType, Database.ShoppingListIdAndType, Database.ItemStatusAndType),

                String.format("CREATE FUNCTION getLibraryItemsWithCharactersAndSessionId(slId INT, sessId UUID, itemNameLike TEXT)\n" +
                        "RETURNS TABLE(%s, %s, %s, %s, %s)\n" +
                        "AS\n" +
                        "$$\n" +
                        "\n" +
                        "BEGIN\n" +
                        "SELECT i.ItemId, i.ItemName, i.SessionId, i.BestPrice, i.StoreId\n" +
                        "WHERE i.SessionId = sessId\n" +
                        "AND lower(i.ItemName) LIKE '%%itemnameLike%%'\n" +
                        "AND i.Itemid NOT IN (SELECT Itemid FROM ShoppingListItems WHERE ShoppingListId = slId);\n" +
                        "END\n" +
                        "$$ LANGUAGE plpgsql\n", Database.ItemIdAndType, Database.ItemNameAndType, Database.SessionIdAndType, Database.BestPriceAndType, Database.StoreIdAndType)
        };


}

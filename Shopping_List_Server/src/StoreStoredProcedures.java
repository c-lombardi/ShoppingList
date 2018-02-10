/**
 * Created by Christopher on 7/30/2016.
 */
public class StoreStoredProcedures {
    public static String[] StoredProcedures = {
            String.format("CREATE FUNCTION createStore(storeName VARCHAR(50))\n" +
                    "RETURNS TABLE (%s)\n" +
                    "AS\n" +
                    "$$\n" +
                    "\n" +
                    "BEGIN\n" +
                    "RETURN QUERY\n" +
                    "INSERT INTO Stores (StoreId, StoreName)\n" +
                    "VALUES (nextval('Store_Seq'), storeName)\n" +
                    "RETURNING StoreId;\n" +
                    "END\n" +
                    "$$ LANGUAGE plpgsql", Database.StoreIdAndType),
            String.format("CREATE FUNCTION updateStoreById(storeId INT, storeName VARCHAR(50))\n" +
                    "RETURNS VOID\n" +
                    "AS\n" +
                    "$$\n" +
                    "\n" +
                    "BEGIN\n" +
                    "UPDATE Stores\n" +
                    "SET StoreName = storeName\n" +
                    "WHERE StoreId = storeId;\n" +
                    "END\n" +
                    "$$ LANGUAGE plpgsql"),
            String.format("CREATE FUNCTION getStoreById(sId INT)\n" +
                    "RETURNS TABLE (%s, %s)\n" +
                    "AS\n" +
                    "$$\n" +
                    "\n" +
                    "BEGIN\n" +
                    "RETURN QUERY\n" +
                    "SELECT StoreId, StoreName\n" +
                    "FROM Stores\n" +
                    "WHERE StoreId = sId;\n" +
                    "END\n" +
                    "$$ LANGUAGE plpgsql", Database.StoreIdAndType, Database.StoreNameAndType),
            String.format("CREATE FUNCTION getStoreByName(sName VARCHAR(50))\n" +
                    "RETURNS TABLE (%s, %s)\n" +
                    "AS\n" +
                    "$$\n" +
                    "\n" +
                    "BEGIN\n" +
                    "RETURN QUERY\n" +
                    "SELECT StoreId, StoreName\n" +
                    "FROM Stores\n" +
                    "WHERE StoreName = sName;\n" +
                    "END\n" +
                    "$$ LANGUAGE plpgsql", Database.StoreIdAndType, Database.StoreNameAndType),
            String.format("CREATE FUNCTION getCountFromStores()\n" +
                    "RETURNS INTEGER\n" +
                    "AS\n" +
                    "$$\n" +
                    "\n" +
                    "BEGIN\n" +
                    "RETURN (\n" +
                    "SELECT StoreId, StoreName\n" +
                    "FROM Stores\n" +
                    "WHERE StoreName = storeName);\n" +
                    "END\n" +
                    "$$ LANGUAGE plpgsql")
    };
}

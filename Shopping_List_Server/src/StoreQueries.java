/**
 * Created by Christopher on 9/2/2015.
 */
public class StoreQueries {
    private StoreQueries(){}

    public static final String GetStoreById (int storeId) {
        return String.format("SELECT * " +
                "FROM Stores " +
                "WHERE StoreId = %d", storeId);
    }
    public static final String GetStoreByName (String storeName) {
        return String.format("SELECT * " +
                "FROM Stores " +
                "WHERE StoreName = %s", storeName);
    }
    public static final String AddStore (String storeName) {
        return String.format("INSERT INTO Stores (StoreName) (%s)", storeName);
    }
    public static final String GetCountFromStores (int count) {
        return String.format("SELECT * " +
                "FROM Stores " +
                "OFFSET %d " +
                "LIMIT 10", count);
    }

    public static final String UpdateStore (int storeId, String newStoreName) {
        return String.format("UPDATE Stores " +
                "SET (StoreName) = (%s) " +
                "WHERE StoreId = %d", newStoreName, storeId);
    }

}

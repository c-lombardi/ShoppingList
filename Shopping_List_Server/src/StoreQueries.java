/**
 * Created by Christopher on 9/2/2015.
 */
public class StoreQueries {
    public static final String getStoreById(final int storeId) {
        return String.format("SELECT * " +
                "FROM Stores " +
                "WHERE StoreId = %d", storeId);
    }

    public static final String getStoreByName(final String storeName) {
        return String.format("SELECT * " +
                "FROM Stores " +
                "WHERE StoreName = '%s'", storeName);
    }

    public static final String addStore(final String storeName) {
        return String.format("INSERT INTO Stores (StoreId, StoreName) " +
                "VALUES (nextval('Store_Seq'), '%s') " +
                "RETURNING StoreId", storeName);
    }

    public static final String getCountFromStores() {
        return String.format("SELECT * " +
                "FROM Stores");
    }

    public static final String updateStore(final Store store) {
        return String.format("UPDATE Stores " +
                "SET (StoreName) = ('%s') " +
                "WHERE StoreId = %d", store.getName(), store.getId());
    }

    public static final String removeStore(final String StoreName) {
        return String.format("DELETE FROM Stores " +
                "WHERE StoreName = '%s'", StoreName);
    }

    public static final String removeStore(final int StoreId) {
        return String.format("DELETE FROM Stores " +
                "WHERE StoreName = %d", StoreId);
    }


}

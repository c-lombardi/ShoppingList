/**
 * Created by Christopher on 9/2/2015.
 */
public class StoreQueries {
    public static final String getStoreById(final int storeId) {
        return String.format("SELECT * " +
                "FROM '%s' " +
                "WHERE StoreId = %d", Database.StoresTableName, storeId);
    }

    public static final String getStoreByName(final String storeName) {
        return String.format("SELECT * " +
                "FROM %s " +
                "WHERE StoreName = '%s'", Database.StoresTableName, storeName);
    }

    public static final String addStore(final String storeName) {
        return String.format("INSERT INTO %s (StoreId, StoreName) " +
                "VALUES (nextval('Store_Seq'), '%s') " +
                "RETURNING StoreId", Database.StoresTableName, storeName);
    }

    public static final String getCountFromStores() {
        return String.format("SELECT * " +
                "FROM %s", Database.StoresTableName);
    }

    public static final String updateStore(final Store store) {
        return String.format("UPDATE %s " +
                "SET (StoreName) = ('%s') " +
                "WHERE StoreId = %d", Database.StoresTableName, store.getName(), store.getId());
    }

    public static final String removeStore(final String StoreName) {
        return String.format("DELETE FROM %s " +
                "WHERE StoreName = '%s'", Database.StoresTableName, StoreName);
    }

    public static final String removeStore(final int StoreId) {
        return String.format("DELETE FROM %s " +
                "WHERE StoreName = %d", Database.StoresTableName, StoreId);
    }


}

/**
 * Created by Christopher on 9/2/2015.
 */
public class StoreQueries {
    public static final String getStoreById() {
        return "SELECT getStoreById(?);";
    }

    public static final String getStoreByName() {
        return "SELECT getStoreByName(?);";
    }

    public static final String createStore() {
        return "SELECT createStore(?, ?);";
    }

    public static final String updateStore() {
        return "SELECT updateStoreById(?, ?);";
    }


}

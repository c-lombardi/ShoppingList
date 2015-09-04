import java.sql.ResultSet;

/**
 * Created by Christopher on 9/1/2015.
 */
public class Store {
    public int getStoreId() {
        return Id;
    }

    public void setStoreId(int storeId) {
        Id = storeId;
    }

    private int Id;

    public String getStoreName() {
        return Name;
    }

    public void setStoreName(String storeName) {
        Name = storeName;
    }

    private String Name;

    public Store (String name)
    {
        Name = name;
    }

    public Store (int id) throws Exception {
        try (Database db = new Database()) {
            ResultSet store = db.SelectTableQuery(StoreQueries.GetStoreById(id));
            while (store.next()) {
                Name = store.getString("StoreName");
            }
        }
    }
}

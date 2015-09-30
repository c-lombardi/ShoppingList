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

    public Store (String store)
    {
        if(store.contains(","))
        {
            String[] storeParts = store.split(",");
            Id = Integer.parseInt(storeParts[0]);
            Name = storeParts[1];
        }
    }

    public Store (String name, Database db) throws Exception {
        ResultSet store = db.SelectTableQuery(StoreQueries.GetStoreByName(name));
        while (store.next()) {
            Name = store.getString("StoreName");
            Id = store.getInt("StoreId");
        }
    }

    public Store (int id, Database db) throws Exception {
        ResultSet store = db.SelectTableQuery(StoreQueries.GetStoreById(id));
        while (store.next()) {
            Name = store.getString("StoreName");
            Id = store.getInt("StoreId");
        }
    }

    public Store(){}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.valueOf(Id).trim());
        sb.append(",");
        sb.append(Name.trim());
        return sb.toString();
    }
}

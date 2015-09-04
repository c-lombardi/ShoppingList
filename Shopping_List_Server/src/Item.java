import java.sql.ResultSet;

/**
 * Created by Christopher on 9/1/2015.
 */
public class Item {
    public int getItemId() {
        return Id;
    }

    public void setItemId(int itemId) {
        Id = itemId;
    }

    //Getters and Setters
    private int Id;
    public String getName() throws Exception {
        return Name;
    }

    public void setName(String name) throws Exception {
        Name = name;
    }

    private String Name;

    public Store getStore() throws Exception {
        return Store;
    }

    public void setStore(Store store) throws Exception {
        Store = store;
    }

    private Store Store;

    public Boolean isListActive() throws Exception {
        return ListActive;
    }

    public void setListActive(Boolean listActive) throws Exception {
        ListActive = listActive;
    }

    private Boolean ListActive;

    public Boolean isLibraryActive() throws Exception {
        return LibraryActive;
    }

    public void setLibraryActive(Boolean libraryActive) throws Exception {
        LibraryActive = libraryActive;
    }

    private Boolean LibraryActive;

    public Float getBestPrice() throws Exception {
        return BestPrice;
    }

    public void setBestPrice(Float price) throws Exception {
        BestPrice = price;
    }

    private Float BestPrice;
    //End Getters and Setters

    //Constructors
    public Item(String name, Store store, Float price) throws Exception {
        Name = name;
        Store = store;
        BestPrice = price;
        ListActive = true;
        LibraryActive = true;try (Database db = new Database())
        {
            int StoreId = 0;
            ResultSet item = db.SelectTableQuery(ItemQueries.AddItem(Name));
            while(item.next()) {
                Id = item.getInt("ItemId");
            }
            db.UpdateTableQuery(ItemQueries.UpdateItemById(Id, this));
        }

    }
    public Item(int itemid) throws Exception {
        try (Database db = new Database())
        {
            int StoreId = 0;
            ResultSet item = db.SelectTableQuery(ItemQueries.GetItemById(itemid));
            while(item.next())
            {
                Name = item.getString("ItemName");
                StoreId = item.getInt("StoreId");
                BestPrice = item.getFloat("BestPrice");
                ListActive = item.getBoolean("ListActive");
                LibraryActive = item.getBoolean("LibraryActive");
            }
            if(StoreId > 0)
            {
                ResultSet store = db.SelectTableQuery(StoreQueries.GetStoreById(StoreId));
                {
                    Store = new Store(store.getString("StoreName"));
                    Store.setStoreId(store.getInt("StoreId"));
                }
            }
        }
    }
    //End Constructors
}

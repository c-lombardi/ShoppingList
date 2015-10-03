import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Christopher on 9/1/2015.
 */
public class Item implements CRUD<Item> {
    private Integer Id;
    private String Name;
    private Store Store;
    private Boolean ListActive;
    private Boolean LibraryActive;
    private Float BestPrice;

    public Float getBestPrice() {
        return BestPrice;
    }

    public Integer getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }

    public Store getStore() {
        return Store;
    }
    /*
    public void setLibraryActive(Boolean libraryActive) {
        LibraryActive = libraryActive;
    }

    public void setListActive(Boolean listActive) {
        ListActive = listActive;
    }*/

    public Boolean isListActive() {
        return ListActive;
    }

    public Boolean isLibraryActive() {
        return LibraryActive;
    }
    //Constructors
    public Item(int id) {
        Id = id;
        ListActive = true;
        LibraryActive = true;
    }
    public Item(String name) {
        Name = name;
        ListActive = true;
        LibraryActive = true;
    }
    /*public Item(String name, Float price) {
        Name = name;
        BestPrice = price;
        ListActive = true;
        LibraryActive = true;
    }
    public Item(String name, Store store, Float price) {
        Name = name;
        Store = store;
        BestPrice = price;
        ListActive = true;
        LibraryActive = true;
    }
    public Item(int Id, String name, Float price) {
        Name = name;
        BestPrice = price;
        ListActive = true;
        LibraryActive = true;
    }*/
    public Item(int id, String name, Store store, Float price) {
        Id = id;
        Name = name;
        Store = store;
        BestPrice = price;
        ListActive = true;
        LibraryActive = true;
    }

    public void fromString(String itemString) throws SQLException, ClassNotFoundException {
        String [] partStrings = itemString.split(",");
        for (int i = 0; i < partStrings.length; i++)
        {
            if(i == 0)
            {
                Id = Integer.parseInt(partStrings[i]);
            }
            else if(i == 1)
            {
                Name = partStrings[i];
            }
            else if(i == 2)
            {
                BestPrice = Float.parseFloat(partStrings[i]);
            }
            else if(i == 3)
            {
                ListActive = Boolean.parseBoolean(partStrings[i]);
            }
            else if(i == 4)
            {
                Store = new Store(partStrings[i]);
            }
            else if(i == 5)
            {
                if(Store == null) {
                    Store = new Store(Integer.parseInt(partStrings[i]));
                } else {
                    Store.setId(Integer.parseInt(partStrings[i]));
                }
            }
        }
    }

    public Item() {}

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(String.valueOf(Id).trim());
        sb.append(",");
        sb.append(Name.trim());
        sb.append(",");
        sb.append(String.valueOf(BestPrice).trim());
        sb.append(",");
        sb.append(String.valueOf(ListActive).trim());
        if(Store != null && Store.getId() != 0)
        {
            sb.append(",");
            sb.append(Store.toString());
        }
        return sb.toString();
    }

    @Override
    public void Create() {
        try (Database db = new Database()) {
            if(Store != null) {
                Store.Create();
            }
            ResultSet rs = db.SelectTableQuery(ItemQueries.AddItem(this));
            while (rs.next()) {
                Id = rs.getInt("ItemId");
                ListActive = true;
                LibraryActive = true;
            }
        } catch (Exception ex) {
            if(Store != null || BestPrice != 0) {
                Update(false);
            } else {
                Update(true);
            }
            Read();
        }
    }

    @Override
    public void Read() {
        try (Database db = new Database()) {
            if(Id != null && Id != 0) {
                ResultSet rs = db.SelectTableQuery(ItemQueries.GetItemById(Id));
                while (rs.next()) {
                    Name = rs.getString("ItemName");
                    BestPrice = rs.getFloat("BestPrice");
                    ListActive = rs.getBoolean("ListActive");
                    LibraryActive = rs.getBoolean("LibraryActive");
                    Store = new Store(rs.getInt("StoreId"));
                    Store.Read();
                }
            } else if(Name != null) {
                ResultSet rs = db.SelectTableQuery(ItemQueries.GetItemByName(Name));
                while (rs.next()) {
                    Id = rs.getInt("ItemId");
                    BestPrice = rs.getFloat("BestPrice");
                    ListActive = rs.getBoolean("ListActive");
                    LibraryActive = rs.getBoolean("LibraryActive");
                    Store = new Store(rs.getInt("StoreId"));
                    Store.Read();
                }
            }
        } catch (Exception ex) {

        }
    }

    @Override
    public ArrayList<Item> ReadAll() {
        ArrayList<Item> returnList = new ArrayList<Item>();
        try (Database db = new Database()) {
            ResultSet rs = db.SelectTableQuery(ItemQueries.GetAllItemsFromList());
            while(rs.next())
            {
                returnList.add(new Item(rs.getInt("ItemId"), rs.getString("ItemName"), new Store(rs.getString("StoreName"), rs.getInt("StoreId")), rs.getFloat("BestPrice")));
            }
        } catch (Exception ex) {
            System.out.println("Fail");
        }
        return returnList;
    }

    @Override
    public void Update(boolean justFlipListActive) {
        try (Database db = new Database()) {
            if (Name != null) {
                if(Store != null) {
                    Store.Create();
                }
                if(!justFlipListActive) {
                    if (Id != null && Id != 0) {
                        db.UpdateTableQuery(ItemQueries.UpdateItemById(this));
                    } else {
                        db.UpdateTableQuery(ItemQueries.UpdateItemByName(this));
                    }
                } else {
                    if (Id != null && Id != 0) {
                        db.UpdateTableQuery(ItemQueries.MakeActiveById(this));
                    } else {
                        db.UpdateTableQuery(ItemQueries.MakeActiveByName(this));
                    }
                }
            }
        } catch (Exception ex) {
            Create();
        }
    }

    @Override
    public void Delete(boolean deleteFromLibrary) {
        try (Database db = new Database()) {
            if (Id != null) {
                if(!deleteFromLibrary) {
                    db.UpdateTableQuery(ItemQueries.RemoveItemFromList(Id));
                } else {
                    db.UpdateTableQuery(ItemQueries.RemoveItemFromLibrary(Id));
                }
            }
        } catch (Exception ex) {

        }
    }

    public void AttachStore(){
        try (Database db = new Database()) {
            if(Store.getId() != null) {
                db.UpdateTableQuery(ItemQueries.AddStoreToItem(Id, Store.getId()));
            }
        } catch(Exception ex) {

        }
    }
    //End Constructors
}

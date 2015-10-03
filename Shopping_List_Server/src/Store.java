import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Christopher on 9/1/2015.
 */
public class Store implements CRUD<Store> {
    public String getName() {
        return Name;
    }

    public Integer getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    private String Name;
    private Integer Id;

    public Store(String n, int id) {
        Name = n;
        Id = id;
    }

    public Store(String n) {
        Name = n;
    }

    public Store(int id) {
        Id = id;
    }


    public Store() throws SQLException, ClassNotFoundException {
    }
    public void fromString(String storeString) {
        String[] storeParts = storeString.split(",");
        Id = Integer.parseInt(storeParts[0]);
        Name = storeParts[1];
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Name.trim());
        if(Id != null && Id != 0) {
            sb.append(",");
            sb.append(String.valueOf(Id).trim());
        }
        return sb.toString();
    }

    @Override
    public void Create() {
        try (Database db = new Database()) {
            ResultSet rs = db.SelectTableQuery(StoreQueries.AddStore(Name));
            while (rs.next()) {
                Id = rs.getInt("StoreId");
            }
        } catch (Exception ex) {
            Read();
        }
    }

    @Override
    public void Read() {
        try (Database db = new Database()) {
            if(Id != null && Id != 0) {
                ResultSet rs = db.SelectTableQuery(StoreQueries.GetStoreById(Id));
                while (rs.next()) {
                    Name = rs.getString("StoreName");
                }
            } else if(Name != null) {
                ResultSet rs = db.SelectTableQuery(StoreQueries.GetStoreByName(Name));
                while (rs.next()) {
                    Id = rs.getInt("StoreId");
                }
            }
        } catch (Exception ex) {

        }
    }

    @Override
    public ArrayList<Store> ReadAll() {
        ArrayList<Store> returnList = new ArrayList<>();
        try (Database db = new Database()) {
            ResultSet rs = db.SelectTableQuery(StoreQueries.GetCountFromStores());
        while(rs.next()){
                returnList.add(new Store(rs.getString("StoreName"), rs.getInt("StoreId")));
            }
        } catch (Exception ex){}
        return returnList;
    }

    @Override
    public void Update(boolean justFlipListActive) {
        try (Database db = new Database()) {
            if (Name != null) {
                if (Id != null) {
                    db.UpdateTableQuery(StoreQueries.UpdateStore(this));
                } else {
                    throw new SQLException();
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
                db.UpdateTableQuery(StoreQueries.RemoveStore(Id));
            } else if (Name != null) {
                db.UpdateTableQuery(StoreQueries.RemoveStore(Name));
            }
        } catch (Exception ex) {

        }
    }
}

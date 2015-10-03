import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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


    public Store() {
    }
    public void fromString(String storeString) {
        final String[] storeParts = storeString.split(",");
        Id = Integer.parseInt(storeParts[0]);
        Name = storeParts[1];
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(Name.trim());
        if(Id != null && Id != 0) {
            sb.append(",");
            sb.append(String.valueOf(Id).trim());
        }
        return sb.toString();
    }

    @Override
    public void create() {
        try (final database db = new database()) {
            final ResultSet rs = db.selectTableQuery(StoreQueries.addStore(Name));
            while (rs.next()) {
                Id = rs.getInt("StoreId");
            }
        } catch (Exception ex) {
            read();
        }
    }

    @Override
    public void read() {
        try (final database db = new database()) {
            if(Id != null && Id != 0) {
                final ResultSet rs = db.selectTableQuery(StoreQueries.getStoreById(Id));
                while (rs.next()) {
                    Name = rs.getString("StoreName");
                }
            } else if(Name != null) {
                final ResultSet rs = db.selectTableQuery(StoreQueries.getStoreByName(Name));
                while (rs.next()) {
                    Id = rs.getInt("StoreId");
                }
            }
        } catch (Exception ex) {

        }
    }

    @Override
    public List<Store> readAll() {
        final List<Store> returnList = new ArrayList<>();
        try (final database db = new database()) {
            final ResultSet rs = db.selectTableQuery(StoreQueries.getCountFromStores());
        while(rs.next()){
                returnList.add(new Store(rs.getString("StoreName"), rs.getInt("StoreId")));
            }
        } catch (Exception ex){}
        return returnList;
    }

    @Override
    public void update(boolean justFlipListActive) {
        try (final database db = new database()) {
            if (Name != null) {
                if (Id != null) {
                    db.updateTableQuery(StoreQueries.updateStore(this));
                } else {
                    throw new SQLException();
                }
            }
        } catch (Exception ex) {
            create();
        }
    }

    @Override
    public void delete(boolean deleteFromLibrary) {
        try (final database db = new database()) {
            if (Id != null) {
                db.updateTableQuery(StoreQueries.removeStore(Id));
            } else if (Name != null) {
                db.updateTableQuery(StoreQueries.removeStore(Name));
            }
        } catch (Exception ex) {

        }
    }
}

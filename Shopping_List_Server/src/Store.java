import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Store implements CRUD<Store> {

    private String Name;
    private int Id;

    public String getName() {
        return Name;
    }

    public void setName(final String name) {
        Name = name;
    }

    public int getId() {
        return Id;
    }

    public void setId(final int id) {
        Id = id;
    }

    @Override
    public Store create() {
        try (final database db = new database()) {
            try (final PreparedStatement stmt = db.selectTableQuery(StoreQueries.addStore(Name))) {
                try (final ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Id = rs.getInt("StoreId");
                    }
                }
            }
        } catch (Exception ex) {
            read();
        } finally {
            return this;
        }
    }

    @Override
    public Store read() {
        try (final database db = new database()) {
            if (Id != 0) {
                try (final PreparedStatement stmt = db.selectTableQuery(StoreQueries.getStoreById(Id))) {
                    try (final ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            Name = rs.getString("StoreName");
                        }
                    }
                }
            } else if (Name != null) {
                try (final PreparedStatement stmt = db.selectTableQuery(StoreQueries.getStoreByName(Name))) {
                    try (final ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            Id = rs.getInt("StoreId");
                        }
                    }
                }
            }
        } catch (Exception ex) {

        } finally {
            return this;
        }
    }

    public List<Store> readAll(final boolean fromLibrary) {
        final List<Store> returnList = new ArrayList<>();
        try (final database db = new database()) {
            try (final PreparedStatement stmt = db.selectTableQuery(StoreQueries.getCountFromStores())) {
                try (final ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Store store = new Store();
                        store.setId(rs.getInt("StoreId"));
                        store.setName(rs.getString("StoreName"));
                        returnList.add(store);
                    }
                }
            }
        } catch (Exception ex) {
        } finally {
            return returnList;
        }
    }

    @Override
    public Store update(final boolean justFlipListActive) {
        try (final database db = new database()) {
            if (Name != null) {
                if (Id != 0) {
                    db.updateTableQuery(StoreQueries.updateStore(this));
                } else {
                    throw new SQLException();
                }
            }
        } catch (Exception ex) {
            create();
        } finally {
            return this;
        }
    }

    @Override
    public Store delete(final boolean deleteFromLibrary) {
        try (final database db = new database()) {
            if (Id != 0) {
                db.updateTableQuery(StoreQueries.removeStore(Id));
            } else if (Name != null) {
                db.updateTableQuery(StoreQueries.removeStore(Name));
            }
        } catch (Exception ex) {

        } finally {
            return this;
        }
    }
}
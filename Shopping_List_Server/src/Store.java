import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.ResultSet;
import java.sql.SQLException;
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
        try (final Database db = new Database()) {
            try (final PreparedSelectStatement stmt = db.selectTableQuery(StoreQueries.createStore())) {
                stmt.setString(1, Name);
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
        try (final Database db = new Database()) {
            if (Id != 0) {
                try (final PreparedSelectStatement stmt = db.selectTableQuery(StoreQueries.getStoreById())) {
                    stmt.setInt(1, Id);
                    try (final ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            Name = rs.getString("StoreName");
                        }
                    }
                }
            } else if (Name != null) {
                try (final PreparedSelectStatement stmt = db.selectTableQuery(StoreQueries.getStoreByName())) {
                    stmt.setString(1, Name);
                    try (final ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            Id = rs.getInt("StoreId");
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        } finally {
            return this;
        }
    }

    public List<Store> readAll(final boolean fromLibrary) {
        throw new NotImplementedException();
    }

    @Override
    public Store update(final boolean justFlipListActive) {
        try (final Database db = new Database()) {
            if (Name != null) {
                if (Id != 0) {
                    try (final PreparedUpdateStatement stmt = db.updateTableQuery(StoreQueries.updateStore())) {
                        stmt.setInt(1, getId());
                        stmt.setString(2, getName());
                        stmt.executeUpdate();
                    }
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
        throw new NotImplementedException();
    }
}
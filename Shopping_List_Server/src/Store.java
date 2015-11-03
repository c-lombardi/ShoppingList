import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christopher on 9/1/2015.
 */
public class Store {
    private final String Name;
    private final int Id;

    private Store(StoreBuilder storeBuilder) {
        Name = storeBuilder.Name;
        Id = storeBuilder.Id;
    }

    public String getName() {
        return Name;
    }

    public int getId() {
        return Id;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(Name.trim());
        sb.append(",");
        sb.append(String.valueOf(Id).trim());
        return sb.toString();
    }

    public static Store fromString(String storeString) {
        final String[] storeParts = storeString.split(",");
        final StoreBuilder sb = new StoreBuilder(storeParts[1], Integer.parseInt(storeParts[0]));
        return sb.build();
    }

    public static class StoreBuilder implements CRUD<StoreBuilder> {
        private String Name;
        private int Id;

        public StoreBuilder(String name, int id) {
            Name = name;
            Id = id;
        }

        public StoreBuilder(String name) {
            Name = name;
            Id = 0;
        }

        public StoreBuilder(int id) {
            Id = id;
        }

        public StoreBuilder() {
            Id = 0;
        }

        public StoreBuilder Id (int id) {
            Id = id;
            return this;
        }

        @Override
        public StoreBuilder create() {
            try (final database db = new database()) {
                try(final PreparedStatement stmt = db.selectTableQuery(StoreQueries.addStore(Name))) {
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
        public StoreBuilder read() {
            try (final database db = new database()) {
                if(Id != 0) {
                    try(final PreparedStatement stmt = db.selectTableQuery(StoreQueries.getStoreById(Id))) {
                        try (final ResultSet rs = stmt.executeQuery()) {
                            while (rs.next()) {
                                Name = rs.getString("StoreName");
                            }
                        }
                    }
                } else if(Name != null) {
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

        public List<StoreBuilder> readAll(boolean fromLibrary) {
            final List<StoreBuilder> returnList = new ArrayList<>();
            try (final database db = new database()) {
                try (final PreparedStatement stmt = db.selectTableQuery(StoreQueries.getCountFromStores())) {
                    try (final ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            returnList.add(new StoreBuilder(rs.getString("StoreName"), rs.getInt("StoreId")));
                        }
                    }
                }
            } catch (Exception ex){} finally {
                return returnList;
            }
        }

        @Override
        public StoreBuilder update(boolean justFlipListActive) {
            try (final database db = new database()) {
                if (Name != null) {
                    if (Id != 0) {
                        db.updateTableQuery(StoreQueries.updateStore(this.build()));
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
        public StoreBuilder delete(boolean deleteFromLibrary) {
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

        public Store build() {
            return new Store(this);
        }
    }
}

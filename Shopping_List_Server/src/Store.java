import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christopher on 9/1/2015.
 */
public class Store {
    private String Name;
    private Integer Id;

    private Store(StoreBuilder storeBuilder) {
        Name = storeBuilder.Name;
        Id = storeBuilder.Id;
    }

    public String getName() {
        return Name;
    }

    public Integer getId() {
        return Id;
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

    public static class StoreBuilder implements CRUD<StoreBuilder> {
        private String Name;
        private Integer Id;

        public StoreBuilder(String name, Integer id) {
            Name = name;
            Id = id;
        }

        public StoreBuilder(String name) {
            Name = name;
        }

        public StoreBuilder(int id) {
            Id = id;
        }

        public StoreBuilder() {
        }

        public StoreBuilder Id (int id) {
            Id = id;
            return this;
        }

        @Override
        public StoreBuilder create() {
            try (final database db = new database()) {
                final ResultSet rs = db.selectTableQuery(StoreQueries.addStore(Name));
                while (rs.next()) {
                    Id = rs.getInt("StoreId");
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

            } finally {
                return this;
            }
        }

        @Override
        public List<StoreBuilder> readAll() {
            final List<StoreBuilder> returnList = new ArrayList<>();
            try (final database db = new database()) {
                final ResultSet rs = db.selectTableQuery(StoreQueries.getCountFromStores());
                while(rs.next()){
                    returnList.add(new StoreBuilder(rs.getString("StoreName"), rs.getInt("StoreId")));
                }
            } catch (Exception ex){} finally {
                return returnList;
            }
        }

        @Override
        public StoreBuilder update(boolean justFlipListActive) {
            try (final database db = new database()) {
                if (Name != null) {
                    if (Id != null) {
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
                if (Id != null) {
                    db.updateTableQuery(StoreQueries.removeStore(Id));
                } else if (Name != null) {
                    db.updateTableQuery(StoreQueries.removeStore(Name));
                }
            } catch (Exception ex) {

            } finally {
                return this;
            }
        }

        public StoreBuilder fromString(String storeString) {
            final String[] storeParts = storeString.split(",");
            Id = Integer.parseInt(storeParts[0]);
            Name = storeParts[1];
            return this;
        }

        public Store build() {
            return new Store(this);
        }
    }
}

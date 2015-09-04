/**
 * Created by Christopher on 9/2/2015.
 */
public class DatabaseQueries {
    private DatabaseQueries(){}
    public static final String createStore = "CREATE TABLE IF NOT EXISTS Stores " +
            "(StoreId INTEGER PRIMARY KEY NOT NULL, " +
            "StoreName CHARACTER(50) NOT NULL UNIQUE)";
    public static final String createItem = "CREATE TABLE IF NOT EXISTS Items " +
            "(ItemId INTEGER PRIMARY KEY NOT NULL, " +
            "ItemName CHARACTER(50) NOT NULL UNIQUE, " +
            "StoreId INTEGER REFERENCES Stores (StoreId), " +
            "BestPrice REAL, " +
            "ListActive BOOLEAN, " +
            "LibraryActive BOOLEAN)";
}

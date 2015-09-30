/**
 * Created by Christopher on 9/2/2015.
 */
public class DatabaseQueries {
    private DatabaseQueries(){}
    public static final String createStore =  "DROP SEQUENCE IF EXISTS Store_Seq; " +
            "CREATE SEQUENCE Store_Seq START 1; " +
            "CREATE TABLE IF NOT EXISTS Stores " +
            "(StoreId INTEGER PRIMARY KEY NOT NULL, " +
            "StoreName CHARACTER(50) NOT NULL UNIQUE)";
    public static final String createItem = "DROP SEQUENCE IF EXISTS Item_Seq; " +
            "CREATE SEQUENCE Item_Seq START 1; " +
            "CREATE TABLE IF NOT EXISTS Items " +
            "(ItemId INTEGER PRIMARY KEY NOT NULL, " +
            "ItemName CHARACTER(50) NOT NULL UNIQUE, " +
            "StoreId INTEGER REFERENCES Stores (StoreId), " +
            "BestPrice REAL DEFAULT 0.00, " +
            "ListActive BOOLEAN DEFAULT TRUE, " +
            "LibraryActive BOOLEAN DEFAULT TRUE);";
}

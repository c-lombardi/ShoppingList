/**
 * Created by Christopher on 9/2/2015.
 */
public class DatabaseQueries {
    public static final String CREATE_SESSION = "DROP EXTENSION IF EXISTS \"uuid-ossp\"; " +
            "CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\"; " +
            "CREATE TABLE IF NOT EXISTS Sessions " +
            "(SessionId uuid NOT NULL PRIMARY KEY DEFAULT uuid_generate_v4(), " +
            "SessionPhoneNumber VARCHAR(12) CHECK (SessionPhoneNumber ~ '^[0-9]+$') NOT NULL, " +
            "SessionAuthCode VARCHAR(6) NULL); " +
            "CREATE UNIQUE INDEX sessionphonenumber_idx ON Sessions (SessionPhoneNumber);";
    public static final String CREATE_STORE = "DROP SEQUENCE IF EXISTS Store_Seq; " +
            "CREATE SEQUENCE Store_Seq START 1; " +
            "CREATE TABLE IF NOT EXISTS Stores " +
            "(StoreId INTEGER PRIMARY KEY NOT NULL, " +
            "StoreName VARCHAR(50) NOT NULL UNIQUE);";
    public static final String CREATE_ITEM = "DROP SEQUENCE IF EXISTS Item_Seq; " +
            "CREATE SEQUENCE Item_Seq START 1; " +
            "CREATE TABLE IF NOT EXISTS Items " +
            "(ItemId INTEGER PRIMARY KEY NOT NULL, " +
            "ItemName VARCHAR(50) NOT NULL, " +
            "StoreId INTEGER REFERENCES Stores (StoreId), " +
            "SessionId uuid REFERENCES Sessions (SessionId) NOT NULL, " +
            "BestPrice REAL DEFAULT 0.00, " +
            "ListActive BOOLEAN DEFAULT TRUE, " +
            "LibraryActive BOOLEAN DEFAULT TRUE); " +
            "CREATE UNIQUE INDEX itemnamesessionid_idx ON Items (ItemName, SessionId);";
}

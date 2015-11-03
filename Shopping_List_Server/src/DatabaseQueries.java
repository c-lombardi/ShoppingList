/**
 * Created by Christopher on 9/2/2015.
 */
public class databaseQueries {
    public static final String CREATE_SESSION = "DROP EXTENSION IF EXISTS \"uuid-ossp\"; " +
    "CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\"; " +
    "CREATE TABLE IF NOT EXISTS Sessions " +
    "(SessionId uuid NOT NULL PRIMARY KEY DEFAULT uuid_generate_v4(), " +
    "SessionName CHARACTER(50) NOT NULL);";
    public static final String CREATE_SESSION_DEVICE = "CREATE TABLE IF NOT EXISTS SessionDevice "+
    "(SessionId uuid NOT NULL, "+
    "DeviceId character varying NOT NULL, "+
    "CONSTRAINT SessionDevice_pkey PRIMARY KEY (SessionId, DeviceId), "+
    "CONSTRAINT SessionDevice_SessionId_fkey FOREIGN KEY (SessionId) "+
    "REFERENCES sessions (sessionid) MATCH SIMPLE "+
    "ON UPDATE NO ACTION ON DELETE NO ACTION)";
    public static final String CREATE_STORE =  "DROP SEQUENCE IF EXISTS Store_Seq; " +
    "CREATE SEQUENCE Store_Seq START 1; " +
    "CREATE TABLE IF NOT EXISTS Stores " +
    "(StoreId INTEGER PRIMARY KEY NOT NULL, " +
    "StoreName CHARACTER(50) NOT NULL UNIQUE)";
    public static final String CREATE_ITEM = "DROP SEQUENCE IF EXISTS Item_Seq; " +
    "CREATE SEQUENCE Item_Seq START 1; " +
    "CREATE TABLE IF NOT EXISTS Items " +
    "(ItemId INTEGER PRIMARY KEY NOT NULL, " +
    "ItemName CHARACTER(50) NOT NULL UNIQUE, " +
    "StoreId INTEGER REFERENCES Stores (StoreId), " +
    "SessionId uuid REFERENCES Sessions (SessionId) NOT NULL, " +
    "BestPrice REAL DEFAULT 0.00, " +
    "ListActive BOOLEAN DEFAULT TRUE, " +
    "LibraryActive BOOLEAN DEFAULT TRUE);";
}

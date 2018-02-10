/**
 * Created by Christopher on 9/2/2015.
 */
public class DatabaseQueries {
    public static final String CREATE_SESSION = String.format("DROP EXTENSION IF EXISTS \"uuid-ossp\"; " +
                "CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\"; " +
                "CREATE TABLE IF NOT EXISTS Sessions " +
                "(SessionId uuid NOT NULL PRIMARY KEY DEFAULT uuid_generate_v4(), " +
                "SessionPhoneNumber VARCHAR(12) CHECK (SessionPhoneNumber ~ '^[0-9]+$') NOT NULL, " +
                "SessionAuthCode VARCHAR(6) NULL); " +
                "CREATE UNIQUE INDEX sessionphonenumber_idx ON Sessions (SessionPhoneNumber);");
    public static final String CREATE_STORE = String.format("DROP SEQUENCE IF EXISTS Store_Seq; " +
                "CREATE SEQUENCE Store_Seq START 1; " +
                "CREATE TABLE IF NOT EXISTS Stores " +
                "(StoreId INTEGER PRIMARY KEY NOT NULL, " +
                "StoreName VARCHAR(50) NOT NULL UNIQUE);");
    public static final String CREATE_SHOPPING_LISTS = String.format("DROP SEQUENCE IF EXISTS Shopping_List_Seq; "+
                "CREATE SEQUENCE Shopping_List_Seq START 1; " +
                "CREATE TABLE IF NOT EXISTS ShoppingLists " +
                "(ShoppingListId INTEGER PRIMARY KEY NOT NULL, " +
                "ShoppingListName VARCHAR(50) NOT NULL, " +
                "SessionId uuid REFERENCES Sessions (SessionId) NOT NULL);");
    public static final String CREATE_ITEM = String.format("DROP SEQUENCE IF EXISTS Item_Seq; " +
                "CREATE SEQUENCE Item_Seq START 1; " +
                "CREATE TABLE IF NOT EXISTS Items " +
                "(ItemId INTEGER PRIMARY KEY NOT NULL, " +
                "ItemName VARCHAR(50) NOT NULL, " +
                "StoreId INTEGER REFERENCES Stores (StoreId), " +
                "SessionId uuid REFERENCES Sessions (SessionId) NOT NULL, " +
                "BestPrice REAL DEFAULT 0.00); " +
                "CREATE UNIQUE INDEX itemnamesessionid_idx ON Items (ItemName, SessionId);");
    public static final String CREATE_SHOPPING_LIST_ITEMS = String.format("CREATE TABLE ShoppingListItems " +
                "( " +
                "ShoppingListId integer NOT NULL, " +
                "ItemId integer NOT NULL, " +
                "ItemStatus VARCHAR(8) DEFAULT 'Default', " +
                "CONSTRAINT pkShoppingListId_ItemId PRIMARY KEY (ShoppingListId, ItemId), " +
                "CONSTRAINT FK_ItemId FOREIGN KEY (ItemId) " +
                "REFERENCES Items (Itemid) MATCH SIMPLE " +
                "ON UPDATE NO ACTION ON DELETE NO ACTION, " +
                "CONSTRAINT ShoppingListId FOREIGN KEY (ShoppingListId) " +
                "REFERENCES ShoppingLists (shoppinglistid) MATCH SIMPLE " +
                "ON UPDATE NO ACTION ON DELETE NO ACTION " +
                ");");
}

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;

/**
 * Created by Christopher on 9/2/2015.
 */
public class Server implements Runnable {

    private int Port;
    private ServerSocket serverSocket = null;
    private Socket socket = null;
    private Thread thread = null;
    private DataInputStream streamIn = null;

    public Server(int port) {
        Port = port;
        try
        {  System.out.println("Binding to port " + port + ", please wait  ...");
            serverSocket = new ServerSocket(port, 100, InetAddress.getLocalHost());
            start();
        }
        catch(IOException ioe)
        {
            System.out.println(ioe);
        }
    }
    @Override
    public void run() {
        while(thread != null) {
            try
            {
                System.out.println("waiting for a connection...");
                socket = serverSocket.accept();
                open();
                System.out.println("Connected!");
                boolean done = false;
                try (Database db = new Database()) {
                    thread.sleep(1000);
                    while (!done) {
                        try {
                            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            StringBuilder sb = new StringBuilder();
                            while (in.ready()) {
                                sb.append(in.readLine());
                            }
                            String MessageFromClient = sb.toString();
                            //Get the command from the client
                            char commandCharFromClient = MessageFromClient.charAt(0);
                            int valueOfFirstChar = Character.getNumericValue(commandCharFromClient);
                            ByteCommand command = ByteCommand.values()[valueOfFirstChar];
                            //Parse String To items and stores
                            try {
                                MessageFromClient = MessageFromClient.substring(2); //Remove first command char and semicolon
                            } catch (Exception ex){}
                            PrintWriter out = new PrintWriter(socket.getOutputStream());
                            switch (command) {
                                case GetItems: {
                                    ResultSet rs = db.SelectTableQuery(ItemQueries.GetCountFromItemList(0));
                                    while (rs.next()) {
                                        out.println((new Item(rs.getInt("ItemId"), db).toString()));
                                        out.flush();
                                    }
                                    break;
                                }
                                case AddItem: {
                                    String ItemsNamesString = MessageFromClient;
                                    ResultSet rs;
                                    if (!(ItemsNamesString.length() < 2)) {
                                        //Check If Already Exists
                                        rs = db.SelectTableQuery(ItemQueries.GetItemByName(ItemsNamesString));
                                        Item i;
                                        StringBuilder itemConcatenator = new StringBuilder();
                                        if(!rs.next()) {
                                            rs = db.SelectTableQuery(ItemQueries.AddItem(ItemsNamesString));
                                            rs.next();
                                            i = new Item(rs.getInt("ItemId"), db);
                                        } else {
                                            i = new Item(rs.getInt("ItemId"), db);
                                            i.setListActive(true);
                                            db.UpdateTableQuery(ItemQueries.UpdateItemById(i.getItemId(), i));
                                        }
                                        if (i.getName() != null) {
                                            itemConcatenator.append(i.toString());
                                            String outputString = itemConcatenator.toString();
                                            out.println(outputString);
                                            out.flush();
                                        }
                                    }
                                    break;
                                }
                                case UpdateItem: {
                                    String ItemsWithNewProperties = MessageFromClient;
                                    Item newItem = new Item(ItemsWithNewProperties);
                                    //get Item original properties
                                    Item oldItem = new Item(newItem.getItemId(), db);
                                    if(oldItem.getStore() != null){
                                        if(oldItem.getStore().getStoreName() != newItem.getStore().getStoreName())
                                        {
                                            //try to get exisiting Store
                                            Store s = null;
                                            try {
                                                s = new Store(newItem.getStore().getStoreName(), db);
                                            } catch(Exception ex) {}
                                            if(s != null) {//Store Existed
                                                newItem.setStore(s);
                                            } else {//Store Needs to be Created
                                                ResultSet rs = db.SelectTableQuery(StoreQueries.AddStore(newItem.getStore().getStoreName()));
                                                while(rs.next())
                                                {
                                                    s = new Store(rs.getInt("StoreId"), db);
                                                }
                                            }
                                            newItem.setStore(s);
                                        }
                                    }
                                    db.UpdateTableQuery(ItemQueries.UpdateItemById(newItem.getItemId(), newItem));
                                    break;
                                }
                                case AttachStoreToItem: {
                                    String[] StoreAndItemIds = MessageFromClient.split(";");
                                    int ItemId = Integer.parseInt(StoreAndItemIds[0]);
                                    int StoreId = Integer.parseInt(StoreAndItemIds[1]);
                                    db.UpdateTableQuery(ItemQueries.AddStoreToItem(ItemId, StoreId));
                                    break;
                                }
                                case RemoveItemFromList: {
                                    int ItemIdToDelete = Integer.parseInt(MessageFromClient);
                                    db.UpdateTableQuery(ItemQueries.RemoveItemFromList(ItemIdToDelete));
                                    break;
                                }
                                case RemoveItemFromLibrary: {
                                    int ItemIdToDelete = Integer.parseInt(MessageFromClient);
                                    db.UpdateTableQuery(ItemQueries.RemoveItemFromLibrary(ItemIdToDelete));
                                    break;
                                }
                                case AddStore: {
                                    String StoreName = MessageFromClient;
                                    ResultSet rs;
                                    if (!(StoreName.length() < 2)) {
                                        //Check If Already Exists
                                        rs = db.SelectTableQuery(StoreQueries.GetStoreByName(StoreName));
                                        Store s;
                                        StringBuilder storeConcatenator = new StringBuilder();
                                        if(!rs.next()) {
                                            rs = db.SelectTableQuery(StoreQueries.AddStore(StoreName));
                                            rs.next();
                                            s = new Store(rs.getInt("StoreId"), db);
                                        } else {
                                            s = new Store(rs.getInt("StoreId"), db);
                                            db.UpdateTableQuery(StoreQueries.UpdateStore(s.getStoreId(), s.getStoreName()));
                                        }
                                        if (s.getStoreName() != null) {
                                            storeConcatenator.append(s.toString());
                                            String outputString = storeConcatenator.toString();
                                            out.println(outputString);
                                            out.flush();
                                        }
                                    }
                                    break;
                                }
                                case RemoveStore: {
                                    int StoreIdToRemove = Integer.parseInt(MessageFromClient);
                                    db.UpdateTableQuery(StoreQueries.RemoveStore(StoreIdToRemove));
                                    break;
                                }
                                case GetStore: {
                                    int StoreIdToAcquire = Integer.parseInt(MessageFromClient);
                                    ResultSet storeSet = db.SelectTableQuery(StoreQueries.GetStoreById(StoreIdToAcquire));
                                    StringBuilder storeConcatenator = new StringBuilder();
                                    while (storeSet.next()) {
                                        storeConcatenator.append(";");
                                        storeConcatenator.append(storeSet.getInt("StoreId"));
                                        storeConcatenator.append(",");
                                        storeConcatenator.append(storeSet.getString("StoreName"));
                                    }
                                    break;
                                }
                            }
                        }
                        catch (Exception ex) {
                            done = true;
                            close();
                        }
                    }
                    close();
                }
                catch(Exception ex)
                {
                }
            }
            catch(Exception ex)
            {
                stop();
                start();
            }
        }
    }

    public void start()
    {
        if (thread == null)
        {
            thread = new Thread(this);
            thread.start();
        }
    }
    public void stop()
    {
        if (thread != null)
        {
            thread.stop();
            thread = null;
        }
    }
    private void open() throws IOException
    {
        streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
    }
    private void close() throws IOException
    {
        if (socket != null)
            socket.close();
        if (streamIn != null)
            streamIn.close();
    }
}

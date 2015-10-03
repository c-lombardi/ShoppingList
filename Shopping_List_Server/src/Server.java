import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Christopher on 9/2/2015.
 */
public class Server implements Runnable {
    private ServerSocket serverSocket = null;
    private Socket socket = null;
    private Thread thread = null;
    private DataInputStream streamIn = null;

    public Server(int port) {
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
                                Item item = new Item();
                                ArrayList<Item> ItemList = item.ReadAll();
                                for (Item i : ItemList) {
                                    out.println(i.toString());
                                    out.flush();
                                }
                                break;
                            }
                            case AddItem: {
                                if (!(MessageFromClient.length() < 2)) {
                                    //Check If Already Exists
                                    Item item = new Item();
                                    item.fromString(MessageFromClient);
                                    item.Create();
                                    out.println(item.toString());
                                    out.flush();
                                }
                                break;
                            }
                            case UpdateItem: {
                                Item newItem = new Item();
                                newItem.fromString(MessageFromClient);
                                newItem.Update(false);
                                out.println(newItem.getId());
                                out.flush();
                                break;
                            }
                            case AttachStoreToItem: {
                                String[] StoreAndItemIds = MessageFromClient.split(";");
                                int ItemId = Integer.parseInt(StoreAndItemIds[0]);
                                int StoreId = Integer.parseInt(StoreAndItemIds[1]);
                                Item itemToAttachTo = new Item(ItemId, "", new Store(StoreId), (float) 0);
                                itemToAttachTo.AttachStore();
                                break;
                            }
                            case RemoveItemFromList: {
                                int ItemIdToDelete = Integer.parseInt(MessageFromClient);
                                Item item = new Item(ItemIdToDelete);
                                item.Delete(false);
                                break;
                            }
                            case RemoveItemFromLibrary: {
                                int ItemIdToDelete = Integer.parseInt(MessageFromClient);
                                Item item = new Item(ItemIdToDelete);
                                item.Delete(true);
                                break;
                            }
                            case AddStore: {
                                Store store = new Store(MessageFromClient);
                                store.Create();
                                out.println(store.toString());
                                out.flush();
                                break;
                            }
                            case RemoveStore: {
                                Store store = new Store(Integer.parseInt(MessageFromClient));
                                store.Delete(false);
                                break;
                            }
                            case GetStore: {
                                Store store = new Store(Integer.parseInt(MessageFromClient));
                                store.Read();
                                out.println(store.toString());
                                out.flush();
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

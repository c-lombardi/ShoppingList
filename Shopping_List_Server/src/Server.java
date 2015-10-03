import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

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
                        final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        final StringBuilder sb = new StringBuilder();
                        while (in.ready()) {
                            sb.append(in.readLine());
                        }
                        String MessageFromClient = sb.toString();
                        //Get the command from the client
                        final char commandCharFromClient = MessageFromClient.charAt(0);
                        final int valueOfFirstChar = Character.getNumericValue(commandCharFromClient);
                        final ByteCommand command = ByteCommand.values()[valueOfFirstChar];
                        //Parse String To items and stores
                        try {
                            MessageFromClient = MessageFromClient.substring(2); //Remove first command char and semicolon
                        } catch (Exception ex){}
                        PrintWriter out = new PrintWriter(socket.getOutputStream());
                        switch (command) {
                            case getItems: {
                                final Item item = new Item();
                                final List<Item> ItemList = item.readAll();
                                for (Item i : ItemList) {
                                    out.println(i.toString());
                                    out.flush();
                                }
                                break;
                            }
                            case addItem: {
                                if (!(MessageFromClient.length() < 2)) {
                                    //Check If Already Exists
                                    final Item item = new Item();
                                    item.fromString(MessageFromClient);
                                    item.create();
                                    out.println(item.toString());
                                    out.flush();
                                }
                                break;
                            }
                            case updateItem: {
                                final Item newItem = new Item();
                                newItem.fromString(MessageFromClient);
                                newItem.update(false);
                                out.println(newItem.getId());
                                out.flush();
                                break;
                            }
                            case attachStoreToItem: {
                                final String[] StoreAndItemIds = MessageFromClient.split(";");
                                final int ItemId = Integer.parseInt(StoreAndItemIds[0]);
                                final int StoreId = Integer.parseInt(StoreAndItemIds[1]);
                                final Item itemToAttachTo = new Item(ItemId, "", new Store(StoreId), (float) 0);
                                itemToAttachTo.attachStore();
                                break;
                            }
                            case removeItemFromList: {
                                final int ItemIdToDelete = Integer.parseInt(MessageFromClient);
                                final Item item = new Item(ItemIdToDelete);
                                item.delete(false);
                                break;
                            }
                            case removeItemFromLibrary: {
                                final int ItemIdToDelete = Integer.parseInt(MessageFromClient);
                                final Item item = new Item(ItemIdToDelete);
                                item.delete(true);
                                break;
                            }
                            case addStore: {
                                final Store store = new Store(MessageFromClient);
                                store.create();
                                out.println(store.toString());
                                out.flush();
                                break;
                            }
                            case removeStore: {
                                final Store store = new Store(Integer.parseInt(MessageFromClient));
                                store.delete(false);
                                break;
                            }
                            case getStore: {
                                final Store store = new Store(Integer.parseInt(MessageFromClient));
                                store.read();
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

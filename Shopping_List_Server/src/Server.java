import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

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
                                for (final Item.ItemBuilder i : new Item.ItemBuilder().readAll()) {
                                    out.println(i.build().toString());
                                    out.flush();
                                }
                                break;
                            }
                            case addItem: {
                                if (!(MessageFromClient.length() < 2)) {
                                    out.println(new Item.ItemBuilder().fromString(MessageFromClient).fromString(MessageFromClient).create().build().toString());
                                    out.flush();
                                }
                                break;
                            }
                            case updateItem: {
                                out.println(new Item.ItemBuilder().fromString(MessageFromClient).update(false).build().getId());
                                out.flush();
                                break;
                            }
                            case attachStoreToItem: {
                                final String[] StoreAndItemIds = MessageFromClient.split(";");
                                new Item.ItemBuilder(Integer.parseInt(StoreAndItemIds[0]), "").store(new Store.StoreBuilder(Integer.parseInt(StoreAndItemIds[1])).build()).bestPrice((float) 0).attachStore();
                                break;
                            }
                            case removeItemFromList: {
                                new Item.ItemBuilder().id(Integer.parseInt(MessageFromClient)).delete(false);
                                break;
                            }
                            case removeItemFromLibrary: {
                                new Item.ItemBuilder().id(Integer.parseInt(MessageFromClient)).delete(true);
                                break;
                            }
                            case addStore: {
                                out.println(new Store.StoreBuilder().fromString(MessageFromClient).create().build().toString());
                                out.flush();
                                break;
                            }
                            case removeStore: {
                                new Store.StoreBuilder(Integer.parseInt(MessageFromClient)).delete(false);
                                break;
                            }
                            case getStore: {
                                out.println(new Store.StoreBuilder(Integer.parseInt(MessageFromClient)).read().build().toString());
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

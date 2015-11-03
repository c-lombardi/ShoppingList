import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

/**
 * Created by Christopher on 9/2/2015.
 */
public class Server implements Runnable {
    private ServerSocket serverSocket = null;
    private Socket socket = null;
    private Thread thread = null;
    private DataInputStream streamIn = null;
    private static final Object lock = new Object();

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
            synchronized (lock)
            {
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
                            //Get the command from the client
                            final char commandCharFromClient = sb.toString().charAt(0);
                            final int valueOfFirstChar = Character.getNumericValue(commandCharFromClient);
                            final ByteCommand command = ByteCommand.values()[valueOfFirstChar];
                            final String MessageFromClient;
                            if (sb.toString().length() > 1){
                                MessageFromClient = sb.toString().substring(2);
                            } else {
                                MessageFromClient = sb.toString();
                            }
                            PrintWriter out = new PrintWriter(socket.getOutputStream());
                            switch (command) {
                                case getItems: {
                                    for (final Item.ItemBuilder i : new Item.ItemBuilder().readAll(false, UUID.fromString(MessageFromClient))) {
                                        out.println(i.build().toString());
                                        out.flush();
                                    }
                                    break;
                                }
                                case addItem: {
                                    if (!(MessageFromClient.length() < 2)) {
                                        final Item inputItem = Item.fromString(MessageFromClient);
                                        out.println(new Item.ItemBuilder(inputItem.getId(), inputItem.getName(), inputItem.getSessionId()).bestPrice(inputItem.getBestPrice()).listActive(true).store(inputItem.getStore()).libraryActive(true).create().build().toString());
                                        out.flush();
                                    }
                                    break;
                                }
                                case updateItem: {
                                    final Item inputItem = Item.fromString(MessageFromClient);
                                    out.println(new Item.ItemBuilder(inputItem.getId(), inputItem.getName(), inputItem.getSessionId()).bestPrice(inputItem.getBestPrice()).listActive(true).store(inputItem.getStore()).libraryActive(true).update(false).build().getId());
                                    out.flush();
                                    break;
                                }
                                case removeItemFromList: {
                                    new Item.ItemBuilder().id(Integer.parseInt(MessageFromClient)).delete(false);
                                    break;
                                }
                                case getLibrary: {
                                    for (final Item.ItemBuilder i : new Item.ItemBuilder().readAll(true, UUID.fromString(MessageFromClient))) {
                                        out.println(i.build().toString());
                                        out.flush();
                                    }
                                    break;
                                }
                                case reAddItems: {
                                    final String[] itemIds = MessageFromClient.split(";");
                                    final UUID sessionId = UUID.fromString(itemIds[0]);
                                    final String[] itemIdsToAdd = new String[itemIds.length - 1];
                                    for(int i = 1; i < itemIds.length; i++) {
                                        itemIdsToAdd[i-1] = itemIds[i];
                                    }
                                    for(Item.ItemBuilder ib : new Item.ItemBuilder().reAdd(itemIdsToAdd, sessionId)) {
                                        out.println(ib.build().toString());
                                        out.flush();
                                    }
                                    break;
                                } case removeItemsFromList: {
                                    new Item.ItemBuilder().removeItems(MessageFromClient.split(";"));
                                    break;
                                }
                                case getLibraryItemsThatContain: {
                                    for (final Item.ItemBuilder i : new Item.ItemBuilder().getLibraryItemsThatContain(MessageFromClient.split(";")[0])) {
                                        out.println(i.build().toString());
                                        out.flush();
                                    }
                                    break;
                                }
                                case createSession: {
                                    final String[] sessionParts = MessageFromClient.split(";");
                                    final String deviceId = sessionParts[0];
                                    final String sessionName = sessionParts[1];
                                    final Session session = new Session.SessionBuilder(null, sessionName).create().build();
                                    new SessionDevice.SessionDeviceBuilder(session.getSessionId(), deviceId).create();
                                    break;
                                }
                                case authorizeSession: {
                                    final SessionDevice sessionDevice = SessionDevice.fromString(MessageFromClient);
                                    out.println(new SessionDevice.SessionDeviceBuilder(sessionDevice.getSessionId(), sessionDevice.getDeviceId()).authorizeSession().toString());
                                    out.flush();
                                    break;
                                }
                                case grantAccessToSession: {
                                    final SessionDevice sessionDevice = SessionDevice.fromString(MessageFromClient);
                                    new SessionDevice.SessionDeviceBuilder(sessionDevice.getSessionId(), sessionDevice.getDeviceId()).create();
                                    break;
                                }
                                case getSessionsForDevice: {
                                    for (final Session.SessionBuilder s : new SessionDevice.SessionDeviceBuilder(null, MessageFromClient).getAuthorizedSessionsForDevice()) {
                                        out.println(s.build().toString());
                                        out.flush();
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
                    stop();
                    start();
                }
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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

/**
 * Created by Christopher on 9/2/2015.
 */
public class Server implements Runnable {
    private static final Object lock = new Object();
    private ServerSocket serverSocket = null;
    private Socket socket = null;
    private Thread thread = null;
    private DataInputStream streamIn = null;

    public Server(final int port) {
        try {
            System.out.println("Binding to port " + port + ", please wait  ...");
            serverSocket = new ServerSocket(port, 100, InetAddress.getLocalHost());
            start();
        } catch (IOException ignored) {
        }
    }

    @Override
    public void run() {
        while (thread != null) {
            synchronized (lock) {
                try {
                    System.out.println("waiting for a connection...");
                    socket = serverSocket.accept();
                    open();
                    System.out.println("Connected!");
                    boolean done = false;
                    thread.sleep(1000); //removed for now to see if performance is still the same.
                    while (!done) {
                        try {
                            final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            final StringBuilder sb = new StringBuilder();
                            while (in.ready()) {
                                sb.append(in.readLine());
                            }
                            final String message = sb.toString();
                            final ObjectMapper objectMapper = new ObjectMapper();
                            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
                            final Message messageObject = objectMapper.readValue(message, Message.class);
                            final Session session = messageObject.getSession();
                            final Item item = messageObject.getItem();
                            final List<Integer> itemIds = messageObject.getItemIds();
                            final ByteCommand command = messageObject.getCommand();
                            final PrintWriter out = new PrintWriter(socket.getOutputStream());
                            if (session.CheckSessionForAuthentication()) {
                                switch (command) {
                                    case getItems: {
                                        for (final Item i : new Item().readAll(false, session.getSessionId(), messageObject.getShopping_list().getShoppingListId())) {
                                            messageObject.setItem(i);
                                            out.println(objectMapper.writeValueAsString(messageObject));
                                            out.flush();
                                        }
                                        //this block is for when there are no items in the list, so the sessionId is not sent back. This occurs when a list is empty
                                        messageObject.setItem(null);
                                        out.println(objectMapper.writeValueAsString(messageObject));
                                        out.flush();
                                        //end weird block
                                        break;
                                    }
                                    case addItem: {
                                        messageObject.setItem(item.create());
                                        out.println(objectMapper.writeValueAsString(messageObject));
                                        out.flush();
                                        break;
                                    }
                                    case updateItem: {
                                        messageObject.setItem(item.update(false));
                                        out.println(objectMapper.writeValueAsString(messageObject));
                                        out.flush();
                                        break;
                                    }
                                    case removeItemFromList: {
                                        item.delete(false);
                                        break;
                                    }
                                    case getLibrary: {
                                        for (final Item i : new Item().readAll(true, session.getSessionId(), messageObject.getShopping_list().getShoppingListId())) {
                                            messageObject.setItem(i);
                                            out.println(objectMapper.writeValueAsString(messageObject));
                                            out.flush();
                                        }
                                        break;
                                    }
                                    case reAddItems: {
                                        for (Item i : new Item().reAdd(itemIds, messageObject.getShopping_list().getShoppingListId())) {
                                            messageObject.setItem(i);
                                            out.println(objectMapper.writeValueAsString(messageObject));
                                            out.flush();
                                        }
                                        break;
                                    }
                                    case removeItemsFromList: {
                                        new Item().removeItems(itemIds);
                                        break;
                                    }
                                    case getLibraryItemsThatContain: {
                                        for (final Item i : item.getLibraryItemsThatContain(item.getName())) {
                                            messageObject.setItem(i);
                                            out.println(objectMapper.writeValueAsString(messageObject));
                                            out.flush();
                                        }
                                        break;
                                    }
                                    case createShoppingList: {
                                        messageObject.getShopping_list().create();
                                        out.println(objectMapper.writeValueAsString(messageObject));
                                        out.flush();
                                        break;
                                    }
                                    case renameShoppingList: {
                                        messageObject.getShopping_list().update(false);
                                        out.println(objectMapper.writeValueAsString(messageObject));
                                        out.flush();
                                        break;
                                    }
                                    case getListOfShoppingLists: {
                                        for (final Shopping_List sl : new Shopping_List().readAll(messageObject.getSession().getSessionId())){
                                            messageObject.setShopping_list(sl);
                                            out.println(objectMapper.writeValueAsString(messageObject));
                                            out.flush();
                                        }
                                        messageObject.setShopping_list(null);
                                        out.println(objectMapper.writeValueAsString(messageObject));
                                        out.flush();
                                        break;
                                    }
                                }
                            } else {
                                if(messageObject.getCommand().equals(ByteCommand.requestNewAuthCode)) {
                                    messageObject.getSession().updateAuthCode();
                                }
                                session.create();
                                messageObject.setSession(session);
                                String outputValue = objectMapper.writeValueAsString(messageObject);
                                out.println(outputValue);
                                out.flush();
                            }
                        } catch (Exception ex) {
                            done = true;
                            close();
                        }
                    }
                    close();
                } catch (Exception ex) {
                    stop();
                    start();
                }
            }
        }
    }

    public void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public void stop() {
        if (thread != null) {
            thread = null;
        }
    }

    private void open() throws IOException {
        streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
    }

    private void close() throws IOException {
        if (socket != null)
            socket.close();
        if (streamIn != null)
            streamIn.close();
    }
}

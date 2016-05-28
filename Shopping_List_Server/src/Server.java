import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christopher on 9/2/2015.
 */
public class Server {
    private static final com.esotericsoftware.kryonet.Server serverSocket = new com.esotericsoftware.kryonet.Server();
    private static final Listener listener = new Listener() {
        public void received(final Connection connection, final Object object) {
            if (object instanceof Message) {
                try {
                    final Message messageObject = (Message) object;
                    final Session session = messageObject.getSession();
                    final Item item = messageObject.getItem();
                    final List<Item> items = messageObject.getItems();
                    final ShoppingList shoppingList = messageObject.getShopping_list();
                    final ByteCommand command = messageObject.getCommand();
                    if (session.CheckSessionForAuthentication()) {
                        switch (command) {
                            case getItems: {
                                for (final Item i : new Item().readAll(false, session.getSessionId(), shoppingList.getShoppingListId())) {
                                    messageObject.setItem(i);
                                    connection.sendTCP(messageObject);
                                }
                                //this block is for when there are no items in the list, so the sessionId is not sent back. This occurs when a list is empty
                                messageObject.setItem(null);
                                connection.sendTCP(messageObject);
                                //end weird block
                                break;
                            }
                            case addItem: {
                                messageObject.setItem(item.create());
                                connection.sendTCP(messageObject);
                                break;
                            }
                            case updateItem: {
                                messageObject.setItem(item.update(false));
                                connection.sendTCP(messageObject);
                                break;
                            }
                            case removeItemFromList: {
                                item.delete(false);
                                connection.sendTCP(messageObject);
                                break;
                            }
                            case getLibrary: {
                                for (final Item i : new Item().readAll(true, session.getSessionId(), messageObject.getShopping_list().getShoppingListId())) {
                                    messageObject.setItem(i);
                                    connection.sendTCP(messageObject);
                                }
                                break;
                            }
                            case reAddItems: {
                                for (Item i : new Item().reAdd(items, messageObject.getShopping_list().getShoppingListId())) {
                                    messageObject.setItem(i);
                                    connection.sendTCP(messageObject);
                                }
                                break;
                            }
                            case removeItemsFromList: {
                                new Item().removeItems(items, messageObject.getShopping_list().getShoppingListId());
                                connection.sendTCP(messageObject);
                                break;
                            }
                            case getLibraryItemsThatContain: {
                                for (final Item i : item.getLibraryItemsThatContain(item.getName(), messageObject.getShopping_list().getShoppingListId())) {
                                    messageObject.setItem(i);
                                    connection.sendTCP(messageObject);
                                }
                                break;
                            }
                            case createShoppingList: {
                                messageObject.getShopping_list().create();
                                connection.sendTCP(messageObject);
                                break;
                            }
                            case renameShoppingList: {
                                messageObject.getShopping_list().update(false);
                                connection.sendTCP(messageObject);
                                break;
                            }
                            case removeShoppingList: {
                                messageObject.getShopping_list().delete(false);
                                connection.sendTCP(messageObject);
                                break;
                            }
                            case getListOfShoppingLists: {
                                final List<ShoppingList> shoppingLists = new ShoppingList().readAll(messageObject.getSession().getSessionId());
                                for (final ShoppingList sl : shoppingLists) {
                                    messageObject.setShopping_list(sl);
                                    connection.sendTCP(messageObject);
                                }
                                if(shoppingLists.isEmpty()) {
                                    connection.sendTCP(messageObject);
                                }
                                break;
                            }
                            case updateItemStatus: {
                                messageObject.getItem().updateItemStatus();
                                connection.sendTCP(messageObject.getItem().read());
                                break;
                            }
                        }
                    } else {
                        if (messageObject.getCommand().equals(ByteCommand.requestNewAuthCode)) {
                            messageObject.getSession().updateAuthCode();
                        }
                        session.create();
                        messageObject.setSession(session);
                        connection.sendTCP(messageObject);
                    }
                    connection.close();
                } catch (Exception ex) {
                    System.out.println(ex);
                    connection.close();
                }
            }
        }
    };

    public Server(final int port) {
        try {
            final Kryo kryo = serverSocket.getKryo();
            kryo.register(Message.class);
            kryo.register(ByteCommand.class);
            kryo.register(Item.class);
            kryo.register(Session.class);
            kryo.register(Store.class);
            kryo.register(ShoppingList.class);
            kryo.register(ArrayList.class);
            kryo.register(ItemStatus.class);
            serverSocket.bind(port);
            serverSocket.addListener(new Listener.QueuedListener(listener) {
                @Override
                protected void queue(Runnable runnable) {
                    runnable.run();
                }
            });
            serverSocket.start();
        } catch (IOException ignored) {
            serverSocket.stop();
        }
    }
}

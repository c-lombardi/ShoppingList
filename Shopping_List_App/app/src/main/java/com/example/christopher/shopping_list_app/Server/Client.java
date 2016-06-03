package com.example.christopher.shopping_list_app.Server;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.example.christopher.shopping_list_app.ItemsListFragment;
import com.example.christopher.shopping_list_app.Models.Item;
import com.example.christopher.shopping_list_app.Models.ItemStatus;
import com.example.christopher.shopping_list_app.Models.ItemStatusHelper;
import com.example.christopher.shopping_list_app.Models.Session;
import com.example.christopher.shopping_list_app.Models.ShoppingList;
import com.example.christopher.shopping_list_app.Models.Store;
import com.example.christopher.shopping_list_app.ShoppingListFragment;
import com.example.christopher.shopping_list_app.Shopping_List;
import com.example.christopher.shopping_list_app.ViewModels.Message;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christopher on 9/10/2015.
 */
public class Client extends AsyncTask<String, Client, String> {
    private static Message message;
    private static String ipAddress;
    private static Fragment fragment;
    private static boolean isLibrary;
    private static String response;
    private static Boolean finished = true;
    private static final com.esotericsoftware.kryonet.Client client = new com.esotericsoftware.kryonet.Client();
    private static final Kryo kryo = client.getKryo();
    private static final Listener listener = new Listener(){
        public void connected(Connection connection) {
            client.sendTCP(message);
        }

        public void received (Connection connection, Object object) {
            if(object instanceof Message) {
                message = (Message) object;
                switch (message.getCommand()) {
                    case getItems: {
                        ItemsListFragment.getItems_CRUD().addToOrUpdateArrayList(message.getItem());
                        break;
                    }
                    case addItem: {
                        ItemsListFragment.getItems_CRUD().addToOrUpdateArrayList(message.getItem());
                        break;
                    }
                    case updateItem: {
                        ItemsListFragment.getItems_CRUD().addToOrUpdateArrayList(message.getItem());
                        break;
                    }
                    case removeItemFromList: {
                        ItemsListFragment.getItems_CRUD().removeFromArrayList(message.getItem());
                        break;
                    }
                    case getLibrary: {
                        ItemsListFragment.getLibraryItems_CRUD().addToOrUpdateArrayList(message.getItem());
                        break;
                    }
                    case reAddItems: {
                        ItemsListFragment.getItems_CRUD().addToOrUpdateArrayList(message.getItem());
                        break;
                    }
                    case removeItemsFromList: {
                        ItemsListFragment.getItems_CRUD().removeItemsFromItemArrayList(message.getItems());
                        break;
                    }
                    case getLibraryItemsThatContain: {
                        ItemsListFragment.getLibraryItems_CRUD().addToOrUpdateArrayList(message.getItem());
                        break;
                    }
                    case createShoppingList: {
                        ShoppingListFragment.AddToOrReplaceShoppingListArrayList(message.getShopping_list());
                        break;
                    }
                    case renameShoppingList: {
                        ShoppingListFragment.AddToOrReplaceShoppingListArrayList(message.getShopping_list());
                        break;
                    }
                    case removeShoppingList: {
                        ShoppingListFragment.RemoveFromShoppingListArrayList(message.getShopping_list());
                        break;
                    }
                    case getListOfShoppingLists: {
                        ShoppingListFragment.AddToOrReplaceShoppingListArrayList(message.getShopping_list());
                        break;
                    }
                    case updateItemStatus: {
                        ItemsListFragment.getItems_CRUD().addToOrUpdateArrayList(message.getItem());
                        break;
                    }
                }
                if(!message.getCommand().equals(ByteCommand.updateItemStatus)) {
                    response = ByteCommandHelper.ByteCommandStringValue(message.getCommand());
                } else {
                    response = ItemStatusHelper.ItemStatusHelperStringValue(message.getItem().getItemStatus());
                }
            }
        }

        public void disconnected(final Connection connection) {
            finished = true;
        }
    };

    private Client(final ClientBuilder clientBuilder) throws IOException {
        message = new Message();
        message.setItem(clientBuilder.item);
        message.setCommand(clientBuilder.command);
        message.setItems(clientBuilder.itemIds);
        message.setShopping_list(Shopping_List.getActiveShopping_List());
        message.setSession(Shopping_List.getSession());
        ipAddress = clientBuilder.ipAddress;
        fragment = clientBuilder.fragment;
        isLibrary = clientBuilder.command == ByteCommand.getLibrary ||
                clientBuilder.command == ByteCommand.getLibraryItemsThatContain;
        kryo.register(Message.class);
        kryo.register(ByteCommand.class);
        kryo.register(Item.class);
        kryo.register(Session.class);
        kryo.register(Store.class);
        kryo.register(ShoppingList.class);
        kryo.register(ArrayList.class);
        kryo.register(ItemStatus.class);
        client.addListener(listener);
        client.start();
    }

    @Override
    protected void onPreExecute() {
        if(!isLibrary && message.getCommand().equals(ByteCommand.getItems)) {
            ItemsListFragment.getItems_CRUD().setSwipeRefreshlayoutRefreshing(true);
        }
    }

    @Override
    protected String doInBackground(final String... params) {
        try {
            if(message.getCommand().equals(ByteCommand.getListOfShoppingLists)){
                ShoppingListFragment.ClearShoppingListArrayList();
            }
            if(message.getCommand().equals(ByteCommand.getItems)){
                ItemsListFragment.getItems_CRUD().ClearArrayList();
            }
            finished = false;
            client.connect(5000, ipAddress, 5297);
            while(!finished) {
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onPostExecute(final String result) {
        client.close();
        if(fragment != null) {
            if (fragment.getClass().equals(ItemsListFragment.class)) {
                if(isLibrary) {
                    ItemsListFragment.getLibraryItems_CRUD().NotifyAdapterThatItemLibraryListChanged();
                    ItemsListFragment.UpdateAlertDialogEnabled(true);
                } else {
                    ItemsListFragment.getItems_CRUD().NotifyAdapterThatItemListChanged();
                }
                ItemsListFragment.getItems_CRUD().setSwipeRefreshlayoutRefreshing(false);
                //Toast.makeText(fragment.getContext(), response, Toast.LENGTH_SHORT).show();
                ItemsListFragment.getItems_CRUD().handleDeleteGreenItemsButton();

            } else if (fragment.getClass().equals(ShoppingListFragment.class)) {
                ShoppingListFragment.getShopping_list_crud().NotifyArrayListAdapterShoppingListChanged();
                //Toast.makeText(fragment.getContext(), response, Toast.LENGTH_SHORT).show();
            }
            Shopping_List.setActiveShopping_List(message.getShopping_list());
            Shopping_List.setSession(message.getSession().getSessionId(), fragment.getActivity());
            Shopping_List.DisplayToast(response, fragment.getActivity());
        }
    }

    public static class ClientBuilder {
        private final ByteCommand command;
        private final String ipAddress;
        private final Fragment fragment;
        private Item item;
        private List<Item> itemIds;

        public ClientBuilder(final ByteCommand cmd, final String ip, final Fragment f) {
            command = cmd;
            ipAddress = ip;
            fragment = f;
        }

        public ClientBuilder Items(final List<Item> i) {
            itemIds = i;
            return this;
        }

        public ClientBuilder Item(final Item i) {
            item = i;
            return this;
        }

        public Client build() {
            try {
                return new Client(this);
            } catch (IOException ignored) {
            }
            return null;
        }
    }
}

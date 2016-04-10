package com.example.christopher.shopping_list_app.Server;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.example.christopher.shopping_list_app.ItemsListFragment;
import com.example.christopher.shopping_list_app.Models.Item;
import com.example.christopher.shopping_list_app.ShoppingListFragment;
import com.example.christopher.shopping_list_app.Shopping_List;
import com.example.christopher.shopping_list_app.ViewModels.Message;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;

/**
 * Created by Christopher on 9/10/2015.
 */
public class Client extends AsyncTask<String, Client, String> {
    private final ObjectMapper objectMapper;
    private Message message;
    private Socket socket;
    private String ipAddress;
    private PrintWriter out;
    private BufferedReader in;
    private Fragment fragment;
    private static boolean isLibrary;

    private Client(final ClientBuilder clientBuilder) throws IOException {
        message = new Message();
        message.setItem(clientBuilder.item);
        message.setCommand(clientBuilder.command);
        message.setItemIds(clientBuilder.itemIds);
        message.setShopping_list(Shopping_List.getActiveShopping_List());
        message.setSession(Shopping_List.getSession());
        objectMapper = new ObjectMapper();
        ipAddress = clientBuilder.ipAddress;
        fragment = clientBuilder.fragment;
        isLibrary = clientBuilder.command == ByteCommand.getLibrary || clientBuilder.command == ByteCommand.getLibraryItemsThatContain || clientBuilder.command == ByteCommand.addItem;
    }

    @Override
    protected void onPreExecute() {
        try {
            if(!isLibrary) {
                ItemsListFragment.getItems_CRUD().setSwipeRefreshlayoutRefreshing(true);
            }
        } catch (final Exception ex) {
        }
    }

    @Override
    protected String doInBackground(final String... params) {
        boolean done = false;
        while (!done) {
            try {
                socket = new Socket(ipAddress, 5297);
                socket.setSoTimeout(300000);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final String messageString = objectMapper.writeValueAsString(message);
                out.println(messageString);
                out.flush();
                switch (message.getCommand()) {
                    case getItems:
                        String itemString;
                    {
                        ItemsListFragment.getItems_CRUD().ClearItemArrayList();
                        while ((itemString = in.readLine()) != null) {
                            message = objectMapper.readValue(itemString, Message.class);
                            ItemsListFragment.getItems_CRUD().addToArrayList(message.getItem());
                        }
                        break;
                    }
                    case addItem: {
                        while ((itemString = in.readLine()) != null) {
                            message = objectMapper.readValue(itemString, Message.class);
                            ItemsListFragment.getItems_CRUD().addToArrayList(message.getItem());
                        }
                        break;
                    }
                    case updateItem: {
                        while ((itemString = in.readLine()) != null) {
                            message = objectMapper.readValue(itemString, Message.class);
                            ItemsListFragment.getItems_CRUD().addToArrayList(message.getItem());
                        }
                        break;
                    }
                    case removeItemFromList: {
                        ItemsListFragment.getItems_CRUD().removeFromArrayList(message.getItem());
                        break;
                    }
                    case getLibrary: {
                        while ((itemString = in.readLine()) != null) {
                            message = objectMapper.readValue(itemString, Message.class);
                            ItemsListFragment.getLibraryItems_CRUD().addToArrayList(message.getItem());
                        }
                        break;
                    }
                    case reAddItems: {
                        isLibrary = true;
                        while ((itemString = in.readLine()) != null) {
                            message = objectMapper.readValue(itemString, Message.class);
                            ItemsListFragment.getItems_CRUD().addToArrayList(message.getItem());
                        }
                        break;
                    }
                    case removeItemsFromList: {
                        ItemsListFragment.getItems_CRUD().removeItemsFromItemArrayList(message.getItemIds());
                        break;
                    }
                    case getLibraryItemsThatContain: {
                        while ((itemString = in.readLine()) != null) {
                            message = objectMapper.readValue(itemString, Message.class);
                            ItemsListFragment.getLibraryItems_CRUD().addToArrayList(message.getItem());
                        }
                        break;
                    }
                    case createShoppingList: {
                        while ((itemString = in.readLine()) != null) {
                            message = objectMapper.readValue(itemString, Message.class);
                            ShoppingListFragment.AddToOrReplaceShoppingListArrayList(message.getShopping_list());
                        }
                        break;
                    }
                    case renameShoppingList: {
                        while ((itemString = in.readLine()) != null) {
                            message = objectMapper.readValue(itemString, Message.class);
                            ShoppingListFragment.AddToOrReplaceShoppingListArrayList(message.getShopping_list());
                        }
                        break;
                    }
                    case getListOfShoppingLists: {
                        while ((itemString = in.readLine()) != null) {
                            message = objectMapper.readValue(itemString, Message.class);
                            ShoppingListFragment.AddToOrReplaceShoppingListArrayList(message.getShopping_list());
                        }
                        break;
                    }
                }
                out.flush();
                out.close();
                in.close();
                done = true;
            } catch (final SocketTimeoutException ex) {
                return "Timed Out";
            } catch (final Exception ex) {
                try {
                    in.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
                out.close();
                try {
                    socket.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
                done = true;
            }
        }
        return ByteCommandHelper.ByteCommandStringValue(message.getCommand());
    }

    @Override
    public void onPostExecute(final String str) {
        if(fragment != null) {
            if (fragment.getClass().equals(ItemsListFragment.class)) {
                if(isLibrary) {
                    ItemsListFragment.getLibraryItems_CRUD().NotifyAdapterThatItemLibraryListChanged();
                    ItemsListFragment.UpdateAlertDialogEnabled(true);
                } else {
                    ItemsListFragment.getItems_CRUD().NotifyAdapterThatItemListChanged();
                }
                ItemsListFragment.getItems_CRUD().setSwipeRefreshlayoutRefreshing(false);
                Toast.makeText(fragment.getContext(), str, Toast.LENGTH_SHORT).show();
                Shopping_List.setSession(message.getSession().getSessionId(), fragment.getActivity());
                ItemsListFragment.getItems_CRUD().handleDeleteGreenItemsButton();

            } else if (fragment.getClass().equals(ShoppingListFragment.class)) {
                ShoppingListFragment.getShopping_list_crud().NotifyArrayListAdapterShoppingListChanged();
                Toast.makeText(fragment.getContext(), str, Toast.LENGTH_SHORT).show();
                Shopping_List.setSession(message.getSession().getSessionId(), fragment.getActivity());
            }
            Shopping_List.DisplayToast(str, fragment.getActivity());
        }
    }

    public static class ClientBuilder {
        private final ByteCommand command;
        private final String ipAddress;
        private final Fragment fragment;
        private Item item;
        private List<Integer> itemIds;

        public ClientBuilder(final ByteCommand cmd, final String ip, final Fragment f) {
            command = cmd;
            ipAddress = ip;
            fragment = f;
        }

        public ClientBuilder ItemIds(final List<Integer> i) {
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

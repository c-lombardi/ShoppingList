package com.example.christopher.shopping_list.Server;

import android.os.AsyncTask;

import com.example.christopher.shopping_list.Models.Item;
import com.example.christopher.shopping_list.Models.Session;
import com.example.christopher.shopping_list.Models.Shopping_List;
import com.example.christopher.shopping_list.Shopping_List_App;
import com.example.christopher.shopping_list.ViewModels.Message;
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
    private Shopping_List_App shoppingListApp;
    private PrintWriter out;
    private BufferedReader in;

    private Client(final ClientBuilder clientBuilder) throws IOException {
        message = new Message();
        message.setSession(clientBuilder.session);
        message.setItem(clientBuilder.item);
        message.setCommand(clientBuilder.command);
        message.setItemIds(clientBuilder.itemIds);
        message.setShopping_list(clientBuilder.shopping_List);
        objectMapper = new ObjectMapper();
        ipAddress = clientBuilder.ipAddress;
        shoppingListApp = clientBuilder.shopping_listApp;
    }

    @Override
    protected void onPreExecute() {
        try {
            shoppingListApp.getItems_CRUD().setSwipeRefreshlayoutRefreshing(true);
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
                        Shopping_List_App.getItems_CRUD().ClearItemArrayList();
                        while ((itemString = in.readLine()) != null) {
                            message = objectMapper.readValue(itemString, Message.class);
                            shoppingListApp.getItems_CRUD().addToArrayList(message.getItem());
                        }
                        break;
                    }
                    case addItem: {
                        while ((itemString = in.readLine()) != null) {
                            message = objectMapper.readValue(itemString, Message.class);
                            shoppingListApp.getItems_CRUD().addToArrayList(message.getItem());
                        }
                        break;
                    }
                    case updateItem: {
                        while ((itemString = in.readLine()) != null) {
                            message = objectMapper.readValue(itemString, Message.class);
                            shoppingListApp.getItems_CRUD().addToArrayList(message.getItem());
                        }
                        break;
                    }
                    case removeItemFromList: {
                        shoppingListApp.getItems_CRUD().removeFromArrayList(message.getItem());
                        break;
                    }
                    case getLibrary: {
                        while ((itemString = in.readLine()) != null) {
                            message = objectMapper.readValue(itemString, Message.class);
                            shoppingListApp.getLibraryItem_crud().addToArrayList(message.getItem());
                        }
                        break;
                    }
                    case reAddItems: {
                        while ((itemString = in.readLine()) != null) {
                            message = objectMapper.readValue(itemString, Message.class);
                            shoppingListApp.getItems_CRUD().addToArrayList(message.getItem());
                        }
                        break;
                    }
                    case removeItemsFromList: {
                        shoppingListApp.getItems_CRUD().removeItemsFromItemArrayList(message.getItemIds());
                        break;
                    }
                    case getLibraryItemsThatContain: {
                        while ((itemString = in.readLine()) != null) {
                            message = objectMapper.readValue(itemString, Message.class);
                            shoppingListApp.getLibraryItem_crud().addToArrayList(message.getItem());
                        }
                        break;
                    }
                    case createShoppingList: {
                        while ((itemString = in.readLine()) != null) {
                            message = objectMapper.readValue(itemString, Message.class);
                            Shopping_List_App.AddToOrReplaceShoppingListArrayList(message.getShopping_list());
                        }
                        break;
                    }
                    case renameShoppingList: {
                        while ((itemString = in.readLine()) != null) {
                            message = objectMapper.readValue(itemString, Message.class);
                            Shopping_List_App.AddToOrReplaceShoppingListArrayList(message.getShopping_list());
                        }
                        break;
                    }
                    case getListOfShoppingLists: {
                        while ((itemString = in.readLine()) != null) {
                            message = objectMapper.readValue(itemString, Message.class);
                            Shopping_List_App.AddToOrReplaceShoppingListArrayList(message.getShopping_list());
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
        if (shoppingListApp != null) {
            shoppingListApp.getItems_CRUD().setSwipeRefreshlayoutRefreshing(false);
            shoppingListApp.DisplayToast(str);
            shoppingListApp.getItems_CRUD().NotifyAdapterThatItemListChanged();
            shoppingListApp.getLibraryItem_crud().NotifyAdapterThatItemLibraryListChanged();
            shoppingListApp.getItems_CRUD().handleDeleteGreenItemsButton();
            shoppingListApp.setSession(message.getSession().getSessionId());
        }
    }

    public static class ClientBuilder {
        private final ByteCommand command;
        private final String ipAddress;
        private final Shopping_List_App shopping_listApp;
        private final Session session;
        private Item item;
        private List<Integer> itemIds;
        private final Shopping_List shopping_List;

        public ClientBuilder(final ByteCommand cmd, final String ip, final Session s, final Shopping_List sl, final Shopping_List_App sla) {
            command = cmd;
            ipAddress = ip;
            session = s;
            shopping_listApp = sla;
            shopping_List = sl;
        }

        public ClientBuilder ItemIds(final List<Integer> i) {
            itemIds = i;
            return this;
        }

        public ClientBuilder Item(final Item i) {
            item = i;
            return this;
        }

        public Client build() throws IOException {
            return new Client(this);
        }
    }
}

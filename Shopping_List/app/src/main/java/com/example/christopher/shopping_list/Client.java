package com.example.christopher.shopping_list;

import android.os.AsyncTask;

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
    private Shopping_List shoppingList;
    private PrintWriter out;
    private BufferedReader in;

    private Client(final ClientBuilder clientBuilder) throws IOException {
        message = new Message();
        message.setSession(clientBuilder.session);
        message.setItem(clientBuilder.item);
        message.setCommand(clientBuilder.command);
        message.setItemIds(clientBuilder.itemIds);
        objectMapper = new ObjectMapper();
        ipAddress = clientBuilder.ipAddress;
        shoppingList = clientBuilder.shopping_list;
    }

    @Override
    protected void onPreExecute() {
        try {
            shoppingList.setSwipeRefreshlayoutRefreshing(true);
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
                out.println(objectMapper.writeValueAsString(message));
                out.flush();
                switch (message.getCommand()) {
                    case getItems:
                        String itemString;
                    {
                        Shopping_List.ClearItemArrayList();
                        while ((itemString = in.readLine()) != null) {
                            message = objectMapper.readValue(itemString, Message.class);
                            Shopping_List.AddToItemArrayList(message.getItem());
                        }
                        break;
                    }
                    case addItem: {
                        while ((itemString = in.readLine()) != null) {
                            message = objectMapper.readValue(itemString, Message.class);
                            Shopping_List.AddToItemArrayList(message.getItem());
                        }
                        break;
                    }
                    case updateItem: {
                        while ((itemString = in.readLine()) != null) {
                            message = objectMapper.readValue(itemString, Message.class);
                            Shopping_List.AddToItemArrayList(message.getItem());
                        }
                        break;
                    }
                    case removeItemFromList: {
                        Shopping_List.removeItemFromItemArrayList(message.getItem());
                        break;
                    }
                    case getLibrary: {
                        while ((itemString = in.readLine()) != null) {
                            message = objectMapper.readValue(itemString, Message.class);
                            Shopping_List.AddToLibraryItemArrayList(message.getItem());
                        }
                        break;
                    }
                    case reAddItems: {
                        while ((itemString = in.readLine()) != null) {
                            message = objectMapper.readValue(itemString, Message.class);
                            Shopping_List.AddToItemArrayList(message.getItem());
                        }
                        break;
                    }
                    case removeItemsFromList: {
                        shoppingList.removeItemsFromItemArrayList(message.getItemIds());
                        break;
                    }
                    case getLibraryItemsThatContain: {
                        while ((itemString = in.readLine()) != null) {
                            message = objectMapper.readValue(itemString, Message.class);
                            Shopping_List.AddToLibraryItemArrayList(message.getItem());
                        }
                        break;
                    }
                }
                out.flush();
                in.close();
                out.close();
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
        if (shoppingList != null) {
            shoppingList.setSwipeRefreshlayoutRefreshing(false);
            shoppingList.DisplayToast(str);
            shoppingList.NotifyAdapterThatItemListChanged();
            shoppingList.NotifyAdapterThatItemLibraryListChanged();
            shoppingList.handleDeleteGreenItemsButton();
            shoppingList.setSession(message.getSession().getSessionId());
        }
    }

    public static class ClientBuilder {
        private final ByteCommand command;
        private final String ipAddress;
        private final Shopping_List shopping_list;
        private final Session session;
        private Item item;
        private List<Integer> itemIds;

        public ClientBuilder(final ByteCommand cmd, final String ip, final Session s, final Shopping_List sl) {
            command = cmd;
            ipAddress = ip;
            session = s;
            shopping_list = sl;
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

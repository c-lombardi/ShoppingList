package com.example.christopher.shopping_list;

import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.Button;
import android.widget.ListView;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;

/**
 * Created by Christopher on 9/10/2015.
 */
public class Client extends AsyncTask<String, Client, String> {
    private final ByteCommand command;
    private Socket socket;
    private final Item item;
    private List<Object> itemIds;
    private final ListView listView;
    private final Shopping_List shoppingList;
    private final SwipeRefreshLayout swipeLayout;
    private final String ipAddress;
    private final Button deleteItemsButton;

    private Client(ClientBuilder clientBuilder)
    {
        listView = clientBuilder.listView;
        swipeLayout = clientBuilder.swipeLayout;
        item = clientBuilder.item;
        itemIds = clientBuilder.itemIds;
        shoppingList = new Shopping_List();
        command = clientBuilder.command;
        ipAddress = clientBuilder.ipAddress;
        deleteItemsButton = clientBuilder.deleteItemsButton;
    }
    private String CreateOutLineOfList(List<Object> objects){
        final StringBuilder sb = new StringBuilder();
        for(int i = 0; i < objects.size(); i++) {
            sb.append(objects.get(i).toString());
            if(i != objects.size() - 1) {
                sb.append(";");
            } else {
                sb.append("\r\n");
            }
        }
        return sb.toString();
    }
    private String CreateOutLine(Object ... objects){
        final StringBuilder sb = new StringBuilder();
        for(int i = 0; i < objects.length; i++)
        {
            sb.append(objects[i].toString());
            if(i != objects.length - 1) {
                sb.append(";");
            } else {
                sb.append("\r\n");
            }
        }
        return sb.toString();
    }
    @Override
    protected void onPreExecute(){
        try {
            if (listView != null)
                listView.setEnabled(false);
            if (swipeLayout != null)
                swipeLayout.setRefreshing(true);
        } catch (Exception ex){}
    }
    @Override
    protected String doInBackground(String... params) {
        boolean done = false;
        while (!done) {
            try {
                socket = new Socket(ipAddress, 5297);
                socket.setSoTimeout(300000);
                final PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                switch (command) {
                    case getItems: {
                        out.println(CreateOutLine(Integer.toString(command.ordinal())));
                        out.flush();
                        shoppingList.ClearItemArrayList();
                        String itemString;
                        while ((itemString = in.readLine()) != null) {
                            if (itemString.contains(",")) {
                                shoppingList.AddToItemArrayList(Item.fromString(itemString));
                            }
                        }
                        break;
                    }
                    case addItem: {
                        try {
                            out.println(CreateOutLine(Integer.toString(command.ordinal()), item));
                            out.flush();
                            String line;
                            while ((line = in.readLine()) != null) {
                                shoppingList.AddToItemArrayList(Item.fromString(line));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case updateItem: {
                        out.println(CreateOutLine(Integer.toString(command.ordinal()), item));
                        out.flush();
                        String line;
                        while ((line = in.readLine()) != null) {
                            shoppingList.AddToItemArrayList(new Item.ItemBuilder(Integer.parseInt(line), item.getName()).bestPrice(item.getBestPrice()).store(item.getStore()).build());
                        }
                        break;
                    }
                    case removeItemFromList: {
                        out.println(CreateOutLine(Integer.toString(command.ordinal()), item.getId()));
                        out.flush();
                        shoppingList.removeItemFromItemArrayList(item);
                        break;
                    }
                    case getLibrary: {
                        out.println(CreateOutLine(Integer.toString(command.ordinal())));
                        out.flush();
                        String line;
                        while ((line = in.readLine()) != null) {
                            String[] itemParts = line.split(",");
                            shoppingList.AddToLibraryItemArrayList(new Item.ItemBuilder(Integer.parseInt(itemParts[0]), itemParts[1]).build());
                        }
                        break;
                    } case reAddItems: {
                        out.println(CreateOutLine(Integer.toString(command.ordinal()), CreateOutLineOfList(itemIds)));
                        out.flush();
                        String itemString;
                        while ((itemString = in.readLine()) != null) {
                            if (itemString.contains(",")) {
                                shoppingList.AddToItemArrayList(Item.fromString(itemString));
                            }
                        }
                        break;
                    } case removeItemsFromList: {
                        out.println(CreateOutLine(Integer.toString(command.ordinal()), CreateOutLineOfList(itemIds)));
                        out.flush();
                        shoppingList.removeItemsFromItemArrayList(itemIds);
                        break;
                    }
                }
                in.close();
                out.close();
                done = true;
            }
            catch (SocketTimeoutException ex)
            {
                return "Timed Out";
            }
            catch (Exception ex) {
                done = true;
            }
        }
        return null;
    }

    @Override
    public void onPostExecute(String str){
        if(listView != null)
            listView.setEnabled(true);
        if(swipeLayout != null)
            swipeLayout.setRefreshing(false);
        shoppingList.DisplayToast(str);
        shoppingList.NotifyAdapterThatItemListChanged();
        shoppingList.NotifyAdapterThatItemLibraryListChanged();
        shoppingList.handleDeleteBoughtItemsButton(deleteItemsButton, swipeLayout);
    }

    public static class ClientBuilder {
        private final ByteCommand command;
        private Item item;
        private List<Object> itemIds;
        private final ListView listView;
        private final SwipeRefreshLayout swipeLayout;
        private final String ipAddress;
        private Button deleteItemsButton;

        public ClientBuilder(ByteCommand cmd, ListView lv, SwipeRefreshLayout srl, Item i, String ip)
        {
            listView = lv;
            swipeLayout = srl;
            item = i;
            command = cmd;
            ipAddress = ip;
        }
        public ClientBuilder(ByteCommand cmd, ListView lv, SwipeRefreshLayout srl, List<Object> i, Button delButton, String ip)
        {
            listView = lv;
            swipeLayout = srl;
            itemIds = i;
            command = cmd;
            ipAddress = ip;
            deleteItemsButton = delButton;
        }
        public ClientBuilder(ByteCommand cmd, ListView lv, SwipeRefreshLayout srl, String ip)
        {
            listView = lv;
            swipeLayout = srl;
            command = cmd;
            ipAddress = ip;
        }

        public Client build() {
            return new Client(this);
        }
    }
}

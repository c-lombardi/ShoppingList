package com.example.christopher.shopping_list;

import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ListView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by Christopher on 9/10/2015.
 */
public class Client extends AsyncTask<String, Client, String> {
    private ByteCommand command;
    private Socket socket = null;
    private Store store;
    private Item item;
    private ListView listView;
    private Shopping_List shoppingList;
    private SwipeRefreshLayout swipeLayout;
    private String IpAddress;
    public Client(ByteCommand cmd, ListView lv, SwipeRefreshLayout srl, Item i, String ip)
    {
        listView = lv;
        swipeLayout = srl;
        item = i;
        shoppingList = new Shopping_List();
        command = cmd;
        IpAddress = ip;
    }
    public Client(ByteCommand cmd, ListView lv, SwipeRefreshLayout srl, String ip)
    {
        listView = lv;
        swipeLayout = srl;
        shoppingList = new Shopping_List();
        command = cmd;
        IpAddress = ip;
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
                socket = new Socket(IpAddress, 5297);
                socket.setSoTimeout(300000);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                switch (command) {
                    case GetItems: {
                        out.println(Integer.toString(ByteCommand.GetItems.ordinal()) + "\r\n");
                        out.flush();
                        String itemString;
                        while ((itemString = in.readLine()) != null) {
                            if (itemString.contains(","))
                                shoppingList.AddToItemArrayList(new Item(itemString));
                        }
                        break;
                    }
                    case AddItem: {
                        try {
                            //Add Item
                            out.println(Integer.toString(ByteCommand.AddItem.ordinal()) + ";" + item.getName() + "\r\n");
                            out.flush();
                            String line;
                            StringBuilder sb = new StringBuilder();
                            while ((line = in.readLine()) != null) {
                                sb.append(line);
                            }
                            String itemString = sb.toString();
                            //Set Item
                            if(itemString.contains(",")) {
                                if(item.getStore() != null)
                                {
                                    socket = new Socket(IpAddress, 5297);
                                    out = new PrintWriter(socket.getOutputStream(), true);
                                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                                    sb = new StringBuilder();
                                    //add store
                                    out.println(Integer.toString(ByteCommand.AddStore.ordinal()) + ";" + item.getStore().getStoreName() + "\r\n");
                                    out.flush();
                                    while ((line = in.readLine()) != null) {
                                        sb.append(line);
                                    }
                                    String storeString = sb.toString();
                                    //Set Store
                                    if(storeString.contains(","))
                                    {
                                        store = new Store(storeString);
                                    }
                                    item = new Item(itemString);
                                    //Attach Store to Item
                                    socket = new Socket(IpAddress, 5297);
                                    out = new PrintWriter(socket.getOutputStream(), true);
                                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                                    out.println(Integer.toString(ByteCommand.AttachStoreToItem.ordinal()) + ";" +  item.getItemId() + ";" + store.getStoreId() + "\r\n");
                                    out.flush();
                                }
                                if(item.getItemId() == 0)
                                    item = new Item(itemString);
                                //Set Store on Item
                                if(store != null)
                                    item.setStore(store);
                                shoppingList.AddToItemArrayList(item);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case UpdateItem: {
                        out.println(Integer.toString(ByteCommand.UpdateItem.ordinal()) + ";" + item.toString() + "\r\n");
                        out.flush();
                        break;
                    }
                    case AttachStoreToItem:
                        break;
                    case RemoveItemFromList: {
                        out.println(Integer.toString(ByteCommand.RemoveItemFromList.ordinal()) + ";" + item.getItemId() + "\r\n");
                        out.flush();
                        break;
                    }
                    case RemoveItemFromLibrary: {
                        out.println(Integer.toString(ByteCommand.RemoveItemFromLibrary.ordinal()) + ";" + item.getItemId() + "\r\n");
                        out.flush();
                        break;
                    }
                    case AddStore: {
                        out.println(Integer.toString(ByteCommand.AddStore.ordinal()) + store.toString() + ";" + "\r\n");
                        out.flush();
                        break;
                    }
                    case RemoveStore:
                        break;
                    case GetStore:
                        break;
                }
                in.close();
                out.close();
                done = true;
            }
            catch (SocketTimeoutException ex)
            {
                done = true;
                return "Timed Out";
            }
            catch (IOException ie) {
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
    }
}

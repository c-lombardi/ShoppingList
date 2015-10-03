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
import java.util.Objects;

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
    private String CreateOutLine(Object ... objects){
        StringBuilder sb = new StringBuilder();
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
                socket = new Socket(IpAddress, 5297);
                socket.setSoTimeout(300000);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                switch (command) {
                    case GetItems: {
                        out.println(CreateOutLine(Integer.toString(ByteCommand.GetItems.ordinal())));
                        out.flush();
                        String itemString;
                        shoppingList.ClearItemArrayList();
                        while ((itemString = in.readLine()) != null) {
                            if (itemString.contains(",")) {
                                Item newItem = new Item();
                                newItem.fromString(itemString);
                                shoppingList.AddToItemArrayList(newItem);
                            }
                        }
                        break;
                    }
                    case AddItem: {
                        try {
                            //Add Item
                            out.println(CreateOutLine(Integer.toString(ByteCommand.AddItem.ordinal()), item));
                            out.flush();
                            String line;
                            while ((line = in.readLine()) != null) {
                                item = new Item();
                                item.fromString(line);
                            }
                            //Set Item
                            shoppingList.AddToItemArrayList(item);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case UpdateItem: {
                        out.println(CreateOutLine(Integer.toString(ByteCommand.UpdateItem.ordinal()), item));
                        out.flush();
                        String line;
                        while ((line = in.readLine()) != null) {
                            item.setItemId(Integer.parseInt(line));
                        }
                        shoppingList.AddToItemArrayList(item);
                        break;
                    }
                    case AttachStoreToItem:
                        break;
                    case RemoveItemFromList: {
                        out.println(CreateOutLine(Integer.toString(ByteCommand.RemoveItemFromList.ordinal()), item.getItemId()));
                        out.flush();
                        break;
                    }
                    case RemoveItemFromLibrary: {
                        out.println(CreateOutLine(Integer.toString(ByteCommand.RemoveItemFromLibrary.ordinal()), item.getItemId()));
                        out.flush();
                        break;
                    }
                    case AddStore: {
                        out.println(CreateOutLine(Integer.toString(ByteCommand.AddStore.ordinal()), store));
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

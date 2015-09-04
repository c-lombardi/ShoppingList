package com.example.christopher.shopping_list;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by Christopher on 9/3/2015.
 */
public class ShoppingListClient {
    private static String serverAddress = "";
    private static int port;

    public ShoppingListClient(String s, int p)
    {
        serverAddress = s;
        port = p;
    }

    public void GetShoppingList() throws IOException {
        Socket s = new Socket(serverAddress, port);
        BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
        String NewItems = input.readLine();
    }
}

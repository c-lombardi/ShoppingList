package com.example.christopher.shopping_list_app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.christopher.shopping_list.R;
import com.example.christopher.shopping_list_app.CRUD.Shopping_List_CRUD;
import com.example.christopher.shopping_list_app.Models.ShoppingList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ShoppingListFragment extends Fragment {
    private static Shopping_List_CRUD shopping_list_crud;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.menu_shopping_list, menu);
        //addButtonMenuItem = menu.findItem(R.id.action_add);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                shopping_list_crud.CreateShoppingListAlertDialog();
                return true;
            default:
                return false;
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shopping_lists_list, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        //if(Shopping_List.isActive())
        if(shopping_list_crud == null)
            shopping_list_crud = new Shopping_List_CRUD(this);
        Shopping_List.setShoppingListFragment(this);
    }

    public static void AddToOrReplaceShoppingListArrayList(final ShoppingList newShopping_List) {
        shopping_list_crud.addToOrUpdateArrayList(newShopping_List);
    }

    public static void RemoveFromShoppingListArrayList(final ShoppingList removedShopping_List) {
        shopping_list_crud.removeFromArrayList(removedShopping_List);
    }

    public static void ClearShoppingListArrayList() {
        shopping_list_crud.ClearArrayList();
    }

    public static Shopping_List_CRUD getShopping_list_crud() {
        return shopping_list_crud;
    }

    public void AddNewShopping_List() {
        shopping_list_crud.CreateShoppingListAlertDialog();
    }
}

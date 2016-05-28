package com.example.christopher.shopping_list_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.christopher.shopping_list.R;
import com.example.christopher.shopping_list_app.CRUD.Items_CRUD;
import com.example.christopher.shopping_list_app.CRUD.LibraryItem_CRUD;
import com.example.christopher.shopping_list_app.Models.Item;
import com.example.christopher.shopping_list_app.Server.ByteCommand;
import com.example.christopher.shopping_list_app.Server.Client;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ItemsListFragment extends Fragment {
    private static Items_CRUD items_crud;
    private static LibraryItem_CRUD libraryItem_crud;
    private static MenuItem itemTotalMenuItem;
    private static MenuItem deleteItemsMenuItem;
    private static MenuItem addButtonMenuItem;
    private static AlertDialog alertDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_item_list, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.menu_item_list, menu);
        itemTotalMenuItem = menu.findItem(R.id.itemTotals);
        deleteItemsMenuItem = menu.findItem(R.id.deleteGreenItems);
        addButtonMenuItem = menu.findItem(R.id.action_add);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(items_crud == null) {
            items_crud = new Items_CRUD(this);
            Shopping_List.setItemsListFragment(this);
        }
    }

    public static Items_CRUD getItems_CRUD(){
        return items_crud;
    }

    public static LibraryItem_CRUD getLibraryItems_CRUD(){
        return libraryItem_crud;
    }

    public static MenuItem getAddButtonMenuItem() {
        return addButtonMenuItem;
    }

    public static MenuItem getItemTotalMenuItem() {
        return itemTotalMenuItem;
    }

    public static MenuItem getDeleteItemsMenuItem() {
        return deleteItemsMenuItem;
    }

    public void DeleteGreenItems() {
        items_crud.DeleteGreenItems();
    }

    public void refreshSwipeRefreshLayout() {
        items_crud.refreshSwipeRefreshLayout();
    }
    public void ClearItemArrayList() {
        items_crud.ClearArrayList();
    }

    public static void UpdateAlertDialogEnabled(boolean enabled) {
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(enabled);
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(enabled);
    }

    public void AddNewItem() {
        View inflatedView = getActivity().getLayoutInflater().inflate(R.layout.create_item_layout, null);
        libraryItem_crud = new LibraryItem_CRUD(inflatedView, (TextView) inflatedView.findViewById(R.id.selectedItemsCount));
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        final EditText itemNameView = (EditText) inflatedView.findViewById(R.id.libraryItemName);
        new Client.ClientBuilder(ByteCommand.getLibrary, getActivity().getPreferences(Context.MODE_PRIVATE).getString(StaticVariables.IpAddressString, StaticVariables.ActualHardCodedIpAddress), ItemsListFragment.this).build().execute();
        itemNameView.addTextChangedListener(new DelayedTextWatcher(500) {
            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
                ItemsListFragment.UpdateAlertDialogEnabled(false);
            }

            @Override
            public void afterTextChangedDelayed(final Editable s) {
                final List<Item> itemsToDelete = new ArrayList<>();
                for (final Item i : libraryItem_crud.getTypeList()) {
                    if (!i.getName().toLowerCase().contains(s.toString()))
                        itemsToDelete.add(i);
                }
                for (final Item i : itemsToDelete) {
                    libraryItem_crud.getTypeList().remove(i);
                }
                final Item localItem = new Item();
                localItem.setName(s.toString());
                localItem.setSessionId(Shopping_List.getSession().getSessionId());
                new Client.ClientBuilder(ByteCommand.getLibraryItemsThatContain, getActivity().getPreferences(Context.MODE_PRIVATE).getString(StaticVariables.IpAddressString, StaticVariables.ActualHardCodedIpAddress), ItemsListFragment.this).Item(localItem).build().execute();
            }
        });
        alert.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int whichButton) {
                if (libraryItem_crud.getItemLibraryChosenHashSet().size() > 0) {
                    final List<Item> itemLibraryChosenArrayList = new ArrayList<>();
                    for (final Item i : libraryItem_crud.getItemLibraryChosenHashSet()) {
                        itemLibraryChosenArrayList.add(i);
                        getItems_CRUD().addToArrayList(i);
                    }
                    getItems_CRUD().getArrayAdapter().notifyDataSetChanged();
                    new Client.ClientBuilder(ByteCommand.reAddItems, getActivity().getPreferences(Context.MODE_PRIVATE).getString(StaticVariables.IpAddressString, StaticVariables.ActualHardCodedIpAddress), ItemsListFragment.this).Items(itemLibraryChosenArrayList).build().execute();
                } else {
                    final String itemName = itemNameView.getText().toString().trim();
                    if (!itemName.isEmpty()) {
                        final Item i = new Item();
                        i.setId(0);
                        i.setName(itemName);
                        i.setSessionId(Shopping_List.getSession().getSessionId());
                        i.setShoppingListId(Shopping_List.getActiveShopping_List().getShoppingListId());
                        new Client.ClientBuilder(ByteCommand.addItem, getActivity().getPreferences(Context.MODE_PRIVATE).getString(StaticVariables.IpAddressString, StaticVariables.ActualHardCodedIpAddress), ItemsListFragment.this).Item(i).build().execute();
                    }
                }
            }
        });
        alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int whichButton) {
            }
        });
        alert.setView(inflatedView);
        alertDialog = alert.show();
    }
}

package com.example.christopher.shopping_list.CRUD;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.christopher.shopping_list.Models.Session;
import com.example.christopher.shopping_list.Models.Shopping_List;
import com.example.christopher.shopping_list.R;
import com.example.christopher.shopping_list.Server.ByteCommand;
import com.example.christopher.shopping_list.Server.Client;
import com.example.christopher.shopping_list.Shopping_List_App;
import com.example.christopher.shopping_list.StaticVariables;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christopher on 3/18/2016.
 */
public class Shopping_List_CRUD extends CRUD.SetUpCRUDOperations<Shopping_List> {
    private MenuItem addButtonMenuItem;
    public Shopping_List_CRUD(final Session s, final Shopping_List_App sla, MenuItem abmi) {
        addButtonMenuItem = abmi;
        setTypeList(new ArrayList<>());
        setShopping_list_app(sla);
        setArrayAdapter(new Shopping_List_CRUD_ListViewAdapter(sla, getTypeList()));
        setListView((ListView) sla.findViewById(R.id.shopping_list_ListView));
        getListView().setEmptyView(sla.findViewById(R.id.empty_Shopping_List_list));
        getListView().setAdapter(getArrayAdapter());
        setSession(s);
    }

    @Override
    public void SetUpCreate() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getShopping_list_app());
        final LayoutInflater createEditShopping_ListInflater = getShopping_list_app().getLayoutInflater();
        final View inflatedView = createEditShopping_ListInflater.inflate(R.layout.create_shopping_list, null);
        alert.setView(inflatedView);
        final EditText shopping_ListNameView = (EditText) inflatedView.findViewById(R.id.create_shopping_listName);
        alert.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int whichButton) {
                try {
                    String shopping_ListName = shopping_ListNameView.getText().toString();
                    Shopping_List newShopping_List = new Shopping_List();
                    newShopping_List.setShoppingListName(shopping_ListName);
                    new Client.ClientBuilder(ByteCommand.createShoppingList, getShopping_list_app().getPreferences(Context.MODE_PRIVATE).getString(StaticVariables.IpAddressString, StaticVariables.ActualHardCodedIpAddress), getSession(), newShopping_List, getShopping_list_app()).build().execute();
                } catch (final Exception ignored) {
                }
            }
        });

        alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int whichButton) {
            }
        });
        alert.show();
    }

    @Override
    public void SetUpRead() {
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                final Items_CRUD itemsCRUD = getShopping_list_app().getItems_CRUD();

                addButtonMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(final MenuItem item) {
                        getShopping_list_app().AddNewItem(item);
                        return true;
                    }
                });
                final Shopping_List new_Shopping_List = new Shopping_List();
                new_Shopping_List.setShoppingListId((int) view.getTag(0));
                getShopping_list_app().setActiveShopping_List(new_Shopping_List);
                itemsCRUD.SetUpUpdate();
            }
        });
        try {
            new Client.ClientBuilder(ByteCommand.getItems, getShopping_list_app().getPreferences(Context.MODE_PRIVATE).getString(StaticVariables.IpAddressString, StaticVariables.ActualHardCodedIpAddress), getSession(), getShopping_list_app().getActiveShopping_List(), getShopping_list_app()).build().execute();
        }catch (Exception ignored) {}
    }

    @Override
    public void SetUpUpdate() {
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                //do edit
                final AlertDialog.Builder alert = new AlertDialog.Builder(getShopping_list_app());
                final LayoutInflater createEditShopping_ListInflater = getShopping_list_app().getLayoutInflater();
                final View inflatedView = createEditShopping_ListInflater.inflate(R.layout.edit_shopping_list, null);
                alert.setView(inflatedView);
                final EditText shopping_ListNameView = (EditText) inflatedView.findViewById(R.id.edit_shopping_listName);
                alert.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int whichButton) {
                        try {
                            String shopping_ListName = shopping_ListNameView.getText().toString();
                            Shopping_List editingShopping_List = new Shopping_List();
                            editingShopping_List.setShoppingListName(shopping_ListName);
                            editingShopping_List.setShoppingListId((int) view.getTag(0));
                            new Client.ClientBuilder(ByteCommand.renameShoppingList, getShopping_list_app().getPreferences(Context.MODE_PRIVATE).getString(StaticVariables.IpAddressString, StaticVariables.ActualHardCodedIpAddress), getSession(), editingShopping_List, getShopping_list_app()).build().execute();
                        } catch (final Exception ignored) {
                        }
                    }
                });

                alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int whichButton) {
                    }
                });
                alert.show();
                return true;
            }
        });
    }

    @Override
    public void Delete() {

    }

    @Override
    public void addToArrayList(Shopping_List newShoppingList) {
        try {
            Shopping_List foundShopping_List = null;
            for(final Shopping_List sl : getTypeList()) {
                if (sl.getShoppingListId() == newShoppingList.getShoppingListId()) {
                    foundShopping_List = sl;
                    break;
                }
            }
            if(foundShopping_List != null ) {
                removeFromArrayList(foundShopping_List);
            }
            getTypeList().add(newShoppingList);
        } catch (final Exception ignored) {
        }
    }

    @Override
    public void removeFromArrayList(Shopping_List removeShoppingList) {
        getTypeList().remove(removeShoppingList);
    }


    public class Shopping_List_CRUD_ListViewAdapter extends CRUD.SetUpCRUDOperations<Shopping_List>.typeListAdapter {

        public Shopping_List_CRUD_ListViewAdapter(final Context context, final List<Shopping_List> objects) {
            super(context, objects);
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            // Get the data item for this position
            final Shopping_List shopping_List = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.shopping_list, parent, false);
            }
            // Lookup view for data population
            final TextView tvName = (TextView) convertView.findViewById(R.id.shopping_listName);
            convertView.setTag(0, shopping_List.getShoppingListId());
            // Populate the data into the template view using the data object
            try {
                tvName.setText(shopping_List.getShoppingListName());
            } catch (final Exception ignored) {
            }
            // Return the completed view to render on screen
            return convertView;
        }
    }
}

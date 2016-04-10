package com.example.christopher.shopping_list_app.CRUD;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import com.example.christopher.shopping_list.R;
import com.example.christopher.shopping_list_app.Models.ShoppingList;
import com.example.christopher.shopping_list_app.Server.ByteCommand;
import com.example.christopher.shopping_list_app.Server.Client;
import com.example.christopher.shopping_list_app.ShoppingListFragment;
import com.example.christopher.shopping_list_app.Shopping_List;
import com.example.christopher.shopping_list_app.StaticVariables;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christopher on 3/18/2016.
 */
public class Shopping_List_CRUD extends CRUD.SetUpCRUDOperations<ShoppingList> {
    public Shopping_List_CRUD(final ShoppingListFragment f) {
        setTypeList(new ArrayList<ShoppingList>());
        setFragment(f);
        setArrayAdapter(new Shopping_List_CRUD_ListViewAdapter(f.getContext(), getTypeList()));
        SetUpListView((ListView) f.getView().findViewById(R.id.shopping_list_ListView));
        getListView().setEmptyView(f.getView().findViewById(R.id.empty_Shopping_List_list));
        getListView().setAdapter(getArrayAdapter());
        new Client.ClientBuilder(ByteCommand.getListOfShoppingLists, getFragment().getActivity().getPreferences(getFragment().getActivity().MODE_PRIVATE).getString(StaticVariables.IpAddressString, StaticVariables.ActualHardCodedIpAddress), getFragment()).build().execute();
    }

    private void SetUpListView(final ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                final ShoppingList new_Shopping_List = new ShoppingList();
                new_Shopping_List.setShoppingListId((int) view.getTag());
                new_Shopping_List.setSessionId(Shopping_List.getSession().getSessionId());
                Shopping_List.setActiveShopping_List(new_Shopping_List);

                Shopping_List.changeTransaction(Shopping_List.getItemsListFragment(), getFragment());
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                //do edit
                final AlertDialog.Builder alert = new AlertDialog.Builder(getFragment().getActivity());
                final LayoutInflater createEditShopping_ListInflater = getFragment().getActivity().getLayoutInflater();
                final View inflatedView = createEditShopping_ListInflater.inflate(R.layout.edit_shopping_list, null);
                alert.setView(inflatedView);
                final EditText shopping_ListNameView = (EditText) inflatedView.findViewById(R.id.edit_shopping_listName);
                alert.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int whichButton) {
                        String shopping_ListName = shopping_ListNameView.getText().toString();
                        ShoppingList editingShopping_List = new ShoppingList();
                        editingShopping_List.setShoppingListName(shopping_ListName);
                        editingShopping_List.setShoppingListId((int) view.getTag(0));
                        Shopping_List.setActiveShopping_List(editingShopping_List);
                        new Client.ClientBuilder(ByteCommand.renameShoppingList, getFragment().getActivity().getPreferences(Context.MODE_PRIVATE).getString(StaticVariables.IpAddressString, StaticVariables.ActualHardCodedIpAddress), getFragment()).build().execute();
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
        super.setListView(listView);
    }

    public void CreateShoppingListAlertDialog() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getFragment().getActivity());
        final LayoutInflater createEditShopping_ListInflater = getFragment().getActivity().getLayoutInflater();
        final View inflatedView = createEditShopping_ListInflater.inflate(R.layout.create_shopping_list, null);
        alert.setView(inflatedView);
        final EditText shopping_ListNameView = (EditText) inflatedView.findViewById(R.id.create_shopping_listName);
        alert.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int whichButton) {
                String shopping_ListName = shopping_ListNameView.getText().toString();
                ShoppingList newShopping_List = new ShoppingList();
                newShopping_List.setSessionId(Shopping_List.getSession().getSessionId());
                newShopping_List.setShoppingListName(shopping_ListName);
                Shopping_List.setActiveShopping_List(newShopping_List);
                new Client.ClientBuilder(ByteCommand.createShoppingList, getFragment().getActivity().getPreferences(Context.MODE_PRIVATE).getString(StaticVariables.IpAddressString, StaticVariables.ActualHardCodedIpAddress), getFragment()).build().execute();
            }
        });

        alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int whichButton) {
            }
        });
        alert.show();
    }

    @Override
    public void addToArrayList(ShoppingList newShoppingList) {
        try {
            ShoppingList foundShopping_List = null;
            for(final ShoppingList sl : getTypeList()) {
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
            System.out.println("");
        }
    }

    @Override
    public void removeFromArrayList(ShoppingList removeShoppingList) {
        getTypeList().remove(removeShoppingList);
    }

    public void NotifyArrayListAdapterShoppingListChanged() {
        getArrayAdapter().notifyDataSetChanged();
    }


    public class Shopping_List_CRUD_ListViewAdapter extends CRUD.SetUpCRUDOperations<ShoppingList>.typeListAdapter {

        public Shopping_List_CRUD_ListViewAdapter(final Context context, final List<ShoppingList> objects) {
            super(context, objects);
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            // Get the data item for this position
            final ShoppingList shopping_List = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.shopping_list, parent, false);
            }
            // Lookup view for data population
            final TextView tvName = (TextView) convertView.findViewById(R.id.shopping_listName);
            convertView.setTag(shopping_List.getShoppingListId());
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

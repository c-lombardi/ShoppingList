package com.example.christopher.shopping_list_app.CRUD;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.widget.SwipeRefreshLayout;
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

import static com.example.christopher.shopping_list_app.StaticHelpers.lengthen;

/**
 * Created by Christopher on 3/18/2016.
 */
public class Shopping_List_CRUD extends CRUD.SetUpCRUDOperations<ShoppingList> {
    private SwipeRefreshLayout swipeRefreshLayout;
    public Shopping_List_CRUD(final ShoppingListFragment f) {
        setTypeList(new ArrayList<ShoppingList>());
        setFragment(f);
        setArrayAdapter(new Shopping_List_CRUD_ListViewAdapter(f.getContext(), getTypeList()));
        SetUpSwipeRefreshLayout();
        SetUpListView((ListView) f.getView().findViewById(R.id.shopping_list_ListView));
        getListView().setEmptyView(f.getView().findViewById(R.id.empty_Shopping_List_list));
        getListView().setAdapter(getArrayAdapter());
        new Client.ClientBuilder(ByteCommand.getListOfShoppingLists, getFragment().getActivity().getPreferences(getFragment().getActivity().MODE_PRIVATE).getString(StaticVariables.IpAddressString, StaticVariables.ActualHardCodedIpAddress), getFragment()).build().execute();
    }

    private void SetUpSwipeRefreshLayout() {
        swipeRefreshLayout = (SwipeRefreshLayout) getFragment().getView().findViewById(R.id.shopping_list_swipe_container);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Shopping_List.showPhoneNumberAndAuthCodeDialogs(false, getFragment().getActivity());
                getListView().setEnabled(false);
                new Client.ClientBuilder(ByteCommand.getListOfShoppingLists, getFragment().getActivity().getPreferences(Context.MODE_PRIVATE).getString(StaticVariables.IpAddressString, StaticVariables.ActualHardCodedIpAddress), getFragment()).build().execute();
            }
        });
    }


    public void setSwipeRefreshlayoutRefreshing(final boolean refresh) {
        try {
            if (swipeRefreshLayout != null && refresh) {
                swipeRefreshLayout.setRefreshing(refresh);
                getListView().setEnabled(true);
            } else if(swipeRefreshLayout != null){
                swipeRefreshLayout.setRefreshing(refresh);
            }
        } catch (final Exception ignored) {
        }
    }

    public void refreshSwipeRefreshLayout() {
        swipeRefreshLayout.post(new Runnable() {
            @Override public void run() {
                new Client.ClientBuilder(ByteCommand.getListOfShoppingLists, getFragment().getActivity().getPreferences(Context.MODE_PRIVATE).getString(StaticVariables.IpAddressString, StaticVariables.ActualHardCodedIpAddress), getFragment()).build().execute();
            }
        });
    }

    private void SetUpListView(final ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                final ShoppingList new_Shopping_List = (ShoppingList)parent.getItemAtPosition(position);
                Shopping_List.setActiveShopping_List(new_Shopping_List);
                getFragment().getActivity().setTitle(lengthen(new_Shopping_List.getShoppingListName(), 18));
                Shopping_List.changeTransaction(Shopping_List.getItemsListFragment(), getFragment());
                new Client.ClientBuilder(ByteCommand.getItems, getFragment().getActivity().getPreferences(getFragment().getActivity().MODE_PRIVATE).getString(StaticVariables.IpAddressString, StaticVariables.ActualHardCodedIpAddress), Shopping_List.getItemsListFragment()).build().execute();
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
                ShoppingList activatedShoppingList = (ShoppingList)parent.getItemAtPosition(position);
                shopping_ListNameView.setText(activatedShoppingList.getShoppingListName());
                alert.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int whichButton) {
                        String shopping_ListName = shopping_ListNameView.getText().toString();
                        ShoppingList editingShopping_List = new ShoppingList();
                        editingShopping_List.setShoppingListId((int) view.getTag());
                        editingShopping_List.setShoppingListName(shopping_ListName);
                        editingShopping_List.setSessionId(Shopping_List.getSession().getSessionId());
                        Shopping_List.setActiveShopping_List(editingShopping_List);
                        new Client.ClientBuilder(ByteCommand.renameShoppingList, getFragment().getActivity().getPreferences(Context.MODE_PRIVATE).getString(StaticVariables.IpAddressString, StaticVariables.ActualHardCodedIpAddress), getFragment()).build().execute();
                    }
                });

                alert.setNeutralButton(R.string.remove_shopping_list, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String shopping_ListName = shopping_ListNameView.getText().toString();
                        ShoppingList removingShopping_List = new ShoppingList();
                        removingShopping_List.setShoppingListId((int) view.getTag());
                        removingShopping_List.setShoppingListName(shopping_ListName);
                        removingShopping_List.setSessionId(Shopping_List.getSession().getSessionId());
                        Shopping_List.setActiveShopping_List(removingShopping_List);
                        new Client.ClientBuilder(ByteCommand.removeShoppingList, getFragment().getActivity().getPreferences(Context.MODE_PRIVATE).getString(StaticVariables.IpAddressString, StaticVariables.ActualHardCodedIpAddress), getFragment()).build().execute();
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
                newShopping_List.setShoppingListName(shopping_ListName);
                newShopping_List.setSessionId(Shopping_List.getSession().getSessionId());
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
    public void addToOrUpdateArrayList(ShoppingList newShoppingList) {
        try {
            if(newShoppingList != null && newShoppingList.getShoppingListId() != null) {
                removeFromArrayList(newShoppingList);
                getTypeList().add(newShoppingList);
            }
        } catch (final Exception ignored) {
        }
    }

    @Override
    public void removeFromArrayList(ShoppingList removeShoppingList) {
        ShoppingList foundShopping_List = null;
        for(final ShoppingList sl : getTypeList()) {
            if (sl.getShoppingListId() == removeShoppingList.getShoppingListId()) {
                foundShopping_List = sl;
                break;
            }
        }
        if(foundShopping_List != null ) {
            getTypeList().remove(foundShopping_List);
        }
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
            getFragment().getActivity().setTitle(getFragment().getResources().getString(R.string.app_name));
            // Get the data item for this position
            final ShoppingList shopping_List = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.shopping_list, parent, false);
            }
            // Lookup view for data population
            final TextView tvName = (TextView) convertView.findViewById(R.id.shopping_listName);
            if(shopping_List != null) {
                convertView.setTag(shopping_List.getShoppingListId());
            }
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

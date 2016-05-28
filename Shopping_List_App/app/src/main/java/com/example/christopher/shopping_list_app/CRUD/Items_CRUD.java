package com.example.christopher.shopping_list_app.CRUD;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.christopher.shopping_list.R;
import com.example.christopher.shopping_list_app.ItemsListFragment;
import com.example.christopher.shopping_list_app.Models.Item;
import com.example.christopher.shopping_list_app.Models.ItemStatus;
import com.example.christopher.shopping_list_app.Models.Store;
import com.example.christopher.shopping_list_app.Server.ByteCommand;
import com.example.christopher.shopping_list_app.Server.Client;
import com.example.christopher.shopping_list_app.Shopping_List;
import com.example.christopher.shopping_list_app.StaticHelpers;
import com.example.christopher.shopping_list_app.StaticVariables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static java.util.Collections.sort;

/**
 * Created by Christopher on 3/18/2016.
 */
public class Items_CRUD extends CRUD.SetUpCRUDOperations<Item> {
    private SwipeRefreshLayout swipeRefreshLayout;
    public Items_CRUD(final Fragment f) {
        setFragment(f);
        setTypeList(new ArrayList<Item>());
        setArrayAdapter(new Items_ArrayAdapter(f.getContext(), getTypeList()));
        SetUpSwipeRefreshLayout();
        setUpListView((ListView) f.getView().findViewById(R.id.itemListView));
        getListView().setEmptyView(f.getView().findViewById(R.id.empty_item_item_list));
        getListView().setAdapter(getArrayAdapter());
    }



    private void SetUpSwipeRefreshLayout() {
        swipeRefreshLayout = (SwipeRefreshLayout) getFragment().getView().findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Shopping_List.showPhoneNumberDialog(false, getFragment().getActivity());
                Shopping_List.showAuthCodeDialog(getFragment().getActivity());
                getListView().setEnabled(false);
                new Client.ClientBuilder(ByteCommand.getItems, getFragment().getActivity().getPreferences(Context.MODE_PRIVATE).getString(StaticVariables.IpAddressString, StaticVariables.ActualHardCodedIpAddress), getFragment()).build().execute();
            }
        });
    }


    public void setSwipeRefreshlayoutRefreshing(final boolean refresh) {
        try {
            if (swipeRefreshLayout != null)
                swipeRefreshLayout.setRefreshing(refresh);
        } catch (final Exception ignored) {
        }
    }

    public void refreshSwipeRefreshLayout() {
        swipeRefreshLayout.post(new Runnable() {
            @Override public void run() {
                new Client.ClientBuilder(ByteCommand.getItems, getFragment().getActivity().getPreferences(Context.MODE_PRIVATE).getString(StaticVariables.IpAddressString, StaticVariables.ActualHardCodedIpAddress), getFragment()).build().execute();
            }
        });
    }

    private void setUpListView(final ListView listView) {
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                final View listItemView = getViewByPosition(position);
                final TextView listItemTextView = (TextView) listItemView.findViewById(R.id.itemName);
                final String itemName = listItemTextView.getText().toString();
                final AlertDialog.Builder alert = new AlertDialog.Builder(getFragment().getActivity());
                final Item foundItem = (Item)parent.getItemAtPosition(position);
                alert.setTitle("Edit Item");
                alert.setMessage("I love you, Alina!");
                final LayoutInflater createEditItemInflater = getFragment().getActivity().getLayoutInflater();
                final View inflatedView = createEditItemInflater.inflate(R.layout.edit_item_layout, null);
                alert.setView(inflatedView);
                final EditText itemNameView = (EditText) inflatedView.findViewById(R.id.itemName);
                final EditText bestPriceView = (EditText) inflatedView.findViewById(R.id.bestPrice);
                final EditText storeNameView = (EditText) inflatedView.findViewById(R.id.storeName);
                try {
                    for (final Item item : getTypeList()) {
                        if (item.getName().equals(itemName)) {
                            itemNameView.setText(item.getName().trim());
                            bestPriceView.setText(Float.toString(item.getBestPrice()).trim());
                            if (item.getStore() != null) {
                                storeNameView.setText(item.getStore().getName());
                            } else {
                                storeNameView.setText("");
                            }
                            break;
                        }
                    }
                } catch (final Exception ignored) {
                }
                alert.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int whichButton) {
                        foundItem.setBestPrice(Float.parseFloat(bestPriceView.getText().toString()));
                        foundItem.setName(itemNameView.getText().toString());
                        final Store foundStore = new Store();
                        foundStore.setName(storeNameView.getText().toString());
                        foundItem.setStore(foundStore);
                        new Client.ClientBuilder(ByteCommand.updateItem, getFragment().getActivity().getPreferences(Context.MODE_PRIVATE).getString(StaticVariables.IpAddressString, StaticVariables.ActualHardCodedIpAddress), getFragment()).Item(foundItem).build().execute();
                        updateItemTotalTitle();
                    }
                });
                alert.show();
                return true;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                final Item foundItem = (Item)parent.getItemAtPosition(position);
                foundItem.configureItemStatus();
                updateItemTotalTitle();
                new Client.ClientBuilder(ByteCommand.updateItemStatus, getFragment().getActivity().getPreferences(Context.MODE_PRIVATE).getString(StaticVariables.IpAddressString, StaticVariables.ActualHardCodedIpAddress), getFragment()).Item(foundItem).build().execute();
            }
        });
        super.setListView(listView);
    }

    @Override
    public void addToArrayList(final Item newItem) {
        try {
            Item foundItem = null;
            for (final Item i : getTypeList()) {
                if (i.getId() == newItem.getId()) {
                    foundItem = i;
                    break;
                }
            }
            if (foundItem != null)
                getTypeList().remove(foundItem);
            if(newItem != null)
                getTypeList().add(newItem);
        } catch (final Exception ignored) {
        }

    }

    @Override
    public void removeFromArrayList(final Item oldItem) {
        try {
            getTypeList().remove(oldItem);
        } catch (final Exception ignored) {
        }
    }

    public void removeItemsFromItemArrayList(final List<Item> items) {
        final HashSet<Item> itemHashSet = new HashSet<>(getTypeList());
        final Iterator it = itemHashSet.iterator();
        if (it.hasNext()) {
            do {
                final Item nextItem = ((Item) it.next());
                if (items.contains(nextItem)) {
                    removeFromArrayList(nextItem);
                }
            } while (it.hasNext());
        }
    }

    private void handleColor(final Item item, final View view, final Boolean displayToast) {
        switch (item.getItemStatus()) {
            case Default: {
                view.setBackgroundColor(Color.TRANSPARENT);
                break;
            }
            case Found: {
                view.setBackgroundColor(Color.GREEN);
                if(displayToast)
                    Shopping_List.DisplayToast(getFragment().getActivity().getString(R.string.Found), getFragment().getActivity());
                break;
            }
            case NotFound: {
                view.setBackgroundColor(Color.RED);
                if(displayToast)
                    Shopping_List.DisplayToast(getFragment().getActivity().getString(R.string.NotFound), getFragment().getActivity());
                break;
            }
        }
    }

    private void updateItemTotalTitle() {
        float totalVal = 0;
        for (final Item i : getTypeList()) {
            try {
                if (i.getItemStatus().equals(ItemStatus.Found)) {
                    totalVal += i.getBestPrice();
                }
            } catch (final Exception ignored) {
            }
        }
        ((ItemsListFragment)getFragment()).getItemTotalMenuItem().setTitle(String.valueOf(StaticHelpers.round(totalVal, 2)));

    }

    public void handleDeleteGreenItemsButton() {
        final MenuItem deleteGreenItemsButton = ((ItemsListFragment)getFragment()).getDeleteItemsMenuItem();
        if(deleteGreenItemsButton != null) {
            Boolean hasFoundItem = false;
            for(Item i : getTypeList()) {
                if(i.getItemStatus().equals(ItemStatus.Found)) {
                    hasFoundItem = true;
                    break;
                }
            }
            if (hasFoundItem) {
                deleteGreenItemsButton.setVisible(true);
                deleteGreenItemsButton.setEnabled(true);
            } else {
                deleteGreenItemsButton.setVisible(false);
                deleteGreenItemsButton.setEnabled(false);
            }
        }
    }

    public void DeleteGreenItems() {
        final List<Item> foundItems = new ArrayList<>();
        for (Item i : getTypeList()){
            if(i.getItemStatus().equals(ItemStatus.Found))
                foundItems.add(i);
        }
        if (foundItems.size() > 0) {
            new Client.ClientBuilder(ByteCommand.removeItemsFromList, getFragment().getActivity().getPreferences(Context.MODE_PRIVATE).getString(StaticVariables.IpAddressString, StaticVariables.ActualHardCodedIpAddress), getFragment()).Items(foundItems).build().execute();
        }
    }

    public void NotifyAdapterThatItemListChanged() {
        try {
            sort(getTypeList());
            getArrayAdapter().notifyDataSetChanged();
            updateItemTotalTitle();
            getListView().setEnabled(true);
        } catch (final Exception ignored) {
        }
    }

    public class Items_ArrayAdapter extends CRUD.SetUpCRUDOperations<Item>.typeListAdapter{

        public Items_ArrayAdapter(final Context context, final List objects) {
            super(context, objects);
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            // Get the data item for this position
            final Item item = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
            }
            // Lookup view for data population
            final TextView tvName = (TextView) convertView.findViewById(R.id.itemName);
            final TextView tvHome = (TextView) convertView.findViewById(R.id.itemStore);
            final TextView tvPrice = (TextView) convertView.findViewById(R.id.bestPrice);
            // Populate the data into the template view using the data object
            try {
                tvName.setText(item.getName());
                tvPrice.setText(String.valueOf("$" + item.getBestPrice()));
                tvHome.setText("");
                if (item.getStore() != null) {
                    tvHome.setText(item.getStore().getName());
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
            if (item != null) {
                handleColor(item, convertView, false);
            }
            // Return the completed view to render on screen
            return convertView;
        }
    }
}

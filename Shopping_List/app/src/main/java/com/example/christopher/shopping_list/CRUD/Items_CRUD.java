package com.example.christopher.shopping_list.CRUD;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.christopher.shopping_list.Models.Item;
import com.example.christopher.shopping_list.Models.Session;
import com.example.christopher.shopping_list.Models.Store;
import com.example.christopher.shopping_list.R;
import com.example.christopher.shopping_list.Server.ByteCommand;
import com.example.christopher.shopping_list.Server.Client;
import com.example.christopher.shopping_list.Shopping_List_App;
import com.example.christopher.shopping_list.StaticHelpers;
import com.example.christopher.shopping_list.StaticVariables;

import java.io.IOException;
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
    private HashMap<String, Integer> colorDict;
    private MenuItem itemTotalMenuItem, deleteItemsMenuItem, addButtonMenuItem;
    private SwipeRefreshLayout swipeRefreshLayout;
    public Items_CRUD(final Session s, final Shopping_List_App sla, final MenuItem itmi, final MenuItem dimi, final MenuItem abmi) {
        colorDict = new HashMap<>();
        addButtonMenuItem = abmi;
        itemTotalMenuItem = itmi;
        deleteItemsMenuItem = dimi;
        setShopping_list_app(sla);
        setTypeList(new ArrayList<>());
        setArrayAdapter(new Items_ArrayAdapter(getShopping_list_app(), getTypeList()));
        setListView((ListView) getShopping_list_app().findViewById(R.id.listView));
        getListView().setEmptyView(getShopping_list_app().findViewById(R.id.item_empty));
        getListView().setAdapter(getArrayAdapter());
        setSession(s);
    }

    @Override
    public void SetUpCreate() {
        final LibraryItem_CRUD libraryItem_crud = getShopping_list_app().getLibraryItem_crud();
        libraryItem_crud.SetUpRead();
        libraryItem_crud.SetUpUpdate();

    }

    @Override
    public void SetUpRead() {
        swipeRefreshLayout = (SwipeRefreshLayout) getShopping_list_app().findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    getShopping_list_app().showPhoneNumberDialog(false);
                    getShopping_list_app().showAuthCodeDialog();
                    getListView().setEnabled(false);
                    new Client.ClientBuilder(ByteCommand.getItems, getShopping_list_app().getPreferences(Context.MODE_PRIVATE).getString(StaticVariables.IpAddressString, StaticVariables.ActualHardCodedIpAddress), getSession(), getShopping_list_app().getActiveShopping_List(), getShopping_list_app()).build().execute();
                } catch (final Exception ignored) {
                }
            }
        });

        if(swipeRefreshLayout != null) {
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        new Client.ClientBuilder(ByteCommand.getItems, getShopping_list_app().getPreferences(Context.MODE_PRIVATE).getString(StaticVariables.IpAddressString, StaticVariables.ActualHardCodedIpAddress), getSession(), getShopping_list_app().getActiveShopping_List(), getShopping_list_app()).build().execute();
                    } catch (final IOException ignored) {
                    }
                }
            });
        }
    }

    @Override
    public void SetUpUpdate() {
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                final View listItemView = getViewByPosition(position);
                final TextView listItemTextView = (TextView) listItemView.findViewById(R.id.itemName);
                final String itemName = listItemTextView.getText().toString();
                final AlertDialog.Builder alert = new AlertDialog.Builder(getShopping_list_app());
                alert.setTitle("Edit Item");
                alert.setMessage("I love you, Alina!");
                final LayoutInflater createEditItemInflater = getShopping_list_app().getLayoutInflater();
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
                        try {
                            for (final Item item : getTypeList()) {
                                if (item.getName().equals(itemName)) {
                                    final String itemName = itemNameView.getText().toString().trim();
                                    if (!itemName.isEmpty()) {
                                        final Item i = new Item();
                                        i.setId(item.getId());
                                        i.setName(itemName);
                                        i.setSessionId(item.getSessionId());
                                        final String bestPriceString = bestPriceView.getText().toString().trim();
                                        if (!bestPriceString.isEmpty()) {
                                            i.setBestPrice(Float.parseFloat(bestPriceString));
                                        }
                                        final String storeName = storeNameView.getText().toString().trim();
                                        if (!storeName.isEmpty()) {
                                            final Store s = new Store();
                                            s.setName(storeName);
                                            i.setStore(s);
                                        }
                                        new Client.ClientBuilder(ByteCommand.updateItem, getShopping_list_app().getPreferences(Context.MODE_PRIVATE).getString(StaticVariables.IpAddressString, StaticVariables.ActualHardCodedIpAddress), getSession(), getShopping_list_app().getActiveShopping_List(), getShopping_list_app()).Item(i).build().execute();
                                        updateItemTotalTitle();
                                    }
                                    break;
                                }
                            }
                        } catch (final Exception ignored) {
                        }
                    }
                });
                alert.show();
                return true;
            }
        });
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                final View listItemView = getViewByPosition(position);
                final TextView listItemTextView = (TextView) listItemView.findViewById(R.id.itemName);
                final String itemName = listItemTextView.getText().toString();
                handleColor(itemName, listItemView);
                handleDeleteGreenItemsButton();
                updateItemTotalTitle();
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

    @Override
    public void Delete() {

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
            colorDict.remove(oldItem.getName());
        } catch (final Exception ignored) {
        }
    }

    public void removeItemsFromItemArrayList(final List<Integer> itemIds) {
        final HashSet<Item> itemHashSet = new HashSet<>(getTypeList());
        final Iterator it = itemHashSet.iterator();
        if (it.hasNext()) {
            do {
                final Item nextItem = ((Item) it.next());
                if (itemIds.contains(nextItem.getId())) {
                    removeFromArrayList(nextItem);
                }
            } while (it.hasNext());
        }
    }

    public void ClearItemArrayList() {
        try {
            getTypeList().clear();
        } catch (final Exception ignored) {
        }
    }

    private void populateColorDict(final Item item, final View view) {
        if (!colorDict.containsKey(item.getName())) {
            colorDict.put(item.getName(), Color.TRANSPARENT);
            view.setBackgroundColor(Color.TRANSPARENT);
        } else {
            view.setBackgroundColor(colorDict.get(item.getName()));
        }
    }

    private void handleColor(final String itemName, final View listItemView) {
        final Integer color = colorDict.get(itemName);
        if (color == Color.RED) {
            colorDict.put(itemName, Color.TRANSPARENT);
            listItemView.setBackgroundColor(Color.TRANSPARENT);
        } else if (color == Color.GREEN) {
            colorDict.put(itemName, Color.RED);
            listItemView.setBackgroundColor(Color.RED);
            getShopping_list_app().DisplayToast(getShopping_list_app().getResources().getString(R.string.NotFound));
        } else if (color == Color.TRANSPARENT) {
            colorDict.put(itemName, Color.GREEN);
            listItemView.setBackgroundColor(Color.GREEN);
            getShopping_list_app().DisplayToast(getShopping_list_app().getResources().getString(R.string.Found));
        }
    }

    private void updateItemTotalTitle() {
        float totalVal = 0;
        for (final Item i : getTypeList()) {
            try {
                if (colorDict.get(i.getName()) == Color.GREEN) {
                    totalVal += i.getBestPrice();
                }
            } catch (final Exception ignored) {
            }
        }
        itemTotalMenuItem.setTitle(String.valueOf(StaticHelpers.round(totalVal, 2)));

    }

    public void handleDeleteGreenItemsButton() {
        if(colorDict != null) {
            if (colorDict.containsValue(Color.GREEN)) {
                deleteItemsMenuItem.setVisible(true);
                deleteItemsMenuItem.setEnabled(true);
            } else {
                deleteItemsMenuItem.setVisible(false);
                deleteItemsMenuItem.setEnabled(false);
            }
        }
    }

    public void DeleteGreenItems() throws IOException {
        final Iterator it = colorDict.entrySet().iterator();
        final List<Item> foundItems = new ArrayList<>();
        while (it.hasNext()) {
            final Map.Entry itemNameAndColor = (Map.Entry) it.next();
            if (Integer.parseInt(itemNameAndColor.getValue().toString()) == Color.GREEN) {
                final String name = itemNameAndColor.getKey().toString().trim();
                final Item localItem = new Item();
                localItem.setId(0);
                localItem.setName(name);
                final int indexOf = getTypeList().indexOf(localItem);
                final Item foundItem = getTypeList().get(indexOf);
                foundItems.add(foundItem);
            }
        }
        final List<Integer> foundItemIds = new ArrayList<>();
        if (foundItems.size() > 0) {
            for (final Item i : foundItems) {
                foundItemIds.add(i.getId());
            }
            new Client.ClientBuilder(ByteCommand.removeItemsFromList, getShopping_list_app().getPreferences(Context.MODE_PRIVATE).getString(StaticVariables.IpAddressString, StaticVariables.ActualHardCodedIpAddress), getSession(), getShopping_list_app().getActiveShopping_List(), getShopping_list_app()).ItemIds(foundItemIds).build().execute();
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
                populateColorDict(item, convertView);
            }
            // Return the completed view to render on screen
            return convertView;
        }
    }
}

package com.example.christopher.shopping_list.CRUD;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.christopher.shopping_list.DelayedTextWatcher;
import com.example.christopher.shopping_list.Models.Item;
import com.example.christopher.shopping_list.Models.Session;
import com.example.christopher.shopping_list.R;
import com.example.christopher.shopping_list.Server.ByteCommand;
import com.example.christopher.shopping_list.Server.Client;
import com.example.christopher.shopping_list.Shopping_List_App;
import com.example.christopher.shopping_list.StaticVariables;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static java.util.Collections.sort;

/**
 * Created by Christopher on 3/19/2016.
 */
public class LibraryItem_CRUD extends CRUD.SetUpCRUDOperations<Item> {
    private HashSet<Item> itemLibraryChosenHashSet;
    public LibraryItem_CRUD(final Session s, final Shopping_List_App sla) {
        setListView((ListView) getShopping_list_app().findViewById(R.id.libraryItemsListView));
        itemLibraryChosenHashSet = new HashSet<>();
        setTypeList(new ArrayList<>());
        setArrayAdapter(new ItemsLibraryAdapter(sla, getTypeList()));
        getListView().setAdapter(getArrayAdapter());
        getListView().setEmptyView(getShopping_list_app().findViewById(R.id.item_empty));
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        getListView().setItemsCanFocus(false);
        setShopping_list_app(sla);
        setSession(s);
    }

    @Override
    public void SetUpCreate() {


    }

    @Override
    public void SetUpRead() {
        try {
            new Client.ClientBuilder(ByteCommand.getLibrary, getShopping_list_app().getPreferences(Context.MODE_PRIVATE).getString(StaticVariables.IpAddressString, StaticVariables.ActualHardCodedIpAddress), getSession(), getShopping_list_app().getActiveShopping_List(), getShopping_list_app()).build().execute();
        } catch (final IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void SetUpUpdate() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getShopping_list_app());
        final LayoutInflater createEditItemInflater = getShopping_list_app().getLayoutInflater();
        final View inflatedView = createEditItemInflater.inflate(R.layout.create_item_layout, null);
        alert.setView(inflatedView);
        final TextView libraryItemChosenHashSetCount = (TextView) inflatedView.findViewById(R.id.selectedItemsCount);
        final EditText itemNameView = (EditText) inflatedView.findViewById(R.id.itemName);
        itemNameView.addTextChangedListener(new DelayedTextWatcher(200) {
            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
                final List<Item> localItemsNotFoundToBeRemoved = new ArrayList<>();
                for (final Item i : getTypeList()) {
                    if (!i.getName().toLowerCase().contains(s.toString()))
                        localItemsNotFoundToBeRemoved.add(i);
                }
                getTypeList().removeAll(localItemsNotFoundToBeRemoved);
                NotifyAdapterThatItemLibraryListChanged();
            }

            @Override
            public void afterTextChangedDelayed(final Editable s) {
                try {
                    final Item localItem = new Item();
                    localItem.setName(s.toString());
                    localItem.setSessionId(getSession().getSessionId());
                    new Client.ClientBuilder(ByteCommand.getLibraryItemsThatContain, getShopping_list_app().getPreferences(Context.MODE_PRIVATE).getString(StaticVariables.IpAddressString, StaticVariables.ActualHardCodedIpAddress), getSession(), getShopping_list_app().getActiveShopping_List(), getShopping_list_app()).Item(localItem).build().execute();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        });
        alert.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int whichButton) {
                try {
                    if (itemLibraryChosenHashSet.size() > 0) {
                        final List<Integer> itemIdLibraryChosenArrayList = new ArrayList<>();
                        for (final Item i : itemLibraryChosenHashSet) {
                            itemIdLibraryChosenArrayList.add(i.getId());
                        }
                        new Client.ClientBuilder(ByteCommand.reAddItems, getShopping_list_app().getPreferences(Context.MODE_PRIVATE).getString(StaticVariables.IpAddressString, StaticVariables.ActualHardCodedIpAddress), getSession(), getShopping_list_app().getActiveShopping_List(), getShopping_list_app()).ItemIds(itemIdLibraryChosenArrayList).build().execute();
                    } else {
                        final String itemName = itemNameView.getText().toString().trim();
                        if (!itemName.isEmpty()) {
                            final Item i = new Item();
                            i.setId(0);
                            i.setName(itemName);
                            i.setSessionId(getSession().getSessionId());
                            new Client.ClientBuilder(ByteCommand.addItem, getShopping_list_app().getPreferences(Context.MODE_PRIVATE).getString(StaticVariables.IpAddressString, StaticVariables.ActualHardCodedIpAddress), getSession(), getShopping_list_app().getActiveShopping_List(), getShopping_list_app()).Item(i).build().execute();
                        }
                    }
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        });
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                final TextView itemNameTextView = (TextView) view.findViewById(R.id.itemName);
                final String chosenItemName = itemNameTextView.getText().toString();
                final Item foundItem = new Item();
                foundItem.setId(Integer.parseInt(view.getTag().toString()));
                foundItem.setName(chosenItemName);
                foundItem.setSessionId(getSession().getSessionId());
                if (itemLibraryChosenHashSet.contains(foundItem)) {
                    view.setBackgroundColor(Color.TRANSPARENT);
                    itemLibraryChosenHashSet.remove(foundItem);
                } else {
                    final Item i = getTypeList().get(getTypeList().indexOf(foundItem));
                    itemLibraryChosenHashSet.add(i);
                    changeColorLibrary(i, view);
                }
                libraryItemChosenHashSetCount.setText(String.valueOf(itemLibraryChosenHashSet.size()));
                if (itemLibraryChosenHashSet.size() == 0) {
                    itemNameView.setHint(R.string.add_item);
                } else {
                    itemNameView.setHint(R.string.filter_results);
                }
                NotifyAdapterThatItemLibraryListChanged();
            }
        });
        alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int whichButton) {
            }
        });
        alert.show().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
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
            if (foundItem != null) {
                getTypeList().remove(foundItem);
            }
            getTypeList().add(newItem);
        } catch (final Exception ignored) {
        }

    }

    @Override
    public void removeFromArrayList(final Item item) {

    }

    private void changeColorLibrary(final Item item, final View view) {
        if (itemLibraryChosenHashSet.contains(item)) {
            view.setBackgroundColor(Color.parseColor("#ff33b5e5"));
        } else {
            view.setBackgroundColor(Color.TRANSPARENT);
        }
    }


    public void NotifyAdapterThatItemLibraryListChanged() {
        try {
            sort(getTypeList());
            getArrayAdapter().notifyDataSetChanged();
        } catch (final Exception ignored) {
        }
    }

    private class ItemsLibraryAdapter extends CRUD.SetUpCRUDOperations<Item>.typeListAdapter {
        public ItemsLibraryAdapter(final Context context, final List users) {
            super(context, users);
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            // Get the data item for this position
            final Item item = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_name, parent, false);
            }
            // Lookup view for data population
            final TextView tvName = (TextView) convertView.findViewById(R.id.itemName);
            // Populate the data into the template view using the data object
            try {
                tvName.setText(item.getName());
            } catch (final Exception e) {
                e.printStackTrace();
            }
            changeColorLibrary(item, convertView);
            if(item != null)
                convertView.setTag(item.getId());
            // Return the completed view to render on screen
            return convertView;
        }
    }
}

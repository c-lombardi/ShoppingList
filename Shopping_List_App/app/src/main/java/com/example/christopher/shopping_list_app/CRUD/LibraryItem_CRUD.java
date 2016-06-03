package com.example.christopher.shopping_list_app.CRUD;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.christopher.shopping_list.R;
import com.example.christopher.shopping_list_app.Models.Item;
import com.example.christopher.shopping_list_app.Shopping_List;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static java.util.Collections.sort;

/**
 * Created by Christopher on 3/19/2016.
 */
public class LibraryItem_CRUD extends CRUD.SetUpCRUDOperations<Item> {
    private final HashSet<Item> itemLibraryChosenHashSet;
    private static TextView libraryItemChosenHashSetCount;
    public LibraryItem_CRUD(final View view, final TextView lichsc) {
        setListView((ListView) view.findViewById(R.id.libraryItemsListView));
        libraryItemChosenHashSetCount = lichsc;
        itemLibraryChosenHashSet = new HashSet<>();
        setTypeList(new ArrayList<Item>());
        setArrayAdapter(new ItemsLibraryAdapter(view.getContext(), getTypeList()));
        getListView().setAdapter(getArrayAdapter());
        getListView().setEmptyView(view.findViewById(R.id.item_empty));
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        getListView().setItemsCanFocus(false);
    }

    public HashSet<Item> getItemLibraryChosenHashSet() {
        return itemLibraryChosenHashSet;
    }

    @Override
    public void addToOrUpdateArrayList(final Item newItem) {
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
        try {
            getTypeList().remove(item);
        } catch (final Exception ignored) {
        }
    }

    @Override
    public void setListView(ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                final TextView itemNameTextView = (TextView) view.findViewById(R.id.libraryItemNameTextView);
                final String chosenItemName = itemNameTextView.getText().toString();
                final Item foundItem = new Item();
                foundItem.setId(Integer.parseInt(view.getTag().toString()));
                foundItem.setName(chosenItemName);
                foundItem.setSessionId(Shopping_List.getSession().getSessionId());
                if (getItemLibraryChosenHashSet().contains(foundItem)) {
                    view.setBackgroundColor(Color.TRANSPARENT);
                    getItemLibraryChosenHashSet().remove(foundItem);
                } else {
                    final Item i = getTypeList().get(getTypeList().indexOf(foundItem));
                    getItemLibraryChosenHashSet().add(i);
                    changeColorLibrary(i, view);
                }
                libraryItemChosenHashSetCount.setText(String.valueOf(getItemLibraryChosenHashSet().size()));
                if (getItemLibraryChosenHashSet().size() == 0) {
                    itemNameTextView.setHint(R.string.add_item);
                } else {
                    itemNameTextView.setHint(R.string.filter_results);
                }
            }
        });
        super.setListView(listView);
    }

    public void changeColorLibrary(final Item item, final View view) {
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
            getListView().setEnabled(true);
        } catch (final Exception ignored) {
        }
    }

    private class ItemsLibraryAdapter extends CRUD.SetUpCRUDOperations<Item>.typeListAdapter {
        public ItemsLibraryAdapter(final Context context, final List objects) {
            super(context, objects);
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
            final TextView tvName = (TextView) convertView.findViewById(R.id.libraryItemNameTextView);
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

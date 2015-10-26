package com.example.christopher.shopping_list;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static java.util.Collections.*;

public class Shopping_List extends AppCompatActivity {
    private static ArrayList<Item> itemArrayList, itemLibraryArrayList;
    private static ItemsAdapter adapter;
    private static ItemsLibraryAdapter libraryAdapter;
    private static HashMap<String, Integer> colorDict;
    private static HashSet<Item> itemLibraryChosenHashSet;
    private static MenuItem itemTotalMenuItem;
    private static MenuItem deleteItemsMenuItem;
    private static ListView itemListView;
    private static SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping__list);
        colorDict = new HashMap<>();
        itemArrayList = new ArrayList<>();
        adapter = new ItemsAdapter(this, itemArrayList);
        itemListView = (ListView) findViewById(R.id.listView);
        itemListView.setEmptyView(findViewById(R.id.empty));
        itemListView.setAdapter(adapter);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    itemListView.setEnabled(false);
                    new Client.ClientBuilder(ByteCommand.getItems, getPreferences(MODE_PRIVATE).getString("IpAddress", "127.0.0.1")).build().execute();
                } catch (Exception ex) {
                    System.out.println("fail");
                }
            }
        });
        itemListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(Shopping_List.this);
                final View listItemView = getViewByPosition(position);
                final TextView listItemTextView = (TextView)listItemView.findViewById(R.id.itemName);
                final String itemName = listItemTextView.getText().toString();
                alert.setTitle(String.format("Remove or Edit %s", itemName));
                alert.setMessage("I love you, Alina!");
                alert.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final AlertDialog.Builder alert = new AlertDialog.Builder(Shopping_List.this);
                        alert.setTitle("Edit Item");
                        alert.setMessage("I love you, Alina!");
                        final LayoutInflater createEditItemInflater = Shopping_List.this.getLayoutInflater();
                        final View inflatedView = createEditItemInflater.inflate(R.layout.edit_item_layout, null);
                        alert.setView(inflatedView);
                        final EditText itemNameView = (EditText) inflatedView.findViewById(R.id.itemName);
                        final EditText bestPriceView = (EditText) inflatedView.findViewById(R.id.bestPrice);
                        final EditText storeNameView = (EditText) inflatedView.findViewById(R.id.storeName);
                        try {
                            for (Item item : itemArrayList) {
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
                        } catch (Exception ex) {
                            System.out.println("fail");
                        }
                        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                try {
                                    for (Item item : itemArrayList) {
                                        if (item.getName().equals(itemName)) {
                                            String itemName = itemNameView.getText().toString().trim();
                                            if (!itemName.isEmpty()) {
                                                final Item.ItemBuilder ib = new Item.ItemBuilder(item.getId(), itemName);
                                                final String bestPriceString = bestPriceView.getText().toString().trim();
                                                if (!bestPriceString.isEmpty()) {
                                                    ib.bestPrice(Float.parseFloat(bestPriceString));
                                                }
                                                final String storeName = storeNameView.getText().toString().trim();
                                                if (!storeName.isEmpty()) {
                                                    final Store.StoreBuilder sb = new Store.StoreBuilder(storeName);
                                                    ib.store(sb.build());
                                                }
                                                new Client.ClientBuilder(ByteCommand.updateItem, getPreferences(MODE_PRIVATE).getString("IpAddress", "127.0.0.1")).Item(ib.build()).build().execute();
                                                updateItemTotalTitle();
                                            }
                                            break;
                                        }
                                    }
                                } catch (Exception ex) {
                                    System.out.println("fail");
                                }
                            }
                        });
                        alert.show();
                    }
                });

                alert.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

                alert.setNegativeButton("Remove", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        try {
                            for (Item i : itemArrayList) {
                                if (i.getName().equals(itemName)) {
                                    new Client.ClientBuilder(ByteCommand.removeItemFromList, getPreferences(MODE_PRIVATE).getString("IpAddress", "127.0.0.1")).Item(i).build().execute();
                                    break;
                                }
                            }
                        } catch (Exception ex) {
                            System.out.println("fail");
                        }
                    }
                });
                alert.show();
                return true;
            }
        });
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final View listItemView = getViewByPosition(position);
                final TextView listItemTextView = (TextView) listItemView.findViewById(R.id.itemName);
                final String itemName = listItemTextView.getText().toString();
                handleColor(itemName, listItemView);
                handleDeleteGreenItemsButton();
                updateItemTotalTitle();
            }
        });
    }

    //START List Section
    public void setSwipeRefreshlayoutRefreshing(boolean refresh){
        try {
            swipeRefreshLayout.setRefreshing(refresh);
        } catch (Exception ex) {

        }
    }

    public void handleDeleteGreenItemsButton () {
        if(colorDict.containsValue(Color.GREEN)) {
            deleteItemsMenuItem.setVisible(true);
            deleteItemsMenuItem.setEnabled(true);
        } else {
            deleteItemsMenuItem.setVisible(false);
            deleteItemsMenuItem.setEnabled(false);
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                new Client.ClientBuilder(ByteCommand.getItems, getPreferences(MODE_PRIVATE).getString("IpAddress", "127.0.0.1")).build().execute();
            }
        });
    }

    public static void ClearItemArrayList(){
        try {
            itemArrayList.clear();
        }
        catch (Exception ex)
        {
            System.out.print("fail");
        }
    }

    public static void AddToItemArrayList(final Item newItem)
    {
        try {
            Item foundItem = null;
            for(Item i : itemArrayList)
            {
                if(i.getId() == newItem.getId()) {
                    foundItem = i;
                    break;
                }
            }
            if(foundItem != null) {
                itemArrayList.remove(foundItem);
            }
            itemArrayList.add(newItem);
        }
        catch (Exception ex)
        {
            System.out.print("fail");
        }
    }
    public void NotifyAdapterThatItemListChanged(){
        try {
            sort(itemArrayList);
            adapter.notifyDataSetChanged();
            updateItemTotalTitle();
            itemListView.setEnabled(true);
        }
        catch (Exception ex)
        {
            System.out.print("fail1");
        }
    }

    public static void removeItemFromItemArrayList(final Item oldItem)
    {
        try {
            itemArrayList.remove(oldItem);
            colorDict.remove(oldItem.getName());
        }
        catch (Exception ex)
        {
            System.out.print("fail");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shopping__list, menu);
        itemTotalMenuItem = menu.findItem(R.id.itemTotals);
        deleteItemsMenuItem = menu.findItem(R.id.deleteGreenItems);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        final int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_add) {
            return true;
        }
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void DisplayToast(String str) {
        try {
            final Toast toast = Toast.makeText(Shopping_List.this.getApplicationContext(), str, Toast.LENGTH_SHORT);
            toast.show();
        } catch (Exception ex) {}
    }

    public void removeItemsFromItemArrayList(List<Object> itemIds) {
        final HashSet<Item> itemHashSet = new HashSet<>(itemArrayList);
        final Iterator it = itemHashSet.iterator();
        while(it.hasNext()){
            final Item nextItem = ((Item)it.next());
            if(itemIds.contains(nextItem.getId())) {
                removeItemFromItemArrayList(nextItem);
            }
        }
    }

    public void DeleteGreenItems(MenuItem item) {
        Iterator it = colorDict.entrySet().iterator();
        final List<Item> foundItems = new ArrayList<>();
        while (it.hasNext()){
            Map.Entry itemNameAndColor = (Map.Entry)it.next();
            if(Integer.parseInt(itemNameAndColor.getValue().toString()) == Color.GREEN) {
                final String name = itemNameAndColor.getKey().toString().trim();
                final Item localItem = new Item.ItemBuilder(name).build();
                final int indexOf = itemArrayList.indexOf(localItem);
                final Item foundItem = itemArrayList.get(indexOf);
                foundItems.add(foundItem);
            }
        }
        final List<Object> foundItemIds = new ArrayList<>();
            if(foundItems.size() > 0) {
            for (Item i : foundItems) {
                foundItemIds.add(i.getId());
            }
            new Client.ClientBuilder(ByteCommand.removeItemsFromList, getPreferences(MODE_PRIVATE).getString("IpAddress", "127.0.0.1")).ItemIds(foundItemIds).build().execute();
        }
    }

    private void populateColorDict(Item item, View view) {
        if(!colorDict.containsKey(item.getName())) {
            colorDict.put(item.getName(), Color.TRANSPARENT);
            view.setBackgroundColor(Color.TRANSPARENT);
        } else {
            view.setBackgroundColor(colorDict.get(item.getName()));
        }
    }

    private void handleColor(String itemName, View listItemView) {
        final Integer color = colorDict.get(itemName);
        if (color == Color.RED) {
            colorDict.put(itemName, Color.TRANSPARENT);
            listItemView.setBackgroundColor(Color.TRANSPARENT);
        } else if (color == Color.GREEN) {
            colorDict.put(itemName, Color.RED);
            listItemView.setBackgroundColor(Color.RED);
        } else if (color == Color.TRANSPARENT) {
            colorDict.put(itemName, Color.GREEN);
            listItemView.setBackgroundColor(Color.GREEN);
        }
    }

    private void updateItemTotalTitle () {
        float totalVal = 0;
        for (Item i : itemArrayList) {
            try {
                if (colorDict.get(i.getName()) == Color.GREEN) {
                    totalVal += i.getBestPrice();
                }} catch (Exception ex) {
                System.out.println("failed");
            }
        }
        itemTotalMenuItem.setTitle(String.valueOf(round(totalVal, 2)));

    }

    private View getViewByPosition(int pos) {
        final int firstListItemPosition = itemListView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + itemListView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return itemListView.getAdapter().getView(pos, null, itemListView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return itemListView.getChildAt(childIndex);
        }
    }

    private class ItemsAdapter extends ArrayAdapter<Item> {
        public ItemsAdapter(Context context, ArrayList<Item> users) {
            super(context, 0, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
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
                if(item.getStore() != null) {
                    tvHome.setText(item.getStore().getName());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            populateColorDict(item, convertView);
            // Return the completed view to render on screen
            return convertView;
        }
    }
    //END List Section

    //START Library Section
    public static void AddToLibraryItemArrayList(final Item newItem)
    {
        try {
            Item foundItem = null;
            for(Item i : itemLibraryArrayList)
            {
                if(i.getId() == newItem.getId()) {
                    foundItem = i;
                    break;
                }
            }
            if(foundItem != null) {
                itemLibraryArrayList.remove(foundItem);
            }
            itemLibraryArrayList.add(newItem);
        }
        catch (Exception ex)
        {
            System.out.print("fail");
        }
    }
    public void NotifyAdapterThatItemLibraryListChanged() {
        try {
            sort(itemLibraryArrayList);
            libraryAdapter.notifyDataSetChanged();
        }
        catch (Exception ex)
        {
            System.out.print("fail1");
        }
    }

    public void AddNewItem(MenuItem view)
    {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final LayoutInflater createEditItemInflater = Shopping_List.this.getLayoutInflater();
        final View inflatedView = createEditItemInflater.inflate(R.layout.create_item_layout, null);
        alert.setView(inflatedView);
        final TextView libraryItemChosenHashSetCount = (TextView)inflatedView.findViewById(R.id.selectedItemsCount);
        itemLibraryArrayList = new ArrayList<>();
        itemLibraryChosenHashSet = new HashSet<>();
        libraryAdapter = new ItemsLibraryAdapter(inflatedView.getContext(), itemLibraryArrayList);
        final EditText itemNameView = (EditText) inflatedView.findViewById(R.id.itemName);
        itemNameView.addTextChangedListener(new DelayedTextWatcher(200) {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                final List<Item> localItemsNotFoundToBeRemoved = new ArrayList<Item>();
                for(Item i :itemLibraryArrayList)
                {
                    if(!i.getName().toLowerCase().contains(s.toString()))
                        localItemsNotFoundToBeRemoved.add(i);
                }
                itemLibraryArrayList.removeAll(localItemsNotFoundToBeRemoved);
                NotifyAdapterThatItemLibraryListChanged();
            }

            @Override
            public void afterTextChangedDelayed(Editable s) {
                new Client.ClientBuilder(ByteCommand.getLibraryItemsThatContain, getPreferences(MODE_PRIVATE).getString("IpAddress", "127.0.0.1")).SearchString(s.toString()).build().execute();
            }
        });
        final ListView itemLibraryListView = (ListView) inflatedView.findViewById(R.id.libraryItemsListView);
        itemLibraryListView.setAdapter(libraryAdapter);
        itemLibraryListView.setEmptyView(inflatedView.findViewById(R.id.createEmpty));
        new Client.ClientBuilder(ByteCommand.getLibrary, getPreferences(MODE_PRIVATE).getString("IpAddress", "127.0.0.1")).build().execute();
        itemLibraryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView itemNameTextView = (TextView) view.findViewById(R.id.itemName);
                String chosenItemName = itemNameTextView.getText().toString();
                Item foundItem = new Item.ItemBuilder(Integer.parseInt(view.getTag().toString()), chosenItemName).build();
                if(itemLibraryChosenHashSet.contains(foundItem)){
                    view.setBackgroundColor(Color.TRANSPARENT);
                    itemLibraryChosenHashSet.remove(foundItem);
                } else {
                    Item i = itemLibraryArrayList.get(itemLibraryArrayList.indexOf(foundItem));
                    itemLibraryChosenHashSet.add(i);
                    changeColorLibrary(i, view);
                }
                libraryItemChosenHashSetCount.setText(String.valueOf(itemLibraryChosenHashSet.size()));
                if (itemLibraryChosenHashSet.size() == 0) {
                    itemNameView.setHint(R.string.add_item);
                } else {
                    itemNameView.setHint(R.string.filter_results);
                }
            }
        });
        itemLibraryListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        itemLibraryListView.setItemsCanFocus(false);
        NotifyAdapterThatItemLibraryListChanged();
        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                try {
                    if(itemLibraryChosenHashSet.size() > 0) {
                        final List<Object> itemIdLibraryChosenArrayList = new ArrayList<>();
                        for(Item i : itemLibraryChosenHashSet) {
                            itemIdLibraryChosenArrayList.add(i.getId());
                        }
                        new Client.ClientBuilder(ByteCommand.reAddItems, getPreferences(MODE_PRIVATE).getString("IpAddress", "127.0.0.1")).ItemIds(itemIdLibraryChosenArrayList).build().execute();
                    }
                    else {
                        final String itemName = itemNameView.getText().toString().trim();
                        if (!itemName.isEmpty()) {
                            final Item.ItemBuilder ib = new Item.ItemBuilder(itemName);
                            new Client.ClientBuilder(ByteCommand.addItem, getPreferences(MODE_PRIVATE).getString("IpAddress", "127.0.0.1")).Item(ib.build()).build().execute();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        alert.show().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    private class ItemsLibraryAdapter extends ArrayAdapter<Item> {
        public ItemsLibraryAdapter(Context context, ArrayList<Item> users) {
            super(context, 0, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
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
            } catch (Exception e) {
                e.printStackTrace();
            }
            changeColorLibrary(item, convertView);
            convertView.setTag(item.getId());
            // Return the completed view to render on screen
            return convertView;
        }
    }

    private void changeColorLibrary(Item item, View view) {
        if(itemLibraryChosenHashSet.contains(item)) {
            view.setBackgroundColor(Color.parseColor("#ff33b5e5"));
        } else {
            view.setBackgroundColor(Color.TRANSPARENT);
        }
    }
    //END Library Section


    //START Configure IP Section
    public void ConfigureIpAddress(MenuItem view)
    {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Configure Server I.P. Address");
        alert.setMessage("Set the I.P. Address to your Server");
        final EditText input = new EditText(this);
        alert.setView(input);
        input.setSingleLine(true);
        input.setText(getPreferences(MODE_PRIVATE).getString("IpAddress", "127.0.0.1"));
        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                try {
                    final SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
                    editor.putString("IpAddress", input.getText().toString().trim());
                    editor.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {}
        });
        alert.show();
    }
    //END Configure IP Section

    //START Generic Helpers
    private static float round(float d, int decimalPlace) {
        return BigDecimal.valueOf(d).setScale(decimalPlace,BigDecimal.ROUND_HALF_UP).floatValue();
    }
    //END Generic Helpers
}

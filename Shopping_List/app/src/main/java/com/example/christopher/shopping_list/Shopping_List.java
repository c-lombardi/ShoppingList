package com.example.christopher.shopping_list;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.Gravity;
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

import com.daimajia.swipe.adapters.ArraySwipeAdapter;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.Collections.sort;

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
    private static Session session;
    private static String SessionIdString = "SessionId";
    private static String SessionPhoneNumberString = "SessionPhoneNumber";
    private static String SessionAuthCodeString = "SessionAuthCode";
    private static String IpAddressString = "IpAddress";
    private static String ActualHardCodedIpAddress = "192.168.10.100";

    public static void ClearItemArrayList() {
        try {
            itemArrayList.clear();
        } catch (final Exception ignored) {
        }
    }

    public static void AddToItemArrayList(final Item newItem) {
        try {
            Item foundItem = null;
            for (final Item i : itemArrayList) {
                if (i.getId() == newItem.getId()) {
                    foundItem = i;
                    break;
                }
            }
            if (foundItem != null)
                itemArrayList.remove(foundItem);
            if(newItem != null)
                itemArrayList.add(newItem);
        } catch (final Exception ignored) {
        }
    }

    public static void removeItemFromItemArrayList(final Item oldItem) {
        try {
            itemArrayList.remove(oldItem);
            colorDict.remove(oldItem.getName());
        } catch (final Exception ignored) {
        }
    }

    //START Library Section
    public static void AddToLibraryItemArrayList(final Item newItem) {
        try {
            Item foundItem = null;
            for (final Item i : itemLibraryArrayList) {
                if (i.getId() == newItem.getId()) {
                    foundItem = i;
                    break;
                }
            }
            if (foundItem != null) {
                itemLibraryArrayList.remove(foundItem);
            }
            itemLibraryArrayList.add(newItem);
        } catch (final Exception ignored) {
        }
    }

    //START Generic Helpers
    private static float round(final float d, final int decimalPlace) {
        return BigDecimal.valueOf(d).setScale(decimalPlace, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping__list);
        colorDict = new HashMap<>();
        itemArrayList = new ArrayList<>();
        adapter = new ItemsAdapter(this, itemArrayList);
        itemListView = (ListView) findViewById(R.id.listView);
        itemListView.setEmptyView(findViewById(R.id.empty));
        itemListView.setAdapter(adapter);

        session = new Session();
        if (getPreferences(MODE_PRIVATE).getString(SessionIdString, "") != "")
            session.setSessionId(UUID.fromString(getPreferences(MODE_PRIVATE).getString(SessionIdString, "")));
        session.setSessionPhoneNumber(getPreferences(MODE_PRIVATE).getString(SessionPhoneNumberString, ""));
        session.setSessionAuthCode(getPreferences(MODE_PRIVATE).getString(SessionAuthCodeString, ""));

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    showPhoneNumberDialog(false);
                    showAuthCodeDialog();
                    itemListView.setEnabled(false);
                    new Client.ClientBuilder(ByteCommand.getItems, getPreferences(MODE_PRIVATE).getString(IpAddressString, ActualHardCodedIpAddress), session, Shopping_List.this).build().execute();
                } catch (final Exception ignored) {
                }
            }
        });
        itemListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                final View listItemView = getViewByPosition(position);
                final TextView listItemTextView = (TextView) listItemView.findViewById(R.id.itemName);
                final String itemName = listItemTextView.getText().toString();
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
                    for (final Item item : itemArrayList) {
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
                            for (final Item item : itemArrayList) {
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
                                        new Client.ClientBuilder(ByteCommand.updateItem, getPreferences(MODE_PRIVATE).getString(IpAddressString, ActualHardCodedIpAddress), session, Shopping_List.this).Item(i).build().execute();
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
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        showPhoneNumberDialog(false);
        showAuthCodeDialog();
    }

    private void showPhoneNumberDialog(final boolean override) {
        if (session.getSessionPhoneNumber().isEmpty() || override) {
            final AlertDialog.Builder alert = new AlertDialog.Builder(Shopping_List.this);
            alert.setTitle("Enter Your Phone Number To Proceed");
            final LayoutInflater createEnterPhoneNumberInflater = Shopping_List.this.getLayoutInflater();
            final View inflatedView = createEnterPhoneNumberInflater.inflate(R.layout.phone_number_fragment, null);
            alert.setView(inflatedView);
            final EditText phoneNumberView = (EditText) inflatedView.findViewById(R.id.phoneNumber);
            phoneNumberView.setText(getPreferences(MODE_PRIVATE).getString(SessionPhoneNumberString, ""));
            alert.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int which) {
                    try {
                        final String inputtedPhoneNumber = phoneNumberView.getText().toString().trim();
                        final SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
                        if(override) {
                            //clear Auth Code and Session Id
                            editor.putString(SessionAuthCodeString, "");
                            editor.putString(SessionIdString, "");

                            session.setSessionAuthCode("");
                            session.setSessionId(null);
                            session.setSessionPhoneNumber(inputtedPhoneNumber);

                            ClearItemArrayList();
                            NotifyAdapterThatItemListChanged();
                        }
                        else {
                            session.setSessionPhoneNumber(inputtedPhoneNumber);
                        }
                        editor.putString(SessionPhoneNumberString, inputtedPhoneNumber);
                        editor.apply();
                        new Client.ClientBuilder(ByteCommand.requestNewAuthCode, getPreferences(MODE_PRIVATE).getString(IpAddressString, ActualHardCodedIpAddress), session, Shopping_List.this).build().execute();
                        showAuthCodeDialog();
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(final DialogInterface dialog, final int whichButton) {
                    try {
                        dialog.dismiss();
                    } catch (final Exception ignored) {
                    }
                }
            });
            alert.show();
        }
    }

    private void showAuthCodeDialog() {
        if (session.getSessionAuthCode().isEmpty() && !session.getSessionPhoneNumber().isEmpty()) {
            final AlertDialog.Builder alert = new AlertDialog.Builder(Shopping_List.this);
            alert.setTitle("Please enter your authorization code.");
            alert.setMessage("Or request a new one be sent to you at " + session.getSessionPhoneNumber());
            final LayoutInflater createEnterAccessCodeInflater = Shopping_List.this.getLayoutInflater();
            final View inflatedView = createEnterAccessCodeInflater.inflate(R.layout.access_code_fragment, null);
            alert.setView(inflatedView);
            final EditText phoneNumberView = (EditText) inflatedView.findViewById(R.id.accessCode);
            alert.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int which) {
                    try {
                        final String inputtedAuthCode = phoneNumberView.getText().toString().trim();
                        final SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();

                        editor.putString(SessionAuthCodeString, inputtedAuthCode);
                        session.setSessionAuthCode(inputtedAuthCode);

                        editor.apply();
                        new Client.ClientBuilder(ByteCommand.getItems, getPreferences(MODE_PRIVATE).getString(IpAddressString, ActualHardCodedIpAddress), session, Shopping_List.this).build().execute();
                    } catch (final Exception ignored) {
                    }
                }
            });
            alert.setNegativeButton("Request Code", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int which) {
                    try {
                        new Client.ClientBuilder(ByteCommand.requestNewAuthCode, getPreferences(MODE_PRIVATE).getString(IpAddressString, ActualHardCodedIpAddress), session, Shopping_List.this).build().execute();
                    } catch (final Exception ignored) {
                    }
                    showAuthCodeDialog();
                }
            });
            alert.show();
        }
    }

    //START List Section
    public void setSwipeRefreshlayoutRefreshing(final boolean refresh) {
        try {
            if (swipeRefreshLayout != null)
                swipeRefreshLayout.setRefreshing(refresh);
        } catch (final Exception ignored) {
        }
    }

    public void handleDeleteGreenItemsButton() {
        if (colorDict.containsValue(Color.GREEN)) {
            deleteItemsMenuItem.setVisible(true);
            deleteItemsMenuItem.setEnabled(true);
        } else {
            deleteItemsMenuItem.setVisible(false);
            deleteItemsMenuItem.setEnabled(false);
        }
    }

    public void setSession(final UUID sId) {
        final SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        session.setSessionId(sId);

        if (session.getSessionId() != null) {
            editor.putString(SessionIdString, sId.toString());
        } else {
            editor.putString(SessionAuthCodeString, "");
            session.setSessionAuthCode("");
        }
        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                try {
                    new Client.ClientBuilder(ByteCommand.getItems, getPreferences(MODE_PRIVATE).getString(IpAddressString, ActualHardCodedIpAddress), session, Shopping_List.this).build().execute();
                } catch (final IOException ignored) {
                }
            }
        });
    }

    public void NotifyAdapterThatItemListChanged() {
        try {
            sort(itemArrayList);
            adapter.notifyDataSetChanged();
            updateItemTotalTitle();
            itemListView.setEnabled(true);
        } catch (final Exception ignored) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shopping__list, menu);
        itemTotalMenuItem = menu.findItem(R.id.itemTotals);
        deleteItemsMenuItem = menu.findItem(R.id.deleteGreenItems);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        final int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            return true;
        }
        return id == R.id.action_settings || super.onOptionsItemSelected(item);

    }

    public void DisplayToast(final String str) {
        try {
            final Toast toast = Toast.makeText(Shopping_List.this.getApplicationContext(), str, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, -20, 25);
            toast.show();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toast.cancel();
                }
            }, 500);
        } catch (final Exception ignored) {
        }
    }

    public void removeItemsFromItemArrayList(final List<Integer> itemIds) {
        final HashSet<Item> itemHashSet = new HashSet<>(itemArrayList);
        final Iterator it = itemHashSet.iterator();
        if (it.hasNext()) {
            do {
                final Item nextItem = ((Item) it.next());
                if (itemIds.contains(nextItem.getId())) {
                    removeItemFromItemArrayList(nextItem);
                }
            } while (it.hasNext());
        }
    }

    public void DeleteGreenItems(final MenuItem item) throws IOException {
        final Iterator it = colorDict.entrySet().iterator();
        final List<Item> foundItems = new ArrayList<>();
        while (it.hasNext()) {
            final Map.Entry itemNameAndColor = (Map.Entry) it.next();
            if (Integer.parseInt(itemNameAndColor.getValue().toString()) == Color.GREEN) {
                final String name = itemNameAndColor.getKey().toString().trim();
                final Item localItem = new Item();
                localItem.setId(0);
                localItem.setName(name);
                final int indexOf = itemArrayList.indexOf(localItem);
                final Item foundItem = itemArrayList.get(indexOf);
                foundItems.add(foundItem);
            }
        }
        final List<Integer> foundItemIds = new ArrayList<>();
        if (foundItems.size() > 0) {
            for (final Item i : foundItems) {
                foundItemIds.add(i.getId());
            }
            new Client.ClientBuilder(ByteCommand.removeItemsFromList, getPreferences(MODE_PRIVATE).getString(IpAddressString, ActualHardCodedIpAddress), session, Shopping_List.this).ItemIds(foundItemIds).build().execute();
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
            DisplayToast(getResources().getString(R.string.NotFound));
        } else if (color == Color.TRANSPARENT) {
            colorDict.put(itemName, Color.GREEN);
            listItemView.setBackgroundColor(Color.GREEN);
            DisplayToast(getResources().getString(R.string.Found));
        }
    }

    private void updateItemTotalTitle() {
        float totalVal = 0;
        for (final Item i : itemArrayList) {
            try {
                if (colorDict.get(i.getName()) == Color.GREEN) {
                    totalVal += i.getBestPrice();
                }
            } catch (final Exception ignored) {
            }
        }
        itemTotalMenuItem.setTitle(String.valueOf(round(totalVal, 2)));

    }
    //END List Section

    private View getViewByPosition(final int pos) {
        final int firstListItemPosition = itemListView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + itemListView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return itemListView.getAdapter().getView(pos, null, itemListView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return itemListView.getChildAt(childIndex);
        }
    }

    public void NotifyAdapterThatItemLibraryListChanged() {
        try {
            sort(itemLibraryArrayList);
            libraryAdapter.notifyDataSetChanged();
        } catch (final Exception ignored) {
        }
    }

    public void AddNewItem(final MenuItem view) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final LayoutInflater createEditItemInflater = Shopping_List.this.getLayoutInflater();
        final View inflatedView = createEditItemInflater.inflate(R.layout.create_item_layout, null);
        alert.setView(inflatedView);
        final TextView libraryItemChosenHashSetCount = (TextView) inflatedView.findViewById(R.id.selectedItemsCount);
        itemLibraryArrayList = new ArrayList<>();
        itemLibraryChosenHashSet = new HashSet<>();
        libraryAdapter = new ItemsLibraryAdapter(inflatedView.getContext(), itemLibraryArrayList);
        final EditText itemNameView = (EditText) inflatedView.findViewById(R.id.itemName);
        itemNameView.addTextChangedListener(new DelayedTextWatcher(200) {
            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
                final List<Item> localItemsNotFoundToBeRemoved = new ArrayList<>();
                for (final Item i : itemLibraryArrayList) {
                    if (!i.getName().toLowerCase().contains(s.toString()))
                        localItemsNotFoundToBeRemoved.add(i);
                }
                itemLibraryArrayList.removeAll(localItemsNotFoundToBeRemoved);
                NotifyAdapterThatItemLibraryListChanged();
            }

            @Override
            public void afterTextChangedDelayed(final Editable s) {
                try {
                    final Item localItem = new Item();
                    localItem.setName(s.toString());
                    localItem.setSessionId(session.getSessionId());
                    new Client.ClientBuilder(ByteCommand.getLibraryItemsThatContain, getPreferences(MODE_PRIVATE).getString(IpAddressString, ActualHardCodedIpAddress), session, Shopping_List.this).Item(localItem).build().execute();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        });
        final ListView itemLibraryListView = (ListView) inflatedView.findViewById(R.id.libraryItemsListView);
        itemLibraryListView.setAdapter(libraryAdapter);
        itemLibraryListView.setEmptyView(inflatedView.findViewById(R.id.createEmpty));
        try {
            new Client.ClientBuilder(ByteCommand.getLibrary, getPreferences(MODE_PRIVATE).getString(IpAddressString, ActualHardCodedIpAddress), session, Shopping_List.this).build().execute();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        itemLibraryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                final TextView itemNameTextView = (TextView) view.findViewById(R.id.itemName);
                final String chosenItemName = itemNameTextView.getText().toString();
                final Item foundItem = new Item();
                foundItem.setId(Integer.parseInt(view.getTag().toString()));
                foundItem.setName(chosenItemName);
                foundItem.setSessionId(session.getSessionId());
                if (itemLibraryChosenHashSet.contains(foundItem)) {
                    view.setBackgroundColor(Color.TRANSPARENT);
                    itemLibraryChosenHashSet.remove(foundItem);
                } else {
                    final Item i = itemLibraryArrayList.get(itemLibraryArrayList.indexOf(foundItem));
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
        alert.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int whichButton) {
                try {
                    if (itemLibraryChosenHashSet.size() > 0) {
                        final List<Integer> itemIdLibraryChosenArrayList = new ArrayList<>();
                        for (final Item i : itemLibraryChosenHashSet) {
                            itemIdLibraryChosenArrayList.add(i.getId());
                        }
                        new Client.ClientBuilder(ByteCommand.reAddItems, getPreferences(MODE_PRIVATE).getString(IpAddressString, ActualHardCodedIpAddress), session, Shopping_List.this).ItemIds(itemIdLibraryChosenArrayList).build().execute();
                    } else {
                        final String itemName = itemNameView.getText().toString().trim();
                        if (!itemName.isEmpty()) {
                            final Item i = new Item();
                            i.setId(0);
                            i.setName(itemName);
                            i.setSessionId(session.getSessionId());
                            new Client.ClientBuilder(ByteCommand.addItem, getPreferences(MODE_PRIVATE).getString(IpAddressString, ActualHardCodedIpAddress), session, Shopping_List.this).Item(i).build().execute();
                        }
                    }
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        });

        alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int whichButton) {
            }
        });
        alert.show().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    private void changeColorLibrary(final Item item, final View view) {
        if (itemLibraryChosenHashSet.contains(item)) {
            view.setBackgroundColor(Color.parseColor("#ff33b5e5"));
        } else {
            view.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    //START Configure IP Section
    public void ConfigureIpAddress(final MenuItem view) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Configure Server I.P. Address");
        alert.setMessage("Set the I.P. Address to your Server");
        final EditText input = new EditText(this);
        alert.setView(input);
        input.setSingleLine(true);
        input.setText(getPreferences(MODE_PRIVATE).getString(IpAddressString, ActualHardCodedIpAddress));
        alert.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int whichButton) {
                try {
                    final SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
                    editor.putString(IpAddressString, input.getText().toString().trim());
                    editor.apply();
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        });

        alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int whichButton) {
            }
        });
        alert.show();
    }
    //END Configure IP Section

    //START Configure Phone Number Section
    public void ConfigurePhoneNumber(final MenuItem view) {
        showPhoneNumberDialog(true);
    }
    //END Configure Phone Number Section
    private class ItemsAdapter extends ArrayAdapter<Item> {
        public ItemsAdapter(final Context context, final ArrayList<Item> users) {
            super(context, 0, users);
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
    //END Configure IP Section

    private class ItemsLibraryAdapter extends ArrayAdapter<Item> {
        public ItemsLibraryAdapter(final Context context, final ArrayList<Item> users) {
            super(context, 0, users);
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
    //END Generic Helpers
}

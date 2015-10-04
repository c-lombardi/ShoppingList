package com.example.christopher.shopping_list;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Shopping_List extends AppCompatActivity {
    static ArrayList<Item> ItemArrayList;
    static ItemsAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping__list);
        ItemArrayList = new ArrayList<>();
        adapter = new ItemsAdapter(this, ItemArrayList);
        final ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        final SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    new Client.ClientBuilder(ByteCommand.getItems, (ListView) findViewById(R.id.listView), (SwipeRefreshLayout) findViewById(R.id.swipe_container), getPreferences(MODE_PRIVATE).getString("IpAddress", "127.0.0.1")).build().execute();
                } catch (Exception ex) {
                    System.out.println("fail");
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(Shopping_List.this);
                alert.setTitle("Remove or Edit Item");
                alert.setMessage("I love you, Alina!");
                final String itemViewText = listView.getItemAtPosition(position).toString();
                final String keyword = itemViewText.split(",")[1];
                alert.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final AlertDialog.Builder alert = new AlertDialog.Builder(Shopping_List.this);
                        alert.setTitle("Edit Item");
                        alert.setMessage("I love you, Alina!");
                        final LayoutInflater createEditItemInflater = Shopping_List.this.getLayoutInflater();
                        final View inflatedView = createEditItemInflater.inflate(R.layout.create_edit_item_layout, null);
                        alert.setView(inflatedView);
                        final EditText itemNameView = (EditText) inflatedView.findViewById(R.id.itemName);
                        final EditText bestPriceView = (EditText) inflatedView.findViewById(R.id.bestPrice);
                        final EditText storeNameView = (EditText) inflatedView.findViewById(R.id.storeName);
                        try {
                            for (Item item : ItemArrayList) {
                                if (item.getName().equals(keyword)) {
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
                                    for (Item item : ItemArrayList) {
                                        if (item.getName().equals(keyword)) {
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
                                                new Client.ClientBuilder(ByteCommand.updateItem, (ListView) findViewById(R.id.listView), (SwipeRefreshLayout) findViewById(R.id.swipe_container), ib.build(), getPreferences(MODE_PRIVATE).getString("IpAddress", "127.0.0.1")).build().execute();
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
                   public void onClick(DialogInterface dialog, int whichButton) {}
                });

                alert.setNegativeButton("Remove", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        try {
                            for (Item i : ItemArrayList) {
                                if (i.getName().equals(keyword)) {
                                    ItemArrayList.remove(i);
                                    new Client.ClientBuilder(ByteCommand.removeItemFromList, (ListView) findViewById(R.id.listView), (SwipeRefreshLayout) findViewById(R.id.swipe_container), i, getPreferences(MODE_PRIVATE).getString("IpAddress", "127.0.0.1")).build().execute();
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
    }

    @Override
    protected void onStart(){
        super.onStart();
        final SwipeRefreshLayout layout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        layout.post(new Runnable() {
            @Override
            public void run() {
                new Client.ClientBuilder(ByteCommand.getItems, (ListView) findViewById(R.id.listView), layout, getPreferences(MODE_PRIVATE).getString("IpAddress", "127.0.0.1")).build().execute();
            }
        });
    }

    public static void ClearItemArrayList(){
        try {
            ItemArrayList.clear();
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
            for(Item i : ItemArrayList)
            {
                if(i.getId() == newItem.getId()) {
                    foundItem = i;
                    break;
                }
            }
            if(foundItem != null) {
                ItemArrayList.remove(foundItem);
            }
            ItemArrayList.add(newItem);
        }
        catch (Exception ex)
        {
            System.out.print("fail");
        }
    }
    public void NotifyAdapterThatItemListChanged(){
        try {
            adapter.notifyDataSetChanged();
        }
        catch (Exception ex)
        {
            System.out.print("fail1");
        }
    }

    public void AddNewItem(MenuItem view)
    {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Add Item");
        alert.setMessage("I love you, Alina!");
        final LayoutInflater createEditItemInflater = Shopping_List.this.getLayoutInflater();
        final View inflatedView = createEditItemInflater.inflate(R.layout.create_edit_item_layout, null);
        alert.setView(inflatedView);
        final EditText itemNameView = (EditText) inflatedView.findViewById(R.id.itemName);
        final EditText bestPriceView = (EditText) inflatedView.findViewById(R.id.bestPrice);
        final EditText storeNameView = (EditText) inflatedView.findViewById(R.id.storeName);
        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                try {
                    final String itemName = itemNameView.getText().toString().trim();
                    if (!itemName.isEmpty()) {
                        final Item.ItemBuilder ib = new Item.ItemBuilder(itemName);
                        final String bestPriceString = bestPriceView.getText().toString().trim();
                        if (!bestPriceString.isEmpty()) {
                            ib.bestPrice(Float.parseFloat(bestPriceString));
                        }
                        final String storeName = storeNameView.getText().toString().trim();
                        if (!storeName.isEmpty()) {
                            Store.StoreBuilder sb = new Store.StoreBuilder(storeName);
                            ib.store(sb.build());
                        }
                        new Client.ClientBuilder(ByteCommand.addItem, (ListView) findViewById(R.id.listView), (SwipeRefreshLayout) findViewById(R.id.swipe_container), ib.build(), getPreferences(MODE_PRIVATE).getString("IpAddress", "127.0.0.1")).build().execute();
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
        alert.show();
    }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shopping__list, menu);
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

    public class ItemsAdapter extends ArrayAdapter<Item> {
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
            // Populate the data into the template view using the data object
            try {
                if(item.getStore() != null) {
                    tvName.setText(item.getName() + " @ " + item.getBestPrice());
                    tvHome.setText("From: " + item.getStore().getName());
                } else {
                    tvName.setText(item.getName() + " @ " + item.getBestPrice());
                    tvHome.setText("");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Return the completed view to render on screen
            return convertView;
        }
    }
}

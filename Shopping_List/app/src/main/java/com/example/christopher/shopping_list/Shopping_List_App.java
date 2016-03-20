package com.example.christopher.shopping_list;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.christopher.shopping_list.CRUD.Items_CRUD;
import com.example.christopher.shopping_list.CRUD.LibraryItem_CRUD;
import com.example.christopher.shopping_list.CRUD.Shopping_List_CRUD;
import com.example.christopher.shopping_list.Models.Session;
import com.example.christopher.shopping_list.Models.Shopping_List;
import com.example.christopher.shopping_list.Server.ByteCommand;
import com.example.christopher.shopping_list.Server.Client;

import java.io.IOException;
import java.util.UUID;

public class Shopping_List_App extends AppCompatActivity {
    private static Shopping_List active_shopping_List;
    private static MenuItem itemTotalMenuItem, addButton, deleteItemsMenuItem;
    private static Shopping_List_CRUD shopping_list_crud;
    private static Items_CRUD items_crud;
    private static LibraryItem_CRUD libraryItem_crud;
    private static Session session;
    //START Generic Helpers

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping__list);
        //need to do shopping_list stuff here
        active_shopping_List = new Shopping_List();
        session = new Session();

        if (getPreferences(MODE_PRIVATE).getString(StaticVariables.SessionIdString, "") != "")
            session.setSessionId(UUID.fromString(getPreferences(MODE_PRIVATE).getString(StaticVariables.SessionIdString, "")));
        session.setSessionPhoneNumber(getPreferences(MODE_PRIVATE).getString(StaticVariables.SessionPhoneNumberString, ""));
        session.setSessionAuthCode(getPreferences(MODE_PRIVATE).getString(StaticVariables.SessionAuthCodeString, ""));

        shopping_list_crud = new Shopping_List_CRUD(session, this, addButton);
        items_crud = new Items_CRUD(session, this, itemTotalMenuItem, deleteItemsMenuItem, addButton);
        libraryItem_crud = new LibraryItem_CRUD(session, this);
        //set menu item for add to add a shopping list.
        shopping_list_crud.SetUpCreate();
        shopping_list_crud.SetUpUpdate();
        shopping_list_crud.SetUpRead();
        //session stuff on load

        showPhoneNumberDialog(false);
        showAuthCodeDialog();
    }


    public void setActiveShopping_List(final Shopping_List shopping_list) {
        active_shopping_List = shopping_list;
    }

    public Shopping_List getActiveShopping_List() {
        return active_shopping_List;
    }

    public static Items_CRUD getItems_CRUD(){
        return items_crud;
    }

    public static LibraryItem_CRUD getLibraryItem_crud() {
        return libraryItem_crud;
    }

    public MenuItem getItemTotalMenuItem() {
        return itemTotalMenuItem;
    }

    public MenuItem getAddButton() {
        return addButton;
    }

    public MenuItem getDeleteItemsMenuItem() {
        return deleteItemsMenuItem;
    }

    //START Library Section

    public static void AddToOrReplaceShoppingListArrayList(final Shopping_List newShopping_List) {
        shopping_list_crud.addToArrayList(newShopping_List);
    }


    public void showPhoneNumberDialog(final boolean override) {
        if (session.getSessionPhoneNumber().isEmpty() || override) {
            final AlertDialog.Builder alert = new AlertDialog.Builder(Shopping_List_App.this);
            alert.setTitle("Enter Your Phone Number To Proceed");
            final LayoutInflater createEnterPhoneNumberInflater = Shopping_List_App.this.getLayoutInflater();
            final View inflatedView = createEnterPhoneNumberInflater.inflate(R.layout.phone_number_fragment, null);
            alert.setView(inflatedView);
            final EditText phoneNumberView = (EditText) inflatedView.findViewById(R.id.phoneNumber);
            phoneNumberView.setText(getPreferences(MODE_PRIVATE).getString(StaticVariables.SessionPhoneNumberString, ""));
            alert.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int which) {
                    try {
                        final String inputtedPhoneNumber = phoneNumberView.getText().toString().trim();
                        final SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
                        if(override) {
                            //clear Auth Code and Session Id
                            editor.putString(StaticVariables.SessionAuthCodeString, "");
                            editor.putString(StaticVariables.SessionIdString, "");

                            session.setSessionAuthCode("");
                            session.setSessionId(null);
                            session.setSessionPhoneNumber(inputtedPhoneNumber);
                        }
                        else {
                            session.setSessionPhoneNumber(inputtedPhoneNumber);
                        }
                        editor.putString(StaticVariables.SessionPhoneNumberString, inputtedPhoneNumber);
                        editor.apply();
                        new Client.ClientBuilder(ByteCommand.requestNewAuthCode, getPreferences(MODE_PRIVATE).getString(StaticVariables.IpAddressString, StaticVariables.ActualHardCodedIpAddress), session, active_shopping_List, Shopping_List_App.this).build().execute();
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

    public void showAuthCodeDialog() {
        if (session.getSessionAuthCode().isEmpty() && !session.getSessionPhoneNumber().isEmpty()) {
            final AlertDialog.Builder alert = new AlertDialog.Builder(Shopping_List_App.this);
            alert.setTitle("Please enter your authorization code.");
            alert.setMessage("Or request a new one be sent to you at " + session.getSessionPhoneNumber());
            final LayoutInflater createEnterAccessCodeInflater = Shopping_List_App.this.getLayoutInflater();
            final View inflatedView = createEnterAccessCodeInflater.inflate(R.layout.access_code_fragment, null);
            alert.setView(inflatedView);
            final EditText phoneNumberView = (EditText) inflatedView.findViewById(R.id.accessCode);
            alert.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int which) {
                    try {
                        final String inputtedAuthCode = phoneNumberView.getText().toString().trim();
                        final SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();

                        editor.putString(StaticVariables.SessionAuthCodeString, inputtedAuthCode);
                        session.setSessionAuthCode(inputtedAuthCode);

                        editor.apply();
                        new Client.ClientBuilder(ByteCommand.getListOfShoppingLists, getPreferences(MODE_PRIVATE).getString(StaticVariables.IpAddressString, StaticVariables.ActualHardCodedIpAddress), session, active_shopping_List, Shopping_List_App.this).build().execute();
                    } catch (final Exception ignored) {
                    }
                }
            });
            alert.setNegativeButton("Request Code", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int which) {
                    try {
                        new Client.ClientBuilder(ByteCommand.requestNewAuthCode, getPreferences(MODE_PRIVATE).getString(StaticVariables.IpAddressString, StaticVariables.ActualHardCodedIpAddress), session, active_shopping_List, Shopping_List_App.this).build().execute();
                    } catch (final Exception ignored) {
                    }
                    showAuthCodeDialog();
                }
            });
            alert.show();
        }
    }

    //START List Section

    public void setSession(final UUID sId) {
        final SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        session.setSessionId(sId);

        if (session.getSessionId() != null) {
            editor.putString(StaticVariables.SessionIdString, sId.toString());
        } else {
            editor.putString(StaticVariables.SessionAuthCodeString, "");
            session.setSessionAuthCode("");
        }
        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
        shopping_list_crud.SetUpRead();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shopping__list, menu);
        itemTotalMenuItem = menu.findItem(R.id.itemTotals);
        deleteItemsMenuItem = menu.findItem(R.id.deleteGreenItems);
        addButton = menu.findItem(R.id.action_add);
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
            final Toast toast = Toast.makeText(Shopping_List_App.this.getApplicationContext(), str, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, -20, 25);
            toast.show();

            Handler handler = new Handler();
            handler.postDelayed(toast::cancel, 500);
        } catch (final Exception ignored) {
        }
    }

    public void DeleteGreenItems(final MenuItem item) throws IOException {
        items_crud.DeleteGreenItems();
    }
    //END List Section

    public void AddNewShopping_List(final MenuItem view) {
    }

    public void AddNewItem(final MenuItem view) {
        items_crud.SetUpCreate();
    }

    //START Configure IP Section
    public void ConfigureIpAddress(final MenuItem view) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Configure Server I.P. Address");
        alert.setMessage("Set the I.P. Address to your Server");
        final EditText input = new EditText(this);
        alert.setView(input);
        input.setSingleLine(true);
        input.setText(getPreferences(MODE_PRIVATE).getString(StaticVariables.IpAddressString, StaticVariables.ActualHardCodedIpAddress));
        alert.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int whichButton) {
                try {
                    final SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
                    editor.putString(StaticVariables.IpAddressString, input.getText().toString().trim());
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
    //END Configure IP Section


    //END Generic Helpers
}

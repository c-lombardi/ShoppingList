package com.example.christopher.shopping_list_app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import com.example.christopher.shopping_list.R;
import com.example.christopher.shopping_list_app.Models.Session;
import com.example.christopher.shopping_list_app.Models.ShoppingList;
import com.example.christopher.shopping_list_app.Server.ByteCommand;
import com.example.christopher.shopping_list_app.Server.Client;

import java.io.IOException;
import java.util.UUID;

public class Shopping_List extends AppCompatActivity {
    private static ShoppingList active_shopping_List;
    private static ShoppingListFragment shoppingListFragment;
    private static ItemsListFragment itemsListFragment;
    private static FragmentManager fragmentManager;
    private static Session session;
    private static boolean active;
    //START Generic Helpers

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        setActive(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
        active_shopping_List = new ShoppingList();
        session = new Session();
        if (getPreferences(MODE_PRIVATE).getString(StaticVariables.SessionIdString, "") != "")
            session.setSessionId(UUID.fromString(getPreferences(MODE_PRIVATE).getString(StaticVariables.SessionIdString, "")));
        session.setSessionPhoneNumber(getPreferences(MODE_PRIVATE).getString(StaticVariables.SessionPhoneNumberString, ""));
        session.setSessionAuthCode(getPreferences(MODE_PRIVATE).getString(StaticVariables.SessionAuthCodeString, ""));

        showPhoneNumberDialog(false, this);
        showAuthCodeDialog(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fragmentManager = getSupportFragmentManager();
        changeTransaction(shoppingListFragment, itemsListFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(!itemsListFragment.isHidden()) {
            itemsListFragment.ClearItemArrayList();
        }
    }

    private static void hideFragment(final Fragment fragmentToHide, final FragmentTransaction fragmentTransaction) {
        fragmentTransaction.hide(fragmentToHide);
    }

    private static void showFragment(final Fragment fragmentToShow, final FragmentTransaction fragmentTransaction) {
        fragmentTransaction.show(fragmentToShow);
    }

    public static void changeTransaction(final Fragment newFragment, final Fragment oldFragment) {
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (oldFragment != null)
            hideFragment(oldFragment, fragmentTransaction);
        showFragment(newFragment, fragmentTransaction);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    public static void setActiveShopping_List(final ShoppingList shopping_list) {
        active_shopping_List = shopping_list;
        itemsListFragment.refreshSwipeRefreshLayout();
    }

    public static void setItemsListFragment(final ItemsListFragment ilf) {
        itemsListFragment = ilf;
    }

    public static void setShoppingListFragment(final ShoppingListFragment slf) {
        shoppingListFragment = slf;
    }

    public static ShoppingList getActiveShopping_List() {
        return active_shopping_List;
    }

    public static Session getSession() { return session; }

    public static ItemsListFragment getItemsListFragment() {
        return itemsListFragment;
    }


    //START Library Section

    public static void showPhoneNumberDialog(final boolean override, final Activity activity) {
        if (session.getSessionPhoneNumber().isEmpty() || override) {
            final AlertDialog.Builder alert = new AlertDialog.Builder(activity);
            alert.setTitle("Enter Your Phone Number To Proceed");
            final LayoutInflater createEnterPhoneNumberInflater = activity.getLayoutInflater();
            final View inflatedView = createEnterPhoneNumberInflater.inflate(R.layout.phone_number_fragment, null);
            alert.setView(inflatedView);
            final EditText phoneNumberView = (EditText) inflatedView.findViewById(R.id.phoneNumber);
            phoneNumberView.setText(activity.getPreferences(MODE_PRIVATE).getString(StaticVariables.SessionPhoneNumberString, ""));
            alert.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int which) {
                    final String inputtedPhoneNumber = phoneNumberView.getText().toString().trim();
                    final SharedPreferences.Editor editor = activity.getPreferences(MODE_PRIVATE).edit();
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
                    new Client.ClientBuilder(ByteCommand.requestNewAuthCode, activity.getPreferences(MODE_PRIVATE).getString(StaticVariables.IpAddressString, StaticVariables.ActualHardCodedIpAddress), null).build().execute();
                    showAuthCodeDialog(activity);
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

    public static void showAuthCodeDialog(final Activity activity) {
        if (session.getSessionAuthCode().isEmpty() && !session.getSessionPhoneNumber().isEmpty()) {
            final AlertDialog.Builder alert = new AlertDialog.Builder(activity);
            alert.setTitle("Please enter your authorization code.");
            alert.setMessage("Or request a new one be sent to you at " + session.getSessionPhoneNumber());
            final LayoutInflater createEnterAccessCodeInflater = activity.getLayoutInflater();
            final View inflatedView = createEnterAccessCodeInflater.inflate(R.layout.access_code_fragment, null);
            alert.setView(inflatedView);
            final EditText phoneNumberView = (EditText) inflatedView.findViewById(R.id.accessCode);
            alert.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int which) {
                    try {
                        final String inputtedAuthCode = phoneNumberView.getText().toString().trim();
                        final SharedPreferences.Editor editor = activity.getPreferences(MODE_PRIVATE).edit();

                        editor.putString(StaticVariables.SessionAuthCodeString, inputtedAuthCode);
                        session.setSessionAuthCode(inputtedAuthCode);
                        editor.apply();
                        changeTransaction(shoppingListFragment, null);
                    } catch (final Exception ignored) {
                    }
                }
            });
            alert.setNegativeButton("Request Code", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int which) {
                    try {
                        new Client.ClientBuilder(ByteCommand.requestNewAuthCode, activity.getPreferences(MODE_PRIVATE).getString(StaticVariables.IpAddressString, StaticVariables.ActualHardCodedIpAddress), null).build().execute();
                    } catch (final Exception ignored) {
                    }
                    showAuthCodeDialog(activity);
                }
            });
            alert.show();
        }
    }

    //START List Section

    public static void setSession(final UUID sId, final Activity activity) {
        final SharedPreferences.Editor editor = activity.getPreferences(MODE_PRIVATE).edit();
        session.setSessionId(sId);

        if (session.getSessionId() != null) {
            editor.putString(StaticVariables.SessionIdString, sId.toString());
        } else {
            editor.putString(StaticVariables.SessionAuthCodeString, "");
            session.setSessionAuthCode("");
        }
        editor.apply();
    }

    public static void DisplayToast(final String str, final Activity activity) {
        final Toast toast = Toast.makeText(activity.getApplicationContext(), str, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, -20, 25);
        toast.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toast.cancel();
                }
            }, 500);
    }

    //END List Section
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

    public void DeleteGreenItems(final MenuItem item) throws IOException {
        itemsListFragment.DeleteGreenItems();
    }
    public void AddNewShopping_List(final MenuItem item) {
        shoppingListFragment.AddNewShopping_List();
    }
    public void AddNewItem(final MenuItem item) {
        itemsListFragment.AddNewItem();
    }
    //END Configure IP Section

    //START Configure Phone Number Section
    public void ConfigurePhoneNumber(final MenuItem view) {
        showPhoneNumberDialog(true, this);
    }
    //END Configure IP Section


    //END Generic Helpers

    public static boolean isActive() {
        return active;
    }

    public static void setActive(boolean active) {
        Shopping_List.active = active;
    }

}

package com.example.christopher.shopping_list.CRUD;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.christopher.shopping_list.Models.Session;
import com.example.christopher.shopping_list.Shopping_List_App;

import java.io.IOException;
import java.util.List;

/**
 * Created by Christopher on 3/18/2016.
 */
public interface CRUD {

    abstract class SetUpCRUDOperations<T> {
        private List<T> typeList;
        private ArrayAdapter<T> arrayAdapter;
        private ListView listView;
        private Session session;
        private Shopping_List_App shopping_list_app;

        public void setShopping_list_app(final Shopping_List_App shopping_list_app) {
            this.shopping_list_app = shopping_list_app;
        }

        public void setTypeList(final List<T> typeList) {
            this.typeList = typeList;
        }

        public void setListView(final ListView listView) {
            this.listView = listView;
        }

        public void setSession(final Session session) {
            this.session = session;
        }

        public void setArrayAdapter(final ArrayAdapter<T> arrayAdapter) {
            this.arrayAdapter = arrayAdapter;
        }

        public SetUpCRUDOperations(){
        }

        public Shopping_List_App getShopping_list_app() {
            return shopping_list_app;
        }

        public List<T> getTypeList() {
            return typeList;
        }

        public ArrayAdapter getArrayAdapter() {
            return arrayAdapter;
        }

        public ListView getListView() {
            return listView;
        }

        public Session getSession() {
            return session;
        }

        public View getViewByPosition(final int pos) {
            final int firstListItemPosition = getListView().getFirstVisiblePosition();
            final int lastListItemPosition = firstListItemPosition + getListView().getChildCount() - 1;

            if (pos < firstListItemPosition || pos > lastListItemPosition) {
                return getListView().getAdapter().getView(pos, null, getListView());
            } else {
                final int childIndex = pos - firstListItemPosition;
                return getListView().getChildAt(childIndex);
            }
        }

        public abstract void SetUpCreate();
        public abstract void SetUpRead();
        public abstract void SetUpUpdate();
        public abstract void Delete();
        public abstract void addToArrayList(T t);
        public abstract void removeFromArrayList(T t);

        public abstract class typeListAdapter extends ArrayAdapter<T> {
            public typeListAdapter(final Context context, final List<T> objects) {
                super(context, 0, objects);
            }
        }
    }
}

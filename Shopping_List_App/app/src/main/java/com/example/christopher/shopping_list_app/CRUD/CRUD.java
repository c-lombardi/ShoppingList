package com.example.christopher.shopping_list_app.CRUD;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

/**
 * Created by Christopher on 3/18/2016.
 */
public interface CRUD {

    abstract class SetUpCRUDOperations<T> {
        private List<T> typeList;
        private ArrayAdapter<T> arrayAdapter;
        private ListView listView;
        private Fragment fragment;

        public void setFragment(final Fragment f) {
            this.fragment = f;
        }

        public void setTypeList(final List<T> typeList) {
            this.typeList = typeList;
        }

        public void setListView(final ListView listView) {
            this.listView = listView;
        }

        public void setArrayAdapter(final ArrayAdapter<T> arrayAdapter) {
            this.arrayAdapter = arrayAdapter;
        }

        public SetUpCRUDOperations(){
        }

        public Fragment getFragment() {
            return fragment;
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

        public void ClearArrayList() {
            try {
                getTypeList().clear();
            } catch (final Exception ignored) {
            }
        }



        public abstract void addToOrUpdateArrayList(T t);
        public abstract void removeFromArrayList(T t);

        public abstract class typeListAdapter extends ArrayAdapter<T> {
            public typeListAdapter(final Context context, final List<T> objects) {
                super(context, 0, objects);
            }
        }
    }
}

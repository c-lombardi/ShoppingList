package com.example.christopher.shopping_list;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * A placeholder fragment containing a simple view.
 */
public class Shopping_ListFragment extends Fragment {

    public Shopping_ListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping__list, container, false);
        ListView list = (ListView) view.findViewById(R.id.listView);
        return inflater.inflate(R.layout.fragment_shopping__list, container, false);
    }
}

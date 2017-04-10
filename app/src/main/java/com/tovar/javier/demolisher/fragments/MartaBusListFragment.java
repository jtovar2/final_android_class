package com.tovar.javier.demolisher.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.TextView;

import com.tovar.javier.demolisher.BaseMartaFragment;
import com.tovar.javier.demolisher.R;
import com.tovar.javier.demolisher.adapters.MartaBusListAdapter;
import com.tovar.javier.demolisher.model.MartaBus;

import java.util.List;

/**
 * Created by javier on 4/9/17.
 */

public class MartaBusListFragment extends Fragment implements BaseMartaFragment {


    ListView busList;
    TextView emptyView;
    MartaBusListAdapter adapter;
    public MartaBusListFragment() {
        // Required empty public constructor
    }



    public static MartaBusListFragment newInstance() {
        MartaBusListFragment fragment = new MartaBusListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_marta_bus_list, container, false);
        // Inflate the layout for this fragment

        emptyView = (TextView) rootView.findViewById(R.id.marta_bus_list_fragment_empty_view);
        emptyView.setText("No marta buses currently running");
        busList = (ListView) rootView.findViewById(R.id.marta_bus_list_fragment_list);

        busList.setEmptyView(emptyView);
        if(adapter == null)
        {
            adapter = new MartaBusListAdapter(this.getContext());
        }
        busList.setAdapter(adapter);
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);




    }

    @Override
    public void onDetach() {
        super.onDetach();

    }
    
    @Override
    public void updateMartaInfo(List<MartaBus> buses) {
        if(adapter == null)
        {
            return;

        }
        adapter.setBuses(buses);
        if(busList != null)
        {
            adapter.notifyDataSetChanged();
        }
    }
}

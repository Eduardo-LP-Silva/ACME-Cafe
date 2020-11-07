package com.ejn.cmov.acmecafe.mobile.ui.items;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ejn.cmov.acmecafe.mobile.R;
import com.ejn.cmov.acmecafe.mobile.ui.ViewModelFactory;

public class ItemsFragment extends Fragment {

    private ItemsViewModel itemsViewModel;

    //TODO
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        itemsViewModel = new ViewModelProvider(this, new ViewModelFactory()).get(ItemsViewModel.class);
        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.item_list);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);


        return inflater.inflate(R.layout.fragment_items, container, false);
    }
}
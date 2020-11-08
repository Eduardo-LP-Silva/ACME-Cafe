package com.ejn.cmov.acmecafe.mobile.ui.items;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ejn.cmov.acmecafe.mobile.R;
import com.ejn.cmov.acmecafe.mobile.data.Callback;
import com.ejn.cmov.acmecafe.mobile.data.Result;
import com.ejn.cmov.acmecafe.mobile.data.model.ItemModel;
import com.ejn.cmov.acmecafe.mobile.ui.ViewModelFactory;

import java.util.Objects;

public class ItemsFragment extends Fragment {

    private ItemsViewModel itemsViewModel = null;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (itemsViewModel == null)
            itemsViewModel = new ViewModelProvider(this, new ViewModelFactory()).get(ItemsViewModel.class);

        itemsViewModel.getItems().observe(Objects.requireNonNull(getActivity()), new Observer<ItemModel[]>() {
            @Override
            public void onChanged(ItemModel[] itemModels) {
                ItemAdapter itemAdapter = new ItemAdapter(itemModels);
                RecyclerView recyclerView = Objects.requireNonNull(getActivity()).findViewById(R.id.item_list);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(itemAdapter);
            }
        });
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if (itemsViewModel == null)
            itemsViewModel = new ViewModelProvider(this, new ViewModelFactory()).get(ItemsViewModel.class);

        itemsViewModel.populateItems(getContext());
        /*
        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.item_list);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        */

        return inflater.inflate(R.layout.fragment_items, container, false);
    }
}
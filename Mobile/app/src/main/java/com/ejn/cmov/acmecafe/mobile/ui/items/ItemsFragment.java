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
import com.ejn.cmov.acmecafe.mobile.data.model.ItemModel;
import com.ejn.cmov.acmecafe.mobile.ui.ViewModelFactory;

public class ItemsFragment extends Fragment {

    private ItemsViewModel itemsViewModel = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        itemsViewModel = new ViewModelProvider(this, new ViewModelFactory()).get(ItemsViewModel.class);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        itemsViewModel.getItems().observe(requireActivity(), new Observer<ItemModel[]>() {
            @Override
            public void onChanged(ItemModel[] itemModels) {
                ItemAdapter itemAdapter = new ItemAdapter(itemModels);
                RecyclerView recyclerView = requireActivity().findViewById(R.id.item_list);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(itemAdapter);
            }
        });
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        itemsViewModel.populateItems(getContext());
        return inflater.inflate(R.layout.fragment_items, container, false);
    }
}
package com.ejn.cmov.acmecafe.mobile.ui.items;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ejn.cmov.acmecafe.mobile.R;
import com.ejn.cmov.acmecafe.mobile.data.model.ItemModel;
import com.ejn.cmov.acmecafe.mobile.ui.OnRecyclerItemClickListener;
import com.ejn.cmov.acmecafe.mobile.ui.ViewModelFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ItemsFragment extends Fragment implements OnRecyclerItemClickListener {

    private ItemsViewModel itemsViewModel = null;
    private FloatingActionButton newOrderBtn;

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
                if (!newOrderBtn.isActivated())
                    newOrderBtn.setActivated(true);

                ItemAdapter itemAdapter = new ItemAdapter(itemModels, ItemsFragment.this);

                if (isAdded()) {
                    RecyclerView recyclerView = requireActivity().findViewById(R.id.item_list);

                    if (recyclerView != null) {
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerView.setAdapter(itemAdapter);
                    }
                }
            }
        });
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_items, container, false);
        itemsViewModel.populateItems(getContext());

        newOrderBtn = view.findViewById(R.id.new_order);
        newOrderBtn.setActivated(false);

        newOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<ItemModel> selectedItems = new ArrayList<>();

                for (ItemModel item : itemsViewModel.getItems().getValue()) {
                    if (item.isSelected())
                        selectedItems.add(item);
                }

                if (selectedItems.size() == 0) {
                    Toast.makeText(getContext(), getString(R.string.make_order_error), Toast.LENGTH_SHORT).show();
                    return;
                }

                Bundle orderArgs = new Bundle();
                orderArgs.putSerializable("items", selectedItems);

                Navigation.findNavController(view).navigate(R.id.action_nav_items_to_compose_order, orderArgs);
            }
        });

        return view;
    }

    @Override
    public void onItemClick(View view, int position) {
        if (itemsViewModel.getItems().getValue() != null) {
            ItemModel clickedItem = itemsViewModel.getItems().getValue()[position];
            final LinearLayout card = view.findViewById(R.id.item_layout);
            final TextView itemName = view.findViewById(R.id.item_name);
            final TextView itemPrice = view.findViewById(R.id.item_price);

            if (clickedItem.isSelected()) {
                card.setBackgroundResource(R.drawable.bottom_border);
                itemName.setTextColor(ContextCompat.getColor(getContext(), R.color.textBrown));
                itemPrice.setTextColor(ContextCompat.getColor(getContext(), R.color.textBrown));
                clickedItem.setSelected(false);
            }
            else {
                card.setBackgroundResource(R.color.colorAccent);
                itemName.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                itemPrice.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                clickedItem.setSelected(true);
            }
        }
    }
}
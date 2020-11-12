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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ejn.cmov.acmecafe.mobile.R;
import com.ejn.cmov.acmecafe.mobile.data.model.ItemModel;
import com.ejn.cmov.acmecafe.mobile.ui.OnRecyclerItemClickListener;
import com.ejn.cmov.acmecafe.mobile.ui.ViewModelFactory;
import com.ejn.cmov.acmecafe.mobile.ui.order.OrderFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

//TODO On resume fetch items again (same in other fragments?)
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

        newOrderBtn = requireActivity().findViewById(R.id.new_order);
        newOrderBtn.setActivated(false);

        itemsViewModel.getItems().observe(requireActivity(), new Observer<ItemModel[]>() {
            @Override
            public void onChanged(ItemModel[] itemModels) {
                if (!newOrderBtn.isActivated())
                    newOrderBtn.setActivated(true);

                ItemAdapter itemAdapter = new ItemAdapter(itemModels, ItemsFragment.this);
                RecyclerView recyclerView = requireActivity().findViewById(R.id.item_list);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(itemAdapter);
            }
        });

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

                OrderFragment orderFragment = new OrderFragment();
                Bundle orderArgs = new Bundle();

                orderArgs.putSerializable("items", selectedItems);
                orderFragment.setArguments(orderArgs);

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack(null)
                        .add(R.id.nav_host_fragment, orderFragment)
                        .commit();
            }
        });
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        itemsViewModel.populateItems(getContext());
        return inflater.inflate(R.layout.fragment_items, container, false);
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
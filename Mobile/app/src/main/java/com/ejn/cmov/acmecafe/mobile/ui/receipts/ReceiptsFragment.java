package com.ejn.cmov.acmecafe.mobile.ui.receipts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ejn.cmov.acmecafe.mobile.R;
import com.ejn.cmov.acmecafe.mobile.data.model.ReceiptModel;
import com.ejn.cmov.acmecafe.mobile.ui.MainMenuActivity;
import com.ejn.cmov.acmecafe.mobile.ui.ViewModelFactory;
import com.ejn.cmov.acmecafe.mobile.ui.items.ItemAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ReceiptsFragment extends Fragment {

    private ReceiptsViewModel receiptsViewModel;
    private FloatingActionButton getReceiptsBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiptsViewModel = new ViewModelProvider(this, new ViewModelFactory()).get(ReceiptsViewModel.class);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        receiptsViewModel.getReceipts().observe(requireActivity(), new Observer<ReceiptModel[]>() {
            @Override
            public void onChanged(ReceiptModel[] receipts) {
                // Prevent race condition
                if (!getReceiptsBtn.isActivated())
                    getReceiptsBtn.setActivated(true);

                ReceiptsAdapter receiptsAdapter = new ReceiptsAdapter(receipts);
                RecyclerView recyclerView = requireActivity().findViewById(R.id.receipts_list);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(receiptsAdapter);
            }
        });

        getReceiptsBtn = requireActivity().findViewById(R.id.get_receipts);
        getReceiptsBtn.setActivated(false);
        getReceiptsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                receiptsViewModel.loadRemoteReceipts(getContext(), ((MainMenuActivity) requireActivity()).getUserID());
                Toast.makeText(getContext(), "Updating Receipts...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        receiptsViewModel.loadLocalReceipts(getContext());
        return inflater.inflate(R.layout.fragment_receipts, container, false);
    }
}
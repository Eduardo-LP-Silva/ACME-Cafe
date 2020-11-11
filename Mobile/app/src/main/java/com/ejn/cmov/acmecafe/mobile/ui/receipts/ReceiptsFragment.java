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
import com.ejn.cmov.acmecafe.mobile.ui.OnRecyclerItemClickListener;
import com.ejn.cmov.acmecafe.mobile.ui.ViewModelFactory;
import com.ejn.cmov.acmecafe.mobile.ui.receipts.Receipt.ReceiptFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ReceiptsFragment extends Fragment implements OnRecyclerItemClickListener {

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

        getReceiptsBtn = requireActivity().findViewById(R.id.get_receipts);
        getReceiptsBtn.setActivated(false);

        receiptsViewModel.getReceipts().observe(requireActivity(), new Observer<ReceiptModel[]>() {
            @Override
            public void onChanged(ReceiptModel[] receipts) {
                // Prevent race condition
                if (!getReceiptsBtn.isActivated())
                    getReceiptsBtn.setActivated(true);

                ReceiptsAdapter receiptsAdapter = new ReceiptsAdapter(receipts, ReceiptsFragment.this);
                RecyclerView recyclerView = requireActivity().findViewById(R.id.receipts_list);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(receiptsAdapter);
            }
        });

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

    @Override
    public void onItemClick(View view, int position) {
        if(receiptsViewModel.getReceipts().getValue() != null) {
            ReceiptModel receipt = receiptsViewModel.getReceipts().getValue()[position];
            ReceiptFragment receiptFragment = new ReceiptFragment();
            Bundle receiptArgs = new Bundle();

            receiptArgs.putSerializable("receipt", receipt);
            receiptFragment.setArguments(receiptArgs);

            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, receiptFragment).commit();
        }

    }
}
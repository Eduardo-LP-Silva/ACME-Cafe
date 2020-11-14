package com.ejn.cmov.acmecafe.mobile.ui.order;

import androidx.appcompat.app.AppCompatActivity;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.widget.Toast;

import com.ejn.cmov.acmecafe.mobile.R;

import java.nio.charset.StandardCharsets;

public class SendOrderActivity extends AppCompatActivity implements NfcAdapter.OnNdefPushCompleteCallback {
    private static final String PAYLOAD_ARG = "payload";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_order);

        Bundle extras = getIntent().getExtras();
        String payload = extras.getString(PAYLOAD_ARG);

        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter == null) {
            Toast.makeText(this, getString(R.string.nfc_error), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        NdefMessage msg = new NdefMessage(new NdefRecord[] {createMimeRecord("application/nfc.ejn.acmecafe.order", payload)});

        nfcAdapter.setNdefPushMessage(msg, this);
        nfcAdapter.setOnNdefPushCompleteCallback(this, this);
    }

    private NdefRecord createMimeRecord(String mimeType, String payload) {
        byte[] mimeBytes = mimeType.getBytes(StandardCharsets.ISO_8859_1);
        byte[] payloadBytes = payload.getBytes(StandardCharsets.ISO_8859_1);
        return new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], payloadBytes);
    }

    @Override
    public void onNdefPushComplete(NfcEvent nfcEvent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Order sent", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    public static String getPayloadArg() {
        return PAYLOAD_ARG;
    }
}
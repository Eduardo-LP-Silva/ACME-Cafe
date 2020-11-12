package com.ejn.cmov.acmecafe.mobile.data;

import android.content.Context;
import android.security.KeyPairGeneratorSpec;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.security.auth.x500.X500Principal;

import androidx.annotation.Nullable;

public class Authentication {
    private static final int KEY_SIZE = 512;
    private static final int NUM_KEY_BYTES = KEY_SIZE / Byte.SIZE;
    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final String KEY_ALGO = "RSA";                    // cryptography family
    private static final String SIGN_ALGO = "SHA256WithRSA";         // signature algorithm
    private static final int CERT_SERIAL = 12121212;                 // certificate serial number (any one does the job)
    private static final String ENC_ALGO = "RSA/ECB/PKCS1Padding";   // encrypt/decrypt algorithm
    private static final String keyname = "ejn.acme.cafe";           // common name in the KeyStore and public key certificate

    public static String getCertificate(Context appContext) {
        genKeys(appContext);

        return genCertificate();
    }

    @Nullable
    public static String genCertificate() {
        X509Certificate cert;
        String b64Cert = null;

        try {
            KeyStore ks = KeyStore.getInstance(ANDROID_KEYSTORE);
            ks.load(null);
            KeyStore.Entry entry = ks.getEntry(keyname, null);

            if (entry != null) {
                cert = (X509Certificate)((KeyStore.PrivateKeyEntry)entry).getCertificate();
                b64Cert = Base64.encodeToString(cert.getEncoded(), Base64.NO_WRAP);    // transform into Base64 string (PEM format without the header and footer)
            }
        }
        catch (Exception e) {
            Log.e("AUTH \\ GEN CERT", e.toString());
        }

        return b64Cert;
    }

    public static void genKeys(Context appContext) {
        try {
            Calendar start = new GregorianCalendar();
            Calendar end = new GregorianCalendar();
            end.add(Calendar.YEAR, 20);            // 20 years validity
            KeyPairGenerator kgen = KeyPairGenerator.getInstance(KEY_ALGO, ANDROID_KEYSTORE);
            AlgorithmParameterSpec spec = new KeyPairGeneratorSpec.Builder(appContext)
                    .setKeySize(KEY_SIZE)
                    .setAlias(keyname)                                       // the name of the key (common name) to retrieve it
                    .setSubject(new X500Principal("CN=" + keyname))
                    .setSerialNumber(BigInteger.valueOf(CERT_SERIAL))       // a serial number to the public key certificate
                    .setStartDate(start.getTime())
                    .setEndDate(end.getTime())
                    .build();
            kgen.initialize(spec);
            KeyPair kp = kgen.generateKeyPair();
        } catch (Exception e) {
            Log.e("AUTH \\ GEN KEYS", e.toString());
        }
    }

    public static String buildQuerySignedMessage(String customerId) {
        String query = "?";
        JSONObject data = new JSONObject();
        String timestamp = getTimeStamp();

        try {
            data.put("customerId", customerId);
            data.put("timestamp", timestamp);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
            keyStore.load(null);
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry(keyname, null);;
            Signature s = Signature.getInstance(SIGN_ALGO);
            s.initSign(privateKeyEntry.getPrivateKey());
            s.update(data.toString().getBytes());
            byte[] signature = s.sign();

            query += "customerId=" + customerId;
            query += "&timestamp=" + timestamp;
            query += "&signature=" + byteArrayToHex(signature);
        } catch (Exception e) {
            Log.e("AUTH \\ BUILD GET", e.toString());
        }

        return query;
    }

    public static JSONObject buildBodySignedMessage(JSONObject data) {
        JSONObject body = new JSONObject();

        try {
            data.put("timestamp", getTimeStamp());
            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
            keyStore.load(null);
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry(keyname, null);;
            Signature s = Signature.getInstance(SIGN_ALGO);
            s.initSign(privateKeyEntry.getPrivateKey());
            s.update(data.toString().getBytes());
            byte[] signature = s.sign();

            body.put("data", data);
            body.put("signature", byteArrayToHex(signature));
        } catch (Exception e) {
            Log.e("AUTH \\ BUILD POST", e.toString());
        }

        return body;
    }

    // transform a byte[] into a string with hexadecimal digits
    private static String byteArrayToHex(byte[] ba) {
        StringBuilder sb = new StringBuilder(ba.length * 2);
        for(byte b: ba)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }

    private static String getTimeStamp() {
        Long tsLong = System.currentTimeMillis() / 1000;

        return tsLong.toString();
    }
}

package com.ejn.cmov.acmecafe.mobile.data.remote;

import android.util.Log;

import com.ejn.cmov.acmecafe.mobile.data.Authentication;
import com.ejn.cmov.acmecafe.mobile.data.Result;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.annotation.Nullable;

/**
 * Class that handles requests to the REST API
 */
public class RemoteDataSource {
    public Result<String> getVouchers(String userID) {
        HttpURLConnection httpConnection = null;

        try {
            httpConnection = createRequest(String.format("voucher%s", Authentication.buildQuerySignedMessage(userID)), "GET",
                    null);
            int responseCode = httpConnection.getResponseCode();

            if (responseCode == 200) {
                String response = readStream(httpConnection.getInputStream());
                return new Result.Success<>(response);
            }
            else {
                String errorCode = Integer.toString(responseCode);
                return new Result.Error<>(errorCode);
            }
        }
        catch (Exception e) {
            return new Result.Error<>(e.getMessage());
        }
        finally {
            if (httpConnection != null)
                httpConnection.disconnect();
        }
    }

    public Result<String> sendOrder(JSONObject payload) {
        HttpURLConnection httpConnection = null;

        try {
            httpConnection = createRequest("order", "POST", payload);
            int responseCode = httpConnection.getResponseCode();

            Log.i("RDS \\ SEND ORDER", Integer.toString(responseCode));

            if (responseCode == 201) {
                String response = readStream(httpConnection.getInputStream());
                return new Result.Success<>(response);
            }
            else {
                String errorCode = Integer.toString(responseCode);
                return new Result.Error<>(errorCode);
            }
        }
        catch (Exception e) {
            return new Result.Error<>(e.getMessage());
        }
        finally {
            if (httpConnection != null)
                httpConnection.disconnect();
        }
    }

    public Result<String> getReceipts(String userID) {
        HttpURLConnection httpConnection = null;

        try {
            httpConnection = createRequest(String.format("order/receipt%s", Authentication.buildQuerySignedMessage(userID)), "GET", null);
            int responseCode = httpConnection.getResponseCode();

            if (responseCode == 200) {
                String response = readStream(httpConnection.getInputStream());
                return new Result.Success<>(response);
            }
            else {
                String errorCode = Integer.toString(responseCode);
                return new Result.Error<>(errorCode);
            }
        }
        catch (Exception e) {
            return new Result.Error<>(e.getMessage());
        }
        finally {
            if (httpConnection != null)
                httpConnection.disconnect();
        }
    }

    public Result<String> getItems() {
        HttpURLConnection httpConnection = null;

        try {
            httpConnection = createRequest("item", "GET", null);
            int responseCode = httpConnection.getResponseCode();

            if (responseCode == 200) {
                String response = readStream(httpConnection.getInputStream());
                return new Result.Success<>(response);
            }
            else {
                String errorCode = Integer.toString(responseCode);
                return new Result.Error<>(errorCode);
            }
        }
        catch (Exception e) {
            return new Result.Error<>(e.getMessage());
        }
        finally {
            if (httpConnection != null)
                httpConnection.disconnect();
        }
    }

    public Result<String> register(JSONObject jsonBody) {
        HttpURLConnection httpConnection = null;

        try {
            httpConnection = createRequest("customer", "POST", jsonBody);
            int responseCode = httpConnection.getResponseCode();

            if (responseCode == 201) {
                String response = readStream(httpConnection.getInputStream());
                return new Result.Success<>(response);
            }
            else {
                String errorCode = Integer.toString(responseCode);
                Log.e("RDS \\ REGISTER", errorCode);
                return new Result.Error<>(errorCode);
            }
        }
        catch (Exception e) {
            Log.e("RDS \\ REGISTER", e.toString());
            return new Result.Error<>(e.getMessage());
        }
        finally {
            if (httpConnection != null)
                httpConnection.disconnect();
        }
    }

    private HttpURLConnection createRequest(String endpoint, String requestMethod, @Nullable JSONObject jsonBody) throws Exception {
        final String apiURL = "http://192.168.1.70:8080/";
        URL url = new URL(apiURL + endpoint);
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();

        httpConnection.setRequestMethod(requestMethod);
        httpConnection.setRequestProperty("Accept", "application/json");
        httpConnection.setConnectTimeout(2500);

        if (jsonBody != null) {
            httpConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            httpConnection.setDoOutput(true);

            DataOutputStream dos = new DataOutputStream(httpConnection.getOutputStream());
            dos.writeBytes(jsonBody.toString());
            dos.flush();
            dos.close();
        }

        return httpConnection;
    }

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        String line;
        StringBuilder response = new StringBuilder();

        try {
            reader = new BufferedReader(new InputStreamReader(in));
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        catch (IOException e) {
            return e.getMessage();
        }
        finally {
            try {
                if (reader != null)
                    reader.close();

                in.close();
            }
            catch (IOException e) {
                response = new StringBuilder(e.toString());
            }
        }

        return response.toString();
    }
}

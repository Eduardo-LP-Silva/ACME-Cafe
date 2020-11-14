package com.ejn.cmov.acmecafe.terminal;

import android.util.Log;

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

    private static volatile RemoteDataSource instance;
    public static String host = "192.168.1.88";
    public static int port = 8080;

    // private constructor : singleton access
    private RemoteDataSource() {
    }

    private RemoteDataSource(String host) {
        this.host = host;
    }

    public static RemoteDataSource getInstance() {
        if (instance == null)
            instance = new RemoteDataSource();

        return instance;
    }

    public static RemoteDataSource getInstance(String host) {
        instance = getInstance();
        instance.host = host;

        return instance;
    }

    public static RemoteDataSource getInstance(String host, int port) {
        instance = getInstance();
        instance.host = host;
        instance.port = port;

        return instance;
    }

    public static Result<String> getReceipts(String userID) {
        HttpURLConnection httpConnection = null;

        try {
            httpConnection = createRequest(String.format("order/receipt?customerId=%s", userID), "GET", null);
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

    public static Result<String> getItems() {
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
            e.printStackTrace();
            return new Result.Error<>(e.getMessage());
        }
        finally {
            if (httpConnection != null)
                httpConnection.disconnect();
        }
    }

    public static Result<String> getCostumers() {
        HttpURLConnection httpConnection = null;

        try {
            httpConnection = createRequest("customer", "GET", null);
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
            e.printStackTrace();
            return new Result.Error<>(e.getMessage());
        }
        finally {
            if (httpConnection != null)
                httpConnection.disconnect();
        }
    }

    public static Result<String> register(JSONObject jsonBody) {
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

    public static Result<String> createOrder(JSONObject jsonBody) {
        HttpURLConnection httpConnection = null;

        try {
            httpConnection = createRequest("order", "POST", jsonBody);
            int responseCode = httpConnection.getResponseCode();

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


    private static HttpURLConnection createRequest(String endpoint, String requestMethod, @Nullable JSONObject jsonBody) throws Exception {
        final String apiURL = "http://" + host + ":8080/";
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

    public static Result<LoggedInUser> login(String username, String password) {
        try {
            // TODO: handle loggedInUser authentication
            LoggedInUser fakeUser =
                    new LoggedInUser(
                            java.util.UUID.randomUUID().toString(),
                            "Jane Doe");
            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            return new Result.Error<>(null);
        }
    }

    private static String readStream(InputStream in) {
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
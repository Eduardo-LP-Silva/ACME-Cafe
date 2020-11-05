package com.ejn.cmov.acmecafe.mobile.data;

import android.util.Log;

import com.ejn.cmov.acmecafe.mobile.data.model.LoggedInUser;
import com.ejn.cmov.acmecafe.mobile.ui.register.RegisterResult;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Class that handles requests to the REST API
 */
public class RemoteDataSource {
    private final String apiURL = "http://192.168.1.91:8080/";

    public Result<LoggedInUser> login(String username, String password) {
        try {
            // TODO: handle loggedInUser authentication
            LoggedInUser fakeUser =
                    new LoggedInUser(
                            java.util.UUID.randomUUID().toString(),
                            "Jane Doe");
            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            return new Result.Error<LoggedInUser>(null);
        }
    }

    public Result<String> register(JSONObject jsonBody) {
        String endpoint = this.apiURL + "customer";
        URL url;
        HttpURLConnection httpConnection = null;

        try {
            url = new URL(endpoint);
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            httpConnection.setRequestProperty("Accept", "application/json");
            httpConnection.setDoOutput(true);

            DataOutputStream dos = new DataOutputStream(httpConnection.getOutputStream());
            dos.writeBytes(jsonBody.toString());
            dos.flush();
            dos.close();

            int responseCode = httpConnection.getResponseCode();

            if (responseCode == 201) {
                String response = readStream(httpConnection.getInputStream());
                Log.i("REGISTER RESPONSE", response);
                return new Result.Success<String>(response);
            }
            else {
                String errorCode = Integer.toString(responseCode);
                Log.e("REGISTER ERROR", errorCode);
                return new Result.Error<String>(errorCode);
            }
        }
        catch (Exception e) {
            return new Result.Error<String>(e.getMessage());
        }
        finally {
            if (httpConnection != null)
                httpConnection.disconnect();
        }
    }

    public void logout() {
        // TODO: revoke authentication
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
                response = new StringBuilder(e.getMessage());
            }
        }

        return response.toString();
    }
}
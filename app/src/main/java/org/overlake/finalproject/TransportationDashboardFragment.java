package org.overlake.finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.overlake.finalproject.databinding.FragmentTransportationDashboardBinding;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TransportationDashboardFragment extends Fragment {
    private static final String API_COLLECT_REQUEST_URL = "https://api.collectapi.com/gasPrice/stateUsaPrice?state=WA";
    private static final String API_COLLECT_REQUEST_HEADER1_NAME = "content-type";
    private static final String API_COLLECT_REQUEST_HEADER1_VALUE = "application/json";
    private static final String API_COLLECT_REQUEST_HEADER2_NAME = "authorization";
    private static final String API_COLLECT_REQUEST_HEADER2_VALUE = "apikey 1HQ11nCJZ4TwyU2ko5LDUv:4osefQcZ6ktkhIzweuLpwy";
    private static final String API_RESPONSE_ATTRIBUTE_RESULT = "result";
    private static final String API_RESPONSE_ATTRIBUTE_CITIES = "cities";
    private static final int API_RESPONSE_INDEX_SEATTLE = 4;
    private static final String API_RESPONSE_ATTRIBUTE_GASOLINE = "gasoline";
    private static final String API_RESPONSE_ATTRIBUTE_MIDGRADE = "midGrade";
    private static final String API_RESPONSE_ATTRIBUTE_PREMIUM = "premium";
    private static final String API_RESPONSE_ATTRIBUTE_DIESEL = "diesel";

    private FragmentTransportationDashboardBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentTransportationDashboardBinding.inflate(inflater);

        Thread networkThread = new Thread(() -> {
            try {
                callCollectAPI();
            } catch (JSONException | IOException e) {
                throw new RuntimeException(e);
            }
        });
        networkThread.start();
        try {
            networkThread.join(); // continue main thread only after network requests complete
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return binding.getRoot();
    }

    private void callCollectAPI() throws JSONException, IOException {

        // https://collectapi.com/api/gasPrice/gas-prices-api

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(API_COLLECT_REQUEST_URL)
                .get()
                .addHeader(API_COLLECT_REQUEST_HEADER1_NAME, API_COLLECT_REQUEST_HEADER1_VALUE)
                .addHeader(API_COLLECT_REQUEST_HEADER2_NAME, API_COLLECT_REQUEST_HEADER2_VALUE)
                .build();

        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        JSONObject responseJson = new JSONObject(responseBody);
        JSONObject resultJson = responseJson.getJSONObject(API_RESPONSE_ATTRIBUTE_RESULT);
        JSONArray citiesArray = resultJson.getJSONArray(API_RESPONSE_ATTRIBUTE_CITIES);
        JSONObject seattleJson = (JSONObject) citiesArray.get(API_RESPONSE_INDEX_SEATTLE);

        String regular = seattleJson.getString(API_RESPONSE_ATTRIBUTE_GASOLINE);
        String midGrade = seattleJson.getString(API_RESPONSE_ATTRIBUTE_MIDGRADE);
        String premium = seattleJson.getString(API_RESPONSE_ATTRIBUTE_PREMIUM);
        String diesel = seattleJson.getString(API_RESPONSE_ATTRIBUTE_DIESEL);

        binding.gasRegular.setText(getContext().getString(R.string.transportation_dashboard_gas_regular, regular));
        binding.gasMidGrade.setText(getContext().getString(R.string.transportation_dashboard_gas_midGrade, midGrade));
        binding.gasPremium.setText(getContext().getString(R.string.transportation_dashboard_gas_premium, premium));
        binding.diesel.setText(getContext().getString(R.string.transportation_dashboard_gas_diesel, diesel));

    }

}

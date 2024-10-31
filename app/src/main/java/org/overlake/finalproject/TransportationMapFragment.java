package org.overlake.finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.overlake.finalproject.databinding.FragmentTransportationDashboardBinding;
import org.overlake.finalproject.databinding.FragmentTransportationMapBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TransportationMapFragment extends Fragment implements OnMapReadyCallback {
    private static final String API_OBA_REQUEST_URL = "REDACTED";
    private static final String API_RESPONSE_ATTRIBUTE_DATA = "data";
    private static final String API_RESPONSE_ATTRIBUTE_LIST = "list";
    private static final String API_RESPONSE_ATTRIBUTE_LAT = "lat";
    private static final String API_RESPONSE_ATTRIBUTE_LON = "lon";
    private static final String API_RESPONSE_ATTRIBUTE_NAME = "name";
    private static final double DEFAULT_CENTER_LATITUDE = 47.653435;
    private static final double DEFAULT_CENTER_LONGITUDE = -122.305641;
    private static final int DEFAULT_PADDING = 25;
    private FragmentTransportationMapBinding binding;
    private List<TransportationLocation> locations;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTransportationMapBinding.inflate(inflater);
        binding.transportationBtnSearch.setOnClickListener(v -> {
            showMap();
        });
        return binding.getRoot();
    }

    private void showMap() {
        Thread networkThread = new Thread(() -> {
            try {
                callOneBusAwayAPI();
            } catch (JSONException | IOException e) {
                throw new RuntimeException(e);
            }
        });
        networkThread.start();
        try {
            networkThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.transportation_map);
        supportMapFragment.getMapAsync(TransportationMapFragment.this);
    }

    private void callOneBusAwayAPI() throws IOException, JSONException {

        // https://developer.onebusaway.org/api/where

        String inputLatitude = binding.latitude.getText().toString();
        String inputLongitude = binding.longitude.getText().toString();
        if (inputLatitude.isEmpty()) {
            inputLatitude = Double.toString(DEFAULT_CENTER_LATITUDE);
        }
        if (inputLongitude.isEmpty()) {
            inputLongitude = Double.toString(DEFAULT_CENTER_LONGITUDE);
        }

        String url = String.format(API_OBA_REQUEST_URL, inputLatitude, inputLongitude);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        JSONObject responseJson = new JSONObject(responseBody);
        JSONObject locationsJson = (JSONObject) responseJson.get(API_RESPONSE_ATTRIBUTE_DATA);
        JSONArray locationsArray = locationsJson.getJSONArray(API_RESPONSE_ATTRIBUTE_LIST);
        locations = new ArrayList<>();
        for (int i = 0; i < locationsArray.length(); i++) {
            JSONObject location = (JSONObject) locationsArray.get(i);
            double latitude = (double) location.get(API_RESPONSE_ATTRIBUTE_LAT);
            double longitude = (double) location.get(API_RESPONSE_ATTRIBUTE_LON);
            String name = (String) location.get(API_RESPONSE_ATTRIBUTE_NAME);
            locations.add(new TransportationLocation(latitude, longitude, name));
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (locations.isEmpty()) {
            Toast.makeText(getContext(), getContext().getString(R.string.error_message), Toast.LENGTH_LONG).show();
            return;
        }
        TransportationLocation temp = locations.get(0);
        LatLng northmost = new LatLng(temp.getLatitude(), temp.getLongitude());
        LatLng eastmost = new LatLng(temp.getLatitude(), temp.getLongitude());
        LatLng southmost = new LatLng(temp.getLatitude(), temp.getLongitude());
        LatLng westmost = new LatLng(temp.getLatitude(), temp.getLongitude());
        for (TransportationLocation location : locations) {
            LatLng coordinates = new LatLng(location.getLatitude(), location.getLongitude());
            String title = location.getName();
            googleMap.addMarker(new MarkerOptions()
                    .position(coordinates)
                    .title(title));
            if (coordinates.latitude > northmost.latitude) {
                northmost = coordinates;
            }
            if (coordinates.latitude < southmost.latitude) {
                southmost = coordinates;
            }
            if (coordinates.longitude > eastmost.longitude) {
                eastmost = coordinates;
            }
            if (coordinates.longitude < westmost.longitude) {
                westmost = coordinates;
            }
        }
        LatLng southwest = new LatLng(southmost.latitude, westmost.longitude);
        LatLng northeast = new LatLng(northmost.latitude, eastmost.longitude);
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, DEFAULT_PADDING));

    }

    public static class TransportationLocation {

        private final double latitude;
        private final double longitude;
        private final String name;
        public TransportationLocation(double latitude, double longitude, String name) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.name = name;
        }

        public double getLatitude() {
            return latitude;
        }
        public double getLongitude() {
            return longitude;
        }
        public String getName() { return name; }

    }

}

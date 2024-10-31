package org.overlake.finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.overlake.finalproject.databinding.FragmentGroceryMapBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GroceryMapFragment extends Fragment implements OnMapReadyCallback {
    private static final int DEFAULT_PADDING = 25;
    private GroceryViewModel viewModel;
    private FragmentGroceryMapBinding binding;
    private String selectedGroceryList;
    private String zipCode;
    private int radius;
    private GroceryPriceAnalyzer priceAnalyzer;
    private List<GroceryLocation> locations;
    private List<GroceryEntry> items = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        viewModel = new ViewModelProvider(this).get(GroceryViewModel.class);
        LiveData<List<String>> liveAllLists = viewModel.getAllLists();
        liveAllLists.observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                buildSpinner(strings);
            }
        });
        binding = FragmentGroceryMapBinding.inflate(inflater);

        binding.showMap.setOnClickListener(v -> {
            zipCode = binding.zipCode.getText().toString();
            radius = Integer.parseInt(binding.radius.getText().toString());
            try {
                buildMap();
            } catch (JSONException | IOException e) {
                throw new RuntimeException(e);
            }
        });

        return binding.getRoot();

    }

    private void buildSpinner(List<String> strings) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.grocery_map_spinner_item, strings);
        binding.listsSpinner.setAdapter(adapter);
        binding.listsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGroceryList = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void buildMap() throws JSONException, IOException {
        LiveData<List<GroceryEntry>> liveItems = viewModel.getList(selectedGroceryList);
        liveItems.observe(getViewLifecycleOwner(), new Observer<List<GroceryEntry>>() {
            @Override
            public void onChanged(List<GroceryEntry> words) {
                GroceryMapFragment.this.items = words;
                List<String> itemNames = new ArrayList<>();
                for (GroceryEntry item : items) {
                    itemNames.add(item.getEntryName());
                }
                // network requests (in priceAnalyzer, which calls KrogerAPI) can't occur on main thread
                Thread networkThread = new Thread(() -> {
                    try {
                        priceAnalyzer = new GroceryPriceAnalyzer(getContext() ,itemNames, zipCode, radius);
                        locations = priceAnalyzer.getResults();
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
                SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                supportMapFragment.getMapAsync(GroceryMapFragment.this); // only allowed on main thread
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GroceryLocation temp = locations.get(0);
        LatLng northmost = new LatLng(temp.getLatitude(), temp.getLongitude());
        LatLng eastmost = new LatLng(temp.getLatitude(), temp.getLongitude());
        LatLng southmost = new LatLng(temp.getLatitude(), temp.getLongitude());
        LatLng westmost = new LatLng(temp.getLatitude(), temp.getLongitude());
        for (GroceryLocation location : locations) {
            LatLng coordinates = new LatLng(location.getLatitude(), location.getLongitude());
            String title = Integer.toString(location.getRankOfCheapest());
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

}

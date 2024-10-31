package org.overlake.finalproject;

import android.content.Context;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroceryPriceAnalyzer {
    private final GroceryKrogerAPIHelper krogerAPIHelper;
    private final Map<String, String> locationsToJsonInfo;
    private final Map<String, Double> locationsToPrice;
    private final List<String> sortedLocations;
    public GroceryPriceAnalyzer(Context context, List<String> items, String zipCode, int radius) throws JSONException, IOException {
        krogerAPIHelper = new GroceryKrogerAPIHelper(context);
        locationsToJsonInfo = krogerAPIHelper.queryForLocations(zipCode, radius);
        locationsToPrice = getPricesAtEachLocation(items);
        sortedLocations = sortLocationsByPrice();
    }

    private Map<String, Double> getPricesAtEachLocation(List<String> items) throws JSONException, IOException {
        Map<String, Double> locationsToPrice = new HashMap<>();
        for (String locationId : locationsToJsonInfo.keySet()) {
            Double totalPrice = 0.00;
            for (String item : items) {
                if (item.equals(GroceryDao.ENTRY_NAME_PLACE_HOLDER)) continue;
                double price = krogerAPIHelper.queryForLowestPriceAtLocation(item, locationId);
                if (price == GroceryKrogerAPIHelper.ERROR_PRODUCT_NONEXISTENT_AT_THIS_LOCATION) {
                    totalPrice = null;
                    break;
                }
                totalPrice += price;
            }
            locationsToPrice.put(locationId, totalPrice);
        }
        return locationsToPrice;
    }

    private List<String> sortLocationsByPrice() {
        List<PriceAtLocation> pricesAtLocations = new ArrayList<>();
        for (String locationId : locationsToJsonInfo.keySet()) {
            Double price = locationsToPrice.get(locationId);
            pricesAtLocations.add(new PriceAtLocation(price, locationId));
        }
        pricesAtLocations.sort(PriceAtLocation::compareTo);

        List<String> sortedLocations = new ArrayList<>();
        for (PriceAtLocation entry : pricesAtLocations) {
            sortedLocations.add(entry.location);
        }
        return sortedLocations;
    }

    public List<GroceryLocation> getResults() throws JSONException {
        List<GroceryLocation> results = new ArrayList<>();
        for (int i = 0; i < sortedLocations.size(); i++) {
            String locationId = sortedLocations.get(i);
            String locationJsonInfo = locationsToJsonInfo.get(locationId);
            Double[] coordinates = krogerAPIHelper.getCoordinatesOfLocation(locationJsonInfo);
            Double price = locationsToPrice.get(locationId);
            GroceryLocation groceryLocation = new GroceryLocation(coordinates[0], coordinates[1], price, i+1);
            results.add(groceryLocation);
        }
        return results;
    }

    public static class PriceAtLocation implements Comparable<PriceAtLocation> {
        public double price;
        public String location;
        public PriceAtLocation(double price, String location) {
            this.price = price;
            this.location = location;
        }
        @Override
        public int compareTo(PriceAtLocation other) {
            if (this.price > other.price) {
                return 1;
            } else if (this.price < other.price) {
                return -1;
            } else {
                return 0;
            }
        }
    }

}

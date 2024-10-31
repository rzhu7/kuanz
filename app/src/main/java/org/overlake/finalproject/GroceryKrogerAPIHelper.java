package org.overlake.finalproject;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GroceryKrogerAPIHelper {
    private static final String CLIENT_ID = "REDACTED";
    private static final String CLIENT_SECRET = "REDACTED";
    private static final String API_ACCESS_TOKEN_KEY = "access_token";
    private static final String API_ACCESS_TOKEN_SCOPE = "product.compact";
    private static final String API_ACCESS_TOKEN_REQUEST_BODY = "client_id=%s&client_secret=%s&scope=%s";
    private static final String API_ACCESS_TOKEN_MEDIA_TYPE = "application/x-www-form-urlencoded";
    private static final String API_ACCESS_TOKEN_REQUEST_URL = "https://api.kroger.com/v1/connect/oauth2/token?grant_type=client_credentials";
    private static final String API_ACCESS_TOKEN_REQUEST_METHOD = "POST";
    private static final String API_ACCESS_TOKEN_REQUEST_HEADER_NAME = "Content-Type";
    private static final String API_ACCESS_TOKEN_REQUEST_HEADER_VALUE = "application/x-www-form-urlencoded";
    private static final String API_LOCATIONS_REQUEST_URL = "https://api.kroger.com/v1/locations/?filter.zipCode.near=%s&filter.radiusInMiles=%s&filter.limit=%s";
    private static final String API_PRODUCTS_REQUEST_URL = "https://api.kroger.com/v1/products/?filter.term=%s&filter.locationId=%s&filter.limit=%s";
    private static final String API_LOCATIONS_PRODUCTS_REQUEST_HEADER1_NAME = "Accept";
    private static final String API_LOCATIONS_PRODUCTS_REQUEST_HEADER1_VALUE = "application/json";
    private static final String API_LOCATIONS_PRODUCTS_REQUEST_HEADER2_NAME = "Authorization";
    private static final String API_LOCATIONS_PRODUCTS_REQUEST_HEADER2_VALUE = "Bearer %s";
    private static final String API_RESPONSE_KEY_DATA = "data";
    private static final String API_RESPONSE_KEY_LOCATIONID = "locationId";
    private static final String API_RESPONSE_KEY_ITEMS = "items";
    private static final String API_RESPONSE_KEY_PRICE = "price";
    private static final String API_RESPONSE_KEY_REGULAR = "regular";
    private static final String API_RESPONSE_KEY_GEOLOCATION = "geolocation";
    private static final String API_RESPONSE_KEY_LATITUDE = "latitude";
    private static final String API_RESPONSE_KEY_LONGITUDE = "longitude";

    private static final String ERROR_NO_LOCATIONS_FOUND = "no locations found";
    private static final String ERROR_PRODUCT_NONEXISTENT_AT_ANY_LOCATION = "not found at any location with parameters";
    public static final double ERROR_PRODUCT_NONEXISTENT_AT_THIS_LOCATION = -1.00;
    private static final int DEFAULT_LIMIT = 5;
    private final Context context;
    private String accessToken;
    public GroceryKrogerAPIHelper(Context context) {
        this.context = context;
    }

    public Double[] getCoordinatesOfLocation(String locationJsonInfo) throws JSONException {
        JSONObject location = new JSONObject(locationJsonInfo);
        JSONObject locationGeoInfo = (JSONObject) location.get(API_RESPONSE_KEY_GEOLOCATION);
        double latitude = (double) locationGeoInfo.get(API_RESPONSE_KEY_LATITUDE);
        double longitude = (double) locationGeoInfo.get(API_RESPONSE_KEY_LONGITUDE);
        return new Double[]{latitude, longitude};
    }

    public String findCheapestLocation(String productName, String zipCode, int radius) throws JSONException, IOException {
        return findCheapestLocation(productName, zipCode, radius, DEFAULT_LIMIT);
    }

    public String findCheapestLocation(String productName, String zipCode, int radius, int limit) throws JSONException, IOException {
        queryForAccessToken();
        Map<String, String> locations = queryForLocations(zipCode, radius, limit);
        if (locations.isEmpty()) {
            return ERROR_NO_LOCATIONS_FOUND;
        }
        String cheapestLocation = null;
        double lowestPrice = Double.MAX_VALUE;
        for (String locationId : locations.keySet()) {
            double lowestPriceAtLocation = queryForLowestPriceAtLocation(productName, locationId, limit);
            if (lowestPriceAtLocation == ERROR_PRODUCT_NONEXISTENT_AT_THIS_LOCATION) {
                continue;
            }
            if (lowestPriceAtLocation < lowestPrice) {
                lowestPrice = lowestPriceAtLocation;
                cheapestLocation = locations.get(locationId);
            }
        }
        return cheapestLocation == null ? ERROR_PRODUCT_NONEXISTENT_AT_ANY_LOCATION : cheapestLocation;
    }
    public double queryForLowestPriceAtLocation(String productName, String locationId) throws JSONException, IOException {
        return queryForLowestPriceAtLocation(productName, locationId, DEFAULT_LIMIT);
    }

    public double queryForLowestPriceAtLocation(String productName, String locationId, int limit) throws IOException, JSONException {

        queryForAccessToken();

        String url = String.format(API_PRODUCTS_REQUEST_URL, productName, locationId, limit);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader(API_LOCATIONS_PRODUCTS_REQUEST_HEADER1_NAME, API_LOCATIONS_PRODUCTS_REQUEST_HEADER1_VALUE)
                .addHeader(API_LOCATIONS_PRODUCTS_REQUEST_HEADER2_NAME, String.format(API_LOCATIONS_PRODUCTS_REQUEST_HEADER2_VALUE, accessToken))
                .build();

        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        JSONObject responseJson = new JSONObject(responseBody);
        JSONArray responseArray = responseJson.getJSONArray(API_RESPONSE_KEY_DATA);

        List<Double> prices = new ArrayList<>();
        for (int i = 0; i < responseArray.length(); i++) {
            JSONObject product = (JSONObject) responseArray.get(i);
            JSONArray itemsArray = product.has(API_RESPONSE_KEY_ITEMS) ? (JSONArray) product.get(API_RESPONSE_KEY_ITEMS) : null;
            JSONObject itemsObject = itemsArray != null ? (JSONObject) itemsArray.get(0) : null;
            JSONObject priceObject = itemsObject != null && itemsObject.has(API_RESPONSE_KEY_PRICE) ? (JSONObject) itemsObject.get(API_RESPONSE_KEY_PRICE) : null;
            double price = priceObject != null && priceObject.has(API_RESPONSE_KEY_REGULAR) ? (double) priceObject.get(API_RESPONSE_KEY_REGULAR) : 0.00;
            if (price != 0.00) prices.add(price);
        }

        if (prices.isEmpty()) {
            return ERROR_PRODUCT_NONEXISTENT_AT_THIS_LOCATION;
        }

        double minimumPrice = prices.get(0);
        for (double price : prices) {
            if (price < minimumPrice) {
                minimumPrice = price;
            }
        }

        return minimumPrice;

    }
    public Map<String, String> queryForLocations(String zipCode, int radius) throws JSONException, IOException {
        return queryForLocations(zipCode, radius, DEFAULT_LIMIT);
    }
    public Map<String, String> queryForLocations(String zipCode, int radius, int limit) throws IOException, JSONException {

        queryForAccessToken();

        String url = String.format(API_LOCATIONS_REQUEST_URL, zipCode, radius, limit);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader(API_LOCATIONS_PRODUCTS_REQUEST_HEADER1_NAME, API_LOCATIONS_PRODUCTS_REQUEST_HEADER1_VALUE)
                .addHeader(API_LOCATIONS_PRODUCTS_REQUEST_HEADER2_NAME, String.format(API_LOCATIONS_PRODUCTS_REQUEST_HEADER2_VALUE, accessToken))
                .build();

        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        JSONObject responseJson = new JSONObject(responseBody);
        JSONArray responseArray = responseJson.getJSONArray(API_RESPONSE_KEY_DATA);

        Map<String, String> locations = new HashMap<>();
        for (int i = 0; i < responseArray.length(); i++) {
            JSONObject location = (JSONObject) responseArray.get(i);
            String locationId = (String) location.get(API_RESPONSE_KEY_LOCATIONID);
            locations.put(locationId, location.toString());
        }

        return locations;

    }

    public void queryForAccessToken() throws IOException, JSONException {

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse(API_ACCESS_TOKEN_MEDIA_TYPE);
        String requestBodyContent = String.format(API_ACCESS_TOKEN_REQUEST_BODY, CLIENT_ID, CLIENT_SECRET, API_ACCESS_TOKEN_SCOPE);
        RequestBody requestBody = RequestBody.create(mediaType, requestBodyContent);
        Request request = new Request.Builder()
                .url(API_ACCESS_TOKEN_REQUEST_URL)
                .method(API_ACCESS_TOKEN_REQUEST_METHOD, requestBody)
                .addHeader(API_ACCESS_TOKEN_REQUEST_HEADER_NAME, API_ACCESS_TOKEN_REQUEST_HEADER_VALUE)
                .build();

        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        JSONObject responseJson = new JSONObject(responseBody);
        accessToken = (String) responseJson.get(API_ACCESS_TOKEN_KEY);

    }

}

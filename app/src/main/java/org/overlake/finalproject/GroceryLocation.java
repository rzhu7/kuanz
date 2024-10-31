package org.overlake.finalproject;

public class GroceryLocation {

    private final double latitude;
    private final double longitude;
    private final double price;
    private final int rankOfCheapest;
    public GroceryLocation(double latitude, double longitude, double price, int rankOfCheapest) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.price = price;
        this.rankOfCheapest = rankOfCheapest;
    }

    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public double getPrice() {
        return price; // for future use in telling the user exact prices at each location
    }
    public int getRankOfCheapest() {
        return rankOfCheapest;
    }
}

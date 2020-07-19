package justinnelson.cs360.com;

import java.util.ArrayList;

public class Campsite {

    // Instance variables
    private int id;
    private String name;
    private String state;
    private String city;
    private ArrayList<String> features;
    private double latitude;
    private double longitude;

    // Empty constructor
    public Campsite() {}

    // Constructor with all variables
    public Campsite(int newId, String newName, String newState, String newCity, ArrayList<String> newFeatures) {
        id = newId;
        name = newName;
        state = newState;
        city = newCity;
        features = newFeatures;
        // Default latitude and longitude
        latitude = 0.0;
        longitude = 0.0;
    }

    // Setters
    public void setId(int newId) {id = newId;}

    public void setName(String newName) {name = newName;}

    public void setState(String newState) {state = newState;}

    public void setCity(String newCity) {city = newCity;}

    public void setFeatures(ArrayList<String> newFeatures) {features = newFeatures;}

    public void setLatitude(double newLat) {latitude = newLat;}

    public void setLongitude(double newLong) {longitude = newLong;}

    // Getters
    public int getId() {return id;}

    public String getName() {return name;}

    public String getState() {return state;}

    public String getCity() {return city;}

    public ArrayList<String> getFeatures() {return features;}

    public double getLatitude() {return latitude;}

    public double getLongitude() {return longitude;}

}

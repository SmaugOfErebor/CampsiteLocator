package justinnelson.cs360.com;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {

    // Database name and version
    private static final int DB_VER = 4;
    private static final String DB_NAME = "campsiteDB.db";

    // Accounts table
    public static final String TABLE_ACCOUNTS = "accounts";
    public static final String ACCOUNTS_COLUMN_ID = "id";
    public static final String ACCOUNTS_COLUMN_USERNAME = "username";
    public static final String ACCOUNTS_COLUMN_PASSWORD = "password";

    // Campsite table
    public static final String TABLE_CAMPSITES = "campsites";
    public static final String CAMPSITE_COLUMN_ID = "id";
    public static final String CAMPSITE_COLUMN_NAME = "name";
    public static final String CAMPSITE_COLUMN_STATE = "state";
    public static final String CAMPSITE_COLUMN_CITY = "city";
    public static final String CAMPSITE_COLUMN_LATITUDE = "latitude";
    public static final String CAMPSITE_COLUMN_LONGITUDE = "longitude";

    // Campsite features table
    public static final String TABLE_FEATURES = "features";
    public static final String FEATURE_COLUMN_ID = "id";
    public static final String FEATURE_COLUMN_CAMPSITE_ID = "campsiteID";
    public static final String FEATURE_COLUMN_FEATURE = "feature";

    // constructor
    public DBHandler(Context ctx, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(ctx, DB_NAME, factory, DB_VER);
    }

    // Creates all tables when the DB is initialized
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the accounts table
        String CREATE_ACCOUNTS_TABLE = "CREATE TABLE " +
                TABLE_ACCOUNTS + "(" +
                ACCOUNTS_COLUMN_ID + " INTEGER PRIMARY KEY," +
                ACCOUNTS_COLUMN_USERNAME + " TEXT," +
                ACCOUNTS_COLUMN_PASSWORD + " TEXT)";
        db.execSQL(CREATE_ACCOUNTS_TABLE);

        // Create the campsite table
        String CREATE_CAMPSITE_TABLE = "CREATE TABLE " +
                TABLE_CAMPSITES + "(" +
                CAMPSITE_COLUMN_ID + " INTEGER PRIMARY KEY," +
                CAMPSITE_COLUMN_NAME + " TEXT," +
                CAMPSITE_COLUMN_STATE + " TEXT," +
                CAMPSITE_COLUMN_CITY + " TEXT," +
                CAMPSITE_COLUMN_LATITUDE + " REAL," +
                CAMPSITE_COLUMN_LONGITUDE + " REAL)";
        db.execSQL(CREATE_CAMPSITE_TABLE);

        // Create the features table
        String CREATE_FEATURES_TABLE = "CREATE TABLE " +
                TABLE_FEATURES + "(" +
                FEATURE_COLUMN_ID + " INTEGER PRIMARY KEY," +
                FEATURE_COLUMN_CAMPSITE_ID + " Integer," +
                FEATURE_COLUMN_FEATURE + " TEXT)";
        db.execSQL(CREATE_FEATURES_TABLE);
    }

    // This method closes an open DB if a new one is created
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CAMPSITES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEATURES);
        onCreate(db);
    }

    // This method adds a new account to the database
    public void addAccount(String username, String password) {
        ContentValues values = new ContentValues();
        values.put(ACCOUNTS_COLUMN_USERNAME, username);
        values.put(ACCOUNTS_COLUMN_PASSWORD, password);

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_ACCOUNTS, null, values);
    }

    public Boolean usernameExists(String username) {
        // Build the query
        String query = "SELECT * FROM " + TABLE_ACCOUNTS +
                " WHERE " + ACCOUNTS_COLUMN_USERNAME +
                " = \"" + username + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        // If moveToFirst is successful, the account exists
        Boolean usernameExists = cursor.moveToFirst();

        cursor.close();

        return usernameExists;
    }

    public Boolean accountLogin(String username, String password) {
        // Build the query
        String query = "SELECT * FROM " + TABLE_ACCOUNTS +
                " WHERE " + ACCOUNTS_COLUMN_USERNAME +
                " = \"" + username + "\"" +
                " AND " + ACCOUNTS_COLUMN_PASSWORD +
                " = \"" + password + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        // If moveToFirst succeeds, login is successful
        Boolean successfulLogin = cursor.moveToFirst();

        cursor.close();

        return successfulLogin;
    }

    // This method adds a new campsite to the database
    public void addCampsite(Campsite campsite) {
        ContentValues values = new ContentValues();
        values.put(CAMPSITE_COLUMN_NAME, campsite.getName());
        values.put(CAMPSITE_COLUMN_STATE, campsite.getState());
        values.put(CAMPSITE_COLUMN_CITY, campsite.getCity());
        values.put(CAMPSITE_COLUMN_LATITUDE, campsite.getLatitude());
        values.put(CAMPSITE_COLUMN_LONGITUDE, campsite.getLongitude());

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_CAMPSITES, null, values);

        // Add campsite features to the database
        for (String feature : campsite.getFeatures()) {
            values = new ContentValues();
            values.put(FEATURE_COLUMN_CAMPSITE_ID, campsite.getId());
            values.put(FEATURE_COLUMN_FEATURE, feature);
            db.insert(TABLE_FEATURES, null, values);
        }
    }

    /**
     * This method searches for a Campsite by exact match
     * @param campsiteName
     * @return
     */
    public Campsite searchCampsite(String campsiteName) {
        // Build the query
        String query = "SELECT * FROM " + TABLE_CAMPSITES +
                       " WHERE " + CAMPSITE_COLUMN_NAME +
                       " = \"" + campsiteName + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Campsite campsite = null;

        if (cursor.moveToFirst()) {
            campsite = singleCampsiteFromCursor(cursor);
        }

        cursor.close();
        db.close();

        return campsite;
    }

    /**
     * This method does a fuzzy search for Campsites
     * @param campsiteName
     * @return campList
     */
    public ArrayList<Campsite> fuzzySearchCampsite(String campsiteName) {
        // A list to hold a Campsite results
        ArrayList<Campsite> campList = new ArrayList<>();

        // Query for an exact match. An exact match should be first always
        Campsite exactMatch = searchCampsite(campsiteName);
        if (exactMatch != null) {
            campList.add(exactMatch);
        }

        // Get the database
        SQLiteDatabase db = this.getWritableDatabase();

        // Try to find matches containing each word provided
        String splitName[] = campsiteName.split(" ");
        for (String word : splitName) {
            // Query for this word
            String query = "SELECT * FROM " + TABLE_CAMPSITES +
                           " WHERE " + CAMPSITE_COLUMN_NAME +
                           " like \"%" + word + "%\"";

            Cursor cursor = db.rawQuery(query, null);

            // Make an ArrayList of any results
            ArrayList<Campsite> resultList = campsiteListFromCursor(cursor);

            // Combine this results list with the existing results list
            campList = combineCampLists(campList, resultList);

            cursor.close();
        }

        // Try to find matches containing each word provided assuming the first and last letters of
        // each word are correct
        for (String word : splitName) {
            // Query for this potentially incorrectly spelled word
            String query = "SELECT * FROM " + TABLE_CAMPSITES +
                    " WHERE " + CAMPSITE_COLUMN_NAME +
                    " like \"%" + word.charAt(0) + "%" + word.charAt(word.length() - 1) + "%\"";

            Cursor cursor = db.rawQuery(query, null);

            // Make an ArrayList of any results
            ArrayList<Campsite> resultList = campsiteListFromCursor(cursor);

            // Combine this results list with the existing results list
            campList = combineCampLists(campList, resultList);

            cursor.close();
        }

        db.close();

        return campList;
    }

    /**
     * This method creates an ArrayList of Campsites from a DB Cursor
     * @param cursor
     * @return
     */
    private ArrayList<Campsite> campsiteListFromCursor(Cursor cursor) {
        ArrayList<Campsite> campList = new ArrayList<>();

        // If we can't move to first, there are no results
        if (cursor.moveToFirst()) {
            // Get the first Campsite
            campList.add(singleCampsiteFromCursor(cursor));

            // Get any remaining Campsites
            while (cursor.moveToNext()) {
                campList.add(singleCampsiteFromCursor(cursor));
            }
        }

        return campList;
    }

    /**
     * This method gets a Campsite object from a Cursor at its current location
     * @param cursor
     * @return
     */
    private Campsite singleCampsiteFromCursor(Cursor cursor) {
        Campsite campsite = new Campsite();
        campsite.setId(Integer.parseInt(cursor.getString(0)));
        campsite.setName(cursor.getString(1));
        campsite.setState(cursor.getString(2));
        campsite.setCity(cursor.getString(3));
        campsite.setLatitude(Double.parseDouble(cursor.getString(4)));
        campsite.setLongitude(Double.parseDouble(cursor.getString(5)));
        // Find all features linked to this campsite and add them to the object
        findAndAddFeatures(campsite);

        return campsite;
    }

    /**
     * This method adds any unique Campsites from the addition list to the base list
     * @param baseList
     * @param additionList
     * @return
     */
    private ArrayList<Campsite> combineCampLists(ArrayList<Campsite> baseList, ArrayList<Campsite> additionList) {
        // Iterate through all addition Campsites
        for (Campsite additionCamp : additionList) {
            Boolean addCampsite = true;

            // Compare this Addition campsite to all existing results to see if it is unique
            for (Campsite baseCamp : baseList) {
                // Compare based on names
                if (baseCamp.getName().equals(additionCamp.getName())) {
                    // Do not add this campsite
                    addCampsite = false;
                    // No need to continue comparing
                    break;
                }
            }

            // Add this campsite if necessary
            if (addCampsite) {
                baseList.add(additionCamp);
            }
        }

        return baseList;
    }

    // This method queries the database for campsite features associated with a campsite
    // The resulting features are added to the campsite object
    public void findAndAddFeatures(Campsite campsite) {
        // Build the query
        String query = "SELECT * FROM " + TABLE_FEATURES +
                " WHERE " + FEATURE_COLUMN_CAMPSITE_ID +
                " = " + campsite.getId();

        Cursor cursor = this.getWritableDatabase().rawQuery(query, null);

        ArrayList<String> features = new ArrayList<String>();

        // Get all the features associated with this campsite
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            features.add(cursor.getString(2));
            while (cursor.moveToNext()) {
                features.add(cursor.getString(2));
            }
        }

        cursor.close();

        campsite.setFeatures(features);
    }

    // This method deletes a campsite with the provided name
    public boolean deleteCampsite(String campsiteName) {
        String query = "SELECT * FROM " + TABLE_CAMPSITES +
                " WHERE " + CAMPSITE_COLUMN_NAME + " = \"" + campsiteName + "\"";

        Campsite campsite = searchCampsite(campsiteName);

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if (campsite != null) {
            db.delete(TABLE_CAMPSITES, CAMPSITE_COLUMN_ID + " = ?",
                    new String[] {String.valueOf(campsite.getId())});
            db.delete(TABLE_FEATURES, FEATURE_COLUMN_CAMPSITE_ID + " = ?",
                    new String[] {String.valueOf(campsite.getId())});
            db.close();
            return true;
        }

        db.close();
        return false;
    }

}

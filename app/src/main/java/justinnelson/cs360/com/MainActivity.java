package justinnelson.cs360.com;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    String currentAccount;

    EditText etUsername;
    EditText etPassword;

    Button favoritesButton;
    Button searchButton;
    Button addNewButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Grab all controls
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        favoritesButton = findViewById(R.id.favoritesButton);
        searchButton = findViewById(R.id.searchButton);
        addNewButton = findViewById(R.id.addNewButton);

        addAllHardCodedCampsites();
        addHardCodedLogin();
    }

    // Attempts to create an account
    public void createAccount(View view) {
        String username = etUsername.getText().toString();
        // Immediately encrypt the password
        // This is to hopefully prevent any unintentional usage of the password in its plaintext form
        // The password is stored in the database in its encrypted form in the event that the
        // database is somehow accessed by another method
        String password = TalesFromTheCrypt.encrypt(etPassword.getText().toString());

        // Check for an empty username
        if (username.equals("")) {
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        // Check for an empty password
        if (etPassword.getText().toString().equals("")) {
            Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        DBHandler db = new DBHandler(this, null, null, 0);

        // Check that the requested username does not already exist
        if (db.usernameExists(username)) {
            Toast.makeText(this, "That username already exists", Toast.LENGTH_SHORT).show();
            return;
        }

        // Finally, create the account
        db.addAccount(username, password);
        Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show();

        // Log into the account
        loginAccount(username);
    }

    // Attempts to authenticate a login
    public void authenticateAccount(View view) {
        String username = etUsername.getText().toString();
        // Immediately encrypt the password
        // This is to hopefully prevent any unintentional usage of the password in its plaintext form
        String password = TalesFromTheCrypt.encrypt(etPassword.getText().toString());
        // Check for an empty username
        if (username.equals("")) {
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        DBHandler db = new DBHandler(this, null, null, 0);
        // Check for valid credentials
        if (!db.accountLogin(username, password)) {
            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            return;
        }

        loginAccount(username);
    }

    // Logs an account in
    private void loginAccount(String username) {
        currentAccount = username;

        // Hide the login layout
        disableLogin();
        // Enable the main buttons and make them visible
        enableMainButtons();
    }

    // Hides and disables the login layout
    private void disableLogin() {
        findViewById(R.id.loginLayout).setVisibility(View.GONE);
        findViewById(R.id.loginLayout).setEnabled(false);
    }

    // Enables all the main activity button and makes them visible
    private void enableMainButtons() {
        searchButton.setEnabled(true);
        addNewButton.setEnabled(true);
        favoritesButton.setVisibility(View.VISIBLE);
        searchButton.setVisibility(View.VISIBLE);
        addNewButton.setVisibility(View.VISIBLE);
    }

    public void startSearchCampsiteActivity(View view) {
        Intent newIntent = new Intent(this, SearchCampsitesActivity.class);
        this.startActivity(newIntent);
    }

    public void startAddCampsiteActivity(View view) {
        Intent newIntent = new Intent(this, AddCampsiteActivity.class);
        this.startActivity(newIntent);
    }

    // Adds all 12 expected hard coded campsites
    private void addAllHardCodedCampsites() {
        // Make a new DB Handler
        DBHandler dbHandler = new DBHandler(this, null, null, 0);

        // Make an array list to use for hard coded features
        ArrayList<String> campsiteFeatures = new ArrayList<>();
        campsiteFeatures.add("Fire Pit");
        campsiteFeatures.add("Bathrooms");

        // Campsite #1
        addHardCodedCampsite(dbHandler, "Alabama Camp", "Alabama", "Montgomery",
                campsiteFeatures, 32.369750, -86.387149);
        // Campsite #2
        addHardCodedCampsite(dbHandler, "Alaska Camp", "Alaska", "Juneau",
                campsiteFeatures, 58.315885, -134.410581);
        // Campsite #3
        addHardCodedCampsite(dbHandler, "Arizona Camp", "Arizona", "Phoenix",
                campsiteFeatures, 33.345779, -112.051363);
        // Campsite #4
        addHardCodedCampsite(dbHandler, "Arkansas Camp", "Arkansas", "Little Rock",
                campsiteFeatures, 34.699675, -92.372126);
        // Campsite #5
        addHardCodedCampsite(dbHandler, "California Camp", "California", "Sacramento",
                campsiteFeatures, 38.546172, -121.504652);
        // Campsite #6
        addHardCodedCampsite(dbHandler, "Colorado Camp", "Colorado", "Denver",
                campsiteFeatures, 39.651268, -105.044791);
        // Campsite #7
        addHardCodedCampsite(dbHandler, "Connecticut Camp", "Connecticut", "Hartford",
                campsiteFeatures, 41.793202, -72.691180);
        // Campsite #8
        addHardCodedCampsite(dbHandler, "Delaware Camp", "Delaware", "Dover",
                campsiteFeatures, 39.148620, -75.556140);
        // Campsite #9
        addHardCodedCampsite(dbHandler, "Florida Camp", "Florida", "Tallahassee",
                campsiteFeatures, 30.358074, -84.156951);
        // Campsite #10
        addHardCodedCampsite(dbHandler, "Georgia Camp", "Georgia", "Atlanta",
                campsiteFeatures, 33.711612, -84.516528);
        // Campsite #11
        addHardCodedCampsite(dbHandler, "Hawaii Camp", "Hawaii", "Honolulu",
                campsiteFeatures, 21.354176, -157.802036);
        // Campsite #12
        addHardCodedCampsite(dbHandler, "Idaho Camp", "Idaho", "Boise",
                campsiteFeatures, 43.567475, -116.140963);
    }

    // Adds a single hard coded campsite
    private void addHardCodedCampsite(DBHandler dbh, String name, String state, String city,
                                      ArrayList<String> features, double lat, double lon) {
        Campsite campsite = new Campsite();
        campsite.setName(name);
        campsite.setState(state);
        campsite.setCity(city);
        campsite.setFeatures(features);
        campsite.setLatitude(lat);
        campsite.setLongitude(lon);
        dbh.addCampsite(campsite);
    }

    private void addHardCodedLogin() {
        DBHandler db = new DBHandler(this, null, null, 0);
        db.addAccount("admin", TalesFromTheCrypt.encrypt("admin"));
    }

}

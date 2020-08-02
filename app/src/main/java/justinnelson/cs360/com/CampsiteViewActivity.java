package justinnelson.cs360.com;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class CampsiteViewActivity extends AppCompatActivity {

    Campsite currentCampsite;

    TextView weatherDescription;
    TextView weatherDegrees;

    TwitterLoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Activity thisActivity = this;

        // Initialize Twitter BEFORE setContentView
        Twitter.initialize(this);

        setContentView(R.layout.activity_campsite_view);

        // Finish initializing the Twitter login button
        loginButton = findViewById(R.id.login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Compose a tweet
                // Successfully logging into Twitter will open a window with a pre-formatted tweet
                // The tweet contains vital information about the campsite that the user logged
                // in from. The user may edit this tweet before tweeting it, or send it as is.
                TweetComposer.Builder builder = new TweetComposer.Builder(thisActivity)
                        .text("I'm camping in " + currentCampsite.getCity() + ", " + currentCampsite.getState() + " at campsite: " + currentCampsite.getName());
                builder.show();
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(thisActivity, "Cannot Tweet unless you log in!", Toast.LENGTH_SHORT).show();
            }
        });

        // Get the Campsite object from the intent
        currentCampsite = (Campsite) getIntent().getSerializableExtra("campsite");

        // Set the TextView texts from the Campsite object
        TextView campsiteNameTextView = findViewById(R.id.campsiteName);
        campsiteNameTextView.setText("Name: " + currentCampsite.getName());

        TextView stateNameTextView = findViewById(R.id.stateName);
        stateNameTextView.setText("State: " + currentCampsite.getState());

        TextView cityNameTextView  = findViewById(R.id.cityName);
        cityNameTextView.setText("City: " + currentCampsite.getCity());

        // Get the weather related controls from the layout
        weatherDescription = findViewById(R.id.weatherDescription);
        weatherDegrees = findViewById(R.id.weatherDegrees);

        // Start the weather thread to make the HTTP request
        WeatherThread wt = new WeatherThread();
        wt.start();
    }

    /**
     * Toggles the 'bookmark' status of the current campsite.
     * Adds/removes the current campsite to/from the bookmarked campsites table in the DB
     * @param view
     */
    public void ToggleBookmark(View view) {
        // TODO: Add logic for adding/removing bookmarked campsites during enhancement 3
    }

    /**
     * This opens a Google Maps intent at the location of the campsite the user is currently viewing
     * The user may use this location for navigation purposes or to decide on which campsite
     * they wish to visit
     * @param view
     */
    public void OpenGoogleMaps(View view) {
        // Create a Uri using the campsite location data
        Uri intentUri = Uri.parse("geo:0,0?q=" + currentCampsite.getLatitude() + "," + currentCampsite.getLongitude() + "(" + currentCampsite.getName() + ")");
        // Create a map intent with this URI
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, intentUri);
        // Set Google Maps as the intent package
        mapIntent.setPackage("com.google.android.apps.maps");
        // Attempt to start an activity that can handle the intent
        startActivity(mapIntent);
    }

    /**
     * Returns the abbreviation of a given state
     * Useful when building the URL string for calls to the weather API
     * @param stateName
     * @return
     */
    private String getStateAbbreviation(String stateName) {
        switch (stateName.toLowerCase()) {
            case "alabama": return "al";
            case "alaska": return "ak";
            case "arizona": return "az";
            case "arkansas": return "ar";
            case "california": return "ca";
            case "colorado": return "co";
            case "connecticut": return "ct";
            case "delaware": return "de";
            case "florida": return "fl";
            case "georgia": return "ga";
            case "hawaii": return "hi";
            case "idaho": return "id";
            case "illinois": return "il";
            case "indiana": return "in";
            case "iowa": return "ia";
            case "kansas": return "ks";
            case "kentucky": return "ky";
            case "louisiana": return "la";
            case "maine": return "me";
            case "maryland": return "md";
            case "massachusetts": return "ma";
            case "michigan": return "mi";
            case "minnesota": return "mn";
            case "mississippi": return "ms";
            case "missouri": return "ms";
            case "montana": return "mt";
            case "nebraska": return "ne";
            case "nevada": return "nv";
            case "new hampshire": return "nh";
            case "new jersey": return "nj";
            case "new mexico": return "nm";
            case "new york": return "ny";
            case "north carolina": return "nc";
            case "north dakota": return "nd";
            case "ohio": return "oh";
            case "oklahoma": return "ok";
            case "oregon": return "or";
            case "pennsylvania": return "pa";
            case "rhode island": return "ri";
            case "south carolina": return "sc";
            case "south dakota": return "sd";
            case "tennessee": return "tn";
            case "texas": return "tx";
            case "utah": return "ut";
            case "vermont": return "vt";
            case "virginia": return "va";
            case "washington": return "wa";
            case "west virginia": return "wv";
            case "wisconsin": return "wi";
            case "wyoming": return "wy";
            default: return "";
        }
    }

    /**
     * Makes an HTTP get request to gather weather data about the campsite's location
     * This gives the user an overall indication of the weather at the campsite's location
     * The user may use this information to decide on a campsite to visit
     */
    class WeatherThread extends Thread {
        public void run() {
            try {
                // Make a URL string to make a request from the weather API
                String urlString = "https://api.openweathermap.org/data/2.5/weather?q=";
                urlString += currentCampsite.getCity().toLowerCase();
                urlString += ",";
                urlString += getStateAbbreviation(currentCampsite.getState());
                urlString += ",us&units=imperial&appid=";
                urlString += getString(R.string.weatherApiKey);

                // Try to create the URL from the string
                URL url = new URL(urlString);

                // Try to open the connection
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

                // Try to get the input stream from the URL connection
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                // Try to read the response
                StringBuilder sb = new StringBuilder();
                int c = 0;
                while ((c = in.read()) != -1) {
                    sb.append((char) c);
                }

                // Try to make a JSON object from the text
                JSONObject jResult = new JSONObject(sb.toString());

                // Get the main pieces of the result JSON
                // Most won't be used just yet, but they could be easily included now
                JSONObject jMain = jResult.getJSONObject("main");
                JSONObject jSys = jResult.getJSONObject("sys");
                JSONObject jWind = jResult.getJSONObject("wind");
                JSONObject jWeather = jResult.getJSONArray("weather").getJSONObject(0);

                // Get the weather description and the temperature
                final String description = jWeather.getString("description");
                final String temperature = jMain.getString("temp") + "°F";

                // Update the text views on the UI thread
                runOnUiThread(new Runnable() {
                    public void run() {
                        weatherDescription.setText(description);
                        weatherDegrees.setText(temperature);
                    }
                });

            } catch (Exception ex) {
                // Abort in the event of a failure
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the login button.
        loginButton.onActivityResult(requestCode, resultCode, data);
    }

}

package justinnelson.cs360.com;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class SearchCampsitesActivity extends AppCompatActivity {

    private EditText campsiteNameET;
    private Spinner stateSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_campsites);

        // Populate the state spinner
        stateSpinner = findViewById(R.id.stateSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.state_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(adapter);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        stateSpinner.setSelection(adapter.getPosition(sharedPref.getString("recentState", "Alabama")));

        // Grab the controls holding input
        campsiteNameET = findViewById(R.id.campsiteName);
    }

    public void onStop() {
        SharedPreferences.Editor prefEdit = getPreferences(Context.MODE_PRIVATE).edit();
        prefEdit.putString("recentState", stateSpinner.getSelectedItem().toString());
        prefEdit.commit();

        super.onStop();
    }

    public void searchCampsites(View view) {
        DBHandler dbHandler = new DBHandler(this, null, null, 0);

        Campsite campsite = dbHandler.searchCampsite(campsiteNameET.getText().toString());

        // TODO: create the page to display campsite information instead of using toast
        if (campsite != null) {
            // Start a campsite view activity with the search results
            Intent newIntent = new Intent(this, CampsiteViewActivity.class);
            newIntent.putExtra("CAMPSITE_NAME", campsite.getName());
            newIntent.putExtra("CAMPSITE_STATE", campsite.getState());
            newIntent.putExtra("CAMPSITE_CITY", campsite.getCity());
            newIntent.putExtra("CAMPSITE_LATITUDE", campsite.getLatitude());
            newIntent.putExtra("CAMPSITE_LONGITUDE", campsite.getLongitude());
            this.startActivity(newIntent);
        } else {
            Toast.makeText(this, "No campsite found with name: " +
                    campsiteNameET.getText().toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void deleteCampsite(View view) {
        DBHandler dbHandler = new DBHandler(this, null, null, 0);

        boolean deleted = dbHandler.deleteCampsite(campsiteNameET.getText().toString());

        if (deleted) {
            Toast.makeText(this, "Campsite with name: " + campsiteNameET.getText().toString() +
                    " successfully deleted", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "No campsite with name: " +
                    campsiteNameET.getText().toString() + " found", Toast.LENGTH_LONG).show();
        }
    }

}

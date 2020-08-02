package justinnelson.cs360.com;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class SearchCampsitesActivity extends AppCompatActivity {

    private EditText campsiteNameET;
    private Spinner stateSpinner;
    private EditText campsiteCityET;

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
        campsiteCityET = findViewById(R.id.campsiteCity);
    }

    public void onStop() {
        SharedPreferences.Editor prefEdit = getPreferences(Context.MODE_PRIVATE).edit();
        prefEdit.putString("recentState", stateSpinner.getSelectedItem().toString());
        prefEdit.commit();

        super.onStop();
    }

    /**
     * Handles the click event of the search button
     * Searches for a list of campsites based on various criteria
     * @param view
     */
    public void searchCampsites(View view) {
        DBHandler dbHandler = new DBHandler(this, null, null, 0);

        ArrayList<Campsite> campsiteList = dbHandler.fuzzySearchCampsite(
                campsiteNameET.getText().toString(),
                stateSpinner.getSelectedItem().toString(),
                campsiteCityET.getText().toString());

        if (!campsiteList.isEmpty()) {
            // Start a campsite list view activity with the search results
            Intent newIntent = new Intent(this, CampsiteListViewActivity.class);
            newIntent.putExtra("campsiteList", campsiteList);
            this.startActivity(newIntent);

        } else {
            Toast.makeText(this, "No campsite found with name: " +
                    campsiteNameET.getText().toString(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Handles the click event of the delete button
     * Deletes the specified campsite by name
     * @param view
     */
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

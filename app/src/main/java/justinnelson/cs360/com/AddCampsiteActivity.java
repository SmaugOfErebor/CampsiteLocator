package justinnelson.cs360.com;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class AddCampsiteActivity extends AppCompatActivity {

    EditText campsiteNameET;
    Spinner stateSpinner;
    EditText campsiteCityET;
    EditText campsiteFeaturesET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_campsite);

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
        campsiteFeaturesET = findViewById(R.id.campsiteFeatures);
    }

    public void onStop() {
        SharedPreferences.Editor prefEdit = getPreferences(Context.MODE_PRIVATE).edit();
        prefEdit.putString("recentState", stateSpinner.getSelectedItem().toString());
        prefEdit.commit();

        super.onStop();
    }

    // Handles the submit button click event
    public void submitNewCampsite(View view) {
        // Do some quick error checking
        if (campsiteNameET.getText().toString().equals("")) {
            Toast.makeText(this, "Campsite must have a name", Toast.LENGTH_LONG).show();
            return;
        }
        if (campsiteCityET.getText().toString().equals("")){
            Toast.makeText(this, "Campsite must have a city", Toast.LENGTH_LONG).show();
            return;
        }
        if (campsiteFeaturesET.getText().toString().equals("")) {
            Toast.makeText(this, "Campsite must have at least one feature", Toast.LENGTH_LONG).show();
        }

        // Build a new campsite object from the input in the form
        Campsite campsite = new Campsite();
        campsite.setName(campsiteNameET.getText().toString());
        campsite.setState(stateSpinner.getSelectedItem().toString());
        campsite.setCity(campsiteCityET.getText().toString());

        // Add all campsite features separated by new lines
        ArrayList<String> features = new ArrayList<>();
        for (String feature : campsiteFeaturesET.getText().toString().split("\n")) {
            features.add(feature);
        }
        campsite.setFeatures(features);

        DBHandler dbHandler = new DBHandler(this, null, null, 0);

        dbHandler.addCampsite(campsite);

        campsiteNameET.setText("");
        campsiteCityET.setText("");
        campsiteFeaturesET.setText("");
        Toast.makeText(this, "Campsite added successfully", Toast.LENGTH_LONG).show();
    }

}

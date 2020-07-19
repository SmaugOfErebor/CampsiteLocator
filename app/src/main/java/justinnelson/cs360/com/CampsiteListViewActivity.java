package justinnelson.cs360.com;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

public class CampsiteListViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campsite_list_view);

        // Get the Campsite list from the intent
        List<Campsite> campsiteList = (List<Campsite>) getIntent().getSerializableExtra("campsiteList");

        // Fill the Campsite ListView
        configureListView(campsiteList);
    }

    /**
     * Fills the campsite list view using an ArrayList of campsites
     * Defines onItemClick for each item
     * @param campsiteList
     */
    private void configureListView(final List<Campsite> campsiteList) {
        // Get the ListView that will hold the Campsites
        ListView campsiteListView = findViewById(R.id.campsiteListView);

        // Create an instance of the custom Campsite Adapter
        CampsiteListAdapter cla = new CampsiteListAdapter(this, R.layout.campsite_list_view_item, campsiteList);

        // Set the adapter on the Campsite ListView
        campsiteListView.setAdapter(cla);

        // Define what to do when a Campsite is clicked
        campsiteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Start a campsite view activity with the clicked Campsite
                Intent newIntent = new Intent(CampsiteListViewActivity.this, CampsiteViewActivity.class);
                newIntent.putExtra("campsite", (Campsite) parent.getItemAtPosition(position));
                startActivity(newIntent);
            }
        });
    }

}

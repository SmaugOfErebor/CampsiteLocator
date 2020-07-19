package justinnelson.cs360.com;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CampsiteListAdapter extends ArrayAdapter<Campsite> {

    private int resourceLayout;
    private Context mContext;

    public CampsiteListAdapter(Context context, int resource, List<Campsite> campsites) {
        super(context, resource, campsites);
        resourceLayout = resource;
        mContext = context;
    }

    /**
     * Builds the custom view from Campsite information to be shown in a ListView
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Inflate the view if it is null
        if (convertView == null) {
            LayoutInflater li = LayoutInflater.from(mContext);
            convertView = li.inflate(resourceLayout, null);
        }

        // Get the Campsite object at the current position
        Campsite c = getItem(position);

        // Only set text on a valid Campsite object
        if (c != null) {
            // Set the text in the TextViews
            TextView tvCampsiteName = convertView.findViewById(R.id.campsiteName);
            if (tvCampsiteName != null) {
                tvCampsiteName.setText(c.getName());
            }

            TextView tvCampsiteCity = convertView.findViewById(R.id.campsiteCity);
            if (tvCampsiteCity != null) {
                tvCampsiteCity.setText(c.getCity());
            }

            TextView tvCampsiteState = convertView.findViewById(R.id.campsiteState);
            if (tvCampsiteState != null) {
                tvCampsiteState.setText(c.getState());
            }
        }

        return convertView;
    }

}

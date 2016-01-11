package savindev.myuniversity.settings;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import savindev.myuniversity.R;

public class SettingsListAdapter extends ArrayAdapter<String>{

    private final Activity context;
    private final String[] list;
    private final Integer[] imageId;
    public SettingsListAdapter(Activity context,
                      String[] list, Integer[] imageId) {
        super(context, R.layout.settings_item, list);
        this.context = context;
        this.list = list;
        this.imageId = imageId;

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.settings_item, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.groupsView);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.settingsImage);
        txtTitle.setText(list[position]);
        imageView.setImageResource(imageId[position]);
        return rowView;
    }
}

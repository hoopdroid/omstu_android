package savindev.myuniversity.notes;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import savindev.myuniversity.R;

//Название вообще не говорящее
public class CustomListAdapter extends ArrayAdapter<String> {

    //замени эти листы на лист из моделей
    private final Activity context;
    private final ArrayList<String> itemname;
    private final ArrayList<String> note_date;
    private final ArrayList<String> tag;


    //параметризуй генерики!!!
    public CustomListAdapter(Activity context, ArrayList itemname,ArrayList eventdate,ArrayList tagslist) {
        super(context, R.layout.notelist,itemname);
        this.context=context;
        this.itemname=itemname;
        this.note_date=eventdate;
        this.tag = tagslist;



    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.notelist, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
        TextView dateTitle = (TextView) rowView.findViewById(R.id.dateTime);

        TextView tagsText = (TextView) rowView.findViewById(R.id.texttags);


        txtTitle.setText(itemname.get(position));

        dateTitle.setText(note_date.get(position));

        tagsText.setText(tag.get(position));

        return rowView;

    };
}

package savindev.myuniversity;

import android.app.Activity;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import android.app.Activity;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

import savindev.myuniversity.notes.NoteModel;

public class CustomNoteAdapter extends ArrayAdapter<NoteModel> {

    private final Activity context;
    private final ArrayList<NoteModel> noteModelArrayList;

    public CustomNoteAdapter(Activity context, ArrayList<NoteModel> noteModelArrayList) {
        super(context, R.layout.notelist,noteModelArrayList);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.noteModelArrayList=noteModelArrayList;




    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.notelist, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
        TextView dateTitle = (TextView) rowView.findViewById(R.id.dateTime);

        TextView tagsText = (TextView) rowView.findViewById(R.id.texttags);


        txtTitle.setText(noteModelArrayList.get(position).getNoteText());

        dateTitle.setText(noteModelArrayList.get(position).getNoteDate());

        tagsText.setText(noteModelArrayList.get(position).getLessonName());

        return rowView;

    };
}

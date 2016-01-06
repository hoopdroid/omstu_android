package savindev.myuniversity.notes;

import android.app.Activity;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import savindev.myuniversity.R;

public class NoteListAdapter extends ArrayAdapter<NoteModel> {

    private final Activity context;
    private final ArrayList<NoteModel> noteModelArrayList;

    public NoteListAdapter(Activity context, ArrayList<NoteModel> noteModelArrayList) {
        super(context, R.layout.notelist,noteModelArrayList);
        this.context=context;
        this.noteModelArrayList=noteModelArrayList;




    }

    public View getView(int position,View view,ViewGroup parent) {

        LayoutInflater inflater=context.getLayoutInflater();

        View rowView=inflater.inflate(R.layout.notelist, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
        TextView dateNote = (TextView)rowView.findViewById(R.id.dateTime);
        ImageView priorityLine = (ImageView)rowView.findViewById(R.id.priorityLine);
        if (noteModelArrayList.get(position).getIsDone() == 1){
            priorityLine.setImageResource(R.drawable.line_green);
            txtTitle.setPaintFlags(txtTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        txtTitle.setText(noteModelArrayList.get(position).getName());
        dateNote.setText(noteModelArrayList.get(position).getDate());

        return rowView;

    }
}

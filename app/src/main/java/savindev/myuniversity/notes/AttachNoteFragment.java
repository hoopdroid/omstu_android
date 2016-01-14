package savindev.myuniversity.notes;


import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

import savindev.myuniversity.notes.AttachActivity;
import savindev.myuniversity.R;
import savindev.myuniversity.db.DBHelper;

/**
 * A simple {@link Fragment} subclass.
 */

//используй модель
    //делай переменные приватными
public class AttachNoteFragment extends Fragment {

    protected static EditText textNote;
    protected static Spinner priority;
    private SharedPreferences settings;
    protected static int userGroup;
    private DBHelper dbHelper;
    protected static String lesson;
    protected static String noteText;
    protected   static String noteName;
    private Button attachPhoto;
    private CheckBox reminderBox;
    protected static int priorityItem;

    public AttachNoteFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_attach_pair_note, container, false);

        textNote = (EditText) view.findViewById(R.id.textNote);
        priority = (Spinner) view.findViewById(R.id.priority);
        attachPhoto = (Button)view.findViewById(R.id.attachPhotoBtn);
        reminderBox = (CheckBox)view.findViewById(R.id.setReminderCheckBox);
        attachPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDevSnackBar();
            }
        });
        reminderBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDevSnackBar();
            }
        });


        settings = getActivity().getSharedPreferences("UserInfo", 0);
        userGroup = settings.getInt("UserGroup", 0);

        dbHelper = new DBHelper(getActivity());

        ArrayAdapter <Priority> adapterPriority = new ArrayAdapter<Priority>(getActivity(), android.R.layout.simple_spinner_dropdown_item, Priority.values());
        priority.setAdapter(adapterPriority);



        return view;
    }


    public static NoteModel saveNote() {

        noteName = AttachActivity.noteName.getText().toString();
        Log.d("NOTE NAME",noteName);
        noteText = textNote.getText().toString();
        String pairId = Integer.toString(AttachActivity.scheduleId)+AttachActivity.date;
        //TODO null for int = -1 ? =-)
        return new NoteModel(noteName, "username",0,
               Priority.fromInt(priority.getSelectedItemPosition()),null,
        null,noteText,AttachActivity.time,pairId,null);


    }

    private void showDevSnackBar(){
        Snackbar snackbar = Snackbar
                .make(getView(),"Функция в разработке =)", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

}

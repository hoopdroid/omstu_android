package savindev.myuniversity.notes;


import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

import savindev.myuniversity.R;
import savindev.myuniversity.db.DBHelper;

/**
 * A simple {@link Fragment} subclass.
 */

//используй модель
    //делай переменные приватными
public class AttachNoteFragment extends Fragment {

    static EditText textNote;
    static Spinner priority;
    static Spinner lessonName;
    SharedPreferences settings;
    static int userGroup;
    DBHelper dbHelper;
    static String lesson;
    static String noteText;
    static int priorityItem;

    public AttachNoteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_attach_note, container, false);

        textNote = (EditText) view.findViewById(R.id.textNote);
        priority = (Spinner) view.findViewById(R.id.priority);
        lessonName = (Spinner) view.findViewById(R.id.lessonName);

        settings = getActivity().getSharedPreferences("UserInfo", 0);
        userGroup = settings.getInt("UserGroup", 0);

        dbHelper = new DBHelper(getActivity());

        ArrayAdapter<?> adapterPriority =
                ArrayAdapter.createFromResource(getActivity(), R.array.notePriority, android.R.layout.simple_spinner_item);
        adapterPriority.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Параметризуй генерики!!!
        ArrayList listLessons = new ArrayList(dbHelper.getSchedulesHelper().
                getGroupLessons(getActivity(), userGroup, true));

        ArrayAdapter<?> adapterLessonName = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, listLessons);
        adapterLessonName.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Вызываем адаптер
        priority.setAdapter(adapterPriority);
        lessonName.setAdapter(adapterLessonName);


        return view;
    }

    //не совсем понимаю, почему сохранение должно возвращать модель.
    public static NoteModel saveNote() {

        lesson = lessonName.getSelectedItem().toString();
        noteText = textNote.getText().toString();
        priorityItem = priority.getSelectedItemPosition();
//        закомментировала. модель изменилась
//        return new NoteModel(userGroup, lesson,
//                null, noteText, "dummy", priorityItem);
        return null;

    }

}

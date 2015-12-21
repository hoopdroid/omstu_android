package savindev.myuniversity;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import savindev.myuniversity.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AttachNoteFragment extends Fragment {


    public AttachNoteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attach_note, container, false);
        return  view;
    }

}

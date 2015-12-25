package savindev.myuniversity.notes;


import android.app.Fragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;

import savindev.myuniversity.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotesFragment extends Fragment {


    public NotesFragment() {
        // Required empty public constructor
    }


    //выкинь листы, используй модель
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_notes, container, false);
        ArrayList taskslist;
        ArrayList datelist;
        ArrayList tagslist;
        taskslist = new ArrayList();
        datelist = new ArrayList();
        tagslist = new ArrayList();




        for(int i = 0 ;i<10;i++){
            taskslist.add("note 1 with big awesome incredible text without any excuses");
            datelist.add(Integer.toString(i)+"декабря");
            tagslist.add("Альтман");
        }

        final SwipeMenuListView listView = (SwipeMenuListView)view.findViewById(R.id.listView);

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getActivity());
                // set item background
                openItem.setBackground(new ColorDrawable(getResources().getColor(R.color.primary)));
                // set item width
                openItem.setWidth(200);
                // set item title
                openItem.setIcon(R.drawable.ic_done_white_24dp);


                // add to menu
                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getActivity());
                // set item background
                deleteItem.setBackground(new ColorDrawable(getResources().getColor(R.color.md_red_500)));
                // set item width
                deleteItem.setWidth(200);
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete_white_24dp);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        CustomListAdapter adapter=new CustomListAdapter(getActivity(), taskslist,datelist,tagslist);



        listView.setMenuCreator(creator);
        listView.setAdapter(adapter);



        return view;
    }


}



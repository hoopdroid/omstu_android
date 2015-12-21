package savindev.myuniversity;


import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.OnButtonClickListener;
import com.dexafree.materialList.card.provider.WelcomeCardProvider;
import com.dexafree.materialList.view.MaterialListView;


/**
 * A simple {@link Fragment} subclass.
 */
public class WelcomeFragment extends Fragment {


    public WelcomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_welcome, container, false);
        final MaterialListView mListView = (MaterialListView) view.findViewById(R.id.material_listview);


        Card welcome_card = new Card.Builder(getActivity())
                .setTag("WELCOME_CARD")
                .setDismissible()
                .withProvider(WelcomeCardProvider.class)
                .setTitle("Данный раздел в разработке!")
                .setTitleColor(Color.WHITE)
                .setDescription("Мы уже работаем над этим")
                .setDescriptionColor(Color.WHITE)
                .setSubtitleColor(Color.WHITE)
                .setBackgroundColor(getResources().getColor(R.color.primary))
                .setButtonText("Закрыть")
                .setOnButtonPressedListener(new OnButtonClickListener() {
                    @Override
                    public void onButtonClicked(final View view, final Card card) {
                        Toast.makeText(getActivity(), "УПС!", Toast.LENGTH_SHORT).show();
                        mListView.animate();
                        mListView.clearAll();
                    }
                })
                .endConfig()
                .build();


        mListView.add(welcome_card);
        MainActivity.fab.hide();
        return view;
    }


}

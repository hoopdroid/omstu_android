package savindev.myuniversity.settings;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import savindev.myuniversity.R;

public class AboutFragment extends Fragment implements View.OnClickListener {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        view.findViewById(R.id.button_error).setOnClickListener(this);
        view.findViewById(R.id.button_functional).setOnClickListener(this);
         view.findViewById(R.id.button_questions).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        switch (v.getId()) {
            case R.id.button_error:
                i.setData(Uri.parse("https://vk.com/topic-111883430_32907493"));
                startActivity(i);
                break;
            case R.id.button_functional:
                i.setData(Uri.parse("https://vk.com/album-111883430_226773828"));
                startActivity(i);
                break;
            case R.id.button_questions:
                i.setData(Uri.parse("https://vk.com/topic-111883430_32907490"));
                startActivity(i);
                break;
        }
    }
}
package savindev.myuniversity.welcomescreen;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import savindev.myuniversity.R;

public class NotInternetFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_not_internet, container, false);
        view.findViewById(R.id.reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent( getActivity() , getActivity().getClass() );
                getActivity().finish();
                getActivity().startActivity(i);
            }
        });
        return view;
    }

}

package savindev.myuniversity;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import savindev.myuniversity.db.DBHelper;
import savindev.myuniversity.welcomescreen.FirstStartActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {

    TextView userNameTxt;
    Button logoutButton;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        logoutButton = (Button)view.findViewById(R.id.buttonLogOut);
        logoutButton.setOnClickListener(this);
        userNameTxt = (TextView)view.findViewById(R.id.usernameTxt);

        SharedPreferences settings = getActivity().getSharedPreferences("UserInfo", 0);

       String username = settings.getString("UserName", "no user");
        userNameTxt.setText(username);
        return view;
    }




    private void alertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Сменить пользователя?")
                .setMessage("Ваши данные могут быть утеряны")

                .setCancelable(false)
                .setPositiveButton("Да",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(getActivity(), FirstStartActivity.class);
                                deleteUserPreferences();//TODO удаление расписаний пользователя,ибо при новом запуске к существующему добавляется еще одно
                                //DBHelper.UsedSchedulesHelper.deleteSchedule(getActivity(),true);
                                startActivity(i);
                                getActivity().finish();
                            }
                        }).setNegativeButton("Нет",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void deleteUserPreferences(){
        SharedPreferences settings = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        settings.edit().clear().apply();
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.buttonLogOut){
            alertDialog();
        }
    }
}

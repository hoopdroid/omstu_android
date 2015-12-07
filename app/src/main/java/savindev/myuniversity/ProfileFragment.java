package savindev.myuniversity;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

        String username = settings.getString("UserFirstName","")+ " "+settings.getString("UserMiddleName","") +" " +settings.getString("UserLastName","");
        userNameTxt.setText(username);
        return view;
    }




    private void alertDialog() {
        final DBHelper dbHelper = DBHelper.getInstance(getActivity());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Сменить пользователя?")
                .setMessage("Ваши данные могут быть утеряны")

                .setCancelable(false)
                .setPositiveButton("Да",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(getActivity(), FirstStartActivity.class);
                                deleteUserPreferences();
                                dbHelper.getUsedSchedulesHelper().deleteMainSchedule(getActivity());
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

        SharedPreferences.Editor editor = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE).edit();
        editor.remove("UserLastName");
        editor.remove("UserFirstName");
        editor.remove("UserMiddleName");
        editor.remove("UserGroup");
        editor.remove("email");
        editor.remove("password");
        editor.remove("UserId").commit();
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.buttonLogOut){
            alertDialog();
        }
    }
}

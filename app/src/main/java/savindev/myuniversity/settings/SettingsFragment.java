package savindev.myuniversity.settings;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import savindev.myuniversity.MainActivity;
import savindev.myuniversity.R;
import savindev.myuniversity.db.DBHelper;
import savindev.myuniversity.db.DBRequest;
import savindev.myuniversity.serverTasks.GetUniversityInfoTask;
import savindev.myuniversity.welcomescreen.FirstStartActivity;

/**
 * Фрагмент - лист с элементами настроек
 */
public class SettingsFragment extends Fragment {


    private int lastPosition;
    private MenuItem refreshItem;
    private GroupsFragment groups;
    private GetUniversityInfoTask guit;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        setRetainInstance(true); //Запрет на пересоздание объекта
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        final ListView settings = (ListView) view.findViewById(R.id.settingsView);
        Integer[] imageId = {
                R.drawable.ic_calendar_blank_grey600_36dp,
                R.drawable.ic_weather_sunny_grey600_36dp,
                R.drawable.ic_account_search,
                R.drawable.ic_information_outline_grey600_36dp,
        };
        SettingsListAdapter adapter = new SettingsListAdapter(getActivity(),
                getResources().getStringArray(R.array.settings_array),imageId);
        settings.setAdapter(adapter);
        groups = new GroupsFragment();
        //При тыке на пункт меню
        settings.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (position) {
                    case 0: //Нулевая позиция - выбор групп
                        //проверка на наличие записей по IitializationInfo в БД, если нет - попытаться загрузить
                        if (!DBRequest.isUniversityInfoThere(getActivity())) {
                            if (MainActivity.isNetworkConnected(getActivity())) {
                                refreshItem.setActionView(R.layout.actionbar_progress);
                                refreshItem.setVisible(true);
                                guit = new GetUniversityInfoTask(getActivity().getBaseContext(), null);
                                guit.execute();
                                try {
                                    guit.get(7, TimeUnit.SECONDS);
//                                    if (guit.get(7, TimeUnit.SECONDS)) {
//                                        //Подождать загрузку данных
//                                        //TODO проанимировать это дело
//                                    }
                                } catch (InterruptedException | ExecutionException | TimeoutException e1) {
                                    e1.printStackTrace();
                                }
                                refreshItem.setVisible(false);
                            } else {
                                Toast.makeText(getActivity(), "Не удалось получить списки" + '\n'
                                        + "Проверьте соединение с интернетом", Toast.LENGTH_LONG).show();
                            }
                        }
                        if (DBRequest.isUniversityInfoThere(getActivity())) { //если выполнено успешно или ключ существует
                            settings.getChildAt(lastPosition).setBackgroundColor(Color.WHITE);
                            lastPosition = position;
                            //Запустить активность/фрагмент для выбора группы
                            if (getResources().getConfiguration().orientation ==
                                    Configuration.ORIENTATION_PORTRAIT) {
                                Intent intent = new Intent(getActivity(), GroupsActivity.class);
                                startActivity(intent);
                            } else {
                                settings.setBackgroundColor(Color.WHITE);
                                settings.getChildAt(position).setBackgroundColor(getActivity().getResources().getColor(R.color.primary));
                                getFragmentManager().beginTransaction().replace(R.id.frgmCont, groups).commit();
                            }
                        }
                        break;
                    case 1:
                        settings.getChildAt(lastPosition).setBackgroundColor(Color.WHITE);
                        lastPosition = position;
                        if (getResources().getConfiguration().orientation ==
                                Configuration.ORIENTATION_PORTRAIT) {
                            Intent intent = new Intent(getActivity(), VitalizationActivity.class);
                            startActivity(intent);
                        } else {
                            settings.setBackgroundColor(Color.WHITE);
                            settings.getChildAt(position).setBackgroundColor(getActivity().getResources().getColor(R.color.primary));
                            getFragmentManager().beginTransaction().replace(R.id.frgmCont, new VitalizationFragment()).commit();
                        }
                        break;
                    case 3://TODO Добавить информацию о приложении
                        //Тестовая функция, перевод порта
                        /*settings.setBackgroundColor(Color.WHITE);
                        settings.getChildAt(lastPosition).setBackgroundColor(Color.WHITE);
                        lastPosition = position;
                        if (getResources().getConfiguration().orientation ==
                                Configuration.ORIENTATION_LANDSCAPE) {
                            settings.getChildAt(position).setBackgroundColor(getActivity().getResources().getColor(R.color.primary));
                        }
                        Intent intent = new Intent(getActivity(), Test.class);
                        startActivity(intent);
                        */
                        break;
                    case 2: // Возможность перейти на авторизацию через настройку( убираем профаил в header поэтому нужная вещь)
                        Intent authIntent = new Intent (getActivity(), FirstStartActivity.class);
                        deleteUserPreferences();
                        startActivity(authIntent);
                        MainActivity.mainActivity.finish();//убиваем instance активит
                        break;
                    default:
                        break;
                }
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.settings, menu);
        refreshItem = menu.findItem(R.id.download_pb);
        refreshItem.setActionView(R.layout.actionbar_progress);
        refreshItem.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void deleteUserPreferences(){
        DBHelper dbHelper = DBHelper.getInstance(getActivity());
        dbHelper.getUsedSchedulesHelper().deleteMainSchedule(getActivity());
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE).edit();
        editor.remove("UserLastName");
        editor.remove("UserFirstName");
        editor.remove("UserMiddleName");
        editor.remove("UserGroup");
        editor.remove("email");
        editor.remove("password");
        editor.remove("UserId").commit();
    }

    //TODO Сделать обработку для API<17
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onDestroy() {
        if(!MainActivity.mainActivity.isDestroyed()) {
            if (guit != null) //завершение запроса, если он активен
                guit.cancel(false);
            getFragmentManager().beginTransaction().remove(groups).commit();
        }//отсоединение фрагмента с настройками, чтобы в actionBar не было лишних элементов
        super.onDestroy();
    }

}
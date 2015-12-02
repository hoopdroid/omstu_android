package savindev.myuniversity.settings;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
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
import savindev.myuniversity.db.DBRequest;
import savindev.myuniversity.serverTasks.GetUniversityInfoTask;

/**
 * Фрагмент - лист с элементами настроек
 */
public class SettingsFragment extends Fragment {


    FragmentTransaction ft;
    //	FrameLayout cont;
    int lastPosition;
    //	Boolean reload = false;
    MenuItem refreshItem;
    GroupsFragment groups;
    GetUniversityInfoTask guit;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        setRetainInstance(true); //Запрет на пересоздание объекта
        View view = inflater.inflate(R.layout.fragment_settings, null);
        final ListView settints = (ListView) view.findViewById(R.id.settingsView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.settings_item, getResources().getStringArray(R.array.settings_array));
        settints.setAdapter(adapter);

        groups = new GroupsFragment();
        //При тыке на пункт меню
        settints.setOnItemClickListener(new OnItemClickListener() {
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
                                    if (guit.get(7, TimeUnit.SECONDS)) {
                                        //Подождать загрузку данных
                                        //TODO проанимировать это дело
                                    }
                                } catch (InterruptedException | ExecutionException | TimeoutException e1) {
                                    e1.printStackTrace();
                                    refreshItem.setVisible(false);
                                }
                            } else {
                                Toast.makeText(getActivity(), "Не удалось получить списки" + '\n'
                                        + "Проверьте соединение с интернетом", Toast.LENGTH_LONG).show();
                            }
                        }
                        if (DBRequest.isUniversityInfoThere(getActivity())) { //если выполнено успешно или ключ существует
                            settints.setBackgroundColor(Color.WHITE); //перекрасить выделенный элемент
                            settints.getChildAt(lastPosition).setBackgroundColor(Color.WHITE);
                            lastPosition = position;
                            if (getResources().getConfiguration().orientation ==
                                    Configuration.ORIENTATION_LANDSCAPE) {
                                settints.getChildAt(position).setBackgroundColor(getActivity().getResources().getColor(R.color.primary));
                                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                fragmentTransaction.replace(R.id.frgmCont, new GroupsFragment());
                                fragmentTransaction.commit();
                            }
                            //Запустить активность/фрагмент для выбора группы
                            if (getResources().getConfiguration().orientation ==
                                    Configuration.ORIENTATION_PORTRAIT) {
                                Intent intent = new Intent(getActivity(), GroupsActivity.class);
                                startActivity(intent);
                            } else {
                                ft = getFragmentManager().beginTransaction();
                                ft.replace(R.id.frgmCont, groups).commit();
                            }
                        }
                        break;
                    case 1:
                        settints.setBackgroundColor(Color.WHITE);
                        settints.getChildAt(lastPosition).setBackgroundColor(Color.WHITE);
                        lastPosition = position;
                        if (getResources().getConfiguration().orientation ==
                                Configuration.ORIENTATION_LANDSCAPE) {
                            settints.getChildAt(position).setBackgroundColor(getActivity().getResources().getColor(R.color.primary));
                        }
                        break;
                    case 2:
                        settints.setBackgroundColor(Color.WHITE);
                        settints.getChildAt(lastPosition).setBackgroundColor(Color.WHITE);
                        lastPosition = position;
                        if (getResources().getConfiguration().orientation ==
                                Configuration.ORIENTATION_LANDSCAPE) {
                            settints.getChildAt(position).setBackgroundColor(getActivity().getResources().getColor(R.color.primary));
                        }
                        break;
                    case 3:
                        settints.setBackgroundColor(Color.WHITE);
                        settints.getChildAt(lastPosition).setBackgroundColor(Color.WHITE);
                        lastPosition = position;
                        if (getResources().getConfiguration().orientation ==
                                Configuration.ORIENTATION_LANDSCAPE) {
                            settints.getChildAt(position).setBackgroundColor(getActivity().getResources().getColor(R.color.primary));
                        }
                        break;
                    case 4: //Тестовая функция, перевод порта
                        settints.setBackgroundColor(Color.WHITE);
                        settints.getChildAt(lastPosition).setBackgroundColor(Color.WHITE);
                        lastPosition = position;
                        if (getResources().getConfiguration().orientation ==
                                Configuration.ORIENTATION_LANDSCAPE) {
                            settints.getChildAt(position).setBackgroundColor(getActivity().getResources().getColor(R.color.primary));
                        }
                        Intent intent = new Intent(getActivity(), Test.class);
                        startActivity(intent);
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

    @Override
    public void onDestroy() {
        if (guit != null) //завершение запроса, если он активен
            guit.cancel(false);
        ft = getFragmentManager().beginTransaction(); //отсоединение фрагмента с настройками, чтобы в actionBar не было лишних элементов
        ft.remove(groups).commit();
        super.onDestroy();
    }

}
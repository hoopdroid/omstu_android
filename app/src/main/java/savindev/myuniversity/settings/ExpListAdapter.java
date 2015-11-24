package savindev.myuniversity.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import savindev.myuniversity.R;
import savindev.myuniversity.db.DBHelper;
import savindev.myuniversity.schedule.GroupsModel;

public class ExpListAdapter extends BaseExpandableListAdapter implements Filterable {
    /**
     * Кастомный адаптер, используется для отображения списка групп и преподавателей
     * Совмещает в себе возможность выбора списка групп и основной группы
     */
    private ArrayFilter mFilter;
    private ArrayList<ArrayList<GroupsModel>> mGroup;
    private Context mContext;
    private ArrayList<String> mNames;
    private final Object mLock = new Object();
    private ArrayList<ArrayList<GroupsModel>> mOriginalValues;
    private ArrayList<String> mOriginalNames;
    private ArrayList<GroupsModel> deleteList = new ArrayList<GroupsModel>();
    private ArrayList<GroupsModel> addList = new ArrayList<GroupsModel>();
    private GroupsModel main;


    public ExpListAdapter(Context context, ArrayList<String> names,
                          ArrayList<ArrayList<GroupsModel>> groups) {
        mContext = context;
        mNames = names;
        mGroup = groups;

        //Для выделения элементов, сохранных в расписании ранее
        ArrayList<GroupsModel> oldListModel = DBHelper.UsedSchedulesHelper.getGroupsModelList(context); //Список старых групп
        main = DBHelper.UsedSchedulesHelper.getMainGroupModel(context); //Основная группа, чтобы ее не добавлять и не удалять
        ArrayList<Integer> oldlistId = new ArrayList<>(); //Два листа для уникальности: требуется сравнить все поля по и id idGroup
        ArrayList<Boolean> oldlist = new ArrayList<>();
        for (GroupsModel model : oldListModel) {
            oldlistId.add(model.getId());
            oldlist.add(model.isGroup());
        }
        for (int i = 0; i < mNames.size(); i++)
            for (int j = 0; j < mGroup.get(i).size(); j++) {
                for (int k = 0; k < oldListModel.size(); k++) {
                    if (oldlistId.get(k) == mGroup.get(i).get(j).getId() && //Если есть совпадение - проверить по признаку isGroup
                            oldlist.get(k) == mGroup.get(i).get(j).isGroup()) {
                        mGroup.get(i).get(j).setSelected(true);
                    }
                }
            }

    }

    @Override
    public int getGroupCount() {
        return mGroup.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mGroup.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroup.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mGroup.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                             ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.group_view, null);
        }

        TextView textGroup = (TextView) convertView.findViewById(R.id.textGroup);
        textGroup.setText(mNames.get(groupPosition));

        return convertView;

    }

    static class ViewHolder {
        public TextView textView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        View view = null;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.child_view, null);
        if (mGroup.get(groupPosition).get(childPosition).isSelected()) {
            view.setBackgroundColor(mContext.getResources().getColor(R.color.primary)); //Выделение ранее сохраненных групп
        }
        if (mGroup.get(groupPosition).get(childPosition).getId() == main.getId() &&
                mGroup.get(groupPosition).get(childPosition).isGroup() == main.isGroup()) {
            view.setBackgroundColor(Color.CYAN); //Выделение главной группы
        }
        final ViewHolder holder = new ViewHolder();
        holder.textView = (TextView) view.findViewById(R.id.textChild);

        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(mGroup.get(groupPosition).get(childPosition).getId() == main.getId() &&
                        mGroup.get(groupPosition).get(childPosition).isGroup() == main.isGroup())) { //Выполнять только для не главной группы
                    if (!mGroup.get(groupPosition).get(childPosition).isSelected()) {
                        view.setBackgroundColor(mContext.getResources().getColor(R.color.primary));
                        mGroup.get(groupPosition).get(childPosition).setSelected(true);
                        addGroup(mGroup.get(groupPosition).get(childPosition));
                        Log.d("11", "add " + mGroup.get(groupPosition).get(childPosition).getName());
                    } else {
                        view.setBackgroundColor(Color.WHITE);
                        mGroup.get(groupPosition).get(childPosition).setSelected(false);
                        deleteGroup(mGroup.get(groupPosition).get(childPosition));
                        Log.d("11", "add " + mGroup.get(groupPosition).get(childPosition).getName());
                    }
                }
            }
        });
        view.setTag(holder);
        holder.textView.setText(mGroup.get(groupPosition).get(childPosition).getName());
        return view;
    }

    private void deleteGroup(GroupsModel group) {
        if (addList.contains(group)) {
            addList.remove(group);
        } else {
            deleteList.add(group);
        }
    }

    private void addGroup(GroupsModel group) {
        if (deleteList.contains(group)) {
            deleteList.remove(group);
        } else {
            addList.add(group);
        }
    }

    public ArrayList<GroupsModel> getAddList() {
        return addList;
    }

    public ArrayList<GroupsModel> getDeleteList() {
        return deleteList;
    }

    public void deleteLists() {
        addList = new ArrayList<>();
        deleteList = new ArrayList<>();
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    private class ArrayFilter extends Filter {
        FilterResults resultsNames = new FilterResults();

        @SuppressLint("DefaultLocale")
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                synchronized (mLock) {
                    mOriginalValues = new ArrayList<ArrayList<GroupsModel>>(mGroup);
                    mOriginalNames = new ArrayList<String>(mNames);
                }
            }
            if (prefix == null || prefix.length() == 0) {
                ArrayList<ArrayList<GroupsModel>> list;
                ArrayList<String> names;
                synchronized (mLock) {
                    list = new ArrayList<ArrayList<GroupsModel>>(mOriginalValues);
                    names = new ArrayList<String>(mOriginalNames);
                }
                results.values = list;
                results.count = list.size();
                resultsNames.values = names;
                resultsNames.count = names.size();
            } else {
                String prefixString = prefix.toString().replaceAll(" ", "").toLowerCase();
                ArrayList<ArrayList<GroupsModel>> values;
                synchronized (mLock) {
                    values = new ArrayList<ArrayList<GroupsModel>>(mOriginalValues);
                }


                final ArrayList<ArrayList<GroupsModel>> newValues = new ArrayList<ArrayList<GroupsModel>>();
                final ArrayList<String> newNames = new ArrayList<String>();

                for (int i = 0; i < values.size(); i++) {
                    final int count = values.get(i).size();
                    final ArrayList<GroupsModel> newValuesChild = new ArrayList<GroupsModel>();
                    for (int j = 0; j < count; j++) {
                        final GroupsModel value = values.get(i).get(j);
                        final String valueText = value.getName().toString().replaceAll(" ", "").toLowerCase(Locale.getDefault());

                        // First match against the whole, non-splitted value
                        if (valueText.startsWith(prefixString)) {
                            newValuesChild.add(value);
                        } else {     //TODO доработать, эта часть должна определять наличие вообще в тексте, а не вначале
                            final String[] words = valueText.split(" ");
                            final int wordCount = words.length;

                            // Start at index 0, in case valueText starts with space(s)
                            for (int k = 0; k < wordCount; k++) {
                                if (words[k].startsWith(prefixString)) {
                                    newValuesChild.add(value);
                                    break;
                                }
                            }
                        }
                    }
                    if (newValuesChild.size() > 0) {
                        newValues.add(newValuesChild);
                        newNames.add(mOriginalNames.get(i));
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
                resultsNames.values = newNames;
                resultsNames.count = newNames.size();

            }

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //noinspection unchecked
            mGroup = (ArrayList<ArrayList<GroupsModel>>) results.values;
            mNames = (ArrayList<String>) resultsNames.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
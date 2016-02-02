package com.alexmarken.navigator.my.university.util.Boxlists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alexmarken.navigator.my.university.R;

import java.util.ArrayList;


public class SearchPersonAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<SearchPersonListItemObject> objects;

    public SearchPersonAdapter(Context context, ArrayList<SearchPersonListItemObject> products) {
        ctx = context;
        objects = products;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // кол-во элементов
    @Override
    public int getCount() {
        return objects.size();
    }

    // элемент по позиции
    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    // id по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }

    // пункт списка
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        View view = convertView;

        if (view == null)
            view = lInflater.inflate(R.layout.item_search_teacher, parent, false);

        TextView tvPersonItemName = (TextView) view.findViewById(R.id.tvPersonItemName);
        TextView tvPersonItemPosition = (TextView) view.findViewById(R.id.tvPersonItemPosition);
        TextView tvPersonItemDirecting = (TextView) view.findViewById(R.id.tvPersonItemDirecting);

        SearchPersonListItemObject item = getProduct(position);

        tvPersonItemName.setText(item.getPerson().getName());
        tvPersonItemPosition.setText(item.getPerson().getPosition());
        tvPersonItemDirecting.setText(item.getPerson().getDirecting());

        return view;
    }

    // товар по позиции
    SearchPersonListItemObject getProduct(int position) {
        return ((SearchPersonListItemObject) getItem(position));
    }

    // содержимое корзины
    ArrayList<SearchPersonListItemObject> getBox() {
        ArrayList<SearchPersonListItemObject> box = new ArrayList<SearchPersonListItemObject>();
        for (SearchPersonListItemObject p : objects) {
            box.add(p);
        }
        return box;
    }
}

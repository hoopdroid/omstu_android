package com.alexmarken.navigator.my.university.util.Boxlists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alexmarken.navigator.my.university.R;

import java.util.ArrayList;


public class MainAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<MainListItemObject> objects;

    public MainAdapter(Context context, ArrayList<MainListItemObject> products) {
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
            view = lInflater.inflate(R.layout.item_mainlist, parent, false);

        TextView tvItemName = (TextView) view.findViewById(R.id.tvItemName);
        TextView tvItemDescr = (TextView) view.findViewById(R.id.tvItemDescr);
        ImageView imgView = (ImageView) view.findViewById(R.id.imgView);

        MainListItemObject item = getProduct(position);

        tvItemName.setText(item.getName());
        tvItemDescr.setText(item.getDesr());
        imgView.setBackgroundResource(item.getImgId());

        return view;
    }

    // товар по позиции
    MainListItemObject getProduct(int position) {
        return ((MainListItemObject) getItem(position));
    }

    // содержимое корзины
    ArrayList<MainListItemObject> getBox() {
        ArrayList<MainListItemObject> box = new ArrayList<MainListItemObject>();
        for (MainListItemObject p : objects) {
            box.add(p);
        }
        return box;
    }
}

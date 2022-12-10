package com.example.insorma;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class AdapterProduct extends BaseAdapter {


    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater = null;


    public AdapterProduct(Activity a, ArrayList<HashMap<String, String>> d) {


        activity = a;
        data = d;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public int getCount() {
        return data.size();
    }
    public Object getItem(int position) {
        return position;
    }
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (convertView == null)

        v = inflater.inflate(R.layout.list_product, null);
        TextView name = (TextView) v.findViewById(R.id.name);
        TextView price = (TextView) v.findViewById(R.id.price);
        TextView rating = (TextView) v.findViewById(R.id.rating);
        ImageView image = (ImageView) v.findViewById(R.id.image);
        HashMap<String, String> list = new HashMap<String, String>();
        list = data.get(position);

        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        final int prc = Integer.parseInt(list.get(Konfigurasi.PRODUCT_PRICE));
        final String PRICE = formatRupiah.format((double)prc);

        name.setText (list.get(Konfigurasi.PRODUCT_NAME));
        price.setText  ("Price : "+PRICE);
        rating.setText  ("Rating : "+list.get(Konfigurasi.PRODUCT_RATING));

        Picasso.get().load(Konfigurasi.URL_IMAGE+list.get(Konfigurasi.PRODUCT_IMAGE))
                .into(image);

        return v;
    }
}
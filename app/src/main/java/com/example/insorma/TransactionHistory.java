package com.example.insorma;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class TransactionHistory extends AppCompatActivity {
    private String JSON_STRING,session;

    private ListView listView;
    private TextView txtKosong;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        session = (sharedPreferences.getString("session", null));
        getSupportActionBar().setTitle("TRANSACTION HISTORY");
        getHistory();
        listView = (ListView) findViewById(R.id.listhistory);
        txtKosong = (TextView) findViewById(R.id.txtkosong);

    }

    private void getHistory() {
        class GetJSON extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(TransactionHistory.this,"Proccess","Waiting...",false,false);
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                showHistory();
            }
            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(Konfigurasi.URL_HISTORY,session);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }



    private void showHistory(){
        JSONObject jsonObject = null;
        ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String, String>>();
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(Konfigurasi.TAG_JSON_ARRAY);
            for(int i = 0; i<result.length(); i++){
                JSONObject jo = result.getJSONObject(i);
                String id = jo.getString(Konfigurasi.HISTORY_ID);
                String furniture = jo.getString(Konfigurasi.HISTORY_FURNITURE);
                String quantity = jo.getString(Konfigurasi.HISTORY_QUANTITY);
                String total = jo.getString(Konfigurasi.HISTORY_TOTAL);
                String date = jo.getString(Konfigurasi.HISTORY_DATE);

                Locale localeID = new Locale("in", "ID");
                NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
                final int prc = Integer.parseInt(total);
                final String TOTAL = formatRupiah.format((double)prc);


                HashMap<String,String> data = new HashMap<>();
                data.put(Konfigurasi.HISTORY_ID,"ID Transaction : "+id);
                data.put(Konfigurasi.HISTORY_FURNITURE,"Furniture Name : "+furniture);
                data.put(Konfigurasi.HISTORY_QUANTITY,"Bought Quantity : "+quantity);
                data.put(Konfigurasi.HISTORY_TOTAL,"Total Price : "+TOTAL);
                data.put(Konfigurasi.HISTORY_DATE,date);
                list.add(data);
            }
            if(result.length()==0){
                txtKosong.setVisibility(View.VISIBLE);
            }
            else{
                txtKosong.setVisibility(View.INVISIBLE);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        ListAdapter adapter = new SimpleAdapter(
                TransactionHistory.this, list, R.layout.list_transaction,
                new String[]{Konfigurasi.HISTORY_ID, Konfigurasi.HISTORY_FURNITURE, Konfigurasi.HISTORY_QUANTITY, Konfigurasi.HISTORY_TOTAL, Konfigurasi.HISTORY_DATE},
                new int[]{R.id.id, R.id.furniture, R.id.quantity, R.id.total, R.id.date});
        listView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu, menu);
        //getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.home){
            startActivity(new Intent(this, HomePage.class));
            finish();
        }
        if (item.getItemId()==R.id.history){
            startActivity(new Intent(this, TransactionHistory.class));
            finish();
        }
        if (item.getItemId()==R.id.profile){
            startActivity(new Intent(this, Profile.class));
            finish();
        }


        return true;
    }

}
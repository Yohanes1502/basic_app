package com.example.insorma;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class HomePage extends AppCompatActivity implements  ListView.OnItemClickListener{

    private String JSON_STRING,session, EMAIL, PHONE, USERNAME ;
    private ArrayList<HashMap<String,String>> list;
    private ListView listView;
    private TextView txtKosong;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        session = (sharedPreferences.getString("session", null));
        getProfile();

        getSupportActionBar().setElevation(0);
        getProduct();
        listView = (ListView) findViewById(R.id.listproduct);
        listView.setOnItemClickListener(this);
        txtKosong = (TextView) findViewById(R.id.txtkosong);

    }

    private void getProfile() {
        class GetJSON extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(HomePage.this,"Proccess","Waiting...",false,false);
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                showProfil(s);
            }
            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(Konfigurasi.URL_PROFIL,session);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    private void showProfil(String json){
        try{
            JSONObject jsonObject = new JSONObject(json);
            JSONArray result = jsonObject.getJSONArray(Konfigurasi.TAG_JSON_ARRAY);
            JSONObject c = result.getJSONObject(0);
            String Username = c.getString(Konfigurasi.USER_USERNAME);
            String Email = c.getString(Konfigurasi.USER_EMAIL);
            String Phone = c.getString(Konfigurasi.USER_PHONE);

            USERNAME = Username;
            EMAIL = Email;
            PHONE = Phone;
            getSupportActionBar().setTitle("Welcome, "+USERNAME);

        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void getProduct() {
        class GetJSON extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(HomePage.this,"Proccess","Waiting...",false,false);
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                showProduct();
            }
            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequest(Konfigurasi.URL_PRODUCT);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    private void showProduct(){
        AdapterProduct adapter;
        JSONObject jsonObject = null;
//        ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String, String>>();
        list = new ArrayList<HashMap<String, String>>();
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(Konfigurasi.TAG_JSON_ARRAY);
            for(int i = 0; i<result.length(); i++){
                JSONObject jo = result.getJSONObject(i);
                String id = jo.getString(Konfigurasi.PRODUCT_ID);
                String name = jo.getString(Konfigurasi.PRODUCT_NAME);
                String price = jo.getString(Konfigurasi.PRODUCT_PRICE);
                String rating = jo.getString(Konfigurasi.PRODUCT_RATING);
                String desk = jo.getString(Konfigurasi.PRODUCT_DESC);
                String image = jo.getString(Konfigurasi.PRODUCT_IMAGE);

                HashMap<String,String> data = new HashMap<>();
                data.put(Konfigurasi.PRODUCT_ID,id);
                data.put(Konfigurasi.PRODUCT_NAME,name);
                data.put(Konfigurasi.PRODUCT_PRICE,price);
                data.put(Konfigurasi.PRODUCT_RATING,rating);
                data.put(Konfigurasi.PRODUCT_IMAGE,image);
                data.put(Konfigurasi.PRODUCT_DESC,desk);
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
        adapter = new AdapterProduct(this, list);
        listView.setAdapter(adapter);
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, DetailProduct.class);
        HashMap<String, String> map = list.get(position);
        String idproduct = map.get(Konfigurasi.PRODUCT_ID);
        String nameproduct = map.get(Konfigurasi.PRODUCT_NAME).toString();
        String priceproduct = map.get(Konfigurasi.PRODUCT_PRICE).toString();
        String ratingproduct = map.get(Konfigurasi.PRODUCT_RATING).toString();
        String imageproduct = map.get(Konfigurasi.PRODUCT_IMAGE).toString();
        intent.putExtra("id",idproduct);
        intent.putExtra("name",nameproduct);
        intent.putExtra("price",priceproduct);
        intent.putExtra("rating",ratingproduct);
        intent.putExtra("image",imageproduct);
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
            Intent intent = new Intent(this, Profile.class);
            intent.putExtra("username",USERNAME);
            intent.putExtra("email",EMAIL);
            intent.putExtra("phone",PHONE);
            startActivity(intent);
            finish();
        }


        return true;
    }
}
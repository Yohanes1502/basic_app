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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class DetailProduct extends AppCompatActivity implements View.OnClickListener {

    private TextView txtName, txtPrice, txtRating;
    private ImageView imgProduct;
    private EditText edtQuantity;
    private Button btnBuy;
    private String Name, Price, Rating, Image, Id, session;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_product);

        sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        session = (sharedPreferences.getString("session", null));

        //get data product
        Intent intent = getIntent();
        Id = intent.getStringExtra("id");
        Name = intent.getStringExtra("name");
        Rating = intent.getStringExtra("rating");
        Price = intent.getStringExtra("price");
        Image = intent.getStringExtra("image");

        txtName = (TextView) findViewById(R.id.name);
        txtPrice = (TextView) findViewById(R.id.price);
        txtRating = (TextView) findViewById(R.id.rating);
        imgProduct = (ImageView) findViewById(R.id.gambar);

        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

        final int prc = Integer.parseInt(Price);
        final String PRICE = formatRupiah.format((double)prc);

        txtName.setText(Name);
        txtRating.setText("Rating : "+Rating);
        txtPrice.setText(PRICE);
        Picasso.get().load(Konfigurasi.URL_IMAGE+Image)
                .into(imgProduct);

        edtQuantity = (EditText) findViewById(R.id.quantity);
        btnBuy = (Button) findViewById(R.id.btnbuy);
        btnBuy.setOnClickListener(this);

        getSupportActionBar().setTitle("DETAIL PRODUCT");

    }

    @Override
    public void onClick(View v) {
        if(v==btnBuy){
            if(edtQuantity.getText().toString().equalsIgnoreCase("0")){
                edtQuantity.requestFocus();
                edtQuantity.setError("Quantity cannot be zero");
            }else if(edtQuantity.getText().toString().isEmpty()){
                edtQuantity.requestFocus();
                edtQuantity.setError("Quantity cannot be empty");
            }else{
                buyAction();
            }
        }
    }

    private void buyAction() {
        final String quantity = edtQuantity.getText().toString().trim();
        class Buy extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(DetailProduct.this,"Proccess...","Waiting...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();

//                Toast.makeText(Login.this,s,Toast.LENGTH_SHORT).show();

                if(s.equalsIgnoreCase("0")){
                    Toasty.error(DetailProduct.this, "Failde to buy !", Toast.LENGTH_SHORT, true).show();
                }
                else if(!s.equalsIgnoreCase("") && !s.equalsIgnoreCase("0") && !s.isEmpty() && s.equalsIgnoreCase("1") ){
                    Toasty.success(DetailProduct.this, "Success to buy...", Toast.LENGTH_SHORT, true).show();
                    edtQuantity.setText("");
//                    Intent j = new Intent(DetailProduct.this, HomePage.class);
//                    startActivity(j);
//                    finish();
                }
                else{
                    Toasty.error(DetailProduct.this,"Check Internet Connection !",Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put(Konfigurasi.QUANTITY,quantity);
                params.put(Konfigurasi.PRODUCT_ID,Id);
                params.put(Konfigurasi.USER_ID,session);
                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(Konfigurasi.URL_BUY, params);
                return res;
            }
        }

        Buy ae = new Buy();
        ae.execute();
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
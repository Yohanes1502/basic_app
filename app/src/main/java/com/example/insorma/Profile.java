package com.example.insorma;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class Profile extends AppCompatActivity implements View.OnClickListener {

    private Button btnEdit, btnSave, btnLogout, btnDelete;
    private EditText edtUsername;
    private TextView txtUsername, txtEmail, txtPhone;
    SharedPreferences sharedPreferences;
    private String session, Username, Phone, Email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        session = (sharedPreferences.getString("session", null));
        Intent intent = getIntent();
        Username = intent.getStringExtra("username");
        Phone = intent.getStringExtra("phone");
        Email = intent.getStringExtra("email");


        getSupportActionBar().setTitle("PROFILE");
        edtUsername = (EditText)findViewById(R.id.edtusername);
        txtUsername = (TextView) findViewById(R.id.username);
        txtEmail = (TextView) findViewById(R.id.email);
        txtPhone = (TextView) findViewById(R.id.phone);
        txtPhone.setText(Phone);
        txtEmail.setText(Email);
        txtUsername.setText(Username);
        edtUsername.setText(Username);

        btnEdit = (Button) findViewById(R.id.btnedit);
        btnSave = (Button) findViewById(R.id.btnsave);
        btnLogout = (Button) findViewById(R.id.btnlogout);
        btnDelete = (Button) findViewById(R.id.btndelete);
        btnEdit.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnLogout.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v==btnEdit){
            btnSave.setVisibility(View.VISIBLE);
            btnEdit.setVisibility(View.GONE);
            txtUsername.setVisibility(View.INVISIBLE);
            edtUsername.setVisibility(View.VISIBLE);
        }
        if(v==btnSave){
            if(edtUsername.getText().toString().isEmpty()){
                edtUsername.requestFocus();
                edtUsername.setError("Field can't be empty");
            }else{
                actSave();
            }
        }
        if(v==btnLogout){
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to logout?").setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(getResources().getString(R.string.prefStatus),"logout");
                            editor.putString("session", "");
                            editor.apply();
                            startActivity(new Intent(Profile.this, MainActivity.class));
                            finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
        if(v==btnDelete){
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to delete the account?").setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            actDelete();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }


    private void actDelete() {
        class JSON extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Profile.this,"Proccess...","Waiting...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();

//                Toast.makeText(Profile.this,s,Toast.LENGTH_SHORT).show();

                if(s.equalsIgnoreCase("0")){
                    Toasty.error(Profile.this, "Failed!", Toast.LENGTH_SHORT, true).show();
                }
                else if(!s.equalsIgnoreCase("") && !s.equalsIgnoreCase("0") && !s.isEmpty() && s.equalsIgnoreCase("1")){
                    Toasty.success(Profile.this, "Account deleted...", Toast.LENGTH_SHORT, true).show();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(getResources().getString(R.string.prefStatus),"logout");
                    editor.putString("session", "");
                    editor.apply();
                    startActivity(new Intent(Profile.this, MainActivity.class));
                    finish();

                }
                else{
                    Toasty.error(Profile.this,"Check Internet Connection !",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put("id",session);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(Konfigurasi.URL_DELETE, params);
                return res;
            }
        }

        JSON ae = new JSON();
        ae.execute();
    }

    private void actSave() {
        final String username = edtUsername.getText().toString();
        class JSON extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Profile.this,"Proccess...","Waiting...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();

//                Toast.makeText(Profile.this,s,Toast.LENGTH_SHORT).show();

                if(s.equalsIgnoreCase("0")){
                    Toasty.error(Profile.this, "Failed!", Toast.LENGTH_SHORT, true).show();
                }
                else if(s.equalsIgnoreCase("101")){
                    Toasty.error(Profile.this, "Username already in use, try again!", Toast.LENGTH_SHORT, true).show();
                }
                else if(!s.equalsIgnoreCase("") && !s.equalsIgnoreCase("0") && !s.isEmpty() && s.equalsIgnoreCase("1")){
                    Toasty.success(Profile.this, "Success...", Toast.LENGTH_SHORT, true).show();
                    btnSave.setVisibility(View.GONE);
                    btnEdit.setVisibility(View.VISIBLE);
                    txtUsername.setVisibility(View.VISIBLE);
                    edtUsername.setVisibility(View.GONE);

                }
                else{
                    Toasty.error(Profile.this,"Check Internet Connection !",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put(Konfigurasi.USER_USERNAME,username);
                params.put(Konfigurasi.USER_ID,session);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(Konfigurasi.URL_EDIT, params);
                return res;
            }
        }

        JSON ae = new JSON();
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
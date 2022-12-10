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
import android.widget.Toast;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtEmail, edtPass;
    private Button btnRegister, btnLogin;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edtEmail = (EditText) findViewById(R.id.edtemail);
        edtPass = (EditText) findViewById(R.id.edtpass);
        btnRegister = (Button) findViewById(R.id.btnregister);
        btnLogin = (Button) findViewById(R.id.btnlogin);
        btnRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        getSupportActionBar().hide();
        sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String loginStatus = sharedPreferences.getString(getResources().getString(R.string.prefStatus),"");
        if(loginStatus.equals("login")){
            startActivity(new Intent(MainActivity.this, HomePage.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View v) {
        if(v==btnLogin){
            if(edtEmail.getText().toString().isEmpty()){
                edtEmail.requestFocus();
                edtEmail.setError("Field can't be empty");
            }else if(edtPass.getText().toString().isEmpty()){
                edtPass.requestFocus();
                edtPass.setError("Field can't be empty");
            }else{
                aksiLogin();
            }
        }
        if(v==btnRegister){
            Intent intent = new Intent(MainActivity.this, Register.class);
            startActivity(intent);
            finish();
        }
    }

    private void aksiLogin() {
        final String email = edtEmail.getText().toString().trim();
        final String pass = edtPass.getText().toString().trim();

        class CLogin extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MainActivity.this,"Proccess...","Waiting...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();

//                Toast.makeText(Login.this,s,Toast.LENGTH_SHORT).show();

                if(s.equalsIgnoreCase("gagal")){
                    Toasty.error(MainActivity.this, "Login Failed! Check email and password", Toast.LENGTH_SHORT, true).show();
                }
                else if(!s.equalsIgnoreCase("") && !s.equalsIgnoreCase("gagal") && !s.isEmpty() ){
                    Toasty.success(MainActivity.this, "Success...", Toast.LENGTH_SHORT, true).show();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(getResources().getString(R.string.prefStatus), "login");
                    editor.putString("session", s);
                    editor.apply();
                    Intent j = new Intent(MainActivity.this, HomePage.class);
                    startActivity(j);
                    finish();
                }
                else{
                    Toasty.error(MainActivity.this,"Check Internet Connection !",Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put(Konfigurasi.USER_EMAIL,email);
                params.put(Konfigurasi.USER_PASSWORD,pass);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(Konfigurasi.URL_LOGIN, params);
                return res;
            }
        }

        CLogin ae = new CLogin();
        ae.execute();
    }




}
package com.example.insorma;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;

public class Register extends AppCompatActivity implements View.OnClickListener{
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +      //any letter
                    "(?=\\S+$)" +           //no white spaces
                    ".{3,}" +               //at least 4 characters
                    "$");

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[c]+[o]+[m]+";

    private EditText edtEmail, edtUsername, edtPhone, edtPass;
    private Button btnRegister, btnLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar);

        edtEmail = (EditText) findViewById(R.id.edtemail);
        edtUsername = (EditText) findViewById(R.id.edtusername);
        edtPass = (EditText) findViewById(R.id.edtpass);
        edtPhone = (EditText) findViewById(R.id.edthp);
        btnRegister = (Button) findViewById(R.id.btnregister);
        btnLogin = (Button) findViewById(R.id.btnlogin);
        btnRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        getSupportActionBar().hide();

    }


    private boolean validatePassword() {
        String passwordInput = edtPass.getText().toString().trim();

        if (passwordInput.isEmpty()) {
            edtPass.setError("Field can't be empty");
            edtPass.requestFocus();
            return false;
        } else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            edtPass.setError("Password must contain both letters and numbers (alphanumeric)");
            edtPass.requestFocus();
            return false;
        } else {
            edtPass.setError(null);
            return true;
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }



    @Override
    public void onClick(View v) {
        if(v==btnRegister){
            if (edtEmail.getText().toString().isEmpty()){
                edtEmail.setError("Field can't be empty");
                edtEmail.requestFocus();
            }
            else if (!edtEmail.getText().toString().trim().matches(emailPattern)) {
                edtEmail.setError("Invalid  email address");
                edtEmail.requestFocus();
            }
            else if(edtUsername.getText().toString().isEmpty()){
                edtUsername.setError("Field can't be empty");
                edtUsername.requestFocus();
            }
            else if(edtUsername.getText().toString().length()<3){
                edtUsername.setError("Username must be between 3 and 20 characters");
                edtUsername.requestFocus();
            }
            else if(edtUsername.getText().toString().length()>20){
                edtUsername.setError("Username must be between 3 and 20 characters");
                edtUsername.requestFocus();
            }
            else if (edtPhone.getText().toString().isEmpty()) {
                edtPhone.setError("Invalid  email address");
                edtPhone.requestFocus();
            }
            else if (!validatePassword()) {
                return;
            }
            else{
                aksiDaftar();
            }


        }
        if(v==btnLogin){
            Intent j = new Intent(Register.this, MainActivity.class);
            startActivity(j);
            finish();
        }
    }

    private void aksiDaftar() {
        final String email = edtEmail.getText().toString().trim();
        final String pass = edtPass.getText().toString().trim();
        final String username = edtUsername.getText().toString().trim();
        final String phone = edtPhone.getText().toString().trim();
        class CDaftar extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Register.this,"Proccess...","Waiting...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();

//                Toast.makeText(Daftar.this,s,Toast.LENGTH_SHORT).show();

                if(s.equalsIgnoreCase("0")){
                    Toasty.error(Register.this, "Registration Failed!", Toast.LENGTH_SHORT, true).show();
                }
                else if(s.equalsIgnoreCase("101")){
                    Toasty.error(Register.this, "Email already in use, try again!", Toast.LENGTH_SHORT, true).show();
                }
                else if(s.equalsIgnoreCase("102")){
                    Toasty.error(Register.this, "Username already in use, try again!", Toast.LENGTH_SHORT, true).show();
                }
                else if(s.equalsIgnoreCase("100")){
                    Toasty.error(Register.this, "Email and username already in use, try again!", Toast.LENGTH_SHORT, true).show();
                }
                else if(!s.equalsIgnoreCase("") && !s.equalsIgnoreCase("0") && !s.isEmpty() && s.equalsIgnoreCase("1")){
                    Toasty.success(Register.this, "Success...", Toast.LENGTH_SHORT, true).show();
                    Intent j = new Intent(Register.this, MainActivity.class);
                    startActivity(j);
                    finish();

                }
                else{
                    Toasty.error(Register.this,"Check Internet Connection !",Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put(Konfigurasi.USER_EMAIL,email);
                params.put(Konfigurasi.USER_USERNAME,username);
                params.put(Konfigurasi.USER_PHONE,phone);
                params.put(Konfigurasi.USER_PASSWORD,pass);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(Konfigurasi.URL_REGISTER, params);
                return res;
            }
        }

        CDaftar ae = new CDaftar();
        ae.execute();
    }

    @Override
    public void onBackPressed() {
        Intent j = new Intent(Register.this, MainActivity.class);
        startActivity(j);
        finish();
    }
}
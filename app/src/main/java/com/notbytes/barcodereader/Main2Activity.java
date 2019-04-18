package com.notbytes.barcodereader;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class Main2Activity extends AppCompatActivity {

    EditText et_name, et_pass;
    String login_name, login_pass, msg;

    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ImageView img = findViewById(R.id.logo);
        et_name = findViewById(R.id.etLogin);
        et_pass = findViewById(R.id.etPass);
    }


    public void userLogin(View v) {
        login_name = et_name.getText().toString();
        login_pass = et_pass.getText().toString();
        if (!login_name.equals("") && !login_pass.equals("")) {
            Background bg = new Background(this);
            bg.execute(login_name, login_pass, msg);
            intent = new Intent(this, SecondActivity.class);
        } else {
            Toast.makeText(this, "Fill up All Field", Toast.LENGTH_LONG).show();
        }
    }

    public void searchProduct(View v) {
        intent = new Intent(this, ViewProduct.class);
        startActivity(intent);
    }

    public class Background extends AsyncTask<String, Void, String> {


        AlertDialog dialog;
        Context context;

        public Background(Context context) {
            this.context = context;
        }


        @Override
        protected void onPostExecute(String s) {

            if (s.equals("login Successful")) {
                startActivity(intent);
            } else {
                dialog.setMessage("Invalid Username Or Password!" + "\n" + "Try Again!");
                dialog.show();
            }
        }

        @Override
        protected void onPreExecute() {
            dialog = new AlertDialog.Builder(context).create();
            dialog.setTitle("Login Status");
        }


        @Override
        protected String doInBackground(String... voids) {

            String result = "";
            String login_name = voids[0];
            String login_pass = voids[1];

            String constr = "http://accsectiondemo.site11.com/loginhere.php";

            try {
                URL url = new URL(constr);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("POST");
                http.setDoInput(true);
                http.setDoOutput(true);

                OutputStream ops = http.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops, "UTF-8"));
                String data = URLEncoder.encode("login_name", "UTF-8") + "=" + URLEncoder.encode(login_name, "UTF-8") + "&&" +
                        URLEncoder.encode("login_pass", "UTF-8") + "=" + URLEncoder.encode(login_pass, "UTF-8");


                writer.write(data);
                writer.flush();
                writer.close();
                ops.close();

                InputStream ips = http.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(ips, "ISO-8859-1"));
                String line = "";

                while ((line = reader.readLine()) != null) {

                    result += line;

                }
                reader.close();
                ips.close();
                http.disconnect();
                return result;

            } catch (MalformedURLException e) {
                result = e.getMessage();
            } catch (IOException e) {
                result = e.getMessage();
            }

            return result;
        }
    }
}

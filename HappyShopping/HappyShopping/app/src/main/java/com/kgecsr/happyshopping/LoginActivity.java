package com.kgecsr.happyshopping;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class LoginActivity extends AppCompatActivity {
    private EditText phone, password;
    private Button loginbtn, new_userbtn;
    private ProgressDialog loadingBar;
    private String ip = "";
    private String code = "";
    private String msg1 = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        phone = (EditText) findViewById(R.id.login__Phone);
        password = (EditText) findViewById(R.id.login_password);
        loginbtn = (Button) findViewById(R.id.login_btn);
        new_userbtn = (Button) findViewById(R.id.new_user_btn);
        loadingBar = new ProgressDialog(this);
        ip = getIntent().getExtras().get("ip").toString();

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String p = phone.getText().toString();
                String q = password.getText().toString();
                saveinfo(p,q);
            }
        });
        new_userbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                intent.putExtra("ip", ip);
                startActivity(intent);
            }
        });

    }

    private void saveinfo(String p1,String p2) {


        if (TextUtils.isEmpty(p1)) {
            Toast.makeText(this, "Please write phone number", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(p2)) {
            Toast.makeText(this, "Please write password", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("loggin account");
            loadingBar.setMessage("please wait,while we are checking the credentias");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            code = "login-"+
                    p1 + "-" + p2;
            MyTask st = new MyTask(ip, code,p1);
            st.execute();
            loadingBar.dismiss();




        }
    }

    class MyTask extends AsyncTask<Void, Void, Void> {
        private Socket s;
        private PrintWriter printWriter;
        private BufferedReader in;
        String ip, code,p1;
        public String msg;

        public MyTask() {
        }

        public MyTask(String ip, String code,String p1) {
            this.ip = ip;
            this.code = code;
            this.p1=p1;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                s = new Socket(ip, 80);

                printWriter = new PrintWriter(s.getOutputStream());

                int length=code.length();
                String len=String.valueOf(length);
                printWriter.write(len);
                printWriter.flush();
                printWriter.write(code);
                printWriter.flush();
                in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                msg = in.readLine();

                Log.i("sundori", "doInBackground: " + msg);
                in.close();
                printWriter.close();
                s.close();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(msg.equals("logged in successfully"))
                        {
                            Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.putExtra("ip", ip);
                            intent.putExtra("phone", p1);
                            startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
                        }



                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}



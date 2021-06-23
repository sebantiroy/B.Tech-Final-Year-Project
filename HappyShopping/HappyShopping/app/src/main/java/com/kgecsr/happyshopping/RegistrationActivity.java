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
import java.net.ServerSocket;
import java.net.Socket;

public class RegistrationActivity extends AppCompatActivity {
    private EditText name, address, phone, password, repassword;
    private Button regbtn, cancelbtn;
    private ProgressDialog loadingBar;
    private String ip = "";
    private String code = "";
    private static Socket s;
    private static PrintWriter printWriter;
    private String msg = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        name = (EditText) findViewById(R.id.user_name);
        address = (EditText) findViewById(R.id.user_address);
        phone = (EditText) findViewById(R.id.user_phone);
        password = (EditText) findViewById(R.id.user_password);
        repassword = (EditText) findViewById(R.id.user_reenter_password);
        regbtn = (Button) findViewById(R.id.reg_btn);
        cancelbtn = (Button) findViewById(R.id.cancel_reg_btn);
        loadingBar = new ProgressDialog(this);
        ip = getIntent().getExtras().get("ip").toString();
        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveinfo();


            }
        });
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                intent.putExtra("ip", ip);
                startActivity(intent);

            }
        });
    }

    private void saveinfo() {
        String username = name.getText().toString();
        String useraddress = address.getText().toString();
        String userphone = phone.getText().toString();
        String userpassword = password.getText().toString();
        String userrepassword = repassword.getText().toString();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Please write name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(useraddress)) {
            Toast.makeText(this, "Please write address", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(userphone)) {
            Toast.makeText(this, "Please write your phone number", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(userpassword)) {
            Toast.makeText(this, "Please write your password", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(userrepassword)) {
            Toast.makeText(this, "Please write your password again", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Register account");
            loadingBar.setMessage("please wait,while we are checking the credentias");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            code = "appuser-"+username + "-" + useraddress + "-" + userphone + "-" + userpassword + "-" + userrepassword;
            MyTask st = new MyTask(ip, code);
            st.execute();

            loadingBar.dismiss();
            Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
            intent.putExtra("ip", ip);
            startActivity(intent);
        }
    }

    class MyTask extends AsyncTask<Void, Void, Void> {
        private Socket s;
        private PrintWriter printWriter;
        private BufferedReader in;
        String ip, code;
        public String msg;

        public MyTask() {
        }

        public MyTask(String ip, String code) {
            this.ip = ip;
            this.code = code;
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
                Log.i("msg", "doInBackground: " + msg);
                in.close();
                printWriter.close();
                s.close();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RegistrationActivity.this, msg, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}

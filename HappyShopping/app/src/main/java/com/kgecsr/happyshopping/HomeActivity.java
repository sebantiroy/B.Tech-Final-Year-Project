package com.kgecsr.happyshopping;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class HomeActivity extends AppCompatActivity {
    private Button add, remove, view, end;
    private String phone,ip;
    private String num="0";
    private String code="";
    private static Socket s;
    private static PrintWriter printWriter;
    private String msg2="";
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        add = (Button) findViewById(R.id.Add_product_btn);
        remove = (Button) findViewById(R.id.remove_product_btn);
        view = (Button) findViewById(R.id.view_product_btn);
        end = (Button) findViewById(R.id.End_btn);
        phone=getIntent().getExtras().get("phone").toString();
        ip=getIntent().getExtras().get("ip").toString();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                num="1";
                Intent intent=new Intent(HomeActivity.this,BarcodeScanActivity.class);
                intent.putExtra("phone",phone);
                intent.putExtra("ip",ip);
                intent.putExtra("num",num);
                startActivity(intent);

            }
        });
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                num="2";
                Intent intent=new Intent(HomeActivity.this,BarcodeScanActivity.class);
                intent.putExtra("phone",phone);
                intent.putExtra("ip",ip);
                intent.putExtra("num",num);
                startActivity(intent);

            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                num="3";
                code=phone+"-"+num;
                MyTask st=new MyTask(ip,code,num);
                st.execute();


            }
        });
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle("Are you sure??");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        num="4";
                        code=phone+"-"+num;
                        MyTask st=new MyTask(ip,code,num);
                        st.execute();
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog=builder.create();
                dialog.show();


            }
        });

    }

class MyTask extends AsyncTask<Void, Void, Void> {
    private Socket s;
    private PrintWriter printWriter;
    private BufferedReader in;
    private BufferedReader ind;
    String ip, code;
    public String msg;

    public MyTask() {
    }

    public MyTask(String ip, String code,String num) {
        this.ip = ip;
        this.code = code;
    }
    @Override
    protected Void doInBackground(Void... voids) {
        try {
            s = new Socket(ip, 80);
            printWriter = new PrintWriter(s.getOutputStream());
            Log.i("msg", "doInBackground: " + code);
            int length=code.length();
            String len=String.valueOf(length);
            printWriter.write(len);
            printWriter.flush();
            printWriter.write(code);
            printWriter.flush();
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            if(num.equals("3"))
            {

                msg = in.readLine();
                if(msg==null)
                {
                    msg="empty";
                }
            }
            else if(num.equals("4"))
            {

                msg = in.readLine();


            }
            Log.i("msg", "doInBackground: " + msg);
            in.close();
            printWriter.close();
            s.close();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if(num.equals("3"))
                    {
                        Toast.makeText(HomeActivity.this,"Viewing cart",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(HomeActivity.this,ViewActivity.class);
                        intent.putExtra("phone",phone);
                        intent.putExtra("ip",ip);
                        intent.putExtra("num",num);
                        intent.putExtra("msg",msg);
                        startActivity(intent);
                    }
                    else if(num.equals("4"))
                    {
                        Toast.makeText(HomeActivity.this,"Ending shopping",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(HomeActivity.this,EndShoppingActivity.class);
                        intent.putExtra("phone",phone);
                        intent.putExtra("ip",ip);
                        intent.putExtra("num",num);
                        intent.putExtra("msg",msg);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
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


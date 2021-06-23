package com.kgecsr.happyshopping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class HomeActivity extends AppCompatActivity {
    private Button add, remove, view, end;
    private String phone,ip;
    private String num="0";
    private String code="";
    private static Socket s;
    private static PrintWriter printWriter;

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
                num="3";
                Intent intent=new Intent(HomeActivity.this,BarcodeScanActivity.class);
                intent.putExtra("phone",phone);
                intent.putExtra("ip",ip);
                intent.putExtra("num",num);
                startActivity(intent);

            }
        });
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num="4";
                Intent intent=new Intent(HomeActivity.this,BarcodeScanActivity.class);
                intent.putExtra("phone",phone);
                intent.putExtra("ip",ip);
                intent.putExtra("num",num);
                startActivity(intent);

            }
        });

    }

}

package com.kgecsr.happyshopping;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class BarcodeScanActivity extends AppCompatActivity {
    private ImageView imageView;
    private Button cancel;
    private String phone,ip,num;
    private String message="";
    private String code="";
    private static Socket s;
    private static PrintWriter printWriter;
    private  String msg="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scan);
        imageView=(ImageView)findViewById(R.id.ScanCode);
        cancel=(Button)findViewById(R.id.cancel_btn);
        phone=getIntent().getExtras().get("phone").toString();
        Log.i("msg1", "doInBackground: " + phone);
        ip=getIntent().getExtras().get("ip").toString();
        Log.i("msg2", "doInBackground: " + ip);
        num=getIntent().getExtras().get("num").toString();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanCode();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(BarcodeScanActivity.this,HomeActivity.class);
                startActivity(intent);
            }
        });

    }
    private void scanCode() {
        IntentIntegrator integrator=new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning Barcode");
        integrator.initiateScan();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        IntentResult result=IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result!=null)
        {
            if(result.getContents() != null)
            {
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setMessage(result.getContents());
                message=result.getContents();
                code=phone+"-"+num+"-"+message;
                builder.setTitle("Scanning Result");
                builder.setPositiveButton("Scan Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        scanCode();
                    }
                }).setNegativeButton("Finish", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("msg1", "doInBackground: " + code);
                        MyTask st=new MyTask(ip,code);
                        st.execute();
                        Log.i("msg2", "doInBackground: " + msg);

                        finish();

                    }
                }).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });
                AlertDialog dialog=builder.create();
                dialog.show();
            }
            else
            {
                Toast.makeText(this, "No Results", Toast.LENGTH_SHORT).show();
            }
        }else
        {
            super.onActivityResult(requestCode,resultCode,data);
        }
    }
    @SuppressLint("StaticFieldLeak")
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
                Log.i("msg", "doInBackground: " + code);
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
                        Toast.makeText(BarcodeScanActivity.this, msg, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }
    }

}

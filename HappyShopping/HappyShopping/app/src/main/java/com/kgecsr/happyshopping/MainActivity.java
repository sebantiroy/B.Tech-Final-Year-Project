package com.kgecsr.happyshopping;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class MainActivity extends AppCompatActivity {
    String ip="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        scanCode();

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
                ip=result.getContents();
                builder.setTitle("Scanning Result");
                Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                intent.putExtra("ip",ip);
                startActivity(intent);
                builder.setPositiveButton("Scan Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        scanCode();
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
}

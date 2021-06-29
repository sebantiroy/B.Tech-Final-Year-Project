package com.kgecsr.happyshopping;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ViewActivity extends AppCompatActivity {
String msg="";
double total=0;
    private TextView tb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        msg=getIntent().getExtras().get("msg").toString();
        if(msg.equals("empty"))
        {
            tb=(TextView)findViewById(R.id.empty);
            tb.setText("Your shopping has not started yet");
        }
        else
        {
            String arr[]=msg.split("-");
            String items[][]=new String[arr.length][4];
            for(int i=0;i<arr.length;i++)
            {
                String a[]=arr[i].split("\\|");
                Log.i("msg"+i, "doInBackground: " + arr[i]);
                for(int j=0;j<4;j++)
                {
                    items[i][j]=a[j];
                }
            }
            for(int i=0;i<arr.length;i++)
            {
                total+=Double.parseDouble(items[i][3]);
            }
            init(items,arr.length,total);
        }

    }
    public void init(String a[][],int n,Double total) {
        TableLayout stk = (TableLayout) findViewById(R.id.table_main);
        TableRow tbrow1 = new TableRow(this);
        TextView tv6 = new TextView(this);
        tv6.setPadding(280,0,0,0);
        tv6.setTypeface(null, Typeface.BOLD);
        tv6.setText("Total price :"+total);
        tv6.setTextColor(Color.WHITE);
        //tbrow1.addView(tv6);
        TableRow tbrow0 = new TableRow(this);
        TextView tv0 = new TextView(this);
        tv0.setText("  Sl.No");
        tv0.setTextColor(Color.WHITE);
        tbrow0.addView(tv0);
        TextView tv1 = new TextView(this);
        tv1.setText(" Product Name ");
        tv1.setTextColor(Color.WHITE);
        tbrow0.addView(tv1);
        TextView tv2 = new TextView(this);
        tv2.setText(" Product Price ");
        tv2.setTextColor(Color.WHITE);
        tbrow0.addView(tv2);
        TextView tv3 = new TextView(this);
        tv3.setText(" Quantity ");
        tv3.setTextColor(Color.WHITE);
        tbrow0.addView(tv3);
        TextView tv4 = new TextView(this);
        tv4.setText(" Total Price ");
        tv4.setTextColor(Color.WHITE);
        tbrow0.addView(tv4);
        stk.addView(tv6);
        stk.addView(tbrow0);
        for (int i = 0; i <n; i++) {
            TableRow tbrow = new TableRow(this);
            TextView t1v = new TextView(this);
            t1v.setText("" + (i+1));
            t1v.setTextColor(Color.WHITE);
            t1v.setGravity(Gravity.CENTER);
            tbrow.addView(t1v);
            TextView t2v = new TextView(this);
            t2v.setText(a[i][0]);
            t2v.setTextColor(Color.WHITE);
            t2v.setGravity(Gravity.CENTER);
            tbrow.addView(t2v);
            TextView t3v = new TextView(this);
            t3v.setText(a[i][1]);
            t3v.setTextColor(Color.WHITE);
            t3v.setGravity(Gravity.CENTER);
            tbrow.addView(t3v);
            TextView t4v = new TextView(this);
            t4v.setText(a[i][2]);
            t4v.setTextColor(Color.WHITE);
            t4v.setGravity(Gravity.CENTER);
            tbrow.addView(t4v);
            TextView t5v = new TextView(this);
            t5v.setText(a[i][3]);
            t5v.setTextColor(Color.WHITE);
            t5v.setGravity(Gravity.CENTER);
            tbrow.addView(t5v);
            stk.addView(tbrow);
        }

    }
}

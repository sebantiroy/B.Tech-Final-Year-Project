package com.kgecsr.happyshopping;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


/*class MyTask extends AsyncTask<Void,Void,Void>

{
    private  Socket s;
    private PrintWriter printWriter;
    //private static InputStreamReader in;
    private  BufferedReader in;
    String incomingMessage;
    String ip,code,rec;
    public String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public MyTask()
    {}
    public MyTask(String ip, String code) {
        this.ip = ip;
        this.code = code;
    }



    @Override
    protected Void doInBackground(Void... voids) {
        try {
            s=new Socket(ip,80);

            printWriter=new PrintWriter(s.getOutputStream());

            printWriter.write(code);
            printWriter.flush();
            in=new BufferedReader(new InputStreamReader(s.getInputStream()));
            msg=in.readLine();

            Log.i("sundori", "doInBackground: "+msg);
            in.close();
            printWriter.close();
            s.close();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {

                }
            });
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }




        return null;
    }
}*/


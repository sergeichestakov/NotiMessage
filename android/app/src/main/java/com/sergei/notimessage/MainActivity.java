package com.sergei.notimessage;

import android.content.BroadcastReceiver;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTextViewReplyFromServer;
    private EditText mEditTextSendMessage;
    private NotificationReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditTextSendMessage = (EditText) findViewById(R.id.edt_send_message);
        mTextViewReplyFromServer = (TextView) findViewById(R.id.tv_reply_from_server);

        Button buttonSend = (Button) findViewById(R.id.btn_send);
        Button btnList = (Button) findViewById(R.id.btnListNotify);
        Button btnCreate = (Button) findViewById(R.id.btnCreateNotify);
        Button btnClear = (Button) findViewById(R.id.btnClearNotify);
        buttonSend.setOnClickListener(this);
        btnList.setOnClickListener(this);
        btnCreate.setOnClickListener(this);
        btnClear.setOnClickListener(this);

        receiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("notification");
        registerReceiver(receiver,filter);

        //To enable notification listening
        if(!NLServiceEnabled()){
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
            Toast.makeText(getApplicationContext(), "Please allow notification access", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            //Creates sample notification
            case R.id.btnCreateNotify:
                NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                NotificationCompat.Builder ncomp = new NotificationCompat.Builder(this);
                ncomp.setContentTitle("My Notification");
                ncomp.setContentText("Notification Listener Service Example");
                ncomp.setTicker("Notification Listener Service Example");
                ncomp.setSmallIcon(R.drawable.ic_launcher);
                ncomp.setAutoCancel(true);
                nManager.notify((int)System.currentTimeMillis(),ncomp.build());
                break;

            //Send a text to server
            case R.id.btn_send:
                String msg = mEditTextSendMessage.getText().toString();
                String[] sampleText = {Build.DEVICE, "NotiMessage", "Text message", msg};
                //sendMessage(sampleText, new byte[0]);
                mEditTextSendMessage.setText("");
                break;
        }
    }

    //Checks if notification listener service is enabled
    private boolean NLServiceEnabled(){
        ComponentName cn = new ComponentName(getApplicationContext(), NotificationListener.class);
        String flat = Settings.Secure.getString(getApplicationContext().getContentResolver(), "enabled_notification_listeners");
        final boolean enabled = flat != null && flat.contains(cn.flattenToString());
        return enabled;
    }

    private void sendMessage(final String[] notification, final byte[] bitmap){
        final String IP = "192.168.1.22";
        final int port = 6066;

        final Handler handler = new Handler();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket s = new Socket(IP, port);

                    OutputStream out = s.getOutputStream();

                    ObjectOutputStream output = new ObjectOutputStream(out);
                    output.writeObject(notification);
                    output.flush();

                    DataOutputStream dos = new DataOutputStream(out);
                    dos.writeInt(bitmap.length);
                    dos.write(bitmap, 0, bitmap.length);

                    dos.flush();
                    BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    final String st = input.readLine();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String s = mTextViewReplyFromServer.getText().toString();
                            if (st.trim().length() != 0)
                                mTextViewReplyFromServer.setText(s + "\nFrom Server : " + st);
                        }
                    });
                    dos.close();
                    out.close();
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public class NotificationReceiver extends BroadcastReceiver {
        String prevNotification;
        @Override
        public void onReceive(Context context, Intent intent) {
            String device = Build.DEVICE;
            String title = intent.getStringExtra("title");
            String message = intent.getStringExtra("content");
            String app = intent.getStringExtra("appName");
            byte[] bitmap = intent.getByteArrayExtra("bitmap");

            //Avoid duplicates
            if(!message.equals(prevNotification)) {
                String[] notification = {device, app, title, message};
                sendMessage(notification, bitmap);
            }
            prevNotification = message;
        }
    }
}
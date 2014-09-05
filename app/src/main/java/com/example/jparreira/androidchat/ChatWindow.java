package com.example.jparreira.androidchat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ibt.ortc.api.Ortc;
import ibt.ortc.extensibility.OnConnected;
import ibt.ortc.extensibility.OnException;
import ibt.ortc.extensibility.OnMessage;
import ibt.ortc.extensibility.OnSubscribed;
import ibt.ortc.extensibility.OrtcClient;
import ibt.ortc.extensibility.OrtcFactory;


public class ChatWindow extends Activity {

    List<Map<String, String>> messages = new ArrayList<Map<String,String>>();
    SimpleAdapter adapter;
    OrtcClient client;
    String nickname;
    int mNotificationId = 001;
    String channel;
    private boolean appIsInForeground;
    private static final int DLG_EXAMPLE1 = 0;
    private static final int TEXT_ID = 0;

    @Override
    protected void onPause() {
        super.onPause();
        appIsInForeground = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        appIsInForeground = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        nickname = ((ChatApplication) getApplication()).nickname;
        channel = ((ChatApplication) getApplication()).channel.ortcChannel;

        setTitle(((ChatApplication) getApplication()).channel.name);

        ListView listView = (ListView) findViewById(R.id.messagesListView);

        adapter = new SimpleAdapter(getApplicationContext(),
                messages,
                R.layout.chat_list_item,
                new String[] {"message"},
                new int[] {R.id.messageItem});

        listView.setAdapter(adapter);

        appIsInForeground = true;

        Ortc ortc = new Ortc();
        OrtcFactory factory = null;

        try {
            factory = ortc.loadOrtcFactory("IbtRealtimeSJ");
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        client = factory.createClient();

        client.setClusterUrl("http://ortc-developers.realtime.co/server/ssl/2.1/");
        client.setConnectionMetadata(nickname);

        client.onConnected = new OnConnected() {
            public void run(final OrtcClient sender) {
                runOnUiThread(new Runnable() {
                    public void run() {

                        MyToast.show(getApplicationContext(), "Connected");

                        sender.send(channel, nickname + " has connected");

                        sender.subscribe(channel, true,

                                new OnMessage() {
                                    public void run(final OrtcClient sender, String channel,
                                                    final String message) {

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                addMessage(message);


                                            }
                                        });
                                    }
                                }
                        );
                    }
                });
            }
        };

        client.onException = new OnException() {
            public void run(OrtcClient sender, Exception ex) {
                final Exception exception = ex;
                runOnUiThread(new Runnable() {
                    public void run() {
                        MyToast.show(getApplicationContext(), exception.getMessage());
                    }
                });
            }
        };

        client.connect("<ENTER YOUR REALTIME APP_KEY HERE>", "chatUser");
    }

    @Override
    protected void onDestroy() {
        client.send(channel, nickname + " has disconnected");
        client.disconnect();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat_window, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.change_nickname:
                changeNickname();
                return true;
            case R.id.active_users:
                openActiveUsers();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openActiveUsers() {
        Intent intent = new Intent(this, ActiveUsers.class);
        startActivity(intent);
    }

    private void changeNickname() {
        /*Intent intent = new Intent(this, NickWindow.class);
        startActivity(intent);
        finish();*/
        showDialog(DLG_EXAMPLE1);
    }

    @Override
    protected Dialog onCreateDialog(int id) {

        switch (id) {
            case DLG_EXAMPLE1:
                return createExampleDialog();
            default:
                return null;
        }
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {

        switch (id) {
            case DLG_EXAMPLE1:
                // Clear the input box.
                EditText text = (EditText) dialog.findViewById(TEXT_ID);
                text.setText("");
                break;
        }
    }

    private Dialog createExampleDialog() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.nickTextView));
        builder.setMessage(getString(R.string.choose_new_nickname));

        // Use an EditText view to get user input.
        final EditText input = new EditText(this);
        input.setId(TEXT_ID);
        builder.setView(input);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                ((ChatApplication) getApplication()).nickname = input.getText().toString();
                nickname = input.getText().toString();
                client.setConnectionMetadata(nickname);
                SharedPreferences nickname = getSharedPreferences("nickname", 0);
                SharedPreferences.Editor editor = nickname.edit();
                editor.putString("nickname", input.getText().toString());
                editor.apply();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder.create();
    }


    private HashMap<String, String> createMessage(String key, String name) {
        HashMap<String, String> message = new HashMap<String, String>();
        message.put(key, name);
        return message;
    }

    public void sendMessage (View view) {
        EditText editText = (EditText) findViewById(R.id.messageEditText);
        final String message = nickname + ": " + editText.getText().toString();
        client.send(channel, message);
        EditText clearText = (EditText)editText;
        clearText.setText("");
    }


    public void addMessage (String message) {
        messages.add(createMessage("message",message));
        adapter.notifyDataSetChanged();
        NotificationCompat.Builder newNotification =
                new NotificationCompat.Builder(ChatWindow.this)
                        .setSmallIcon(R.drawable.ic_small_notif)
                        .setContentTitle("New message")
                        .setContentText(message)
                        .setDefaults(-1)
                        .setAutoCancel(true);
        Intent notificationIntent = new Intent(this, ChatWindow.class);
        PendingIntent notificationPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        newNotification.setContentIntent(notificationPendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (!appIsInForeground) {
            notificationManager.notify(mNotificationId, newNotification.build());
            mNotificationId++;
        }
        else {
            notificationManager.cancelAll();
            mNotificationId = 001;
        }
    }

}

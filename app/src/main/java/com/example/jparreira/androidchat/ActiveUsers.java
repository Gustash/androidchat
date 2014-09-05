package com.example.jparreira.androidchat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ibt.ortc.api.OnPresence;
import ibt.ortc.api.Ortc;
import ibt.ortc.api.Presence;


public class ActiveUsers extends Activity {

    SimpleAdapter adapter;
    List<Map<String, String>> users = new ArrayList<Map<String,String>>();
    String nickname;
    ProgressDialog progress;
    private static final int DLG_EXAMPLE1 = 0;
    private static final int TEXT_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_users);
        ListView listView = (ListView) findViewById(R.id.usersListView);

        nickname = ((ChatApplication) getApplication()).nickname;
        Channel channel = ((ChatApplication) getApplication()).channel;

        addUser(nickname);

        adapter = new SimpleAdapter(getApplicationContext(),
                users,
                R.layout.chat_list_item,
                new String[] {"user"},
                new int[] {R.id.messageItem});

        listView.setAdapter(adapter);

        final ProgressDialog myProgressDialog = ProgressDialog.show(ActiveUsers.this, getString(R.string.loading), getString(R.string.loading_active_users), true);
        myProgressDialog.setCancelable(false);

        Ortc.presence(
                "http://ortc-developers.realtime.co/server/ssl/2.1/",
                true,
                "<ENTER YOUR REALTIME APP_KEY HERE>",
                "chatUser",
                channel.ortcChannel
                , new OnPresence() {
                    @Override
                    public void run(Exception error, Presence presence) {
                        final Exception exception = error;
                        final Presence presenceData = presence;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (exception != null) {
                                    MyToast.show(getApplicationContext(), String.format("Error: %s", exception.getMessage()));
                                } else {
                                    Iterator<?> metadataIterator = presenceData.getMetadata().entrySet().iterator();
                                    while (metadataIterator.hasNext()) {
                                        Map.Entry<String, Long> entry = (Map.Entry<String, Long>) metadataIterator.next();
                                        if(!entry.getKey().equals(nickname)) {
                                            addUser(entry.getKey());
                                        }
                                    }
                                    myProgressDialog.dismiss();

                                    adapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.active_users, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.change_nickname) {
            changeNickname();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                SharedPreferences nickname = getSharedPreferences("nickname", 0);
                SharedPreferences.Editor editor = nickname.edit();
                editor.putString("nickname", input.getText().toString());
                editor.apply();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        return builder.create();
    }

    private HashMap<String, String> createUser(String key, String name) {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(key, name);
        return user;
    }

    public void addUser (String user) {
        users.add(createUser("user", user));
    }


}

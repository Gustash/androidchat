package com.example.jparreira.androidchat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChannelSelectWindow extends Activity {

    List<Map<String, String>> channels = new ArrayList<Map<String,String>>();
    SimpleAdapter adapter;
    private static final int DLG_EXAMPLE1 = 0;
    private static final int TEXT_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_select_window);

        ListView listView = (ListView) findViewById(R.id.channelListView);

        adapter = new SimpleAdapter(getApplicationContext(),
                channels,
                R.layout.channel_list_item,
                new String[] {"channel"},
                new int[] {R.id.channelNameItem});

        listView.setAdapter(adapter);

        for (int i = 0;i < ((ChatApplication) getApplication()).availableChannels.length; i++) {
            addChannel(((ChatApplication) getApplication()).availableChannels [i]);
        }
        adapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Channel channel = (((ChatApplication) ChannelSelectWindow.this.getApplication()).availableChannels [position]);

                Intent intent = new Intent(ChannelSelectWindow.this, ChatWindow.class);
                ((ChatApplication) ChannelSelectWindow.this.getApplication()).channel = channel;
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.channel_select_window, menu);
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

    private HashMap<String, String> createChannel(String key, String name) {
        HashMap<String, String> channel = new HashMap<String, String>();
        channel.put(key, name);
        return channel;
    }

    public void addChannel (Channel channel) {
        channels.add(createChannel("channel", channel.name));
    }
}

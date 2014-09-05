package com.example.jparreira.androidchat;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.EditText;

import java.util.Set;

import ibt.ortc.api.OnEnablePresence;
import ibt.ortc.api.Ortc;



public class NickWindow extends Activity {

    public final static String NICKNAME = "com.example.jparreira.androidchat.NICKNAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nick_window);

        SharedPreferences nickname = getSharedPreferences("nickname", 0);

        ((ChatApplication) this.getApplication()).nickname = nickname.getString("nickname", "User");

        String VersionValue = "v.1.0";

        SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);

        if (settings.getBoolean(VersionValue, true)) {
            settings.edit().putBoolean(VersionValue, false).apply();
        }
        else {
            Intent channelSelectWindow = new Intent(this, ChannelSelectWindow.class);
            this.startActivity(channelSelectWindow);
        }

        ((ChatApplication) getApplication()).initAvailableChannels();

        Ortc.enablePresence("http://ortc-developers.realtime.co/server/2.1/", true,
                "<ENTER YOUR REALTIME APP_KEY HERE>",
                "<ENTER YOUR REALTIME PRIVATE_KEY HERE>", "chat:*", true,
                new OnEnablePresence() {
                    public void run(Exception error, String result) {
                        if (error != null) {
                            MyToast.show(getApplicationContext(), error.getMessage());
                        }
                    }
                });
    }

    public void openChat (View view){

        Intent intent = new Intent(this, ChannelSelectWindow.class);
        EditText editText = (EditText) findViewById(R.id.nickEditText);
        String nick = editText.getText().toString();
        ((ChatApplication) this.getApplication()).nickname = nick;
        SharedPreferences nickname = getSharedPreferences("nickname", 0);
        SharedPreferences.Editor editor = nickname.edit();
        editor.putString("nickname", nick);
        editor.apply();
        ((ChatApplication) this.getApplication()).nickname = nickname.getString("nickname", "User");
        startActivity(intent);

    }
}

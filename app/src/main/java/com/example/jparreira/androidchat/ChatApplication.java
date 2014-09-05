package com.example.jparreira.androidchat;

import android.app.Application;

public class ChatApplication extends Application {

    public String nickname;
    public Channel channel;
    public Channel[] availableChannels;

    public void initAvailableChannels() {

        availableChannels = new Channel[11];
        availableChannels[0] = new Channel("chat:general", "General");
        availableChannels[1] = new Channel("chat:gaming", "Gaming");
        availableChannels[2] = new Channel("chat:programming", "Programming");
        availableChannels[3] = new Channel("chat:sports", "Sports");
        availableChannels[4] = new Channel("chat:tvseries", "TV Series");
        availableChannels[5] = new Channel("chat:anime", "Anime");
        availableChannels[6] = new Channel("chat:manga", "Manga");
        availableChannels[7] = new Channel("chat:movies", "Movies");
        availableChannels[8] = new Channel("chat:drama", "Drama");
        availableChannels[9] = new Channel("chat:music", "Music");
        availableChannels[10] = new Channel("chat:technology", "Technology");
    }

}
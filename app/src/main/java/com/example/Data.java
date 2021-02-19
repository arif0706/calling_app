package com.example;


public class Data {
    public String Title,body,ChannelName,valid;
    public Data(){

    }

    public Data(String title, String body, String room,String valid) {
        Title= title;
        this.body = body;
        this.valid=valid;
        ChannelName = room;
    }
    public Data(String valid){
        this.valid=valid;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getChannelName() {
        return ChannelName;
    }

    public void setChannelName(String channelName) {
        ChannelName = channelName;
    }

    public String getValid() {
        return valid;
    }

    public void setValid(String valid) {
        this.valid = valid;
    }

}

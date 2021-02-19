package com.example;


public  class User
{
    public String Name,emailId,UID,token;

    User (){}

    public User(String Name,String emailId,String UID,String token){
        this.token=token;
        this.emailId=emailId;
        this.Name= Name;
        this.UID=UID;
    }
}
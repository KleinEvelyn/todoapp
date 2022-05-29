package com.example.todo;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;


public class LoginData {
    Context context;

    // login data constructor
    public LoginData(Context context) {
        this.context = context; // get Activity context
    }

    // set login function
    public void setLogin(String userName, String password) {

        // get SharedPreference file
        SharedPreferences sh = context.getSharedPreferences("LoginData", MODE_PRIVATE);

        // create editor
        SharedPreferences.Editor myEdit = sh.edit();

        // put password username and  in a file
        myEdit.putString("userName", userName);
        myEdit.putString("password", password);
        myEdit.apply(); // apply changes

    }

    // this function will return user name from file
    public String getUserName() {
        SharedPreferences sh = context.getSharedPreferences("LoginData", MODE_PRIVATE);
        if (!sh.contains("userName")) {
            SharedPreferences.Editor myEdit = sh.edit();
            myEdit.putString("userName", "");
            myEdit.apply();
        }
        return sh.getString("userName", "");
    }

}
package com.shubo7868.shubham.facedetectioapp;

import android.app.Application;

import com.firebase.client.Firebase;

public class app  extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Firebase.setAndroidContext(this);
    }
}

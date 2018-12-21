package com.example.dineshkumar.diary;

import com.google.firebase.database.FirebaseDatabase;

public class FirebaseApplication extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        /* Enable disk persistence  */
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}

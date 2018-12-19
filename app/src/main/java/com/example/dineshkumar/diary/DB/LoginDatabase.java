package com.example.dineshkumar.diary.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.dineshkumar.diary.Model.Diary;
import com.example.dineshkumar.diary.Model.User;

/**
 * Created by Dinesh Kumar on 2/26/2018.
 */

public class LoginDatabase extends SQLiteOpenHelper{

    String tabName = "loginTable";
    static final String DB_NAME = "DiaryDBs";
    static final int DB_VER = 3;

    public LoginDatabase(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "create table " + tabName + " (id integer primary key autoincrement,uname text unique,upassword text);";
        sqLiteDatabase.execSQL(query);
        Log.i("tabTag","Table login created !");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
       /* String query = "drop table if exists " + tabName;
        sqLiteDatabase.execSQL(query);
        onCreate(sqLiteDatabase);*/
    }

    public long addUser(String uname,String pswd) throws SQLException
    {
       SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("uname",uname);
        values.put("upassword",pswd);
        long status = db.insert(tabName,null,values);
        db.close();
        return status;

    }

    public User getUser()
    {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "+tabName,null);
        String user="";
        String password="";
        while(cursor.moveToNext())
        {
            user = cursor.getString(1);
            password = cursor.getString(2);
        }

        cursor.close();
        db.close();

        return new User(user,password);
    }
}

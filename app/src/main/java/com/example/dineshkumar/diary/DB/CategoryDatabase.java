package com.example.dineshkumar.diary.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.dineshkumar.diary.Model.Category;

import java.util.ArrayList;

/**
 * Created by Dinesh Kumar on 2/26/2018.
 */

public class CategoryDatabase extends SQLiteOpenHelper {

    private final static String DB_NAME="DiaryDBsss";
    private final static int DB_VER = 6;
    private String tabName = "categoryTable";
    String catNameCol = "cat_name";
    public CategoryDatabase(Context context) {
        super(context, DB_NAME, null,DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "create table "+tabName+" (id integer primary key autoincrement,"+catNameCol+" text unique);";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists "+tabName);
        onCreate(sqLiteDatabase);
    }

    public long addCategory(String cat_name)
    {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues value = new ContentValues();
        value.put("cat_name",cat_name);
        long status = db.insert(tabName,null,value);
        return status;
    }

    public ArrayList<String> getCategories() {
        ArrayList list = new ArrayList<String>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from " + tabName, null);

        while (cursor.moveToNext())
        {
            String cat_name = cursor.getString(1);
            list.add(cat_name);
        }

        cursor.close();
        db.close();

        return list;
    }

    public boolean deleteCategory(String catgName)
    {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(tabName, catNameCol + "='" + catgName+"'", null) > 0;
    }
}

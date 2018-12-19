package com.example.dineshkumar.diary.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.dineshkumar.diary.Model.Diary;

import java.util.ArrayList;

/**
 * Created by Dinesh Kumar on 2/21/2018.
 */

public class DiaryDatabase extends SQLiteOpenHelper {

    static final String DB_NAME = "DiaryDB";
    static final int DB_VER = 5;
    String tabName = "Diary";

    String titleCol = "title";
    String createDateCol = "created_date";
    String modifyDateCol="modified_date";
    String descCol="desc";
    String categoryCol="category";


    Context context;

    public DiaryDatabase(Context context)
    {
        super(context, DB_NAME, null, DB_VER);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {

        String query = "Create table " + tabName + " (id integer primary key autoincrement, "+titleCol+"  text unique,"+descCol+" text,"+createDateCol+" text,"+modifyDateCol+" text,"+categoryCol+" text);";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String query = "drop table if exists " + tabName;
        sqLiteDatabase.execSQL(query);
        onCreate(sqLiteDatabase);
    }

    public long insertData(Diary diary)
    {

        ContentValues values = new ContentValues();

        values.put(titleCol, diary.getTitle());
        values.put(descCol, diary.getDesc());
        values.put(createDateCol, diary.getCreatedDate());
        values.put(modifyDateCol, diary.getModifiedDate());
        values.put(categoryCol,diary.getCategory());
        SQLiteDatabase db = getWritableDatabase();

        long status = db.insert(tabName, null, values);
        db.close();

        return status;
    }

    public long updateData(String whereClause, String whereVal,Diary diary)
    {
        SQLiteDatabase db = getWritableDatabase();

      //  String strSQL = "UPDATE "+tabName+" SET "+colName+" = '"+val+"' WHERE "+whereClause+ "= '"+ whereVal+"'";

        whereClause=whereClause+"=?";
        String[] whereVals = new String[] {whereVal};
        ContentValues values = new ContentValues();

        values.put(descCol,diary.getDesc());
        values.put(createDateCol,diary.getCreatedDate());
        values.put(modifyDateCol,diary.getModifiedDate());
        values.put(categoryCol,diary.getCategory());

        db.update(tabName, values, whereClause, whereVals);
        return 0l;
    }

    public ArrayList<Diary> getData(String orderBy)
    {
     ArrayList<Diary> diaryArrayList = new ArrayList<Diary>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "+tabName+" order by "+orderBy,null);

        while(cursor.moveToNext())
        {
            String title = cursor.getString(1);
            String desc = cursor.getString(2);
            String createdDate = cursor.getString(3);
            String modifiedDate = cursor.getString(4);
            String category = cursor.getString(5);
            diaryArrayList.add(new Diary(title,desc,createdDate,modifiedDate,category));
        }

        cursor.close();
        db.close();
     return diaryArrayList;
    }

    public ArrayList<Diary> getData(String colName,String val)
    {
        ArrayList<Diary> diaryArrayList = new ArrayList<Diary>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "+tabName+" where "+colName+"='"+val+"'",null);

        while(cursor.moveToNext())
        {
            String title = cursor.getString(1);
            String desc = cursor.getString(2);
            String createdDate = cursor.getString(3);
            String modifiedDate = cursor.getString(4);
            String category = cursor.getString(5);
            diaryArrayList.add(new Diary(title,desc,createdDate,modifiedDate,category));
        }

        cursor.close();
        db.close();
        return diaryArrayList;
    }

    public ArrayList<Diary> getDataByWordMatch(String likeWildcard)
    {
        if(likeWildcard.contains("'")){return null;}

        ArrayList<Diary> diaryArrayList = new ArrayList<Diary>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "+tabName+" where "+titleCol+" LIKE '%"+likeWildcard+"%'"  ,null);

        while(cursor.moveToNext())
        {
            String title = cursor.getString(1);
            String desc = cursor.getString(2);
            String createdDate = cursor.getString(3);
            String modifiedDate = cursor.getString(4);
            String category = cursor.getString(5);
            diaryArrayList.add(new Diary(title,desc,createdDate,modifiedDate,category));
        }

        cursor.close();
        db.close();
        return diaryArrayList;
    }

    public boolean delete(String colName, String val)
    {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(tabName, colName + "='" + val+"'", null) > 0;
    }


}

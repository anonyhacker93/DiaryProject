package com.example.dineshkumar.diary;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dineshkumar.diary.CustomUtils.DateFormatter;
import com.example.dineshkumar.diary.DB.CategoryDatabase;
import com.example.dineshkumar.diary.DB.DiaryDatabase;
import com.example.dineshkumar.diary.Model.Diary;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WriteDiary extends AppCompatActivity {

    @BindView(R.id.edTitle)
    EditText edTitle;
    @BindView(R.id.edDesc)
    EditText edDesc;
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @BindView(R.id.categorySpinner)
    AppCompatSpinner categorySpinner;

    DiaryDatabase database;
    Diary diary;
    CategoryDatabase categoryDatabase;

    String modifiedDate;

    boolean editableMode;

    ArrayAdapter<String> dataAdapter;
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_write_diary);

        ButterKnife.bind(this);
        fetch_setCategory();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

         database = new DiaryDatabase(this);

          if (getIntent().getExtras() != null) {  // Editing existing Diary note
          diary = getIntent().getExtras().getParcelable("diaryObj");
          Date curDate = new Date();

          modifiedDate = DateFormatter.setDateFormat(curDate);

          diary.setModifiedDate(modifiedDate);
          edTitle.setText(diary.getTitle());
          edDesc.setText(diary.getDesc());
          categorySpinner.setSelection(dataAdapter.getPosition(diary.getCategory()));
          editableMode = true;
          edTitle.setEnabled(false);
        }
        else
        {
            edTitle.setEnabled(true);
            editableMode = false;
        }
    }

    @OnClick(R.id.btnSave)
   void writeIntoDB()
    {
      String title = edTitle.getText().toString();
      String desc = edDesc.getText().toString();
      String catgName = (String) categorySpinner.getSelectedItem();

      if(title.length() >0)
      {
          if(editableMode == false)
          {
              Date curDate = new Date();
              String createdDate = DateFormatter.setDateFormat(curDate);

              if (database.insertData(new Diary(title, desc, createdDate, modifiedDate,catgName)) > 0) {
                  finish();
              } else {
                  Toast.makeText(this, "This name title already exists !", Toast.LENGTH_SHORT).show();
              }
          }
          else
          {
              if(diary !=null) {
                  String modifiedDate = DateFormatter.setDateFormat(new Date());
                   diary = new Diary(title,desc,diary.getCreatedDate(), modifiedDate, catgName);
                   database.updateData("title", title, diary);
                   Toast.makeText(this, "Updated !", Toast.LENGTH_SHORT).show();
                   finish();
              }
          }
      }
      else
      {
          ShowAlertDialog("Set Title","Please set a title first",false);
      }
    }

    void ShowAlertDialog(String title,String msg,boolean buttonEnable)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(title)
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    void fetch_setCategory()
    {
        categoryDatabase = new CategoryDatabase(this);
        ArrayList <String>cat_list = categoryDatabase.getCategories();

        dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, cat_list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(dataAdapter);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}

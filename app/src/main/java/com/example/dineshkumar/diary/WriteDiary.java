package com.example.dineshkumar.diary;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dineshkumar.diary.CustomUtils.DateFormatter;
import com.example.dineshkumar.diary.DB.CategoryDatabase;
import com.example.dineshkumar.diary.DB.DiaryDatabase;
import com.example.dineshkumar.diary.Model.Diary;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.dineshkumar.diary.MainActivity.INTENT_DIARY_KEY;

public class WriteDiary extends AppCompatActivity {
    final static String CONCAT_CHAR = "_dup";

    @BindView(R.id.edTitle)
    EditText edTitle;
    @BindView(R.id.edDesc)
    EditText edDesc;
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @BindView(R.id.categorySpinner)
    AppCompatSpinner categorySpinner;

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

        if (getIntent().getExtras() != null) {
            diary = getIntent().getExtras().getParcelable(INTENT_DIARY_KEY);
            editDiary(diary);
        } else {
            edTitle.setEnabled(true);
            editableMode = false;
        }
    }

    private void editDiary(Diary diary) {
        modifiedDate = DateFormatter.setDateFormat(new Date());

        diary.setModifiedDate(modifiedDate);
        edTitle.setText(diary.getTitle());
        edDesc.setText(diary.getDesc());
        categorySpinner.setSelection(dataAdapter.getPosition(diary.getCategory()));
        editableMode = true;
        edTitle.setEnabled(false);
    }

    @OnClick(R.id.btnSave)
    void writeIntoDB() {
        String title = edTitle.getText().toString();
        String desc = edDesc.getText().toString();
        String catgName = (String) categorySpinner.getSelectedItem();

        if (title.length() > 0) {
            if (editableMode == false) {
                String createdDate = DateFormatter.setDateFormat(new Date());
                Diary diary = new Diary(title, desc, createdDate, "", catgName);
                uploadOnFirebase(diary,editableMode);
            } else {
                if (diary != null) {
                    String modifiedDate = DateFormatter.setDateFormat(new Date());
                    diary = new Diary(title, desc, diary.getCreatedDate(), modifiedDate, catgName);
                    uploadOnFirebase(diary,editableMode);
                }
            }

        } else {
            ShowAlertDialog("Set Title", "Please set a title first", false);
        }
    }

    private void uploadOnFirebase(final Diary diary, final boolean editableMode) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        final DatabaseReference myRef = database.getReference(Constants.FIREBASE_ROOT_REFERENCE);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userName = getUserNameFromSharedPreference();// Retrieved from SharedPreference
                DatabaseReference childRef = myRef.child(userName).child(Constants.FIREBASE_DIARY_REFERENCE).child(diary.getCategory()).child(diary.getTitle());
                if (dataSnapshot.child(userName).child(Constants.FIREBASE_DIARY_REFERENCE).child(diary.getCategory()).child(diary.getTitle()).exists()) {
                    if (editableMode) {
                        childRef.setValue(diary);
                        Log.i("myTag", "Data Updated");
                        finish();
                    } else {
                        Toast.makeText(WriteDiary.this, "This title already exist.", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    childRef.setValue(diary);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("myTag", "Problem while adding ");
                Toast.makeText(getBaseContext(), "ErrorTag :" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    void ShowAlertDialog(String title, String msg, boolean buttonEnable) {
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

    void fetch_setCategory() {
        categoryDatabase = new CategoryDatabase(this);
        ArrayList<String> cat_list = categoryDatabase.getCategories();

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

    private String getUserNameFromSharedPreference() {
        SharedPreferences preferences = getSharedPreferences(Constants.SHARED_PREF_USERNAME, MODE_PRIVATE);
        String userName = preferences.getString(Constants.SHARED_PREF_USERNAME, null);
        return userName;
    }

}

package com.example.dineshkumar.diary;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dineshkumar.diary.CustomUtils.DateFormatter;
import com.example.dineshkumar.diary.DB.DiaryDatabase;
import com.example.dineshkumar.diary.Model.Diary;

import java.io.Serializable;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.dineshkumar.diary.MainActivity.INTENT_DIARY_KEY;

public class ViewDiary extends AppCompatActivity {


    @BindView(R.id.viewTitle)
    TextView title;

    @BindView(R.id.viewDesc)
    TextView desc;

    @BindView(R.id.viewCreatedDate)
    TextView createDate;

    @BindView(R.id.viewCategory)
    TextView category;

    DiaryDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_diary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ButterKnife.bind(this);
        setDiaryViewContent();
        setFAB();
    }

    protected void onResume() {
        super.onResume();
        setDiaryViewContent();
    }

    private void setDiaryViewContent() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Diary diary = (Diary) bundle.getParcelable(INTENT_DIARY_KEY);
                if (diary == null) {
                    Log.d("DiaryTag", "ViewDiary :setDiaryViewContent :: diary is null");
                    return;
                }
                if (diary.getTitle() != null) { // Fresh Request
                    title.setText(diary.getTitle());
                    desc.setText(diary.getDesc());
                    createDate.setText(diary.getCreatedDate());
                    category.setText(diary.getCategory());
                } else {
                   /* ArrayList<Diary> arrayList = database.getData("title", title.getText().toString());
                    Diary diary = arrayList.get(0);
                    desc.setText(diary.getDesc());
                    createDate.setText(diary.getCreatedDate());
                    category.setText(diary.getCategory());*/
                    Toast.makeText(this, "ViewDiary :setDiaryViewContent in else", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    void deleteDiaryView() {
        database.delete("title", title.getText().toString());
        Toast.makeText(this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    void EditDiaryContent() {
        String titleStr = title.getText().toString();
        String descStr = desc.getText().toString();
        String catgStr = category.getText().toString();
        String createDateStr = createDate.getText().toString();
        String modifiedDate = DateFormatter.setDateFormat(new Date());
        Diary diary = new Diary(titleStr, descStr, createDateStr, modifiedDate, catgStr);
        Log.i("catTags", catgStr);
        Intent intent = new Intent(ViewDiary.this, WriteDiary.class);
        intent.putExtra(INTENT_DIARY_KEY, (Serializable) diary);
        startActivity(intent);
    }

    void setFAB() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.writeDiaryFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditDiaryContent();
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete_menu, menu);
        MenuItem item = menu.findItem(R.id.delete_menu_btn);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.delete_menu_btn:
                deleteDiaryView();
                finish();
            default:
                onBackPressed();

        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        Log.i("backTg", "Back pressed");
        return true;
    }
}

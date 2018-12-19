package com.example.dineshkumar.diary;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dineshkumar.diary.DB.CategoryDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddCategory extends AppCompatActivity {

    @BindView(R.id.catgName)
    EditText edCatgName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.addCatgBtn)
    void addCategory()
    {
        CategoryDatabase database = new CategoryDatabase(this);
        database.addCategory(edCatgName.getText().toString());
        showCloseConfirmDialog();
        Toast.makeText(this, "Category Added !", Toast.LENGTH_SHORT).show();
    }

    void showCloseConfirmDialog()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
// Add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(AddCategory.this, "Category added successfully !", Toast.LENGTH_SHORT).show();
           finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setTitle("Want to add more category ?");
        dialog.show();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        Log.i("backTg","Back pressed");
        return true;
    }
}

package com.example.dineshkumar.diary;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dineshkumar.diary.DB.CategoryDatabase;
import com.example.dineshkumar.diary.Model.Category;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddCategory extends AppCompatActivity {
    public static final String TAG = "com.example.dineshkumar.diary.AddCategory";
    public static final String FIREBASE_CATEG_REFERENCE = "Category";
    public static final String FIREBASE_ROOT_REFERENCE = "Root";
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
    void addCategory() {
        Category category = new Category(edCatgName.getText().toString());
        CategoryDatabase database = new CategoryDatabase(this);
        database.addCategory(category.getName());
        addToFirebase(category);
        showCloseConfirmDialog();
        Toast.makeText(this, "Category Added !", Toast.LENGTH_SHORT).show();
    }

    private void addToFirebase(final Category category) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference(FIREBASE_ROOT_REFERENCE);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child(category.getName()).exists()) {
                    String email = "blahblah";
                    myRef.child(email).child(FIREBASE_CATEG_REFERENCE).setValue(category);
                    Log.i("myTag", "Category added");
                } else {
                    Toast.makeText(getBaseContext(), "Oh ! unable to Update !!", Toast.LENGTH_SHORT).show();
                    Log.i("myTag", "Problem while adding ");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("myTag", "Problem while adding ");
                Toast.makeText(getBaseContext(), "ErrorTag :" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    void showCloseConfirmDialog() {
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
        Log.i("backTg", "Back pressed");
        return true;
    }
}

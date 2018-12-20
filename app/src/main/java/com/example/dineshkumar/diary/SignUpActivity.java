package com.example.dineshkumar.diary;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dineshkumar.diary.Model.User;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends BaseActivity {
    private DatabaseReference _usersReference;
    @BindView(R.id.uname)
    EditText uname;

    @BindView(R.id.upassword)
    EditText upswd;

    @BindView(R.id.confirm_upassword)
    EditText confirm_upsswd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        initFirebase();
    }

    @OnClick(R.id.btnSignup)
    void createAccount() {
        String userName = uname.getText().toString();
        String upswrd = upswd.getText().toString();
        String confirm_upswrd = confirm_upsswd.getText().toString();

        if (upswrd.equals(confirm_upswrd)) {
            validateUser(userName,upswrd);
        }else {
            Toast.makeText(this, "Password and confirm password not matched", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void signInFirebase(final String userName, final String pswd) {
        final ProgressDialog progressDialog = getProgressDialog(SignUpActivity.this,"Please wait", "Connecting with Server");
        progressDialog.setIndeterminate(false);
        progressDialog.show();
        _usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User userInfo = new User(userName, pswd);
                if (dataSnapshot.child(userName).exists()) {
                    Toast.makeText(SignUpActivity.this, "User already exist !", Toast.LENGTH_SHORT).show();
                } else {
                    _usersReference.child(userName).setValue(userInfo);
                    Toast.makeText(SignUpActivity.this, "Account created successfully !", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void initFirebase() {
        FirebaseApp.initializeApp(this);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        _usersReference = firebaseDatabase.getReference(Constants.FIREBASE_USER_REFERENCE);
    }

    private void validateUser(final String userName,final String password){
        _usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(userName).exists()){
                    Toast.makeText(SignUpActivity.this, "User name already existed ! Try Another", Toast.LENGTH_SHORT).show();
                }else {
                    signInFirebase(userName,password);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SignUpActivity.this, "Problem occurred "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

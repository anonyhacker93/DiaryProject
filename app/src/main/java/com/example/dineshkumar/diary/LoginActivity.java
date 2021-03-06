package com.example.dineshkumar.diary;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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

import static com.example.dineshkumar.diary.Constants.SHARED_PREF_USERNAME;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.uname)
    EditText uname;
    @BindView(R.id.upassword)
    EditText upassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            boolean isLogout = bundle.getBoolean(MainActivity.INTENT_LOGOUT_KEY);
            if (isLogout) {
                upassword.setText("");
                saveToSharedPreference(null);
            }
        } else {
            String userName = getUserNameFromSharedPreference();
            if (userName != null) { //Already logged-in
                gotoMainActivity();
            }
        }
        initFirebase();
    }

    @OnClick(R.id.btnLogin)
    void login() {
        final String uName = uname.getText().toString();
        final String uPwd = upassword.getText().toString();
        User user = new User(uName, uPwd);
        loginFirebase(user);

    }

    @OnClick(R.id.btnSignup)
    void signUp() {
        Intent signUp = new Intent(this, SignUpActivity.class);
        startActivity(signUp);
    }

    DatabaseReference usersReference;

    private void initFirebase() {
        FirebaseApp.initializeApp(this);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        usersReference = firebaseDatabase.getReference(Constants.FIREBASE_USER_REFERENCE);
    }


    private void loginFirebase(User user) {
        if (user == null) return;
        final String uName = user.getUname();
        final String uPwd = user.getPassword();
        if (!user.getUname().isEmpty() && !user.getPassword().isEmpty()) {
            final ProgressDialog progressDialog = getProgressDialog(LoginActivity.this, "Please wait", "Authenticating...");
            progressDialog.show();
            usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(uName).exists()) {

                        if (!uPwd.isEmpty() && !uName.isEmpty()) {
                            User userInfo = dataSnapshot.child(uName).getValue(User.class);
                            if (uPwd.equals(userInfo.getPassword())) {
                                gotoMainActivity();
                                Toast.makeText(LoginActivity.this, "Login Successfully !", Toast.LENGTH_SHORT).show();
                                saveToSharedPreference(uName);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Wrong Password !", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if (uName.isEmpty()) {
                                Toast.makeText(LoginActivity.this, "Please enter user name ", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Please enter password ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "User not existed !", Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progressDialog.dismiss();
                }
            });
        } else {
            if (uName.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter user name ", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoginActivity.this, "Please enter password ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void gotoMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void saveToSharedPreference(String userName) {
        SharedPreferences preferences = getSharedPreferences(SHARED_PREF_USERNAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SHARED_PREF_USERNAME, userName);
        editor.commit();
        editor.apply();
    }

    private String getUserNameFromSharedPreference() {
        SharedPreferences preferences = getSharedPreferences(Constants.SHARED_PREF_USERNAME, MODE_PRIVATE);
        String userName = preferences.getString(Constants.SHARED_PREF_USERNAME, null);
        return userName;
    }

}

package com.example.dineshkumar.diary;

import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dineshkumar.diary.DB.LoginDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUp extends AppCompatActivity {

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
    }

    @OnClick(R.id.btnSignup)
    void createAccount()
    {
        String userName = uname.getText().toString();
        String upswrd = upswd.getText().toString();
        String confirm_upswrd = confirm_upsswd.getText().toString();

        if(upswrd.equals(confirm_upswrd)) {

            LoginDatabase db = new LoginDatabase(this);
           try {
               long status =db.addUser(userName, upswrd);
              if( status > 0) {
                  Toast.makeText(this, "Account created successfully !", Toast.LENGTH_SHORT).show();
                    finish();
              }
              else
              {
                  Toast.makeText(this, "Unable to addUser !"+status, Toast.LENGTH_SHORT).show();
              }
           }
           catch (Exception ex)
           {
               Toast.makeText(this, "Unable to add user !"+ex, Toast.LENGTH_SHORT).show();
           }
            db.close();

        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}

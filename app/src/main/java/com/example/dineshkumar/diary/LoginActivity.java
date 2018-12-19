package com.example.dineshkumar.diary;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dineshkumar.diary.DB.LoginDatabase;
import com.example.dineshkumar.diary.Model.User;

import java.sql.SQLException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.uname)
    EditText uname;
    @BindView(R.id.upassword)
    EditText upassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.btnLogin)
    void login()
    {

            LoginDatabase db = new LoginDatabase(this);

            User user = db.getUser();
            if(uname.getText().toString().equals(user.getUname()) && upassword.getText().toString().equals(user.getPassword()))
            {
                Intent home = new Intent(this,MainActivity.class);
                home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(home);
            }
            else
            {
                Toast.makeText(this, "Username/Password Incorrect !", Toast.LENGTH_SHORT).show();
            }

    }

    @OnClick(R.id.btnSignup)
    void signUp()
    {
        Intent signUp = new Intent(this,SignUp.class);
        startActivity(signUp);
    }
}

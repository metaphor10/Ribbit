package com.software.arielb.ribbit;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class LoginActivity extends ActionBarActivity {

    protected TextView mSignUpTextView;
    protected EditText mUserName;
    protected EditText mPassword;

    protected Button mLoginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_login);
        mSignUpTextView=(TextView) findViewById(R.id.signUpText);
        mSignUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });

        mUserName=(EditText)findViewById(R.id.usernameField);
        mPassword=(EditText)findViewById(R.id.passwordField);

        mLoginButton=(Button) findViewById(R.id.loginButton);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName=mUserName.getText().toString();
                String password=mPassword.getText().toString();


                userName=userName.trim();
                password=password.trim();


                if(userName.isEmpty() || password.isEmpty()){
                    AlertDialog.Builder builder= new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage(R.string.login_error_message)
                            .setTitle(R.string.login_error_title)
                            .setPositiveButton(android.R.string.ok,null);
                    AlertDialog dialog=builder.create();
                    dialog.show();
                }else {
                    // TODO: create a login
                    setSupportProgressBarIndeterminateVisibility(true);
                    ParseUser.logInInBackground(userName,password,new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            setSupportProgressBarIndeterminateVisibility(false);
                            if (e==null){
                                //success
                                Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }else {
                                AlertDialog.Builder builder= new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage(e.getMessage())
                                        .setTitle(R.string.login_error_title)
                                        .setPositiveButton(android.R.string.ok,null);
                                AlertDialog dialog=builder.create();
                                dialog.show();
                            }
                        }
                    });

                }
            }
        });
    }
}

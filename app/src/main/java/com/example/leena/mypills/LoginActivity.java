package com.example.leena.mypills;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private EditText userEmail,userPassword;
    private TextView needNewAccount;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setContentView(R.layout.splash);
//        //display the logo during 5 seconds,
//        new CountDownTimer(5000,1000){
//            @Override
//            public void onTick(long millisUntilFinished){}
//
//            @Override
//            public void onFinish(){
//                //set the new Content of your activity
//                LoginActivity.this.setContentView(R.layout.activity_login);
//            }
//        }.start();





        setContentView(R.layout.activity_login);

        needNewAccount=(TextView)findViewById(R.id.need_to_register);
        userEmail=(EditText)findViewById(R.id.login_email);
        userPassword=(EditText)findViewById(R.id.login_password);
        loginButton=(Button)findViewById(R.id.login_btn);

        loadingBar=new ProgressDialog(this);

        mAuth=FirebaseAuth.getInstance();

        needNewAccount.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                sendUserToRegisterActivity();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allowingUserToLogin();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser!=null)
        {
            sendUserToMainActivity();
        }
    }

    private void allowingUserToLogin() {
        String email=userEmail.getText().toString();
        String password=userPassword.getText().toString();

        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this,"Please specify email.",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"Please specify password.",Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.setTitle("Signing in...");
            loadingBar.setMessage("This will take some time.");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                sendUserToMainActivity();

                                Toast.makeText(LoginActivity.this, "Authentication successful.",
                                        Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            } else {
                              String msg=task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, "Authentication failed. Error:"+msg,
                                        Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                            }

                        }
                    });
        }
    }

    private void sendUserToMainActivity() {
        Intent mainIntent=new Intent(LoginActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void sendUserToRegisterActivity() {

        Intent registerIntent=new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(registerIntent);
       // finish();
    }
}

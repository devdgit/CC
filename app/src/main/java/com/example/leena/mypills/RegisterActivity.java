package com.example.leena.mypills;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class RegisterActivity extends AppCompatActivity {

    private EditText userEmail,userPassword,userConfirm;
    private Button registerBtn;
    private ProgressDialog loadingBar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth=FirebaseAuth.getInstance();

        userEmail=(EditText)findViewById(R.id.register_email);
        userPassword=(EditText)findViewById(R.id.register_password);
        userConfirm=(EditText)findViewById(R.id.register_confirm);
        registerBtn=(Button)findViewById(R.id.register_create_account);

        loadingBar=new ProgressDialog(this);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewAccount();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser=firebaseAuth.getCurrentUser();
        if(currentUser!=null)
        {
            sendUserToMainActivity();
        }
    }

    private void createNewAccount() {
        String email=userEmail.getText().toString();
        String pass=userPassword.getText().toString();
        String confirm=userConfirm.getText().toString();

        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this,"Please enter email",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(pass))
        {
            Toast.makeText(this,"Please enter password",Toast.LENGTH_SHORT).show();
        }

        else if(!TextUtils.equals(pass,confirm))
        {
            Toast.makeText(this,"Please confirm password",Toast.LENGTH_SHORT).show();
        }
        else{

            loadingBar.setTitle("Creating new account...");
            loadingBar.setMessage("This will take some time.");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            firebaseAuth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        sendUserToSetupActivity();
                        loadingBar.dismiss();
                        Toast.makeText(RegisterActivity.this,"you are registered",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        loadingBar.dismiss();
                        String message=task.getException().getMessage();
                        Toast.makeText(RegisterActivity.this,"Error"+message,Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendUserToSetupActivity() {
        Intent setupIntent=new Intent(RegisterActivity.this,SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }

    private void sendUserToMainActivity() {
        Intent mainIntent=new Intent(RegisterActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}

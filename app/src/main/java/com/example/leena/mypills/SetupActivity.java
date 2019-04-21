package com.example.leena.mypills;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private EditText user;
    private  EditText fulln,agee;
    private CircleImageView profileImage;
    private Button saveBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private StorageReference profileImageRef;

    final static int galleryPic=1;
    private ProgressDialog loadingBar;

    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        user=(EditText)findViewById(R.id.setup_username);
        fulln=(EditText)findViewById(R.id.setup_fullname);
        agee=(EditText)findViewById(R.id.setup_age);
        saveBtn=(Button)findViewById(R.id.save_btn);
        profileImage=(CircleImageView)findViewById(R.id.setup_profile_img);

//        user.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (keyCode == KeyEvent.KEYCODE_ENTER) {
//                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(user.getWindowToken(), 0);
//                }
//                return false;
//            }
//        });


        loadingBar=new ProgressDialog(this);

        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();
        userRef= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        //findRef=FirebaseDatabase.getInstance().getReference().child("Finds");

        profileImageRef= FirebaseStorage.getInstance().getReference().child("profile images");



        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAccountInfo();
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery=new Intent();
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery,galleryPic);
            }
        });

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.hasChild("profile_img"))
                    {
                        String image=dataSnapshot.child("profile_img").getValue().toString();
                        Picasso.get().load(image).placeholder(R.drawable.profile).into(profileImage);
                    }
                    else{
                        Toast.makeText(SetupActivity.this,"Please select profile image.",Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==galleryPic && resultCode==RESULT_OK && data!=null)
        {
            Uri imageUri=data.getData();

            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1).start(this);

        }

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK) {
                loadingBar.setTitle("Adding Profile Image..");
                loadingBar.setMessage("This will take some time.");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);

                Uri resultUri = result.getUri();
                final StorageReference filepath = profileImageRef.child(currentUserId + ".jpg");
//                filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
//                        // ...
//                        Toast.makeText(SetupActivity.this,"Picture added successfully",Toast.LENGTH_SHORT).show();
//
//                        final String downloadURL=filepath.getDownloadUrl().toString();
//                        userRef.child("profile_img").setValue(downloadURL).addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                // Write was successful!
//                                // ...
//                                loadingBar.dismiss();
//                                Toast.makeText(SetupActivity.this,"Link created",Toast.LENGTH_SHORT).show();
//
//                                Intent setupIntent=new Intent(SetupActivity.this,SetupActivity.class);
//                                startActivity(setupIntent);
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                // Write failed
//                                // ...
//                                loadingBar.dismiss();
//                                String msg=e.getMessage();
//                                Toast.makeText(SetupActivity.this,"Error:"+msg,Toast.LENGTH_SHORT).show();
//
//                            }
//                        });
//
//                    }
//                });
//            }

                UploadTask uploadTask = filepath.putFile(resultUri);

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            String downloadUri = task.getResult().toString();
                            userRef.child("profile_img").setValue(downloadUri).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Write was successful!
                                    // ...
                                    loadingBar.dismiss();
                                   // Toast.makeText(SetupActivity.this, "Link created", Toast.LENGTH_SHORT).show();

                                    Intent setupIntent = new Intent(SetupActivity.this, SetupActivity.class);
                                    startActivity(setupIntent);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Write failed
                                    // ...
                                    loadingBar.dismiss();
                                    String msg = e.getMessage();
                                    Toast.makeText(SetupActivity.this, "Error:" + msg, Toast.LENGTH_SHORT).show();

                                }
                            });

                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });
            }
            else{
                loadingBar.dismiss();
                Toast.makeText(SetupActivity.this,"Error in cropping image",Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void saveAccountInfo() {

        String username=user.getText().toString();
        String fullname=fulln.getText().toString();
        String age=agee.getText().toString();

        if(TextUtils.isEmpty(username))
        {
            Toast.makeText(this,"Please write your username",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(fullname))
        {
            Toast.makeText(this,"Please write your full name",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(age))
        {
            Toast.makeText(this,"Please write your age",Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.setTitle("Processing..");
            loadingBar.setMessage("This will take some time.");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            HashMap userMap=new HashMap();
            userMap.put("username",username);
            userMap.put("fullname",fullname);
            userMap.put("age",age);
            userMap.put("points","50");
            userMap.put("nametolower",fullname.toLowerCase());

            userRef.updateChildren(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // Write was successful!
                    // ...
                    loadingBar.dismiss();
                    Toast.makeText(SetupActivity.this,"Your account is created successfully",Toast.LENGTH_SHORT).show();
                    sendUserToMainActivity();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Write failed
                            // ...
                            loadingBar.dismiss();
                            Toast.makeText(SetupActivity.this,"Error:"+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void sendUserToMainActivity() {
        Intent mainIntent=new Intent(SetupActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}

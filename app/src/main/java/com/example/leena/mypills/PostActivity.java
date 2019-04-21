package com.example.leena.mypills;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class PostActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageButton postImage;
    private Button addPostBtn;
    private EditText postDescription;
    private static final int galleryPic=1;
    private Uri imageUri;
    private String description;

    private StorageReference postImgRef;
    private ProgressDialog loadingBar;

    private String saveCurrentDate,saveCurrentTime, postRandomName,downloadURL,currentUserId;
    private DatabaseReference userRef,postRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postImage=(ImageButton)findViewById(R.id.post_img);
        postDescription=(EditText)findViewById(R.id.post_description);
        addPostBtn=(Button)findViewById(R.id.post_add_btn);

        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        postRef= FirebaseDatabase.getInstance().getReference().child("Posts");
        mAuth= FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();

        postImgRef= FirebaseStorage.getInstance().getReference();
        loadingBar=new ProgressDialog(this);

        mToolbar=(Toolbar)findViewById(R.id.find_friends_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Upload Image");

        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validatePostInfo();
            }
        });


    }

    private void validatePostInfo() {
        description=postDescription.getText().toString();
        if(imageUri==null)
        {
            Toast.makeText(this,"Please add image",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(description))
        {
            Toast.makeText(this,"Please add description",Toast.LENGTH_SHORT).show();
        }
        else{
            storingImageToFirebase();
        }

    }

    private void storingImageToFirebase() {
        loadingBar.setTitle("Uploading Image");
        loadingBar.setMessage("Task in process...");
        loadingBar.show();
        loadingBar.setCanceledOnTouchOutside(true);

        Calendar callForDate=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("dd-mm-yyyy");
        saveCurrentDate=currentDate.format(callForDate.getTime());

        Calendar callForTime=Calendar.getInstance();
        SimpleDateFormat currentTime=new SimpleDateFormat("hh:mm:ss");
        saveCurrentTime=currentTime.format(callForTime.getTime());

        postRandomName=saveCurrentDate+saveCurrentTime;

        final StorageReference filepath=postImgRef.child("post images").child(imageUri.getLastPathSegment()+postRandomName+".jpg");
//        filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                if(task.isSuccessful())
//                {
//                    downloadURL = task.getResult().getMetadata().getReference().getDownloadUrl().toString();
//                    loadingBar.dismiss();
//                    Toast.makeText(PostActivity.this,"Image uploaded successfully",Toast.LENGTH_SHORT).show();
//
//                    savingPostInfoToDatabase();
//
//
//                }
//                else {
//                    loadingBar.dismiss();
//                    String msg=task.getException().getMessage();
//                    Toast.makeText(PostActivity.this,"Error: "+msg,Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        UploadTask uploadTask = filepath.putFile(imageUri);

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
                    downloadURL = task.getResult().toString();
                    loadingBar.dismiss();
                    Toast.makeText(PostActivity.this,"Image uploaded successfully",Toast.LENGTH_SHORT).show();
                    savingPostInfoToDatabase();


                } else {
                    // Handle failures
                    // ...
                    loadingBar.dismiss();
                    String msg=task.getException().getMessage();
                    Toast.makeText(PostActivity.this,"Error: "+msg,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void savingPostInfoToDatabase() {
        userRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    String fullname=dataSnapshot.child("fullname").getValue().toString();
                    HashMap postsMap=new HashMap();
                    postsMap.put("description",description);
                    postsMap.put("imageURL",downloadURL);
                    postsMap.put("userid",currentUserId);
                    postsMap.put("fullname",fullname);
                    postsMap.put("time",saveCurrentTime);
                    postsMap.put("date",saveCurrentDate);

                    postRef.child(currentUserId+postRandomName).updateChildren(postsMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if(task.isSuccessful())
                                    {
                                      //  Toast.makeText(PostActivity.this,"Image added",Toast.LENGTH_SHORT).show();
                                        sendUserToMainActivity();
                                    }
                                    else {
                                        Toast.makeText(PostActivity.this,"error",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void openGallery() {
        Intent gallery=new Intent();
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        startActivityForResult(gallery,galleryPic);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==galleryPic && resultCode==RESULT_OK && data!=null )
        {
            imageUri=data.getData();
            postImage.setImageURI(imageUri);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==android.R.id.home)
        {
            sendUserToMainActivity();

        }
        return super.onOptionsItemSelected(item);

    }

    private void sendUserToMainActivity() {
        Intent mainIntent=new Intent(PostActivity.this,MainActivity.class);
       // mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
